package uk.ac.ed.inf;

import uk.ac.ed.inf.api.ApiInvoker;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.util.core.Move;
import uk.ac.ed.inf.util.core.Navigator;
import uk.ac.ed.inf.util.core.OrderValidator;
import uk.ac.ed.inf.util.writer.DeliveryJsonWriter;
import uk.ac.ed.inf.util.writer.FlightpathJsonWriter;
import uk.ac.ed.inf.util.writer.GeoJsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

public class App {
    /**
     * Main function to run the program
     * @param args the arguments passed to the program
     * @throws IOException if there is an error with the request this error is thrown
     * @throws InterruptedException if system is interrupted this error is thrown
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // Start timer
        long StartTime = System.currentTimeMillis();

        // Input validation for arguments
        if (args.length != 2) {
            throw new IllegalArgumentException("\u001B[31mArg length error: Incorrect number of arguments\u001B[0m");
        }

        String date = args[0];
        String url = args[1];

        System.out.println("\u001B[37mEntered Date:\u001B[0m " + date);
        System.out.println("\u001B[37mEntered URL:\u001B[0m " + url+"\n");

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("\u001B[31mDate format error: Date must be in the format YYYY-MM-DD\u001B[0m");
        } else if (!url.matches("https://ilp-rest.azurewebsites.net")) {
            throw new IllegalArgumentException("\u001B[31mIncorrect URL error: URL must be https://ilp-rest.azurewebsites.net\u001B[0m");
        }

        // Check if REST service is alive and throw error if service is not
        String serviceAlive = ApiInvoker.serviceAlive(url);
        if (!serviceAlive.equals("true")) {
            throw new IllegalArgumentException("\u001B[31mService error: Service is not alive\u001B[0m");
        }

        // Fetching data log
        System.out.println("\u001B[36m\u001B[3mFetching data from REST service...\u001B[0m ");

        // Fetch data from API
        Order[] orders = ApiInvoker.orderFetcher(url, date);
        if (orders.length == 0) {
            throw new IllegalArgumentException("\u001B[31mOrder error: No orders for this date\u001B[0m");
        }
        Restaurant[] restaurants = ApiInvoker.restaurantFetcher(url);
        NamedRegion centralArea = ApiInvoker.centralAreaFetcher(url);
        NamedRegion[] noFlyZones = ApiInvoker.noFlyZoneFetcher(url);

        // Data fetched confirmation log
        System.out.println("\u001B[32mData fetched successfully\u001B[0m\n");

        // Validate orders, adding all valid orders to a list "validOrders"
        OrderValidator validator = new OrderValidator();
        List<Order> validOrders = new ArrayList<>();
        for (Order order : orders) {
            Order orderCheck = validator.validateOrder(order, restaurants);
            if (orderCheck.getOrderStatus() != OrderStatus.INVALID) {
                validOrders.add(order);
            }
        }

        // Calculating flight paths log
        System.out.println("\u001B[36m\u001B[3mCalculating flight paths...\u001B[0m ");

        // Calculate optimal route
        Navigator pathfinder = new Navigator(noFlyZones, centralArea, restaurants, validOrders);
        List<Move> paths = pathfinder.findPaths();

        // Get year, month and day from date
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);

        // Create results folder in root directory
        new File("resultfiles").mkdirs();

        // Creates the delivery JSON file
        String deliveryFileName="deliveries-" + year + "-" + month + "-" + day + ".json";
        try (FileWriter fileWriter = new FileWriter("resultfiles/" + deliveryFileName)) {
            fileWriter.write(DeliveryJsonWriter.writeDeliveryJson(orders));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        // Log delivery JSON file creation
        System.out.println("\u001B[32mCreated " + deliveryFileName + "\u001B[0m");

        // Creates the flightpath JSON file
        String flightpathFileName="flightpath-" + year + "-" + month + "-" + day + ".json";
        try (FileWriter fileWriter = new FileWriter("resultfiles/" + flightpathFileName)) {
            fileWriter.write(FlightpathJsonWriter.writeFlightpathJson(paths));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        // Log FlightPath JSON file creation
        System.out.println("\u001B[32mCreated " + flightpathFileName + "\u001B[0m");

        // Creates the drone GeoJSON file
        String droneFileName="drone-" + year + "-" + month + "-" + day + ".geojson";
        try (FileWriter fileWriter = new FileWriter("resultfiles/" + droneFileName)) {
            fileWriter.write(GeoJsonWriter.writeGeoJson(paths));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        // Log drone GeoJSON file creation
        System.out.println("\u001B[32mCreated " + droneFileName + "\u001B[0m\n");

        // Log runtime
        long EndTime = System.currentTimeMillis();
        double elapsedTime = (double) (EndTime - StartTime)/1000;
        System.out.println("\u001B[35mRuntime: " + elapsedTime + " seconds\u001B[0m");
    }
}
