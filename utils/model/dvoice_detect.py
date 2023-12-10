import os
import librosa
import torch
import numpy as np
import pandas as pd
from sklearn.utils import shuffle
from sklearn.ensemble import VotingClassifier, BaggingClassifier, AdaBoostClassifier, GradientBoostingClassifier, RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, matthews_corrcoef, roc_auc_score
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
import matplotlib.pyplot as plt
from xgboost import XGBClassifier
from xgboost import plot_importance
import tensorflow as tf
from tensorflow import keras
import shutil
import time
from scipy.special import softmax
import sys
import tensorflow as tf
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.optimizers import SGD
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint, ReduceLROnPlateau
import h5py
import argparse
import cv2
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Flatten, Dense, Dropout, Activation, LeakyReLU, Softmax, Concatenate, Lambda, Bidirectional, LSTM
from tensorflow.keras.models import Sequential, Model
from keras.models import Sequential
from keras.layers import Bidirectional, LSTM, Dense, Dropout
from keras.regularizers import l2
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import StratifiedKFold, cross_val_score, train_test_split
from sklearn.ensemble import RandomForestClassifier, VotingClassifier, BaggingClassifier, AdaBoostClassifier, GradientBoostingClassifier
from sklearn.neighbors import KNeighborsClassifier
from xgboost import XGBClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression

feature_names = ['Chromagram_Mean', 'RootMeanSquare_Mean', 'SpectralCentroid_Mean',
       'SpectralBandwidth_Mean', 'Rolloff_Mean', 'ZeroCrossingRate_Mean',
       'MFCC1_Mean', 'MFCC2_Mean', 'MFCC3_Mean', 'MFCC4_Mean', 'MFCC5_Mean',
       'MFCC6_Mean', 'MFCC7_Mean', 'MFCC8_Mean', 'MFCC9_Mean', 'MFCC10_Mean',
       'MFCC11_Mean', 'MFCC12_Mean', 'MFCC13_Mean', 'MFCC14_Mean',
       'MFCC15_Mean', 'MFCC16_Mean', 'MFCC17_Mean', 'MFCC18_Mean',
       'MFCC19_Mean', 'MFCC20_Mean']


def read_feature(filepath):
    with open(filepath, 'r') as file:
        feature_names = [line.strip() for line in file]
    return feature_names

def csv_loader(folder_path):
    df_dict = {}
    
    for filename in os.listdir(folder_path):
        df_dict[filename] = pd.read_csv(folder_path + '/' + filename)
        
    return df_dict        

def data_loader(data_folder, feature_names):
    df_dict = csv_loader(data_folder)

    fake1_data = df_dict['fake1.csv'][feature_names]
    fake2_data = df_dict['fake2.csv'][feature_names]

    real1_data = df_dict['real1.csv'][feature_names]
    real2_data = df_dict['real2.csv'][feature_names]


    real1_data = real1_data.copy()
    real2_data = real2_data.copy()
    fake1_data = fake1_data.copy()
    fake2_data = fake2_data.copy()


    real1_data.loc[:, 'target'] = 0
    real2_data.loc[:, 'target'] = 0
    fake1_data.loc[:, 'target'] = 1
    fake2_data.loc[:, 'target'] = 1
    
    data_1 = pd.concat([real1_data, real2_data, fake1_data, fake2_data])
    
    return data_1

# 주어진 특징들의 이름
feature_names = [
    "Chromagram", "RootMeanSquare", "SpectralCentroid", "SpectralBandwidth",
    "Rolloff", "ZeroCrossingRate", "MFCC1", "MFCC2", "MFCC3", "MFCC4",
    "MFCC5", "MFCC6", "MFCC7", "MFCC8", "MFCC9", "MFCC10", "MFCC11",
    "MFCC12", "MFCC13", "MFCC14", "MFCC15", "MFCC16", "MFCC17", "MFCC18",
    "MFCC19", "MFCC20"
]

