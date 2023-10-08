package yashasvig.crawler.postprocessing.di;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import yashasvig.crawler.postprocessing.PostProcessor;
import yashasvig.crawler.postprocessing.print.FilePrinter;
import yashasvig.crawler.postprocessing.print.UserConsolePrinter;

@Module
public class PostProducersDaggerModule {

    @Provides
    @IntoSet
    static PostProcessor provideFilePrinter() {
        return new FilePrinter();
    }

    @Provides
    @IntoSet
    static PostProcessor provideUserConsolePrinter() {
        return new UserConsolePrinter();
    }
}

