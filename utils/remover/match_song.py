import os
import subprocess
import pandas as pd
import argparse

def separate_audio(input_file, output_dir, gpu_id=1):
    """
    Separate vocals from the given audio file.

    Parameters:
    - input_file (str): Path to the input audio file
    - output_dir (str): Path to the output directory for separated vocals
    - gpu_id (int): GPU ID to use for processing (default is 1)
    """
    # Build the command for separating audio
    command = [
        'python',
        'inference.py',
        '--input', input_file,
        '--tta',
        '--gpu', str(gpu_id),
        '--output_dir', output_dir
    ]

    # Run the command
    subprocess.run(command)

def parsing_songid(directory_path):
    """
    Parse the song IDs from the given directory.

    Parameters:
    - directory_path (str): Path to the directory containing song files

    Returns:
    - dict: Dictionary with singers as keys and their song titles as values
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

                # If the singer name is already in the dictionary, add the song title; otherwise, create a new entry
                if singer in song_dict:
                    song_dict[singer].append(song_title)
                else:
                    song_dict[singer] = [song_title]

            except IndexError as e:
                # Exception handling: If a list index error occurs, print the file information and continue
                print(f"Error processing file {file}: {e}. Skipping this file.")

    return song_dict
    

def load_data(filepath):
    """
    Load data from the given CSV file into a DataFrame.

    Parameters:
    - filepath (str): Path to the CSV file

    Returns:
    - pd.DataFrame: Loaded data as a DataFrame
    """
    cols = ['Title', 'Artist', 'Rating', 'Genre', 'Genre_ID', 'f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']

    df_song = pd.read_csv(filepath, names=cols)
    df_song['Index'] = df_song.index
    
    return df_song
    
    
def extract_duplicates(df, dictionary):
    """
    Extract duplicate song information from the DataFrame.

    Parameters:
    - df (pd.DataFrame): DataFrame to check for duplicates
    - dictionary (dict): Dictionary with singers as keys and their song titles as values

    Returns:
    - list: List of duplicate song information
    """
    duplicates = []

    for index, row in df.iterrows():
        title = row['Title']
        artist = row['Artist']

        if artist in dictionary and title in dictionary[artist]:
            duplicates.append((title, artist))

    return duplicates


def find_matching_files(folder_path, duplicates_list):
    """
    Find matching mp3 files in the given folder for the duplicate song information.

    Parameters:
    - folder_path (str): Path to the folder containing mp3 files
    - duplicates_list (list): List of duplicate song information

    Returns:
    - list: List of matching mp3 file paths
    """
    matching_files = []

    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_name, file_extension = os.path.splitext(file)
            if file_extension == '.mp3':
                for title, artist in duplicates_list:
                    if title in file_name and artist in file_name:
                        file_path = os.path.join(root, file)
                        matching_files.append(file_path)

    return matching_files


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Separate vocals from audio files using a deep learning model.")
    parser.add_argument('-i', '--input_folder', type=str, help='Path to the folder containing mp3 files')
    parser.add_argument('-d', '--song_path', type=str, help='Path to the directory containing song files')
    parser.add_argument('-c', '--song_info_path', type=str, help='Path to the CSV file with song information')
    parser.add_argument('-o', '--output_folder', type=str, help='Path to the output folder for separated vocals')
    parser.add_argument('-g', '--gpu_id', type=int, default=0, help='GPU ID to use for processing (default is 0)')

    args = parser.parse_args()

    current_directory = os.getcwd()

    input_folder = args.input_folder
    song_path = args.song_path
    song_info_path = args.song_info_path
    output_folder = args.output_folder or current_directory + '/vocal_remove_song'
    gpu_id = args.gpu_id

    if not (input_folder and song_path and song_info_path):
        parser.error("Please provide valid paths for input_folder, song_path, and song_info_path.")

    df_song = load_data(song_info_path)
    song_dict = parsing_songid(song_path)   
    duplicates_list = extract_duplicates(df_song, song_dict)
    matching_files_list = find_matching_files(song_path, duplicates_list)

    for file_path in matching_files_list:
        separate_audio(file_path, output_folder, gpu_id)


# python match_song.py -i /path/to/input_folder -d /path/to/song_directory -c /path/to/song_info_csv -o /path/to/output_folder -g gpu_id
