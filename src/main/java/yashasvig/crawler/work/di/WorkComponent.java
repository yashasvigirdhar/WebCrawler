package yashasvig.crawler.work.di;

import dagger.Component;
import org.jsoup.Connection;
import yashasvig.crawler.work.WorkTracker;

import javax.inject.Singleton;
import java.util.concurrent.ThreadPoolExecutor;

@Component(modules = WorkModule.class)
@Singleton
public interface WorkComponent {

    ThreadPoolExecutor workerPool();

    Connection jsoupConnection();

    WorkTracker workTracker();
}
