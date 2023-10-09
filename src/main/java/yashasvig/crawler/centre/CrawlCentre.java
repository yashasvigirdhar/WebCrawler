package yashasvig.crawler.centre;

import dagger.Lazy;
import yashasvig.crawler.centre.di.qualifiers.PostProcessingPool;
import yashasvig.crawler.global.Constants;
import yashasvig.crawler.models.Page;
import yashasvig.crawler.postprocessing.PostProcessor;
import yashasvig.crawler.work.WorkCallback;
import yashasvig.crawler.work.WorkCoordinator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static yashasvig.crawler.global.Constants.SUPPORTED_SCHEMES;

/**
 * Schedules the crawling of all the web pages and handles the post-processing once a page has been crawled.
 */
@Singleton
public class CrawlCentre {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final WorkCoordinator workCoordinator;
    private final ExecutorService postProcessingExecutor;
    private final Lazy<Set<PostProcessor>> postProcessors;
    private Instant startTime;

    @Inject
    CrawlCentre(Lazy<Set<PostProcessor>> postProcessors, WorkCoordinator workCoordinator,
                @PostProcessingPool ExecutorService postProcessingExecutor) {
        this.workCoordinator = workCoordinator;
        this.postProcessingExecutor = postProcessingExecutor;
        this.postProcessors = postProcessors;
    }

    /**
     * Starts the crawling process.
     *
     * <p>The process happens asynchronously and this method returns shortly after scheduling it.</p>
     *
     * <p>Note that it throws {@link IllegalArgumentException} if the passed url scheme is not supported. See
     * {@link Constants#SUPPORTED_SCHEMES} to see what all schemes are supported</p>
     */
    public void start(URI baseUri) {
        if (Arrays.stream(SUPPORTED_SCHEMES).noneMatch(s -> s.equals(baseUri.getScheme()))) {
            throw new IllegalArgumentException(
                    String.format("Only %s schemes are supported currently", Arrays.toString(SUPPORTED_SCHEMES)));
        }

        workCoordinator.setCallback(new WorkCallbackImpl());

        this.startTime = Instant.now();
        workCoordinator.crawlDomain(baseUri);
        postProcessingExecutor.submit(() -> {
            for (PostProcessor processor : postProcessors.get()) {
                try {
                    processor.onCrawlingStarted(baseUri);
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                            String.format("Couldn't invoke onCrawlingStarted on listener:%s", processor.getName()), e);
                }
            }
        });
    }

    private class WorkCallbackImpl implements WorkCallback {

        @Override
        public void onFinishedPageSuccessfully(Page page) {
            postProcessingExecutor.submit(() -> {
                for (PostProcessor processor : postProcessors.get()) {
                    try {
                        processor.onFinishedPageSuccessfully(page);
                    } catch (Exception e) {
                        logger.log(Level.WARNING,
                                String.format("Couldn't invoke onFinishedPageSuccessfully on listener:%s",
                                        processor.getName()), e);
                    }
                }
            });
        }

        @Override
        public void onError(String url, String errorMessage) {
            postProcessingExecutor.submit(() -> {
                for (PostProcessor processor : postProcessors.get()) {
                    try {
                        processor.onError(url, errorMessage);
                    } catch (Exception e) {
                        logger.log(Level.WARNING,
                                String.format("Couldn't invoke onError on listener:%s", processor.getName()), e);
                    }
                }
            });
        }

        @Override
        public void onFinishedCrawling() {
            postProcessingExecutor.execute(() -> {
                Duration timeTaken = Duration.between(startTime, Instant.now());

                for (PostProcessor processor : postProcessors.get()) {
                    try {
                        processor.onFinishedCrawling(timeTaken);
                    } catch (Exception e) {
                        logger.log(Level.WARNING,
                                String.format("Couldn't invoke onFinishedCrawling on listener:%s", processor.getName()),
                                e);
                    }
                }
            });
        }
    }
}
