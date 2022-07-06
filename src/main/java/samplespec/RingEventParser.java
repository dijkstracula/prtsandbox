package samplespec;

import prt.events.PEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class RingEventParser implements Function<String, Stream<PEvent<?>>> {

    private static final HashMap<String, Function<String, ? extends PEvent<?>>> handlers = new HashMap<>(Map.of(
            "ADD", RingEventParser::payloadToAddEvent,
            "MUL", RingEventParser::payloadToMulEvent
    ));

    private static PEvents.addEvent payloadToAddEvent(String payload) {
        return new PEvents.addEvent(Integer.valueOf(payload));
    }

    private static PEvents.mulEvent payloadToMulEvent(String payload) {
        return new PEvents.mulEvent(Integer.valueOf(payload));
    }


    @Override
    public Stream<PEvent<?>> apply(String line) {
        String[] tokens = line.split(":");
        if (tokens.length != 2) {
            return Stream.of();
        }

        String event = tokens[0];
        String payload = tokens[1];

        if (handlers.containsKey(event)) {
            return Stream.of(handlers.get(event).apply(payload));
        }
        return Stream.of();
    }
}
