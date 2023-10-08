package yashasvig.crawler.work;

import yashasvig.crawler.models.Page;

import java.net.URL;

/**
 * Provides a way for caller to get informed when the crawling for a page has finished.
 */
public interface WorkCallback {
    /**
     * Called when the crawling finished successfully for {@code Page}.
     *
     * <p>Invocation of this method means that the child links for the respective page have been successfully
     * populated.</p>
     *
     * <p>This would be called on the same thread on which the worker crawled this page so it could be different for
     * different pages.</p>
     */
    void onFinishedPageSuccessfully(Page page);

    /**
     * Called when we couldn't crawl the {code Page}.
     *
     * <p>This would be called on the same thread on which the worker tried crawling this url so it could be
     * different for different pages..</p>
     *
     * @param errorMessage an error message that's appropriate for logging
     */
    void onError(String url, String errorMessage);

    void onFinishedCrawling();
}
