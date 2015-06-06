package org.elasticsearch.index.analysis;

/**
 * Created by yar on 5/6/15.
 */

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TokenFilter subclass which adds hypernyms to a TokenStream based on a map.
 */
public final class HypernymFilter extends TokenFilter {

    private static ESLogger logger;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);

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
        this.currentHypernyms = new ArrayList<>();
        this.currentHypernymIndex = -1;
        logger.info("Current hypernyms: " + hypernyms.keySet());
    }

    @Override
    public boolean incrementToken() throws IOException {
        if( save != null ) {
            // Keep the previous token's state. We still have hypernyms to inject into the stream
            restoreState(save);
        }
        if (currentHypernymIndex >= 0 && currentHypernymIndex < currentHypernyms.size()) {
            // We have hypernyms to add to the tokens stream
            char[] currentHypernym = currentHypernyms.get(currentHypernymIndex).toCharArray();
            posIncrAtt.setPositionIncrement(0);
            String str = new String(currentHypernym);
            termAtt.setEmpty().append(str);
            logger.info("Found hypernym: " + str);
            currentHypernymIndex++;
            return true;
        } else if (currentHypernymIndex >= 0 && currentHypernymIndex == currentHypernyms.size()) {
            // We're done adding hypernyms to the tokens stream
            currentHypernymIndex = -1;
            currentHypernyms = null;
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
            currentHypernyms = hypernyms.get(term);
            if (currentHypernyms != null) {
                // This token has hypernyms!
                logger.info("found matching hypernyms for term: "
                        + term + ": " + currentHypernyms.size());
                currentHypernymIndex = 0;
                // Save original state
                save = captureState();
            }
        }
        return true;
    }

    @Override
    public void reset() throws IOException {
        input.reset();
    }

    public Map<String, List<String>> getHypernyms() {
        return hypernyms;
    }
}
