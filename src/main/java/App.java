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
        VBox container = new VBox();
        container.setStyle("-fx-background-color:#6e6969;");
        container.setSpacing(20);   // Contents of Container are less cramped together
        container.setPadding(new Insets(20));

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
        clockBox.setPadding(new Insets(10));
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
        VBox weatherBox = new VBox(new Label("Weather Box"),
                weatherFeature.countryName,
                weatherFeature.weatherObjLabel,
                weatherFeature.imageView,
                weatherFeature.menuBar
        );
        weatherBox.setPrefSize(700,450);
        weatherBox.setSpacing(20);
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
        gameLabel.setStyle("-fx-font-size: 25;"+"-fx-text-fill: #b3c6ff;");
        HBox gameHBox0 = new HBox(
                gameLabel
        );
        HBox gameHBox1 = new HBox(
                fillBarGameFeature.textLabel
        );
        fillBarGameFeature.textLabel.setStyle("-fx-font-size: 20;"+"-fx-text-fill: #b3c6ff;");

        HBox gameHBox2 = new HBox(
                fillBarGameFeature.fillButton,
                fillBarGameFeature.restartButton,
                fillBarGameFeature.difficultyMenuBar
        );
        gameHBox2.setSpacing(20);

        HBox gameHBox3 = new HBox(
                fillBarGameFeature.bar,
                fillBarGameFeature.rectangle
        );
        gameHBox3.setSpacing(20);

        gameHBox0.setAlignment(Pos.BOTTOM_CENTER);
        gameHBox1.setAlignment(Pos.BOTTOM_CENTER);
        gameHBox2.setAlignment(Pos.BOTTOM_CENTER);
        gameHBox3.setAlignment(Pos.BOTTOM_CENTER);
        VBox gameVBox = new VBox(gameHBox0,gameHBox1,gameHBox2,gameHBox3);
        gameVBox.setMaxSize(600,300);
        gameVBox.setSpacing(15);
        gameVBox.setPadding(new Insets(10));
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
        cryptoFeature.idLabel.setStyle("-fx-font-size: 15;"+"-fx-text-fill: #0a0a0a;");
        cryptoFeature.nameLabel.setStyle("-fx-font-size: 15;"+"-fx-text-fill: #0a0a0a;");
        cryptoFeature.priceLabel.setStyle("-fx-font-size: 15;"+"-fx-text-fill: #0a0a0a;");
        cryptoFeature.trendLabel.setStyle("-fx-font-size: 15;"+"-fx-text-fill: #0a0a0a;");
        cryptoFeature.trendLabel.setWrapText(true);
        cryptoFeature.updateLabel.setStyle("-fx-font-size: 15;"+"-fx-text-fill: #0a0a0a;");
        cryptoFeature.infoLabel.setStyle("-fx-font-size: 15;"+"-fx-text-fill: #0a0a0a;");

        Label cryptoTitle = new Label("Cryptocurrencies!");
        cryptoTitle.setStyle("-fx-font-size: 20;"+"-fx-text-fill: #0a0a0a;");
        HBox cryptoBoxUpper = new HBox(cryptoTitle);
        cryptoBoxUpper.setAlignment(Pos.TOP_CENTER);

        VBox cryptoBoxLeft = new VBox(
                cryptoFeature.idLabel,
                cryptoFeature.nameLabel,
                cryptoFeature.priceLabel,
                cryptoFeature.trendLabel,
                cryptoFeature.updateLabel,
                cryptoFeature.infoLabel
        );
        cryptoBoxLeft.setSpacing(20);
        cryptoBoxLeft.setAlignment(Pos.CENTER_LEFT);

        VBox cryptoBoxRight = new VBox(
                cryptoFeature.imageView,
                cryptoFeature.textField
        );
        cryptoBoxRight.setSpacing(20);
        cryptoBoxRight.setAlignment(Pos.CENTER_RIGHT);

        HBox cryptoBoxMiddle = new HBox(cryptoBoxLeft,cryptoBoxRight);
        cryptoBoxMiddle.setSpacing(60);
        cryptoBoxMiddle.setPadding(new Insets(20));

        VBox cryptoVBox = new VBox(cryptoBoxUpper,cryptoBoxMiddle);
        cryptoVBox.setPrefSize(700,450);
        cryptoVBox.setSpacing(20);
        cryptoVBox.setStyle("-fx-background-color:#307f9a;-fx-background-radius: 10px;");
        cryptoVBox.setBorder(
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

        HBox middleBox = new HBox(weatherBox,cryptoVBox);
        middleBox.setAlignment(Pos.CENTER);
        middleBox.setSpacing(120);

        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(clockBox, middleBox, gameVBox);
        Scene scene = new Scene(container);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String ... args) {
        launch();
    }
}