package me.yaraju.elasticsearch.analysis;

import me.yaraju.elasticsearch.index.analysis.HypernymFilter;
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
    private HypernymFilter filter;
    private Analyzer analyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
            Tokenizer source = new MockTokenizer(reader, MockTokenizer.WHITESPACE, true);
            Map<String, List<String>> hypernymsMap = new HashMap<>();
            hypernymsMap.put("father", Lists.newArrayList("parent"));
            hypernymsMap.put("mother", Lists.newArrayList("parent"));
            hypernymsMap.put("thing", Lists.newArrayList("state_of_affairs", "situation"));
            filter = new HypernymFilter(source, hypernymsMap);
            return new Analyzer.TokenStreamComponents(source, filter);
        }
    };

    @Test
    public void testPositionIncrementsSingleTerm() throws Exception {
        String output[] = {"father", "parent"};
        int posIncrements[] = {1, 0};

        assertAnalyzesTo(analyzer, "father", output, posIncrements);
    }

    @Test
    public void testPositionIncrementsNoMatch() throws Exception {
        String output[] = {"asdasdadasd"};
        int posIncrements[] = {1};

        assertAnalyzesTo(analyzer, "asdasdadasd", output, posIncrements);
    }

    @Test
    public void testPositionIncrementsAnotherSingleTerm() throws Exception {
        String output[] = {"thing", "state_of_affairs", "situation"};
        int posIncrements[] = {1, 0, 0};

        assertAnalyzesTo(analyzer, "thing", output, posIncrements);
    }

    public void testPositionIncrementsTwoTerm() throws Exception {

        String output[] = {"father", "parent", "thing", "state_of_affairs", "situation"};
        int posIncrements[] = {1, 0, 1, 0, 0};

        assertAnalyzesTo(analyzer, "father thing", output, posIncrements);
    }

    public void testPositionIncrementsFourTerms() throws Exception {

        String output[] = {
                "father", "parent",
                "mother", "parent",
                "thing", "state_of_affairs", "situation",
                "mother", "parent",};
        int posIncrements[] = {
                1, 0,
                1, 0,
                1, 0, 0,
                1, 0};

        assertAnalyzesTo(analyzer, "father mother thing mother", output, posIncrements);
    }

    public void testPositionOffsetsFourTerms() throws Exception {

        String output[] = {
                "father", "parent",
                "mother", "parent",
                "thing", "state_of_affairs", "situation",
                "mother", "parent",};
        int startOffsets[] = {
                0, 0,
                7, 7,
                14, 14, 14,
                20, 20};
        int endOffsets[] = {
                6, 6,
                13, 13,
                19, 19, 19,
                26, 26};

        assertAnalyzesTo(analyzer, "father mother thing mother", output, startOffsets, endOffsets);
    }
}