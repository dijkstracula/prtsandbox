import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import prt.events.PEvent;
import punit.Log4JEventSubscriber;
import sampleimpl.Ring;
import samplespec.RingEventParser;
import samplespec.RingSpec;

import java.util.function.Function;
import java.util.stream.Stream;

public class RingTest {

    @Test
    @DisplayName("Can add to a Ring")
    public void testSingleRingAdd() {
        Ring r = new Ring();
        r.Add(42);
    }

    // This is the core of the magic that is going to be hidden behind
    // the log4j interface, still very much TODO.
    private void setup() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext();
        Configuration config = ctx.getConfiguration();
        Filter f = config.getFilter();

        // Our dummy experimental pipeline: currently requires:

        // A transformation function mapping loglines to sequences of events;
        Function<String, Stream<PEvent<?>>> parser = new RingEventParser();

        // A Monitor to consume those events;
        RingSpec m = new RingSpec();
        m.ready();

        // And a pipeline that, when the source observes a log line, parses it into
        // an event and passes it to the monitor.
        // (Such a pipeline could instead send the log line to an out-of-process runtime
        // monitor too, of course.  The point is that the rest of this code doesn't care.)
        Log4JEventSubscriber source = new Log4JEventSubscriber(f);
        source.observe()
                .subscribeOn(Schedulers.single())
                .flatMap(s -> Observable.fromIterable(() -> parser.apply(s).iterator()))
                .forEach(p -> m.process(p));
        source.start();

        // The only thing left to do is add our shim appender to the class.
        var logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(Ring.class);
        logger.addAppender(source);
    }

    @Test
    @DisplayName("Can multiply to a Ring")
    public void testSingleRingMul() {
        setup();
        Ring r = new Ring();
        r.Mul(42);
    }
}
