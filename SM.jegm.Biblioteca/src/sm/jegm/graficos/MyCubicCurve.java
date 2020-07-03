
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */
package sm.jegm.graficos;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * La clase MyCubicCurve representa una curva con dos puntos de control.
 * Se crea arrastrando el ratón como una línea normal y haciendo dos clicks en los puntos 
 * a añadir los puntos de control.
 * 
 * @author juane
 */
public class MyCubicCurve extends MyShape {

    private boolean isCreating = true;
    private int clicksCount = 0;
    private Point cp1 = null, cp2 = null;

    public MyCubicCurve(Point2D p1, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke) {
        super();
        myShape = new CubicCurve2D.Double();
        cp1 = cp2 = (Point) p1;
        ((CubicCurve2D) myShape).setCurve(p1, cp1, cp2, p1);

        this.strokeColor = color;
        this.fillColor = fillColor;
        this.shapeFill = typeFill;
        this.smoothed = smoothed;
        this.transparencyDegree = transparency;
        this.shapeGrossor = grossor;
        this.shapeTypeStroke = typeStroke;
    }

    public boolean isCreating() {
        return isCreating;
    }

    public void onClick(Point p1) {
        if (clicksCount == 0) {
            CubicCurve2D auxMyShape = ((CubicCurve2D) myShape);
            cp1  = p1;
            auxMyShape.setCurve(auxMyShape.getP1(), cp1, cp2, auxMyShape.getP2());
            clicksCount++;
        } else if (clicksCount == 1) {
            CubicCurve2D auxMyShape = ((CubicCurve2D) myShape);
            cp2 = p1;
            auxMyShape.setCurve(auxMyShape.getP1(), cp1, cp2, auxMyShape.getP2());
            isCreating = false;
            clicksCount++;
        }
    }

    @Override
    public Point getLocation() {
        CubicCurve2D auxMyShape = ((CubicCurve2D) myShape);

        return new Point((int) auxMyShape.getBounds().getX(), (int) auxMyShape.getBounds().getY());
    }

    @Override
    public void setLocation(Point2D p) {
        CubicCurve2D auxMyShape = ((CubicCurve2D) myShape);
        Point leftCornerRectangle = new Point((int)auxMyShape.getBounds().getX(), (int)auxMyShape.getBounds().getY());
        
        Point newPoint = new Point((int) (p.getX() - leftCornerRectangle.x), (int) (p.getY() - leftCornerRectangle.y));
        
        //System.out.println("Resta: " + newPoint);
        
        auxMyShape.setCurve(auxMyShape.getX1()+newPoint.x, auxMyShape.getY1()+newPoint.y, auxMyShape.getCtrlX1()+newPoint.x, auxMyShape.getCtrlY1()+newPoint.y, auxMyShape.getCtrlX2()+newPoint.x, auxMyShape.getCtrlY2()+newPoint.y, auxMyShape.getX2()+newPoint.x, auxMyShape.getY2()+newPoint.y);
    }

    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
        CubicCurve2D auxMyShape = ((CubicCurve2D) myShape);
        
        if (clicksCount == 0) {
            auxMyShape.setCurve(auxMyShape.getP1(), cp1, cp2, p2);
        }else  if (clicksCount == 1) {
            cp1 = (Point) p2;
            auxMyShape.setCurve(auxMyShape.getP1(), cp1, cp2, auxMyShape.getP2());
        }else if(clicksCount == 2){
            cp2 = (Point) p2;
            auxMyShape.setCurve(auxMyShape.getP1(), cp1, cp2, auxMyShape.getP2());
        }
    }

    @Override
    public Rectangle getBounds() {
        return myShape.getBounds();
    }

    @Override
    public String toString() {
        return "Cubic Curve " + this.id;
    }
}
