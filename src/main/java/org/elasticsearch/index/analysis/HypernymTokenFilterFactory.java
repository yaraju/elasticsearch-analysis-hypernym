package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.elasticsearch.utils.MultimapFileReader;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yar on 5/6/15.
 */
@AnalysisSettingsRequired
public class HypernymTokenFilterFactory extends org.elasticsearch.index.analysis.AbstractTokenFilterFactory {
    private Properties properties;
    private final Map<String, List<String>> hypernyms;

    @Inject
    public HypernymTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        Reader reader = null;
        properties = new Properties();
        if (settings.get("hypernyms_path") != null) {
            reader = Analysis.getReaderFromFile(env, settings, "hypernyms_path");
        } else {
            throw new ElasticsearchIllegalArgumentException("hypernym requires `hypernyms_path` to be configured");
        }
        try {
            hypernyms = new MultimapFileReader(new BufferedReader(reader)).readMultimap();
            logger.warn("loaded with " + hypernyms.size());
        } catch (Exception e) {
            logger.error("failed to build hypernyms");
            throw new ElasticsearchIllegalArgumentException("failed to build hypernyms", e);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HypernymFilter(tokenStream, hypernyms);
    }
}
