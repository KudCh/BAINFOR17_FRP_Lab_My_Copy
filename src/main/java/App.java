import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        stage.setTitle("Dashboard of Team Abandoned!");
        stage.setWidth(800);
        stage.setHeight(400);
        stage.setMaximized(true);

        // Container will contain all features of the dashboard
        VBox container = new VBox(new Label());
        container.setStyle("-fx-background-color:#6e6969;");
        container.setSpacing(30);   // Contents of Container are less cramped together

        /* ------------------------------------------------------------------------------------------------------ */

        ClockFeature clockFeature = new ClockFeature(); // initialize the ClockFeature
        Label clockLabel = new Label("Date & Clock:");
        HBox clockBox = new HBox(
                clockLabel,
                clockFeature.digitalDateLabel,
                clockFeature.digitalClockLabel
        );
        clockLabel.setStyle("-fx-font-size: 20;"+"-fx-text-fill: #2a52c9;");
        clockBox.setMaxWidth(400);
        clockBox.setSpacing(20);
        clockBox.setPadding(new Insets(20));
        clockBox.setStyle("-fx-background-color:#ffffff;");
        clockBox.setBorder(
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
        Label weatherLabel = new Label("Weather Feature:");
        FlowPane weatherBox = new FlowPane(
                weatherLabel,
                weatherFeature.weatherObjLabel
        );
        weatherBox.setHgap(20);
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
        Label gameLabel = new Label("Fill the Bar game!");
        gameLabel.setStyle("-fx-font-size: 40;"+"-fx-text-fill: #b3c6ff;");
        HBox gameHBox0 = new HBox(
                gameLabel
        );
        HBox gameHBox1 = new HBox(
                fillBarGameFeature.textLabel
        );
        fillBarGameFeature.textLabel.setStyle("-fx-font-size: 20;"+"-fx-text-fill: #b3c6ff;");

        HBox gameHBox2 = new HBox(
                fillBarGameFeature.difficultyMenuBar,
                fillBarGameFeature.fillButton,
                fillBarGameFeature.restartButton
        );
        gameHBox2.setSpacing(25);

        HBox gameHBox3 = new HBox(
                fillBarGameFeature.bar,
                fillBarGameFeature.rectangle
        );
        gameHBox3.setSpacing(25);

        gameHBox0.setAlignment(Pos.BOTTOM_CENTER);
        gameHBox1.setAlignment(Pos.BOTTOM_CENTER);
        gameHBox2.setAlignment(Pos.BOTTOM_CENTER);
        gameHBox3.setAlignment(Pos.BOTTOM_CENTER);
        VBox gameVBox = new VBox(gameHBox0,gameHBox1,gameHBox2,gameHBox3);

        gameVBox.setMaxWidth(750);
        gameVBox.setSpacing(20);
        gameVBox.setPadding(new Insets(20));
        gameVBox.setStyle("-fx-background-color:#505050;-fx-background-radius: 50px;");
        gameVBox.setBorder(
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

        Label cryptoTitle = new Label("Cryptocurrencies!");
        cryptoTitle.setStyle("-fx-font-size: 20;"+"-fx-text-fill: #e9ff4b;");
        HBox crypto = new HBox(
                cryptoTitle,
                cryptoFeature.imageView,
                cryptoFeature.idLabel,
                cryptoFeature.nameLabel,
                cryptoFeature.priceLabel,
                cryptoFeature.updateLabel,
                cryptoFeature.trendLabel
        );

        /* ------------------------------------------------------------------------------------------------------ */

        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(clockBox, weatherBox, crypto, gameVBox);
        Scene scene = new Scene(container);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String ... args) {
        launch();
    }
}