/*
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */

package sm.jegm.graficos;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * La clase MyRoundRectangle representa un rectángulo con las esquinas redondeadas
 * con parámetros fijos (30, 30).
 * 
 * @author juane
 */

/**
 * La clase MyRoundRectangle representa un rectángulo redondeado.
 * @author juane
 */
public class MyRoundRectangle extends MyShape{
    public MyRoundRectangle(Point2D p1, Point2D p2, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke) {
        super();
        myShape = new RoundRectangle2D.Double(p1.getX(), p1.getY(), 0, 0, 30, 30);

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
        ((RoundRectangle2D) myShape).setFrame(p.getX(), p.getY(), ((RoundRectangle2D) myShape).getWidth(), ((RoundRectangle2D) myShape).getHeight());
    }

    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
        ((RoundRectangle2D.Double) myShape).setFrameFromDiagonal(p1, p2);
    }

    @Override
    public Point getLocation() {
        RoundRectangle2D.Double rectangle = (RoundRectangle2D.Double) this.myShape;
        return new Point((int) rectangle.getX(), (int)rectangle.getY());
    }

    @Override
    public Rectangle getBounds() {
        return myShape.getBounds();
    }

    @Override
    public String toString() {
        return "Round Rectangle " + this.id;
    }
}
