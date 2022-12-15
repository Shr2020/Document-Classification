import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Driver {

  public static void main(String[] args) throws IOException {
    Set<String> stopwords = new HashSet<>();
    stopwords.addAll(FileUtils.readAllLines("stopwords.txt"));
    List<String> docNames = new ArrayList<String>();
    List<String> dataset = new ArrayList<>();

    for (String filename : FileUtils.readAllLines("data.txt")) {
      System.out.println(filename);
      String path = filename.replaceAll("\\.", "/");
      String[] p = path.split("/");
      String docName = p[2] + "/" + p[3];
      docNames.add(docName);
      dataset.add(FileUtils.readString(filename));
    }
    
    // process the docs
    Preprocessor pp = new Preprocessor(stopwords, 3);
    List<ProcessedDoc> processedDocs = pp.run(dataset, docNames);
    
    System.out.println(pp.getTotalTerms().size());
    

    // get total terms in the dataset
    List<String> totalTerms = new ArrayList<String>(pp.getTotalTerms());
    System.out.println(totalTerms.size());
    // calculate doc_matrix and tf_idf matrix
    Frequency fr = new Frequency();
    fr.create_document_term_matrix(processedDocs, docNames.size(), totalTerms.size(), totalTerms);
    double[][] tf_idf = fr.get_tf_idf_matrix(processedDocs, docNames.size(), totalTerms.size(), totalTerms);
    
    // Calculate Keywords
    fr.calculate_keywords(3, totalTerms);
    
    //calculate kmeans
    Kmeans km;
    Metrics met;
    Map<String, List<Integer>> clusteredDocs1;
    // KMEANS: COSINE SIMILARITY
    System.out.println("**************KMEANS WITH COSINE SIMILARITY************************");
    String mode = "cos";
    int numClusters = 3;
    km = new Kmeans(docNames, tf_idf, mode, numClusters);
    km.initialize_clusters();
    clusteredDocs1 = km.runKmeans();
    met = new Metrics(clusteredDocs1);
    met.calculateMetrics();
    System.out.println();

    // KMEANS: EUC SIMILARITY
    System.out.println("***************KMEANS WITH EUC SIMILARITY******************");
    mode = "euc";
    numClusters = 3;
    km = new Kmeans(docNames, tf_idf, mode, numClusters);
    km.initialize_clusters();
    clusteredDocs1 = km.runKmeans();
    met = new Metrics(clusteredDocs1);
    met.calculateMetrics();
    System.out.println();

    //calculate kmeans++
    Kmeanspp kmpp;
    Map<String, List<Integer>> clusteredDocs2;

    // KMEANS++: COSINE SIMILARITY
    System.out.println("**************KMEANS++ WITH COSINE SIMILARITY******************");
    mode = "cos";
    numClusters = 3;
    kmpp = new Kmeanspp(docNames, tf_idf, mode, numClusters);
    clusteredDocs2 = kmpp.runKmeanspp();
    met = new Metrics(clusteredDocs2);
    met.calculateMetrics();
    System.out.println();

    // KMEANS: EUC SIMILARITY
    System.out.println("*****************KMEANS++ WITH EUC SIMILARITY*******************");
    mode = "euc";
    numClusters = 3;
    kmpp = new Kmeanspp(docNames, tf_idf, mode, numClusters);
    clusteredDocs2 = kmpp.runKmeanspp();
    met = new Metrics(clusteredDocs2);
    met.calculateMetrics();
  }
}
