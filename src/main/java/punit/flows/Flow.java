package punit.flows;

import java.util.function.Function;

/**
 * A flow represents a way to connect something that produces log lines
 * to a thing that effectully-consumes them.
 *
 * TODO: At present, this function never returns anything.  But: this
 * is almost certainly the wrong design.  We need a way of getting information
 * back from the monitor (certainly at least whether or not the spec has
 * been violated!)  We could either make that an explicit return type (i.e.
 * `Optional<BadThingHappened>` or just enforce that a Flow can throw a
 * prt.AssertionFailedException.
 */
public abstract class Flow implements Function<String, Void> {
}
