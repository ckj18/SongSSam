import os
import numpy as np
from sklearn.cluster import KMeans
import argparse

def calculate_pitch_range(file_path):
    # Use torchcrepe to predict pitch
    frequency_data = np.load(file_path)

    # Calculate the average frequency for each time frame
    average_frequency = np.mean(frequency_data, axis=0)

    return average_frequency

def cluster_pitch_ranges(pitch_ranges, num_clusters=8):
    # Reshape the pitch_ranges array
    pitch_ranges = np.array(pitch_ranges).reshape(-1, 1)

    # Perform KMeans clustering
    kmeans = KMeans(n_clusters=num_clusters, random_state=42)
    kmeans.fit(pitch_ranges)

    # Get the cluster centers (representative pitch ranges)
    cluster_centers = kmeans.cluster_centers_

    return cluster_centers

def save_cluster_centers_to_text(cluster_centers, output_file):
    np.savetxt(output_file, cluster_centers, delimiter=',', fmt='%.6f')

def main(args):
    f0_folder = args.f0_folder
    num_clusters = args.num_clusters
    output_file = args.output_file

    pitch_ranges = []

    # Iterate through each file in the folder
    for root, dirs, files in os.walk(f0_folder):
        for file in files:
            if file.endswith('.npy'):
                file_path = os.path.join(root, file)

                # Calculate the pitch range for each file
                avg_pitch_range = calculate_pitch_range(file_path)
                pitch_ranges.append(avg_pitch_range)

    # Cluster the pitch ranges
    cluster_centers = cluster_pitch_ranges(pitch_ranges, num_clusters)

    # Save cluster centers to a text file
    save_cluster_centers_to_text(cluster_centers, output_file)

    print(f"Cluster Centers (Representative Pitch Ranges) saved to {output_file}")

if __name__ == '__main__':
    # Define command-line arguments
    parser = argparse.ArgumentParser(description="Cluster pitch ranges from WAV files using KMeans.")
    parser.add_argument('--f0_folder', type=str, help='Path to the folder containing pitch data in numpy format (.npy).')
    parser.add_argument('--num_clusters', type=int, default=8, help='Number of clusters for KMeans.')
    parser.add_argument('--output_file', type=str, default='cluster_centers.txt', help='Output file for cluster centers.')

    # Parse command-line arguments
    args = parser.parse_args()

    # Call the main function with parsed arguments
    main(args)


# python f0_collect.py --f0_folder /path/to/f0_folder --num_clusters 8 --output_file cluster_centers.txt
