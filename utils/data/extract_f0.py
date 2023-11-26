import argparse
import os
import librosa
import soundfile as sf
import torch
from tqdm import tqdm
import numpy as np
import pandas as pd
import yaml
import torch
import torch.nn.functional as F
import torchcrepe
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from torchaudio.transforms import Resample

CREPE_RESAMPLE_KERNEL = {}
F0_KERNEL = {}

def MaskedAvgPool1d(x, kernel_size):
    x = x.unsqueeze(1)
    x = F.pad(x, ((kernel_size - 1) // 2, kernel_size // 2), mode="reflect")
    mask = ~torch.isnan(x)
    masked_x = torch.where(mask, x, torch.zeros_like(x))
    ones_kernel = torch.ones(x.size(1), 1, kernel_size, device=x.device)

    # Perform sum pooling
    sum_pooled = F.conv1d(
        masked_x,
        ones_kernel,
        stride=1,
        padding=0,
        groups=x.size(1),
    )

    # Count the non-masked (valid) elements in each pooling window
    valid_count = F.conv1d(
        mask.float(),
        ones_kernel,
        stride=1,
        padding=0,
        groups=x.size(1),
    )
    valid_count = valid_count.clamp(min=1)  # Avoid division by zero

    # Perform masked average pooling
    avg_pooled = sum_pooled / valid_count

    return avg_pooled.squeeze(1)

def MedianPool1d(x, kernel_size):
    x = x.unsqueeze(1)
    x = F.pad(x, ((kernel_size - 1) // 2, kernel_size // 2), mode="reflect")
    x = x.squeeze(1)
    x = x.unfold(1, kernel_size, 1)
    x, _ = torch.sort(x, dim=-1)
    return x[:, :, (kernel_size - 1) // 2]

class f0_extractor:
    def __init__(self, f0_extractor, sample_rate = 44100, hop_size = 512, f0_min = 65, f0_max = 800):
        self.f0_extractor = f0_extractor
        self.sample_rate = sample_rate
        self.hop_size = hop_size
        self.f0_min = f0_min
        self.f0_max = f0_max
        
        if f0_extractor == 'crepe':
            key_str = str(sample_rate)
            if key_str not in CREPE_RESAMPLE_KERNEL:
                CREPE_RESAMPLE_KERNEL[key_str] = Resample(sample_rate, 16000, lowpass_filter_width = 128)
            self.resample_kernel = CREPE_RESAMPLE_KERNEL[key_str]
            
    def extract(self, audio, device = None, silence_front = 0): # audio: 1d numpy array
        # extractor start time
        n_frames = int(len(audio) // self.hop_size) + 1
                
        start_frame = int(silence_front * self.sample_rate / self.hop_size)
        real_silence_front = start_frame * self.hop_size / self.sample_rate
        audio = audio[int(np.round(real_silence_front * self.sample_rate)) : ]
    
        if self.f0_extractor == 'crepe':
            if device is None:
                device = 'cuda' if torch.cuda.is_available() else 'cpu'
            resample_kernel = self.resample_kernel.to(device)
            wav16k_torch = resample_kernel(torch.FloatTensor(audio).unsqueeze(0).to(device))
            
            f0, confidence = torchcrepe.predict(wav16k_torch, 16000, 80, self.f0_min, self.f0_max, pad=True, model='full', batch_size=512, device=device, return_periodicity=True)
            confidence = MedianPool1d(confidence, 4)
            f1 = torchcrepe.threshold.At(0.9)(f0, confidence)
            f1 = MaskedAvgPool1d(f1, 4)
            
            f1 = f1.squeeze(0).cpu().numpy()
            f1 = np.array([f1[int(min(int(np.round(n * self.hop_size / self.sample_rate / 0.005)), len(f1) - 1))] for n in range(n_frames - start_frame)])
            f1 = np.pad(f1, (start_frame, 0))
            
            f1 = f1[f1 > 0] 
            
        return f1
    
    def extract_centroid(self, frequency):
        X = np.array(frequency).reshape(-1, 1)
        
        # K-means 클러스터링 알고리즘 적용
        kmeans = KMeans(n_clusters=8, random_state=0).fit(X)

        # 각 클러스터의 센트로이드 값 확인
        centroids = kmeans.cluster_centers_
        
        return kmeans, centroids.astype(int).T
    
    
    def visualize_centroid(self, f1, kmeans, centroid):
        data = pd.DataFrame(f1)
        
        # 데이터 포인트 그리기
        plt.scatter(data.index, data.values, c=kmeans.labels_, cmap='viridis')

        # 센트로이드 그리기
        plt.scatter(range(len(centroid)), centroid, c='red')

        plt.xlabel('Index')
        plt.ylabel('Frequency')
        plt.title('K-means Clustering')
        plt.show()
        
    def f0_feature(self, centroids):
        features = ['f0_1', 'f0_2', 'f0_3', 'f0_4', 'f0_5', 'f0_6', 'f0_7', 'f0_8']
        data = pd.DataFrame(sorted(centroids), columns=features)
        
        return data
        
def main():
    p = argparse.ArgumentParser()
    p.add_argument('--extractor', '-e', type=str, default="crepe")
    p.add_argument('--input_file', '-i', required=True)
    p.add_argument('--sr', '-r', type=int, default=44100)
    p.add_argument('--hop_length', '-H', type=int, default=512)
    p.add_argument('--output_dir', '-o', type=str, default="")
    args = p.parse_args()

    print('loading wave source...', end=' ')
    extractor = f0_extractor(args.extractor)
    audio, _ = librosa.load(
        args.input_file, sr=args.sr, mono=False, dtype=np.float32, res_type='kaiser_fast')
    f0 = extractor.extract(audio)
    
    _, centroids = extractor.extract_centroid(f0)
    data = extractor.f0_feature(centroids)
    
    # 인풋 파일의 이름과 확장자를 분리
    base_name = os.path.basename(args.input_file)
    file_name, _ = os.path.splitext(base_name)

    # 아웃풋 파일 경로 생성
    output_path = os.path.join(args.output_dir, f'{file_name}f0.csv')

    # 데이터프레임 저장
    data.to_csv(output_path, index=False)


if __name__ == '__main__':
    main()