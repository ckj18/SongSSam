import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

class Visualize:
    def __init__(self, data, feature, count_num):
        """
        Initialize the visualization class.

        Parameters:
        - data (pd.DataFrame): DataFrame to visualize
        - feature (str): Name of the feature to visualize
        - count_num (int): Number of items to display per graph
        """
        self.data = data
        self.feature = feature
        self.count_num = count_num

    def data_distribution(self):
        """
        Visualize the distribution of a specific feature in the given DataFrame with multiple graphs.
        """
        # For the 'Genre' feature, multiple genres are separated by ';', so create new rows by splitting them
        if self.feature == 'Genre':
            new_rows = []
            for _, row in self.data.iterrows():
                genres = row[self.feature].split()
                for genre in genres:
                    new_rows.append({self.feature: genre})

            self.data = pd.DataFrame(new_rows)

        counts = self.data[self.feature].value_counts()
        num_subplots = len(counts) // self.count_num + 1

        plt.figure(figsize=(14, 6 * num_subplots))
        for i in range(num_subplots):
            start_idx = i * self.count_num
            end_idx = (i + 1) * self.count_num

            plt.subplot(num_subplots, 1, i + 1)
            sns.barplot(x=counts.index[start_idx:end_idx], y=counts.values[start_idx:end_idx])
            plt.xticks(rotation=45, horizontalalignment='right')

            plt.title("Data distribution (Graph {})".format(i + 1))
            plt.xlabel("Data")
            plt.ylabel("Count")

            plt.tight_layout()

        plt.show()
