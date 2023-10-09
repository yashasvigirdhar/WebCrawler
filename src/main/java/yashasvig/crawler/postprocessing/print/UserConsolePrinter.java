package yashasvig.crawler.postprocessing.print;

import yashasvig.crawler.models.Page;
import yashasvig.crawler.postprocessing.PostProcessor;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link PostProcessor} which prints some useful information to the user while crawling is
 * going on.
 */
public final class UserConsolePrinter implements PostProcessor {

    private URL baseUrl;
    private final AtomicInteger crawledPages;

    public UserConsolePrinter() {
        crawledPages = new AtomicInteger(0);
    }

    @Override
    public void onCrawlingStarted(URL url) {
        this.baseUrl = url;
        System.out.printf("""
                                        
                *** Tighten your belts, we are starting to crawl %s***.
                Keep an eye on this console for some progress stats.
                                        
                """, baseUrl);
    }

    @Override
    public void onFinishedPageSuccessfully(Page page) {
        int totalCrawled = crawledPages.incrementAndGet();
        System.out.printf("Total pages crawled until now: %s\n", totalCrawled);
    }

    @Override
    public void onError(String url, String errorMessage) {

    }

    @Override
    public void onFinishedCrawling(Duration timeTaken) {
        if (crawledPages.get() > 0) {
            System.out.printf("\nHooray! Successfully finished crawling %s.\nWe crawled %s pages in %s seconds\n",
                    baseUrl.toExternalForm(), crawledPages, timeTaken.getSeconds());
        } else {
            System.out.print("Looks like something went wrong when crawling. Please see logs for more details.\n");
        }
    }

    @Override
    public String getName() {
        return "UserConsolePrinter";
    }
}
