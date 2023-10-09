package yashasvig.crawler.work.di;

import dagger.Component;
import org.jsoup.Connection;

import javax.inject.Singleton;
import java.util.concurrent.ThreadPoolExecutor;

@Component(modules = WorkModule.class)
@Singleton
public interface WorkComponent {

    ThreadPoolExecutor workerPool();

    Connection jsoupConnection();
}
