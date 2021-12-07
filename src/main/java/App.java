import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Observable<Long> digitalClock = Observable.interval(200,TimeUnit.MILLISECONDS);
        Label digitalClockLabel = new Label();
        digitalClockLabel.setTextFill(Color.BLUEVIOLET);
        digitalClock
                .observeOn(JavaFxScheduler.platform())
                .subscribe(currentTime -> digitalClockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        VBox container = new VBox();
        HBox clock = new HBox(digitalClockLabel);
        clock.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,CornerRadii.EMPTY, Insets.EMPTY)));
        clock.setMaxWidth(125);
        clock.prefHeight(10);

        Rectangle rec1 = new Rectangle(5, 5, 100, 80);
        rec1.setFill(Color.RED);
        rec1.setStroke(Color.GREEN);
        rec1.setStrokeWidth(3);
        rec1.onMouseClickedProperty().set((e) -> {
            System.out.println("Clicked rec1!");
        });

        Text someText = new Text("Hello world!");
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #ff9f00");
        Rectangle rec2 = new Rectangle(100.0d, 100.0d, 80, 100);
        rec2.setFill(Color.rgb(91, 127, 255));
        rec2.setStroke(Color.hsb(40, 0.7, 0.8));
        rec2.setStrokeWidth(3);
        rec2.onMouseEnteredProperty().set((e) -> {
            System.out.println("Mouse over rec2!");
        });
        pane.getChildren().addAll(rec1,rec2,someText);
        Group group = new Group(rec1,rec2);

        container.getChildren().addAll(clock,group,pane);
        stage.setTitle("Amazing dashboard!");
//        stage.initStyle(StageStyle.DECORATED);
        Scene scene = new Scene(container,1080,540);
//        scene.setFill(new LinearGradient(0,0,1,1,true,
//                CycleMethod.NO_CYCLE,
//                new Stop(0, Color.web("blue")),
//                new Stop(1, Color.web("purple"))
//        ));
        stage.setScene(scene);
        stage.show();
//        Stage myStage = new Stage();
////        Label aLabel = new Label("A test label");
////        Scene aScene = new Scene(aLabel,200,100);
////        stage.setScene(aScene);
////        stage.setHeight(500);
//        myStage.initModality(Modality.APPLICATION_MODAL); //block
////        stage.initStyle(StageStyle.DECORATED);
//        stage.setScene(scene); // only one scene per stage at one time
//        scene.setCursor(Cursor.OPEN_HAND); //cursor changes
//        VBox vBox2 = new VBox();
//        Scene secondScene = new Scene(vBox2);
//        myStage.setScene(secondScene);
//        myStage.showAndWait();
////        nodes???
////        fxml not importing???

//        Observable<Long> myTime = Observable
//                .interval(1, TimeUnit.SECONDS);
//        /* Observable sources from the backend */
//        Observable<Integer> oddTicks = Observable
//                .interval(1, TimeUnit.SECONDS) // Every 3 seconds, increments the number in the observable
//                .map(Long::intValue) // Converts to int
//                .filter(v -> v % 2 != 0 || v % 2 == 0); // Filters out even numbers
//        // Gets a different name every 2 seconds
//        final String[] names = {"Alice", "Bob", "Pierre", "Gabriel", "Manuel"};
//        Observable<String> nameObservable = Observable
//                .interval(1, TimeUnit.SECONDS)
//                .map(Long::intValue)
//                .map(i -> names[i % names.length]);
//        // Combines two observables
//        Observable<String> nameWithTick = Observable
//                .combineLatest(oddTicks, nameObservable, (currentTick, currentName) -> currentName + currentTick);
//        // Static labels
//        Label plus = new Label(" + ");
//        Label equals = new Label(" = ");
//        // Displaying changing data in the UI
//        Label nameLabel = new Label();
//        Label tickLabel = new Label();
//        Label nameWithTickLabel = new Label();
//        Label timeLabel = new Label();
//        // Observing observables values and reacting to new values by updating the UI components
//        nameObservable
//                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
//                .subscribe(nameLabel::setText);
//        oddTicks
//                .observeOn(JavaFxScheduler.platform())
//                .subscribe(currentTick -> tickLabel.setText(currentTick.toString()));
//        nameWithTick
//                .observeOn(JavaFxScheduler.platform())
//                .subscribe(nameWithTickLabel::setText);
//        /* Observable sources from the front end */
//        // Getting number of clicks on a button
//        Button button = new Button("Click");
////        ToggleButton button = new ToggleButton("Toggle Button Click!");
//
//        Observable<Integer> clicks = JavaFxObservable.actionEventsOf(button)
//                .subscribeOn(Schedulers.computation()) // Switching thread
//                .map(ae -> 1)
//                .scan(0, (acc, newClick) -> acc + newClick);
//
//        Label clicksLabel = new Label();
//        clicks
//                .observeOn(JavaFxScheduler.platform())
//                .subscribe(clickNumber -> {
//                    clicksLabel.setText("\tYou clicked " + clickNumber + " times");
//                    System.out.println(stage.getOpacity());
//                    Double newOpa = stage.getOpacity() - 0.05;
//                    stage.setOpacity(newOpa);
//                    boolean b = true;
//                    stage.setFullScreen(b);
//                });
//        // Assemble full view
//        VBox container = new VBox();
//        HBox nameWithTickBox = new HBox(nameLabel, plus, tickLabel, equals, nameWithTickLabel,timeLabel);
//        HBox clicksBox = new HBox(button, clicksLabel);
//        HBox timeBox = new HBox(timeLabel);
//        container.getChildren().addAll(nameWithTickBox, clicksBox, timeBox);
//        Scene scene = new Scene(container, 640, 480);
//        stage.setScene(scene);
//        stage.show();
//
//        /* Create welcome name */
//        Text text1 = new Text("I am text ");
//        text1.setStyle("-fx-font: normal bold 48px 'serif' ");
//        text1.setStroke(Color.BLUEVIOLET);
//        text1.setFill(Color.BLUEVIOLET);
//
//        HBox stringText = new HBox(text1);        // HBox creates row
//        stringText.setAlignment(Pos.CENTER);
//        stringText.setPrefHeight(75);
//        container.getChildren().addAll(stringText);
//
//        FlowPane p = new FlowPane();
//        Button b1 = new Button("B1");
//        Button b2 = new Button("B2");
//        Button b3 = new Button("B3");
//        p.getChildren().add(b1);
//        p.getChildren().add(b2);
//        p.getChildren().add(b3);
//        container.getChildren().addAll(p);
    }
    public static void main(String ... args) {
        launch();
    }
}