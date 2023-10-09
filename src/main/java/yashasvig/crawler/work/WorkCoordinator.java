package yashasvig.crawler.work;

import org.jsoup.Connection;
import yashasvig.crawler.models.Page;
import yashasvig.crawler.work.di.qualifier.WorkerPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides API to schedule the crawling of a new {@link Page}. This class also provides way to be invoked when the
 * crawling finishes through {@link WorkCallback}.
 */
@Singleton
public class WorkCoordinator {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final Connection connection;
    private UrlFilter filter;
    private WorkCallback pageProcessingFinishCallback;
    private final ThreadPoolExecutor workerPool;
    private final WorkTracker workTracker;
    private final Set<String> visitedUrls;
    private final AwaitingFinishThread awaitingFinishThread;


    @Inject
    WorkCoordinator(WorkTracker workTracker, @WorkerPool ThreadPoolExecutor workerPool, Connection connection) {
        this.visitedUrls = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.workTracker = workTracker;
        this.workerPool = workerPool;
        this.connection = connection;
        awaitingFinishThread = new AwaitingFinishThread();
    }

    /**
     * @param callback an instance of {@link WorkCallback} which would be invoked
     *                 after crawling for a page finishes
     */
    public void setCallback(WorkCallback callback) {
        this.pageProcessingFinishCallback = new WorkCallbackDelegate(callback);
    }

    /**
     * Submits a new {@code Page} to be crawled by the system.
     *
     * <p>Note that this would crawl all the nested pages on the passed page as well.</p>
     *
     * <p>Note that the request would be picked up a shared pool of workers and is not guaranteed to be executed
     * immediately. This method returns immediately after submitting the request.</p>
     */
    public void crawlDomain(URI url) {
        if (awaitingFinishThread.isAlive()) {
            throw new IllegalArgumentException("Only 1 crawling allowed at a time");
        }

        filter = new UrlFilter(url.getAuthority());
        workTracker.trackNewPage();
        awaitingFinishThread.start();

        workerPool.submit(new Worker(connection.newRequest(), filter, pageProcessingFinishCallback, url));
    }

    private void scheduleUrlIfRequired(URI uri) {
        if (visitedUrls.add(uri.toString())) {
            try {
                URL ignored = uri.toURL();
                workTracker.trackNewPage();
                workerPool.submit(new Worker(connection.newRequest(), filter, pageProcessingFinishCallback, uri));
            } catch (MalformedURLException e) {
                logger.log(Level.INFO, String.format("Can't schedule %s for crawling", uri), e);
            }
        }
    }

    private class WorkCallbackDelegate implements WorkCallback {
        private final WorkCallback delegate;

        public WorkCallbackDelegate(WorkCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onFinishedPageSuccessfully(Page page) {
            System.out.printf("Total Queued: %s, Active threads: %s\n", workerPool.getTaskCount(),
                    workerPool.getActiveCount());
            page.getChildUrls().forEach(WorkCoordinator.this::scheduleUrlIfRequired);
            workTracker.finishedPage();
            delegate.onFinishedPageSuccessfully(page);
        }

        @Override
        public void onError(String url, String errorMessage) {
            workTracker.finishedPage();
            delegate.onError(url, errorMessage);
        }

        @Override
        public void onFinishedCrawling() {
            delegate.onFinishedCrawling();
        }
    }

    private class AwaitingFinishThread extends Thread {
        @Override
        public void run() {
            workTracker.waitForFinish();
            pageProcessingFinishCallback.onFinishedCrawling();
        }
    }
}
