# -*- coding: utf-8 -*-
"""
Created on Sat Jun 26 21:33:41 2021

@author: Matthew
"""

import pandas as pd
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn import metrics
from sklearn.tree import plot_tree

hf = pd.read_csv (r'heart_failure_clinical_records_dataset.csv')

hf = {
  'attributes': pd.DataFrame(hf, columns=hf.columns[:12]),
  'target': pd.DataFrame(hf, columns=['DEATH_EVENT']),
  'targetNames': ['Alive','Dead']
  }

#SPLITTING THE DATASETS INTO 80-20 TRAIN-TEST PROPORTION
for dt in [hf]:
  x_train, x_test, y_train, y_test = train_test_split(dt['attributes'], dt['target'], test_size=0.2, random_state=0)
  dt['train'] = {
    'attributes': x_train,
    'target': y_train
  }
  dt['test'] = {
    'attributes': x_test,
    'target': y_test
  }

#SETTING THE INPUT OF THE DECISION TREE CLASSIFIER
#NOTE: .columns[:12] is for input including the time; set the max_depth = 3

input_columns = hf['attributes'].columns[:11].tolist()
dtc= DecisionTreeClassifier(criterion='gini', max_depth = 6)

#TRAIN THE MODEL
dtc.fit(hf['train']['attributes'][input_columns], hf['train']['target'].DEATH_EVENT)

#TEST THE MODEL AGAINST THE TESTING DATASET
predicts = dtc.predict(hf['test']['attributes'][input_columns])
print(pd.DataFrame(list(zip(hf['test']['target'].DEATH_EVENT,predicts)), columns=['target', 'predicted']))

#CALCULATING THE TESTING ACCURACY
accuracy = dtc.score(hf['test']['attributes'][input_columns],hf['test']['target'].DEATH_EVENT)
print(f'Accuracy: {accuracy:.4f}')

#COMPUTING THE CONFUSION METRIX
print(metrics.confusion_matrix(hf['test']['target'].DEATH_EVENT,predicts))
print(metrics.classification_report(hf['test']['target'].DEATH_EVENT, predicts, zero_division = 0))

#Feature importance calculation
importances = dtc.feature_importances_
importanceList = []

print("Feature importance ranking")
for i in range(0, len(importances)):
  importanceList.append([input_columns[i], importances[i]])
  print(f'{input_columns[i]}: {importances[i]:.4f}')


#CALCULATING MAX DEPTH FOR OPTIMAL ACCURACY
depthList = []
for x in range(1, 21):
  depthList.append(x)
#Training accuracy, Testing accuracy
accTrainList = []
accTestList = []

for i in range(0, len(depthList)):
  dtc_exp = DecisionTreeClassifier(max_depth = depthList[i])
  dtc_exp.fit(hf['train']['attributes'][input_columns], hf['train']['target'].DEATH_EVENT)
  accTrainList.append(dtc_exp.score(hf['train']['attributes'][input_columns], 
                      hf['train']['target'].DEATH_EVENT))
  accTestList.append(dtc_exp.score(hf['test']['attributes'][input_columns], 
                    hf['test']['target'].DEATH_EVENT))
  
'''  
plt.figure()
plt.plot(depthList, accTrainList, label = "Training accuracy")
plt.plot(depthList, accTestList, label = "Testing accuracy")
plt.xlabel('Max depth')
plt.ylabel('Accuracy')
plt.title('Max depth against training & testing data')
plt.legend()
plt.show()  
'''

#SAVE THE IMAGE TO VISUALIZE THE OUTPUT
plt.figure(figsize=[50,50])
tree = plot_tree(dtc, feature_names=input_columns, 
                 class_names=hf['targetNames'], filled=True, rounded=True)
plt.savefig('Clincal_6d.png')