# 폴더 내의 모든 WAV 파일에 대해 특징 추출
def extract_features(folder_path):
    features_list = []

    for filename in os.listdir(folder_path):
        if filename.endswith(".wav"):
            filepath = os.path.join(folder_path, filename)

            try:
                # librosa를 사용하여 음성 파일 로드
                y, sr = librosa.load(filepath)

                # 특징 추출
                chroma = np.mean(librosa.feature.chroma_stft(y=y, sr=sr))
                rmse = np.mean(librosa.feature.rms(y=y))
                spec_cent = np.mean(librosa.feature.spectral_centroid(y=y, sr=sr))
                spec_bw = np.mean(librosa.feature.spectral_bandwidth(y=y, sr=sr))
                rolloff = np.mean(librosa.feature.spectral_rolloff(y=y, sr=sr))
                zcr = np.mean(librosa.feature.zero_crossing_rate(y))

                mfccs = np.mean(librosa.feature.mfcc(y=y, sr=sr, n_mfcc=20).T, axis=0)

                # 각 특징에 대한 평균, 중간값 및 표준 편차 계산
                feature_values = [np.mean(chroma),
                                  np.mean(rmse),
                                  np.mean(spec_cent),
                                  np.mean(spec_bw),
                                  np.mean(rolloff),
                                  np.mean(zcr)]

                for mfcc_value in mfccs:
                    feature_values.extend([np.mean(mfcc_value)])

                features_list.append(feature_values)

            except Exception as e:
                print(f"Error processing file {filename}: {str(e)}")
                # 예외가 발생하면 해당 파일은 건너뛰고 다음 파일을 처리합니다.

    return features_list

def data_split(df):
    # Extract features and target
    X = df.drop(columns=['target'])
    y = df['target']

    # Standardize the features
    scaler = StandardScaler()
    X = scaler.fit_transform(X)

    # Split the data into training, validation, and testing sets
    X_train, X_temp, y_train, y_temp = train_test_split(X, y, test_size=0.3, random_state=42)
    X_valid, X_test, y_valid, y_test = train_test_split(X_temp, y_temp, test_size=0.5, stratify=y_temp, random_state=42)
    
    return X_train, y_train, X_valid, y_valid, X_test, y_test
    
def LSTM_model(X_train, input_dim=64, dropout_rate=0.5, lr=1e-3):
    # Build the BiLSTM model
    model = Sequential()
    model.add(Bidirectional(LSTM(input_dim, activation='relu', return_sequences=True, kernel_regularizer=l2(lr), recurrent_regularizer=l2(lr)), input_shape=(X_train.shape[1], X_train.shape[2])))
    model.add(Dropout(dropout_rate))

    model.add(Bidirectional(LSTM(input_dim, activation='relu', return_sequences=True, kernel_regularizer=l2(lr), recurrent_regularizer=l2(lr))))
    model.add(Dropout(dropout_rate))

    model.add(Bidirectional(LSTM(input_dim, activation='relu', kernel_regularizer=l2(lr), recurrent_regularizer=l2(lr))))
    model.add(Dropout(dropout_rate))

    model.add(Dense(1, activation='sigmoid'))
    
    return model
    
def metric(y_test, y_pred, y_pred_proba, results, model_name):
    # Model evaluation
    # Assuming y_test and y_pred are binary (0 or 1)
    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(y_test, y_pred)
    recall = recall_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    mcc = matthews_corrcoef(y_test, y_pred)
    roc_auc = roc_auc_score(y_test, y_pred_proba)

    results[model_name] = {
            'Accuracy': accuracy,
            'Precision': precision,
            'Recall': recall,
            'F1 Score': f1,
            'MCC': mcc,
            'ROC AUC': roc_auc
    }
    
    return results

