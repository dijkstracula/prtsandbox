package sample.samplespec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import prt.State;
import prt.events.PEvent;

import java.math.BigInteger;
import java.util.List;

public class RingSpec extends prt.Monitor {
    Logger logger = LogManager.getLogger(this.getClass());

    private BigInteger val;

    public RingSpec() {
        super();

        addState(new State.Builder("INIT")
                .isInitialState(true)
                .withEvent(PEvents.addEvent.class, e -> {
                    BigInteger nextVal = val.add(BigInteger.valueOf(e.i));

                    if (!nextVal.equals(BigInteger.valueOf(e.total))) {
                        throw new prt.exceptions.PAssertionFailureException("Sum failed");
                    }

                    logger.info(val + " + " + e.i + " = " + nextVal);
                    val = nextVal;
                })
                .withEvent(PEvents.mulEvent.class, e -> {
                    BigInteger nextVal = val.multiply(BigInteger.valueOf(e.i));

                    if (!nextVal.equals(BigInteger.valueOf(e.total))) {
                        throw new prt.exceptions.PAssertionFailureException("Mult failed");
                    }

                    logger.info(val + " * " + e.i + " = " + nextVal);
                    val = nextVal;
                })
                .build());

        val = BigInteger.valueOf(0);
    }

    @Override
    public List<Class<? extends PEvent<?>>> getEventTypes() {
        return List.of(PEvents.addEvent.class, PEvents.mulEvent.class);
    }

}
