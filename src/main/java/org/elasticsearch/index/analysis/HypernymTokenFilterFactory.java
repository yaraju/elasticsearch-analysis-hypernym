package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.uima.lucas.indexer.analysis.HypernymFilterFactory;
import org.apache.uima.lucas.indexer.util.MultimapFileReaderFactory;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by yar on 5/6/15.
 */
public class HypernymTokenFilterFactory extends org.elasticsearch.index.analysis.AbstractTokenFilterFactory {
    private final HypernymFilterFactory filterFactoryDelegate;
    private Properties properties;

    @Inject
    public HypernymTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        filterFactoryDelegate = new HypernymFilterFactory(new MultimapFileReaderFactory());
        if (settings.get("hypernyms_path") != null) {
            properties.put(HypernymFilterFactory.FILE_PATH_PARAMETER, indexSettings.get("hypernyms_path"));
        } else {
            throw new ElasticsearchIllegalArgumentException("hypernym requires `hypernyms_path` to be configured");
        }

    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        try {
            return filterFactoryDelegate.createTokenFilter(tokenStream, properties);
        } catch (IOException e) {
            return tokenStream;
        }
    }
}
