package yashasvig.crawler.work;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import yashasvig.crawler.models.Page;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides API to schedule the crawling of a new {@link Page}. This class also provides way to be invoked when the
 * crawling finishes through {@link WorkCallback}.
 */
public final class WorkCoordinator {

    /**
     * Max no of concurrent workers that the factory can create. Each concurrent worker corresponds to a new thread
     * being spawn in the system.
     */
    private static final int NUM_OF_WORKERS = 10;

    private final Connection connection;
    private UrlFilter filter;
    private final WorkCallback pageProcessingFinishCallback;
    private final ThreadPoolExecutor workerPool;
    private final WorkTracker workTracker;
    private final Set<String> visitedUrls;
    private final AwaitingFinishThread awaitingFinishThread;

    /**
     * @param callback an instance of {@link WorkCallback} which would be invoked
     *                 after crawling for a page finishes
     */
    public WorkCoordinator(WorkCallback callback) {
        this.pageProcessingFinishCallback = new WorkCallbackDelegate(callback);
        this.workTracker = new WorkTracker();
        this.visitedUrls = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.connection = Jsoup.newSession().ignoreContentType(true).timeout(5000);
        workerPool = new ThreadPoolExecutor(NUM_OF_WORKERS, NUM_OF_WORKERS, 2L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        workerPool.allowCoreThreadTimeOut(true);
        awaitingFinishThread = new AwaitingFinishThread();
    }

    /**
     * Submits a new {@code Page} to be crawled by the system.
     *
     * <p>Note that this would crawl all the nested pages on the passed page as well.</p>
     *
     * <p>Note that the request would be picked up a shared pool of workers and is not guaranteed to be executed
     * immediately. This method returns immediately after submitting the request.</p>
     */
    public void crawlDomain(URL url) {
        if (awaitingFinishThread.isAlive()) {
            throw new IllegalArgumentException("Only 1 crawling allowed at a time");
        }

        filter = new UrlFilter(url.toExternalForm());
        workTracker.trackNewPage();
        awaitingFinishThread.start();

        try {
            workerPool.submit(new Worker(connection.newRequest(), filter, pageProcessingFinishCallback, url.toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the total no of pages processed by the system.
     */
    public long getTotalPageProcessed() {
        return workerPool.getCompletedTaskCount();
    }

    private void scheduleUrlIfRequired(URI url) {
        if (visitedUrls.add(url.toString())) {
            workTracker.trackNewPage();
            workerPool.submit(new Worker(connection.newRequest(), filter, pageProcessingFinishCallback, url));
        }
    }

    private class WorkCallbackDelegate implements WorkCallback {
        private final WorkCallback delegate;

        public WorkCallbackDelegate(WorkCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onFinishedPageSuccessfully(Page page) {
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
