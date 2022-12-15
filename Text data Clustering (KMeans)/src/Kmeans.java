import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Kmeans {
    private List<String> docNames;
    private double[][] tf_idf;
    private String mode;
    private int numClusters;
    Map<String, List<Double>> currentCentroids = new HashMap<>();
    Map<String, List<Integer>> clusteredDocs = new HashMap<>();
    int numIterations = 10;

     
    public Kmeans(List<String> docNames, double[][] tf_idf, String mode, int numClusters) {
        this.docNames = docNames;
        this.tf_idf = tf_idf;
        this.mode = mode;
        this.numClusters = numClusters;
    }
    
    public void initialize_clusters() {
        int j = 0;
        for (int i = 0 ; i < numClusters; i++){
            
            List<Double> cent = DoubleStream.of(tf_idf[j]).boxed().collect(Collectors.toCollection(ArrayList::new));
            currentCentroids.put("C"+(i+1), cent);
            j = j + 10; 
        }
        //System.out.println(currentClusters);
    }

    public List<Double> getMean(List<Integer> docsIndex) {
        //System.out.println(docsIndex);
        List<Double> mean = new ArrayList<>(Collections.nCopies(tf_idf[0].length, 0.0));
        int terms = tf_idf[0].length;
        //System.out.println(terms);
        for (int j = 0; j < terms; j++){
            double sum = 0.0;
            for (int i : docsIndex) {
                sum += tf_idf[i][j];
            }
            double avg = sum/docsIndex.size();
            mean.set(j, avg);
        }
        //System.out.println(mean);
        return mean;
    }

    public Map<String, List<Integer>> runKmeans() {
        int iter = numIterations;
        Similarity sim = new Similarity();
        while (iter > 0) {
        // reinitialise clusters
        for (int i = 0; i < numClusters; i++){
            int clusterIndex = i+1;
            clusteredDocs.put("C"+clusterIndex, new ArrayList<>());
        }
            
           for (int j = 0; j < tf_idf.length; j++){
                int cluster = 0;
                if (mode == "euc"){
                    cluster = sim.eucledianSimilarity(tf_idf, currentCentroids, j);
                }
                else{
                    cluster = sim.cosineSimilarity(tf_idf, currentCentroids, j);
                }
                clusteredDocs.get("C"+cluster).add(j);
            }

            // find new centroid
            //System.out.println("***************");
            //System.out.println(currentCentroids);
            for (int j = 0; j < numClusters; j++){
                int clusterIndex = j+1;
                List<Double> centroidNew = getMean(clusteredDocs.get("C" + clusterIndex));
                currentCentroids.put("C" + clusterIndex, centroidNew);
            }
            //System.out.println(currentCentroids);
            //System.out.println("***************");
            //System.out.println(clusteredDocs);
            iter--;
        }

        // print the clusters
        for(String clusterName: clusteredDocs.keySet()) {
            System.out.println("Documents in " + clusterName + ":");
            for (int ind :  clusteredDocs.get(clusterName)) {
                System.out.println(docNames.get(ind));
            }
            System.out.println();
        }

        return clusteredDocs;

    }     

}
