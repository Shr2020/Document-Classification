import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.pipeline.*;

public class Preprocessor {

  public Preprocessor(Set<String> stopwords, int ngramFreqThreshold) {
    this.stopwords = stopwords;
    this.ngramFreqThreshold = ngramFreqThreshold;
  }

  private int ngramFreqThreshold;
  private Set<String> stopwords;
  private Set<String> totalTerms = new HashSet<>();

  private Map<String, Integer> globalNgrams = new HashMap<>();

  private void mergeMaps(Map<String, Integer> dest, Map<String, Integer> source) {
    if (dest.isEmpty() || source.isEmpty()) {
      dest.putAll(source);
      return;
    }

    for (Map.Entry<String, Integer> entry : source.entrySet()) {
      Integer n = dest.get(entry.getKey());
      if (n == null) {
        dest.put(entry.getKey(), entry.getValue());
      } else {
        dest.put(entry.getKey(), n + entry.getValue());
      }
    }
  }

  public Map<String, Integer> generateNGrams(List<String> input, int n) {
    Map<String, Integer> ngramFreq = new HashMap<>();
    for (int i = 0; i <= input.size() - n; ++i) {
      String ngram = "";
      for (int j = i; j < i + n; ++j) {
        ngram += input.get(j) + "_";
      }
      ngram = ngram.toLowerCase().substring(0, ngram.length() - 1);   // remove last"_"

      Integer count = ngramFreq.get(ngram);
      if (count == null) {
        ngramFreq.put(ngram, 1);
      } else {
        ngramFreq.put(ngram, count + 1);
      }
    }
    return ngramFreq;
  }

  public String removePunctuation(String input) {
    // https://docs.oracle.com/javase/9/docs/api/java/util/regex/Pattern.html
    return input.replaceAll("\\p{Punct}", "");
  }

  public String removeStopWords(String input) {
    ArrayList<String> words = Stream.of(input.split("\\s+"))
                                .collect(Collectors.toCollection(ArrayList<String>::new));
    words.removeIf(s -> this.stopwords.contains(s.toLowerCase()));

    return words.stream().collect(Collectors.joining(" "));
  }

  private Map<String, Integer> generateTermsUsingNER(Sentence sent) {
    Map<String, Integer> nerTermFreq = new HashMap<>();
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    // create an empty Annotation just with the given text
    CoreDocument document = new CoreDocument(sent.toString());
    // run all Annotators on this text
    pipeline.annotate(document);
    
    Annotation document2 = new Annotation(sent.toString());
    pipeline.annotate(document2);
    List<CoreMap> sentences = document2.get(CoreAnnotations.SentencesAnnotation.class);
    List<String> tokens = new ArrayList<>();
    for(CoreMap sentence: sentences) {
        // traversing the words in the current sentence
        // a CoreLabel is a CoreMap with additional token-specific methods
        tokens.addAll(getNerTerms(sentence));
    }

    for (String tok : tokens) {
      if (nerTermFreq.containsKey(tok)) {
        nerTermFreq.put(tok, nerTermFreq.get(tok) + 1);
      } else {
        nerTermFreq.put(tok, 1);
      }
    }    
    return nerTermFreq;
  }

  private List<String> getNerTerms(CoreMap sentence) {
    String prevNeToken = "O";
    String currNeToken = "O";
    boolean newToken = true;
    int term_length = 0;
    StringBuilder sb = new StringBuilder();
    List<String> tokens = new ArrayList<>();
    for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
      currNeToken = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
      String word = token.get(CoreAnnotations.TextAnnotation.class);
      
      // Strip out "O"s completely, makes code below easier to understand
      if (currNeToken.equals("O")) {
        if (!prevNeToken.equals("O") && (sb.length() > 0)) {
          handleEntity(sb, tokens, term_length);
          newToken = true;
          term_length = 0;
        }
        //tokens.add(word);
        continue;
      }

      if (newToken) {
        prevNeToken = currNeToken;
        newToken = false;
        sb.append(word.toLowerCase());
        term_length++;
        continue;
      }

      if (currNeToken.equals(prevNeToken)) {
        sb.append("_" + word.toLowerCase());
        term_length++;
      } else {
        // We're done with the current entity - print it out and reset
        // TODO save this token into an appropriate ADT to return for useful processing..
        handleEntity(sb, tokens, term_length);
        newToken = true;
        term_length = 0;
      }
      prevNeToken = currNeToken;
    }
    return tokens;
  }

  private void handleEntity(StringBuilder inSb, List<String> inTokens, int termLen) {
    if (termLen > 1) {
      inTokens.add(inSb.toString());
    }
    inSb.setLength(0);
  }

  public ProcessedDoc process(String docString) {
    ProcessedDoc processedDoc = new ProcessedDoc();
    docString = removeStopWords(docString);
    docString = removePunctuation(docString);
  
    Document doc = new Document(docString);
    for (Sentence sent : doc.sentences()) {
      for (String lemma : sent.lemmas()) {
        Integer count = processedDoc.getTermFreq().get(lemma.toLowerCase());
        if (count == null) {
          processedDoc.getTermFreq().put(lemma.toLowerCase(), 1);
        } else {
          processedDoc.getTermFreq().put(lemma.toLowerCase(), count + 1);
        }
      }
      Map<String, Integer> nerTermFreq = generateTermsUsingNER(sent);
      processedDoc.getNerTermFreq().putAll(nerTermFreq);
      mergeMaps(processedDoc.getTermFreq(), nerTermFreq);
      //processedDoc.getTermFreq().putAll(nerTermFreq);
      
      Map<String, Integer> singleDocNgrams = new HashMap<>();
      mergeMaps(singleDocNgrams, generateNGrams(sent.lemmas(), 2));
      mergeMaps(singleDocNgrams, generateNGrams(sent.lemmas(), 3));

      processedDoc.getNgramFreq().putAll(singleDocNgrams);

      // also add ngrams to global map
      mergeMaps(this.globalNgrams, singleDocNgrams);

      //add all normal terms and ner in a single set. ngrams will be added later
      totalTerms.addAll(processedDoc.getTermFreq().keySet());
    }

    return processedDoc;
  }

  public List<ProcessedDoc> run(List<String> dataset, List<String> names) throws IOException {

    List<ProcessedDoc> processedDocs = new ArrayList<>();
    for (String s : dataset) {
      processedDocs.add(process(s));
    }

    for (int i = 0; i < names.size(); i++) {
      processedDocs.get(i).documentName = names.get(i);
    }

    // now that we have global ngrams, use the global frequency to keep only the most frequent ngrams in each ProcessedDoc
    Set<String> frequentNgrams = new HashSet<>();
    for (Map.Entry<String, Integer> entry : this.globalNgrams.entrySet()) {
      if (entry.getValue() >= this.ngramFreqThreshold) {
        frequentNgrams.add(entry.getKey());
      }
    }

    for (ProcessedDoc pdoc : processedDocs) {
      pdoc.getNgramFreq().entrySet().removeIf(e -> !frequentNgrams.contains(e.getKey()));
      pdoc.finalTermFreq.putAll(pdoc.getTermFreq());
      mergeMaps(pdoc.finalTermFreq, pdoc.getNgramFreq());
      //System.out.println(pdoc.finalTermFreq);
    }

    totalTerms.addAll(frequentNgrams);
  
    return processedDocs;
  }

  public Set<String> getTotalTerms() {
    return totalTerms;
  }
}
