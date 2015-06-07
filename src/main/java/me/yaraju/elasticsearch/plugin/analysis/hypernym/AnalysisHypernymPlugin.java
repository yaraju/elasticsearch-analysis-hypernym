package me.yaraju.elasticsearch.plugin.analysis.hypernym;

import me.yaraju.elasticsearch.index.analysis.WordnetHypernymAnalysisBinderProcessor;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

/**
 * Created by yar on 5/6/15.
 */
public class AnalysisHypernymPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "hypernym";
    }

    @Override
    public String description() {
        return "Wordnet Hypernym token filter support";
    }

//    @Override
//    public Collection<Class<? extends Module>> modules() {
//        Collection<Class<? extends Module>> classes = new ArrayList<>();
//        classes.add(WordnetHypernymAnalysisModule.class);
//        return classes;
//    }

    /**
     * Automatically called with the analysis module.
     */
    public void onModule(AnalysisModule module) {
        module.addProcessor(new WordnetHypernymAnalysisBinderProcessor());
    }
}
