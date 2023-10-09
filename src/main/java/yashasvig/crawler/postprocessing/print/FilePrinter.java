package yashasvig.crawler.postprocessing.print;

import yashasvig.crawler.models.Page;
import yashasvig.crawler.postprocessing.PostProcessor;
import yashasvig.crawler.util.IndentingPrintWriter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link PostProcessor} that prints the crawling output to a file.
 */
public final class FilePrinter implements PostProcessor {

    private IndentingPrintWriter writer;
    private final AtomicInteger crawledPages;

    public FilePrinter() {
        crawledPages = new AtomicInteger(0);
    }

    @Override
    public void onCrawlingStarted(URI url) {
        initializeFileWriter(url);
    }


    @Override
    public void onFinishedPageSuccessfully(Page page) {
        crawledPages.incrementAndGet();
        page.print(writer);
    }

    @Override
    public void onError(String url, String errorMessage) {
        writer.increaseIndent();
        writer.println("Error: " + errorMessage);
        writer.println();
        writer.decreaseIndent();
    }

    @Override
    public void onFinishedCrawling(Duration timeTaken) {
        writer.println("\n\n*******Stats********");
        writer.increaseIndent();
        writer.print("Total no of pages scraped", crawledPages);
        writer.println();
        writer.print("Time taken (in seconds)", timeTaken.getSeconds());
        writer.println();
        writer.decreaseIndent();
        writer.flush();
    }

    @Override
    public String getName() {
        return "Printer";
    }

    private void initializeFileWriter(URI uri) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        Date resultdate = new Date(System.currentTimeMillis());
        try {
            this.writer = new IndentingPrintWriter(
                    new PrintWriter(uri.getAuthority() + "-" + sdf.format(resultdate) + ".txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        writer.println("Crawling started, let's go!");
    }
}
