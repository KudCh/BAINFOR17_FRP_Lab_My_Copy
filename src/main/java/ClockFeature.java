import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ClockFeature {
    public Label digitalClockLabel = new Label();
    public Label digitalDateLabel = new Label();

    ClockFeature() {
        /* Observable gives the current time every 200 ms */
        Observable<Long> digitalClock = Observable.interval(200, TimeUnit.MILLISECONDS);
        /* Observable gives the current day every minute */
        Observable<Long> digitalDate = Observable.interval(1, TimeUnit.MINUTES);

        digitalClockLabel.setTextFill(Color.rgb(166, 50, 248));
        digitalDateLabel.setTextFill(Color.rgb(166, 50, 160));
        digitalDateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Disposable disposable1 = digitalClock
                .observeOn(JavaFxScheduler.platform())
                .subscribe(e -> digitalClockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        Disposable disposable2 = digitalDate
                .observeOn(JavaFxScheduler.platform())
                .subscribe(e -> digitalDateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        /* add & dispose of disposables */
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.addAll(disposable1,disposable2);
//        compositeDisposable.dispose();
    }
}
