/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * La clase MyShape parte de base de todas las figuras a implementar en la biblioteca
 * conteniendo todas las propiedades de dichas figuras así como la funcionalidad en común
 * de todas estas.
 * 
 * @author jegarcia
 */
public abstract class MyShape {
    /**
     * Numerador auto incremental con cada una de las figuras creadas.
     */
    protected static int NUMERADOR = 0;

    /**
     * ID único de cada figura
     */
    protected int id = NUMERADOR;
    /**
     * Figura interna del API de Java a instanciar según el tipo de figura
     * @see Shape
     */
    protected Shape myShape;
    
    /*
    Propiedades de todas las figuras a crear en la biblioteca
    */
    
    protected Color strokeColor = Color.BLACK;
    protected Color fillColor = Color.GREEN;
    protected Stroke shapeStroke;
    protected TypeStroke shapeTypeStroke = TypeStroke.CONTINUOUS;
    protected TypeFill shapeFill = TypeFill.SMOOTH_COLOR;
    protected RenderingHints shapeSmooth;
    protected AlphaComposite shapeTransparency;
    protected GradientPaint shapeDegradate;
    
    protected boolean smoothed = false;
    protected double transparencyDegree = 0.0F;
    protected int shapeGrossor = 2;

    /**
     * Constructor por defecto, incrementa el numerador con cada una de las figuras creadas.
     */
    public MyShape(){
        NUMERADOR++;
    }
    
    /**
     * Funcionamiento por defecto de cada una de las figuras creadas para pintarse.
     * @param g2d Objeto Graphics2D de la figura a dibujar
     */
    public void draw(Graphics2D g2d) {
        // Stroke
        g2d.setColor(strokeColor);
        
        if (shapeTypeStroke == TypeStroke.CONTINUOUS) {
            shapeStroke = new BasicStroke(shapeGrossor);
            g2d.setStroke(shapeStroke);

        } else { //dotted
            float dash[] = {2.5f};
            shapeStroke = new BasicStroke(shapeGrossor, BasicStroke.JOIN_MITER, BasicStroke.CAP_SQUARE, 0.5F, dash, 0.0F);
            g2d.setStroke(shapeStroke);
        }

        // Transparency (alpha)
        shapeTransparency = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) transparencyDegree);
        g2d.setComposite(shapeTransparency);

        // Smooth
        if (!smoothed) {
            shapeSmooth = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        } else {
            shapeSmooth = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g2d.setRenderingHints(shapeSmooth);

        g2d.draw(myShape);

        // Fill
        g2d.setColor(fillColor);

        if (null != shapeFill) {
            switch (shapeFill) {
                case SMOOTH_COLOR:
                    g2d.setPaint(fillColor);
                    g2d.fill(myShape);
                    break;
                case VERTICAL_DEGRADATE:
                    shapeDegradate = new GradientPaint(myShape.getBounds().x, myShape.getBounds().y, strokeColor, myShape.getBounds().x, myShape.getBounds().y + myShape.getBounds().height, fillColor);
                    g2d.setPaint(shapeDegradate);
                    g2d.fill(myShape);
                    break;
                case HORIZONTAL_DEGRADATE:
                    shapeDegradate = new GradientPaint(myShape.getBounds().x, myShape.getBounds().y, strokeColor, myShape.getBounds().x + myShape.getBounds().width, myShape.getBounds().y, fillColor);
                    g2d.setPaint(shapeDegradate);
                    g2d.fill(myShape);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Funcionamiento por defecto para comprobar si un punto con coordenadas x e y
     * está está dentro de la figura.
     * 
     * @param x coordenada x del punto a comprobar
     * @param y coordenada y del punto a comprobar
     * @return Si esta dentro o no
     */
    public boolean contains(double x, double y) {
        return this.myShape.contains(x, y);
    }

    /**
     * Funcionamiento por defecto para comprobar si un punto pasado
     * está está dentro de la figura.
     * 
     * @param p Punto a comprobar
     * @return Si esta dentro o no
     */
    public boolean contains(Point p) {
        return this.myShape.contains(p.x, p.y);
    }

    /* Abstract methods (interface?)*/
    
    /**
     * Método a implementar en las subclases para especificar la localización
     * de la figura.
     * @param p Punto donde establecer la localización
     */
    public abstract void setLocation(Point2D p);

    /**
     * Método a implementar en las subclases para especificar la localización-tamaño
     * de la figura, pasando p1 como nueva esquina izquierda superior y p2 como
     * nueva esquina derecha inferior del bounding box.
     * @param p1 Punto esquina izqueirda superior
     * @param p2 Punto esquina derecha inferior
     */
    public abstract void updateShapeSize(Point2D p1, Point2D p2);

    /**
     * Método a implementar en las subclases para obtener la localización actual
     * de las figuras
     * @return El punto de localización
     */
    public abstract Point getLocation();
    
    /**
     * Método para obtener el rectángulo que contiene la figuraen su interior
     * @return 
     */
    public abstract Rectangle getBounds();
    

    /* END Abstract methods */
    
    @Override
    public String toString(){
        return "Shape " + this.id;
    }
    
    // setters and getters
    
    public int getId(){
        return this.id;
    }
    
    public Shape getMyShape() {
        return myShape;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Stroke getShapeStroke() {
        return shapeStroke;
    }

    public TypeStroke getShapeTypeStroke() {
        return shapeTypeStroke;
    }

    public TypeFill getShapeFill() {
        return shapeFill;
    }

    public RenderingHints getShapeSmooth() {
        return shapeSmooth;
    }

    public AlphaComposite getShapeTransparency() {
        return shapeTransparency;
    }

    public GradientPaint getShapeDegradate() {
        return shapeDegradate;
    }

    public boolean isSmoothed() {
        return smoothed;
    }

    public double getTransparencyDegree() {
        return transparencyDegree;
    }

    public int getShapeGrossor() {
        return shapeGrossor;
    }

    public void setMyShape(Shape myShape) {
        this.myShape = myShape;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setShapeStroke(Stroke shapeStroke) {
        this.shapeStroke = shapeStroke;
    }

    public void setShapeTypeStroke(TypeStroke shapeTypeStroke) {
        this.shapeTypeStroke = shapeTypeStroke;
    }

    public void setShapeFill(TypeFill shapeRelleno) {
        this.shapeFill = shapeRelleno;
    }

    public void setShapeSmooth(RenderingHints shapeSmooth) {
        this.shapeSmooth = shapeSmooth;
    }

    public void setShapeTransparency(AlphaComposite shapeTransparency) {
        this.shapeTransparency = shapeTransparency;
    }

    public void setShapeDegradate(GradientPaint shapeDegradate) {
        this.shapeDegradate = shapeDegradate;
    }

    public void setSmoothed(boolean smoothed) {
        this.smoothed = smoothed;
    }

    public void setTransparencyDegree(float transparencyDegree) {
        this.transparencyDegree = transparencyDegree;
    }

    public void setShapeGrossor(int shapeGrossor) {
        this.shapeGrossor = shapeGrossor;
    }

    // END setters and getters
}
