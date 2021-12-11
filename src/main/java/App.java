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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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

        /* Observable sources from the backend */
        final int[] degrees = {15, 20, 25};
        Observable<Integer> degreesObservable = Observable
                .interval(5, TimeUnit.SECONDS) // Every 3 seconds, increments the number in the observable
                .map(Long::intValue) // Converts to int
                .map(i -> degrees[i % 3]);


        // Combines two observables
        Observable<String> nameWithTick = Observable
                .combineLatest(oddTicks, nameObservable, (currentTick, currentName) -> currentName + currentTick);

        Observable<String> weatherWithDegrees = Observable
                .combineLatest(weatherObservable, degreesObservable, (currentWeather, currentDegrees) -> "The weather now is: " + currentWeather + "; " + currentDegrees.toString() + "degrees");

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
        Label weatherWithDegreesLabel = new Label();


        // Observing observables values and reacting to new values by updating the UI components
        nameObservable
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
                .subscribe(nameLabel::setText);
        weatherWithDegrees
                .observeOn(JavaFxScheduler.platform())
                .subscribe(weatherWithDegreesLabel::setText);
      //  weatherObservable
      //          .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
      //          .subscribe(weatherNow -> weatherLabel.setText("\tNow the weather is " + weatherNow));
        weatherObservable
                .map(string -> string.toLowerCase() +".png")
                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
               // .subscribe(imageName::setText);
                .subscribe(name -> { imageName.setText(name);
                    try {FileInputStream input = new FileInputStream(imageName.getText());
                        Image png = new Image(input, 100, 100, false, false);
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
        Button button = new Button("Switch C° / F°");

        Label degreeType = new Label("C");
        String cel = new String("");
        Observable<Integer> clickDegrees = JavaFxObservable.actionEventsOf(button)
                .subscribeOn(Schedulers.computation()) // Switching thread
                .map(ae -> 1)
                .scan(0, (acc, newClick) -> (acc + newClick)%2);
               // .map(ae -> {
               //     cel = degreeType.getText();
               //     if (cel.equals("C")){ae::"F"} else {ae::"C";}
               // });
               // .scan(0, (acc, newClick) -> acc + newClick);

        Label degreeTypeLabel = new Label();
        Label degreeNumLabel = new Label("0");

     /*/   degreesObservable
                .observeOn(JavaFxScheduler.platform())
                .subscribe(degreeNumLabel::setText);
*/
        clickDegrees
                .observeOn(JavaFxScheduler.platform())
                .subscribe(clickDegreeID -> {
                    if (clickDegreeID == 0){
                        degreeTypeLabel.setText("F");
                        Integer n = Integer.valueOf(degreeNumLabel.getText());
                        n = n-2;
                        degreeNumLabel.setText(String.valueOf(n));
                    } else {degreeTypeLabel.setText("C");
                        Integer n = Integer.valueOf(degreeNumLabel.getText());
                        n = n+2;
                        degreeNumLabel.setText(String.valueOf(n));
                    }
                        }
                    //    degreeTypeLabel.setText(clickDegrees)
                );
     /*   degreesObservable
                .observeOn(JavaFxScheduler.platform())
                .subscribe(degree -> {
                            if (degreeTypeLabel.getText().equals("F")){
                                degreeNumLabel.setText(String.valueOf((degree-2)));} else {degreeNumLabel.setText(String.valueOf(degree));}
                        }
                        //    degreeTypeLabel.setText(clickDegrees)
                ); /*/
   //     degreesClickObservable
   //             .observeOn(JavaFxScheduler.platform())
   /*             .subscribe(obsList -> {
                            Integer clickStatus = obsList[0]
                            Integer degreeNum = obsList[1]
                            if (clickStatus==0){
                                degreeNumLabel.setText(String.valueOf((degreeNum-2)));} else {degreeNumLabel.setText(String.valueOf(degreeNum));}
                        }
                        //    degreeTypeLabel.setText(clickDegrees)
                );*/
        // Assemble full view
        VBox container = new VBox();
        container.setStyle("-fx-border-width: 16;");
        HBox nameWithTickBox = new HBox(nameLabel, plus, tickLabel, equals, nameWithTickLabel);
        HBox clicksBox = new HBox(button);

        HBox weatherBox = new HBox(weatherWithDegreesLabel, degreeTypeLabel, degreeNumLabel);
        VBox weatherImageBox = new VBox(weatherBox, imageView);

        container.getChildren().addAll(nameWithTickBox, clicksBox, weatherBox, weatherImageBox);

        Scene scene = new Scene(container, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String ... args) {
        launch();
    }



}
