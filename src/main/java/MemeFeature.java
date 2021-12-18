import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MemeFeature {
    // Whenever the displayed cryptocurrency goes down --> negative meme at random.
    // Whenever the displayed cryptocurrency goes up --> positive meme at random.
    // Get MEMES: https://api.imgflip.com/get_memes/
    /* + Positive:
       + Oprah: 28251713
       + Boat cat: 1367068
       - Negative:
       - Bernie: 222403160
       - grumpy cat: 405658
    */
    static String uriGetMemes = "https://api.imgflip.com/get_memes/";
    MemeFeature() {
        HttpRequest fetchMemes = HttpRequest.newBuilder()
                .uri(URI.create(String.format(uriGetMemes)))
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        httpClient.sendAsync(fetchMemes, HttpResponse.BodyHandlers.ofString())
                .thenAcceptAsync(resp -> {
                    try {
//                        JSONObject jsonObject = new JSONObject(resp.body());
//                        System.out.println(jsonObject);
                        System.out.println(resp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
