## Clustering of Textual Data

Using KMeans too classify documents in categories (C1, C4, C7).

### Tasks
#### 1. Preprocessing
1. Filter and remove stop words.
2. Apply tokenization, stemming and lemmatization.
3. Apply named-entity extraction (NER).
4. Use a sliding window approach to merge remaining phrases that belong together.
5. Generate a document-term matrix

#### 2. Clustering textual data
1. Similarity: Cosine and Euclidean
2. K-means clustering and K-means++ clustering

#### 3. Model Performance
1. Confusion matrix
2. Precision 
3. Recall 
4. F-measure

#### 4. Visualization



## Compile and Run

Download and unzip Stanford CoreNLP JARS.  
Set `CORENLP_LIBS` to the path of the folder containing Stanford CoreNLP JARS.  
Set `OUTPUT` to the path where you want to put the compiled java classes.

Run the commands below to run on Terminal. The program takes 4-5 minutes to run completely.

```
export CORENLP_LIBS=./stanford-corenlp-4.4.0
export OUTPUT=./output
javac -cp ${OUTPUT}:${CORENLP_LIBS}/\* -d ${OUTPUT} src/*.java
java -cp ${OUTPUT}:${CORENLP_LIBS}/\* Driver
```

Note:
The tfidf matrix generated here in tfidf.txt is used to run the visualization. Copy the matrix from the text file to the notebook.