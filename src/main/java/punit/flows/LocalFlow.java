package punit.flows;

import prt.Monitor;
import prt.events.PEvent;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LocalFlow extends Flow {
    Function<String, Stream<? extends PEvent<?>>> parser;
    Monitor monitor;

    public LocalFlow(Function<String, Stream<? extends PEvent<?>>> p, Supplier<Monitor> m) {
        parser = p;

        monitor = m.get();
        monitor.ready();
    }

    @Override
    public Void apply(String s) {
        parser.apply(s).forEach(monitor::process);
        return null;
    }

}
