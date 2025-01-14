package uk.ac.ed.inf.util.core;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Router {
    private final NamedRegion centralArea;
    private final NamedRegion[] noFlyZones;
    private final HashMap<String, List<Move>> cachedPaths = new HashMap<>();

    /**
     * Helper class to calculate the path of a drone
     * @param noFlyZones the no-fly-zones
     * @param centralArea the central area
     */
    public Router(NamedRegion[] noFlyZones, NamedRegion centralArea) {
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
    }

    /**
     * Helper function to find the path from one location to another
     * @param locationA the start location
     * @param locationB the end location
     * @param orderNo the order number
     * @return a list of moves
     */
    private List<Move> calculateRoute(LngLat locationA, LngLat locationB, String orderNo) {
        // Creates a list of previous moves
        List<LngLat> prevMoves = new ArrayList<>();

        // Instantiates a LngLatHandler
        LngLatHandler handler = new LngLatHandler();

        // Sets the current position to the start position
        LngLat currentPos = locationA;
        ArrayList<Move> path = new ArrayList<>();

        // Defines all possible angles the drone can move in
        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};
        double ang = 0;
        double distance;

        // While the drone is not close to the destination
        while (!handler.isCloseTo(currentPos, locationB)) {

            // Set the closest distance to "infinite"
            double closest = Double.MAX_VALUE;
            for (double angle : angles) {

                // Find the next position for each angle
                LngLat nextPos = handler.nextPosition(currentPos, angle);

                // Check if next position is previously considered
                if (!prevMoves.contains(new LngLat(nextPos.lng(), nextPos.lat()))) {
                    boolean currInCentral = handler.isInCentralArea(currentPos, centralArea);
                    boolean nextInCentral = handler.isInCentralArea(nextPos, centralArea);
                    boolean isInNoFlyZone = false;

                    // Checks if next position is in a noFlyZone
                    for (NamedRegion noFlyZone : this.noFlyZones) {
                        if (handler.isInRegion(nextPos, noFlyZone)) {
                            isInNoFlyZone = true;
                            break;
                        }
                    }

                    // If next position is not in a noFlyZone
                    if (!isInNoFlyZone) {

                        // If we leave the central area we are not allowed to go back in
                        if (currInCentral || !nextInCentral) {
                            distance = handler.distanceTo(nextPos, locationB);

                            // If the distance is closer than the current closest distance
                            if (distance < closest) {

                                // Set new closest to this distance
                                closest = distance;

                                // Save the move angle
                                ang = angle;
                            }
                        }
                    }

                    // Add this move to the list of previous moves
                    prevMoves.add(new LngLat(nextPos.lng(), nextPos.lat()));
                }
            }

            // Using the closest distance to the destination create a new move and add it to the path
            Move move = new Move(currentPos, ang, handler.nextPosition(currentPos, ang), orderNo);
            path.add(move);

            // Set currentPosition to the end of the new move
            currentPos = handler.nextPosition(currentPos, ang);
        }
        path.add(new Move(currentPos, 999, currentPos, orderNo));
        return path;
    }

    /**
     * Helper function to find the total path from one location to another and back again
     * @param restaurantLoc the restaurant location
     * @param dropOff the drop off location
     * @param orderNo the order number
     * @return a list of moves
     */
    public List<Move> calculateTotalPath(LngLat restaurantLoc, LngLat dropOff, String orderNo) {
        // Creates a cache key for each path
        String key = "KEY:" + restaurantLoc.lng() + restaurantLoc.lat() + dropOff.lng() + dropOff.lat();
        if (cachedPaths.containsKey(key)) {

            // Copying the cache
            List<Move> cachedResult = new ArrayList<>();

            // Set all orderNo to the current orderNo
            for (Move move : cachedPaths.get(key)) {

                // Copying the value of each move in the cachedPaths
                Move copy = new Move(move.getStart(), move.getAngle(), move.getEnd(), orderNo);

                // Adding the copied move to the cachedResult
                cachedResult.add(copy);
            }
            return cachedResult;
        }
        else {

            // Find path from drop off location to restaurant
            List<Move> path = calculateRoute(dropOff, restaurantLoc, orderNo);

            // Creates a new list to store the reversed path
            ArrayList<Move> copy = new ArrayList<>();
            for (Move move : path) {

                // If the move is a hover move, skip it
                if (move.getAngle() == 999) {
                    continue;
                }

                // Reverses positions and angle for each move
                Move copyMove = new Move(move.getEnd(), (move.getAngle()+180)%360, move.getStart(), orderNo);

                // Adds the reversed move to the copy
                copy.add(copyMove);
            }

            // Reverses the path
            Collections.reverse(copy);

            // Saves position to hover after reversing path
            LngLat endHover = copy.get(copy.size()-1).getEnd();

            // Adds hover move at the end of the path
            copy.add(new Move(endHover, 999, endHover, orderNo));

            // Adds the reversed path to the original path
            path.addAll(copy);

            // Adds the path to the cache
            cachedPaths.put(key, path);
        }
        // Returns the path
        return cachedPaths.get(key);
    }
}
