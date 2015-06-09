package me.yaraju.elasticsearch.plugin.analysis.hypernym;

import me.yaraju.elasticsearch.index.analysis.WordnetAnalysisBinderProcessor;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

/**
 * Created by yar on 5/6/15.
 */
public class AnalysisWordnetPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "analysis-wordnet";
    }

    @Override
    public String description() {
        return "Wordnet extra token filters support";
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
        module.addProcessor(new WordnetAnalysisBinderProcessor());
    }
}
