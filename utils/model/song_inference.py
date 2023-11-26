import os
import re
import subprocess
import numpy as np
import pandas as pd
import math
import argparse

def parse_song_id(directory_path):
    """
    Extracts song information from the given directory and returns it as a dictionary.

    Parameters:
    - directory_path (str): Path to the directory from which to extract song information

    Returns:
    - dict: A dictionary with singers as keys and a list of their song titles as values
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

                # If the singer's name is already in the dictionary, add the song title; otherwise, create a new entry
                if singer in song_dict:
                    song_dict[singer].append(song_title)
                else:
                    song_dict[singer] = [song_title]

            except IndexError as e:
                # Exception handling: If a list index error occurs, print information about the file and continue
                print(f"Error processing file {file}: {e}. Skipping this file.")

    return song_dict


def load_data(filepath):
    """
    Loads data from the given file path and returns it as a DataFrame.

    Parameters:
    - filepath (str): Path to the data file to load

    Returns:
    - pd.DataFrame: DataFrame of the loaded data
    """
    cols = ['Title', 'Artist', 'Rating', 'Genre', 'Genre_ID',
            'f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']

    df_song = pd.read_csv(filepath, names=cols)
    df_song['Index'] = df_song.index

    return df_song


def run_inference(input_file, model_checkpoint, output_folder, key_shift, pitch_range):
    """
    Executes model inference for the given input file.

    Parameters:
    - input_file (str): Path to the input file for inference
    - model_checkpoint (str): Path to the model checkpoint
    - output_folder (str): Path to the folder to save the inference results
    - key_shift (float): Key shift value
    - pitch_range (float): Pitch range value
    """
    # Extracting file name and extension
    file_name, file_extension = os.path.splitext(input_file)

    # Creating output file name
    output_file = f"inference_{os.path.basename(file_name)}{file_extension}"

    # Build the command for running inference
    command = [
        'python',
        'main.py',
        '-i', input_file,
        '-m', model_checkpoint,
        '-o', os.path.join(output_folder, output_file),
        '-k', str(key_shift),
        '-eak', str(pitch_range)
    ]

    # Run the command
    subprocess.run(command)


def calculate_pitch_shift(f0, f1):
    """
    Calculates the pitch shift value between two frequencies.

    Parameters:
    - f0 (float): First frequency
    - f1 (float): Second frequency

    Returns:
    - float: Calculated pitch shift value
    """
    k = 12 * math.log2(f1 / f0)
    return k


def read_text_file_to_list(file_path):
    """
    Reads the given text file and returns each line's value as a list.

    Parameters:
    - file_path (str): Path to the text file to read

    Returns:
    - list: List containing values from each line of the text file
    """
    try:
        with open(file_path, 'r') as file:
            # Read lines from the text file and remove leading/trailing whitespaces
            content = [line.strip() for line in file.readlines()]
        return content
    except FileNotFoundError:
        print(f"Error: File not found at {file_path}")
        return []


def extract_id_from_filepath(filepath):
    """
    Extracts the ID value from the file path.

    Parameters:
    - filepath (str): File path from which to extract the ID value

    Returns:
    - int: Extracted ID value
    """
    # Extracting only the file name from the file path
    file_name = os.path.basename(filepath)

    # Extracting the part of the file name that starts with a number
    id_value = int(file_name.split('_')[0])

    return id_value


def extract_duplicates(df, dictionary):
    """
    Extracts duplicated song information from the DataFrame.

    Parameters:
    - df (pd.DataFrame): DataFrame to check for duplicates
    - dictionary (dict): Dictionary with singers as keys and a list of their song titles as values

    Returns:
    - tuple: Tuple containing information about duplicated songs and the updated DataFrame
    """
    duplicates = []

    # Adding a new column 'id'
    df['id'] = None

    for index, row in df.iterrows():
        title = row['Title']
        artist = row['Artist']

        if artist in dictionary and title in dictionary[artist]:
            # If the row is a duplicate, extract and save the id value from the file path
            duplicates.append((title, artist))

    return duplicates, df


def apply_duplicates(df, dictionary):
    """
    Applies ID values for duplicated song information to the DataFrame.

    Parameters:
    - df (pd.DataFrame): DataFrame to apply ID values
    - dictionary (dict): Dictionary with singers as keys and a list of their song titles as values

    Returns:
    - pd.DataFrame: DataFrame with applied ID values
    """
    for index, row in df.iterrows():
        title = row['Title']
        artist = row['Artist']
        if artist in dictionary and title in dictionary[artist]:
            # If the row is a duplicate, extract and save the id value from the file path
            file_path = row['File']
            id_value = extract_id_from_filepath(file_path)
            df.at[index, 'id'] = id_value

    return df


# Extracts mp3 file paths within a specific folder
def find_matching_files(folder_path, duplicates_list, df):
    """
    Finds mp3 files that match duplicated songs in the given folder and saves the paths in the DataFrame.

    Parameters:
    - folder_path (str): Path to the folder containing mp3 files
    - duplicates_list (list): List containing information about duplicated songs
    - df (pd.DataFrame): DataFrame to store the results

    Returns:
    - pd.DataFrame: DataFrame with added mp3 file paths
    """
    df['File'] = None
    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_name, file_extension = os.path.splitext(file)
            if file_extension == '.mp3':
                for title, artist in duplicates_list:
                    if title in file_name and artist in file_name:
                        file_path = os.path.join(root, file)
                        df.loc[df['Title'] == title, 'File'] = file_path

    return df


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Run inference for vocal removal on a folder of mp3 files.")
    parser.add_argument('-i', '--input_folder', type=str, help='Path to the folder containing mp3 files')
    parser.add_argument('-o', '--output_folder', type=str, help='Path to the output folder for inference results')
    parser.add_argument('-m', '--model_checkpoint', type=str, help='Path to the model checkpoint')
    parser.add_argument('-k', '--key_shift', type=float, default=0.0, help='Key shift value')
    parser.add_argument('-eak', '--pitch_range', type=float, default=0.0, help='Pitch range value')
    parser.add_argument('--auto_key', action='store_true', help='Use automatic key calculation')

    args = parser.parse_args()

    if args.auto_key:
        df_song = load_data(args.song_info_path)
        song_dict = parse_song_id(args.song_path)

        # Call the function to extract duplicated Title and Artist
        duplicates_list, df_song = extract_duplicates(df_song, song_dict)
        df_song = find_matching_files(args.song_path, duplicates_list, df_song)
        df_song = apply_duplicates(df_song, song_dict)

        # Print rows in df_song that match the values in duplicates_list
        filtered_df = df_song[df_song.apply(lambda row: (row['Title'], row['Artist']) in duplicates_list, axis=1)]

        # Read the text file to a list
        text_list = read_text_file_to_list(args.text_file_path)

        user_list = [float(value) for value in text_list]
        user_f0 = np.mean(user_list)

        # Sort the values in each row from 'f0_1' to 'f0_8'
        df_song_sorted = filtered_df[['f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']].apply(sorted,
                                                                                                             axis=1,
                                                                                                             result_type='expand')
        # Add the sorted values to the existing DataFrame
        filtered_df[
            ['f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']] = df_song_sorted

        filtered_df['f0_avg'] = filtered_df[
            ['f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']].mean(axis=1)

        f0_info = filtered_df[['id', 'f0_avg']]

        # Initialize the dictionary
        k_values_dict = {}

        # Save the k value for each element in the dictionary
        for index, row in f0_info.iterrows():
            f0_avg_value = row['f0_avg']
            id_value = row['id']

            # Calculate the k value using the calculate_pitch_shift function
            k_value = calculate_pitch_shift(user_f0, f0_avg_value)

            # Save the id as the key and the k value as the value in the dictionary
            k_values_dict[id_value] = k_value

        for root, dirs, files in os.walk(args.input_folder):
            for file in files:
                file_path = os.path.join(root, file)

                if 'Vocals' in file and file.endswith('.wav'):
                    # Extract the numerical part from the file name
                    match = re.search(r'(\d+)_Vocals\.wav', file)
                    if match:
                        number_part = match.group(1)

                        if args.auto_key:
                            # Get the key_shift value for the corresponding number from k_values_dict
                            key_shift = k_values_dict.get(int(number_part), 0.0)
                        # Call the run_inference function
                        run_inference(file_path, args.model_checkpoint, args.output_folder, args.key_shift, args.pitch_range)


# python song_inference.py -i /your/input/folder -o /your/output/folder -m /your/model_checkpoint/path -k 0.0 -eak 0.0 --auto_key
