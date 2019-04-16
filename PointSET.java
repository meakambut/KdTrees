import edu.princeton.cs.algs4.Point2D;
import java.util.TreeSet;
import java.util.Stack;
import java.util.NavigableSet;
import edu.princeton.cs.algs4.RectHV;

import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

   private final TreeSet<Point2D> set;

   public PointSET()                               // construct an empty set of points
   { this.set = new TreeSet<Point2D>(); }
 
   public boolean isEmpty()                      // is the set empty? 
   { return set.isEmpty(); }

   public int size()                         // number of points in the set 
   { return set.size(); }

   public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
   { 
     if (p == null) throw new java.lang.IllegalArgumentException(); 
     set.add(p); 
   }

   public boolean contains(Point2D p)            // does the set contain point p? 
   { 
     if (p == null) throw new java.lang.IllegalArgumentException(); 
     return set.contains(p); 
   }

   public void draw()                         // draw all points to standard draw 
   {
     for (Point2D p : set)
       p.draw();
   }

   public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary) 
   {  
     if (rect == null) throw new java.lang.IllegalArgumentException(); 
     if (set.isEmpty())
       return null;
     Stack<Point2D> pointsInsideRectangle = new Stack<Point2D>();
     NavigableSet<Point2D> subset = this.set.subSet (new Point2D(rect.xmin(), rect.ymin()), true, new Point2D(rect.xmax(), rect.ymax()), true);
     for (Point2D p : subset)
       if (rect.contains(p))
         pointsInsideRectangle.push(p);  
     return pointsInsideRectangle;
   }

   public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty 
   {
     if (p == null) throw new java.lang.IllegalArgumentException(); 
     if (set.isEmpty())
       return null;
     double min = Double.POSITIVE_INFINITY;
     Point2D pmin = this.set.first();
     double d = 0.0001;
     double xmin = p.x() - d, xmax = p.x() + d, ymin = p.y() - d, ymax = p.y() + d;
     RectHV rectangle = new RectHV(Math.max(0, xmin), Math.max(0, ymin), Math.max(0, xmax), Math.max(0, ymax));
     Stack<Point2D> pointsInsideRectangle = new Stack<Point2D>();
     while (pointsInsideRectangle.empty())
     {
       for (Point2D point : this.range(rectangle))
         pointsInsideRectangle.push(point);
       d *= 2;
       xmin = p.x() - d; xmax = p.x() + d; ymin = p.y() - d; ymax = p.y() + d;
       rectangle = new RectHV(Math.max(0, xmin), Math.max(0, ymin), Math.max(0, xmax), Math.max(0, ymax));
     }
  
     for (Point2D point : this.range(rectangle))
       if (p.distanceSquaredTo(point) < min)
       {
         
         pmin = point;
         min = p.distanceSquaredTo(point);
       }
     return pmin;
   }

   public static void main(String[] args)                  // unit testing of the methods (optional) 
   {
     return;
   }
}