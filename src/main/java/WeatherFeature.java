import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.scene.control.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.pdfsam.rxjavafx.observables.JavaFxObservable;

public class WeatherFeature {

    private static String getWeatherURI = "http://api.openweathermap.org/data/2.5/weather?mode=xml&q=%s&appid=45059b7910230a3382b2cddbeac472fe&units=metric";
    private static String uriGetGeo = "https://freegeoip.app/json/";
    public Label weatherObjLabel = new Label();
    public ImageView imageView = new ImageView();
    public Label countryName = new Label();
    ToggleGroup toggleGroup = new ToggleGroup();
    Menu menu = new Menu("Choose country");
    MenuBar menuBar = new MenuBar();

    static CompletableFuture<Weather> queryWeather(String weatherURI, String countryName) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(String.format(weatherURI, countryName))).build();

        CompletableFuture<Weather> response = httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    try {
                        Document myXML = XPathClass.convertStringToXMLDocument(resp.body());
                        String pathWeatherDescription = new String("/current/weather/@value");
                        String pathIconID = new String("/current/weather/@icon");
                        String pathTemperature = new String("/current/temperature/@value");

                        String weather = XPathClass.evaluateXPath(myXML, pathWeatherDescription).get(0);
                        Float temperature = Float.valueOf(XPathClass.evaluateXPath(myXML, pathTemperature).get(0));
                        String iconID = XPathClass.evaluateXPath(myXML, pathIconID).get(0);

                        Weather weatherObjj = new Weather(weather,temperature, iconID, countryName);

                        return weatherObjj;

                    } catch (JSONException e) { e.printStackTrace(); } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
        return response;
    }

    static CompletableFuture<String> queryGeoIP(String uriGetLongLat) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(uriGetLongLat)).build();
        CompletableFuture<String> response = httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    try {
                        JSONObject json = new JSONObject(resp.body());
                        String countryName = json.getString("country_name");

                        return countryName;
                    } catch (Exception e) { e.printStackTrace(); }
                    return null;
                });
        return response;
    }

    public WeatherFeature() {
        try {
            countryName.setText(queryGeoIP(uriGetGeo).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ArrayList countryList = new ArrayList();
        countryList.add("Germany");
        countryList.add("Luxembourg");
        countryList.add("France");
        countryList.add("Belgium");

        RadioMenuItem menuItem1 = new RadioMenuItem("Germany");
        RadioMenuItem menuItem2 = new RadioMenuItem("Luxembourg");
        RadioMenuItem menuItem3 = new RadioMenuItem("France");
        RadioMenuItem menuItem4 = new RadioMenuItem("Belgium");

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        menu.getItems().add(menuItem3);
        menu.getItems().add(menuItem4);
        toggleGroup.getToggles().addAll(menuItem1,menuItem2, menuItem3,menuItem4);
       // menuItem1.setSelected(true);
        menuBar.getMenus().add(menu);




      //  Observable<String> countryObservable = Observable.fromFuture(queryGeoIP(uriGetGeo));

        Observable<String> countryObservable = JavaFxObservable.actionEventsOf(menu)
                .subscribeOn(Schedulers.computation())
                .map(ae -> {
                    MenuItem choice = (MenuItem) ae.getTarget();
                    System.out.println(choice.getText());
                    return choice.getText();
                });


        Observable<Weather> currentWeather = countryObservable
                .flatMap(country -> Observable.fromFuture(queryWeather(getWeatherURI, country)));

        currentWeather
                .observeOn(JavaFxScheduler.platform())
                .subscribe(weatherObject -> {

                    String country = weatherObject.countryName;

                    String description = weatherObject.weather;
                    String temp = String.valueOf(weatherObject.temp);

                    weatherObjLabel.setText(description + ", " + temp + "°C");
                    countryName.setText(country);

                    String iconID = weatherObject.iconID;
                    String imageSource = "http://openweathermap.org/img/wn/"+iconID+"@2x.png";
                    Image png = new Image(imageSource, 100, 100, false, false);
                    imageView.setImage(png);
                });




    }
}