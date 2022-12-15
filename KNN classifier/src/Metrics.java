import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Metrics {

    int[][] confusionMatrix;
    List<HashMap<String, Integer>> predictions;
    List<String> categories;
    Map<String, Integer> nameToIndex  = new HashMap<String, Integer>();
    
    public Metrics(List<HashMap<String, Integer>> pediction_map, List<String> categories) {
        this.predictions = pediction_map;
        this.categories = categories;
        initialize_confusionMatrix();
    }

    Map<Integer, String> originalPrediction  = new HashMap<Integer, String>() {{
        put(0,"C1");
        put(1,"C1");
        put(2,"C1");
        put(3,"C1");
        put(4,"C4");
        put(5,"C4");
        put(6,"C7");
        put(7,"C7");
        put(8,"C4");
        put(9,"C1");
    }};

    private void initialize_confusionMatrix() {
        confusionMatrix = new int[categories.size()][categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            nameToIndex.put(categories.get(i), i);
        }
    }

    // Map<String, Integer> nameToIndex  = new HashMap<String, Integer>() {{
    //     put("C1", 0);
    //     put("C4", 1);
    //     put("C7", 2);
    // }};

    public void calculateConfusionMatrix() {
        for (int i = 0; i < predictions.size(); i++ ) {
            HashMap<String, Integer> preds = predictions.get(i);
            String predictedCategory = find_predicted_category(preds);
            int row = nameToIndex.get(predictedCategory);
            int col = nameToIndex.get(originalPrediction.get(i));
            confusionMatrix[row][col]++;
        }
        print_confusion_matrix();
    }

    public void print_confusion_matrix() {
        System.out.println("CONFUSION MATRIX:");
        System.out.println("Columns and Row Order: " + categories.toString());
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix.length; j++) {
                System.out.print(confusionMatrix[i][j]);
                if (j < confusionMatrix.length - 1) {
                    System.out.print(" , ");
                }
            }
            System.out.println();
        }
    }

    public String find_predicted_category(HashMap<String, Integer> preds) {
        int max = 0;
        String category = "";
        for (String key : preds.keySet()) {
            if (preds.get(key) > max) {
                max = preds.get(key);
                category = key;
            }
        }
        return category;
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
        // System.out.println("C1, C2, C3 Precision: ");
        // System.out.println(prec.toString());
        // System.out.println();
        // System.out.println("C1, C2, C3 Recall: ");
        // System.out.println(rec.toString());
        // System.out.println();
        System.out.println("Precision: " + avg_prec);
        System.out.println("Recall: " + avg_rec);
        System.out.println("F-score: " + f1score);
    }
    
    public void calculateMetrics(){
        calculateConfusionMatrix();
        calculatePrecisionRecall();
    }
}
