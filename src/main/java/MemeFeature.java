import org.json.JSONArray;
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
       + Oprah: NR79:  28251713
       + Boat cat: NR76: 1367068
       - Negative:
       - Bernie: NR9: 222403160
       - grumpy cat: NR81: 405658
    */
//    static String uriGetMemes = "https://api.imgflip.com/get_memes";
//    MemeFeature() {
//        HttpRequest fetchMemes = HttpRequest.newBuilder()
//                .uri(URI.create(String.format(uriGetMemes)))
//                .build();
//        HttpClient httpClient = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_1_1)
//                .connectTimeout(Duration.ofSeconds(10))
//                .build();
//        httpClient.sendAsync(fetchMemes, HttpResponse.BodyHandlers.ofString())
//                .thenAcceptAsync(resp -> {
//                    try {
//                        JSONObject allMemes = new JSONObject(resp.body());
//                        JSONObject data = allMemes.getJSONObject("data");
//                        JSONArray memes = data.getJSONArray("memes");
//                        System.out.println(memes);
//                        for (var i=0;i<memes.length();i++) {
//                            System.out.println(i+"    "+memes.get(i));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//    }
}
