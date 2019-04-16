import edu.princeton.cs.algs4.Point2D;
import java.util.Stack;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class KdTree 
{

   private Node root;
   private Stack<Point2D> pointsInsideRectangle;
   private Point2D nearestPoint = null;
   private double minSquaredDistance;
   private int sizeOfTree;

   private class Node 
   {
     private final Point2D p;      // the point
     private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
     private Node lb;        // the left/bottom subtree
     private Node rt;        // the right/top subtree
     
     public Node(Point2D p, RectHV rect, Node lb, Node rt)
     {
       this.p = p;
       this.rect = rect;
       this.lb = lb;
       this.rt = rt;
     }
      
     private Node insert(Point2D point, Node subtree, boolean level, RectHV rectangle)
     {
       if (subtree == null) 
       {
         subtree = new Node(point, rectangle, null, null);
         return subtree;
       }

       if (point.equals(subtree.p))
       {
         sizeOfTree--;
         return subtree;
       }

       if (level)
         if (subtree.p.x() > point.x())
           if (subtree.lb == null)
             subtree.lb = this.insert(point, subtree.lb, !level, new RectHV(subtree.rect.xmin(), subtree.rect.ymin(), subtree.p.x(), subtree.rect.ymax()));
           else
             subtree.lb = this.insert(point, subtree.lb, !level, null);
         else
           if (subtree.rt == null)
             subtree.rt = this.insert(point, subtree.rt, !level, new RectHV(subtree.p.x(), subtree.rect.ymin(), subtree.rect.xmax(), subtree.rect.ymax()));
           else
             subtree.rt = this.insert(point, subtree.rt, !level, null);
       else
         if (subtree.p.y() > point.y())
           if (subtree.lb == null)
             subtree.lb = this.insert(point, subtree.lb, !level, new RectHV(subtree.rect.xmin(), subtree.rect.ymin(), subtree.rect.xmax(), subtree.p.y()));
           else
             subtree.lb = this.insert(point, subtree.lb, !level, null);
         else
           if (subtree.rt == null)
             subtree.rt = this.insert(point, subtree.rt, !level, new RectHV(subtree.rect.xmin(), subtree.p.y(), subtree.rect.xmax(), subtree.rect.ymax()));
           else
             subtree.rt = this.insert(point, subtree.rt, !level, null);

       return subtree;
     }
   }
     
   public KdTree()                               // construct an empty set of points 
   {  
      this.root = null; 
      this.sizeOfTree = 0; 
   }

   public boolean isEmpty()                      // is the set empty? 
   { return root == null; }

   public int size()                         // number of points in the set 
   {
     if (this.root == null) 
       return 0;
     else
       return this.sizeOfTree;
   }

   public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
   {
     if (p == null) throw new java.lang.IllegalArgumentException(); 
     this.sizeOfTree++;
     if (root == null)
       root = new Node(p, new RectHV(0, 0, 1, 1), null, null);
     else
       this.root.insert(p, root, true, null);
   }

   public boolean contains(Point2D p)            // does the set contain point p? 
   { 
     if (p == null) throw new java.lang.IllegalArgumentException(); 
     if (this.root != null)
       return this.contains(p, this.root, true); 
     else return false;
   }

   private boolean contains(Point2D p, Node subtree, boolean level)
   {
     if (subtree == null)
       return false;
     if (p.equals(subtree.p))
       return true;
     else
     if (level)
       if (p.x() < subtree.p.x())
         return contains(p, subtree.lb, !level);
       else
         return contains(p, subtree.rt, !level);
     else
       if (p.y() < subtree.p.y())
         return contains(p, subtree.lb, !level);
       else
         return contains(p, subtree.rt, !level);
   }

   public void draw()                         // draw all points to standard draw 
   { 
     draw(root, true);
   }
 
   private void draw(Node subtree, boolean level)
   {  
     StdDraw.setPenColor(StdDraw.BLACK);
     StdDraw.setPenRadius(0.01);
     if (subtree != null)
     {
       draw(subtree.lb, !level);  
       subtree.p.draw();
       if (level)
       {
         StdDraw.setPenColor(StdDraw.RED);
         Point2D a1, a2;
         a1 = new Point2D(subtree.p.x(), subtree.rect.ymin());
         a2 = new Point2D(subtree.p.x(), subtree.rect.ymax());
         a1.drawTo(a2);
       } 
       else
       {
         StdDraw.setPenColor(StdDraw.BLUE);
         Point2D a1, a2;
         a1 = new Point2D(subtree.rect.xmin(), subtree.p.y());
         a2 = new Point2D(subtree.rect.xmax(), subtree.p.y());
         a1.drawTo(a2);
       } 
       draw(subtree.rt, !level);
     }
   }

   public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary) 
   {
     if (rect == null) throw new java.lang.IllegalArgumentException(); 
     pointsInsideRectangle = new Stack<Point2D>();
     range(this.root, rect, true);
     return pointsInsideRectangle;
   }

   private void range(Node subtree, RectHV rect, boolean level)
   {
     if (subtree == null)
       return;
      
     double x = subtree.p.x(), y = subtree.p.y();

     if (level) 
       if (x >= rect.xmin() && x <= rect.xmax())
       {
         if (rect.contains(subtree.p))
           this.pointsInsideRectangle.push(subtree.p);
         range(subtree.lb, rect, !level);
         range(subtree.rt, rect, !level);
       }
       else
         if (x < rect.xmin())
           range(subtree.rt, rect, !level);
         else
           range(subtree.lb, rect, !level);
     else
       if (y >= rect.ymin() && y <= rect.ymax())
       {
         if (rect.contains(subtree.p))
           this.pointsInsideRectangle.push(subtree.p);
         range(subtree.lb, rect, !level);
         range(subtree.rt, rect, !level);
       }
       else
         if (y < rect.ymin())
           range(subtree.rt, rect, !level);
         else
           range(subtree.lb, rect, !level);
   
     /* if (rect.intersects(subtree.rect))
     {
       if (rect.contains(subtree.p))
         this.pointsInsideRectangle.push(subtree.p);
       range(subtree.lb, rect);
       range(subtree.rt, rect);
     }  */  
   }

   public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty 
   {
       if (p == null) throw new java.lang.IllegalArgumentException(); 
       this.minSquaredDistance = Double.POSITIVE_INFINITY;
       nearest(this.root, p, true);
       return this.nearestPoint;
   }

   private void nearest(Node subtree, Point2D p, boolean level)
   {
     if (subtree == null)
       return;
     
     if (this.minSquaredDistance > subtree.rect.distanceSquaredTo(p))
     {
       if (this.minSquaredDistance > p.distanceSquaredTo(subtree.p))
       {
         this.minSquaredDistance = p.distanceSquaredTo(subtree.p);
         this.nearestPoint = subtree.p;
       }
       if (level)                                        
         if (p.x() < subtree.p.x())
         {
           nearest(subtree.lb, p, !level);
           nearest(subtree.rt, p, !level);
         }
         else 
         {
           nearest(subtree.rt, p, !level);
           nearest(subtree.lb, p, !level);
         }
       else
         if (p.y() < subtree.p.y())
         {
           nearest(subtree.lb, p, !level);
           nearest(subtree.rt, p, !level);
         }
         else
         {
           nearest(subtree.rt, p, !level);
           nearest(subtree.lb, p, !level);
         }
     }
   }

   public static void main(String[] args)                  // unit testing of the methods (optional) 
   { 
      /*  String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            brute.insert(p);
        }

      double x0 = 0.0, y0 = 0.0;      // initial endpoint of rectangle
        double x1 = 0.0, y1 = 0.0;      // current location of mouse
        boolean isDragging = false;     // is the user dragging a rectangle

        // draw the points
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        brute.draw();
        StdDraw.show(); 

        // process range search queries
        StdDraw.enableDoubleBuffering();
        while (true) {

            // user starts to drag a rectangle
            if (StdDraw.isMousePressed() && !isDragging) {
                x0 = x1 = StdDraw.mouseX();
                y0 = y1 = StdDraw.mouseY();
                isDragging = true;
            }

            // user is dragging a rectangle
            else if (StdDraw.isMousePressed() && isDragging) {
                x1 = StdDraw.mouseX();
                y1 = StdDraw.mouseY();
            }

            // user stops dragging rectangle
            else if (!StdDraw.isMousePressed() && isDragging) {
                isDragging = false;
            }

            // draw the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw the rectangle
            RectHV rect = new RectHV(Math.min(x0, x1), Math.min(y0, y1),
                                     Math.max(x0, x1), Math.max(y0, y1));
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius();
            rect.draw();

            // draw the range search results for brute-force data structure in red
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            for (Point2D p : brute.range(rect))
                p.draw();

            // draw the range search results for kd-tree in blue
            StdDraw.setPenRadius(0.02);
            StdDraw.setPenColor(StdDraw.BLUE);
            for (Point2D p : kdtree.range(rect))
                p.draw();

            StdDraw.show();
            StdDraw.pause(20);
        } */
       return;
   }
}