package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.collect.Lists;
import org.junit.Test;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HypernymFilterTest extends BaseTokenStreamTestCase {
    private Analyzer analyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
            Tokenizer source = new MockTokenizer(reader, MockTokenizer.WHITESPACE, true);
            Map<String, List<String>> hypernymsMap = new HashMap<>();
            hypernymsMap.put("father", Lists.newArrayList("parent"));
            hypernymsMap.put("mother", Lists.newArrayList("parent"));
            hypernymsMap.put("thing", Lists.newArrayList("physical_entity"));
            return new Analyzer.TokenStreamComponents(source, new HypernymFilter(source, hypernymsMap));
        }
    };

    @Test
    public void testPositionIncrementsSingleTerm() throws Exception {
        String output[] = {"father", "parent"};
        // the position increment for the first term must be one in this case and of the second must be 0,
        // because the second term is stored in the same position in the token filter stream
        int posIncrements[] = {1, 0};
        // this is dummy stuff, but the test does not run without it
        int posLengths[] = {1, 1};

        assertAnalyzesToPositions(analyzer, "father", output, posIncrements, posLengths);

    }
}