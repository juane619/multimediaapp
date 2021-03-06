/*
 * Represents each item of color chooser
 */
package practicaFinal;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * La clase ColorComboBoxRenderer representa la clase que controla el renderizado
 * de los combo box de los colores. Implementa la interfaz ListCellRenderer pa dicho propósito.
 * 
 * @author juane
 */
public class ColorComboBoxRenderer extends javax.swing.JPanel implements ListCellRenderer {

    /**
     * Creates new form ColorComboBoxRenderer
     */
    public ColorComboBoxRenderer() {
        initComponents();
    }

    /**
     * Método que controla cada una de las celdas/elementos del combobox.
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return 
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (index == list.getModel().getSize() - 2) {
            jButtonColor.setText("+");
            jButtonColor.setContentAreaFilled(false);
        } else {
            jButtonColor.setContentAreaFilled(true);
            jButtonColor.setText(" ");
            jButtonColor.setBackground((Color) value);
        }

        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonColor = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jButtonColor.setBackground(new java.awt.Color(255, 255, 255));
        jButtonColor.setMaximumSize(new java.awt.Dimension(1000, 9000));
        jButtonColor.setPreferredSize(new java.awt.Dimension(35, 30));
        add(jButtonColor, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonColor;
    // End of variables declaration//GEN-END:variables
}
