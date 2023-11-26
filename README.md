# SongSSam Model Train & Inference

## Overview

The dvoice toolkit is a versatile collection of Python scripts designed for audio processing, feature extraction, and analysis. These scripts serve various purposes in the audio processing pipeline, including voice processing, pitch extraction, segmentation, song inference, song matching, and database parsing.

## Usage

Before running the scripts, ensure you have the necessary dependencies installed. You can install them using the following command:

```bash
pip install -r requirements.txt
```

### 1. dvoice_process.py

```bash
python dvoice_process.py -i /path/to/input_folder -m /path/to/model_checkpoint -o /path/to/output_folder
```

- `-i`: Path to the input folder containing audio files.
- `-m`: Path to the model checkpoint for voice processing.
- `-o`: Path to the output folder where processed files will be saved.

### 2. f0_collect.py

```bash
python f0_collect.py --f0_folder /path/to/f0_folder --num_clusters 8 --output_file cluster_centers.txt
```

- `--f0_folder`: Path to the folder containing pitch (f0) information.
- `--num_clusters`: Number of clusters for k-means clustering.
- `--output_file`: Output file path to save cluster centers.

### 3. segment.py

```bash
python segment.py -i /path/to/input_folder -o /path/to/output_folder
```

- `-i`: Path to the input folder containing audio files.
- `-o`: Path to the output folder where segmented files will be saved.

### 4. song_inference.py

```bash
python song_inference.py -i /your/input/folder -o /your/output/folder -m /your/model_checkpoint/path -k 0.0 -eak 0.0 --auto_key
```

- `-i`: Path to the input folder containing audio files for song inference.
- `-o`: Path to the output folder where inference results will be saved.
- `-m`: Path to the model checkpoint for song inference.
- `-k`: Key detection threshold.
- `-eak`: Energy threshold for automatic key detection.
- `--auto_key`: Enable automatic key detection.

### 5. match_song.py

```bash
python match_song.py -i /path/to/input_folder -d /path/to/song_directory -c /path/to/song_info_csv -o /path/to/output_folder -g gpu_id
```

- `-i`: Path to the input folder containing audio files for song matching.
- `-d`: Path to the directory containing reference songs.
- `-c`: Path to the CSV file containing song information.
- `-o`: Path to the output folder where matching results will be saved.
- `-g`: GPU ID for processing.

### 6. spliter.py

```bash
python spliter.py -i /path/to/input/folder -o /path/to/output/folder --gpu_id 0
```

- `-i`: Path to the input folder containing audio files to be split.
- `-o`: Path to the output folder where split files will be saved.
- `--gpu_id`: GPU ID for processing.

### 7. extracter.py

```bash
python extracter.py --input_folder /path/to/input_folder --output_dir /path/to/output_dir
```

- `--input_folder`: Path to the input folder containing audio files for feature extraction.
- `--output_dir`: Path to the output directory where extracted features will be saved.

### 8. parsing_database.py

```bash
python parsing_database.py --dir_path <dir_path_value> --split_path <split_path_value> --data_path <data_path_value> --split_num <split_num_value>
```

- `--dir_path`: Path to the directory.
- `--split_path`: Path for splitting.
- `--data_path`: Path for data.
- `--split_num`: Number for splitting.
