package yashasvig.crawler.centre;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import yashasvig.crawler.models.Page;
import yashasvig.crawler.postprocessing.PostProcessor;
import yashasvig.crawler.work.WorkCallback;
import yashasvig.crawler.work.WorkCoordinator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CrawlCentreTest {

    private static final URI INVALID_SCHEME_URI;
    private static final URI VALID_SCHEME_URI;
    private static final Page SAMPLE_PAGE;
    private static final String SAMPLE_ERROR = "Look at this error!!";

    static {
        try {
            VALID_SCHEME_URI = new URI("https://monzo.com");
            INVALID_SCHEME_URI = new URI("ftp://monzo.com");
            SAMPLE_PAGE = new Page(VALID_SCHEME_URI, new HashSet<>());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    private final PostProcessor processor = mock(PostProcessor.class);
    private final WorkCoordinator workCoordinator = mock(WorkCoordinator.class);

    private final CrawlCentre crawlCentre = new CrawlCentre(
            () -> ImmutableSet.of(processor),
            workCoordinator,
            MoreExecutors.newDirectExecutorService());

    @Test
    void start_schemeNotSupported_throws() {
        assertThrows(IllegalArgumentException.class, () -> crawlCentre.start(INVALID_SCHEME_URI));
    }

    @Test
    void start_addsWorkCallback() {
        crawlCentre.start(VALID_SCHEME_URI);

        verify(workCoordinator).setCallback(any());
    }

    @Test
    void start_callsWorkCoordinatorAPI() {
        crawlCentre.start(VALID_SCHEME_URI);

        verify(workCoordinator).crawlDomain(VALID_SCHEME_URI);
    }

    @Test
    void start_invalidUri_doesNotCallWorkCoordinatorAPI() {
        assertThrows(IllegalArgumentException.class, () -> crawlCentre.start(INVALID_SCHEME_URI));

        verify(workCoordinator, never()).crawlDomain(any());
    }

    @Test
    void start_invokesCrawlingStartedOnProcessor() {
        crawlCentre.start(VALID_SCHEME_URI);

        verify(processor).onCrawlingStarted(VALID_SCHEME_URI);
    }

    @Test
    void callback_forwardsOnFinishedPageSuccessfully() {
        ArgumentCaptor<WorkCallback> workCallbackCaptor = ArgumentCaptor.forClass(WorkCallback.class);
        crawlCentre.start(VALID_SCHEME_URI);

        verify(workCoordinator).setCallback(workCallbackCaptor.capture());

        WorkCallback workCallback = workCallbackCaptor.getValue();
        workCallback.onFinishedPageSuccessfully(SAMPLE_PAGE);

        verify(processor).onFinishedPageSuccessfully(SAMPLE_PAGE);
    }

    @Test
    void callback_forwardsOnError() {
        ArgumentCaptor<WorkCallback> workCallbackCaptor = ArgumentCaptor.forClass(WorkCallback.class);
        crawlCentre.start(VALID_SCHEME_URI);

        verify(workCoordinator).setCallback(workCallbackCaptor.capture());

        WorkCallback workCallback = workCallbackCaptor.getValue();
        workCallback.onError(VALID_SCHEME_URI.toString(), SAMPLE_ERROR);

        verify(processor).onError(VALID_SCHEME_URI.toString(), SAMPLE_ERROR);
    }

    @Test
    void callback_forwardsOnFinishedCrawling() {
        ArgumentCaptor<WorkCallback> workCallbackCaptor = ArgumentCaptor.forClass(WorkCallback.class);
        crawlCentre.start(VALID_SCHEME_URI);

        verify(workCoordinator).setCallback(workCallbackCaptor.capture());

        WorkCallback workCallback = workCallbackCaptor.getValue();
        workCallback.onFinishedCrawling();

        verify(processor).onFinishedCrawling(any());
    }
}