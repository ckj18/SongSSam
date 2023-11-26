import pandas as pd
import numpy as np
import io
import logging
import torch
import os
import boto3
import json

from sklearn.metrics.pairwise import cosine_similarity
from sklearn.preprocessing import MinMaxScaler
from flask import Flask, request, Response
from flask_cors import CORS

class RS_system:
    def __init__(self, filepath, user_f0, user_genre):
        """
        Initialize the recommendation system class.

        Parameters:
        - filepath (str): Path to the song data file.
        - user_f0 (float): User's voice feature.
        - user_genre (dict): Dictionary representing user's genre preferences.
        """
        self.filepath = filepath
        self.user_f0 = user_f0
        self.user_genre = user_genre
    
    def mapping_genre(self, data):
        """
        Map genres to IDs and create a new column.

        Parameters:
        - data (pd.DataFrame): DataFrame containing song data.

        Returns:
        - pd.DataFrame: DataFrame with Genre_ID column.
        """
        genre_to_id = self.extract_genre_id(data)
        data['Genre_ID'] = data['Genre'].str.split().apply(lambda x: ' '.join(str(genre_to_id[genre]) for genre in x))
        
        return data

    def extract_genre_id(self, data):
        """
        Extract unique genres from song data and map them to unique IDs.

        Parameters:
        - data (pd.DataFrame): DataFrame containing song data.

        Returns:
        - dict: Dictionary mapping genres to IDs.
        """
        unique_genres = sorted(data['Genre'].str.split().explode().unique())
        genre_to_id = {genre: idx for idx, genre in enumerate(unique_genres)}
        
        return genre_to_id

    def encode_genre(self, df_song):
        """
        Encode genres using one-hot encoding and add a new column.

        Parameters:
        - df_song (pd.DataFrame): DataFrame containing song data.

        Returns:
        - pd.DataFrame: DataFrame with one-hot encoded Genre column.
        """
        df_onehot = df_song['Genre'].str.get_dummies(sep=' ')
        return df_onehot

    def rating_probability(self, data):
        """
        Add Rating_Distribution column, converting ratings to a probability distribution.

        Parameters:
        - data (pd.DataFrame): DataFrame containing song data.

        Returns:
        - pd.DataFrame: DataFrame with Rating_Distribution column.
        """
        data['Log_Rating'] = np.log1p(data['Rating'])
        max_rating = np.max(data['Log_Rating'])
        min_rating = np.min(data['Log_Rating'])
        data['Rating_Distribution'] = (data['Log_Rating'] - min_rating) / (max_rating - min_rating)
        
        return data

    def parse_content(self, df_song, selected_genres):
        """
        Extract feature data for the recommendation system.

        Parameters:
        - df_song (pd.DataFrame): DataFrame containing song data.
        - selected_genres (list): List of user-preferred genres.

        Returns:
        - pd.DataFrame: DataFrame containing feature data for the recommendation system.
        """
        df_song_preference = self.rating_probability(df_song)
        df_onehot = self.encode_genre(df_song_preference)
        df_content = pd.concat([df_song[['Rating_Distribution', 'f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']], df_onehot[selected_genres]], axis=1)
        
        return df_content

    def f0_scale(self, df_song):
        """
        Normalize f0 values using Min-Max Scaling.

        Parameters:
        - df_song (pd.DataFrame): DataFrame containing song data.

        Returns:
        - pd.DataFrame: DataFrame with normalized f0 values.
        """
        selected_column = ['f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']
        df_song[selected_column] = df_song[selected_column].apply(lambda row: sorted(row), axis=1, result_type='expand')
        scaler = MinMaxScaler()
        column_values = df_song[selected_column].values.reshape(-1, len(selected_column))
        normalized_values = scaler.fit_transform(column_values)
        df_song[selected_column] = normalized_values

        return df_song

    def user_data(self, user_f0, user_genre):
        """
        Create a DataFrame with user's voice features and preferred genres.

        Parameters:
        - user_f0 (float): User's voice feature.
        - user_genre (dict): Dictionary representing user's genre preferences.

        Returns:
        - pd.DataFrame: DataFrame with user's voice features and preferred genres.
        - list: List of user-preferred genres.
        """
        user_f0_df = pd.DataFrame([user_f0])
        selected_genres = [genre for genre, weight in user_genre.items() if weight >= 1]
        user_genre_df = pd.DataFrame([user_genre])
        user_df = pd.concat([user_f0_df, user_genre_df[selected_genres]], axis=1)
        
        return user_df, selected_genres
    
    def filter_rows_with_nonzero_values(self, df_recommend, selected_columns):
        """
        Select rows where all values in selected columns are nonzero.

        Parameters:
        - df_recommend (pd.DataFrame): DataFrame used for recommendations.
        - selected_columns (list): List of selected columns.

        Returns:
        - pd.DataFrame: DataFrame with rows where all values in selected columns are nonzero.
        """
        condition = (df_recommend[selected_columns] != 0).any(axis=1)
        return df_recommend[condition]

    def recommend_songid(self, recommend_df, top_k=20, sample=16):
        """
        Recommend songs.

        Parameters:
        - recommend_df (pd.DataFrame): DataFrame used for recommendations.
        - top_k (int): Number of top songs to recommend.
        - sample (int): Number of songs to sample for the final recommendation list.

        Returns:
        - list: List of recommended song indices.
        """
        except_columns = ["Index", "Rating_Distribution"]
        weighted_similarity_matrix = cosine_similarity(recommend_df.iloc[:-1].drop(columns=except_columns), recommend_df.iloc[-1:].drop(columns=except_columns))
        similar_items = list(enumerate(weighted_similarity_matrix.flatten(), start=1))
        similar_items.sort(key=lambda x: x[1], reverse=True)
        top_recommendations = [index for index, _ in similar_items[:top_k]]
        probabilities = recommend_df['Rating_Distribution'].iloc[top_recommendations]
        selected_song_index = np.random.choice(top_recommendations, size=sample, p=probabilities/probabilities.sum())
        result = recommend_df['Index'].iloc[selected_song_index]
        result = recommend_df['Index'].iloc[top_recommendations].values
        df_song_mapped = recommend_df.loc[result]
        recommend_df.dropna(inplace=True)
        return list(df_song_mapped.index)
    
    def load_data(self, filepath):
        """
        Load song data.

        Parameters:
        - filepath (str): Path to the song data file.

        Returns:
        - pd.DataFrame: DataFrame containing song data.
        """
        cols = ['Title', 'Artist', 'Rating', 'Genre', 'Genre_ID', 
           'f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']

        df_song = pd.read_csv(filepath, names=cols)
        df_song['Index'] = df_song.index
        
        return df_song
    
    def recommend(self):
        """
        Perform recommendations and return the result in JSON format.

        Returns:
        - str: JSON string containing the recommendation result.
        """
        df_song = self.load_data(self.filepath)
        df_user, selected_genres = self.user_data(self.user_f0, self.user_genre)

        df_content = self.parse_content(df_song, selected_genres)

        df_content['Index'] = df_content.index 
        df_recommend = self.f0_scale(pd.concat([df_content, df_user]))
        selected_recommend = self.filter_rows_with_nonzero_values(df_recommend, selected_genres)
        
        song_id = self.recommend_songid(selected_recommend)
        
        # Convert song_id list to JSON format
        song_id_json = json.dumps({"song_id": song_id})
        
        return song_id_json



app = Flask(__name__)

logging.getLogger("numba").setLevel(logging.WARNING)
logger = logging.getLogger(__name__)
s3 = boto3.client('s3', aws_access_key_id='AKIATIVNZLQ23AQR4MPK', aws_secret_access_key='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')
bucket = "songssam.site"

CORS(app)

@app.route("/SongRecommend", methods=["POST"])
def song_recommend():
    request_form = request.form
    f_db_path = './db_filepath'
    f_user_info_json = request_form.get("user_info_json", None)
    uuid = request_form.get("uuid", "")

    if not os.path.exists("exp/" + str(uuid)):
        os.makedirs("exp/" + str(uuid))
    else:
        logger.info("folder already exists")
        
    user_f0 = f_user_info_json['user_f0']    
    user_genre =  f_user_info_json['user_genre']
        
    rs = RS_system(f_db_path, user_f0, user_genre)    
    song_json = rs.recommend()
    
    return Response(song_json)



if __name__ == "__main__":
    app.run(port=6844, host="0.0.0.0", debug=False, threaded=False)
