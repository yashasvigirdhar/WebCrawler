package yashasvig.crawler.work;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class UrlFilterTest {
    private static final String AUTHORITY = "monzo.com";


    private final UrlFilter filter = new UrlFilter(AUTHORITY);

    @Test
    void isValid_validAuthority_returnsTrue() throws URISyntaxException {
        assertTrue(filter.isValid(new URI("http://monzo.com/legal/page2.pdf")));
    }

    @Test
    void isValid_invalidAuthority_returnsFalse() throws URISyntaxException {
        assertFalse(filter.isValid(new URI("http://bonzo.com/legal/page2.pdf")));
    }

    @Test
    void isValid_fragmentPresent_returnsFalse() throws URISyntaxException {
        assertFalse(filter.isValid(new URI("http://monzo.com/legal/page2#mainContent.pdf")));
    }
}