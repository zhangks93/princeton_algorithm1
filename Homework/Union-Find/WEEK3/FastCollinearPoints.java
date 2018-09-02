import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
/**
 * @author evasean www.cnblogs.com/evasean/
 */
public class FastCollinearPoints {
    
    private Point[] points; //提交作业时提示输入的给构造函数的数组内容不能发生改变，故类中加个数组将输入参数存起来
    private final LineSegment[] segments;
    private int segNum;
    
    private List<PointPair> pointPairList; //存储构成LineSegment的起点和终点Point对
    /**
     * LineSegment类不允许变动，但是可使用灵活度受限，自己新加个内部类使用
     * 本类用来存储可构成LineSegment的起点和终点point对
     * 由于在遍历过程中会存在包含关系的起点和终点point对，仅仅靠LineSegment类识别包含关系的效率会很低
     * 此类中加了slope来记录就可以很大的提高效率了，因为一个点和一个斜率就确定了一条直线
     * 不需要再进行额外比较和计算
     * 因为由于PointPair是对points从前到后遍历产生的，所以如果两个PointPair存在包含关系，那么
     * 这两个PointPair中largePoint和slope一定相等
     * 但smallPoint不相等，smallPoint更小的那个PointPair包含了另一个PointPair
     * 这是LineSegment去重的关键
     * @author evasean www.cnblogs.com/evasean/
     */
    private class PointPair{
        private final Point smallPoint;
        private final Point largePoint;
        private final double slope; 
        public PointPair(Point smallPoint, Point largePoint){
            this.smallPoint = smallPoint;
            this.largePoint = largePoint;
            this.slope = largePoint.slopeTo(smallPoint);
        }
        public Point getLargePoint(){
            return this.largePoint;
        }
        public Point getSmallPoint(){
            return this.smallPoint;
        }
        public double getSlope(){
            return this.slope;
        }
        public int compareTo(PointPair that) {
            Point l1 = this.getLargePoint();
            Point l2 = that.getLargePoint();
            double s1 = this.getSlope();
            double s2 = that.getSlope();
            if(l1.compareTo(l2) > 0) return 1;
            else if(l1.compareTo(l2) < 0) return -1;
            else{
                if(s1>s2) return 1;
                else if(s1<s2) return -1;
                else return 0;
            }
        }
        /**
         * 判断PointPair中的包含关系时需要用到比较器
         * 此比较器是以largePoint为比较的主要元素，slope为次要元素
         * smallPoint不参比较大小的考核，仅仅在两个PointPair相等时用作判断包含关系之用
         * 两个PointPair pp1 和 pp2中
         * if pp1.largePoint > pp2.largePoint --> pp1 > pp2
         * else if pp1.largePoint < pp2.largePoint --> pp1 < pp2
         * if pp1.largePoint == pp2.largePoint && pp1.slope > pp2.slope --> pp1 > pp2
         * if pp1.largePoint == pp2.largePoint && pp1.slope < pp2.slope --> pp1 < pp2
         * if pp1.largePoint == pp2.largePoint && pp1.slope == pp2.slope --> pp1 == pp2
         * @return
         */
        public Comparator<PointPair> pointPairComparator() {
            return new PointPairComparator();
        }
        private class PointPairComparator implements Comparator<PointPair>{
            @Override
            public int compare(PointPair pp1, PointPair pp2) {
                // TODO Auto-generated method stub
                Point l1 = pp1.getLargePoint();
                Point l2 = pp2.getLargePoint();
                double s1 = pp1.getSlope();
                double s2 = pp2.getSlope();
                if(l1.compareTo(l2) > 0) return 1;
                else if(l1.compareTo(l2) < 0) return -1;
                else{
                    return Double.compare(s1, s2); //double元素用Double.compare进行比较更精确
                }
            }
        }
    }
    
    public FastCollinearPoints(Point[] inpoints) {
        // finds all line segments containing 4 or more points
        if (inpoints == null)
            throw new IllegalArgumentException("Constructor argument Point[] is null!");
        // finds all line segments containing 4 points
        for (int i=0;i<inpoints.length;i++) {
            if (inpoints[i] == null)
                throw new IllegalArgumentException("there is null in constructor argument");
        }
        points = new Point[inpoints.length];
        for (int i=0;i<inpoints.length;i++) {
            points[i] = inpoints[i];
        }
        Arrays.sort(points); //对本对象的私有数组进行排序
        for (int i=0;i<points.length-1;i++) {
            if (points[i].compareTo(points[i+1]) == 0) // 与前一个元素相等
                throw new IllegalArgumentException("there exists repeated points!");
        }
        //作业提交时提示随机穿插顺序调用numberOfSegments()和segment()方法返回结果要求稳定
        //那么构造函数中就要把LineSegment找好
        findPointPairForLineSegment(points);
        segments = generateLineSegment();
    }

