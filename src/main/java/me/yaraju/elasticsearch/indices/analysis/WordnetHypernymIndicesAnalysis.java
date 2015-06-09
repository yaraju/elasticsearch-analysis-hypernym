package me.yaraju.elasticsearch.indices.analysis;

import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

/**
 * Created by yar on 5/6/15.
 */
public class WordnetHypernymIndicesAnalysis extends AbstractComponent {
    @Inject
    public WordnetHypernymIndicesAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService,
                                          HypernymTokenFilterFactoryProvider hypernymTokenFilterFactoryProvider,
                                          HyponymTokenFilterFactoryProvider hyponymTokenFilterFactoryProvider) {
        super(settings);

        indicesAnalysisService.tokenFilterFactories().put("hypernym", new PreBuiltTokenFilterFactoryFactory(
                hypernymTokenFilterFactoryProvider.get()));
        indicesAnalysisService.tokenFilterFactories().put("hyponym", new PreBuiltTokenFilterFactoryFactory(
                hyponymTokenFilterFactoryProvider.get()));
    }
}
