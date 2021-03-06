package me.yaraju.elasticsearch.index.analysis;

/**
 * Created by yar on 5/6/15.
 */

import edu.mit.jwi.IDictionary;
import me.yaraju.wordnet.WordNetUtils;
import org.apache.lucene.analysis.TokenStream;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * TokenFilter subclass which adds hypernyms to a TokenStream based on a map.
 */
public final class HypernymFilter extends BaseWordnetTokenFilter {

    public HypernymFilter(TokenStream input, IDictionary wordnetDict) {
        super(input, wordnetDict, "hypernyms");
    }

    public HypernymFilter(TokenStream input, Map<String, List<String>> valuesMap) {
        super(input, valuesMap, "hypernyms");
    }

    @Override
    protected Collection<String> getWordnetValues(String term) {
        return WordNetUtils.getHypernyms(wordnetDict, term, "n", false);
    }
}
