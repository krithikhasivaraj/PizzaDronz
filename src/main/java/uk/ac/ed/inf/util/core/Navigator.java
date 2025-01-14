package uk.ac.ed.inf.util.core;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.ac.ed.inf.ilp.constant.OrderStatus.DELIVERED;

public class Navigator {
    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final Restaurant[] restaurants;
    private final List<Order> orders;

    /**
     * Constructor for PathFinder class
     * @param noFlyZones the no-fly-zones
     * @param centralArea the central area
     * @param restaurants the restaurants
     * @param orders the orders
     */
    public Navigator(NamedRegion[] noFlyZones, NamedRegion centralArea, Restaurant[] restaurants, List<Order> orders) {
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
        this.restaurants = restaurants;
        this.orders = orders;
    }

    /**
     * Helper function to find the location of the restaurant for an order
     * @param order the order
     * @return the location of the restaurant
     */
    private LngLat getRestaurantLoc(Order order) {
        // Checks each restaurant to find which restaurant contains every pizza in the order
        for (Restaurant restaurant : restaurants) {
            // Converts the array of pizzas in the order and the restaurant menus to lists
            List<Pizza> pizzasList = Arrays.stream(order.getPizzasInOrder()).toList();
            List<Pizza> menuList = Arrays.stream(restaurant.menu()).toList();

            // Returns the restaurant where every pizza in the order is in the restaurant's menu
            if (OrderValidator.intersection(pizzasList, menuList) == order.getPizzasInOrder().length) {
                return restaurant.location();
            }
        }
        throw new IllegalArgumentException("Restaurant not found for order");
    }

    /**
     * Calculates the optimal route for every order and returns a list of every move made
     * @return a list of moves
     */
    public List<Move> findPaths() {
        Router router = new Router(noFlyZones, centralArea);

        // Creates a list for all paths
        List<Move> paths = new ArrayList<>();
        for (Order order : orders) {
            // Pick up location is the restaurant
            LngLat restLoc = getRestaurantLoc(order);
            // Drop off location is the Appleton Tower
            LngLat dropOff = new LngLat(-3.186874, 55.944494);
            // Find path from Appleton tower to restaurant and back again
            List<Move> path = router.calculateTotalPath(restLoc, dropOff, order.getOrderNo());
            // Add path to paths list
            paths.addAll(path);
            // Set order status to "DELIVERED"
            order.setOrderStatus(DELIVERED);
        }
        return paths;
    }
}
