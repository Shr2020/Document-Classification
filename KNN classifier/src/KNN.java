import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;


public class KNN {

    private double[][] train_tfidf;
    private Map<Integer, String> document_category_map  = new HashMap<Integer, String>();
    private Map<String, String> category_labels_map  = new HashMap<String, String>();

    KNN(double[][] tfIdf, Map<Integer, String> docToCategoryMap, Map<String, String> category_labels_map) {
        this.train_tfidf = tfIdf;
        this.document_category_map = docToCategoryMap;
        this.category_labels_map = category_labels_map;
    }

    public HashMap<String, Integer> knn_process(double[] testdoc_tfidf, int num_neighbors, List<String> trainDocs, String testDocName) {
        HashMap<Integer, Double> similarityMap = new HashMap<>();
        Similarity sim = new Similarity();
        for (int i = 0; i < train_tfidf.length; i++){
            List<Double> train = DoubleStream.of(train_tfidf[i]).boxed().collect(Collectors.toCollection(ArrayList::new));
            List<Double> test = DoubleStream.of(testdoc_tfidf).boxed().collect(Collectors.toCollection(ArrayList::new));
            double similarity = sim.calculateCosDistance(train, test);
            similarityMap.put(i, similarity);
        }

        // sort the map
        Map<Integer, Double> sortedMap = new LinkedHashMap<>();
        similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        
        Integer[] keys = sortedMap.keySet().toArray(new Integer[sortedMap.size()]);
        HashMap<String, Integer> results = new HashMap<String, Integer>();
        int k = num_neighbors;
        int i = 0;
        System.out.println("\nKNN Output: Documents That are most similar to document: " + testDocName);
        while (k > 0) {
            String document_category = document_category_map.get(keys[i]);
            if (results.containsKey(document_category)) {
                results.put(document_category, results.get(document_category) + 1);
            } else results.put(document_category, 1);
            System.out.println("Document name: " + trainDocs.get(keys[i]) + " Category: " +  document_category + ": " + category_labels_map.get(document_category));
            k--;
            i++;
        }

        // return Fuzzy KNN Output
        System.out.println("\nFuzzy KNN Output:");
        for (String key: results.keySet()) {
            System.out.println(key + "="+ (Double.valueOf(results.get(key))/Double.valueOf(num_neighbors))*100 + "%");
        }
        System.out.println("\n*****************************\n");
        return results;
    }






















    
}
