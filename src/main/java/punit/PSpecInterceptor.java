package punit;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import punit.annotations.PSpecTest;
import punit.flows.Flow;

import java.lang.reflect.Method;
import java.util.Optional;

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

        appender.observe()
                .subscribeOn(Schedulers.single())
                .forEach(flow::apply);
        appender.start();

        // Before executing the test code, add the shim appender to the logger.
        var implLogger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(params.impl());
        implLogger.addAppender(appender);

        invocation.proceed(); // Run the test.

        // Reset the logger to its previous state by removing our appender.
        implLogger.removeAppender(appender);
    }

}
