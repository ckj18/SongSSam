import librosa
import librosa.display
import matplotlib.pyplot as plt
import numpy as np

from scipy.spatial.distance import cosine
from sklearn.metrics.pairwise import cosine_similarity


class SpectrumSimilarity:
    def __init__(self) -> None:
        pass
    
    def calculate_similarity(self, spec1, spec2):
        """
        Calculate the cosine similarity between two spectra.

        Parameters:
        - spec1 (numpy.ndarray): First spectrum
        - spec2 (numpy.ndarray): Second spectrum

        Returns:
        - float: Similarity between the two spectra (closer to 1 means more similar)
        """
        # Flatten the spectra into 1D arrays
        spec1_1d = spec1.flatten()
        spec2_1d = spec2.flatten()

        # Calculate the cosine distance between the two spectra
        distance = cosine(spec1_1d, spec2_1d)

        # Convert distance to similarity
        similarity = 1 - distance

        return similarity
    
    def log_spectrum_similarity(self, source_path, target_path):
        """
        Calculate the similarity between logarithmically-scaled spectra.

        Parameters:
        - source_path (str): Path to the audio file for comparison
        - target_path (str): Path to the audio file to compare against

        Returns:
        - float: Similarity between the two logarithmically-scaled spectra (closer to 1 means more similar)
        """
        source_log_spectrum, target_log_spectrum = self.log_scale_spectrum(source_path, target_path)
        
        min_length = min(source_log_spectrum.shape[1], target_log_spectrum.shape[1])
        min_height = min(source_log_spectrum.shape[0], target_log_spectrum.shape[0])

        source_log_spectrum = source_log_spectrum[:min_height, :min_length]
        target_log_spectrum = target_log_spectrum[:min_height, :min_length]

        # Calculate similarity
        return self.calculate_similarity(source_log_spectrum, target_log_spectrum)
            
    
    def log_scale_spectrum(self, source_path, target_path):
        """
        Convert the spectrum of the given audio file to logarithmic scale.

        Parameters:
        - source_path (str): Path to the audio file for conversion
        - target_path (str): Path to the audio file for conversion

        Returns:
        - tuple: Converted logarithmic scale spectra (source, target)
        """
        # Load the files
        source_wav, _ = librosa.load(source_path)
        target_wav, _ = librosa.load(target_path)

        # Calculate the spectrum for each file
        source_spectrum = librosa.stft(source_wav)
        target_spectrum = librosa.stft(target_wav)

        # Calculate the magnitude of the spectrum
        source_spectrum_magnitude, _ = librosa.magphase(source_spectrum)
        target_spectrum_magnitude, _ = librosa.magphase(target_spectrum)

        # Convert the spectrum magnitude to logarithmic scale
        source_log_spectrum = librosa.amplitude_to_db(source_spectrum_magnitude)
        target_log_spectrum = librosa.amplitude_to_db(target_spectrum_magnitude)
        
        return source_log_spectrum, target_log_spectrum
    
    
    def plot_log_spectrum_comparison(self, source_path, target_path):
        """
        Plot a graph comparing the logarithmically-scaled spectra of two audio files.

        Parameters:
        - source_path (str): Path to the audio file for comparison
        - target_path (str): Path to the audio file to compare against
        """
        source_log_spectrum, target_log_spectrum = self.log_scale_spectrum(source_path, target_path)
        # Plot the graph
        fig, ax = plt.subplots(nrows=2, sharex=True, sharey=True)

        img = librosa.display.specshow(source_log_spectrum, x_axis='time', y_axis='log', ax=ax[0])
        ax[0].set(title='Source Log-Spectrum')
        ax[0].label_outer()

        img = librosa.display.specshow(target_log_spectrum, x_axis='time', y_axis='log', ax=ax[1])
        ax[1].set(title='Target Log-Spectrum')

        fig.colorbar(img, ax=ax, format="%+2.0f dB")

        plt.show()
        
        
class MfccSimilarity:
    def __init__(self) -> None:
        pass
    
    def extract_mfcc(self, source_path, target_path):
        """
        Extract MFCC (Mel-frequency cepstral coefficients) features from two audio files.

        Parameters:
        - source_path (str): Path to the first audio file
        - target_path (str): Path to the second audio file

        Returns:
        - tuple: Extracted MFCC features (source, target)
        """
        # Load audio files and sampling rates
        source_wav, sr_source = librosa.load(source_path, sr=None)
        target_wav, sr_target = librosa.load(target_path, sr=None)

        # Extract MFCC features
        source_mfcc = librosa.feature.mfcc(y=source_wav, sr=sr_source)
        target_mfcc = librosa.feature.mfcc(y=target_wav, sr=sr_target)
        
        return source_mfcc, target_mfcc
    
    
    def calculate_mfcc_similarity(self, source_path, target_path):
        """
        Calculate the similarity based on MFCC (Mel-frequency cepstral coefficients) features between two audio files.

        Parameters:
        - source_path (str): Path to the audio file for comparison
        - target_path (str): Path to the audio file to compare against

        Returns:
        - float: Similarity between the two MFCC features (closer to 1 means more similar)
        """
        source_wav, sr_source = librosa.load(source_path)
        target_wav, sr_target = librosa.load(target_path)
        
        # Calculate MFCC features
        source_mfcc = librosa.feature.mfcc(y=source_wav, sr=sr_source, n_mfcc=20)
        target_mfcc = librosa.feature.mfcc(y=target_wav, sr=sr_target, n_mfcc=20)
        
        # Calculate the mean vectors of MFCC features
        source_mfcc_mean = source_mfcc.mean(axis=1)
        target_mfcc_mean = target_mfcc.mean(axis=1)

        # Calculate cosine similarity
        similarity = cosine_similarity([source_mfcc_mean], [target_mfcc_mean])
        
        return similarity[0][0]
    
    
    def visualize_similarity(self, source_path, target_path):
        """
        Visualize the similarity based on MFCC (Mel-frequency cepstral coefficients) features between two audio files.

        Parameters:
        - source_path (str): Path to the audio file for comparison
        - target_path (str): Path to the audio file to compare against
        """
        # Load audio files and sampling rates
        source_mfcc, target_mfcc = self.extract_mfcc(source_path, target_path)

        # Calculate Dynamic Time Warping (DTW) distance between MFCCs
        dtw_distance, _ = librosa.sequence.dtw(source_mfcc, target_mfcc)

        # Visualize DTW path as a heatmap
        plt.figure(figsize=(10, 6))
        plt.subplot(3, 1, 1)
        librosa.display.specshow(librosa.power_to_db(source_mfcc, ref=np.max), y_axis='mel', x_axis='time')
        plt.title('Source MFCC')
        plt.colorbar(format='%+2.0f dB')

        plt.subplot(3, 1, 2)
        librosa.display.specshow(librosa.power_to_db(target_mfcc, ref=np.max), y_axis='mel', x_axis='time')
        plt.title('Target MFCC')
        plt.colorbar(format='%+2.0f dB')

        plt.subplot(3, 1, 3)
        librosa.display.specshow(dtw_distance, x_axis='frames', y_axis='frames', cmap='viridis_r')
        plt.title('Dynamic Time Warping (DTW) Distance')
        plt.colorbar()

        plt.tight_layout()
        plt.show()
