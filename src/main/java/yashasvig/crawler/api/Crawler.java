package yashasvig.crawler.api;

import yashasvig.crawler.centre.CrawlCentre;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Entry point into the crawler system.
 *
 * <p>This is the only API that should be used by the external entities.</p>
 */
public final class Crawler {

    private final CrawlCentre crawlCentre;

    /**
     * Initializes the crawler system.
     *
     * <p>Note that the output of the process would be stored in a text file in the same folder in which we are
     * running the application. The format of the output file would be: <base-url>-<timestamp>.txt</p>
     */
    public Crawler() {
        crawlCentre = new CrawlCentre();
    }

    /**
     * Starts the crawling process.
     *
     * <p>The process happens asynchronously and this method returns shortly after scheduling it.</p>
     *
     * @param baseUrl the base url to start crawling
     */
    public void crawl(String baseUrl) throws URISyntaxException, MalformedURLException {
        crawlCentre.start(new URI(baseUrl).toURL());
    }
}
