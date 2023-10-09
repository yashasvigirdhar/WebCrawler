package yashasvig.crawler.api;

import yashasvig.crawler.centre.CrawlCentre;
import yashasvig.crawler.global.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Entry point into the crawler system.
 *
 * <p>This is the only API that should be used by the external entities.</p>
 */
@Singleton
public final class Crawler {

    private final CrawlCentre crawlCentre;

    /**
     * Initializes the crawler system.
     *
     * <p>Note that the output of the process would be stored in a text file in the same folder in which we are
     * running the application. The format of the output file would be: <base-url>-<timestamp>.txt</p>
     */
    @Inject
    Crawler(CrawlCentre crawlCentre) {
        this.crawlCentre = crawlCentre;
    }

    /**
     * Starts the crawling process.
     *
     * <p>The process happens asynchronously and this method returns shortly after scheduling it.</p>
     *
     * <p>Note that it throws {@link IllegalArgumentException} if the passed url scheme is not supported. See
     * {@link Constants#SUPPORTED_SCHEMES} to see what all schemes are supported</p>
     *
     * @param baseUrl the base url to start crawling
     * @throws IllegalArgumentException if the passed url is not supported
     */
    public void crawl(String baseUrl) throws URISyntaxException {
        crawlCentre.start(new URI(baseUrl));
    }
}
