/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicaFinal;

/**
 * La clase VentanaInterna representa cada una de las ventanas internas de las que está formada la aplicación.
 * Extiende de JInternalFrame y es la base de las demás ventanas internas de la aplicación.
 *
 * @author jegarcia
 */
public class VentanaInterna extends javax.swing.JInternalFrame {
    protected VentanaPrincipal parentVP = null;
    protected static int NUMERADOR = 0;

    /**
     * Crea una nueva ventana (formulario) dentro de la ventana principal
     *
     * @param vp: Assign parent of this intern window
     */
    public VentanaInterna(VentanaPrincipal vp) {
        parentVP = vp;
        initComponents();
        NUMERADOR++;
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
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("New " + NUMERADOR);
        setToolTipText("");
        setFocusCycleRoot(false);
        setPreferredSize(new java.awt.Dimension(175, 50));
        setRequestFocusEnabled(false);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Actualiza las barras de herramientas de la ventana principal cuando la ventana es activada (focusada).
     * @param evt 
     */
    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        this.parentVP.setSelectedFrame(this);
        this.parentVP.updateToolbars(this, true);
    }//GEN-LAST:event_formInternalFrameActivated



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
