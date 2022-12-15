## KNN Classifier

Classify documnets using KNN algorithm in category (C1, C4, C7)

### Tasks
Preprocessing and vectorization
k-NN classifier
Fuzzy k-NN classifier
Model performance evaluation

## For Eclipse
The JDK version required is JDK-16 or above.
Stanford CoreNLP version: stanford-corenlp-4.4.0

Import BDS_HW3_Final as project.<br>
Configure Build path and add external jars (all jars of Stanford coreNLP).

To execute the program Run **Driver.java** as java application.


## For Linux Command Line: Compile and Run

Download and unzip Stanford CoreNLP JARS.  
Set `CORENLP_LIBS` to the path of the folder containing Stanford CoreNLP JARS.  
Set `OUTPUT` to the path where you want to put the compiled java classes.

Run the commands below to run on Terminal. The program takes 4-5 minutes to run completely.

```
export CORENLP_LIBS=../stanford-corenlp-latest/stanford-corenlp-4.4.0
export OUTPUT=./output
javac -cp ${OUTPUT}:${CORENLP_LIBS}/\* -d ${OUTPUT} src/*.java
java -cp ${OUTPUT}:${CORENLP_LIBS}/\* Driver
```

Note:<br>
-To change the vale of K (neighbors), update the variable "num_neighbors" in Driver.java <br>
-Add the train data (classified documents in their respective folders/category) in dataset_3/data<br>
-Add the test data (unknown documents) in dataset_3/testData/unknown