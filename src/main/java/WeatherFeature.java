import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WeatherFeature {

    private static String getWeatherURI = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=45059b7910230a3382b2cddbeac472fe&units=metric";
    private static JSONObject myObj;
    public Label weatherObjLabel = new Label();


    static CompletableFuture<Weather> queryWeather(String cityName) {

        String URIGetWeather = String.format(getWeatherURI, cityName);

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest myReq = HttpRequest.newBuilder().uri(URI.create(String.format(URIGetWeather))).build();

        //CompletableFuture<HttpResponse<String>>
        CompletableFuture<Weather> response = httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    try {
                        myObj = new JSONObject(resp.body());
                        JSONArray arr = myObj.getJSONArray("weather");
                        JSONObject data1 = arr.getJSONObject(0);
                        String weatherD = data1.getString("description");
                        String iconID = data1.getString("icon");

                        JSONObject data2 = myObj.getJSONObject("main");
                        Integer temperature = data2.getInt("temp");

                        Weather weatherObjj = new Weather(weatherD,temperature, iconID);

                        return CompletableFuture.completedFuture(weatherObjj);

                    } catch (JSONException e) { e.printStackTrace(); }
                    return null;
                })
                .join();
        return response;
    }

    ImageView imageView = new ImageView();
    Label cityName = new Label();

    public WeatherFeature(){
        final String[] cities = {"Luxembourg", "Berlin","New+York"};
        Observable<String> cityObservable = Observable
                .interval(3, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> cities[i % cities.length]);



        Observable<Weather> currentWeather = cityObservable
                .flatMap(cityName -> Observable.fromFuture(queryWeather(cityName)));


        Observable<Pair<Weather,String>> wetherAndCityObs = Observable
                .combineLatest(currentWeather, cityObservable, (wObj, city) ->{

                    return new Pair<>(wObj,city);
                });
        wetherAndCityObs
                .observeOn(JavaFxScheduler.platform())
                .subscribe(p -> {

                    Weather wObj = p.getKey();
                    String city = p.getValue();

                    String descr = wObj.weather;
                    String temp = String.valueOf(wObj.temp);

                    weatherObjLabel.setText(descr + ", " + temp + "°C");
                    cityName.setText(city);

                    String iconID = wObj.iconID;
                    String imageSource = "http://openweathermap.org/img/wn/"+iconID+"@2x.png";
                    Image png = new Image(imageSource, 100, 100, false, false);
                    imageView.setImage(png);
                });


    }

}