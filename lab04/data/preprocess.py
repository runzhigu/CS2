'''
Python script to process Titanic dataset for ID3 algorithm

Original data available at:
        https://www.kaggle.com/c/titanic/data
'''

import pandas as pd
import numpy as np

from sklearn.model_selection import train_test_split

# This is a Kaggle dataset so the test set does not have y values so we 
# won't bother dealing with it
train_data = pd.read_csv('original_data/train.csv')

# Split x and y values
y_train = train_data['Survived']
train_data.drop(labels='Survived', axis=1, inplace=True)

# We don't care about these features
drop_columns = ['Name', 'PassengerId', 'Ticket', 'Cabin']
train_data.drop(labels=drop_columns, axis=1, inplace=True)

# Fill missing embarked values with most common port
train_data['Embarked'].fillna('S', inplace=True)
# Rename ticket classes
train_data['Pclass'][train_data['Pclass'] == 1] = 'poor'
train_data['Pclass'][train_data['Pclass'] == 2] = 'middle'
train_data['Pclass'][train_data['Pclass'] == 3] = 'rich'
# Fill missing ages with median age
train_data['Age'].fillna(train_data['Age'].median(), inplace=True)
# Create bins to categorize features and label them
train_data['Age'] = pd.cut(train_data['Age'], bins=[
                          0, 11, 25, 40, np.inf], labels=['child', 'young', 'middle', 'old'], right=True)
train_data['Fare'] = pd.cut(train_data['Fare'], bins=[
                           0, 10, 16, 40, np.inf], labels=['low', 'average', 'high', 'rich'], right=True)
train_data['SibSp'] = pd.cut(train_data['SibSp'], bins=[
                            -0.1, 0.9, 2, np.inf], labels=['none', 'few', 'many'], right=True)
train_data['Parch'] = pd.cut(train_data['Parch'], bins=[
                            -0.1, 0.9, 1, np.inf], labels=['none', 'one', 'many'], right=True) 

# Check if dataframe is correct
print(train_data.head())

# Grab training data
X_train = train_data.values

# 15% of data is for validation and we seed random state for splitting
state = 12
test_size = 0.15

# Split into training and validation sets
X_train, X_val, y_train, y_val = train_test_split(X_train, y_train,
        test_size=test_size, random_state=state)

# Write datasets to csv files
columns = ['Pclass', 'Sex', 'Age', 'SibSp', 'Parch', 'Fare', 'Embarked']
pd.DataFrame(X_train, columns=columns).to_csv('X_train.csv', index=False)
pd.DataFrame(X_val, columns=columns).to_csv('X_val.csv', index=False)
pd.DataFrame(y_train, columns=['Survived']).to_csv('y_train.csv', index=False)
pd.DataFrame(y_val, columns=['Survived']).to_csv('y_val.csv', index=False)
