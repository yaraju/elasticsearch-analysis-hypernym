package org.elasticsearch.index.analysis;

/**
 * Created by yar on 5/6/15.
 */

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * TokenFilter subclass which adds hypernyms to a TokenStream based on a map.
 */
public class HypernymFilter extends TokenFilter {

    private static Logger logger = Logger.getLogger(HypernymFilter.class);
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private Map<String, List<String>> hypernyms;

    private int currentHypernymIndex;

    private List<String> currentHypernyms;

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
        this.hypernyms = hypernyms;
        this.currentHypernymIndex = -1;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) return false;
        if (currentHypernymIndex >= 0 && currentHypernymIndex < currentHypernyms.size()) {
            char[] currentHypernym = currentHypernyms.get(currentHypernymIndex).toCharArray();
            termAtt.copyBuffer(currentHypernym, 0, currentHypernym.length);
            currentHypernymIndex++;
            posIncrAtt.setPositionIncrement(0);
            return true;
        } else if (currentHypernymIndex >= 0 && currentHypernymIndex == currentHypernyms.size()) {
            currentHypernymIndex = -1;
            currentHypernyms = null;
            posIncrAtt.setPositionIncrement(1);
            return true;
        }
        char [] buffer = termAtt.buffer();
        if (buffer.length > 0) {
            currentHypernyms = hypernyms.get(String.valueOf(buffer));
            if (currentHypernyms != null) {
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
