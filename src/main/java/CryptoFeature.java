import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CryptoFeature {
    public Label cryptoLabel = new Label("Crypto Currencies");
    public TextArea cryptoText = new TextArea("SomeText!!!!");
    public String[] cryptoCurrencies = {"DOGE","BTC","ETH"}; // Automate?
    private static String cryptoUrl = "https://www.cryptingup.com/api/assets/";

    static CompletableFuture<String> getCrypto(String someCrypto){
        String URIGetCrypto = cryptoUrl+someCrypto;
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest myCryptoReq = HttpRequest.newBuilder().uri(URI.create(URIGetCrypto)).build();

        CompletableFuture<String> response = httpClient.sendAsync(myCryptoReq, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(resp ->{
                    return CompletableFuture.completedFuture(resp.body());
                }).join();
        return response;
    }

    // Display course and trend of these three cryptocurrencies -> perhaps rotate
    public CryptoFeature(){
        Observable<String> availableCryptos = Observable
                .interval(2,5, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> cryptoCurrencies[i % cryptoCurrencies.length]);

        Observable<String> cryptoObservable = availableCryptos
                .flatMap(currentCrypto -> Observable.fromFuture(getCrypto(currentCrypto)));

        cryptoObservable
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cryptoJson-> {
                    cryptoLabel.setText("123");
                });
    }
}
