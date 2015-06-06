package org.elasticsearch.index.analysis;

/**
 * Created by yar on 5/6/15.
 */

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * TokenFilter subclass which adds hypernyms to a TokenStream based on a map.
 */
public final class HypernymFilter extends TokenFilter {

    private static ESLogger logger;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private Map<String, List<String>> hypernyms;

    private int currentHypernymIndex;

    private List<String> currentHypernyms;
    private State save;

    /**
     * Constructor.
     *
     * @param input
     *          the input TokenStream
     * @param hypernyms
     *          the hypernym map. key: token text, value: list of hypernyms
     */
    public HypernymFilter(TokenStream input, Map<String, List<String>> hypernyms) {
        super(input);
        this.logger = Loggers.getLogger(getClass());
        this.hypernyms = hypernyms;
        this.currentHypernymIndex = -1;
        logger.warn("Current hypernyms: " + hypernyms.keySet());
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            logger.warn("No more tokens found");
            return false;
        }
        if (currentHypernymIndex >= 0 && currentHypernymIndex < currentHypernyms.size()) {
            logger.warn("More hypernyms for this token...");
            char[] currentHypernym = currentHypernyms.get(currentHypernymIndex).toCharArray();
            termAtt.copyBuffer(currentHypernym, 0, currentHypernym.length);
            currentHypernymIndex++;
            // Save original state
            posIncrAtt.setPositionIncrement(0);
            save = captureState();
            return true;
        } else if (currentHypernymIndex >= 0 && currentHypernymIndex == currentHypernyms.size()) {
            logger.warn("No more hypernyms for this token.");
            currentHypernymIndex = -1;
            currentHypernyms = null;
            // Save original state
            posIncrAtt.setPositionIncrement(1);
            save = captureState();
            return true;
        }
        if (termAtt.length() > 0) {
            char [] buffer = termAtt.buffer();
            String term = new String(buffer, 0, termAtt.length());
            logger.warn("Processing term: " + term);
            currentHypernyms = hypernyms.get(term);
            if (currentHypernyms != null) {
                logger.warn("found matching hypernyms for term: "
                        + term + ": " + currentHypernyms.size());
                currentHypernymIndex = 0;
            }
        }
        return true;
    }

    @Override
    public void reset() throws IOException {
        currentHypernymIndex = -1;
        if (currentHypernyms != null)
            currentHypernyms = null;

        input.reset();
    }

    public Map<String, List<String>> getHypernyms() {
        return hypernyms;
    }
}
