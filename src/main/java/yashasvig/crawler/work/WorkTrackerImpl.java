package yashasvig.crawler.work;

import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Phaser;

/**
 * An implementation of {@link WorkTracker} by using a {@link Phaser}, an advanced concurrency construct from Java
 * that allows us to dynamically change the no of parties that are taking part in the synchronization. Due to our
 * nature of work where we don't know beforehand how many pages we'd be crawling, this is super helpful to us. </p>
 */
@Singleton
public final class WorkTrackerImpl implements WorkTracker {

    @VisibleForTesting
    final Phaser phaser;

    @Inject
    WorkTrackerImpl() {
        phaser = new Phaser();
    }

    @Override
    public void waitForFinish() {
        phaser.awaitAdvance(0);
        phaser.forceTermination();
    }

    @Override
    public void trackNewPage() {
        phaser.register();
    }

    @Override
    public void finishedPage() {
        phaser.arrive();
    }
}
