package yashasvig.crawler.work;

import java.net.URI;

/**
 * Provides API to check if a particular {@link URI} is a valid one for our program to crawl.
 */
final class UrlFilter {
    private final String authority;

    /**
     * @param authority the authority which should be used to filter out links for further crawling
     */
    UrlFilter(String authority) {
        this.authority = authority;
    }

    /**
     * Returns true if we should try to crawl the passed {@code uri}.
     */
    public boolean isValid(URI uri) {
        return uri.getAuthority().equals(authority) && uri.getFragment() == null;
    }
}
