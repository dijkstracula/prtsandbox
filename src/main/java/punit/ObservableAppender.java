package punit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
        name = "ObservableAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class ObservableAppender extends AbstractAppender {

    private final PublishSubject<String> downstream;


    public ObservableAppender(Filter filter) {
        // TODO: Layout?
        super("ObservableAppender", filter, null, false, null);
        this.downstream = PublishSubject.create();
    }

    @Override
    public void append(LogEvent event) {
        downstream.onNext(event.getMessage().getFormattedMessage());
    }

    public Observable<String> observe() {
        return downstream;
    }
}
