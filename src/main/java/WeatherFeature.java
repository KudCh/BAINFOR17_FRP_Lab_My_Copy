import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.util.Pair;
import org.json.JSONArray;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.pdfsam.rxjavafx.observables.JavaFxObservable;

public class WeatherFeature {

    private static String getWeatherURI = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=45059b7910230a3382b2cddbeac472fe&units=metric";
    private static String uriGetGeo = "https://freegeoip.app/json/";
    public Label weatherObjLabel = new Label();
    public ImageView imageView = new ImageView();
    public Label countryName = new Label();

    // create a label
    Label label1 = new Label("This is a ContextMenu example ");

    // create menuitems
    MenuItem menuItem1 = new RadioMenuItem("Luxembourg");
    MenuItem menuItem2 = new RadioMenuItem("Germany");
    MenuItem menuItem3 = new RadioMenuItem("France");
    MenuItem menuItem4 = new RadioMenuItem("Netherlands");
    ToggleGroup toggleGroup = new ToggleGroup();
    // create a tilepane
    TilePane tilePane = new TilePane(label1);

    Menu menu = new Menu("Menu countries");
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
                        JSONObject myObj = new JSONObject(resp.body());
                        JSONArray arr = myObj.getJSONArray("weather");
                        JSONObject data1 = arr.getJSONObject(0);
                        String weather = data1.getString("description");
                        String iconID = data1.getString("icon");

                        JSONObject data2 = myObj.getJSONObject("main");
                        Integer temperature = data2.getInt("temp");

                        Weather weatherObjj = new Weather(weather,temperature, iconID);

                        return weatherObjj;

                    } catch (JSONException e) { e.printStackTrace(); }
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
                        //  String latitude = json.getString("latitude");
                        //  String longitude = json.getString("longitude");
                        String countryName = json.getString("country_name");

                      /*  ArrayList<String> geoIP = new ArrayList<>();
                        geoIP.add(latitude);
                        geoIP.add(longitude);   */

                        // Pair<String, ArrayList<String>> geoInfo = new Pair<>(countryName, geoIP);
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

        RadioMenuItem menuItem1 = new RadioMenuItem("Germany");
        RadioMenuItem menuItem2 = new RadioMenuItem("Luxembourg");

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        toggleGroup.getToggles().addAll(menuItem1,menuItem2);
        menuItem1.setSelected(true);
        menuBar.getMenus().add(menu);

        Observable<Object> countryObservable = JavaFxObservable.actionEventsOf(menu)
                .subscribeOn(Schedulers.computation()) // Switching thread
                .map(ae -> {
                    ObservableList<MenuItem> allMenus = menu.getItems();
                    int index = allMenus.indexOf(ae.getTarget());
                    return countryList.get(index);
                });


        //  Observable<String> countryObservable = Observable.fromFuture(queryGeoIP(uriGetGeo));

        Observable<Weather> currentWeather = countryObservable
                .flatMap(country -> {
                    return Observable.fromFuture(queryWeather(getWeatherURI, (String) country));
                }); //format with countryName


//        Observable<Pair<String, Weather>> weatherAndCountryObs = Observable
//                .combineLatest(currentWeather, countryObservable, (weatherObject, country) ->{
//
//                    return new Pair<>(country, weatherObject); //countryName and weather data
//                });

//        weatherAndCountryObs
        currentWeather
                .observeOn(JavaFxScheduler.platform())
                .subscribe(p -> {

                    String country = "Lux";//p.getKey();
                    Weather weatherObject = p;//.getValue();

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