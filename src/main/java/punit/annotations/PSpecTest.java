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
    Class<? extends Supplier<? extends Flow>> flowFactory();
}
