import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
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
import org.json.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class App extends Application {
    @Override
    public void start(Stage stage) {

        String URIGetLongLat = "https://freegeoip.app/json/";
        HttpRequest myReq = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URIGetLongLat)))
                .build();
        HttpClient myClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        CompletableFuture<HttpResponse<String>> resp = myClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString());
//        CompletableFuture<HttpResponse<String>> obj = asyncResp.thenApply(resp -> {
//            System.out.println(resp.body());
//            return resp;
//        });
        resp.thenAcceptAsync(res -> {
            try {
                JSONObject myObj = new JSONObject(res.body());
                System.out.println(myObj);
                String longitude = myObj.getString("longitude");
                String latitude = myObj.getString("latitude");
                System.out.println(longitude+" "+latitude);

                String URIGetWeather = String.format("https://www.7timer.info/bin/astro.php?lon=%s&lat=%s&ac=0&unit=metric&output=json&tzshift=0", longitude,latitude);
                System.out.println(URIGetWeather);

                HttpRequest myReq2 = HttpRequest.newBuilder()
                        .uri(URI.create(String.format(URIGetWeather)))
                        .build();
                CompletableFuture<HttpResponse<String>> resp2 = myClient.sendAsync(myReq2, HttpResponse.BodyHandlers.ofString());
                resp2.thenAcceptAsync(res2 -> {
                    try {
                        JSONObject myObj2 = new JSONObject(res2.body());
                        System.out.println(myObj2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        /* Observable gives the current time every 200 ms */
        stage.setTitle("Team Abandoned Dashboard");
        stage.setWidth(1500);
        stage.setHeight(750);
//        stage.setMaximized(true);

        /* ------------------------------------------------------------------------------------------------------ */

        Observable<Long> digitalClock = Observable.interval(200, TimeUnit.MILLISECONDS);
        /* Observable gives the current day every minute */
        Observable<Long> digitalDate = Observable.interval(1, TimeUnit.MINUTES);
        Label digitalClockLabel = new Label();
        Label digitalDateLabel = new Label();
        digitalDateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        digitalClockLabel.setTextFill(Color.rgb(166, 50, 248));
        digitalDateLabel.setTextFill(Color.rgb(166, 50, 150));
        digitalClock
                .observeOn(JavaFxScheduler.platform())
                .subscribe(e -> digitalClockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        digitalDate
                .observeOn(JavaFxScheduler.platform())
                .subscribe(e -> digitalDateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        /* ------------------------------------------------------------------------------------------------------ */

        /* Observable sources from the backend */
        final String[] weather = {"cloudy", "sunny"};
        Observable<String> weatherObservable = Observable
                .interval(5, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> weather[i % 2]);

        final int[] degrees = {15, 20, 25};
        Observable<Integer> degreesObservable = Observable
                .interval(5, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> degrees[i % 3]);

        Observable<String> weatherWithDegrees = Observable
                .combineLatest(weatherObservable, degreesObservable, (currentWeather, currentDegrees) ->
                        "The weather now is " + currentWeather + ", it's " + currentDegrees.toString() + " °C");

      //  Label weatherLabel = new Label();
        Image image;
        ImageView imageView = new ImageView();
        Label imageName = new Label();
        Label weatherWithDegreesLabel = new Label();

        weatherWithDegrees
                .observeOn(JavaFxScheduler.platform())
                .subscribe(weatherWithDegreesLabel::setText);
        weatherObservable
                .map(string -> "weatherIcons/"+ string.toLowerCase() +".png")
                .observeOn(JavaFxScheduler.platform())
                .subscribe(name -> {
                    imageName.setText(name);
                    try {FileInputStream input = new FileInputStream(imageName.getText());
                        Image png = new Image(input, 50, 50, false, false);
                        imageView.setImage(png);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
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
        Rectangle myRectangle = new Rectangle(50, 50);
        myRectangle.setFill(Color.RED);
        Label myRectangleLabel = new Label();

        Observable<Float> fillBarButtonClick = JavaFxObservable.actionEventsOf(fillBarButton)
                .subscribeOn(Schedulers.computation())
                .map(ae -> 0.05F)
                .map(increase -> {
                    clickCounter.updateAndGet(i -> i + 1);
                    Float currentProgress = progressFloat.get();
                    if (currentProgress + increase >= 1.0F) {
                        progressFloat.set(1.0F);
                    } else {
                        progressFloat.set(currentProgress + increase);
                    }
                    return (progressFloat.get());
                });
        Observable<Float> fillBarDecay = Observable
                .interval(250, TimeUnit.MILLISECONDS)
                .map(decay -> 0.02F)
                .map(decrease -> {
                    Float currentProgress = progressFloat.get();
//                    System.out.println(currentProgress);
                    if (currentProgress - decrease <= 0) {
                        progressFloat.set(0.0F);
                    } else {
                        progressFloat.set(currentProgress - decrease);
                    }
                    return progressFloat.get();
                });
        Label fillBarLabel = new Label();
        fillBarButtonClick
                .observeOn(JavaFxScheduler.platform())
                .subscribe(currentProgress -> {
                    fillBarBar.setProgress(currentProgress);
                    if (progressFloat.get() == 1.0F) {
                        myRectangle.setFill(Color.GREEN);
                        myRectangleLabel.setText("You win! It took: " + clickCounter.get() + " clicks!");
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
        TextField nameTextField = new TextField();
        AtomicReference<String> typedString = new AtomicReference<>(new String());
        Label textFieldLabel = new Label();
        nameTextField.onKeyTypedProperty().set(c -> {
            typedString.updateAndGet(v -> v + c.getCharacter());
            textFieldLabel.setText(typedString.get());
        });


        /* ------------------------------------------------------------------------------------------------------ */

        // Assemble full view
        VBox container = new VBox(new Label("Container"));
        container.setStyle("-fx-background-color:#6e6969;");
        container.setSpacing(5);
        container.setMaxWidth(stage.getMaxWidth() / 2);
        container.setMaxHeight(stage.getMaxHeight() / 2);

        /* ------------------------------------------------------------------------------------------------------ */

        HBox clockHBox = new HBox(new Label("Date & Digital clock"), digitalDateLabel, digitalClockLabel);
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

        HBox weatherBox;
        weatherBox = new HBox(new Label("Weather Box"), weatherWithDegreesLabel, imageView);
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

        HBox fillBarGame = new HBox(new Label("Fill the Bar game!"), fillBarBar, fillBarButton, fillBarLabel, myRectangle, myRectangleLabel);
        fillBarGame.setStyle("-fx-background-color:#505050;-fx-background-radius: 50px;");
        fillBarGame.setMaxWidth(600);
        fillBarGame.setSpacing(10);
        fillBarGame.setPadding(new Insets(20));
        fillBarGame.setTranslateX(stage.getWidth() / 4);
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

        HBox textHBox = new HBox(nameTextField, textFieldLabel);

        /* ------------------------------------------------------------------------------------------------------ */

        container.getChildren().addAll(clockHBox, weatherBox, nameWithTickBox, clicksBox, fillBarGame, textHBox);
        Scene scene = new Scene(container);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String ... args) {
        launch();
    }
}