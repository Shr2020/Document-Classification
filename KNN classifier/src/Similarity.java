import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Similarity {

    public int eucledianSimilarity(double[][] tf_idf, Map<String, List<Double>> centroids, int docIndex) {
        int clusterInd = 0;
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < centroids.keySet().size(); i++){
            int clusterIndex = i + 1;
            List<Double> vector1 = DoubleStream.of(tf_idf[docIndex]).boxed().collect(Collectors.toCollection(ArrayList::new));
            List<Double> vector2 = centroids.get("C" + clusterIndex);

            double dist = calculateEucDistance(vector1, vector2);
            if (dist < min) {
                min = dist;
                clusterInd = clusterIndex;
            }
        }
        return clusterInd;
    }
    

    public double calculateEucDistance(List<Double> v1, List<Double> v2) {
        double sum = 0.0;
        if (v1.size() != v2.size()) {
            System.out.println("ERROR");
            return Double.POSITIVE_INFINITY;
        }
        for (int i = 0; i < v1.size(); i++) {
            sum += Math.pow(v1.get(i).doubleValue() - v2.get(i).doubleValue(), 2);
        }
        return Math.sqrt(sum);
    }

    public int cosineSimilarity(double[][] tf_idf, Map<String, List<Double>> centroids, int docIndex) {
        int clusterInd = 0;
        Double max = 0.0;

        for (int i = 0; i < centroids.keySet().size(); i++){
            int clusterIndex = i + 1;
            List<Double> vector1 = DoubleStream.of(tf_idf[docIndex]).boxed().collect(Collectors.toCollection(ArrayList::new));
            List<Double> vector2 = centroids.get("C" + clusterIndex);

            double dist = calculateCosDistance(vector1, vector2);
            if (dist > max) {
                max = dist;
                clusterInd = clusterIndex;
            }
        }
        return clusterInd;
    }

    public double calculateCosDistance(List<Double> v1, List<Double> v2) {
        double dotProduct = 0.0;
        double magV1 = 0.0;
        double magV2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            magV1 += Math.pow(v1.get(i), 2);
            magV2 += Math.pow(v2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(magV1) * Math.sqrt(magV2));
    }
}
