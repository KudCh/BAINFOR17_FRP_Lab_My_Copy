import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* todo: finish everything */
public class WeatherFeature {

    static class Pair<A,B> {
        A latitude;
        B longitude;

        Pair(A latitude, B longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /* todo: expand cities hashmap or automate it */
    HashMap<String, Pair<Double,Double>> citiesHashMap = new HashMap<>() {{
        put("Tokyo", new Pair<>(35.6879, 139.6922));
        put("New York", new Pair<>(40.6943, -73.9249));
        put("Mumbai", new Pair<>(18.9667, 72.8333));
    }};
    public Label weatherLabel = new Label();
    public ImageView imageView = new ImageView();
    WeatherFeature() {

    }
    static String uriGetLongLat = "https://freegeoip.app/json/";

    static JSONObject getCurrentLocation() {
        AtomicReference<JSONObject> currentWeather = null;
        HttpRequest fetchLongLatRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format(uriGetLongLat)))
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        httpClient.sendAsync(fetchLongLatRequest, HttpResponse.BodyHandlers.ofString())
                .thenAcceptAsync(resp -> {
                    try {
                        JSONObject jsonObject = new JSONObject(resp.body());
                        String latitude = jsonObject.getString("latitude");
                        String longitude = jsonObject.getString("longitude");
                        String uriFetchWeather = String.format("https://www.7timer.info/bin/astro.php?lon=%s&lat=%s&ac=0&unit=metric&output=json&tzshift=0", longitude,latitude);
                        HttpRequest fetchWeather = HttpRequest.newBuilder()
                                .uri(URI.create(String.format(uriFetchWeather)))
                                .build();
                        httpClient.sendAsync(fetchWeather, HttpResponse.BodyHandlers.ofString())
                                .thenAcceptAsync(weather -> {
                                   try {
                                       currentWeather.set(new JSONObject(weather.body()));
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }

                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        return currentWeather.get();
//        Observable<String> locationObs = Observable.interval(5, TimeUnit.SECONDS).flatMap();
//        Observable<String> currentWeather = locationObs
//                .flatMap(cityName -> {
//                    System.out.println(cityName);
//                    Observable.fromFuture(queryWeather(cityName));
//                });
//        CompletableFuture<HttpResponse<String>> resp = myClient.sendAsync(myReq, HttpResponse.BodyHandlers.ofString());
//        resp.thenAcceptAsync(res -> {
//                String URIGetWeather = String.format("https://www.7timer.info/bin/astro.php?lon=%s&lat=%s&ac=0&unit=metric&output=json&tzshift=0", longitude,latitude);
//                HttpRequest myReq2 = HttpRequest.newBuilder().uri(URI.create(String.format(URIGetWeather))).build();
//                CompletableFuture<HttpResponse<String>> resp2 = myClient.sendAsync(myReq2, HttpResponse.BodyHandlers.ofString());
//                resp2.thenAcceptAsync(res2 -> {
//                        JSONObject myObj2 = new JSONObject(res2.body());
//                        System.out.println(myObj2);
    }
}
    //        JSONObject test = WeatherFeature.getCurrentLocation();
//        System.out.println(test);
//        String uriGetLongLat = "https://freegeoip.app/json/";
//        HttpRequest getLongLatRequest = HttpRequest.newBuilder()
//                .uri(URI.create(String.format(uriGetLongLat)))
//                .build();
//        HttpClient httpClient = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_2)
//                .connectTimeout(Duration.ofSeconds(5))
//                .build();
//    final String[] weather = {"CLOUDY", "SUNNY"};
//    Observable<String> weatherObservable = Observable
//            .interval(5, TimeUnit.SECONDS)
//            .map(Long::intValue) // Converts to int
//            .map(i -> weather[i % 2]);
//
//    //TODO: Subscribe for text and imageview to the same observable -> they are interlinked
//    Observable<String> weatherImageObservable = Observable
//            .interval(5, TimeUnit.SECONDS)
//            .map(Long::intValue) // Converts to int
//            .map(i -> "weatherIcons/" + (weather[i % 2].toLowerCase()) + ".png");

//        weatherObservable
//                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
//                .subscribe(weatherNow -> weatherLabel.setText("\tNow the weather is " + weatherNow));
//                weatherImageObservable
//                .observeOn(JavaFxScheduler.platform()) // Updates of the UI need to be done on the JavaFX thread
//                .subscribe(imgName -> {
//                FileInputStream input = new FileInputStream(imgName); //TODO: Try catch block -> if img cannot be found in folder
//                Image img = new Image(input);
//                imageView.setImage(img);
//                imageView.setFitHeight(50);
//                imageView.setFitWidth(50);
//                });