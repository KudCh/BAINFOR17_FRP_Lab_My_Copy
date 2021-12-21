import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

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

//        //CompletableFuture<HttpResponse<String>>
//        CompletableFuture<Weather> response = httpClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString())
//                .thenApplyAsync(resp ->{
//                    myObj = new JSONObject(resp.body());
//                    try {
//                        JSONArray arr = myObj.getJSONArray("weather");
//                        JSONObject data1 = arr.getJSONObject(0);
//                        String weatherD = data1.getString("description");
//
//                        JSONObject data2 = myObj.getJSONObject("main");
//                        Integer temperature = data2.getInt("temp");
//
//                        Weather weatherObjj = new Weather(weatherD,temperature);
//
//                        return CompletableFuture.completedFuture(weatherObjj);
//
//                    } catch (JSONException e) { e.printStackTrace(); }
//                    return null;
//                })
//                .join();
        return null;
    }


    public WeatherFeature(){
        final String[] cities = {"Luxembourg", "New York"};
        Observable<String> cityObservable = Observable
                .interval(2, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> cities[i % cities.length]);



        Observable<Weather> currentWeather = cityObservable
                .flatMap(cityName -> Observable.fromFuture(queryWeather(cityName)));

        //Label weatherObjLabel = new Label();
        currentWeather
                .observeOn(JavaFxScheduler.platform())
                .subscribe(wObj -> {
                    try {
                        String descr = wObj.weather;
                        String temp = String.valueOf(wObj.temp);

                        weatherObjLabel.setText(descr + ", " + temp);
                    } catch (Exception e){}
                });

    }

}