import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONException;
import org.json.JSONObject;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CryptoFeature {
    public ArrayList<String> cryptoCurrenciesList = new ArrayList<>();
    public TextField textField = new TextField("Enter ID to display!");
    public ImageView imageView = new ImageView();
    public Label priceLabel = new Label();
    public Label idLabel = new Label();
    public Label nameLabel = new Label();
    public Label updateLabel = new Label();
    public Label trendLabel = new Label();
    public Label infoLabel = new Label("Current list: \n BTC");

    // method to fetch fresh data for the given cryptocurrency
    static CompletableFuture<String> getCrypto(String someCrypto){
        String crypto = someCrypto.toUpperCase();   // Crypto IDs must be uppercase
        String cryptoUrlBase = "https://www.cryptingup.com/api/assets/%s";
        String URIGetCrypto = String.format(cryptoUrlBase, crypto);

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

    CryptoFeature(){
        cryptoCurrenciesList.add("BTC");    // BitCoin is shown by default

        // CSS for 'imageview'
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, #000000, 25, 0, 0, 0);");

        Observable<String> addCrypto = JavaFxObservable.actionEventsOf(textField)
                .map(enterWasPressed -> {
                    String content = textField.getText();
                    textField.clear();
                    return content;
                });

        // Observable with time-interval
        // Rotate the three cryptocurrencies in list every couple of seconds
        ObservableList<String> fxCollection = FXCollections.observableArrayList(cryptoCurrenciesList);
        fxCollection.addListener((ListChangeListener<String>) change -> {
            StringBuilder res = new StringBuilder("Input added to list! Current list: \n ");
            for (String s : fxCollection) res.append(s).append("\n");
            infoLabel.setText(res.toString());
        });

        Disposable disposable1 = addCrypto
                .observeOn(JavaFxScheduler.platform())
                .subscribe(enteredText-> {
                    fxCollection.add(enteredText);
                    if (Objects.equals(String.valueOf(enteredText), "clear")) {
                        fxCollection.clear();       // clear the arrayList
                        fxCollection.add("BTC");    // BitCoin as default again
                    }
                });

        Observable<String> availableCryptos = Observable
                .interval(0,8, TimeUnit.SECONDS)
                .map(Long::intValue)
                .map(i -> fxCollection.get(i%fxCollection.size())); // rotate the currencies in the list

        // Listen to 'availableCryptos' Observable and fetch data about given cryptocurrency
        Observable<String> cryptoObservable = availableCryptos
                .flatMap(currentCrypto -> Observable.fromFuture(getCrypto(currentCrypto)));

        // Extract relevant information from http response and modify the UI accordingly
        Disposable disposable2 = cryptoObservable
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
        compositeDisposable.addAll(disposable1,disposable2);
    }
}