    /**
     * 寻找满足LineSegment的PointPair
     * @param points
     */
    private void findPointPairForLineSegment(Point[] points){
        int pNum = points.length;
        pointPairList = new ArrayList<PointPair>();
        for (int i = 0; i < pNum - 3; i++) { //i不需要遍历最后三个节点，因为至少四个节点才能组成LineSegment
            if(points[i]==null)
                throw new IllegalArgumentException("there is null in constructor argument");
            Point origin = points[i]; //i处节点作为相对原点
            Point[] tPoints = new Point[pNum-i-1]; //需要用到额外空间来存储本轮i之后的节点根据它们各自与节点i的相对斜率来排序的结果
            int tpNum = 0;
            for (int j = i + 1; j < pNum; j++) {
                tPoints[tpNum++] = points[j];
            }
            //origin.slopeOrder()这个比较器就是告诉Arrays.sort待排序的那些节点tPoints排序的依据是各自与节点i的斜率
            Arrays.sort(tPoints,origin.slopeOrder()); 
            
            int startPostion = 0; //startPosition用来记录slope相同的point位置区间的起始位置
            double slope = origin.slopeTo(tPoints[0]);
            Map<Integer,Integer> intervalMap = new HashMap<Integer,Integer>(); //记录slope相同的point位置区间
            int curPostion = 1;
            for(; curPostion<tpNum; curPostion++){
                if(Double.compare(origin.slopeTo(tPoints[curPostion]), slope)==0)
                    continue;
                else{ //遍历至slope不与之前相同的位置
                    if(curPostion-startPostion >= 3) { //如果大于3，就表示满足了组成LineSegment的条件，记录point位置区间
                        intervalMap.put(startPostion, curPostion-1);//curPostion-1就是区间终止节点位置
                    }
                    slope = origin.slopeTo(tPoints[curPostion]);
                    startPostion = curPostion; //重置起始节点
                }
            }
            if(curPostion-startPostion >= 3) { //tPoints最后一个节点也可能与前一节点有相同的slope
                intervalMap.put(startPostion, curPostion-1);
            }
            //根据满足条件的区间位置，创建PointPair
            for(int key : intervalMap.keySet()){
                int value = intervalMap.get(key);
                Point[] linearPoints = new Point[value-key+2];
                linearPoints[0] = origin;
                int l = 1;
                while(key<=value){
                    linearPoints[l++] = tPoints[key++];
                }
                Arrays.sort(linearPoints);
                PointPair pointPair = new PointPair(linearPoints[0], linearPoints[l-1]);
                pointPairList.add(pointPair);
            }
            //清空临时数据，便于垃圾回收
            intervalMap.clear();
            intervalMap = null;
            for(int t=0;t<tPoints.length;t++){
                tPoints[t] = null;
            }
            tPoints = null;
        }
    }
    /**
     * 生成LineSegment
     * @return
     */
    private LineSegment[]  generateLineSegment(){
        int ppsize = pointPairList.size();
        if(ppsize==0) return new LineSegment[0];;
        PointPair[] pointPairs =  new PointPair[ppsize];
        int i = 0;
        for(PointPair pp : pointPairList){
            pointPairs[i++] = pp;
        }
        pointPairList.clear();
        //根据pointPairComparator比较器所定制的排序依据进行排序，使得存在包含关系的PointPair变成相邻关系
        Arrays.sort(pointPairs,pointPairs[0].pointPairComparator());
        List<LineSegment> lineSegmentList = new ArrayList<LineSegment>();
        
        PointPair ppls = pointPairs[0]; 
        for(i=1;i<ppsize;i++){
            if(ppls.compareTo(pointPairs[i])==0){ //相邻的PointPair相等时，具有更小smallPoint的PointPair区间更大
                Point s = pointPairs[i].getSmallPoint();
                if(ppls.getSmallPoint().compareTo(s) > 0)
                    ppls = pointPairs[i];
            }else{
                LineSegment seg = new LineSegment(ppls.getSmallPoint(),ppls.getLargePoint());
                lineSegmentList.add(seg);
                ppls = pointPairs[i];
            }
        }
        LineSegment seg = new LineSegment(ppls.getSmallPoint(),ppls.getLargePoint());
        lineSegmentList.add(seg);
        
        LineSegment[] segments = new LineSegment[lineSegmentList.size()];
        segNum = 0;
        for (LineSegment ls : lineSegmentList) {
            segments[segNum++] = ls;
        }
        return segments;
    }
    
    public int numberOfSegments() {
        // the number of line segments
        return segNum;
    }
    
    public LineSegment[] segments() {
        // the line segments
        //作业提交时，提示要求多次调用segments()方法返回的应该是不同的对象
        LineSegment[] retseg = new LineSegment[segNum];
        for(int i =0 ;i<segNum;i++){
            retseg[i] = segments[i];
        }
        return retseg;
    }
    
    public static void main(String[] args) {
        // read the n points from a file
        //In in = new In(args[0]);
        In in = new In("collinear/rs1423.txt"); //本地测试使用
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
//        StdDraw.setPenColor(StdDraw.RED);
//        StdDraw.setPenRadius(0.01);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
