/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicaFinal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import sm.jegm.iu.LienzoImagen2D;

/**
 * Ventana interna que contiene una imagen/lienzo editable.
 * @author jegarcia
 */
public class VentanaInternaImage extends VentanaInterna {
    /**
     * Crea una nueva ventqna interna relacionada con las imágenes
     *
     * @param vp: VentanaPrincipal padre de la ventana interna
     */
    public VentanaInternaImage(VentanaPrincipal vp) {
        super(vp);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lienzoImagen2D = new sm.jegm.iu.LienzoImagen2D();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("New: " +this.NUMERADOR);
        setToolTipText("");
        setFocusCycleRoot(false);
        setPreferredSize(new java.awt.Dimension(700, 600));
        setRequestFocusEnabled(false);
        setVisible(true);
        getContentPane().setLayout(new java.awt.CardLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        jScrollPane1.setAutoscrolls(true);

        lienzoImagen2D.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 0), 1, true));
        lienzoImagen2D.setFocusable(false);
        lienzoImagen2D.setMaximumSize(new java.awt.Dimension(32000, 32000));
        lienzoImagen2D.setOpaque(false);
        lienzoImagen2D.setPreferredSize(new java.awt.Dimension(400, 350));
        lienzoImagen2D.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lienzoImagen2DMouseMoved(evt);
            }
        });
        lienzoImagen2D.setLayout(new java.awt.FlowLayout());
        jScrollPane1.setViewportView(lienzoImagen2D);

        getContentPane().add(jScrollPane1, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Actualiza las coordenadas del puntero del mouse en el componente padre.
     * @param evt 
     */
    private void lienzoImagen2DMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lienzoImagen2DMouseMoved
        BufferedImage srcImg = this.getLienzo().getPanelImage(true);
        if (srcImg != null) {
            int pixel[] = null;

            if (evt.getX() >= 0 && evt.getX() < srcImg.getRaster().getWidth() && evt.getY() >= 0 && evt.getY() < srcImg.getRaster().getHeight()) {
                this.parentVP.updatePosition(evt.getX(),evt.getY());

                pixel = srcImg.getRaster().getPixel(evt.getX(), evt.getY(), pixel);
                //int colorMouse = srcImg.getRGB(evt.getPoint().x, evt.getPoint().y);
                if (pixel != null) {
                    Color c;
                    if(pixel.length > 1)
                        c = new Color(pixel[0], pixel[1], pixel[2]);
                    else
                        c = new Color(pixel[0]);
                    this.parentVP.updateColorMouse(c);
                }
            }
        }

    }//GEN-LAST:event_lienzoImagen2DMouseMoved

    public LienzoImagen2D getLienzo() {
        return lienzoImagen2D;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private sm.jegm.iu.LienzoImagen2D lienzoImagen2D;
    // End of variables declaration//GEN-END:variables

}
