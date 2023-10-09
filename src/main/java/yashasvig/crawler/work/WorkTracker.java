package yashasvig.crawler.work;

/**
 * Utility class to track the completion status of ongoing work.
 * <p>
 * This class provides a way to register the ongoing work and wait for all the work to be completed for any
 * post-processing to be done.
 */
public interface WorkTracker {

    /**
     * Waits for all the work to be completed in a blocking manner. This would wait only for the work that has been
     * registered to this class.
     *
     * <p><b></p>This blocks the calling thread.</b></p>
     */
    void waitForFinish();

    /**
     * Increments the number of work items that we need to wait before completion.
     */
    void trackNewPage();

    /**
     * Decrements the number of work items that we need to wait before completion.
     */
    void finishedPage();
}
