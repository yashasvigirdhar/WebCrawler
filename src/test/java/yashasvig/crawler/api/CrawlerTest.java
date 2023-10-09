package yashasvig.crawler.api;

import org.junit.jupiter.api.Test;
import yashasvig.crawler.centre.CrawlCentre;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CrawlerTest {

    private static final String VALID_URI = "https://monzo.com";
    private static final String INVALID_URL = "https://monzo.com[/]";


    CrawlCentre crawlCentre = mock(CrawlCentre.class);
    private final Crawler crawler = new Crawler(crawlCentre);


    @Test
    void crawl_validUri_forwardsToCrawlCentre() throws URISyntaxException {
        crawler.crawl(VALID_URI);

        verify(crawlCentre).start(eq(new URI(VALID_URI)));
    }

    @Test
    void crawl_invalidUri_throwsException() {
        assertThrows(URISyntaxException.class, () -> crawler.crawl(INVALID_URL));
    }
}