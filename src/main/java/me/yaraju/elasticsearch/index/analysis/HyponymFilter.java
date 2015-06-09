package me.yaraju.elasticsearch.index.analysis;

import edu.mit.jwi.IDictionary;
import me.yaraju.wordnet.WordNetUtils;
import org.apache.lucene.analysis.TokenStream;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by yar on 8/6/15.
 */
public class HyponymFilter extends BaseWordnetTokenFilter {
    public HyponymFilter(TokenStream input, IDictionary wordnetDict) {
        super(input, wordnetDict, "hypernyms");
    }

    public HyponymFilter(TokenStream input, Map<String, List<String>> valuesMap) {
        super(input, valuesMap, "hypernyms");
    }

    @Override
    protected Collection<String> getWordnetValues(String term) {
        return WordNetUtils.getHyponyms(wordnetDict, term, "n", false);
    }
}
