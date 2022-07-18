package punit;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import prt.exceptions.PAssertionFailureException;
import punit.annotations.PAssertExpected;
import punit.annotations.PSpecTest;
import punit.exceptions.PAssertMismatchExeception;
import punit.flows.Flow;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This JUnit test interceptor handles per-test specification setup and teardown. It will construct the Logger shim,
 * associate the given Flow with it, and insert it into given class's logger's appenders.  After the test runs,
 * it will undo the insertion.
 */
public class PSpecInterceptor implements InvocationInterceptor {
    private Optional<PSpecTest> extractAnnotation(Method m) {
        PSpecTest a = m.getAnnotation(PSpecTest.class);
        return Optional.ofNullable(a);
    }

    private Optional<Class<? extends Throwable>> expectedException(Method m) {
        if (m.getAnnotation(PAssertExpected.class) == null) {
            return Optional.empty();
        }
        return Optional.of(PAssertionFailureException.class);
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        PSpecTest params = extractAnnotation(invocationContext.getExecutable())
                .orElseThrow(() -> new RuntimeException("No PSpecTest annotation??"));

        LoggerContext ctx = (LoggerContext) LogManager.getContext();
        Configuration config = ctx.getConfiguration();


        // Set up the Log4J appender that we will shim into the class under test, that
        // will synchronously process all log messages and send them through the effectful Flow
        // to the spec.
        ObservableAppender appender = new ObservableAppender(config.getFilter());
        Flow flow = params.specFlowFactory().getConstructor().newInstance().get();

        // Stash any thrown exception from the other thread here; throw upon completion.
        AtomicReference<Throwable> actualException = new AtomicReference<>();

        appender.observe()
                .subscribeOn(Schedulers.single())
                .subscribe(
                        flow::apply,
                        t -> actualException.compareAndSet(null, t));

        appender.start();

        // Before executing the test code, add the shim appender to the logger.
        var implLogger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(params.impl());
        implLogger.addAppender(appender);

        // Extract whether we expect this method to throw an exception, run the test,
        // and compare any exceptions thrown with the one we expect.
        invocation.proceed();

        Optional<PAssertMismatchExeception> error =
                PAssertMismatchExeception.fromTestResults(
                        Optional.ofNullable(actualException.get()),
                        expectedException(invocationContext.getExecutable()));
        if (error.isPresent()) {
            throw error.get();
        }

        // Reset the logger to its previous state by removing our appender.
        implLogger.removeAppender(appender);
    }

}
