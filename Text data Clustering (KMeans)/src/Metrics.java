import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Metrics {
    List<Set<Integer>> generated_classifications = new ArrayList<>();
    List<Set<Integer>> original_clusters =  new ArrayList<>();
    double[][] confusionMatrix;
    Map<String, List<Integer>> docClusters;
    
    Metrics(Map<String, List<Integer>> docClusters) {
        this.docClusters = docClusters;
        create_original_clusters();
        create_generated_classification();
    }

    private void create_generated_classification() {
        for(int i = 0; i < original_clusters.size(); i++){
            generated_classifications.add(new HashSet<>(docClusters.get("C" + (i+1))));
        }
        // List<String> keys = new ArrayList<>(docClusters.keySet());
        // List<Integer> clusters = new ArrayList<>();
        // for (int j = 0; j < original_clusters.size(); j++) {
        //     clusters.add(j);
        // }
        // while(clusters.size() > 0) {
        //     int max = 0;
        //     String k = "";
        //     for (int i = 0; i < keys.size(); i++) {
        //         List<Integer> l = docClusters.get(keys.get(i));
        //         // get intersection of two sets
        //         Set<Integer> temp = new HashSet<>(l);
        //         temp.retainAll(original_clusters.get(clusters.get(0)));
        //         if (temp.size() > max) {
        //             max = temp.size();
        //             k = keys.get(i);
        //         }
        //     }
        //     generated_classifications.set(clusters.get(0), new HashSet<>(docClusters.get(k)));
        //     clusters.remove(0);
        //     keys.remove(k);
        // }
    }

    public void create_original_clusters() {
        original_clusters.add(new HashSet<>(Arrays.asList(new Integer[] {0,1,2,3,4,5,6,7})));
        original_clusters.add(new HashSet<>(Arrays.asList(new Integer[] {8,9,10,11,12,13,14,15})));
        original_clusters.add(new HashSet<>(Arrays.asList(new Integer[] {16,17,18,19,20,21,22,23})));
        confusionMatrix = new double[docClusters.keySet().size()][original_clusters.size()];
    }

    public void calculateConfusionMatrix() {
        System.out.println("Confusion Matrix: ");
        for (int i = 0; i < generated_classifications.size(); i++) {
            for (int j = 0; j < original_clusters.size(); j++) {
                
                // get intersection of two sets
                Set<Integer> temp = new HashSet<>(generated_classifications.get(i)); // use the copy constructor
                temp.retainAll(original_clusters.get(j));
                confusionMatrix[i][j] = temp.size();
                System.out.print(confusionMatrix[i][j]+" , ");
            }
            System.out.println();
        }
    } 

    public void calculatePrecisionRecall() {
        List<Double> prec = new ArrayList<>();
        List<Double> rec = new ArrayList<>();
        double sumPrec = 0.0;
        double sumRec = 0.0;
        for(int i = 0; i < confusionMatrix.length; i++) {
            double den = 0.0;
            double num = 0.0;
            for(int j = 0; j < confusionMatrix[0].length; j++){
                if (i == j) {
                    num = confusionMatrix[i][j];
                }
                den += confusionMatrix[i][j];
            }
            sumPrec += num/den;
            prec.add(i, num/den);
        }
        double avg_prec = sumPrec / confusionMatrix.length;

        for(int i = 0; i < confusionMatrix[0].length; i++) {
            double den = 0.0;
            double num = 0.0;
            for(int j = 0; j < confusionMatrix.length; j++){
                if (i == j) {
                    num = confusionMatrix[j][i];
                }
                den += confusionMatrix[j][i];
            }
            sumRec += num/den;
            rec.add(i, num/den);
        }
        double avg_rec = sumRec / confusionMatrix.length;
        double f1score = 2 * (avg_prec*avg_rec) / (avg_prec + avg_rec);
        System.out.println();
        System.out.println("C1, C2, C3 Precision: ");
        System.out.println(prec.toString());
        System.out.println();
        System.out.println("C1, C2, C3 Recall: ");
        System.out.println(rec.toString());
        System.out.println();
        System.out.println("Precision: " + avg_prec);
        System.out.println("Recall: " + avg_rec);
        System.out.println("F-score: " + f1score);
    }

    public void calculateMetrics(){
        calculateConfusionMatrix();
        calculatePrecisionRecall();
    }
    
}
