package uk.ac.ed.inf.util.core;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.MAX_PIZZAS_PER_ORDER;
import static uk.ac.ed.inf.ilp.constant.SystemConstants.ORDER_CHARGE_IN_PENCE;

public class OrderValidator implements OrderValidation {

    Logger logger = Logger.getLogger(OrderValidator.class.getName());

    private Map<String, String> pizzaToRestaurant;

    public static int intersection(java.util.List<Pizza> orderList, java.util.List<Pizza> menu) {
        int counter = 0;
        for (Pizza pizza : orderList) {
            if (menu.contains(pizza)) {
                counter++;
            }
        }
        return counter;
    }

    private boolean isRestaurantOpen(Order order, Restaurant[] restaurants) {
        String restaurantName = this.pizzaToRestaurant.get(order.getPizzasInOrder()[0].name());
        Optional<Restaurant> selectedRestaurant = Arrays.stream(restaurants)
                .filter(restaurant -> restaurant.name().equalsIgnoreCase(restaurantName)).findFirst();
        return selectedRestaurant.map(restaurant -> Arrays.stream(restaurant.openingDays())
                .anyMatch(dayOfWeek -> dayOfWeek.compareTo(order.getOrderDate().getDayOfWeek()) == 0)).orElse(false);
    }

    private boolean isOrderFitForSingleRestaurant(Order order) {
        List<String> restaurantNames = new ArrayList<>();
        Arrays.stream(order.getPizzasInOrder()).forEach(pizza -> restaurantNames.add(this.pizzaToRestaurant.get(pizza.name())));
        return restaurantNames.stream().allMatch(restaurantNames.get(0)::equalsIgnoreCase);
    }

    private boolean isPizzaCountWithinLimit(Order order) {
        long pizzaCount = Arrays.stream(order.getPizzasInOrder()).count();
        return (pizzaCount <= MAX_PIZZAS_PER_ORDER && Long.signum(pizzaCount) == 1);
    }

    private boolean isPizzaTypeAvailable(Order order) {
        return Arrays.stream(order.getPizzasInOrder()).allMatch(pizza -> this.pizzaToRestaurant.containsKey(pizza.name()));
    }

    private boolean isOrderTotalCorrect(Order order) {
        return (order.getPriceTotalInPence() == Arrays.stream(order.getPizzasInOrder())
                .mapToInt(Pizza::priceInPence).sum() + ORDER_CHARGE_IN_PENCE);
    }

    private boolean isCardCvvValid(Order order) {
        Pattern pattern = Pattern.compile("\\d{3}");
        return pattern.matcher(order.getCreditCardInformation().getCvv()).matches();
    }

    private boolean isCardExpDateValid(Order order) throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/uu");
        YearMonth expiry = YearMonth.parse(order.getCreditCardInformation().getCreditCardExpiry(), formatter);
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        YearMonth currentYearMonth = YearMonth.from(today);
        return !(currentYearMonth.isAfter(expiry));
    }

    private boolean isCardNumberValid(Order order) {
        Pattern pattern = Pattern.compile("\\d{16}");
        return pattern.matcher(order.getCreditCardInformation().getCreditCardNumber()).matches();
    }

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        // Populate a map k = pizza name, v = restaurant name
        this.pizzaToRestaurant = new HashMap<>();
        for (Restaurant r : definedRestaurants) {
            String restaurantName = r.name();
            for (Pizza p : r.menu()) {
                this.pizzaToRestaurant.put(p.name(), restaurantName);
            }
        }

        // Check if the ordered count of pizzas exceed drone capacity
        if (!isPizzaCountWithinLimit(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - More than drone capacity");
            return orderToValidate;
        }

        // Check if the card number is valid
        if (!isCardNumberValid(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - Card number invalid");
            return orderToValidate;
        }

        // Check if the card is not expired
        try {
            if (!isCardExpDateValid(orderToValidate)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                logger.log(Level.INFO,"Invalid order - Card expired");
                return orderToValidate;
            }
        } catch (DateTimeException e) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.SEVERE,"Problem with text parsing with card expiry date", e);
            return orderToValidate;
        }

        // Check if the card CVV is valid
        if (!isCardCvvValid(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - Card CVV invalid");
            return orderToValidate;
        }

        // Check if the total sum of ordered pizzas matches the price tag
        if (!isOrderTotalCorrect(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - Sum of orders don't match with the order total");
            return orderToValidate;
        }

        // Check if the pizza type is available in the menu considering all the restaurants
        if (!isPizzaTypeAvailable(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - Undefined type of pizza");
            return orderToValidate;
        }

        // Check if the order can be delivered from a single restaurant
        if (!isOrderFitForSingleRestaurant(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - Can't be delivered from one restaurant");
            return orderToValidate;
        }

        // Check if the restaurant is open
        if (!isRestaurantOpen(orderToValidate, definedRestaurants)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            logger.log(Level.INFO,"Invalid order - Restaurant is closed on the day of order");
            return orderToValidate;
        }

        // No issues in validation checks
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        logger.log(Level.INFO,"Valid order - all set to go");
        return orderToValidate;
    }
}
