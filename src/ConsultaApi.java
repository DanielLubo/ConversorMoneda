import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ConsultaApi {

    private static final String apiKey = "d8809c48e3ac47d7146a0ce4";
    private static final String urlApi = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/";

    public static Map<String, Double> obtenerTasaCambio (String monedaBase){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlApi + monedaBase))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if(jsonObject.get("result").getAsString().equals("success")){
                JsonObject tasas = jsonObject.getAsJsonObject("conversion_rates");
                return gson.fromJson(tasas, Map.class);
            } else {
                throw new RuntimeException("Error en la consulta a la API");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al consultar la API", e);
        }
    }
}
