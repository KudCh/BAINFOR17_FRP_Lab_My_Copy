import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.scene.control.Label;
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
    public Label cryptoText = new Label();
    public String[] cryptoCurrencies = {"DOGE","BTC","ETH"};

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
    // Rotate the three cryptocurrencies in list every 5 seconds
    public CryptoFeature(){
        Observable<String> availableCryptos = Observable
                .interval(0,5, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> cryptoCurrencies[i % cryptoCurrencies.length]);

        // Listen to 'availableCryptos' Observable and fetch data about given cryptocurrency
        Observable<String> cryptoObservable = availableCryptos
                .flatMap(currentCrypto -> Observable.fromFuture(getCrypto(currentCrypto)));

        // Extract relevant information from http response and modify the UI accordingly
        Disposable disposable = cryptoObservable
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cryptoObject-> {
                    // todo: try catch
                    JSONObject json = new JSONObject(cryptoObject).getJSONObject("asset");
                    String name = json.getString("name");
                    String price = json.getString("price");
                    String update = json.getString("updated_at");
                    String trendLastHour = json.getString("change_1h");
                    cryptoText.setText(name+" "+price+" "+update+" "+trendLastHour);
                });

        /* add disposables */
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }
}
