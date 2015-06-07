package me.yaraju.elasticsearch.indices.analysis;

import org.elasticsearch.common.inject.AbstractModule;

/**
 * Created by yar on 5/6/15.
 */
public class WordnetHypernymAnalysisModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WordnetHypernymIndicesAnalysis.class).asEagerSingleton();
    }
}
