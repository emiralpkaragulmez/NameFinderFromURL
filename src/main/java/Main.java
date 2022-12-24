import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws IOException {

        //Create sentence detector, tokenizer and name finder using with models
        InputStream sentenceModelIn = Main.class.getResourceAsStream("opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin");
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(new SentenceModel(sentenceModelIn));

        InputStream tokenizerModelIn = Main.class.getResourceAsStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
        Tokenizer tokenizer = new TokenizerME(new TokenizerModel(tokenizerModelIn));

        InputStream tokenModelIn = Main.class.getResourceAsStream("en-ner-person.bin");
        NameFinderME nameFinder = new NameFinderME(new TokenNameFinderModel(tokenModelIn));

        //Copy text of body from website of given url and put in string
        Document doc = Jsoup.connect(args[0]).get();
        String body = doc.body().text();

        //Declare sentences of body's text
        String[] sentences = sentenceDetector.sentDetect(body);

        //Tokenize every word and collect in array of tokens
        ArrayList<String> arrayOfTokens = new ArrayList<>();
        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence);
            Collections.addAll(arrayOfTokens, tokens);
        }


        //Turn array list into array of string because name finder only accept array
        String[] tokens = new String[arrayOfTokens.size()];
        for (int i = 0; i < arrayOfTokens.size(); i++) {
            tokens[i] = arrayOfTokens.get(i);
        }

        //Find spans of names and put references into nameSpans
        Span[] nameSpans = nameFinder.find(tokens);

        //Detokenize names from tokens array into array of 'names' using name spans
        String[] names = new String[nameSpans.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = "";
            for (int k = nameSpans[i].getStart(); k < nameSpans[i].getEnd(); k++) {
                names[i] = names[i] + tokens[k];
            }
        }

        //Print names
        for (String name : names) {
            System.out.println(name);
        }
    }
}

