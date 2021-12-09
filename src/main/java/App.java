import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        /* Observable gives the current time every 200 ms */
        stage.setTitle("Team Abandoned Dashboard");
        stage.setWidth(1500);
        stage.setHeight(750);
//        stage.setMaximized(true);

        /* ------------------------------------------------------------------------------------------------------ */

        Observable<Long> digitalClock = Observable.interval(200,TimeUnit.MILLISECONDS);
        /* Observable gives the current day every minute */
        Observable<Long> digitalDate = Observable.interval(1,TimeUnit.MINUTES);
        Label digitalClockLabel = new Label();
        Label digitalDateLabel = new Label();
        digitalDateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        digitalClockLabel.setTextFill(Color.rgb(166,50,248));
        digitalDateLabel.setTextFill(Color.rgb(166,50,150));
        digitalClock
                .observeOn(JavaFxScheduler.platform())
                .subscribe(e -> digitalClockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        digitalDate
                .observeOn(JavaFxScheduler.platform())
                .subscribe(e -> digitalDateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        /* ------------------------------------------------------------------------------------------------------ */

        final String[] weather = {"CLOUDY", "SUNNY"};
        Observable<String> weatherObservable = Observable
                .interval(5, TimeUnit.SECONDS)
                .map(Long::intValue) // Converts to int
                .map(i -> weather[i % 2]);

        //TODO: Subscribe for text and imageview to the same observable -> they are interlinked
        Observable<String> weatherImageObservable = Observable
                .interval(5, TimeUnit.SECONDS)
                .map(Long::intValue) // Converts to int
                .map(i -> weather[i%2])
                .map(name -> "weatherIcons/"+(name.toLowerCase())+ ".png");

        Label weatherLabel = new Label();
        ImageView imageView = new ImageView();

        weatherObservable
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(weatherNow -> weatherLabel.setText("\tNow the weather is " + weatherNow));
        weatherImageObservable
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(imgName -> {
//                    System.out.println(imgName);
                    FileInputStream input = new FileInputStream(imgName); //TODO: Try catch block -> if img cannot be found in folder
                    Image img = new Image(input);
                    imageView.setImage(img);
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                });

        /* ------------------------------------------------------------------------------------------------------ */

        /* Observable sources from the backend */
        Observable<Integer> oddTicks = Observable
                .interval(3, TimeUnit.SECONDS) // Every 3 seconds, increments the number in the observable
                .map(Long::intValue) // Converts to int
                .filter(v -> v % 2 != 0); // Filters out even numbers

        // Gets a different name every 2 seconds
        final String[] names = {"Alice", "Bob", "Pierre", "Gabriel", "Manuel"};
        Observable<String> nameObservable = Observable
                .interval(2, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> names[i % names.length]);

        // Combines two observables
        Observable<String> nameWithTick = Observable
                .combineLatest(oddTicks, nameObservable, (currentTick, currentName) -> currentName + currentTick);

        // Static labels
        Label plus = new Label(" + ");
        Label equals = new Label(" = ");
        // Displaying changing data in the UI
        Label nameLabel = new Label();
        Label tickLabel = new Label();
        Label nameWithTickLabel = new Label();

        // Observing observables values and reacting to new values by updating the UI components
        nameObservable
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(nameLabel::setText);
        oddTicks
                .observeOn(JavaFxScheduler.platform())
                .subscribe(currentTick -> tickLabel.setText(currentTick.toString()));
        nameWithTick
                .observeOn(JavaFxScheduler.platform())
                .subscribe(nameWithTickLabel::setText);

        /* Observable sources from the front end */
        // Getting number of clicks on a button
        Button button = new Button("Click");

        Observable<Integer> clicks = JavaFxObservable.actionEventsOf(button)
                .subscribeOn(Schedulers.computation()) // Switching thread
                .map(ae -> 1)
                .scan(0, Integer::sum);

        Label clicksLabel = new Label();
        clicks
                .observeOn(JavaFxScheduler.platform())
                .subscribe(clickNumber -> clicksLabel.setText("\tYou clicked " + clickNumber + " times"));

        /* ------------------------------------------------------------------------------------------------------ */

        ProgressBar fillBarBar = new ProgressBar(0);
        Button fillBarButton = new Button("Click fast to fill bar!");
        AtomicReference<Float> progressFloat = new AtomicReference<>(0.0F);
        AtomicReference<Integer> clickCounter = new AtomicReference<>(0);
        Rectangle myRectangle = new Rectangle(50,50);
        myRectangle.setFill(Color.RED);
        Label myRectangleLabel = new Label();

        Observable<Float> fillBarButtonClick = JavaFxObservable.actionEventsOf(fillBarButton)
                .subscribeOn(Schedulers.computation())
                .map(ae -> 0.05F)
                .map(increase -> {
                    clickCounter.updateAndGet(i -> i + 1);
                    Float currentProgress = progressFloat.get();
                    if (currentProgress+increase >= 1.0F) {
                        progressFloat.set(1.0F);
                    } else {
                        progressFloat.set(currentProgress+increase);
                    }
                    return (progressFloat.get());
                });
        Observable<Float> fillBarDecay = Observable
                .interval(250,TimeUnit.MILLISECONDS)
                .map(decay -> 0.02F)
                .map(decrease -> {
                    Float currentProgress = progressFloat.get();
//                    System.out.println(currentProgress);
                    if (currentProgress-decrease <= 0) {
                        progressFloat.set(0.0F);
                    }else {
                        progressFloat.set(currentProgress-decrease);
                    }
                    return  progressFloat.get();
                });
        Label fillBarLabel = new Label();
        fillBarButtonClick
                .observeOn(JavaFxScheduler.platform())
                .subscribe(currentProgress -> {
                    fillBarBar.setProgress(currentProgress);
                    if (progressFloat.get() == 1.0F) {
                        myRectangle.setFill(Color.GREEN);
                        myRectangleLabel.setText("You win! It took: "+clickCounter.get()+" clicks!");
                    }
                });
        fillBarDecay
                .observeOn(JavaFxScheduler.platform())
                .subscribe(decay -> {
                    fillBarBar.setProgress(decay);
                    if (progressFloat.get() == 0.0F) {
                        myRectangle.setFill(Color.RED);
                        myRectangleLabel.setText("Try again?");
                        clickCounter.set(0);
                    }
                });
        myRectangle.onMouseClickedProperty().set(e -> {
            myRectangle.setFill(Color.RED);
            progressFloat.set(0.0F);
            clickCounter.set(0);
        });

        /* ------------------------------------------------------------------------------------------------------ */

        /* Next feature: ... */
        //TODO: Implement new feature


        /* ------------------------------------------------------------------------------------------------------ */

        // Assemble full view
        VBox container = new VBox(new Label("Container"));
        container.setStyle("-fx-background-color:#6e6969;");
        container.setSpacing(5);
        container.setMaxWidth(stage.getMaxWidth()/2);
        container.setMaxHeight(stage.getMaxHeight()/2);

        /* ------------------------------------------------------------------------------------------------------ */

        HBox clockHBox = new HBox(new Label("Date & Digital clock"),digitalDateLabel, digitalClockLabel);
        clockHBox.setTranslateX(10);
        clockHBox.setSpacing(20);
        clockHBox.setMaxWidth(420);
        clockHBox.setPadding(new Insets(20));
        clockHBox.setStyle("-fx-background-color:#ffffff;");
        clockHBox.setBorder(
                new Border(
                        new BorderStroke(
                                Color.BLACK,
                                new BorderStrokeStyle(
                                        StrokeType.INSIDE,
                                        StrokeLineJoin.MITER,
                                        StrokeLineCap.BUTT,
                                        10,
                                        0,
                                        null
                                ),
                                new CornerRadii(0),
                                new BorderWidths(8)
                        )
                )
        );

        /* ------------------------------------------------------------------------------------------------------ */

        HBox weatherBox = new HBox(new Label("Weather Box"),weatherLabel, imageView);
        weatherBox.setTranslateX(10);
        weatherBox.setSpacing(20);
        weatherBox.setMaxWidth(420);
        weatherBox.setMinHeight(75);
        weatherBox.setStyle("-fx-background-color:#9dd6ea;-fx-background-radius: 10px;");
        weatherBox.setBorder(
                new Border(
                        new BorderStroke(
                                Color.BLACK,
                                new BorderStrokeStyle(
                                        StrokeType.INSIDE,
                                        StrokeLineJoin.MITER,
                                        StrokeLineCap.BUTT,
                                        5,
                                        0,
                                        null
                                ),
                                new CornerRadii(10),
                                new BorderWidths(2)
                        )
                )
        );

        /* ------------------------------------------------------------------------------------------------------ */

        HBox nameWithTickBox = new HBox(nameLabel, plus, tickLabel, equals, nameWithTickLabel);

        /* ------------------------------------------------------------------------------------------------------ */

        HBox clicksBox = new HBox(button, clicksLabel);

        /* ------------------------------------------------------------------------------------------------------ */

        HBox fillBarGame = new HBox(new Label("Fill the Bar game!"), fillBarBar, fillBarButton, fillBarLabel,myRectangle,myRectangleLabel);
        fillBarGame.setStyle("-fx-background-color:#505050;-fx-background-radius: 50px;");
        fillBarGame.setMaxWidth(600);
        fillBarGame.setSpacing(10);
        fillBarGame.setPadding(new Insets(20));
        fillBarGame.setTranslateX(stage.getWidth()/4);
        fillBarGame.setBorder(
                new Border(
                        new BorderStroke(
                                Color.DARKGOLDENROD,
                                new BorderStrokeStyle(
                                        StrokeType.CENTERED,
                                        StrokeLineJoin.ROUND,
                                        StrokeLineCap.ROUND,
                                        10,
                                        0,
                                        null
                                ),
                                new CornerRadii(50),
                                new BorderWidths(8)
                        )
                )
        );

        /* ------------------------------------------------------------------------------------------------------ */

        container.getChildren().addAll(clockHBox, weatherBox, nameWithTickBox, clicksBox, fillBarGame);
        Scene scene = new Scene(container);
        stage.setScene(scene);
        stage.show();
}
    public static void main(String ... args) {
        launch();
    }
}
