import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.stream.Collectors;


public class Driver {

    public static void main(String[] args) throws IOException {
        Set<String> stopwords = new HashSet<>();
        stopwords.addAll(FileUtils.readAllLines("stopwords.txt"));
        List<String> docNames = new ArrayList<String>();
        List<String> dataset = new ArrayList<>();
        List<String> testset = new ArrayList<>();
        List<String> testDocNames = new ArrayList<String>();
        
        //trainset
        for (String filename : FileUtils.readAllLines("data.txt")) {
          System.out.println(filename);
          String path = filename.replaceAll("\\.", "/");
          String[] p = path.split("/");
          String docName = p[2] + "/" + p[3];
          docNames.add(docName);
          dataset.add(FileUtils.readFile(filename));
        }

        // testset
        for (String filename : FileUtils.readAllLines("testData.txt")) {
            System.out.println(filename);
            String path = filename.replaceAll("\\.", "/");
            String[] p = path.split("/");
            String docName = p[2] + "/" + p[3];
            testDocNames.add(docName);
            testset.add(FileUtils.readFile(filename));
        }
        
        // process the training docs
        Preprocessor pp = new Preprocessor(stopwords, 3);
        List<ProcessedDoc> processedDocs = pp.run(dataset, docNames);
        
        // get total terms in the dataset
        List<String> totalTerms = new ArrayList<String>(pp.getTotalTerms());
        System.out.println(totalTerms.size());

        // calculate doc_matrix and tf_idf matrix
        Frequency fr = new Frequency();
        fr.create_document_term_matrix(processedDocs, docNames.size(), totalTerms.size(), totalTerms);
        double[][] train_tf_idf = fr.get_tf_idf_matrix(processedDocs, docNames.size(), totalTerms.size(), totalTerms);

        // process the test docs
        Preprocessor testPP = new Preprocessor(stopwords, 3);
        List<ProcessedDoc> processedTestDocs = testPP.run(testset, testDocNames);
    
        //create tfidf for each test documenet
        List<double[][]> tfidfList = new ArrayList<>();
        for (int i = 0; i < processedTestDocs.size(); i++) {
            Frequency fr_tmp = new Frequency();
            List<ProcessedDoc> processedDocsTrainTest = new ArrayList<>(processedDocs);
            processedDocsTrainTest.add(processedTestDocs.get(i));
            fr_tmp.create_document_term_matrix(processedDocsTrainTest, processedDocsTrainTest.size(), totalTerms.size(), totalTerms);
            double[][] test_tf_idf = fr_tmp.get_tf_idf_matrix(processedDocsTrainTest, processedDocsTrainTest.size(), totalTerms.size(), totalTerms);
            tfidfList.add(test_tf_idf);
        }

        // create map of category with document number
        Map<Integer, String> docToCategoryMap = new HashMap<>();
        for (int i = 0; i < docNames.size(); i++) {
            String[] p = docNames.get(i).split("/");
            docToCategoryMap.put(i, p[0]);
        }
        //System.out.println(docToCategoryMap);

        Map<String, String> category_labels_map  = new HashMap<String, String>();
        Map<String, Integer> category_doc_start_index_map  = new HashMap<String, Integer>();
        Map<String, Integer> category_doc_stop_index_map  = new HashMap<String, Integer>();
        
        // find document indexes for each category
        String prev_category = "";
        for (int i = 0; i < docNames.size(); i++) {
            String category = docToCategoryMap.get(i);
            if (!prev_category.equals(category)) {
                System.out.println(prev_category + " : " + category);
                category_doc_start_index_map.put(category, i);
                if (prev_category != "") {
                    category_doc_stop_index_map.put(prev_category, i-1);
                }
                prev_category = category;
            }

            if (i == docNames.size() - 1) {
                category_doc_stop_index_map.put(category, i);
            }
        }

        //System.out.println(category_doc_start_index_map);
        //System.out.println(category_doc_stop_index_map);

        // create labels for different categories
        for (String category : new HashSet<String>(docToCategoryMap.values())) {
            if (category.equals("C1")) {
                category_labels_map.put("C1", "Airline Safety");
            }
            else if (category.equals("C4")) {
                category_labels_map.put("C4", "Hoof and Mouth Disease");
            }
            else if (category.equals("C7")) {
                category_labels_map.put("C7", "Mortgage Rates");
            }
            else {
                String keyword = fr.calculate_keywords(category, category_doc_start_index_map.get(category), category_doc_stop_index_map.get(category), totalTerms);
                category_labels_map.put(category, keyword);
            }
        }

        SortedSet<String> categoriesSet = new TreeSet<String>(category_labels_map.keySet());
        List<String> categories = categoriesSet.stream().collect(Collectors.toList());
        //System.out.println(category_labels_map);

        int num_neibhors = 3;

        //KNN
        List<HashMap<String, Integer>> result_list = new ArrayList<>();
        KNN knn = new KNN(train_tf_idf, docToCategoryMap, category_labels_map);
        System.out.println();
        for (int i = 0; i < tfidfList.size(); i++) {
            System.out.println("Document no.: " +  (i + 1));
            double[][] matrix = tfidfList.get(i);
            double[] test_row= matrix[matrix.length-1];
            HashMap<String, Integer> result = knn.knn_process(test_row, num_neibhors, docNames, testDocNames.get(i));
            result_list.add(result);
        }

        Metrics met = new Metrics(result_list, categories);
        met.calculateMetrics();
    }
}
