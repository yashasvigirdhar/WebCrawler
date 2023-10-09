package yashasvig.crawler.di;

import dagger.Component;
import yashasvig.crawler.client.CrawlingClient;
import yashasvig.crawler.postprocessing.di.PostProducersModule;
import yashasvig.crawler.work.di.WorkModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PostProducersModule.class, WorkModule.class})
public interface GlobalComponent {

    void inject(CrawlingClient crawlingClient);
}