def ml_load():
    rf_model = RandomForestClassifier(n_estimators=100, random_state=42)
    knn_model = KNeighborsClassifier(n_neighbors=5)
    xgb_model = XGBClassifier(n_estimators=100, random_state=42)
    logistic_model = LogisticRegression(penalty='l2', C=1.0, random_state=42)

    # 소프트 보팅 앙상블 초기화
    soft_voting_model = VotingClassifier(estimators=[('rf', rf_model), ('knn', knn_model), ('xgb', xgb_model), ('logistic', logistic_model)], voting='soft')

    # 하드 보팅 앙상블 초기화
    hard_voting_model = VotingClassifier(estimators=[('rf', rf_model), ('knn', knn_model), ('xgb', xgb_model), ('logistic', logistic_model)], voting='hard')

    # 랜덤패치배깅 앙상블 초기화
    random_patches_bagging_model = BaggingClassifier(estimator=DecisionTreeClassifier(), n_estimators=50, max_samples=0.8, max_features=0.8, random_state=42)

    # 아다부스트 앙상블 초기화
    adaboost_model = AdaBoostClassifier(estimator=DecisionTreeClassifier(), n_estimators=50, learning_rate=0.001, random_state=42)

    # 그래디언트 부스팅 앙상블 초기화
    gradient_boosting_model = GradientBoostingClassifier(n_estimators=50, learning_rate=0.001, max_depth=3, random_state=42)
    
    models = [rf_model, knn_model, xgb_model, logistic_model, soft_voting_model, hard_voting_model, random_patches_bagging_model, adaboost_model, gradient_boosting_model]
    models_name = ['rf_model', 'knn_model', 'xgb_model', 'logistic_model', 'soft_voting_model', 'hard_voting_model',
                   'random_patches_bagging_model', 'adaboost_model', 'gradient_boosting_model']
    return models, models_name

def ml_model(df, results):
    X_train, y_train, X_valid, y_valid, X_test, y_test = data_split(df)
    models, models_name = ml_load()

    for model in models:
        model.fit(X_train, y_train)

    for idx, model in enumerate(models):
        model_name = models_name[idx]
        y_pred = model.predict(X_test)
        results = metric(y_test, y_pred, y_pred, results, model_name)
        
    return results, y_test

