import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Team Abandoned Dashboard");
        stage.setWidth(1500);
        stage.setHeight(750);

        // Assemble full view
        VBox container = new VBox(new Label());
        container.setStyle("-fx-background-color:#6e6969;");
        container.setSpacing(5);
        container.setMaxWidth(stage.getMaxWidth() / 2);
        container.setMaxHeight(stage.getMaxHeight() / 2);

        /* ------------------------------------------------------------------------------------------------------ */

        ClockFeature clockFeature = new ClockFeature(); // initialize the ClockFeature
        HBox clockHBox = new HBox(new Label("Date & Clock"), clockFeature.digitalDateLabel, clockFeature.digitalClockLabel);
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
        WeatherFeature weatherFeature = new WeatherFeature();
        HBox weatherBox = new HBox(new Label("Weather Box"),
                weatherFeature.weatherLabel,
                weatherFeature.imageView
        );
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
        FillBarGameFeature fillBarGameFeature = new FillBarGameFeature();
        HBox fillBarGame = new HBox(
                new Label("Fill the Bar game!"),
                fillBarGameFeature.bar,
                fillBarGameFeature.fillButton,
                fillBarGameFeature.restartButton,
                fillBarGameFeature.rectangle,
                fillBarGameFeature.fillBarLabel,
                fillBarGameFeature.rectangleLabel,
                fillBarGameFeature.difficultyMenuBar
        );
        fillBarGame.setStyle("-fx-background-color:#505050;-fx-background-radius: 50px;");
        fillBarGame.setMaxWidth(1000);
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

        CryptoFeature cryptoFeature = new CryptoFeature();

        /* ------------------------------------------------------------------------------------------------------ */

        MemeFeature memeFeature = new MemeFeature();

        /* ------------------------------------------------------------------------------------------------------ */

        container.getChildren().addAll(clockHBox, weatherBox, fillBarGame);
        Scene scene = new Scene(container);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String ... args) {
        launch();
    }
}