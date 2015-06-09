package me.yaraju.elasticsearch.index.analysis;

import org.elasticsearch.index.analysis.AnalysisModule;

/**
 * Created by yar on 5/6/15.
 */
public class WordnetAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("hypernym", HypernymTokenFilterFactory.class);
        tokenFiltersBindings.processTokenFilter("hyponym", HyponymTokenFilterFactory.class);
    }
}
