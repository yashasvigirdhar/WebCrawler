package yashasvig.crawler.work;

import java.util.concurrent.Phaser;

/**
 * Utility class to track the ongoing work.
 * <p>
 * This class provides a way to register the ongoing work and wait for all the work to be completed for any
 * post-processing to be done.
 */
final class WorkTracker {
    private final Phaser phaser;

    WorkTracker() {
        phaser = new Phaser();
    }

    void waitForFinish() {
        phaser.awaitAdvance(0);
    }

    void trackNewPage() {
        phaser.register();
    }

    void finishedPage() {
        phaser.arrive();
    }
}
