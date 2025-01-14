package uk.ac.ed.inf.util.core;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_IS_CLOSE_DISTANCE;
import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;

public class LngLatHandler implements LngLatHandling {
    public LngLatHandler() {
    }
    /**
     * Helper function,
     * calculates the Euclidean distance function to find the distance between two points,
     * distance = sqrt((x2-x1)^2+(y2-y1)^2)
     * @param x1 1st x coordinate
     * @param x2 2nd x coordinate
     * @param y1 1st y coordinate
     * @param y2 2nd y coordinate
     * @return distance between two coordinates
     **/
    private double euclideanDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Uses helper function euclideanDistance to calculate the distance between start and end coordinates
     * @param startPosition start coordinates (longitude, latitude) in degrees
     * @param endPosition   end coordinates (longitude, latitude) in degrees
     * @return distance between start and end coordinates in degrees
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        return euclideanDistance(startPosition.lng(), endPosition.lng(), startPosition.lat(), endPosition.lat());
    }

    /**
     * Checks if position of the drone is within 0.00015 degrees of another position
     * @param startPosition start coordinates (longitude, latitude) in degrees
     * @param otherPosition end coordinates (longitude, latitude) in degrees
     * @return true if the distance between start and end coordinates is less than 0.00015 degrees
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) <  DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * Checks if the drone is in the region using ray tracing,
     * uses helper class Line to check if the point is in the area covered by the polygon
     * @param position coordinates (longitude, latitude) in degrees
     * @param region a named region with name and vertices properties
     * @return true if the point is in the region
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        int n = vertices.length;
        return Line.checkInside(vertices, n, position)==1;
    }

    /**
     * Uses Trigonometry to calculate the change in longitude and latitude from the starting position to the new position,
     * sin(angle)*distance = change in longitude,
     * cos(angle)*distance = change in latitude,
     * @param startPosition coordinates (longitude, latitude) in degrees
     * @param angle angle in radians
     * @return the new position
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        if (angle == 999) {
            return startPosition;
        }
        else if (0>angle || angle>360) {
            throw new IllegalArgumentException("Angle must be between 0 and 360");
        }
        else if (angle%22.5!=0) {
            throw new IllegalArgumentException("Angle must be a multiple of 22.5");
        }
        double newLng = startPosition.lng() + DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle));
        double newLat = startPosition.lat() + DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle));
        return new LngLat(newLng, newLat);
    }
}