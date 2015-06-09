package me.yaraju.elasticsearch.index.analysis;

import edu.mit.jwi.IDictionary;
import me.yaraju.utils.MultimapFileReader;
import me.yaraju.wordnet.WordNetUtils;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.base.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yar on 9/6/15.
 */
public abstract class BaseWordnetTokenFilterFactory extends AbstractTokenFilterFactory {
    protected final Map<String, List<String>> valuesMap;
    protected IDictionary wordnetDict;

    public BaseWordnetTokenFilterFactory(Index index, Settings indexSettings, Environment env, String name, Settings settings, String wordnetType) {
        super(index, indexSettings, name, settings);
        Reader reader = null;
        if (settings.get(wordnetType + "_path") != null) {
            reader = Analysis.getReaderFromFile(env, settings, wordnetType + "_path");
        } else {
            throw new ElasticsearchIllegalArgumentException(wordnetType + " requires `" + wordnetType + "_path` to be configured");
        }
        try {
            String format = settings.get("format");
            if (Strings.isNullOrEmpty(format)) {
                logger.info("No " + wordnetType + " `format` specified. Assuming multimap.");
                format = "multimap";
            }
            if (("multimap").equalsIgnoreCase(format)) {
                valuesMap = new MultimapFileReader(new BufferedReader(reader)).readMultimap();
            } else if ("wordnet".equalsIgnoreCase(format)) {
                valuesMap = new HashMap<>();
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
            logger.info("loaded with " + valuesMap.size());
        } catch (Exception e) {
            logger.error("failed to build " + wordnetType);
            throw new ElasticsearchIllegalArgumentException("failed to build " + wordnetType, e);
        }
    }
}
