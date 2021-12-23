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

public class FillBarGameFeature {
    public Button fillButton = new Button("Click fast to fill bar!");
    public ProgressBar bar = new ProgressBar(0); // ProgressBar must reach 100% to win
    public Button restartButton = new Button("Restart?");
    public Rectangle rectangle = new Rectangle(30,30);  // Visualizes if the player won
    public Label textLabel = new Label();
    public MenuBar difficultyMenuBar = new MenuBar();   // MenuBar and Menu to select difficulty of the game
    Menu difficultyMenu = new Menu("Game difficulty");
    RadioMenuItem difficultyEasy = new RadioMenuItem("Easy");
    RadioMenuItem difficultyMedium = new RadioMenuItem("Medium");
    RadioMenuItem difficultyHard = new RadioMenuItem("Hard");

    FillBarGameFeature() {
        fillButton.setStyle("-fx-background-color: #1da1fd;"+"-fx-border-color: #202d56;"); // css for fillButton
        bar.setStyle("-fx-background-color: #ffffff;" +
                     "-fx-border-color: #000000;" +
                     "-fx-accent: #6926de;"
                    );
        bar.setPrefSize(285,30);
        restartButton.setStyle("-fx-background-color: #1da1fd;"+"-fx-border-color: #202d56;"); // css for restartButton

        ToggleGroup toggleGroup = new ToggleGroup();    // Only one difficulty can be selected at a time due to the toggleGroup!
        toggleGroup.getToggles().addAll(difficultyEasy,difficultyMedium,difficultyHard);
        difficultyMenu.getItems().addAll(difficultyEasy,difficultyMedium,difficultyHard);
        difficultyMenuBar.getMenus().add(difficultyMenu);
        difficultyEasy.setSelected(true);   // Default difficulty is easy
        Float[] difficultyArray = new Float[3]; // Predefined difficulties below...
        difficultyArray[0] = 0.15F; // One click is 15% of total progress
        difficultyArray[1] = 0.10F; // One click is 10% of total progress
        difficultyArray[2] = 0.05F; // One click is 05% of total progress
        rectangle.setFill(Color.RED);   // Initially Red, will turn Green if game is won!

        // Using AtomicReference, since easiest way I found to set default value for difficulty...
        AtomicReference<Integer> difficulty = new AtomicReference<>(0); // By default, easy difficulty

        // Using AtomicReference, since no other way I found to not have negative Progress...
        AtomicReference<Float> decaySum  = new AtomicReference<>(0F);

        // JavaFxObservable to modify the difficulty based on the user input
        // the 'difficulty' list and 'allMenus' have the same order, allowing us to change difficulties
        Disposable disposable1 = JavaFxObservable.actionEventsOf(difficultyMenu)
                .forEach(menuChoice -> {
                    ObservableList<Toggle> allMenus = toggleGroup.getToggles();
                    Integer index = allMenus.indexOf((Toggle) menuChoice.getTarget());
                    difficulty.getAndSet(index);
                });

        // JavaFxObservable to sum the amount of times that the user has clicked the 'fillButton'
        Observable<Integer> fillBarClicks =  JavaFxObservable.actionEventsOf(fillButton)
                .subscribeOn(Schedulers.computation())  // Computation, since we sum the click action events
                .map(accClicks -> 1)
                .scan(0,Integer::sum);  // Sum clicks, with initial value = 0

        // JavaFxObservable listening to 'restartButton'
        // Resets game by coloring 'rectangle' red again and setting the progress to 0
        Disposable disposable2 = JavaFxObservable.actionEventsOf(restartButton)
                .subscribeOn(Schedulers.io())   // restartButton click is input event
                .forEach(actionEvent -> {
                    rectangle.setFill(Color.RED);
                    decaySum.getAndSet(10.0F);
                });

        // this Observable is the decay that "fights" against the user clicks
        // every 0.1 seconds the decay increases by 1% of total progress
        Observable<Float> fillBarDecay = Observable
                .interval(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .map(decay -> 0.010F);

        // Combine the player clicks and the decay ticks
        Observable<Pair<Integer,Float>> fillBarProgress = Observable
                .combineLatest(fillBarClicks,fillBarDecay, (nrClicks, decay) -> {
                    decaySum.updateAndGet(sum -> sum + decay);  // Sum decay ticks
                    Float clickSum = nrClicks * difficultyArray[difficulty.get()];  // Add appropriate difficulty
                    float currentProgress = clickSum - decaySum.get();
                    if (currentProgress >= 1.0F) {
                        currentProgress = 1.0F;  // The player won!
                    } else if (currentProgress <= 0.0F) {   // We do not want the progress to go below 0
                        decaySum.set(clickSum);  // Set the 'decaySum' equal to the 'clickSum' -> difference is 0
                        currentProgress = 0.0F;  // Set progress to 0
                    }
                    return new Pair<>(nrClicks,currentProgress);
                });

        // observe Observable 'fillBarProgress'
        // input: Pair<clicks,progress>
        Disposable disposable3 = fillBarProgress
                .observeOn(JavaFxScheduler.platform()) // Updates to the UI are done on the JavaFx thread
                .subscribe(clicksAndProgress -> {
                    Integer nrOfClicks = clicksAndProgress.getKey();    // Extract number of clicks
                    Float currentProgress = clicksAndProgress.getValue();   // Extract current progress
                    bar.setProgress(currentProgress);   // Update the progressBar
                    textLabel.setText("Number of clicks: "+nrOfClicks);

                    // Case: player won
                    if (currentProgress == 1.0F) {
                        rectangle.setFill(Color.GREEN); // Visualize win
                        textLabel.setText("You won with "+nrOfClicks+" number of clicks!"); // Indicate to the player required clicks
                        decaySum.getAndSet(0F); // Stop the decay/game by setting it to 0
                    }
                });

        /* add disposables to indicate that we do not need the output of subscribe */
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.addAll(disposable1, disposable2, disposable3);
    }
}
