package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.util.core.LngLatHandler;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.util.core.OrderValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.CENTRAL_REGION_NAME;

/**
 * Unit test for PizzaDronz App.
 */
public class AppTest
        extends TestCase
{
    /**
     * Create the test case - not done for CW1
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested - not done for CW1
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test - not done for CW1
     */
    public void testApp()
    {
        assertTrue( true );
    }

    /**
     * Unit tests for the ApiInvoker() function
     */
    public class ApiInvokerTest
            extends TestCase {
        public static Test suite() {
            return new TestSuite(ApiInvokerTest.class);
        }
    }

    /**
     * Unit tests for the distanceTo() function
     */
    public class DistanceToTest
            extends TestCase {
        public DistanceToTest(String testName) {
            super(testName);
        }
        public static Test suite() {
            return new TestSuite(DistanceToTest.class);
        }

        /**
         * Average test case,
         *`tests two positions (0.0, 0.0) and (0.0, 0.00015),
         * this test should pass as the distance between both positions is 0.00015 degrees
         */
        public void testDistanceOfOneMove() {
            LngLat p1 = new LngLat(0.0,0.0);
            LngLat p2 = new LngLat(0.0,0.00015);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1,p2);
            assertEquals(output,0.00015);
        }

        /**
         * Average test case,
         * tests two positions (0.0, 0.0) and (-3.0, -4.0),
         * this test should pass as the distance between both positions is 5.0 degrees
         */
        public void testNegativeValues() {
            LngLat p1 = new LngLat(0.0,0.0);
            LngLat p2 = new LngLat(-3.0,-4.0);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1,p2);
            assertEquals(output,5.0);
        }

        /**
         * Average test case,
         * tests two positions (4.0, 5.0) and (12.0, 11.0),
         * this test should pass as the distance between both positions is 10.0 degrees
         */
        public void testNonOriginPosition() {
            LngLat p1 = new LngLat(4.0,5.0);
            LngLat p2 = new LngLat(12.0,11.0);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1,p2);
            assertEquals(output,10.0);
        }

        /**
         * Average test case
         * tests two positions (0.0004, 0.0005) and (0.0012, 0.0011),
         * this test should pass as the distance between both positions is 0.001 degrees
         */
        public void testDecimalValues() {
            LngLat p1 = new LngLat(0.0004,0.0005);
            LngLat p2 = new LngLat(0.0012,0.0011);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1,p2);
            assertEquals(output,0.001);
        }

        /**
         * Extreme test case,
         * tests two positions (0.0, 0.0) and (0.0, 0.0),
         * this test should pass as the distance between both positions is 0.0 degrees
         */
        public void testSamePosition() {
            LngLat p1 = new LngLat(0.0, 0.0);
            LngLat p2 = new LngLat(0.0, 0.0);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1, p2);
            assertEquals(output, 0.0);
        }

        /**
         * Exceptional test case,
         * tests two positions (1.0, 1.0) and (4.0, 5.0),
         * this test should be false as the distance between both positions is 5.0 degrees != 4.0 degrees
         */
        public void testLowerWrongValue() {
            LngLat p1 = new LngLat(1.0, 1.0);
            LngLat p2 = new LngLat(4.0, 5.0);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1, p2);
            assertFalse(output==4.0);
        }

        /**
         * Exceptional test case,
         * tests two positions (0.0, 0.0) and (4.0, 5.0),
         * this test should be false as the distance between both positions is != 6.0 degrees
         */
        public void testLargerWrongValue() {
            LngLat p1 = new LngLat(1.0, 1.0);
            LngLat p2 = new LngLat(4.0, 5.0);
            LngLatHandler handler = new LngLatHandler();
            double output = handler.distanceTo(p1, p2);
            assertFalse(output==6.0);
        }
    }

    /**
     * Unit tests for the InRegion() function
     */
    public class InRegionTest
            extends TestCase {
        public InRegionTest(String testName) {
            super(testName);
        }
        public static Test suite() {
            return new TestSuite(InRegionTest.class);
        }

        /**
         * Average test case,
         * tests a position (3.0, 1.0) against a 4x4x4 Triangle area,
         * this test should be true as the position is within the triangle
         */
        public void testTriangle() {
            LngLat p = new LngLat(3.0, 1.0);
            LngLat v1 = new LngLat(0.0, 0.0);
            LngLat v2 = new LngLat(4.0, 0.0);
            LngLat v3 = new LngLat(0.0, 4.0);

            LngLat[] vertices = {v1, v2, v3};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertTrue(output);
        }

        /**
         * Average test case,
         * tests a position (1.0, 3.0) against a 10x10 square area,
         * this test should be true as the position is within the square
         */
        public void testSquare() {
            LngLat p = new LngLat(1.0, 3.0);
            LngLat v1 = new LngLat(0.0, 10.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(10.0, 0.0);
            LngLat v4 = new LngLat(10.0, 10.0);

            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * tests a position (6.0, 5.0) against a Pentagon area,
         * this test should be true as the position is on a vertex in the Pentagon
         */
        public void testPentagon() {
            LngLat p = new LngLat(6.0, 5.0);
            LngLat v1 = new LngLat(0.0, 8.0);
            LngLat v2 = new LngLat(6.0, 5.0);
            LngLat v3 = new LngLat(6.0, -1.0);
            LngLat v4 = new LngLat(-2.0,-4.0);
            LngLat v5 = new LngLat(-5.0, 3.0);

            LngLat[] vertices = {v1, v2, v3, v4, v5};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * tests a position (0.0, 5.0) against a 10x10 square area,
         * this test should be true as the position is on an edge of the square
         */
        public void testSquareEdge() {
            LngLat p = new LngLat(0.0, 5.0);
            LngLat v1 = new LngLat(0.0, 10.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(10.0, 0.0);
            LngLat v4 = new LngLat(10.0, 10.0);

            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * tests a position (0.0, 0.0) against a 10x10 square area,
         * this test should be true as the position is on a vertex of the square
         */
        public void testSquareVertex() {
            LngLat p = new LngLat(0.0, 0.0);
            LngLat v1 = new LngLat(0.0, 10.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(10.0, 0.0);
            LngLat v4 = new LngLat(10.0, 10.0);

            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertTrue(output);
        }

        /**
         * Exceptional test case,
         * tests a position (-1.0, -3.0) against a 10x10 square area,
         * this test should be false as the position is out with the square
         */
        public void testWrongLowerSquare() {
            LngLat p = new LngLat(-1.0, -3.0);
            LngLat v1 = new LngLat(0.0, 10.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(10.0, 0.0);
            LngLat v4 = new LngLat(10.0, 10.0);

            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertFalse(output);
        }

        /**
         * Exceptional test case,
         * tests a position (12.0, 11.0) against a 10x10 square area,
         * this test should be false as the position is out with the square
         */
        public void testWrongHigherSquare() {
            LngLat p = new LngLat(12.0, 11.0);
            LngLat v1 = new LngLat(0.0, 10.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(10.0, 0.0);
            LngLat v4 = new LngLat(10.0, 10.0);

            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertFalse(output);
        }

        /**
         * Exceptional test case,
         * tests a position (7.0, 7.0) against a Pentagon area,
         * this test should be false as the position is out with the Pentagon
         */
        public void testWrongPentagon() {
            LngLat p = new LngLat(6.0, 5.0);
            LngLat v1 = new LngLat(0.0, 8.0);
            LngLat v2 = new LngLat(6.0, 5.0);
            LngLat v3 = new LngLat(6.0, -1.0);
            LngLat v4 = new LngLat(-2.0,-4.0);
            LngLat v5 = new LngLat(-5.0, 3.0);

            LngLat[] vertices = {v1, v2, v3, v4, v5};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("test area", vertices);
            boolean output = handler.isInRegion(p, region);
            assertTrue(output);
        }
    }

    /**
     * Unit tests for the isCloseTo() function
     */
    public class IsCloseToTest
            extends TestCase {
        public IsCloseToTest(String testName) {
            super(testName);
        }
        public static Test suite() {
            return new TestSuite(IsCloseToTest.class);
        }

        /**
         * Average test case,
         * tests two positions (0.0, 0.0) and (0.0003, 0.0004),
         * this test should True as the distance between both positions is 0.00005 <= 0.00015 degrees
         */
        public void testPythagorasValues() {
            LngLat p1 = new LngLat(0.0,0.0);
            LngLat p2 = new LngLat(0.00003,0.00004);
            LngLatHandler handler = new LngLatHandler();
            boolean output = handler.isCloseTo(p1,p2);
            assertTrue(output);
        }

        /**
         * Average test case,
         * tests two positions (0.00001, 0.00001) and (0.0007, 0.0009),
         * this test should True as the distance between both positions is 0.00001 <= 0.00015 degrees
         */
        public void testNegativeValues() {
            LngLat p1 = new LngLat(-0.00001,-0.00001);
            LngLat p2 = new LngLat(-0.00007,-0.00009);
            LngLatHandler handler = new LngLatHandler();
            boolean output = handler.isCloseTo(p1,p2);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * tests two positions (0.0, 0.0) and (0.0, 0.00015),
         * this test should be False as the distance between both positions is 0.00015 == 0.00015 degrees
         */
        public void testEdgeCase() {
            LngLat p1 = new LngLat(0.0,0.0);
            LngLat p2 = new LngLat(0.0,0.00015);
            LngLatHandler handler = new LngLatHandler();
            boolean output = handler.isCloseTo(p1,p2);
            assertFalse(output);
        }

        /**
         * Extreme test case,
         * tests two positions (0.0, 0.0) and (0.0, 0.0),
         * this test should be True as the distance between both positions is within 0.00 <= 0.00015 degrees
         */
        public void testZero() {
            LngLat p1 = new LngLat(0.0,0.0);
            LngLat p2 = new LngLat(0.0,0.0);
            LngLatHandler handler = new LngLatHandler();
            boolean output = handler.isCloseTo(p1,p2);
            assertTrue(output);
        }

        /**
         * Exceptional test case,
         * tests two positions (3.0, 3.0) and (4.0, 5.0),
         * this test should be False as the distance between both positions is greater than 0.00015 degrees
         */
        public void testLargeValues() {
            LngLat p1 = new LngLat(3,3);
            LngLat p2 = new LngLat(4,5);
            LngLatHandler handler = new LngLatHandler();
            boolean output = handler.isCloseTo(p1,p2);
            assertFalse(output);
        }

        /**
         * Exceptional test case,
         * tests two positions (3.0, 3.0) and (0.0003, 0.0004),
         * this test should be False as the distance between both positions is much greater than 0.00015 degrees
         */
        public void testLargeAndSmallValues() {
            LngLat p1 = new LngLat(3,3);
            LngLat p2 = new LngLat(0.0003,0.0004);
            LngLatHandler handler = new LngLatHandler();
            boolean output = handler.isCloseTo(p1,p2);
            assertFalse(output);
        }
    }

    /**
     * Unit tests for the IsInCentralArea() function
     */
    public class IsInCentralAreaTest
            extends TestCase {
        public IsInCentralAreaTest(String testName) {
            super(testName);
        }
        public static Test suite() {
            return new TestSuite(IsInCentralAreaTest.class);
        }

        /**
         * Average test case,
         * creates a position (55.943, -3.1844) tested against the default Central Area vertices,
         * this test should be true as this point is in the Central Area
         */
        public void testPointInCentral() {
            LngLat p = new LngLat(55.943,-3.1844);
            LngLat v1 = new LngLat(55.946233, -3.192473);
            LngLat v2 = new LngLat(55.942617, -3.192473);
            LngLat v3 = new LngLat(55.942617, -3.184319);
            LngLat v4 = new LngLat(55.946233, -3.184319);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertTrue(output);
        }

        /**
         * Average test case,
         * creates a position (55.9456, -3.1894) tested against the default Central Area vertices,
         * this test should be true as this point is in the Central Area
         */
        public void testAnotherPointInCentral() {
            LngLat p = new LngLat(55.9456,-3.1894);
            LngLat v1 = new LngLat(55.946233, -3.192473);
            LngLat v2 = new LngLat(55.942617, -3.192473);
            LngLat v3 = new LngLat(55.942617, -3.184319);
            LngLat v4 = new LngLat(55.946233, -3.184319);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertTrue(output);
        }

        /**
         * Average test case,
         * creates a position (3.0, 4.0) tested against 7x7 Square region as the Central Area,
         * this test should be true as this point is in this region
         */
        public void testDifferentVertices() {
            LngLat p = new LngLat(3.0,4.0);
            LngLat v1 = new LngLat(0.0, 7.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(7.0, 0.0);
            LngLat v4 = new LngLat(7.0, 7.0);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * creates a position (55.946233, -3.1894) tested against the default Central Area vertices,
         * this test should be true as this point is in the Central Area
         */
        public void testLngEdgePoint() {
            LngLat p = new LngLat(55.946233,-3.1894);
            LngLat v1 = new LngLat(55.946233, -3.192473);
            LngLat v2 = new LngLat(55.942617, -3.192473);
            LngLat v3 = new LngLat(55.942617, -3.184319);
            LngLat v4 = new LngLat(55.946233, -3.184319);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * creates a position (55.9456, -3.192473) tested against the default Central Area vertices,
         * this test should be true as this point on a Central Area edge
         */
        public void testLatEdgePoint() {
            LngLat p = new LngLat(55.9456,-3.192473);
            LngLat v1 = new LngLat(55.946233, -3.192473);
            LngLat v2 = new LngLat(55.942617, -3.192473);
            LngLat v3 = new LngLat(55.942617, -3.184319);
            LngLat v4 = new LngLat(55.946233, -3.184319);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertTrue(output);
        }

        /**
         * Extreme test case,
         * creates a position (55.946233, -3.192473) tested against the default Central Area vertices and a differing name input,
         * this test should be true as this point is on a Central Area vertex
         */
        public void testVertexPoint() {
            LngLat p = new LngLat(55.946233,-3.192473);
            LngLat v1 = new LngLat(55.946233, -3.192473);
            LngLat v2 = new LngLat(55.942617, -3.192473);
            LngLat v3 = new LngLat(55.942617, -3.184319);
            LngLat v4 = new LngLat(55.946233, -3.184319);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("Central Area",vertices);
            try {
                boolean output = handler.isInCentralArea(p, region);}
            catch (Exception e) {
                assertEquals("the named region: Central Area is not valid - must be: central", e.getMessage());
            }
        }

        /**
         * Extreme test case,
         * creates a position (7.0, 7.0) tested against 7x7 Square region as the Central Area and a differing name input,
         * this test should be true as this point is in this region
         */
        public void testVertexDifferentVertices() {
            LngLat p = new LngLat(7.0,7.0);
            LngLat v1 = new LngLat(0.0, 7.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(7.0, 0.0);
            LngLat v4 = new LngLat(7.0, 7.0);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion("centralarea",vertices);
            try {
                boolean output = handler.isInCentralArea(p, region);}
            catch (Exception e) {
                assertEquals("the named region: centralarea is not valid - must be: central", e.getMessage());
            }
        }

        /**
         * Exceptional test case,
         * creates a position (56.0, -3.2) tested against the default Central Area vertices,
         * this test should be false as this point is not in the Central Area
         */
        public void testWrongValues() {
            LngLat p = new LngLat(56.0,-3.2);
            LngLat v1 = new LngLat(55.946233, -3.192473);
            LngLat v2 = new LngLat(55.942617, -3.192473);
            LngLat v3 = new LngLat(55.942617, -3.184319);
            LngLat v4 = new LngLat(55.946233, -3.184319);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertFalse(output);
        }

        /**
         * Exceptional test case,
         * creates a position (20.0, 10.0) tested against a 7x7 square region as the Central Area,
         * this test should be false as this point is not in the area specified
         */
        public void testWrongDifferentVertices() {
            LngLat p = new LngLat(20.0,10.0);
            LngLat v1 = new LngLat(0.0, 7.0);
            LngLat v2 = new LngLat(0.0, 0.0);
            LngLat v3 = new LngLat(7.0, 0.0);
            LngLat v4 = new LngLat(7.0, 7.0);
            LngLat[] vertices = {v1, v2, v3, v4};
            LngLatHandler handler = new LngLatHandler();
            NamedRegion region = new NamedRegion(CENTRAL_REGION_NAME,vertices);
            boolean output = handler.isInCentralArea(p, region);
            assertFalse(output);
        }
    }

    /**
     * Unit tests for the nextPosition() function
     */
    public class NextPositionTest
            extends TestCase {
        public NextPositionTest(String testName) {
            super(testName);
        }
        public static Test suite() {
            return new TestSuite(NextPositionTest.class);
        }

        /**
         * Average test case,
         * tests position (0.0, 0.0) and angle for hover is 999,
         * this test should be true as the output should be (0.0, 0.0)
         */
        public void testHover() {
            LngLat startingPosition = new LngLat(0.0, 0.0);
            LngLat expectedNextPosition = new LngLat(0.0, 0.0);
            double angle = 999;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (0.0, 0.0) and angle for EAST (0 pi),
         * this test should be true as the output should be (0.00015, 0.0)
         */
        public void testEast() {
            LngLat startingPosition = new LngLat(0.0, 0.0);
            LngLat expectedNextPosition = new LngLat(0.00015, 0.0);
            double angle = 0.0; LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }


        /**
         * Average test case,
         * tests position (0.0, 0.0) and angle for WEST (1 pi),
         * this test should be true as the output should be (-0.00015,0.0)
         */
        public void testWest() {
            LngLat startingPosition = new LngLat(0.0, 0.0);
            LngLat expectedNextPosition = new LngLat(-0.00015,0.0);
            double angle = 180.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (0.0, 0.0) and angle for NORTH (1/2 pi),
         * this test should be true as the output should be (0.0, 0.00015)
         */
        public void testNorth() {
            LngLat startingPosition = new LngLat(0.0, 0.0);
            LngLat expectedNextPosition = new LngLat(0.0, 0.00015);
            double angle = 90.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (0.0, 0.0) and angle for SOUTH (3/2 pi),
         * this test should be true as the output should be (0.0, -0.00015)
         */
        public void testSouth() {
            LngLat startingPosition = new LngLat(0.0, 0.0);
            LngLat expectedNextPosition = new LngLat(0.0, -0.00015);
            double angle = 270.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (1.0, 1.0) and angle for NORTH EAST (1/4 pi),
         * this test should be true as the output should be (1.000106066, 1.000106066)
         */
        public void testNorthEast() {
            LngLat startingPosition = new LngLat(1.0, 1.0);
            LngLat expectedNextPosition = new LngLat(1.000106066, 1.000106066);
            double angle = 45.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (1.0, 1.0) and angle for NORTH WEST (3/4 pi),
         * this test should be true as the output should be (0.999893934, 1.000106066)
         */
        public void testNorthWest() {
            LngLat startingPosition = new LngLat(1.0, 1.0);
            LngLat expectedNextPosition = new LngLat(0.999893934, 1.000106066);
            double angle = 135.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (1.0, 1.0) and angle for SOUTH WEST (5/4 pi),
         * this test should be true as the output should be (0.999893934, 0.999893934)
         */
        public void testSouthWest() {
            LngLat startingPosition = new LngLat(1.0, 1.0);
            LngLat expectedNextPosition = new LngLat(0.999893934, 0.999893934);
            double angle = 225.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (1.0, 1.0) and angle for SOUTH EAST (7/4 pi),
         * this test should be true as the output should be (1.000106066, 0.999893934)
         */
        public void testSouthEast() {
            LngLat startingPosition = new LngLat(1.0, 1.0);
            LngLat expectedNextPosition = new LngLat(1.000106066, 0.999893934);
            double angle = 315.0;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (-3.0, 56.0) and angle for EAST NORTH EAST (1/8 pi),
         * this test should be true as the output should be (-2.999942597, 56.0000574)
         */
        public void testEastNorthEast() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-2.999861418, 56.0000574);
            double angle = 22.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (-3.0, 56.0) and angle for NORTH NORTH EAST (3/8 pi),
         * this test should be true as the output should be (-2.999942597, 56.00013858)
         */
        public void testNorthNorthEast() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-2.999942597, 56.00013858);
            double angle = 67.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (-3.0, 56.0) and angle for NORTH NORTH WEST (5/8 pi),
         * this test should be true as the output should be (-3.000057403, 56.00013858)
         */
        public void testNorthNorthWest() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-3.000057403, 56.00013858);
            double angle = 112.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (-3.0, 56.0) and angle for WEST NORTH WEST (7/8 pi),
         * this test should be true as the output should be (-3.000138582, 56.0000574)
         */
        public void testWestNorthWest() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-3.000138582, 56.0000574);
            double angle = 157.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Average test case,
         * tests position (-3.0, 56.0) and angle for WEST SOUTH WEST (9/8 pi),
         * this test should be true as the output should be (-3.000057403, 55.9999426)
         */
        public void testWestSouthWest() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-3.000138582, 55.9999426);
            double angle = 202.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertTrue(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Exceptional test case,
         * tests position (-3.0, 56.0) and angle for SOUTH SOUTH WEST (11/8 pi),
         * this test should be false as the output should be (-3.000057403, 55.99986142)
         */
        public void testSouthSouthWest() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-3.000057403, 56.1);
            double angle = 247.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertFalse(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Exceptional test case,
         * tests position (-3.0, 56.0) and angle for SOUTH SOUTH EAST (13/8 pi),
         * this test should be false as the output should be (-2.999942597, 55.99986142)
         */
        public void testSouthSouthEast() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-2.999942597, 56.1);
            double angle = 292.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            System.out.println(output);
            assertFalse(handler.isCloseTo(output, expectedNextPosition));
        }

        /**
         * Exceptional test case,
         * tests position (-3.0, 56.0) and angle for EAST SOUTH EAST (15/8 pi),
         * this test should be false as the output should be (-2.999861418, 55.9999426)
         */
        public void testEastSouthEast() {
            LngLat startingPosition = new LngLat(-3.0, 56.0);
            LngLat expectedNextPosition = new LngLat(-2.999861418, 56.00013858);
            double angle = 337.5;
            LngLatHandler handler = new LngLatHandler();
            LngLat output = handler.nextPosition(startingPosition, angle);
            assertFalse(handler.isCloseTo(output, expectedNextPosition));
        }
    }

    public class ValidateOrderTest
            extends TestCase {
        public ValidateOrderTest(String testName) {
            super(testName);
        }
        public static Test suite() {
            return new TestSuite(ValidateOrderTest.class);
        }

        /**
         * Average test case,
         * tests an order with more than 4 pizzas
         */
        public void testMoreThan4Pizzas() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),
                                    ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{
                    new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
                    new Pizza("Pizza E", 1000),
            });
            order.setPriceTotalInPence( 5000+ SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }


        /**
         * Average test case,
         * tests an order with an invalid CVV (4 letters)
         */
        public void testInvalidCVV() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),
                                    ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{
                    new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence( 4000+ SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests an order with an invalid card number (9 letters)
         */
        public void testInvalidCardNum() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "1234567891234567",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),
                                    ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{
                    new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence( 4000+ SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests an order with wrong total cost
         */
        public void testWrongTotalCost() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence(2000+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests an order with an expired card
         */
        public void testExpiredCard() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            "04/22",
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence(4000+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests an order when pizza is not from any restaurant
         */
        public void testUndefinedPizza() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza H", 1000),
                    new Pizza("Undefined Pizza", 1600),
                    new Pizza("Pizza E", 1300),
                    new Pizza("Pizza F", 1900),
            });
            order.setPriceTotalInPence(5800+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{
                    new Restaurant("myRestaurant",
                            new LngLat(55.945535152517735, -3.1912869215011597),
                            new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                            new Pizza[]{new Pizza("Pizza A", 1000),
                                    new Pizza("Pizza B", 1000),
                                    new Pizza("Pizza C", 1000),
                                    new Pizza("Pizza D", 1000),}),
                    new Restaurant("theirRestaurant",
                            new LngLat(55.945535152517767, -3.1912869219011597),
                            new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                            new Pizza[]{new Pizza("Pizza E", 1300),
                                    new Pizza("Pizza F", 1900),
                                    new Pizza("Pizza G", 1600),
                                    new Pizza("Pizza H", 1000),})
            };

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests an order when the restaurant is closed
         */
        public void testRestaurantClosed() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 5));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence(4000+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.RESTAURANT_CLOSED, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests an order when the pizza is from multiple restaurants
         */
        public void testOrderFromMultipleRestaurants() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza A", 1000),
                    new Pizza("Pizza A", 1000),
                    new Pizza("Pizza E", 1000),
                    new Pizza("Pizza G", 1000),
            });
            order.setPriceTotalInPence(4000+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{
                    new Restaurant("myRestaurant",
                            new LngLat(55.945535152517735, -3.1912869215011597),
                            new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                            new Pizza[]{new Pizza("Pizza A", 1000),
                                    new Pizza("Pizza B", 1000),
                                    new Pizza("Pizza C", 1000),
                                    new Pizza("Pizza D", 1000),}),
                    new Restaurant("theirRestaurant",
                            new LngLat(55.945535152517767, -3.1912869219011597),
                            new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                            new Pizza[]{new Pizza("Pizza E", 1000),
                                    new Pizza("Pizza F", 1000),
                                    new Pizza("Pizza G", 1000),
                                    new Pizza("Pizza H", 1000),})
            };

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        }

        /**
         * Average test case,
         * tests a valid order
         */
        public void testValidOrder() {
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(24, 30)),
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza H", 1000),
                    new Pizza("Pizza G", 1600),
                    new Pizza("Pizza E", 1300),
                    new Pizza("Pizza F", 1900),
            });
            order.setPriceTotalInPence(5800+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{
                    new Restaurant("myRestaurant",
                            new LngLat(55.945535152517735, -3.1912869215011597),
                            new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                            new Pizza[]{new Pizza("Pizza A", 1000),
                                    new Pizza("Pizza B", 1000),
                                    new Pizza("Pizza C", 1000),
                                    new Pizza("Pizza D", 1000),}),
                    new Restaurant("theirRestaurant",
                            new LngLat(55.945535152517767, -3.1912869219011597),
                            new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                            new Pizza[]{new Pizza("Pizza E", 1300),
                                    new Pizza("Pizza F", 1900),
                                    new Pizza("Pizza G", 1600),
                                    new Pizza("Pizza H", 1000),})
            };

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus());
        }

        /**
         * Extreme test case,
         * tests an order with a card that expires on the same month and year
         */
        public void testValidExpiryDate() {
            // Creates a value that expires this month
            String expiryDate = "09/23";
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            expiryDate,
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence(4000+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus());
        }

        /**
         * Extreme test case,
         * tests an order with a card that expires in December
         */
        public void testDecemberExpiryDate() {
            // Creates a value that expires this month
            String expiryDate = "12/23";
            // Creates an order and sets all properties
            Order order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 12, 14));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setCreditCardInformation(
                    new CreditCardInformation(
                            "5555553753048194",
                            expiryDate,
                            String.format("%d%d%d", ThreadLocalRandom.current().nextInt(0, 10), ThreadLocalRandom.current().nextInt(0, 10),  ThreadLocalRandom.current().nextInt(0, 10))
                    )
            );
            order.setPizzasInOrder(new Pizza[]{new Pizza("Pizza A", 1000),
                    new Pizza("Pizza B", 1000),
                    new Pizza("Pizza C", 1000),
                    new Pizza("Pizza D", 1000),
            });
            order.setPriceTotalInPence(4000+SystemConstants.ORDER_CHARGE_IN_PENCE);

            // Creates a restaurant object
            Restaurant[] restaurants = new Restaurant[]{new Restaurant("myRestaurant",
                    new LngLat(55.945535152517735, -3.1912869215011597),
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                    new Pizza[]{new Pizza("Pizza A", 1000),
                            new Pizza("Pizza B", 1000),
                            new Pizza("Pizza C", 1000),
                            new Pizza("Pizza D", 1000),}
            )};

            // Creates an OrderValidator object and checks validation code
            var validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
            assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus());
        }
    }


}
