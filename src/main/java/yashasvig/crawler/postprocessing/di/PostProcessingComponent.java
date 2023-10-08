package yashasvig.crawler.postprocessing.di;

import dagger.Component;
import dagger.Lazy;
import yashasvig.crawler.postprocessing.PostProcessor;

import java.util.Set;

@Component(modules = PostProducersDaggerModule.class)
public interface PostProcessingComponent {

    Lazy<Set<PostProcessor>> postProcessors();
}
