package yashasvig.crawler.work.di;

import dagger.Module;
import dagger.Provides;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import yashasvig.crawler.work.WorkTracker;
import yashasvig.crawler.work.WorkTrackerImpl;
import yashasvig.crawler.work.di.qualifier.WorkerPool;

import javax.inject.Singleton;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Module
public class WorkModule {

    /**
     * Max no of concurrent workers that the factory can create. Each concurrent worker corresponds to a new thread
     * being spawn in the system.
     */
    private static final int NUM_OF_WORKERS = 10;

    @Provides
    @WorkerPool
    @Singleton
    static ThreadPoolExecutor provideWorkerPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUM_OF_WORKERS, NUM_OF_WORKERS, 2L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    @Provides
    @Singleton
    static Connection provideJsoupConnection() {
        return Jsoup.newSession().ignoreContentType(true).timeout(5000);
    }

    @Provides
    @Singleton
    static WorkTracker provideWorkTracker() {
        return new WorkTrackerImpl();
    }
}
