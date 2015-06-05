package org.elasticsearch.indices.analysis;

import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;

/**
 * Created by yar on 5/6/15.
 */
public class WordnetHypernymIndicesAnalysis extends AbstractComponent {
    @Inject
    public WordnetHypernymIndicesAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService,
                                          HypernymTokenFilterFactoryProvider hypernymTokenFilterFactoryProvider) {
        super(settings);

        indicesAnalysisService.tokenFilterFactories().put("hypernym", new PreBuiltTokenFilterFactoryFactory(
                hypernymTokenFilterFactoryProvider.get()));
    }
}
