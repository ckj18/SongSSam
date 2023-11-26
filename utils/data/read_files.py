import pandas as pd

class ReadDB:
    """
    Class for reading a database.
    """

    def __init__(self, dataset_path=None, octave_path=None, octave_table_path=None, columns_name=['Title', 'Artist', 'Rating', 'Genre']):
        """
        Initialization method for the class.

        Parameters:
        - dataset_path (str): Path to the dataset file.
        - octave_path (str): Path to the octave file.
        - octave_table_path (str): Path to the octave table file.
        - columns_name (list): List of column names.
        """
        self.dataset_path = dataset_path
        self.octave_path = octave_path
        self.octave_table_path = octave_table_path
        self.columns_name = columns_name

    def read_singer(self, dataset_path, columns_name=['Title', 'Artist', 'Rating', 'Genre']):
        """
        Method to read and process singer information.

        Parameters:
        - dataset_path (str): Path to the dataset file.
        - columns_name (list): List of column names.

        Returns:
        - pd.DataFrame: Processed DataFrame.
        """
        df_singer = pd.read_csv(dataset_path, names=columns_name)

        # Remove duplicate data
        df_singer = df_singer.sort_values(by='Rating', ascending=False)  # Sort in descending order based on Rating
        df_singer = df_singer.drop_duplicates(subset=['Title', 'Artist'], keep='first')  # Keep the first row among duplicate rows

        # Combine Title and Artist to create the 'Id' column
        df_singer['Id'] = df_singer['Title'] + '_' + df_singer['Artist']

        # Map the 'Id' column to unique integers
        df_singer['Id'] = pd.factorize(df_singer['Id'])[0]

        return df_singer

    def read_octave(self, octave_path):
        """
        Method to read the octave file.

        Parameters:
        - octave_path (str): Path to the octave file.

        Returns:
        - pd.DataFrame: Read DataFrame.
        """
        return pd.read_csv(octave_path)

    def read_table(self, octave_table_path):
        """
        Method to read the octave table file.

        Parameters:
        - octave_table_path (str): Path to the octave table file.

        Returns:
        - pd.DataFrame: Read DataFrame.
        """
        return pd.read_csv(octave_table_path)

    def convert_flat_to_sharp(self, note):
        """
        Method to convert flats to sharps.

        Parameters:
        - note (str): Musical note.

        Returns:
        - str: Converted musical note.
        """
        flats = {"Ab": "G#", "Bb": "A#", "Db": "C#", "Eb": "D#", "Gb": "F#"}

        for flat, sharp in flats.items():
            note = note.replace(flat, sharp)

        return note

    def convert_to_frequency(self, row, table):
        """
        Method to convert to frequency.

        Parameters:
        - row (pd.Series): Row of the DataFrame.
        - table (pd.DataFrame): Frequency table.

        Returns:
        - tuple: Converted frequency tuple.
        """
        # Split values of the "Low" and "High" columns into note and octave
        low_note = ''.join(filter(str.isalpha, row['Low']))
        low_octave = int(''.join(filter(str.isdigit, row['Low'])))

        high_note = ''.join(filter(str.isalpha, row['High']))
        high_octave = int(''.join(filter(str.isdigit, row['High'])))

        low_note = self.convert_flat_to_sharp(low_note)
        high_note = self.convert_flat_to_sharp(high_note)

        # Find the frequency values corresponding to the note in the octave_table and return
        low_frequency = table[low_note][low_octave]
        high_frequency = table[high_note][high_octave]

        return low_frequency, high_frequency

    def check_artist(self, data1, data2):
        """
        Method to check artist information.

        Parameters:
        - data1 (pd.DataFrame): DataFrame 1.
        - data2 (pd.DataFrame): DataFrame 2.
        """
        artist = list(data1['Artist'])
        for idx in artist:
            if idx not in set(data2['Artist']):
                print(idx)

    def artist_num(self, data1, data2):
        """
        Method to check the consistency of artist information.

        Parameters:
        - data1 (pd.DataFrame): DataFrame 1.
        - data2 (pd.DataFrame): DataFrame 2.
        """
        if len(data1['Artist'].value_counts()) == len(data2['Artist'].value_counts()):
            print("Artist information is consistent.")
        else:
            print("Searching for missing artist information.")
            self.check_artist(data1, data2)
            self.check_artist(data2, data1)
