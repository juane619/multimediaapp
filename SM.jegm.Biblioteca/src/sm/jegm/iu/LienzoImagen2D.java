/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.iu;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

/**
 * Clase que representa un lienzo en el que dibujar con la posibilidad de añadir una imagen.
 * 
 * @author juane
 */
public class LienzoImagen2D extends Lienzo2D {

    private BufferedImage panelImage;
    private int currentBrigthnessLevel = 0;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (panelImage != null) {
            g.drawImage(panelImage, 0, 0, this);
        }
        if (this.clip != null) {
            this.setBorderClip((Graphics2D) g);
        }
    }

    /**
     * Paint a dotted border around the image zone
     */
    private void setBorderClip(Graphics2D g2d) {
        Stroke stroke = g2d.getStroke();
        float[] patt = new float[]{
            2.0f, 2.0f
        };
        BasicStroke dottedStroke = new BasicStroke(1.5f, 0, 2, 1.0f, patt, 0.0f);
        g2d.setStroke(dottedStroke);
        g2d.draw(this.clip);
        g2d.setStroke(stroke);
    }

    /* GETTERS AND SETTERS */
    public int getCurrentBrigthnessLevel() {
        return currentBrigthnessLevel;
    }

    public void setCurrentBrigthnessLevel(int currentBrigthnessLevel) {
        this.currentBrigthnessLevel = currentBrigthnessLevel;
    }

    public BufferedImage getPanelImage(boolean drawVector) {
        if (drawVector && panelImage != null) {
            
            BufferedImage fullImage;
            if (panelImage.getType() == BufferedImage.TYPE_CUSTOM) {
                fullImage = new BufferedImage(panelImage.getWidth(), panelImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            } else {
                fullImage = new BufferedImage(panelImage.getWidth(), panelImage.getHeight(), this.panelImage.getType());
            }

            Graphics2D g2d = (Graphics2D) fullImage.getGraphics();

            //setAttributes(g2d);
            g2d.drawImage(panelImage, null, null);
            paintShapeVector(g2d);

            return fullImage;
        } else {
            return panelImage;
        }
    }

    public void setPanelImage(BufferedImage panelImage) {
        this.panelImage = panelImage;

        if (panelImage != null) {
            setPreferredSize(new Dimension(panelImage.getWidth(), panelImage.getHeight()));
            this.setClip(new Rectangle(0, 0, panelImage.getWidth(), panelImage.getHeight()));
        }

    }

    /* END  GETTERS AND SETTERS */
}
