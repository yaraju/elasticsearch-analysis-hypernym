package me.yaraju.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AnalysisSettingsRequired;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * Created by yar on 5/6/15.
 */
@AnalysisSettingsRequired
public class HypernymTokenFilterFactory extends BaseWordnetTokenFilterFactory {

    @Inject
    public HypernymTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, Environment env,
                                      @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, env, name, settings, "hypernyms");
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (wordnetDict != null) {
            return new HypernymFilter(tokenStream, wordnetDict);
        } else if (valuesMap != null) {
            return new HypernymFilter(tokenStream, valuesMap);
        }
        return tokenStream;
    }
}
