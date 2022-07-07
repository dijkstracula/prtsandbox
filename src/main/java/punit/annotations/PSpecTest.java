package punit.annotations;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import punit.PSpecInterceptor;
import punit.flows.Flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;


/**
 * PSpecTest annotates a test method to indicate that instances of a given class should run under the
 * observeration of a specification flow, which consumes strings logged by instances of the given class
 * and passes them downstream to a runtime monitor to observe and alert upon.
 *
 * The specifics of setting this up can be found in the PSpecInterceptor class.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Tag("PSpecTest")
@Test
@ExtendWith(PSpecInterceptor.class)
public @interface PSpecTest {
    /* The class we want to shim our observing log appender into. */
    Class<?> impl();

    /* How to generate a string -> unit operation that connects the implementation's
     * log lines to the monitor spec. */
    Class<? extends Supplier<? extends Flow>> specFlowFactory();
}
