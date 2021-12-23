import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class CryptoFeature {
    public String[] cryptoCurrencies = {"DOGE","BTC","ETH","BNB"};    // List with well known cryptocurrencies
    public ImageView imageView = new ImageView();
    public Label priceLabel = new Label();
    public Label idLabel = new Label();
    public Label nameLabel = new Label();
    public Label updateLabel = new Label();
    public Label trendLabel = new Label();

    // method to fetch fresh data for the given cryptocurrency
    static CompletableFuture<String> getCrypto(String someCrypto){
        String cryptoUrlBase = "https://www.cryptingup.com/api/assets/";
        String URIGetCrypto = cryptoUrlBase + someCrypto;

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest myCryptoReq = HttpRequest.newBuilder()
                .uri(URI.create(URIGetCrypto))
                .build();

        return httpClient.sendAsync(myCryptoReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp -> CompletableFuture.completedFuture(resp.body())).join();
    }

    // Observable with time-interval
    // Rotate the three cryptocurrencies in list every couple of seconds
    CryptoFeature(){
        // CSS for 'imageview'
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, #000000, 25, 0, 0, 0);");

        Observable<String> availableCryptos = Observable
                .interval(0,10, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> cryptoCurrencies[i % cryptoCurrencies.length]);

        // Listen to 'availableCryptos' Observable and fetch data about given cryptocurrency
        Observable<String> cryptoObservable = availableCryptos
                .flatMap(currentCrypto -> Observable.fromFuture(getCrypto(currentCrypto)));

        // Extract relevant information from http response and modify the UI accordingly
        Disposable disposable = cryptoObservable
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cryptoObject-> {
                    try {
                        JSONObject json = new JSONObject(cryptoObject).getJSONObject("asset");
                        String id = json.getString("asset_id");
                        idLabel.setText(id);

                        String name = json.getString("name");
                        nameLabel.setText(name);

                        double price = json.getDouble("price");
                        priceLabel.setText(String.valueOf(price));

                        String update = json.getString("updated_at");
                        updateLabel.setText(update);

                        double trendLastHour = json.getDouble("change_1h");
                        trendLabel.setText(String.valueOf(trendLastHour));

                        // Images from local source are read and displayed depending on whether the cryptocurrency trend is upwards or downwards.
                        if (trendLastHour >= 0.0) {
                            imageView.setImage(new Image("file:CryptoJPGs/good_stonks.jpg",300,225,false,true));
                        }
                        else {
                            imageView.setImage(new Image("file:CryptoJPGs/bad_stonks.jpg" ,300,225,false,true));
                        }
                    }
                    catch (JSONException e) {
                        System.err.println("Exception occurred when marshalling JSON: "+e);
                    }
                });

        /* add disposables */
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }
}
