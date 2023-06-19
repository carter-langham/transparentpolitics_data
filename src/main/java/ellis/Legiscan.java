package ellis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Legiscan {

    private static final String API_KEY = System.getenv("LEGISCAN_API_KEY");
    public static final String[] GOOD_STATES = { "AL", "AR", "HI", "IL", "MD", "NE", "NV", "SD", "TN", "WY" };

    public static final String[] STATES = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID",
            "IL",
            "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM",
            "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI",
            "WY", "DC", "US" };

    private static String API_URL = "https://api.legiscan.com/?key=" + API_KEY
            + "&op=getSearch&state=UT&query=SB16&year=1";

    static String billName;
    static String state;

    static String session_id;
    static String state_id;
    // static ArrayList<String> fileNames = new ArrayList<>();

    public Legiscan() {

    }

    public Legiscan(String session_id, String state_id) {
        this.session_id = session_id;
        this.state_id = state_id;
    }

    public void setSessionID(String session_id) {
        this.session_id = session_id;
    }

    public void setStateID(String state_id) {
        this.state_id = state_id;
    }

    public void API() {

        API_URL = "https://api.legiscan.com/?key=" + API_KEY + "&op=getMasterList&id=" + session_id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Legiscan::prettyPrintJson)
                .thenAccept(Legiscan::writeToFile)
                .join();

        // System.out.println(API_URL);
    }

    private static void writeToFile(String data) {
        Path path = Paths.get("billFiles/" + state_id + "_" + session_id + ".json");
        System.out.println("billFiles/" + state_id + "_" + session_id + ".json");
        try {
            Files.writeString(path, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String prettyPrintJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonElement);
    }
}
