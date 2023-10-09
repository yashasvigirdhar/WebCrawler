package yashasvig.crawler.postprocessing;

import yashasvig.crawler.models.Page;

import java.net.URI;
import java.time.Duration;

/**
 * Implemented by any system which wants to be invoked during various stages of the crawling process.
 *
 * <p>Note that callbacks here would be called sequentially for all implementations of {@link PostProcessor}s so
 * consumers are expected to return quickly.
 */
public interface PostProcessor {

    /**
     * Invoked when the crawling of {@code url} starts
     *
     * <p>This is the first callback in the lifetime of crawling a particular domain and is usually followed by other
     * callbacks, eventually ending at {@link #onFinishedCrawling(Duration)}.
     */
    void onCrawlingStarted(URI url);

    /**
     * Invoked when the crawling finished successfully for {@code Page}.
     *
     * <p>Invocation of this method means that the child links for the respective page have been successfully
     * populated.</p>
     */
    void onFinishedPageSuccessfully(Page page);

    /**
     * Invoked when we couldn't crawl the {code Page}.
     *
     * @param errorMessage an error message that's appropriate for logging
     */
    void onError(String url, String errorMessage);

    /**
     * Invoked when crawling is finished.
     *
     * <p>This is the last in the series of callbacks invoked in the lifetime of crawling a particular domain.</p>
     *
     * @param timeTaken total time taken by the crawling process
     */
    void onFinishedCrawling(Duration timeTaken);

    /**
     * Invoked to get a logging-friendly name of the implementation.
     */
    String getName();
}
