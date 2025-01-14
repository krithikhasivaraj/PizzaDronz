package uk.ac.ed.inf.util.core;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Helper class for calculating whether a position is in a polygon's area,
 * adapted from <a href="https://www.geeksforgeeks.org/how-to-check-if-a-given-point-lies-inside-a-polygon/">link</a>
 */
public class Line {
    public LngLat p1, p2;

    public Line(LngLat p1, LngLat p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    static int onLine(Line l1, LngLat p) {
        // Check whether p is on the line or not
        if (p.lng() <= Math.max(l1.p1.lng(), l1.p2.lng())
                && p.lng() >= Math.min(l1.p1.lng(), l1.p2.lng())
                && (p.lat() <= Math.max(l1.p1.lat(), l1.p2.lat())
                && p.lat() >= Math.min(l1.p1.lat(), l1.p2.lat())))
            return 1;
        return 0;
    }

    static int direction(LngLat a, LngLat b, LngLat c) {
        double val = (b.lat() - a.lat()) * (c.lng() - b.lng())
                - (b.lng() - a.lng()) * (c.lat() - b.lat());

        if (val == 0)

            // Collinear
            return 0;

        else if (val < 0)

            // Anti-clockwise direction
            return 2;

        // Clockwise direction
        return 1;
    }

    static int isIntersect(Line l1, Line l2) {
        // Four direction for two lines and points of other
        // line
        int dir1 = direction(l1.p1, l1.p2, l2.p1);
        int dir2 = direction(l1.p1, l1.p2, l2.p2);
        int dir3 = direction(l2.p1, l2.p2, l1.p1);
        int dir4 = direction(l2.p1, l2.p2, l1.p2);

        // When intersecting
        if (dir1 != dir2 && dir3 != dir4)
            return 1;

        // When p1 of line2 are on the line1
        if (dir1 == 0 && onLine(l1, l2.p1) == 1)
            return 1;

        // When p2 of line2 are on the line1
        if (dir2 == 0 && onLine(l1, l2.p2) == 1)
            return 1;

        // When p1 of line1 are on the line2
        if (dir3 == 0 && onLine(l2, l1.p1) == 1)
            return 1;

        // When p2 of line1 are on the line2
        if (dir4 == 0 && onLine(l2, l1.p2) == 1)
            return 1;

        return 0;
    }

    static int checkInside(LngLat[] poly, int n, LngLat p) {

        // When polygon has less than 3 edge, it is not
        // polygon

        if (n < 3)
            return 0;

        // Create a point at infinity, y is same as point p
        LngLat pt = new LngLat(999.99, p.lat());
        Line exline = new Line(p, pt);
        int count = 0;
        int i = 0;
        do {

            // Forming a line from two consecutive points of
            // poly
            Line side
                    = new Line(poly[i], poly[(i + 1) % n]);
            if (isIntersect(side, exline) == 1) {

                // If side intersects exline
                if (direction(side.p1, p, side.p2) == 0)
                    return onLine(side, p);
                count++;
            }
            i = (i + 1) % n;
        } while (i != 0);

        // When count is odd
        return count & 1;
    }
}