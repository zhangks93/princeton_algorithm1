import java.util.Arrays;
import java.util.HashMap;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;

import javax.sound.sampled.Line;

/**
 * Created by Liutong Chen on 08/24/2018
 */

public class BruteCollinearPoints {
    private int numberOfSegments = 0;
    private LineSegment[] segmentsArr;
    private HashMap<Double, Point[]> curSegments = new HashMap<>();

    /**
     * Finds all line segmentsArr containing 4 points.
     * To check whether the 4 points p, q, r, and s are collinear, check whether the three slopes between p and q, between p and r, and between p and s are all equal.
     * @param points
     */
    public BruteCollinearPoints(Point[] points) {
        int N = points.length;
        for (int point1Id = 0;  point1Id < N; point1Id++) {
            for (int point2Id = point1Id+1; point2Id < N; point2Id++) {
                for (int point3Id = point2Id + 1; point3Id < N; point3Id++) {
                    for (int point4Id = point3Id + 1; point4Id < N; point4Id++) {
                        Point point1 = points[point1Id];
                        Point point2 = points[point2Id];
                        Point point3 = points[point3Id];
                        Point point4 = points[point4Id];
                        double slope1To2 = point1.slopeTo(point2);
                        double slope1To3 = point1.slopeTo(point3);
                        double slope1To4 = point1.slopeTo(point4);
                        if (slope1To2 == slope1To3 && slope1To2 == slope1To4) {
                            numberOfSegments++;
                            Point[] tempPointArr = new Point[]{point1, point2, point3, point4};
                            Arrays.sort(tempPointArr);
                            Point[] prevPoints = curSegments.get(slope1To2);
                            if (prevPoints != null) {
                                tempPointArr[1] = prevPoints[0];
                                tempPointArr[2] = prevPoints[1];
                                Arrays.sort(tempPointArr);
                            }
                            curSegments.put(slope1To2, new Point[]{tempPointArr[0], tempPointArr[3]});
                        }
                    }
                }
            }
        }
    }

    /**
     * The number of line segmentsArr
     * @return
     */
    public int numberOfSegments() {
        return numberOfSegments;
    }

    /**
     * The line segmentsArr
     * @return
     */
    public LineSegment[] segments() {
        segmentsArr = new LineSegment[curSegments.size()];
        int iter = 0;
        for (Point[] endPoints : curSegments.values()) {
            segmentsArr[iter++] = new LineSegment(endPoints[0], endPoints[1]);
        }
        return segmentsArr;
    }

    /**
     * For testing purpose, delete later
     * TODO: DELETE THIS BEFORE SUBMISSION
     */
    public static void main(String[] args) {
        Point[] points = new Point[]{
                new Point(19000, 10000),
                new Point(18000, 10000),
                new Point(32000, 10000),
                new Point(21000, 10000),
                new Point(1234, 5678),
                new Point(14000, 10000),
        };

        BruteCollinearPoints testBrute = new BruteCollinearPoints(points);
        System.out.println(testBrute.numberOfSegments());
        LineSegment[] lineSegments = testBrute.segments();
        for (LineSegment line: lineSegments) {
            System.out.println(line);
        }
    }
}