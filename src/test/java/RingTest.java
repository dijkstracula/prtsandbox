import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import prt.Monitor;
import prt.events.PEvent;
import punit.ObservableAppender;
import punit.annotations.PAssertExpected;
import punit.annotations.PSpecTest;
import punit.flows.Flow;
import punit.flows.LocalFlow;
import sample.sampleimpl.Ring;
import sample.samplespec.PEvents;
import sample.samplespec.PTypes;
import sample.samplespec.RingEventParser;
import sample.samplespec.RingSpec;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RingTest {
    ObservableAppender source;

    @Test
    @DisplayName("Can add to a Ring")
    public void testSingleRingAdd() {
        Ring r = new Ring();
        r.Add(42);
    }

    public static class RingSpecFlowFactory implements Supplier<Flow> {
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

    @Test
    @DisplayName("Can multiply to a ring specification, manually")
    public void testRingSpecMul() {
        Monitor spec = new RingSpec();
        spec.ready();

        spec.accept(new PEvents.mulEvent(new PTypes.PTuple_i_total(42, 0)));
    }

    @Test
    @DisplayName("Test no overflow, manually")
    public void testSpecOverflow() {
        Monitor spec = new RingSpec();
        spec.ready();

        spec.accept(new PEvents.addEvent(new PTypes.PTuple_i_total(32, 32)));
        spec.accept(new PEvents.addEvent(new PTypes.PTuple_i_total(10, 42)));
    }

    //@Test
    @PSpecTest(impl = Ring.class, specFlowFactory = RingSpecFlowFactory.class)
    @DisplayName("Can multiply to a Ring specification, by way of driving the implementation")
    public void testSingleRingMul() {
        Ring r = new Ring();
        r.Add(32);
        r.Add(10);
    }

    //@Test
    @PSpecTest(impl = Ring.class, specFlowFactory = RingSpecFlowFactory.class)
    @PAssertExpected
    @DisplayName("Can cause a P assertion violation")
    public void testRingOverflow() {
        Ring r = new Ring();
        r.Add(32);
        r.Mul(10);
    }
}
