import os
import argparse
from pydub import AudioSegment

def extract_wav_files_from_folder(folder_path):
    """
    Extracts the full paths of files with the ".wav" extension in the given folder.

    Parameters:
    - folder_path (str): Folder path

    Returns:
    - List[str]: List of full paths of files with the ".wav" extension
    """
    return [os.path.join(root, file) for root, dirs, files in os.walk(folder_path) for file in files if file.endswith(".wav")]

def split_audio(input_file, output_folder, split_interval=10 * 1000, min_interval=8 * 1000):
    """
    Splits the given audio file at the specified time intervals and saves the results in another folder.

    Parameters:
    - input_file (str): Path to the input audio file
    - output_folder (str): Path to the folder to store the split audio files
    - split_interval (int): Split interval (default: 10 seconds)
    - min_interval (int): Minimum interval for a valid segment (default: 8 seconds)
    """
    audio = AudioSegment.from_wav(input_file)

    for i in range(0, len(audio), split_interval):
        segment = audio[i:i + split_interval]

        if len(segment) >= min_interval:
            # Generate the path for the output file
            output_file = f"{output_folder}/segment_{os.path.splitext(os.path.basename(input_file))[0]}_{i // split_interval + 1}.wav"
            # Save the split audio
            segment.export(output_file, format="wav")

def process_folder(args):
    """
    Processes all audio files in the given folder and saves the split audio.

    Parameters:
    - args (argparse.Namespace): argparse namespace object containing the command-line arguments
    """
    # Create the folder to store the results
    os.makedirs(args.output_folder, exist_ok=True)
    # Extract ".wav" files from the input folder
    wav_files = extract_wav_files_from_folder(args.input_folder)

    for wav_file in wav_files:
        print(f"Processing: {wav_file}")
        # Perform audio splitting for each file
        split_audio(wav_file, args.output_folder, args.split_interval, args.min_interval)

if __name__ == "__main__":
    # Create an ArgumentParser object for parsing command-line arguments
    parser = argparse.ArgumentParser(description="Split WAV files in a folder.")
    # Define the required command-line arguments
    parser.add_argument('-i', '--input_folder', type=str, help='Path to the input folder')
    parser.add_argument('-o', '--output_folder', type=str, help='Path to the output folder')
    parser.add_argument('--split_interval', type=int, default=10 * 1000, help='Split interval in milliseconds')
    parser.add_argument('--min_interval', type=int, default=8 * 1000, help='Minimum interval for a valid segment in milliseconds')
    
    # Save the command-line arguments in the args object
    args = parser.parse_args()

    # Call the folder processing function
    process_folder(args)


# python segment.py -i /path/to/input_folder -o /path/to/output_folder
