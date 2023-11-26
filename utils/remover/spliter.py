import os
import subprocess
import argparse

def separate_audio(input_file, output_dir, gpu_id=0):
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

def separate_audio_in_folder(input_folder, output_dir, gpu_id=0):
    # Create the output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)
    
    separate_audio(input_folder, output_dir, gpu_id)

    # List all audio files in the input folder
    audio_files = [f for f in os.listdir(input_folder) if f.endswith('.mp3')]  # Replace with the actual audio file extension

    for audio_file in audio_files:
        input_path = os.path.join(input_folder, audio_file)
        output_path = os.path.join(output_dir, audio_file)

        # Call the separate_audio function for each file in the folder
        separate_audio(input_path, output_path, gpu_id)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Separate vocals from an MP3 file using the vocal separation script.")
    parser.add_argument('-i', '--input_folder', type=str, help='Path to the input MP3 folder')
    parser.add_argument('-o', '--output_folder', type=str, help='Path to the output folder')
    parser.add_argument('--gpu_id', type=int, default=0, help='GPU ID to use (default is 0)')

    args = parser.parse_args()

    separate_audio_in_folder(args.input_folder, args.output_folder, args.gpu_id)


# python spliter.py -i /path/to/input/folder -o /path/to/output/folder --gpu_id 0
