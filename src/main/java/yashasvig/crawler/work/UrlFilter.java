package yashasvig.crawler.work;

/**
 * Provides API to check if a particular url is a valid one for our program.
 */
public final class UrlFilter {
    private final String baseUrl;

    UrlFilter(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isValid(String url) {
        return url.startsWith(baseUrl);
    }
}
