package yashasvig.crawler.di;

import dagger.Component;
import yashasvig.crawler.centre.di.CentreModule;
import yashasvig.crawler.client.CrawlingClient;
import yashasvig.crawler.postprocessing.di.PostProducersModule;
import yashasvig.crawler.work.di.WorkModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PostProducersModule.class, WorkModule.class, CentreModule.class})
public interface GlobalComponent {

    void inject(CrawlingClient crawlingClient);
}
