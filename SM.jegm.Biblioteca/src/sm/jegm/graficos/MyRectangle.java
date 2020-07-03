/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.graficos;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * La clase MyRectangle representa un rectángulo normal.
 * 
 * @author jegarcia
 */
public class MyRectangle extends MyShape {

    public MyRectangle(Point2D p) {
        super();
        myShape = new Rectangle((int) p.getX(), (int) p.getY(), 0, 0);
    }

    public MyRectangle(Point2D p1, Point2D p2) {
        super();
        myShape = new Rectangle();
        ((Rectangle) myShape).setFrameFromDiagonal(p1, p2);
    }

    public MyRectangle(Point2D p1, Point2D p2, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke) {
        super();
        myShape = new Rectangle();
        ((Rectangle) myShape).setFrameFromDiagonal(p1, p2);

        this.strokeColor = color;
        this.fillColor = fillColor;
        this.shapeFill = typeFill;
        this.smoothed = smoothed;
        this.transparencyDegree = transparency;
        this.shapeGrossor = grossor;
        this.shapeTypeStroke = typeStroke;
    }

    @Override
    public void setLocation(Point2D p) {
        ((Rectangle) myShape).setLocation((Point) p);
    }

    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
        ((Rectangle) myShape).setFrameFromDiagonal(p1, p2);
    }

    @Override
    public Point getLocation() {
        Rectangle rectangle = (Rectangle) this.myShape;
        return new Point(rectangle.x, rectangle.y);
    }

    @Override
    public Rectangle getBounds() {
        return myShape.getBounds();
    }

    @Override
    public String toString() {
        return "Rectangle " + this.id;
    }

}
