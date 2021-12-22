import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* todo: find way to reset click counter /*
/* todo: add comments */

public class FillBarGameFeature {
    public Button fillButton = new Button("Click fast to fill bar!");
    public ProgressBar bar = new ProgressBar(0); // ProgressBar starts at 0
    public Button restartButton = new Button("Restart?");
    public Rectangle rectangle = new Rectangle(25,25);
    public Label fillBarLabel = new Label();
    public Label rectangleLabel = new Label();
    public MenuBar difficultyMenuBar = new MenuBar();
    Menu difficultyMenu = new Menu("Game difficulty");
    RadioMenuItem difficultyEasy = new RadioMenuItem("Easy");
    RadioMenuItem difficultyMedium = new RadioMenuItem("Medium");
    RadioMenuItem difficultyHard = new RadioMenuItem("Hard");

    FillBarGameFeature() {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(difficultyEasy,difficultyMedium,difficultyHard);    // Only one difficulty can be selected at a time!
        difficultyMenu.getItems().addAll(difficultyEasy,difficultyMedium,difficultyHard);
        difficultyMenuBar.getMenus().add(difficultyMenu);
        difficultyEasy.setSelected(true);   // Default difficulty is easy
        Float[] difficultyArray = new Float[3];
        difficultyArray[0] = 0.15F; // One click is 15%
        difficultyArray[1] = 0.10F; // One click is 10%
        difficultyArray[2] = 0.05F; // One click is 05%
        rectangle.setFill(Color.RED);   // Initially Red, will turn Green if game is won!

        // Using AtomicReference, since easiest way I found to set default value...
        AtomicReference<Integer> difficulty = new AtomicReference<>(0); // By default, easy difficulty

        // Using AtomicReference, since no other way I found to not have negative Progress...
        AtomicReference<Float> decaySum  = new AtomicReference<>(0F);

        Disposable disposable1 = JavaFxObservable.actionEventsOf(difficultyMenu)
                .forEach(menuChoice -> {
                    ObservableList<Toggle> allMenus = toggleGroup.getToggles();
                    Integer index = allMenus.indexOf((Toggle) menuChoice.getTarget());
                    difficulty.getAndSet(index);
                });

        Observable<Integer> fillBarClicks =  JavaFxObservable.actionEventsOf(fillButton)
                .subscribeOn(Schedulers.computation())
                .map(accClicks -> 1)
                .scan(0,Integer::sum);  // Count the number of clicks in this sum
        
        Disposable disposable2 = JavaFxObservable.actionEventsOf(restartButton)
                .subscribeOn(Schedulers.io())
                .forEach(actionEvent -> {
                    rectangle.setFill(Color.RED);
                    decaySum.getAndSet(10.0F);
                });

        Observable<Float> fillBarDecay = Observable
                .interval(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .map(decay -> 0.010F);  // Decay increases every second by 10% of total progress

        // Collection here
        Observable<Pair<Integer,Float>> fillBarProgress = Observable
                .combineLatest(fillBarClicks,fillBarDecay, (nrClicks, decay) -> {
                    decaySum.updateAndGet(sum -> sum + decay);
                    Float clickSum = nrClicks * difficultyArray[difficulty.get()];
                    float currentProgress = clickSum - decaySum.get();
                    float v;
                    if (currentProgress >= 1.0F) {
                        v = 1.0F;  // The player won!
                    } else if (currentProgress <= 0.0F) {
                        decaySum.set(clickSum);
                        v = 0.0F;  // Cannot go below 0% progress
                    } else {
                        v = currentProgress;
                    }
                    return new Pair<>(nrClicks,v);
                });

        Disposable disposable3 = fillBarProgress
                .observeOn(JavaFxScheduler.platform()) // Updates to the UI are done on the JavaFx thread
                .subscribe(clicksAndProgress -> {
                    Integer nrOfClicks = clicksAndProgress.getKey();
                    Float currentProgress = clicksAndProgress.getValue();
                    bar.setProgress(currentProgress);   // Update the progressBar
                    rectangleLabel.setText("Number of clicks: "+nrOfClicks);
                    if (currentProgress == 1.0F) {
                        rectangle.setFill(Color.GREEN);
                        rectangleLabel.setText("You won with "+nrOfClicks+" number of clicks!");
                        decaySum.getAndSet(0F); // Stop the game by setting decay to 0
                    }
                });

        /* add disposables */
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.addAll(disposable1, disposable2, disposable3);
    }
}
