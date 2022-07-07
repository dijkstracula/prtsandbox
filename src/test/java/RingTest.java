import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import prt.Monitor;
import prt.events.PEvent;
import punit.ObservableAppender;
import punit.annotations.PSpecTest;
import punit.flows.Flow;
import punit.flows.LocalFlow;
import sampleimpl.Ring;
import samplespec.RingEventParser;
import samplespec.RingSpec;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RingTest {
    ObservableAppender source;

    @Test
    @DisplayName("Can add to a Ring")
    public void testSingleRingAdd() {
        Ring r = new Ring();
        r.Add(42);
    }

    public static class LocalRingSpecFlow implements Supplier<Flow> {
        @Override
        public Flow get() {
            // Our dummy experimental pipeline: currently requires:

            // 1) A transformation function mapping loglines to sequences of events;
            Function<String, Stream<? extends PEvent<?>>> parser = new RingEventParser();

            // 2) A way to construct a Monitor to consume those events;
            Supplier<Monitor> getMonitor = () -> new RingSpec();

            // And a flow that, when the source observes a log line, parses it into
            // an event and passes that event forward to the monitor.
            // (Such a pipeline could instead send the log line to an out-of-process runtime
            // monitor too, of course.  The point is no code external to the Flow cares.)
            return new LocalFlow(parser, getMonitor);
        }
    }

    //@Test
    @PSpecTest(impl = Ring.class, flowFactory = LocalRingSpecFlow.class)
    @DisplayName("Can multiply to a Ring")
    public void testSingleRingMul() {
        Ring r = new Ring();
        r.Mul(42);
    }
}
