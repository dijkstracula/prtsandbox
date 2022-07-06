package samplespec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import prt.State;
import prt.events.PEvent;

import java.util.List;

public class RingSpec extends prt.Monitor {
    Logger logger = LogManager.getLogger(this.getClass());
    public RingSpec() {
        super();

        addState(new State.Builder("INIT")
                .isInitialState(true)
                .withEvent(PEvents.addEvent.class, i -> logger.info("Adding " + i))
                .withEvent(PEvents.mulEvent.class, i -> logger.info("Multiplying " + i))
                .build());
    }

    @Override
    public List<Class<? extends PEvent<?>>> getEventTypes() {
        return List.of(PEvents.addEvent.class, PEvents.mulEvent.class);
    }
}
