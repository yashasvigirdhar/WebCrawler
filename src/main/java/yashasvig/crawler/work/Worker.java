package yashasvig.crawler.work;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import yashasvig.crawler.models.Page;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a single instance of {@link Runnable} in the system which is responsible for scraping a single
 * webpage (represented by {@code Page} in our system).
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li> Scrapes a particular {@code Page} passed to it.</li>
 *     <li> Invokes a callback (see {@code PageProcessingFinishCallback}) after finishing the processing and
 *     populating the child links for that page.</li>
 * </ul>
 */
final class Worker implements Runnable {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final static String HTTP = "http";
    private final static String HTTPS = "https";

    private final Connection session;
    private final UrlFilter filter;
    private final WorkCallback finishCallback;
    private final URI url;


    /**
     * Constructs a new worker instance.
     *
     * @param session      an instance of {@link Connection} used to fetch the web page and parse it
     * @param filter       would be applied to the child urls on this page to decide if we want to
     *                     further process them
     * @param workCallback to be invoked when the processing for this page finishes
     * @param url          the page to be crawled by this worker
     */
    Worker(Connection session, UrlFilter filter, WorkCallback workCallback, URI url) {
        this.session = session;
        this.filter = filter;
        this.finishCallback = workCallback;
        this.url = url;
    }

    @Override
    public void run() {
        processPage(url);
    }

    private void processPage(URI pageUrl) {
        Document document;
        try {
            Connection connection = session.url(pageUrl.toString());
            Connection.Response response = connection.method(Connection.Method.HEAD).execute();
            String contentType = response.contentType();
            if (contentType != null && !contentType.contains("text/html")) {
                finishCallback.onFinishedPageSuccessfully(new Page(pageUrl, new HashSet<>()));
                //System.out.printf("Skip downloading %s\n", response.url());
                return;
            }

            document = connection.get();
        } catch (IOException exception) {
            logger.log(Level.INFO, String.format("Error: %s for %s", exception.getLocalizedMessage(), pageUrl));
            finishCallback.onError(url.toString(), exception.getLocalizedMessage());
            return;
        }

        List<String> childLinks = document.select("a[href]").stream().map(element -> element.attr("abs:href"))
                .filter(link -> link.startsWith(HTTP) || link.startsWith(HTTPS)).toList();

        Set<URI> childURIs = new HashSet<>();
        for (String link : childLinks) {
            try {
                URI uri = new URI(link);
                if (filter.isValid(uri)) {
                    childURIs.add(uri);
                }
            } catch (URISyntaxException e) {
                logger.log(Level.INFO, "Invalid url found: " + link);
            }
        }
        finishCallback.onFinishedPageSuccessfully(new Page(pageUrl, childURIs));
    }
}
