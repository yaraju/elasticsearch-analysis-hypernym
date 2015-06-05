package org.elasticsearch.index.analysis;

/**
 * Created by yar on 5/6/15.
 */
public class WordnetHypernymAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("hypernym", HypernymTokenFilterFactory.class);
    }
}
