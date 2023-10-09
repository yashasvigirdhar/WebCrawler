package yashasvig.crawler.centre.di;

import dagger.Module;
import dagger.Provides;
import yashasvig.crawler.centre.di.qualifiers.PostProcessingPool;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Module
public class CentreModule {

    @Provides
    @PostProcessingPool
    @Singleton
    static ExecutorService providePostProcessingPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 2L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}
