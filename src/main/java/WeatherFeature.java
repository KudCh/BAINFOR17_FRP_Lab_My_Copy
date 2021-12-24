import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.scene.control.*;
import org.json.JSONObject;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.w3c.dom.Document;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;

public class WeatherFeature {

    private static final String getWeatherURI = "http://api.openweathermap.org/data/2.5/weather?mode=xml&q=%s&appid=45059b7910230a3382b2cddbeac472fe&units=metric";
    public Label weatherObjLabel = new Label();
    public ImageView imageView = new ImageView();
    public Label countryName = new Label();
    public Label localWeatherObjLabel = new Label();
    public ImageView localImageView = new ImageView();
    public Label localCountryName = new Label();
    ToggleGroup toggleGroup = new ToggleGroup();
    Menu menu = new Menu("Choose location");
    MenuBar menuBar = new MenuBar();


    RadioMenuItem menuItem1 = new RadioMenuItem("Esch-sur-Alzette");
    RadioMenuItem menuItem2 = new RadioMenuItem("Arlon");
    RadioMenuItem menuItem3 = new RadioMenuItem("Strasbourg");
    RadioMenuItem menuItem4 = new RadioMenuItem("Trier");

    static CompletableFuture<Weather> queryWeather(String countryName) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(String.format(WeatherFeature.getWeatherURI, countryName))).build();

        return httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    try {
                        Document myXML = XPathClass.convertStringToXMLDocument(resp.body());
                        String pathWeatherDescription = "/current/weather/@value";
                        String pathIconID = "/current/weather/@icon";
                        String pathTemperature = "/current/temperature/@value";

                        String weather = XPathClass.evaluateXPath(myXML, pathWeatherDescription).get(0);
                        Float temperature = Float.valueOf(XPathClass.evaluateXPath(myXML, pathTemperature).get(0));
                        String iconID = XPathClass.evaluateXPath(myXML, pathIconID).get(0);

                        return new Weather(weather,temperature, iconID, countryName);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    static CompletableFuture<String> queryGeoIP(String uriGetLongLat) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(uriGetLongLat)).build();
        return httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    try {
                        JSONObject json = new JSONObject(resp.body());

                        return json.getString("country_name");
                    } catch (Exception e) { e.printStackTrace(); }
                    return null;
                });
    }

    static void observingWeather(Observable<String> countryObs, Label weather, Label countryName, ImageView imageView){

        Observable<Weather> currentWeather = countryObs
                .flatMap(country -> Observable.fromFuture(queryWeather(country)));

        currentWeather
                .observeOn(JavaFxScheduler.platform())
                .subscribe(weatherObject -> {

                    String country = weatherObject.countryName;

                    String description = weatherObject.weather;
                    String temp = String.valueOf(weatherObject.temp);

                    weather.setText(description + ", " + temp + "°C");
                    countryName.setText(country);

                    String iconID = weatherObject.iconID;
                    String imageSource = "http://openweathermap.org/img/wn/"+iconID+"@2x.png";
                    Image png = new Image(imageSource, 100, 100, false, false);
                    imageView.setImage(png);
                });
    }

    public WeatherFeature() {

        String uriGetGeo = "https://freegeoip.app/json/";
        Observable<String> localCountryObservable = Observable.fromFuture(queryGeoIP(uriGetGeo));
        observingWeather(localCountryObservable, localWeatherObjLabel, localCountryName, localImageView);

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        menu.getItems().add(menuItem3);
        menu.getItems().add(menuItem4);
        toggleGroup.getToggles().addAll(menuItem1,menuItem2, menuItem3,menuItem4);
        menuBar.getMenus().add(menu);

        Observable<String> externalCountryObservable = JavaFxObservable.actionEventsOf(menu)
                .subscribeOn(Schedulers.computation())
                .map(ae -> {
                    MenuItem choice = (MenuItem) ae.getTarget();
                    return choice.getText();
                });
        observingWeather(externalCountryObservable, weatherObjLabel, countryName, imageView);

    }
}