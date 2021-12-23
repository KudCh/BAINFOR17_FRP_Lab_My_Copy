import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class WeatherFeature {

    private static String getWeatherURI = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=45059b7910230a3382b2cddbeac472fe&units=metric";
    private static String uriGetGeo = "https://freegeoip.app/json/";
    public Label weatherObjLabel = new Label();
    public ImageView imageView = new ImageView();
    public Label countryName = new Label();

    static CompletableFuture<Weather> queryWeather(String weatherURI, String latitude, String longitude) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(String.format(weatherURI, latitude, longitude))).build();

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

    static CompletableFuture<Pair<String, ArrayList<String>>> queryGeoIP(String uriGetLongLat) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(uriGetLongLat)).build();
        CompletableFuture<Pair<String, ArrayList<String>>> response = httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    try {
                        JSONObject json = new JSONObject(resp.body());
                        String latitude = json.getString("latitude");
                        String longitude = json.getString("longitude");
                        String countryName = json.getString("country_name");

                        ArrayList<String> geoIP = new ArrayList<>();
                        geoIP.add(latitude);
                        geoIP.add(longitude);

                        Pair<String, ArrayList<String>> geoInfo = new Pair<>(countryName, geoIP);

                        return geoInfo;
                    } catch (Exception e) { e.printStackTrace(); }
                    return null;
                });
        return response;
    }

    public WeatherFeature(){
        Observable<Pair<String, ArrayList<String>>> geoObservable = Observable.fromFuture(queryGeoIP(uriGetGeo));

        Observable<Weather> currentWeather = geoObservable
                .flatMap(geoIP -> Observable.fromFuture(queryWeather(getWeatherURI, geoIP.getValue().get(0), geoIP.getValue().get(1)))); //format with latitude and longitude


        Observable<Pair<String, Weather>> weatherAndCityObs = Observable
                .combineLatest(currentWeather, geoObservable, (weatherObject, geoData) ->{

                    return new Pair<>(geoData.getKey(), weatherObject); //countryName and weather data
                });

        weatherAndCityObs
                .observeOn(JavaFxScheduler.platform())
                .subscribe(p -> {

                    String country = p.getKey();
                    Weather weatherObject = p.getValue();

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