package me.yaraju.elasticsearch.index.analysis;

import edu.mit.jwi.IDictionary;
import me.yaraju.wordnet.WordNetUtils;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.base.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.index.analysis.AnalysisSettingsRequired;
import org.elasticsearch.index.settings.IndexSettings;
import me.yaraju.utils.MultimapFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yar on 5/6/15.
 */
@AnalysisSettingsRequired
public class HypernymTokenFilterFactory extends org.elasticsearch.index.analysis.AbstractTokenFilterFactory {
    private IDictionary wordnetDict;
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
            String format = settings.get("format");
            if (Strings.isNullOrEmpty(format)) {
                logger.info("No hypernyms `format` specified. Assuming multimap.");
                format = "multimap";
            }
            if (("multimap").equalsIgnoreCase(format)) {
                hypernyms = new MultimapFileReader(new BufferedReader(reader)).readMultimap();
            } else if ("wordnet".equalsIgnoreCase(format)) {
                hypernyms = new HashMap<>();
                try {
                    wordnetDict = WordNetUtils.getDictionary();
                } catch (IOException e) {
                    throw new ElasticsearchIllegalArgumentException(
                            "WordNet dictionary not found. Please install WordNet at /usr/share/wordnet");
                }
            } else {
                throw new ElasticsearchIllegalArgumentException(
                        "Invalid format specified: " + format +
                        ". Please choose either multimap or wordnet.");
            }
            logger.info("loaded with " + hypernyms.size());
        } catch (Exception e) {
            logger.error("failed to build hypernyms");
            throw new ElasticsearchIllegalArgumentException("failed to build hypernyms", e);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (wordnetDict != null) {
            return new HypernymFilter(tokenStream, wordnetDict);
        } else if (hypernyms != null) {
            return new HypernymFilter(tokenStream, hypernyms);
        }
        return tokenStream;
    }
}
