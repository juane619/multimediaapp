/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.graficos;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * La clase MyLine representa una linea recta normal, formándose pulsando y arrastrando el ratón.
 *
 * @author juane
 */
public class MyLine extends MyShape {
    public MyLine(Point2D p1, Point2D p2, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke) {
        myShape = new Line2D.Double(p1, p2);

        this.strokeColor = color;
        this.fillColor = fillColor;
        this.shapeFill = typeFill;
        this.smoothed = smoothed;
        this.transparencyDegree = transparency;
        this.shapeGrossor = grossor;
        this.shapeTypeStroke = typeStroke;
    }
    
    public MyLine(Point2D p1, Point2D p2) {
        myShape = new Line2D.Double(p1, p2);
    }

    
    @Override
    public boolean contains(Point p) {
        //System.out.println("point mouse: " + p.toString());
        return isNear(p);
    }

    private boolean isNear(Point2D p) {
        return ((Line2D) myShape).ptLineDist(p) <= 5.0;
    }

    // Set the new location according new mouse point
    @Override
    public void setLocation(Point2D mousePoint) {
        Line2D.Double auxLine = (Line2D.Double) myShape;
        Point p2aux = new Point((int) (auxLine.x2 - auxLine.x1), (int) (auxLine.y2 - auxLine.y1));
        
        auxLine.setLine(mousePoint, new Point((int)mousePoint.getX()+p2aux.x, (int)mousePoint.getY()+p2aux.y));
    }

    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
        ((Line2D) this.myShape).setLine(p1, p2);
    }

    @Override
    public Rectangle getBounds() {
        return myShape.getBounds();
    }
    
    @Override
    public String toString(){
        return "Line " + this.id;
    }
    
    @Override
    public Point getLocation() {
        Line2D line = (Line2D) this.myShape;
        return new Point((int) line.getX1(), (int) line.getY1());
    }

}
