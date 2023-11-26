import os
import pandas as pd
from read_files import ReadDB
import argparse

class ParsingDB:
    def __init__(self, directory_path, split_path, data_path):
        self.directory_path = directory_path
        self.split_path = split_path
        self.data_path = data_path

    def parsing_songid(self, directory_path):
        """
        Parse song IDs from the given directory.

        Parameters:
        - directory_path (str): Path to the directory containing song files.

        Returns:
        - song_dict (dict): Dictionary mapping artists to their respective song titles.
        """
        song_dict = {}
        subfolders = [f.path for f in os.scandir(directory_path) if f.is_dir()]

        for subfolder in subfolders:
            files = os.listdir(subfolder)
            files.sort(key=lambda x: int(x.split('_')[0]))

            for file in files:
                filename = os.path.splitext(file)[0]
                parts = filename.split('_')

                try:
                    song_title = parts[1]
                    singer = parts[2]

                    if singer in song_dict:
                        song_dict[singer].append(song_title)
                    else:
                        song_dict[singer] = [song_title]

                except IndexError as e:
                    print(f"Error processing file {file}: {e}. Skipping this file.")

        return song_dict

    def find_duplicate_songs(self, result, dataframe):
        """
        Find duplicate songs between the parsed song IDs and the DataFrame.

        Parameters:
        - result (dict): Parsed song IDs.
        - dataframe (pd.DataFrame): DataFrame containing song data.

        Returns:
        - duplicates (set): Set of duplicate song titles.
        """
        all_songs = set()
        for songs_list in result.values():
            all_songs.update(songs_list)

        dataframe_songs = set(dataframe['Title'])
        duplicates = all_songs.intersection(dataframe_songs)

        return duplicates

    def remove_duplicates_from_column(self, dataframe, column_name, duplicates):
        """
        Remove rows from the DataFrame where the specified column has duplicate values.

        Parameters:
        - dataframe (pd.DataFrame): Input DataFrame.
        - column_name (str): Name of the column to check for duplicates.
        - duplicates (set): Set of duplicate values in the column.

        Returns:
        - dataframe (pd.DataFrame): DataFrame with duplicate rows removed.
        """
        dataframe = dataframe[~dataframe[column_name].isin(duplicates)]
        return dataframe

    def make_duplicate_df(self, df_duplicate, df_singer_remove_duplicate):
        """
        Create a DataFrame of duplicate songs, sampling additional songs for artists with fewer than 5 songs.

        Parameters:
        - df_duplicate (pd.DataFrame): DataFrame containing duplicate songs.
        - df_singer_remove_duplicate (pd.DataFrame): DataFrame without duplicate songs.

        Returns:
        - df_duplicate_songs (pd.DataFrame): DataFrame of duplicate songs.
        """
        artist_counts = df_duplicate['Artist'].value_counts()
        artists_below_5 = artist_counts[artist_counts < 5].index.tolist()

        df_duplicate_songs = pd.DataFrame()
        df_duplicate_songs = df_duplicate_songs.append(df_duplicate)

        for artist in artists_below_5:
            artist_songs = df_singer_remove_duplicate[df_singer_remove_duplicate['Artist'] == artist]
            probabilities = artist_songs['Rating'] / artist_songs['Rating'].sum()
            num_songs_to_add = min(5 - artist_counts[artist], len(artist_songs))

            if num_songs_to_add > 0:
                selected_songs = artist_songs.sample(num_songs_to_add, replace=False, weights=probabilities, random_state=42)
                df_duplicate_songs = df_duplicate_songs.append(selected_songs, ignore_index=True)

        return df_duplicate_songs

    def select_songs(self, df_singer_remove_duplicate, df_duplicate_songs):
        """
        Select songs for artists not included in the duplicate songs DataFrame.

        Parameters:
        - df_singer_remove_duplicate (pd.DataFrame): DataFrame without duplicate songs.
        - df_duplicate_songs (pd.DataFrame): DataFrame of duplicate songs.

        Returns:
        - df_singer_selected (pd.DataFrame): DataFrame of selected songs.
        """
        new_singer = list(set(df_duplicate_songs['Artist'].values))
        df_singer_selected = pd.DataFrame(columns=df_singer_remove_duplicate.columns)

        remaining_artists = set(df_singer_remove_duplicate['Artist'].unique()) - set(new_singer)

        total_rows = 0

        for artist in remaining_artists:
            artist_songs = df_singer_remove_duplicate[df_singer_remove_duplicate['Artist'] == artist]
            probabilities = artist_songs['Rating'] / artist_songs['Rating'].sum()
            num_songs_to_add = min(5, len(artist_songs), 1000 - total_rows)

            if num_songs_to_add > 0:
                selected_songs = artist_songs.sample(num_songs_to_add, replace=False, weights=probabilities, random_state=42)
                df_singer_selected = df_singer_selected.append(selected_songs, ignore_index=True)

                total_rows += num_songs_to_add

                if total_rows >= 1000:
                    break

        return df_singer_selected

    def split_dataframe(self, dataframe, folder_name, N=4):
        """
        Split the DataFrame into N groups based on the number of songs per artist.

        Parameters:
        - dataframe (pd.DataFrame): Input DataFrame.
        - folder_name (str): Name of the folder to save split DataFrames.
        - N (int): Number of groups.

        Returns:
        - None
        """
        artist_counts = dataframe['Artist'].value_counts()
        sorted_artists = artist_counts.index
        groups = [[] for _ in range(N)]

        for artist in sorted_artists:
            min_group_idx = min(range(N), key=lambda i: len(groups[i]))
            groups[min_group_idx].append(artist)

        for i, artists in enumerate(groups):
            group_df = dataframe[dataframe['Artist'].isin(artists)]
            save_path = os.path.join(folder_name, f"group_{i + 1}.txt")
            group_df.to_csv(save_path, columns=['Artist', 'Title', 'Id'], sep=',', index=False)

    def concat_dataframe(self, data1, data2):
        """
        Concatenate two DataFrames.

        Parameters:
        - data1 (pd.DataFrame): First DataFrame.
        - data2 (pd.DataFrame): Second DataFrame.

        Returns:
        - pd.DataFrame: Concatenated DataFrame.
        """
        return pd.concat([data1, data2])

