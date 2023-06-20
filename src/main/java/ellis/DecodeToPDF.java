package ellis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

public class DecodeToPDF {

    public static Dotenv dotenv = Dotenv.load();
    public static ArrayList<Integer> billIDList = new ArrayList<>();
    public static String API_KEY = null;
    public static HashMap<Integer, String> billDocs = new HashMap<>();

    public DecodeToPDF() {
        Dotenv dotenv = Dotenv.load();

        API_KEY = dotenv.get("LEGISCAN_API_KEY");
    }

    public void printBillIDList() {
        for (int i = 0; i < Main.allData.size(); i++) {
            if (!(Main.allData.get(i)[6].equals("**EMPTY**") || Main.allData.get(i)[6].equals("**UNKNOWN**"))) {
                billIDList.add(Integer.parseInt(Main.allData.get(i)[6]));
            }

        }
        // System.out.println(billIDList.size());
    }

    public void createTextFiles() {
        for (int i = 0; i < billIDList.size(); i++) {

            int billId = billIDList.get(i);
            String urlString = "https://api.legiscan.com/?key=" + API_KEY + "&op=getBillText&id=" + billId;

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    // Save JSON to file
                    String filePath = "billTextEncoded/ENCODED_BILL_TEXT_" + billId + ".json";
                    Files.write(Paths.get(filePath), content.toString().getBytes(), StandardOpenOption.CREATE);

                    // Close connections
                    in.close();
                    conn.disconnect();

                    // System.out.println(urlString);
                } else {
                    System.out.println("GET request not worked");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveHashValues() throws IOException {
        // Map<Integer, String> billDocs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("billTextEncoded"))) {
            for (Path path : directoryStream) {
                JsonNode jsonNode = objectMapper.readTree(path.toFile());
                int billId = jsonNode.get("text").get("bill_id").asInt();
                String doc = jsonNode.get("text").get("doc").asText();
                billDocs.put(billId, doc);
            }
        }
    }

    public void convertToPDF() throws IOException {

        try {
            Files.walk(Paths.get("billText/")).filter(Files::isRegularFile).forEach(filePath -> {
                System.out.println("Processing file: " + filePath.toString()); // Debug print
                try {
                    File file = filePath.toFile();
                    FileInputStream fis = new FileInputStream(file);
                    JSONObject jsonObject = new JSONObject(new JSONTokener(fis));

                    if (jsonObject.has("text")) {
                        JSONObject textObject = jsonObject.getJSONObject("text");

                        if (textObject.has("mime") && textObject.has("doc")) {
                            String mime = textObject.getString("mime");
                            String b64 = textObject.getString("doc");
                            byte[] decoder = Base64.getDecoder().decode(b64);

                            if (mime.equals("text/html")) {
                                // Decode and save as HTML
                                File outputFile = new File("billTextDecoded/" + textObject.getInt("doc_id") + ".html");
                                String decoded = new String(decoder, StandardCharsets.UTF_8);
                                Files.write(outputFile.toPath(), decoded.getBytes(StandardCharsets.UTF_8));
                                System.out.println("HTML " + textObject.getInt("doc_id") + " Saved");
                            } else if (mime.contains("pdf")) {
                                // Decode and save as PDF
                                File outputFile = new File("billTextDecoded/" + textObject.getInt("doc_id") + ".pdf");
                                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                    fos.write(decoder);
                                    System.out.println("PDF " + textObject.getInt("doc_id") + " Saved");
                                }
                            } else {
                                System.out.println("Unhandled mime type: " + mime); // Debug print
                            }
                        } else {
                            System.out.println(
                                    "Missing 'mime' or 'doc' in 'text' object in file: " + filePath.toString()); // Debug
                                                                                                                 // print
                        }
                    } else {
                        System.out.println("Missing 'text' in file: " + filePath.toString()); // Debug print
                    }

                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * for (Map.Entry<Integer, String> billEntry : billDocs.entrySet()) {
         * // Decode the base64 PDF data
         * byte[] decodedPdf = Base64.getMimeDecoder().decode(billEntry.getValue());
         * 
         * try {
         * // Write the decoded PDF data to a file in the billText directory with the
         * // filename being the key
         * Files.write(Paths.get("billText/" + billEntry.getKey() + ".pdf"),
         * decodedPdf);
         * } catch (IOException e) {
         * System.err.println("Error writing to file: " + e.getMessage());
         * }
         * }
         * 
         */
    }

}
