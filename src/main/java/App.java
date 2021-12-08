import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    @Override
    public void start(Stage stage) {

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

        /* Observable sources from the backend */
        final String[] weather = {"CLOUDY", "SUNNY"};
        Observable<String> weatherObservable = Observable
                .interval(5, TimeUnit.SECONDS) // Every 3 seconds, increments the number in the observable
                .map(Long::intValue) // Converts to int
                .map(i -> weather[i % 2]);


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
        Label weatherLabel = new Label();
        Image image;
        ImageView imageView = new ImageView();
        //String imageName = new String();
        Label imageName = new Label();


        // Observing observables values and reacting to new values by updating the UI components
        nameObservable
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(nameLabel::setText);
        weatherObservable
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(weatherNow -> weatherLabel.setText("\tNow the weather is " + weatherNow));
        weatherObservable
                .map(string -> string.toLowerCase() +".png")
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(name -> {
                    try {FileInputStream input = new FileInputStream(imageName.getText());
                        Image png = new Image(input);
                        imageView.setImage(png);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                });

        oddTicks
                .observeOn(JavaFxScheduler.platform())
                .subscribe(currentTick -> tickLabel.setText(currentTick.toString()));
        nameWithTick
                .observeOn(JavaFxScheduler.platform())
                .subscribe(nameWithTickLabel::setText);


//        String newName = new String(imageName.getText());




        /* Observable sources from the front end */
        // Getting number of clicks on a button
        Button button = new Button("Click");

        Observable<Integer> clicks = JavaFxObservable.actionEventsOf(button)
                .subscribeOn(Schedulers.computation()) // Switching thread
                .map(ae -> 1)
                .scan(0, (acc, newClick) -> acc + newClick);

        Label clicksLabel = new Label();
        clicks
                .observeOn(JavaFxScheduler.platform())
                .subscribe(clickNumber -> clicksLabel.setText("\tYou clicked " + clickNumber + " times"));

        // Assemble full view
        VBox container = new VBox();
        container.setStyle("-fx-border-width: 16;");
        HBox nameWithTickBox = new HBox(nameLabel, plus, tickLabel, equals, nameWithTickLabel);
        HBox clicksBox = new HBox(button, clicksLabel);
        HBox weatherBox = new HBox(weatherLabel, imageView);
        container.getChildren().addAll(nameWithTickBox, clicksBox, weatherBox);

        Scene scene = new Scene(container, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String ... args) {
        launch();
    }



}
