/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.graficos;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * La clase MyEllipse representa una figura elipse.
 *
 * @author jegarcia
 */
public class MyEllipse extends MyShape {
    public MyEllipse(Point2D p) {
        myShape = new Ellipse2D.Double(p.getX(), p.getY(), 0, 0);
    }

    public MyEllipse(Point2D p1, Point2D p2) {
        myShape = new Ellipse2D.Double();
        ((Ellipse2D.Double) myShape).setFrameFromDiagonal(p1, p2);
    }

    public MyEllipse(Point2D p1, Point2D p2, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke) {
        myShape = new Ellipse2D.Double();
        ((Ellipse2D.Double) myShape).setFrameFromDiagonal(p1, p2);

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
        ((Ellipse2D.Double) myShape).setFrame(new Rectangle((int)p.getX(), (int)p.getY(), getBounds().width, getBounds().height));
    }

    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
        ((Ellipse2D.Double) myShape).setFrameFromDiagonal(p1, p2);
    }

  @Override
    public Rectangle getBounds() {
        return myShape.getBounds();
    }

    
    @Override
    public Point getLocation() {
        Ellipse2D ellipse = (Ellipse2D) this.myShape;
        return new Point((int) ellipse.getX(), (int) ellipse.getY());
    }
    
    @Override
    public String toString() {
        return "Ellipse " + this.id;
    }
}
