/*
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */

package sm.jegm.graficos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;


/**
 * Clase que representa texto a añadir en el lienzo.
 * 
 * Se lanza un diálogo de selección de atributos para el texto a insertar.
 * @author juane
 */


public class MyText extends MyShape{
    Font myFont;
    String font = "";
    int fontStyle = Font.PLAIN;
    int fontSize = 8;
    String text = "Writting..";
    Point position;
    
    Rectangle textBounds;
    
    public MyText(){
        this.position = new Point(20, 20);
    }
    
    public MyText(Point position){
        this.position = position;
    }
    
    public MyText(Point position, String font, int style, int size, String text, Color color, TypeFill typeFill, Color fillColor, boolean smoothed, double transparency, int grossor, TypeStroke typeStroke){
        this.position = position;
        this.font = font;
        this.fontStyle = style;
        this.fontSize = size;
        this.text = text;
        
        this.strokeColor = color;
        this.fillColor = fillColor;
        this.shapeFill = typeFill;
        this.smoothed = smoothed;
        this.transparencyDegree = transparency;
        this.shapeGrossor = grossor;
        this.shapeTypeStroke = typeStroke;
    }
    
    @Override
    public void draw(Graphics2D g2d){
        g2d.setPaint(strokeColor);
        
        if (shapeTypeStroke == TypeStroke.CONTINUOUS) {
            shapeStroke = new BasicStroke(shapeGrossor);
            g2d.setStroke(shapeStroke);

        } else { //dotted
            float dash[] = {2.5f};
            shapeStroke = new BasicStroke(shapeGrossor, BasicStroke.JOIN_MITER, BasicStroke.CAP_SQUARE, 0.5F, dash, 0.0F);
            g2d.setStroke(shapeStroke);
        }
        
         // Smooth
        if (!smoothed) {
            shapeSmooth = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        } else {
            shapeSmooth = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        }
        
        g2d.setRenderingHints(shapeSmooth);
        
        myFont = new Font(font, fontStyle, fontSize);
        
        g2d.setFont(myFont);
        
        FontMetrics textMetrics = g2d.getFontMetrics();
        textBounds = new Rectangle(position.x, position.y, textMetrics.stringWidth(text), textMetrics.getAscent());
        
        g2d.drawString(text, position.x, position.y+textMetrics.getAscent());
    }
    
    @Override
    public void setLocation(Point2D p) {
        this.position = (Point) p;
    }

    @Override
    public void updateShapeSize(Point2D p1, Point2D p2) {
        Point offset = new Point((int)( p1.getX()- p2.getX()), (int) (p1.getY() - p2.getY() ));
        
        if(p2.getX() > getBounds().x+getBounds().width &&  p2.getY() > getBounds().y+getBounds().height)
            fontSize++;
        else if(p2.getX() < getBounds().x+getBounds().width &&  p2.getY() < getBounds().y+getBounds().height)
            fontSize--;
    }

    @Override
    public Point getLocation() {
        return position;
    }
    
    @Override
    public Rectangle getBounds() {
        return textBounds;
    }
    
    @Override
    public String toString(){
        return "Text " + id;
    }
}