def main():
    parser = argparse.ArgumentParser(description="Parse and process song data.")
    parser.add_argument("--dir_path", type=str, help="Path to the directory containing song files.")
    parser.add_argument("--split_path", type=str, help="Path to save split DataFrames.")
    parser.add_argument("--data_path", type=str, help="Path to the CSV file containing song data.")
    parser.add_argument("--split_num", type=int, default=4, help="Number of splits for the DataFrame.")

    args = parser.parse_args()

    # Load the DataFrame
    read_db = ReadDB(dataset_path=args.data_path)
    df_singer = read_db.read_singer(args.data_path)

    # Load information from the database
    parsing = ParsingDB(args.dir_path, args.split_path, args.data_path)
    song_dict = parsing.parsing_songid(args.dir_path)

    # Save duplicated song titles between the database information and the DataFrame
    duplicates = parsing.find_duplicate_songs(song_dict, df_singer)
    df_duplicate = df_singer[df_singer['Title'].isin(duplicates)]

    # Save the DataFrame without duplicate rows based on unique ID values
    df_singer_remove_duplicate = parsing.remove_duplicates_from_column(df_singer, 'Id', df_duplicate['Id'])

    # Sample songs from df_singer_remove_duplicate for songs with fewer than 5 songs per artist relative to duplicates
    df_duplicate_songs = parsing.make_duplicate_df(df_duplicate, df_singer_remove_duplicate)

    # Sample songs from df_singer_remove_duplicate not included in df_duplicate_songs
    df_singer_selected = parsing.select_songs(df_singer_remove_duplicate, df_duplicate_songs)

    # Merge the two sampled DataFrames and extract data not present in the database, storing it in df_singer_finder
    df_refinement = parsing.concat_dataframe(df_singer_selected, df_duplicate_songs)
    df_singer_finder = parsing.remove_duplicates_from_column(df_refinement, 'Id', df_duplicate['Id'])

    # Split the extracted DataFrame into split_num parts and save in split_path
    parsing.split_dataframe(df_singer_finder, args.split_path, args.split_num)


if __name__ == '__main__':
    main()


# python parsing_database.py --dir_path <dir_path_value> --split_path <split_path_value> --data_path <data_path_value> --split_num <split_num_value>
