package yashasvig.crawler.work;

import com.google.common.util.concurrent.MoreExecutors;
import org.jsoup.Connection;
import org.junit.jupiter.api.Test;
import yashasvig.crawler.models.Page;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WorkCoordinatorTest {

    private static final URI VALID_SCHEME_URI;
    private static final Page SAMPLE_PAGE;
    private static final String SAMPLE_ERROR = "Look at this error!!";

    static {
        try {
            VALID_SCHEME_URI = new URI("https://monzo.com");
            SAMPLE_PAGE = new Page(VALID_SCHEME_URI, new HashSet<>());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final WorkTracker workTracker = mock(WorkTracker.class);
    private final Connection connection = mock(Connection.class);


    private final WorkCoordinator coordinator = new WorkCoordinator(workTracker,
            MoreExecutors.newDirectExecutorService(),
            connection);

    @Test
    void setCallback() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);

        assertNotNull(coordinator.getWorkCallback());
    }

    @Test
    void crawlDomain_invokesWorkTracker() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);
        coordinator.crawlDomain(VALID_SCHEME_URI);

        verify(workTracker).trackNewPage();
    }

    @Test
    void onFinishedPageSuccessfully_invokesWorkTracker() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);

        WorkCallback wrapper = coordinator.getWorkCallback();
        wrapper.onFinishedPageSuccessfully(SAMPLE_PAGE);

        verify(workTracker).finishedPage();
    }

    @Test
    void onError_invokesWorkTracker() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);

        WorkCallback wrapper = coordinator.getWorkCallback();
        wrapper.onError(VALID_SCHEME_URI.toString(), SAMPLE_ERROR);

        verify(workTracker).finishedPage();
    }

    @Test
    void forwardsCallback_onFinishedPageSuccessfully() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);

        WorkCallback wrapper = coordinator.getWorkCallback();
        wrapper.onFinishedPageSuccessfully(SAMPLE_PAGE);

        verify(callback).onFinishedPageSuccessfully(SAMPLE_PAGE);
    }

    @Test
    void forwardsCallback_onError() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);

        WorkCallback wrapper = coordinator.getWorkCallback();
        wrapper.onError(VALID_SCHEME_URI.toString(), SAMPLE_ERROR);

        verify(callback).onError(VALID_SCHEME_URI.toString(), SAMPLE_ERROR);
    }

    @Test
    void forwardsCallback_onFinishedCrawling() {
        WorkCallback callback = mock(WorkCallback.class);
        coordinator.setCallback(callback);

        WorkCallback wrapper = coordinator.getWorkCallback();
        wrapper.onFinishedCrawling();

        verify(callback).onFinishedCrawling();
    }
}