import os
import re
import subprocess
import numpy as np
import pandas as pd
import math
import argparse

def run_inference_for_folder(args):
    # Get a list of all WAV files in the input folder.
    wav_files = [f for f in os.listdir(args.input_folder) if f.endswith('.wav')]

    # Run inference for each WAV file.
    for wav_file in wav_files:
        # Create the full path for the input file.
        input_file = os.path.join(args.input_folder, wav_file)

        # Extract the file name and extension.
        file_name, file_extension = os.path.splitext(wav_file)

        # Generate the output file name.
        output_file = f"inference_{file_name}{file_extension}"

        # Compose the command to run the inference.
        command = [
            'python',
            'main.py',
            '-i', input_file,
            '-m', args.model_checkpoint,
            '-o', os.path.join(args.output_folder, output_file),
            '-k', str(args.key_shift),
            '-eak', str(args.pitch_range)
        ]

        # Execute the command.
        subprocess.run(command)

if __name__ == "__main__":
    # Create an ArgumentParser object to parse command-line arguments.
    parser = argparse.ArgumentParser(description="Run inference for a folder containing WAV files.")
    # Define the required command-line arguments.
    parser.add_argument('-i', '--input_folder', type=str, help='Path to the input folder')
    parser.add_argument('-m', '--model_checkpoint', type=str, help='Path to the model checkpoint')
    parser.add_argument('-o', '--output_folder', type=str, help='Path to the output folder')
    parser.add_argument('-k', '--key_shift', type=int, default=0, help='Key shift value')
    parser.add_argument('-eak', '--pitch_range', type=int, default=0, help='Pitch range value')

    # Parse the command-line arguments.
    args = parser.parse_args()

    # Call the inference function for the folder.
    run_inference_for_folder(args)


# python dvoice_process.py -i /path/to/input_folder -m /path/to/model_checkpoint -o /path/to/output_folder
