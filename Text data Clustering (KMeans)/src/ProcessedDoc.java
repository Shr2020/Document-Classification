import java.util.HashMap;
import java.util.Map;

public class ProcessedDoc {
    public String documentName;
    private Map<String, Integer> termFreq = new HashMap<>();
    private Map<String, Integer> ngramFreq = new HashMap<>();
    private Map<String, Integer> nerTermFreq = new HashMap<>();
    public Map<String, Integer> finalTermFreq = new HashMap<>();

    public Map<String, Integer> getTermFreq() {
      return termFreq;
    }
    public Map<String, Integer> getNgramFreq() {
      return ngramFreq;
    }
    public Map<String, Integer> getNerTermFreq() {
      return nerTermFreq;
    }

    public String getDocumentName() {
      return documentName;
    }
}
