import os
import subprocess
import argparse

def extract_audio(input_file, output_dir):
    """
    Extracts audio using an external script.

    Parameters:
    - input_file (str): Path to the input audio file.
    - output_dir (str): Directory to save the extracted audio.

    Note:
    The function calls an external script named 'extract_f0.py'.
    """
    # Build the command for separating audio
    command = [
        'python',
        'extract_f0.py',
        '--input', input_file,
        '--output_dir', output_dir
    ]

    # Run the command
    subprocess.run(command)

def extract_audio_in_folder(input_folder, output_dir):
    """
    Extracts audio for all files in a given folder.

    Parameters:
    - input_folder (str): Path to the input folder containing audio files.
    - output_dir (str): Directory to save the extracted audio.

    Note:
    This function processes all audio files in the input folder.
    """
    # Create the output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)

    # List all audio files in the input folder
    audio_files = [f for f in os.listdir(input_folder) if f.endswith('.wav')]  # Replace with the actual audio file extension

    for audio_file in audio_files:
        input_path = os.path.join(input_folder, audio_file)
        output_path = os.path.join(output_dir, audio_file)

        # Call the separate_audio function for each file in the folder
        extract_audio(input_path, output_path)

if __name__ == '__main__':
    # Set up argparse to handle command line arguments
    parser = argparse.ArgumentParser(description='Extract audio using an external script.')
    parser.add_argument('--input_folder', type=str, help='Path to the input folder containing audio files.')
    parser.add_argument('--output_dir', type=str, help='Directory to save the extracted audio.')
    args = parser.parse_args()

    # Process audio files in the input folder and save the extracted audio to the output folder
    extract_audio_in_folder(args.input_folder, args.output_dir)


# python extracter.py --input_folder /path/to/input_folder --output_dir /path/to/output_dir
