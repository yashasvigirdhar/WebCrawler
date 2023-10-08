package yashasvig.crawler.models;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import yashasvig.crawler.util.IndentingPrintWriter;

import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * An Immutable representation of a web page in the system.
 */
@Immutable
public final class Page {

    private final URL url;
    private final Set<URI> childUrls;   // we store URI in set since #equals and #hashcode for

    public Page(URL url, Set<URI> childUrls) {
        this.url = url;
        this.childUrls = childUrls;
    }

    public ImmutableList<URI> getChildUrls() {
        return ImmutableList.copyOf(childUrls);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(url, page.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    public void print(IndentingPrintWriter writer) {
        writer.increaseIndent();
        writer.println("Page: " + url.toExternalForm());
        writer.increaseIndent();
        writer.println("Urls on this page:");
        writer.increaseIndent();
        childUrls.forEach(url -> writer.println(url.toString()));
        writer.println();
        writer.decreaseIndent();
        writer.decreaseIndent();
        writer.decreaseIndent();
    }
}
