package uk.ac.ed.inf.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class to fetch data from the API
 */
public class ApiInvoker {
    // Create a client and objectMapper constant so we call only once each
    private static final HttpClient Client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Helper function to send a request to the API
     * @param uri the uri to send the request to
     * @return the response body as a string
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    private static String sendRequest(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        HttpResponse<String> response = Client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalArgumentException("\u001B[31mResponse code error: " + response.statusCode() + " for " + uri+"\u001B[0m");
        }
        return response.body();
    }

    /**
     * Helper function to check if the service is alive
     * @param url the url of the service
     * @return the response body as a string
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    public static String serviceAlive(String url) throws IOException, InterruptedException {
        String urlString = url+"/isAlive";
        return sendRequest(urlString);
    }

    /**
     * Helper function to fetch restaurants from the API
     * @param url the url of the service
     * @return an array of Restaurant objects mapped from the response body
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    public static Restaurant[] restaurantFetcher(String url) throws IOException, InterruptedException {
        // Create a request for restaurants
        String urlString = url+"/restaurants";
        String response = sendRequest(urlString);

        return objectMapper.readValue(response, Restaurant[].class);
    }

    /**
     * Helper function to fetch orders from the API given a date
     * @param url the url of the service
     * @param date the date to fetch orders from
     * @return an array of Order objects mapped from the response body
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    public static Order[] orderFetcher(String url, String date) throws IOException, InterruptedException {
        // Create a request for orders
        String urlString = url+"/orders/"+date;
        String response = sendRequest(urlString);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.readValue(response, Order[].class);
    }

    /**
     * Helper function to fetch the central area from the API
     * @param url the url of the service
     * @return a NamedRegion object mapped from the response body containing the coordinates for the central area
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    public static NamedRegion centralAreaFetcher(String url) throws IOException, InterruptedException {
        // Create a request for central area coordinates
        String urlString = url+"/centralArea";
        String response = sendRequest(urlString);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        return objectMapper.readValue(response, NamedRegion[].class)[0];
    }

    /**
     * Helper function to fetch no-fly zone coordinates from the API
     * @param url the url of the service
     * @return an array of NamedRegion objects mapped from the response body containing the coordinates for the no-fly zones
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    public static NamedRegion[] noFlyZoneFetcher(String url) throws IOException, InterruptedException {
        // Create a request for no-fly zone coordinates
        String urlString = url+"/noFlyZones";
        String response = sendRequest(urlString);

        return objectMapper.readValue(response, NamedRegion[].class);
    }
}
