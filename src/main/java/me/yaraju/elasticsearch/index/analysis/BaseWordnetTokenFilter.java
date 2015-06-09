package me.yaraju.elasticsearch.index.analysis;

import edu.mit.jwi.IDictionary;
import me.yaraju.wordnet.WordNetUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by yar on 8/6/15.
 */
public abstract class BaseWordnetTokenFilter extends TokenFilter {
    protected static ESLogger logger;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    protected Map<String, List<String>> valuesMap;
    protected int currentMatchIndex;
    protected List<String> currentMatches;
    protected IDictionary wordnetDict;
    protected String wordnetType;
    private State save;

    public BaseWordnetTokenFilter(TokenStream input, IDictionary wordnetDict, String wordnetType) {
        this(input);
        this.wordnetType = wordnetType;
        this.wordnetDict = wordnetDict;
    }

    /**
     * Constructor.
     *
     * @param input
     *          the input TokenStream
     * @param valuesMap
     *          the values map. key: token text, value: list of values to return
     * @param wordnetType
     *          the type of filter (e.g. hypernym/hyponym)
     */
    public BaseWordnetTokenFilter(TokenStream input, Map<String, List<String>> valuesMap, String wordnetType) {
        this(input);
        this.logger = Loggers.getLogger(getClass());
        this.wordnetType = wordnetType;
        this.valuesMap = valuesMap;
        if (valuesMap != null) {
            logger.info("Current " + wordnetType + ": " + valuesMap.keySet());
        }
    }

    private BaseWordnetTokenFilter(TokenStream input) {
        super(input);
        this.logger = Loggers.getLogger(getClass());
        this.currentMatchIndex = -1;
        this.currentMatches = new ArrayList<>();
    }

    @Override
    public boolean incrementToken() throws IOException {
        if( save != null ) {
            // Keep the previous token's state. We still have valuesMap to inject into the stream
            restoreState(save);
        }
        if (currentMatchIndex >= 0 && currentMatchIndex < currentMatches.size()) {
            // We have valuesMap to add to the tokens stream
            char[] currentOutputToken = currentMatches.get(currentMatchIndex).toCharArray();
            posIncrAtt.setPositionIncrement(0);
            String str = new String(currentOutputToken);
            termAtt.setEmpty().append(str);
            logger.info("Found " + wordnetType + ": " + str);
            currentMatchIndex++;
            return true;
        } else if (currentMatchIndex >= 0 && currentMatchIndex == currentMatches.size()) {
            // We're done adding matches to the tokens stream
            currentMatchIndex = -1;
            currentMatches.clear();
            save = null;
            clearAttributes();
        }
        if (!input.incrementToken()) {
            // Do we have another token?
            return false;
        }
        if (termAtt.length() > 0) {
            // We have another non-empty token
            char [] buffer = termAtt.buffer();
            String term = new String(buffer, 0, termAtt.length());
            logger.info("Processing term: " + term);
            populateWordnetValues(term);
            if (currentMatches != null && currentMatches.size() > 0) {
                // This token has matches!
                logger.info("found matching " + wordnetType + "s for term: "
                        + term + ": " + currentMatches.size());
                currentMatchIndex = 0;
                // Save original state
                save = captureState();
            }
        }
        return true;
    }

    private void populateWordnetValues(String term) {
        if (valuesMap != null) {
            if (valuesMap.get(term) != null) {
                currentMatches.addAll(valuesMap.get(term));
            }
        } else {
            if (wordnetDict != null) {
                Collection<String> wordnetValues = getWordnetValues(term);
                currentMatches.addAll(wordnetValues);
            }
        }
    }

    protected abstract Collection<String> getWordnetValues(String term);

    @Override
    public void reset() throws IOException {
        input.reset();
    }

    public Map<String, List<String>> getValuesMap() {
        return valuesMap;
    }
}
