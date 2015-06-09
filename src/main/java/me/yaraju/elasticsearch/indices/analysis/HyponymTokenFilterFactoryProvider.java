package me.yaraju.elasticsearch.indices.analysis;

import me.yaraju.elasticsearch.index.analysis.HyponymTokenFilterFactory;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Provider;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * Created by yar on 5/6/15.
 */
public class HyponymTokenFilterFactoryProvider implements Provider<HyponymTokenFilterFactory> {

    private final Index index;
    private final Settings indexSettings;
    private final String name;
    private final Settings settings;
    private final Environment env;

    @Inject
    public HyponymTokenFilterFactoryProvider(Index index, @IndexSettings Settings indexSettings,
                                             Environment env, @Assisted String name, @Assisted Settings settings) {
        this.index = index;
        this.indexSettings = indexSettings;
        this.env = env;
        this.name = name;
        this.settings = settings;
    }

    @Override
    public HyponymTokenFilterFactory get() {
        return new HyponymTokenFilterFactory(index, indexSettings, env, name, settings);
    }
}