def CNN(filter_scale=1, dropout_rate=0.8):
    # CNN 모델 정의
    model = Sequential()
    model.add(Conv2D(64//filter_scale, (3, 3), name='conv1', padding='valid', kernel_initializer='he_normal', input_shape=(64, 64, 3)))
    model.add(LeakyReLU(0.01))
    model.add(Conv2D(32//filter_scale, (3, 3), name='conv2', padding='valid', kernel_initializer='he_normal'))
    model.add(LeakyReLU(0.01))
    model.add(MaxPooling2D((3, 3), strides=(3, 3)))

    model.add(Conv2D(128//filter_scale, (3, 3), name='conv3', padding='valid', kernel_initializer='he_normal'))
    model.add(LeakyReLU(0.01))
    model.add(Conv2D(64//filter_scale, (3, 3), name='conv4', padding='valid', kernel_initializer='he_normal'))
    model.add(LeakyReLU(0.01))
    model.add(MaxPooling2D((3, 3), strides=(3, 3)))

    model.add(Flatten())
    model.add(Dense(256//filter_scale))
    model.add(Dropout(dropout_rate))
    model.add(Dense(64//filter_scale))
    model.add(Dropout(dropout_rate))
    model.add(Dense(2))
    model.add(Softmax(axis=1))
    
    return model

def label_encoding(y_train, y_valid, y_test):
    y_train = to_categorical(y_train)
    y_valid = to_categorical(y_valid)
    y_test = to_categorical(y_test)
    
    return y_train, y_valid, y_test

# 이미지 데이터를 로드하고 전처리하는 함수
def load_images_and_labels(folder_path):
    images = []
    labels = []

    for folder in os.listdir(folder_path):
        folder_path_full = os.path.join(folder_path, folder)
        if os.path.isdir(folder_path_full):
            label = 1 if "fake" in folder else 0

            for filename in os.listdir(folder_path_full):
                img_path = os.path.join(folder_path_full, filename)
                img = cv2.imread(img_path)
                img = cv2.resize(img, (64, 64))  # 이미지 크기를 조절
                img = img / 255.0
                images.append(img)
                labels.append(label)

    return np.array(images), np.array(labels)

def CV_model(results):
    # 데이터셋 로드 및 전처리
    folder_path = "./dataset/dvoice_img"
    images, labels = load_images_and_labels(folder_path)

    X_train, X_temp, y_train, y_temp = train_test_split(images, labels, test_size=0.3, random_state=42)
    X_valid, X_test, y_valid, y_test = train_test_split(X_temp, y_temp, test_size=0.5, stratify=y_temp, random_state=42)

    # 레이블을 원-핫 인코딩
    y_train, y_valid, y_test = label_encoding(y_train, y_valid, y_test)

    model = CNN()

    # Define Early Stopping and Learning Rate Scheduler callbacks
    early_stopping = EarlyStopping(monitor='val_loss', patience=3, restore_best_weights=True)
    lr_scheduler = ReduceLROnPlateau(monitor='val_loss', factor=0.1, patience=2, verbose=1, min_lr=1e-7)

    # 모델 컴파일
    model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

    # Model training with Early Stopping and Learning Rate Scheduler
    model.fit(X_train, y_train, validation_data=(X_valid, y_valid), epochs=6, batch_size=32, callbacks=[early_stopping, lr_scheduler])

    # 모델 평가

    # Model evaluation
    y_pred_proba_cnn = model.predict(X_test)
    y_pred = np.argmax(y_pred_proba_cnn, axis=1)
    y_true = np.argmax(y_test, axis=1)
    model_name = 'CNN'
    checkpoint = load_model_from_checkpoint(model, 'cv_checkpoint.pt')
    
    return metric(y_true, y_pred, y_pred, results, model_name), y_test, y_pred_proba_cnn, model, X_test, checkpoint

def NLP_model(df, results):
    X_train, y_train, X_valid, y_valid, X_test, y_test = data_split(df)

    # Reshape the features for LSTM input
    X_train = X_train.reshape((X_train.shape[0], 1, X_train.shape[1]))
    X_valid = X_valid.reshape((X_valid.shape[0], 1, X_valid.shape[1]))
    X_test = X_test.reshape((X_test.shape[0], 1, X_test.shape[1]))

    model = LSTM_model(X_train)
    early_stopping = EarlyStopping(monitor='val_loss', patience=4, restore_best_weights=True)
    lr_scheduler = ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=2, verbose=1, min_lr=1e-7)
    
    # Compile the model
    model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

    # Model training with Early Stopping and Learning Rate Scheduler
    model.fit(X_train, y_train, validation_data=(X_valid, y_valid), epochs=25, batch_size=32, callbacks=[early_stopping, lr_scheduler])
    
    # Make predictions on the test set
    y_pred_proba_bilstm = model.predict(X_test)
    y_pred = (y_pred_proba_bilstm > 0.5).astype(int)

    model_name = 'BiLSTM'
    checkpoint = load_model_from_checkpoint(model, 'nlp_checkpoint.pt')
    
    return metric(y_test, y_pred, y_pred_proba_bilstm, results, model_name), y_test, y_pred_proba_bilstm, checkpoint

def ensemble_DL_weighted_average(y_test, y_pred_proba_cnn, y_pred_proba_bilstm, results, weight_cnn=0.5, weight_bilstm=0.5):
    # Calculate the weighted average of probabilities
    y_pred_combined_proba = (weight_cnn * y_pred_proba_cnn[:, 1].reshape(-1, 1) +
                            weight_bilstm * y_pred_proba_bilstm) / (weight_cnn + weight_bilstm)

    # Convert probabilities to binary predictions
    y_pred_combined = (y_pred_combined_proba > 0.5).astype(int)

    # Evaluate the ensemble
    accuracy = accuracy_score(y_test, y_pred_combined)
    precision = precision_score(y_test, y_pred_combined)
    recall = recall_score(y_test, y_pred_combined)
    f1 = f1_score(y_test, y_pred_combined)
    mcc = matthews_corrcoef(y_test, y_pred_combined)
    roc_auc = roc_auc_score(y_test, y_pred_combined_proba)

    # Store results in the 'results' dictionary
    model_name = 'Weighted Average Ensemble'
    results[model_name] = {
        'Accuracy': accuracy,
        'Precision': precision,
        'Recall': recall,
        'F1 Score': f1,
        'MCC': mcc,
        'ROC AUC': roc_auc
    }

    return results

def get_grad_cam(model, img_array, layer_name):
    # Extract the specified layer's output and the model's predicted class
    grad_model = Model(
        [model.inputs],
        [model.get_layer(layer_name).output, model.output]
    )

    with tf.GradientTape() as tape:
        conv_output, predictions = grad_model(img_array)
        class_predictions = predictions[:, np.argmax(predictions[0])]

    # Get the gradient of the predicted class with respect to the model's output feature map
    grads = tape.gradient(class_predictions, conv_output)

    # Compute the guided gradients
    guided_grads = (tf.cast(conv_output > 0, "float32") * tf.cast(grads > 0, "float32") * grads)

    # Compute the weights using global average pooling
    weights = tf.reduce_mean(guided_grads, axis=(0, 1, 2))

    # Get the feature map of the last convolutional layer
    conv_output, predictions = grad_model(img_array)

    # Create the heatmap
    heatmap = tf.reduce_sum(tf.multiply(weights, conv_output), axis=-1)
    heatmap = np.maximum(heatmap, 0)
    heatmap /= np.max(heatmap)

    return heatmap

def load_model_from_checkpoint(model, checkpoint_path):
    # 모델의 상태를 불러오기 위해 optimizer와 함께 checkpoint를 불러옴
    checkpoint = torch.load(checkpoint_path)
    model.load_state_dict(checkpoint['model_state_dict'])
    # 만약 optimizer 상태도 저장했다면 아래 코드를 사용하여 optimizer를 불러올 수 있음
    # optimizer.load_state_dict(checkpoint['optimizer_state_dict'])
    
    # 모델을 evaluation 모드로 설정
    model.eval()
    return model

def visualize_grad_cam(model, X_test, y_test, y_pred):
    # Select 6 random indices for visualization
    indices = np.random.choice(len(X_test), size=6, replace=False)

    # Grad-CAM 생성 및 시각화
    last_conv_layer_name = 'conv4'

    plt.figure(figsize=(15, 8))
    for i, idx in enumerate(indices, 1):
        selected_image = X_test[idx:idx+1]
        true_label = np.argmax(y_test[idx])
        pred_label = np.argmax(y_pred[idx])

        heatmap = get_grad_cam(model, selected_image, last_conv_layer_name)

        plt.subplot(2, 6, i)
        plt.imshow(selected_image[0])
        plt.title('Ori: {}\n Pred: {}'.format(true_label, pred_label))

        plt.subplot(2, 6, i + 6)
        plt.imshow(heatmap[0], cmap='viridis')
        plt.title('Grad-CAM Heatmap')

    plt.show()


# # Grad-CAM 시각화 호출
# visualize_grad_cam(model, X_test, y_test_cv, y_pred_proba_cnn)


if __name__ == "__main__":
    data_folder = './utils/csv_data'
    feature_names = read_feature('feature_names.txt')
    df = data_loader(data_folder, feature_names)
    results = {}
    ml_result, y_test_ml = ml_model(df, results)
    nlp_result, y_test_nlp, y_pred_proba_bilstm = NLP_model(df, results)
    cv_result, y_test_cv, y_pred_proba_cnn, model, X_test = CV_model(results)

    # 데이터프레임 생성
    df_results = pd.DataFrame(results).T  # Transpose for better visualization
    df_results.index.name = 'Model'
    df_results