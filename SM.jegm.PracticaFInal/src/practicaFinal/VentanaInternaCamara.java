/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicaFinal;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Clase que representa una ventana interna relacionada con la webcam.
 *
 * @author juane
 */
public class VentanaInternaCamara extends VentanaInterna {

    private Webcam webcam;

    /**
     * Creates new form VentanaInternaSound
     *
     * @param vp Ventana principal padre
     */
    private VentanaInternaCamara(VentanaPrincipal vp) {
        super(vp);
        initComponents();

        try {
            webcam = Webcam.getDefault();
            if (webcam.isOpen()) {
                webcam = null;
            }
            if (webcam != null) {
                Dimension resoluciones[] = webcam.getViewSizes();
                Dimension maxRes = resoluciones[resoluciones.length - 1];
                if (!webcam.isOpen()) {
                    webcam.setViewSize(maxRes);
                }
                WebcamPanel areaVisual = new WebcamPanel(webcam);
                getContentPane().add(areaVisual, BorderLayout.CENTER);
                pack();
            }
        } catch (WebcamException ex) {
            System.err.println("Error creating webcam..");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Webcam: " + this.NUMERADOR);
        setMinimumSize(new java.awt.Dimension(150, 40));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 500));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        close();
    }//GEN-LAST:event_formInternalFrameClosing

    public static VentanaInternaCamara getInstance(VentanaPrincipal vp) {
        VentanaInternaCamara v = new VentanaInternaCamara(vp);
        return (v.webcam != null ? v : null);
    }

    /**
     * Cierra la webcam
     */
    private void close() {
        webcam.close();
    }

    /**
     * Muestra una ventana interna con la imagen capturada de la webcam
     *
     * @return BufferedImage si webcam no es null, null en caso contrario
     */
    public BufferedImage getImage() {
        if (webcam != null) {
            BufferedImage img = webcam.getImage();
            return img;
        }

        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
