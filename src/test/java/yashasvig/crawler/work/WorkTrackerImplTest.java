package yashasvig.crawler.work;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkTrackerImplTest {

    private WorkTrackerImpl tracker;

    @BeforeEach
    public void setUp() {
        tracker = new WorkTrackerImpl();
    }

    @Test
    void waitForFinish_waitsForRegisteredParties() {
        tracker.trackNewPage();
        Thread t1 = new Thread(() -> tracker.finishedPage());
        t1.start();
        tracker.waitForFinish();
        assertEquals(1, tracker.phaser.getPhase() + Integer.MIN_VALUE);
        assertEquals(1, tracker.phaser.getRegisteredParties());
        assertTrue(tracker.phaser.isTerminated());
    }

    @Test
    void waitForFinish_terminates() {
        tracker.trackNewPage();
        Thread t1 = new Thread(() -> tracker.finishedPage());
        t1.start();
        tracker.waitForFinish();
        assertTrue(tracker.phaser.isTerminated());
    }

}