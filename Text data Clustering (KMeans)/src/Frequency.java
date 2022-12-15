import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Frequency {
    private static int[][] docTermMatrix;
    private static double[][] tf_idf_matrix;
    private static Map<String, Integer> termToNumDocs = new HashMap<>();

    public void create_document_term_matrix(List<ProcessedDoc> processedDocs, int numDocs, int numTerms, List<String> terms) {
        int[][] doc_term_matrix = new int[numDocs][numTerms];
        for (int i = 0; i < numDocs; i++){
            ProcessedDoc doc = processedDocs.get(i);
            Map<String, Integer> termMap = doc.finalTermFreq;
            for (int j = 0; j < numTerms; j++) {
                String term = terms.get(j);
                if (!termToNumDocs.containsKey(term)) {
                    termToNumDocs.put(term, 0);
                }
                if (termMap.containsKey(term)) {
                    doc_term_matrix[i][j] = termMap.get(term);
                    termToNumDocs.put(term, termToNumDocs.get(term) + 1);
                } else {
                    doc_term_matrix[i][j] = 0;
                }
            }
        }
        docTermMatrix = doc_term_matrix;
    }

    public double[][] get_tf_idf_matrix(List<ProcessedDoc> processedDocs, int numDocs, int numTerms, List<String> terms) {
        tf_idf_matrix = new double[numDocs][numTerms];
        for (int i = 0; i < numDocs; i++){
            ProcessedDoc doc = processedDocs.get(i);
            Map<String, Integer> termMap = doc.finalTermFreq;
            double totalTermsInDoc = termMap.keySet().size();
            for (int j = 0; j < numTerms; j++) {
                String term = terms.get(j);
                int docFreq = docTermMatrix[i][j];
                double tf = docFreq/totalTermsInDoc;
                double idf = Math.log(numDocs / termToNumDocs.get(term)); 
                double tf_idf = tf * idf;
                tf_idf_matrix[i][j] = tf_idf;
            }
        }
        write_tfidf(tf_idf_matrix);
        return tf_idf_matrix;   
    }

    public void write_tfidf(double[][] tfidf) {
        try {
            FileWriter wr = new FileWriter("tfidf.txt");
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            FileWriter writer = new FileWriter("tfidf.txt", true);
            writer.write("[");
            for(int i = 0; i < tfidf.length; i++){
                writer.write("[");
                for (int j = 0; j < tfidf[0].length; j++) {
                    if (j == tfidf[0].length-1) {
                        writer.write(Double.toString(tfidf[i][j]));
                    } else {
                        writer.write(tfidf[i][j] + ",");
                    }
                }
                if ( i == tfidf.length - 1) {
                    writer.write("]");
                } else {
                    writer.write("],");
                }
            }
            writer.write("]");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculate_keywords(int numFolders, List<String> terms) {
        int numDocs = tf_idf_matrix.length;
        int cols = tf_idf_matrix[0].length;
        int docsPerFolder = numDocs/numFolders;
        int folderNum = 1;
        Map<String, Map> folder_map = new HashMap<>();
        for (int i = 0; i < numDocs; i = i + docsPerFolder) {
            Map<String, Double> term_val = new HashMap<>();
            for (int k = 0; k < cols; k++) {
                double sum_tfidf = 0.0;
                for (int j = i; j < i + docsPerFolder; j++) {
                    sum_tfidf += tf_idf_matrix[j][k];
                }
                term_val.put(terms.get(k), sum_tfidf);
            }
            folder_map.put("C"+folderNum, term_val);
            folderNum++;
        }

        process(folder_map);
    }

    private void process(Map<String, Map> fm) {
        Map<String, List<String>> folderWiseKeyWords = new HashMap<>();
        for (String key: fm.keySet()) {
            Object topTen = fm.get(key).entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(10)
            .collect(Collectors.toMap(Map.Entry<String, Double>::getKey, Map.Entry<String, Double>::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            System.out.println((Map)topTen);
            
            Map m = (Map)topTen;
            folderWiseKeyWords.put(key, new ArrayList<>(m.keySet()));
        } 

        System.out.println(folderWiseKeyWords);

        try {
            FileWriter wr = new FileWriter("topics.txt");
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key: folderWiseKeyWords.keySet()){
            try{
                FileWriter writer = new FileWriter("topics.txt", true);
                writer.write("Category "+ key +" keywords: ");
                writer.write("\n");
                for (String el: folderWiseKeyWords.get(key)){
                    writer.write(el + ", ");
                }
                writer.write("\n");
                writer.write("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }    
    }
}
