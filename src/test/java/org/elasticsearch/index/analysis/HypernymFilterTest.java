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
            hypernymsMap.put("thing", Lists.newArrayList("situation", "state_of_affairs"));
            return new Analyzer.TokenStreamComponents(source, new HypernymFilter(source, hypernymsMap));
        }
    };

    @Test
    public void testPositionIncrementsSingleTerm() throws Exception {
        String output[] = {"father", "parent"};
        // the position increment for the first term must be one in this case and of the second must be 0,
        // because the second term is stored in the same position in the token filter stream
        int posIncrements[] = {1, 0};

        assertAnalyzesTo(analyzer, "father", output, posIncrements);
    }

    @Test
    public void testPositionIncrementsAnotherSingleTerm() throws Exception {
        String output[] = {"thing", "situation", "state_of_affairs"};
        // the position increment for the first term must be one in this case and of the second must be 0,
        // because the second term is stored in the same position in the token filter stream
        int posIncrements[] = {1, 0, 0};

        assertAnalyzesTo(analyzer, "thing", output, posIncrements);
    }

    public void testPositionIncrementsTwoTerm() throws Exception {

        String output[] = {"father", "parent", "thing", "situation", "state_of_affairs"};
        // the position increment for the first term must be one in this case and of the second must be 0,
        // because the second term is stored in the same position in the token filter stream
        int posIncrements[] = {1, 0, 1, 0, 0};

        assertAnalyzesTo(analyzer, "father thing", output, posIncrements);
    }

    public void testPositionIncrementsFourTerms() throws Exception {

        String output[] = {
                "father", "parent",
                "mother", "parent",
                "thing", "situation", "state_of_affairs",
                "mother", "parent",};
        // the position increment for the first term must be one in this case and of the second must be 0,
        // because the second term is stored in the same position in the token filter stream
        int posIncrements[] = {
                1, 0,
                1, 0,
                1, 0, 0,
                1, 0};
        // this is dummy stuff, but the test does not run without it

        // position increments are following the 1-0 pattern, because for each next term we insert a new term into
        // the same position (i.e. position increment is 0)
        assertAnalyzesTo(analyzer, "father mother thing mother", output, posIncrements);
    }

    public void testPositionOffsetsFourTerms() throws Exception {

        String output[] = {
                "father", "parent",
                "mother", "parent",
                "thing", "situation", "state_of_affairs",
                "mother", "parent",};
        int startOffsets[] = {
                0, 0,
                7, 7,
                14, 14,
                20, 20};
        int endOffsets[] = {
                6, 6,
                13, 13,
                19, 19,
                26, 26};

        assertAnalyzesTo(analyzer, "father mother thing mother", output, startOffsets, endOffsets);
    }
}