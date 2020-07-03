/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.graficos;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * La clase MyPolyline representa una forma libre que se forma a base de clicks hasta formar
 * una figura cualquiera (con todos sus lados rectos).
 *
 * @author juane
 */
public class MyPolyline extends MyShape {
    private boolean creating = true;
    
    public MyPolyline(Point2D p1, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke) {
        myShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        this.strokeColor = color;
        this.fillColor = fillColor;
        this.shapeFill = typeFill;
        this.smoothed = smoothed;
        this.transparencyDegree = transparency;
        this.shapeGrossor = grossor;
        this.shapeTypeStroke = typeStroke;
        
        ((GeneralPath)this.myShape).moveTo( p1.getX(),p1.getY());
    }
    
    public boolean isCreating(){
        return creating;
    }
    
    public void onClick(Point p1) 
    {
        ((GeneralPath)this.myShape).lineTo(p1.x, p1.y);
    }
    
    public void onDoubleClick() 
    {   
        this.creating = false;
    }
    
    public MyPolyline(Point2D p1, Point2D p2) {
        myShape = new Line2D.Double(p1, p2);

        this.strokeColor = Color.BLACK;
        this.fillColor = Color.GREEN;
        this.shapeFill = TypeFill.SMOOTH_COLOR;
        this.smoothed = false;
        this.transparencyDegree = 1.0F;
        this.shapeGrossor = 2;
        this.shapeTypeStroke = TypeStroke.CONTINUOUS;
    }

    
    @Override
    public boolean contains(Point p) {
        //System.out.println("point mouse: " + p.toString());
        return isNear(p);
    }

    private boolean isNear(Point2D p) {
        return ((Line2D) myShape).ptLineDist(p) <= 5.0;
    }

    
    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
//        nOT IMPLEMENTED
    }

    @Override
    public String toString(){
        return "Line " + this.id;
    }
    
    @Override
    public Point getLocation()
    {
        GeneralPath line = ((GeneralPath) this.myShape);
        return new Point((int) line.getBounds().x, (int) line.getBounds().y);
    }

    @Override
    public void setLocation(Point2D p) {
        Point originalLocation = this.getLocation();

        double dx = p.getX() - originalLocation.getX();
        double dy = p.getY() - originalLocation.getY();
        
        
        ((GeneralPath) this.myShape).transform(AffineTransform.getTranslateInstance(dx,dy));
      
    }
    
    @Override
    public Rectangle getBounds() {
        return myShape.getBounds();
    }

}
