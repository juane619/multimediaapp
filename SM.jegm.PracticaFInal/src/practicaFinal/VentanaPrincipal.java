/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */
package practicaFinal;

import sm.jegm.graficos.PainToolType;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.image.BlendOp;
import sm.image.EqualizationOp;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SubtractionOp;
import sm.image.TintOp;
import sm.image.color.GreyColorSpace;
import sm.image.color.YCbCrColorSpace;
import sm.jegm.graficos.MyShape;
import sm.jegm.graficos.TypeFill;
import sm.jegm.graficos.TypeStroke;
import sm.jegm.imagen.DessertOp;
import sm.jegm.imagen.DiffuseOp;
import sm.jegm.imagen.MultiplicationOp;
import sm.jegm.imagen.MySobelOp;
import sm.jegm.imagen.PixelizeOp;
import sm.jegm.imagen.SepiaOp;
import sm.jegm.imagen.UmbralizationOp;
import sm.jegm.myevents.LienzoEvent;
import sm.sound.SMPlayer;
import sm.sound.SMRecorder;
import sm.sound.SMSoundPlayer;
import sm.sound.SMSoundRecorder;
import sm.jegm.myevents.LienzoEventListener;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * La clase VentanaPrincipal define la ventana principal de la aplicación.
 * Define la interfaz de interacción con el usuario. Está formada por todos los
 * controles relacionados con todas las funcionalidades de nuestra aplicación.
 *
 * @author juane
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    /**
     * La clase LienzoEventHandler es la que maneja el evento generado por la
     * clase Lienzo2D al añadir una figura a esta.
     *
     * @see LienzoEventListener
     */
    public class LienzoEventHandler implements LienzoEventListener {

        /**
         * Método que se encarga de actualizar la lista de figuras de la ventana
         * principal cuando se captura el evento LienzoEvent generado por la
         * clase Lienzo2D
         *
         * @param evt Evento LienzoEvent capturado
         * @see LienzoEvent
         */
        @Override
        public void onShapeAdded(LienzoEvent evt) {
            //System.out.println("Figura añadida");
            jTabbedPaneLists.setSelectedIndex(1);
            VentanaInternaImage vi = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            shapesListModel.clear();
            for (MyShape shape : vi.getLienzo().getMyShapes()) {
                shapesListModel.addElement(shape);
            }
        }
    };

    /**
     * La clase MediaPlayerEventHandler es la que maneja los eventos generados
     * por la ventana de reproduccion (video/audio).
     */
    public class MediaPlayerEventHandler extends MediaPlayerEventAdapter {

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            stateReadyToPause();
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            stateReadyToPlayFromPause();
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            this.paused(mediaPlayer);
            stateReadyToPlayFromStop();
        }
    };

    private Color colorsStroke[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN, Color.PINK};
    private Color colorsFill[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN, Color.PINK};

    /**
     * Representa el modelo de la lista de figuras de la ventana interna
     * seleccionada
     */
    private DefaultListModel shapesListModel;

    /**
     * Representa la imagen temporal usada para usar alguna de las operaciones
     * que se incorporan.
     */
    private BufferedImage tmpImage;

    /**
     * Representa la ventana interna usada para mezclar imágenes mediante
     * deslizador
     */
    private VentanaInternaBlend blendWindow;

    // Audio
    /**
     * Representa el reproductor de audio de la aplicación
     */
    private SMPlayer audioPlayer;
    /**
     * Representa el grabador de audio de la aplicación
     */
    private SMRecorder audioRecorder;
    /**
     * Representa el temporizador usado tanto en la reproducción como en la
     * grabación
     */
    private Timer audioTimer;
    private long duration;
    /**
     * Representa el modelo de datos de la lista de reproducción que incorpora
     * la aplicación
     */
    private DefaultListModel playListModel;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    /**
     * Representa el archivo temporal de audio grabado antes de guardar el
     * archivo
     */
    File tmpAudio;
    /**
     * Representa la instancia de la clase manejadora del evento LienzoEvent
     */
    LienzoEventHandler lienzoEventHandler;

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        shapesListModel = new DefaultListModel();
        playListModel = new DefaultListModel();

        initComponents();

        jComboBoxStrokeColor.setModel(
                new DefaultComboBoxModel(colorsStroke));

        jComboBoxStrokeColor.setRenderer(
                new ColorComboBoxRenderer());

        jComboBoxFillColor.setModel(
                new DefaultComboBoxModel(colorsFill));
        jComboBoxFillColor.setRenderer(
                new ColorComboBoxRenderer());

        lienzoEventHandler = new LienzoEventHandler();
        
        addJTextCoordXListener();
        addJTextCoordYListener();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupColors = new javax.swing.ButtonGroup();
        buttonGroupForms = new javax.swing.ButtonGroup();
        buttonGroupAttributes = new javax.swing.ButtonGroup();
        buttonGroupFilling = new javax.swing.ButtonGroup();
        jPanelSuperior = new javax.swing.JPanel();
        jToolBarForms = new javax.swing.JToolBar();
        jButtonNew = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonDuplicate = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jToggleButtonPoint = new javax.swing.JToggleButton();
        jToggleButtonLine = new javax.swing.JToggleButton();
        jToggleButtonCubicCurve = new javax.swing.JToggleButton();
        jToggleButtonRect = new javax.swing.JToggleButton();
        jToggleButtonRoundRect = new javax.swing.JToggleButton();
        jToggleButtonElli = new javax.swing.JToggleButton();
        jToggleButtonPolyline = new javax.swing.JToggleButton();
        jToggleButtonText = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jComboBoxStrokeColor = new javax.swing.JComboBox<>();
        jComboBoxFillColor = new javax.swing.JComboBox<>();
        jPanelLocation = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextCoordX = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextCoordY = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jSpinnerThickness = new javax.swing.JSpinner();
        jComboBoxTypeStroke = new javax.swing.JComboBox<>();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jToggleButtonNotFill = new javax.swing.JToggleButton();
        jToggleButtonFill = new javax.swing.JToggleButton();
        jToggleButtonDegradateH = new javax.swing.JToggleButton();
        jToggleButtonDegradateV = new javax.swing.JToggleButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jSliderTransparency = new javax.swing.JSlider();
        jToggleButtonSmooth = new javax.swing.JToggleButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        jToolBarAudio = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        jButtonPlay = new javax.swing.JButton();
        jButtonStop = new javax.swing.JButton();
        jToggleButtonRecord = new javax.swing.JToggleButton();
        jLabelElapsedTime = new javax.swing.JLabel();
        jProgressBar = new javax.swing.JProgressBar();
        jLabelTotalTime = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jToolBarVideo = new javax.swing.JToolBar();
        jPanel3 = new javax.swing.JPanel();
        jButtonWebcam = new javax.swing.JButton();
        jButtonScreenshot = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelCentral = new javax.swing.JPanel();
        mainDesktop = new javax.swing.JDesktopPane();
        jPanelCentralDerecha = new javax.swing.JPanel();
        jTabbedPaneLists = new javax.swing.JTabbedPane();
        jScrollPanePlaylist = new javax.swing.JScrollPane();
        jListPlayList = new javax.swing.JList<>();
        jScrollPaneShapeslist = new javax.swing.JScrollPane();
        jListShapesList = new javax.swing.JList<>();
        jPanelInferior = new javax.swing.JPanel();
        jScrollPaneTools = new javax.swing.JScrollPane();
        jPanelTools = new javax.swing.JPanel();
        jPanelImageBrightness = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSliderBrightness = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        jPanelImageFilter = new javax.swing.JPanel();
        jComboBoxFilter = new javax.swing.JComboBox<>();
        jPanelImageContrast = new javax.swing.JPanel();
        jButtonConstrast = new javax.swing.JButton();
        jButtonBrightConstrast = new javax.swing.JButton();
        jButtonDarkContrast = new javax.swing.JButton();
        jPanelImageOpers = new javax.swing.JPanel();
        jButtonNegative = new javax.swing.JButton();
        jButtonTinted = new javax.swing.JButton();
        jButtonEqualization = new javax.swing.JButton();
        jButtonSepia = new javax.swing.JButton();
        jButtonSinusoidal = new javax.swing.JButton();
        jButtonSobel = new javax.swing.JButton();
        jButtonPixelize = new javax.swing.JButton();
        jButtonDiffuse = new javax.swing.JButton();
        jButtonDessertOp = new javax.swing.JButton();
        jPanelColorOpers = new javax.swing.JPanel();
        jButtonExtractBands = new javax.swing.JButton();
        jComboBoxColorSpace = new javax.swing.JComboBox<>();
        jPanelImageRotations = new javax.swing.JPanel();
        jSliderRotate = new javax.swing.JSlider();
        jButtonRotate90 = new javax.swing.JButton();
        jButtonRotate180 = new javax.swing.JButton();
        jButtonRotate270 = new javax.swing.JButton();
        jPanelZoom = new javax.swing.JPanel();
        jButtonZoomPlus = new javax.swing.JButton();
        jButtonZoomMinus = new javax.swing.JButton();
        jPanelBinaries = new javax.swing.JPanel();
        jButtonBinaryPlus = new javax.swing.JButton();
        jButtonBinaryMinus = new javax.swing.JButton();
        jButtonBinaryProduct = new javax.swing.JButton();
        jSliderBinaries = new javax.swing.JSlider();
        jPanelImageUmbralization = new javax.swing.JPanel();
        jSliderUmbralization = new javax.swing.JSlider();
        jPanelBinaries1 = new javax.swing.JPanel();
        jSliderOwnOper = new javax.swing.JSlider();
        jLabelOwnOper = new javax.swing.JLabel();
        jButtonOwnOper = new javax.swing.JButton();
        jPanelStatus = new javax.swing.JPanel();
        jTextFieldToolSelected = new javax.swing.JTextField();
        jTextFieldCoordinates = new javax.swing.JTextField();
        jTextFieldColorMouse = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuView = new javax.swing.JMenu();
        jCheckBoxMenuItemFormsBar = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuSound = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemToolsBar = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemStatusBar = new javax.swing.JCheckBoxMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SMM app");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(500, 273));
        setName("Main Frame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1024, 768));

        jPanelSuperior.setLayout(new javax.swing.BoxLayout(jPanelSuperior, javax.swing.BoxLayout.PAGE_AXIS));

        jToolBarForms.setBorder(null);
        jToolBarForms.setFloatable(false);
        jToolBarForms.setRollover(true);
        jToolBarForms.setPreferredSize(new java.awt.Dimension(980, 70));

        jButtonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/new.png"))); // NOI18N
        jButtonNew.setToolTipText("New");
        jButtonNew.setFocusable(false);
        jButtonNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewActionPerformed(evt);
            }
        });
        jToolBarForms.add(jButtonNew);

        jButtonOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/open2.png"))); // NOI18N
        jButtonOpen.setToolTipText("Open");
        jButtonOpen.setFocusable(false);
        jButtonOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jToolBarForms.add(jButtonOpen);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/save.png"))); // NOI18N
        jButtonSave.setToolTipText("Save");
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBarForms.add(jButtonSave);

        jButtonDuplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/duplicate3.png"))); // NOI18N
        jButtonDuplicate.setToolTipText("Duplicate");
        jButtonDuplicate.setFocusable(false);
        jButtonDuplicate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDuplicate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDuplicateActionPerformed(evt);
            }
        });
        jToolBarForms.add(jButtonDuplicate);
        jToolBarForms.add(jSeparator1);

        buttonGroupForms.add(jToggleButtonPoint);
        jToggleButtonPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/punto.png"))); // NOI18N
        jToggleButtonPoint.setSelected(true);
        jToggleButtonPoint.setToolTipText("Point");
        jToggleButtonPoint.setFocusable(false);
        jToggleButtonPoint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonPoint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPointActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonPoint);

        buttonGroupForms.add(jToggleButtonLine);
        jToggleButtonLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/linea.png"))); // NOI18N
        jToggleButtonLine.setToolTipText("Line");
        jToggleButtonLine.setFocusable(false);
        jToggleButtonLine.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonLine.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonLineActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonLine);

        buttonGroupForms.add(jToggleButtonCubicCurve);
        jToggleButtonCubicCurve.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/curve.png"))); // NOI18N
        jToggleButtonCubicCurve.setToolTipText("Cubic Line");
        jToggleButtonCubicCurve.setFocusable(false);
        jToggleButtonCubicCurve.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonCubicCurve.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonCubicCurve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCubicCurveActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonCubicCurve);

        buttonGroupForms.add(jToggleButtonRect);
        jToggleButtonRect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/rectangulo.png"))); // NOI18N
        jToggleButtonRect.setToolTipText("Rectangle");
        jToggleButtonRect.setFocusable(false);
        jToggleButtonRect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonRect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonRect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonRectActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonRect);

        buttonGroupForms.add(jToggleButtonRoundRect);
        jToggleButtonRoundRect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/rounded-rectangle.png"))); // NOI18N
        jToggleButtonRoundRect.setToolTipText("Round rectangle");
        jToggleButtonRoundRect.setFocusable(false);
        jToggleButtonRoundRect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonRoundRect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonRoundRect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonRoundRectActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonRoundRect);

        buttonGroupForms.add(jToggleButtonElli);
        jToggleButtonElli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/elipse.png"))); // NOI18N
        jToggleButtonElli.setToolTipText("Ellipse");
        jToggleButtonElli.setFocusable(false);
        jToggleButtonElli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonElli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonElli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonElliActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonElli);

        buttonGroupForms.add(jToggleButtonPolyline);
        jToggleButtonPolyline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/polyline.png"))); // NOI18N
        jToggleButtonPolyline.setToolTipText("Polyline");
        jToggleButtonPolyline.setFocusable(false);
        jToggleButtonPolyline.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonPolyline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonPolyline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPolylineActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonPolyline);

        buttonGroupForms.add(jToggleButtonText);
        jToggleButtonText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/edit.png"))); // NOI18N
        jToggleButtonText.setToolTipText("Text");
        jToggleButtonText.setFocusable(false);
        jToggleButtonText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonText.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTextActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonText);
        jToolBarForms.add(jSeparator2);

        jComboBoxStrokeColor.setToolTipText("Stroke color");
        jComboBoxStrokeColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStrokeColorActionPerformed(evt);
            }
        });
        jToolBarForms.add(jComboBoxStrokeColor);

        jComboBoxFillColor.setToolTipText("Fill color");
        jComboBoxFillColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFillColorActionPerformed(evt);
            }
        });
        jToolBarForms.add(jComboBoxFillColor);

        jPanelLocation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Location", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));
        jPanelLocation.setPreferredSize(new java.awt.Dimension(200, 60));
        java.awt.FlowLayout flowLayout4 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT);
        flowLayout4.setAlignOnBaseline(true);
        jPanelLocation.setLayout(flowLayout4);

        jLabel1.setText("X: ");
        jPanelLocation.add(jLabel1);

        jTextCoordX.setText("0");
        jTextCoordX.setToolTipText("");
        jTextCoordX.setPreferredSize(new java.awt.Dimension(55, 22));
        jPanelLocation.add(jTextCoordX);

        jLabel2.setText("Y: ");
        jPanelLocation.add(jLabel2);

        jTextCoordY.setText("0");
        jTextCoordY.setPreferredSize(new java.awt.Dimension(55, 22));
        jPanelLocation.add(jTextCoordY);

        jToolBarForms.add(jPanelLocation);
        jToolBarForms.add(jSeparator3);

        jSpinnerThickness.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jSpinnerThickness.setToolTipText("Grossor");
        jSpinnerThickness.setMinimumSize(new java.awt.Dimension(30, 28));
        jSpinnerThickness.setPreferredSize(new java.awt.Dimension(50, 28));
        jSpinnerThickness.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerThicknessStateChanged(evt);
            }
        });
        jToolBarForms.add(jSpinnerThickness);

        jComboBoxTypeStroke.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CONTINUOUS", "DOTTED" }));
        jComboBoxTypeStroke.setToolTipText("Stroke type");
        jComboBoxTypeStroke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTypeStrokeActionPerformed(evt);
            }
        });
        jToolBarForms.add(jComboBoxTypeStroke);
        jToolBarForms.add(jSeparator4);

        buttonGroupFilling.add(jToggleButtonNotFill);
        jToggleButtonNotFill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/no-bucket.png"))); // NOI18N
        jToggleButtonNotFill.setSelected(true);
        jToggleButtonNotFill.setToolTipText("Not fill");
        jToggleButtonNotFill.setFocusable(false);
        jToggleButtonNotFill.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonNotFill.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonNotFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonNotFillActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonNotFill);

        buttonGroupFilling.add(jToggleButtonFill);
        jToggleButtonFill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/paint-bucket.png"))); // NOI18N
        jToggleButtonFill.setToolTipText("Smooth fill");
        jToggleButtonFill.setFocusable(false);
        jToggleButtonFill.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonFill.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonFillActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonFill);

        buttonGroupFilling.add(jToggleButtonDegradateH);
        jToggleButtonDegradateH.setText("H");
        jToggleButtonDegradateH.setToolTipText("Horizontal degradate");
        jToggleButtonDegradateH.setFocusable(false);
        jToggleButtonDegradateH.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonDegradateH.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonDegradateH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonDegradateHActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonDegradateH);

        buttonGroupFilling.add(jToggleButtonDegradateV);
        jToggleButtonDegradateV.setText("V");
        jToggleButtonDegradateV.setToolTipText("Vertical degradate");
        jToggleButtonDegradateV.setFocusable(false);
        jToggleButtonDegradateV.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonDegradateV.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonDegradateV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonDegradateVActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonDegradateV);
        jToolBarForms.add(jSeparator5);

        jSliderTransparency.setToolTipText("Changue transparency");
        jSliderTransparency.setValue(100);
        jSliderTransparency.setPreferredSize(new java.awt.Dimension(70, 29));
        jSliderTransparency.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderTransparencyStateChanged(evt);
            }
        });
        jToolBarForms.add(jSliderTransparency);

        jToggleButtonSmooth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/general/aliasing.png"))); // NOI18N
        jToggleButtonSmooth.setToolTipText("Anti-aliasing");
        jToggleButtonSmooth.setFocusable(false);
        jToggleButtonSmooth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonSmooth.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonSmooth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSmoothActionPerformed(evt);
            }
        });
        jToolBarForms.add(jToggleButtonSmooth);
        jToolBarForms.add(jSeparator6);

        jPanelSuperior.add(jToolBarForms);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jToolBarAudio.setBorder(null);
        jToolBarAudio.setFloatable(false);
        jToolBarAudio.setRollover(true);
        jToolBarAudio.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jToolBarAudio.setPreferredSize(new java.awt.Dimension(410, 70));

        jPanel2.setPreferredSize(new java.awt.Dimension(175, 33));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jButtonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/play.png"))); // NOI18N
        jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPlayActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonPlay);

        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/stop24x24.png"))); // NOI18N
        jButtonStop.setToolTipText("Stop");
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonStop);

        jToggleButtonRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/record24x24.png"))); // NOI18N
        jToggleButtonRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonRecordActionPerformed(evt);
            }
        });
        jPanel2.add(jToggleButtonRecord);

        jToolBarAudio.add(jPanel2);

        jLabelElapsedTime.setText("00:00");
        jToolBarAudio.add(jLabelElapsedTime);
        jToolBarAudio.add(jProgressBar);

        jLabelTotalTime.setText("00:00");
        jToolBarAudio.add(jLabelTotalTime);
        jToolBarAudio.add(jSeparator10);
        jToolBarAudio.add(jSeparator7);

        jPanel1.add(jToolBarAudio);

        jToolBarVideo.setBorder(null);
        jToolBarVideo.setFloatable(false);
        jToolBarVideo.setRollover(true);
        jToolBarVideo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jToolBarVideo.setPreferredSize(new java.awt.Dimension(130, 70));

        jPanel3.setPreferredSize(new java.awt.Dimension(130, 33));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jButtonWebcam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/video/Camara.png"))); // NOI18N
        jButtonWebcam.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonWebcam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWebcamActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonWebcam);

        jButtonScreenshot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/video/Capturar.png"))); // NOI18N
        jButtonScreenshot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScreenshotActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonScreenshot);

        jToolBarVideo.add(jPanel3);
        jToolBarVideo.add(jSeparator8);

        jPanel1.add(jToolBarVideo);

        jPanelSuperior.add(jPanel1);

        getContentPane().add(jPanelSuperior, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(1800);

        jPanelCentral.setLayout(new javax.swing.BoxLayout(jPanelCentral, javax.swing.BoxLayout.X_AXIS));

        mainDesktop.setBackground(new java.awt.Color(204, 204, 204));
        mainDesktop.setForeground(new java.awt.Color(153, 153, 153));
        mainDesktop.setFocusCycleRoot(false);
        mainDesktop.setPreferredSize(new java.awt.Dimension(50, 50));
        jPanelCentral.add(mainDesktop);

        jSplitPane1.setLeftComponent(jPanelCentral);

        jPanelCentralDerecha.setLayout(new javax.swing.BoxLayout(jPanelCentralDerecha, javax.swing.BoxLayout.LINE_AXIS));

        jTabbedPaneLists.setToolTipText("");

        jListPlayList.setModel(playListModel);
        jListPlayList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jListPlayList.setAutoscrolls(false);
        jListPlayList.setPreferredSize(new java.awt.Dimension(225, 500));
        jListPlayList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListPlayListValueChanged(evt);
            }
        });
        jScrollPanePlaylist.setViewportView(jListPlayList);

        jTabbedPaneLists.addTab("PlayList", jScrollPanePlaylist);

        jListShapesList.setModel(shapesListModel);
        jListShapesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListShapesList.setAutoscrolls(false);
        jListShapesList.setPreferredSize(new java.awt.Dimension(225, 500));
        jListShapesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListShapesListValueChanged(evt);
            }
        });
        jScrollPaneShapeslist.setViewportView(jListShapesList);

        jTabbedPaneLists.addTab("Shapes List", jScrollPaneShapeslist);

        jPanelCentralDerecha.add(jTabbedPaneLists);

        jSplitPane1.setRightComponent(jPanelCentralDerecha);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanelInferior.setAutoscrolls(true);
        jPanelInferior.setLayout(new javax.swing.BoxLayout(jPanelInferior, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelTools.setAutoscrolls(true);
        jPanelTools.setEnabled(false);
        jPanelTools.setPreferredSize(new java.awt.Dimension(1244, 200));
        java.awt.FlowLayout flowLayout3 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT);
        flowLayout3.setAlignOnBaseline(true);
        jPanelTools.setLayout(flowLayout3);

        jPanelImageBrightness.setBorder(javax.swing.BorderFactory.createTitledBorder("Brigthness"));
        jPanelImageBrightness.setLayout(new java.awt.GridLayout(1, 1));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/brigthness_light.png"))); // NOI18N
        jLabel3.setMinimumSize(new java.awt.Dimension(1, 1));
        jLabel3.setPreferredSize(new java.awt.Dimension(10, 24));
        jPanelImageBrightness.add(jLabel3);

        jSliderBrightness.setMaximum(255);
        jSliderBrightness.setMinimum(-255);
        jSliderBrightness.setToolTipText("Changue brightness");
        jSliderBrightness.setValue(0);
        jSliderBrightness.setPreferredSize(new java.awt.Dimension(60, 29));
        jSliderBrightness.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderBrightnessStateChanged(evt);
            }
        });
        jSliderBrightness.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSliderBrightnessFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jSliderBrightnessFocusLost(evt);
            }
        });
        jPanelImageBrightness.add(jSliderBrightness);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/brightness_dark.png"))); // NOI18N
        jLabel4.setIconTextGap(1);
        jLabel4.setMinimumSize(new java.awt.Dimension(1, 1));
        jLabel4.setName(""); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(10, 10));
        jPanelImageBrightness.add(jLabel4);

        jPanelTools.add(jPanelImageBrightness);

        jPanelImageFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        jPanelImageFilter.setMinimumSize(new java.awt.Dimension(108, 70));
        jPanelImageFilter.setPreferredSize(new java.awt.Dimension(175, 70));
        jPanelImageFilter.setLayout(new java.awt.GridLayout(1, 1));

        jComboBoxFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select filter..", "Medium blurring (3x3)", "Binomial blurring (3x3)", "Focus", "Relief", "Laplace border detector", "Medium blurring (5x5)", "Medium blurring (7x7)", "Gaussian Filter (5x5)" }));
        jComboBoxFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFilterFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jComboBoxFilterFocusLost(evt);
            }
        });
        jComboBoxFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterActionPerformed(evt);
            }
        });
        jPanelImageFilter.add(jComboBoxFilter);

        jPanelTools.add(jPanelImageFilter);

        jPanelImageContrast.setBorder(javax.swing.BorderFactory.createTitledBorder("Contrast"));
        jPanelImageContrast.setDoubleBuffered(false);
        jPanelImageContrast.setEnabled(false);
        jPanelImageContrast.setMinimumSize(new java.awt.Dimension(96, 70));
        jPanelImageContrast.setLayout(new java.awt.GridLayout(1, 3));

        jButtonConstrast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/contraste.png"))); // NOI18N
        jButtonConstrast.setToolTipText("Normal");
        jButtonConstrast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConstrastActionPerformed(evt);
            }
        });
        jPanelImageContrast.add(jButtonConstrast);

        jButtonBrightConstrast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/iluminar.png"))); // NOI18N
        jButtonBrightConstrast.setToolTipText("Iluminated");
        jButtonBrightConstrast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrightConstrastActionPerformed(evt);
            }
        });
        jPanelImageContrast.add(jButtonBrightConstrast);

        jButtonDarkContrast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/oscurecer.png"))); // NOI18N
        jButtonDarkContrast.setToolTipText("Darkened");
        jButtonDarkContrast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDarkContrastActionPerformed(evt);
            }
        });
        jPanelImageContrast.add(jButtonDarkContrast);

        jPanelTools.add(jPanelImageContrast);

        jPanelImageOpers.setBorder(javax.swing.BorderFactory.createTitledBorder("Operations"));
        jPanelImageOpers.setDoubleBuffered(false);
        jPanelImageOpers.setEnabled(false);
        jPanelImageOpers.setMinimumSize(new java.awt.Dimension(400, 58));
        jPanelImageOpers.setPreferredSize(new java.awt.Dimension(175, 100));
        jPanelImageOpers.setLayout(new java.awt.GridLayout(2, 3));

        jButtonNegative.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/negative.png"))); // NOI18N
        jButtonNegative.setToolTipText("Negative");
        jButtonNegative.setAlignmentY(0.0F);
        jButtonNegative.setBorderPainted(false);
        jButtonNegative.setMaximumSize(new java.awt.Dimension(41, 30));
        jButtonNegative.setPreferredSize(new java.awt.Dimension(25, 25));
        jButtonNegative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNegativeActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonNegative);

        jButtonTinted.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/tintar.png"))); // NOI18N
        jButtonTinted.setToolTipText("Tinted");
        jButtonTinted.setAlignmentY(0.0F);
        jButtonTinted.setBorderPainted(false);
        jButtonTinted.setPreferredSize(new java.awt.Dimension(25, 25));
        jButtonTinted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTintedActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonTinted);

        jButtonEqualization.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/ecualizar.png"))); // NOI18N
        jButtonEqualization.setToolTipText("Equalization");
        jButtonEqualization.setAlignmentY(0.0F);
        jButtonEqualization.setBorderPainted(false);
        jButtonEqualization.setPreferredSize(new java.awt.Dimension(25, 25));
        jButtonEqualization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEqualizationActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonEqualization);

        jButtonSepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/sepia.png"))); // NOI18N
        jButtonSepia.setToolTipText("Sepia");
        jButtonSepia.setAlignmentY(0.0F);
        jButtonSepia.setBorderPainted(false);
        jButtonSepia.setPreferredSize(new java.awt.Dimension(25, 25));
        jButtonSepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSepiaActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonSepia);

        jButtonSinusoidal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/sinusoidal.png"))); // NOI18N
        jButtonSinusoidal.setToolTipText("Sine");
        jButtonSinusoidal.setAlignmentX(10.0F);
        jButtonSinusoidal.setAlignmentY(0.0F);
        jButtonSinusoidal.setBorderPainted(false);
        jButtonSinusoidal.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonSinusoidal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSinusoidalActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonSinusoidal);

        jButtonSobel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/sobel.png"))); // NOI18N
        jButtonSobel.setToolTipText("Sobel");
        jButtonSobel.setAlignmentY(0.0F);
        jButtonSobel.setBorderPainted(false);
        jButtonSobel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSobelActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonSobel);

        jButtonPixelize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/pixelize.png"))); // NOI18N
        jButtonPixelize.setToolTipText("Pixelize");
        jButtonPixelize.setAlignmentY(0.0F);
        jButtonPixelize.setBorderPainted(false);
        jButtonPixelize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPixelizeActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonPixelize);

        jButtonDiffuse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/difusse.png"))); // NOI18N
        jButtonDiffuse.setToolTipText("Diffuse");
        jButtonDiffuse.setAlignmentY(0.0F);
        jButtonDiffuse.setBorderPainted(false);
        jButtonDiffuse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiffuseActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonDiffuse);

        jButtonDessertOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/dessert.png"))); // NOI18N
        jButtonDessertOp.setToolTipText("Dessert");
        jButtonDessertOp.setAlignmentY(0.0F);
        jButtonDessertOp.setBorderPainted(false);
        jButtonDessertOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDessertOpActionPerformed(evt);
            }
        });
        jPanelImageOpers.add(jButtonDessertOp);

        jPanelTools.add(jPanelImageOpers);

        jPanelColorOpers.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));
        jPanelColorOpers.setPreferredSize(new java.awt.Dimension(170, 70));
        jPanelColorOpers.setLayout(new java.awt.GridLayout(1, 0));

        jButtonExtractBands.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/bandas.png"))); // NOI18N
        jButtonExtractBands.setToolTipText("Bands extraction");
        jButtonExtractBands.setMaximumSize(new java.awt.Dimension(40, 40));
        jButtonExtractBands.setMinimumSize(new java.awt.Dimension(20, 20));
        jButtonExtractBands.setPreferredSize(new java.awt.Dimension(20, 20));
        jButtonExtractBands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExtractBandsActionPerformed(evt);
            }
        });
        jPanelColorOpers.add(jButtonExtractBands);

        jComboBoxColorSpace.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sRGB", "YCC", "GREY" }));
        jComboBoxColorSpace.setToolTipText("Spaces converter");
        jComboBoxColorSpace.setMinimumSize(new java.awt.Dimension(70, 22));
        jComboBoxColorSpace.setPreferredSize(new java.awt.Dimension(57, 30));
        jComboBoxColorSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxColorSpaceActionPerformed(evt);
            }
        });
        jPanelColorOpers.add(jComboBoxColorSpace);

        jPanelTools.add(jPanelColorOpers);

        jPanelImageRotations.setBorder(javax.swing.BorderFactory.createTitledBorder("Rotations"));
        jPanelImageRotations.setPreferredSize(new java.awt.Dimension(300, 70));
        jPanelImageRotations.setLayout(new java.awt.GridLayout(1, 4));

        jSliderRotate.setMaximum(360);
        jSliderRotate.setMinorTickSpacing(90);
        jSliderRotate.setPaintTicks(true);
        jSliderRotate.setToolTipText("Rotation");
        jSliderRotate.setValue(0);
        jSliderRotate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderRotateStateChanged(evt);
            }
        });
        jSliderRotate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSliderRotateFocusGained(evt);
            }
        });
        jPanelImageRotations.add(jSliderRotate);

        jButtonRotate90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/rotacion90.png"))); // NOI18N
        jButtonRotate90.setToolTipText("90 degrees");
        jButtonRotate90.setFocusable(false);
        jButtonRotate90.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRotate90.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRotate90.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRotate90ActionPerformed(evt);
            }
        });
        jPanelImageRotations.add(jButtonRotate90);

        jButtonRotate180.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/rotacion180.png"))); // NOI18N
        jButtonRotate180.setToolTipText("180 degrees");
        jButtonRotate180.setFocusable(false);
        jButtonRotate180.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRotate180.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRotate180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRotate180ActionPerformed(evt);
            }
        });
        jPanelImageRotations.add(jButtonRotate180);

        jButtonRotate270.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/rotacion270.png"))); // NOI18N
        jButtonRotate270.setToolTipText("270 degrees");
        jButtonRotate270.setFocusable(false);
        jButtonRotate270.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRotate270.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRotate270.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRotate270ActionPerformed(evt);
            }
        });
        jPanelImageRotations.add(jButtonRotate270);

        jPanelTools.add(jPanelImageRotations);

        jPanelZoom.setBorder(javax.swing.BorderFactory.createTitledBorder("Scale"));
        jPanelZoom.setLayout(new java.awt.GridLayout(1, 0));

        jButtonZoomPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/zoomin2.png"))); // NOI18N
        jButtonZoomPlus.setToolTipText("Enlarge");
        jButtonZoomPlus.setFocusable(false);
        jButtonZoomPlus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonZoomPlus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonZoomPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZoomPlusActionPerformed(evt);
            }
        });
        jPanelZoom.add(jButtonZoomPlus);

        jButtonZoomMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/zoomout2.png"))); // NOI18N
        jButtonZoomMinus.setToolTipText("Reduce");
        jButtonZoomMinus.setFocusable(false);
        jButtonZoomMinus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonZoomMinus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonZoomMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZoomMinusActionPerformed(evt);
            }
        });
        jPanelZoom.add(jButtonZoomMinus);

        jPanelTools.add(jPanelZoom);

        jPanelBinaries.setBorder(javax.swing.BorderFactory.createTitledBorder("Binaries"));
        jPanelBinaries.setLayout(new java.awt.GridLayout(1, 0));

        jButtonBinaryPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/aumentar.png"))); // NOI18N
        jButtonBinaryPlus.setToolTipText("Plus");
        jButtonBinaryPlus.setFocusable(false);
        jButtonBinaryPlus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonBinaryPlus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonBinaryPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBinaryPlusActionPerformed(evt);
            }
        });
        jPanelBinaries.add(jButtonBinaryPlus);

        jButtonBinaryMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/disminuir.png"))); // NOI18N
        jButtonBinaryMinus.setToolTipText("Minus");
        jButtonBinaryMinus.setFocusable(false);
        jButtonBinaryMinus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonBinaryMinus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonBinaryMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBinaryMinusActionPerformed(evt);
            }
        });
        jPanelBinaries.add(jButtonBinaryMinus);

        jButtonBinaryProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/image/multiply.png"))); // NOI18N
        jButtonBinaryProduct.setToolTipText("Multiply");
        jButtonBinaryProduct.setFocusable(false);
        jButtonBinaryProduct.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonBinaryProduct.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonBinaryProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBinaryProductActionPerformed(evt);
            }
        });
        jPanelBinaries.add(jButtonBinaryProduct);

        jSliderBinaries.setPaintLabels(true);
        jSliderBinaries.setPaintTicks(true);
        jSliderBinaries.setSnapToTicks(true);
        jSliderBinaries.setToolTipText("Blend");
        jSliderBinaries.setPreferredSize(new java.awt.Dimension(70, 29));
        jSliderBinaries.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderBinariesStateChanged(evt);
            }
        });
        jSliderBinaries.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSliderBinariesFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jSliderBinariesFocusLost(evt);
            }
        });
        jPanelBinaries.add(jSliderBinaries);

        jPanelTools.add(jPanelBinaries);

        jPanelImageUmbralization.setBorder(javax.swing.BorderFactory.createTitledBorder("Umbralization"));
        jPanelImageUmbralization.setLayout(new java.awt.GridLayout(1, 0));

        jSliderUmbralization.setMaximum(255);
        jSliderUmbralization.setToolTipText("Umbralization");
        jSliderUmbralization.setValue(127);
        jSliderUmbralization.setPreferredSize(new java.awt.Dimension(70, 29));
        jSliderUmbralization.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderUmbralizationStateChanged(evt);
            }
        });
        jSliderUmbralization.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSliderUmbralizationFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jSliderUmbralizationFocusLost(evt);
            }
        });
        jPanelImageUmbralization.add(jSliderUmbralization);

        jPanelTools.add(jPanelImageUmbralization);

        jPanelBinaries1.setBorder(javax.swing.BorderFactory.createTitledBorder("Own abs operation"));
        jPanelBinaries1.setLayout(new java.awt.GridLayout(1, 0));

        jSliderOwnOper.setMaximum(255);
        jSliderOwnOper.setPaintLabels(true);
        jSliderOwnOper.setPaintTicks(true);
        jSliderOwnOper.setToolTipText("Absolute op");
        jSliderOwnOper.setValue(128);
        jSliderOwnOper.setPreferredSize(new java.awt.Dimension(70, 29));
        jSliderOwnOper.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderOwnOperStateChanged(evt);
            }
        });
        jSliderOwnOper.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSliderOwnOperFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jSliderOwnOperFocusLost(evt);
            }
        });
        jPanelBinaries1.add(jSliderOwnOper);

        jLabelOwnOper.setText("128");
        jPanelBinaries1.add(jLabelOwnOper);

        jButtonOwnOper.setText("Apply");
        jButtonOwnOper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOwnOperActionPerformed(evt);
            }
        });
        jPanelBinaries1.add(jButtonOwnOper);

        jPanelTools.add(jPanelBinaries1);

        jScrollPaneTools.setViewportView(jPanelTools);

        jPanelInferior.add(jScrollPaneTools);

        jPanelStatus.setToolTipText("");
        jPanelStatus.setMinimumSize(new java.awt.Dimension(102, 40));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT);
        flowLayout1.setAlignOnBaseline(true);
        jPanelStatus.setLayout(flowLayout1);

        jTextFieldToolSelected.setEditable(false);
        jTextFieldToolSelected.setText("Point");
        jTextFieldToolSelected.setMinimumSize(new java.awt.Dimension(70, 60));
        jTextFieldToolSelected.setPreferredSize(new java.awt.Dimension(120, 30));
        jPanelStatus.add(jTextFieldToolSelected);

        jTextFieldCoordinates.setEditable(false);
        jTextFieldCoordinates.setText("None");
        jTextFieldCoordinates.setMinimumSize(new java.awt.Dimension(6, 30));
        jTextFieldCoordinates.setPreferredSize(new java.awt.Dimension(150, 30));
        jPanelStatus.add(jTextFieldCoordinates);

        jTextFieldColorMouse.setEditable(false);
        jTextFieldColorMouse.setText("None");
        jTextFieldColorMouse.setMinimumSize(new java.awt.Dimension(6, 30));
        jTextFieldColorMouse.setPreferredSize(new java.awt.Dimension(120, 30));
        jPanelStatus.add(jTextFieldColorMouse);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setText(" ");
        jButton1.setFocusable(false);
        jButton1.setRequestFocusEnabled(false);
        jPanelStatus.add(jButton1);

        jPanelInferior.add(jPanelStatus);

        getContentPane().add(jPanelInferior, java.awt.BorderLayout.SOUTH);

        jMenuFile.setText("File");

        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem1);

        jMenuItem2.setText("Open");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem2);

        jMenuItem3.setText("Save");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem3);

        jMenuBarMain.add(jMenuFile);

        jMenuView.setText("View");

        jCheckBoxMenuItemFormsBar.setSelected(true);
        jCheckBoxMenuItemFormsBar.setText("Tools bar");
        jCheckBoxMenuItemFormsBar.setToolTipText("");
        jCheckBoxMenuItemFormsBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemFormsBarActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemFormsBar);

        jCheckBoxMenuSound.setSelected(true);
        jCheckBoxMenuSound.setText("Sounds bar");
        jCheckBoxMenuSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuSoundActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuSound);

        jCheckBoxMenuItemToolsBar.setSelected(true);
        jCheckBoxMenuItemToolsBar.setText("Image bar");
        jCheckBoxMenuItemToolsBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemToolsBarActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemToolsBar);

        jCheckBoxMenuItemStatusBar.setSelected(true);
        jCheckBoxMenuItemStatusBar.setText("State bar");
        jCheckBoxMenuItemStatusBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemStatusBarActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemStatusBar);

        jMenuBarMain.add(jMenuView);

        jMenuHelp.setText("Help");

        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBarMain.add(jMenuHelp);

        setJMenuBar(jMenuBarMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Add jtextxcoordx listener
     */
    private void addJTextCoordXListener() {
        jTextCoordX.getDocument().addDocumentListener(new DocumentListener() {
            public void updateSelectedShape() {
                VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

                if (vi != null) {
                    VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
                    try {
                        int x = 0;
                        if (!jTextCoordX.getText().equals("")) {
                            x = Integer.parseInt(jTextCoordX.getText());
                        }
                        if (x < 0) {
                            JOptionPane.showMessageDialog(null,
                                    "Coordinates must be positives", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            selectedWindow.getLienzo().setLocationSelectedShape(x, -1);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Insert only integers", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override

            public void insertUpdate(DocumentEvent e) {
                updateSelectedShape();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSelectedShape();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSelectedShape();
            }
        }
        );
    }

    /**
     * Add jtextcoordy listener
     */
    private void addJTextCoordYListener() {
        // Listen for changes in the text
        jTextCoordY.getDocument().addDocumentListener(new DocumentListener() {
            public void updateSelectedShape() {
                VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

                if (vi != null) {
                    VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
                    try {
                        int y = 0;
                        if (!jTextCoordY.getText().equals("")) {
                            y = Integer.parseInt(jTextCoordY.getText());
                        }
                        if (y < 0) {
                            JOptionPane.showMessageDialog(null,
                                    "Coordinates must be positives", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            selectedWindow.getLienzo().setLocationSelectedShape(-1, y);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Insert only integers", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override

            public void insertUpdate(DocumentEvent e) {
                updateSelectedShape();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSelectedShape();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSelectedShape();
            }
        }
        );
    }


    /* TOP MENU / MAIN BAR */
    /**
     * Muestra el diálogo del tamaño de la nueva imagen a crear y crea una nueva
     * imagen dentro de una nueva ventana interna
     *
     * @param evt
     */
    private void jButtonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewActionPerformed

        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        // By default, image of 800x600 px
        xField.setText("800");
        yField.setText("600");

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Width:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Height:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Specify image size", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            VentanaInternaImage vi = new VentanaInternaImage(this);
            vi.getLienzo().addLienzoEventListener(lienzoEventHandler);

            mainDesktop.add(vi);
            vi.setVisible(true);
            BufferedImage img = new BufferedImage(Integer.parseInt(xField.getText()), Integer.parseInt(yField.getText()), BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgG2d = (Graphics2D) img.getGraphics();
            imgG2d.setColor(Color.WHITE);
            imgG2d.fill(new Rectangle(img.getWidth(), img.getHeight()));
            vi.getLienzo().setPanelImage(img);

            try {
                vi.setSelected(true);
                vi.setMaximum(true);
                vi.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            } catch (java.beans.PropertyVetoException e) {
            }
            mainDesktop.setSelectedFrame(vi);
            vi.moveToFront();
        }

    }//GEN-LAST:event_jButtonNewActionPerformed

    /**
     * Lanza el diálogo para abrir un nuevo archivo, de los permitidos por la
     * aplicación (audio e imágenes) actuando de diferente forma según el
     * archivo elegido.
     *
     * @param evt
     */
    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        JFileChooser dlg = new JFileChooser();

        dlg.setFileFilter(new FileNameExtensionFilter("Image files (*.jpg, *.jpeg, *.gif, *.png)", "jpg", "jpeg", "gif", "png"));
        dlg.setFileFilter(new FileNameExtensionFilter("Audio files (*.wav, *.au)", "wav", "au"));
        dlg.setFileFilter(new FileNameExtensionFilter("Video files (*.avi, *.mp4, *.mpg)", "avi", "mp4", "mpg"));
        dlg.setFileFilter(new FileNameExtensionFilter("All supported files", "wav", "au", "jpg", "jpeg", "gif", "png", "avi", "mp4", "mpg", "avi"));

        dlg.setAcceptAllFileFilterUsed(false);

        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = new File(dlg.getSelectedFile().getAbsolutePath()) {
                    @Override
                    public String toString() {
                        return this.getName();
                    }
                };

                if (f.getName().endsWith(".wav") || f.getName().endsWith(".au")) {
                    jTabbedPaneLists.setSelectedIndex(0);
                    playListModel.addElement(f);
                    jListPlayList.setSelectedIndex(playListModel.getSize() - 1);
                } else if (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg") || f.getName().endsWith(".gif") || f.getName().endsWith(".png")) {
                    VentanaInternaImage vi = new VentanaInternaImage(this);
                    vi.getLienzo().addLienzoEventListener(lienzoEventHandler);

                    mainDesktop.add(vi);
                    vi.setVisible(true);

                    BufferedImage img = ImageIO.read(f);
                    vi.getLienzo().setPanelImage(img);
                    vi.setTitle(f.getName());

                    try {
                        vi.setSelected(true);
                        vi.setMaximum(true);
                        vi.moveToFront();
                        mainDesktop.setSelectedFrame(vi);
                    } catch (java.beans.PropertyVetoException e) {
                        System.err.println("Error in image intern windows: " + e.getMessage());
                    }
                } else {
                    VentanaInternaVLCPlayer vi = VentanaInternaVLCPlayer.getInstance(this, f);
                    vi.addMediaPlayerEventListener(new MediaPlayerEventHandler());

                    mainDesktop.add(vi);
                    vi.setVisible(true);
                    vi.setTitle(f.getName());

                    try {
                        vi.setSelected(true);
                        vi.setMaximum(true);
                        vi.moveToFront();
                        mainDesktop.setSelectedFrame(vi);
                    } catch (java.beans.PropertyVetoException e) {
                        System.err.println("Error in video intern windows: " + e.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error opening image..: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_jButtonOpenActionPerformed

    /**
     * Lanza el diálogo de guardado de la posible imagen de la ventana interna
     * del momento en el formato especificado por el usuario.
     *
     * @param evt
     */
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            JFileChooser dlg = new JFileChooser() {
                @Override
                public void approveSelection() {
                    File f = getSelectedFile();
                    if (f.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                super.approveSelection();
                                return;
                            case JOptionPane.NO_OPTION:
                                return;
                            case JOptionPane.CLOSED_OPTION:
                                return;
                            case JOptionPane.CANCEL_OPTION:
                                cancelSelection();
                                return;
                        }
                    }
                    super.approveSelection();
                }

            };

            // set file name as windows name
            dlg.setSelectedFile(new File(selectedWindow.getTitle()));

            // set filters to save img
            dlg.setFileFilter(new FileNameExtensionFilter("PNG image (*.png)", "png"));
            dlg.setFileFilter(new FileNameExtensionFilter("GIF image (*.gif)", "gif"));
            dlg.setFileFilter(new FileNameExtensionFilter("JPG image (*.jpg)", "jpg"));

            dlg.setAcceptAllFileFilterUsed(false);

            int resp = dlg.showSaveDialog(this);
            if (resp == JFileChooser.APPROVE_OPTION) {
                try {
                    /* Save the image with filter extension select always*/
                    BufferedImage img = selectedWindow.getLienzo().getPanelImage(true);

                    if (img != null) {
                        File f = dlg.getSelectedFile();

                        String filename = "", ext = null;

                        if (f.getName().contains(".")) {
                            filename = f.getName().substring(0, f.getName().lastIndexOf("."));
                            ext = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length());
                        } else {
                            filename = f.getName();
                        }

                        // check if supported extension
                        if (ext == null || ext.isEmpty()) {
                            ext = ((FileNameExtensionFilter) dlg.getFileFilter()).getExtensions()[0];

                        } else if (!ext.equals(((FileNameExtensionFilter) dlg.getFileFilter()).getExtensions()[0])) {
                            ext = ((FileNameExtensionFilter) dlg.getFileFilter()).getExtensions()[0];
                        } else if (!ext.equals("jpg") && !ext.equals("png") && !ext.equals("gif")) {
                            // jpg by default
                            ext = "jpg";
                        }
                        f = new File(f.getParent(), filename + "." + ext);
                        ImageIO.write(img, ext, f);
                        selectedWindow.setTitle(f.getName());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error saving image..: " + ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    /**
     * En caso de haber alguna imagen en alguna ventana interna, se crea una
     * ventana interna nueva con la copia de dicha imagen.
     *
     * @param evt
     */
    private void jButtonDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDuplicateActionPerformed
        VentanaInternaImage selectedVi = (VentanaInternaImage) this.mainDesktop.getSelectedFrame();

        if (selectedVi != null) {
            VentanaInternaImage newVi = new VentanaInternaImage(this);
            newVi.getLienzo().addLienzoEventListener(lienzoEventHandler);
            mainDesktop.add(newVi);
            newVi.setVisible(true);
            newVi.setTitle("Copy of " + selectedVi.getTitle());

            BufferedImage img = selectedVi.getLienzo().getPanelImage(true);

            newVi.getLienzo().setPanelImage(img);

            try {
                newVi.setSelected(true);
                newVi.moveToFront();
            } catch (PropertyVetoException ex) {
                System.err.println("Error setting selected windows..");
            }
        }
    }//GEN-LAST:event_jButtonDuplicateActionPerformed

    /**
     * Muestra/oculta el panel indicado.
     *
     * @param evt
     */
    private void jCheckBoxMenuItemFormsBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemFormsBarActionPerformed
        this.jToolBarForms.setVisible(jCheckBoxMenuItemFormsBar.getState());
    }//GEN-LAST:event_jCheckBoxMenuItemFormsBarActionPerformed

    /**
     * Muestra/oculta el panel indicado.
     *
     * @param evt
     */
    private void jCheckBoxMenuItemToolsBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemToolsBarActionPerformed
        this.jPanelTools.setVisible(jCheckBoxMenuItemToolsBar.getState());
    }//GEN-LAST:event_jCheckBoxMenuItemToolsBarActionPerformed

    /**
     * Muestra/oculta el panel indicado.
     *
     * @param evt
     */
    private void jCheckBoxMenuItemStatusBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemStatusBarActionPerformed
        this.jPanelStatus.setVisible(jCheckBoxMenuItemStatusBar.getState());
    }//GEN-LAST:event_jCheckBoxMenuItemStatusBarActionPerformed

    /**
     * Muestra/oculta el panel indicado.
     *
     * @param evt
     */
    private void jCheckBoxMenuSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuSoundActionPerformed
        jToolBarAudio.setVisible(jCheckBoxMenuSound.getState());
    }//GEN-LAST:event_jCheckBoxMenuSoundActionPerformed

    /**
     * Muestra el diálogo de información sobre la aplicación.
     *
     * @param evt
     */
    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        JOptionPane.showMessageDialog(this, "Nombre del programa: SMM app\nVersion: 1.0.0\nAutor: JuanE García", "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    /* END TOP MENU / MAIN BAR */
 /* SHAPES BUTTONS */
    /**
     * Selecciona el botón de dibujar linea, cambia al cursor de creación y
     * deselecciona en caso de que esté, la figura seleccionada
     *
     * @param evt
     */
    private void jToggleButtonPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonPointActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.POINT);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Point");
        //updateToolbars(selectedWindow);
    }//GEN-LAST:event_jToggleButtonPointActionPerformed

    /**
     * Selecciona el botón de dibujar linea, cambia al cursor de creación y
     * deselecciona en caso de que esté, la figura seleccionada
     *
     * @param evt
     */
    private void jToggleButtonLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonLineActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.LINE);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Line");
        //updateToolbars(selectedWindow);
    }//GEN-LAST:event_jToggleButtonLineActionPerformed

    /**
     * Selecciona el botón de dibujar rectángulos, cambia al cursor de creación
     * y deselecciona en caso de que esté, la figura seleccionada
     *
     * @param evt
     */
    private void jToggleButtonRectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonRectActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.RECTANGLE);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Rectangle");
        //updateToolbars(selectedWindow);
    }//GEN-LAST:event_jToggleButtonRectActionPerformed

    /**
     * Selecciona el botón de dibujar elipses, cambia al cursor de creación y
     * deselecciona en caso de que esté, la figura seleccionada
     *
     * @param evt
     */
    private void jToggleButtonElliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonElliActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.ELLIPSE);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Ellipse");
        //updateToolbars(selectedWindow);
    }//GEN-LAST:event_jToggleButtonElliActionPerformed

    /**
     * Selecciona el botón de dibujar polilineas, cambia al cursor de creación y
     * deselecciona en caso de que esté, la figura seleccionada
     */
private void jToggleButtonPolylineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonPolylineActionPerformed
    VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

    if (vi != null && vi instanceof VentanaInternaImage) {
        VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
        selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.POLYLINE);
        selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        selectedWindow.getLienzo().deselectSelectedShape();
    }
    jTextFieldToolSelected.setText("Polyline");
    }//GEN-LAST:event_jToggleButtonPolylineActionPerformed


    /* END SHAPES BUTTONS */
 /* SHAPES ATTRIBUTES */
    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jComboBoxStrokeColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStrokeColorActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            if (jComboBoxStrokeColor.getSelectedIndex() == jComboBoxStrokeColor.getItemCount() - 2) {
                Color newColor = JColorChooser.showDialog(null, "Choose a color", null);
                selectedWindow.getLienzo().setCurrentColor(newColor);
                colorsStroke[6] = newColor;
                jComboBoxStrokeColor.setModel(
                        new DefaultComboBoxModel(colorsStroke));
                jComboBoxStrokeColor.setSelectedIndex(6);
            } else {
                selectedWindow.getLienzo().setCurrentColor((Color) jComboBoxStrokeColor.getSelectedItem());
            }
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jComboBoxStrokeColorActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jComboBoxFillColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFillColorActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            if (jComboBoxFillColor.getSelectedIndex() == jComboBoxFillColor.getItemCount() - 2) {
                Color newColor = JColorChooser.showDialog(null, "Choose a color", null);
                selectedWindow.getLienzo().setFillColor(newColor);
                colorsFill[6] = newColor;
                jComboBoxFillColor.setModel(
                        new DefaultComboBoxModel(colorsFill));
                jComboBoxFillColor.setSelectedIndex(6);
            } else {
                selectedWindow.getLienzo().setFillColor((Color) jComboBoxFillColor.getSelectedItem());
            }
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jComboBoxFillColorActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jSpinnerThicknessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerThicknessStateChanged
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setGrossorValue((int) jSpinnerThickness.getValue());
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jSpinnerThicknessStateChanged

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jComboBoxTypeStrokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTypeStrokeActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            if (jComboBoxTypeStroke.getSelectedItem().equals("CONTINUOUS")) {
                selectedWindow.getLienzo().setCurrentTypeStroke(TypeStroke.CONTINUOUS);
            } else if (jComboBoxTypeStroke.getSelectedItem().equals("DOTTED")) {
                selectedWindow.getLienzo().setCurrentTypeStroke(TypeStroke.DOTTED);
            }
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jComboBoxTypeStrokeActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jToggleButtonNotFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonNotFillActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentTypeFill(TypeFill.UNFILLED);
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jToggleButtonNotFillActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jToggleButtonFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonFillActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentTypeFill(TypeFill.SMOOTH_COLOR);
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jToggleButtonFillActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jToggleButtonDegradateHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonDegradateHActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentTypeFill(TypeFill.HORIZONTAL_DEGRADATE);
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jToggleButtonDegradateHActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jToggleButtonDegradateVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonDegradateVActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentTypeFill(TypeFill.VERTICAL_DEGRADATE);
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jToggleButtonDegradateVActionPerformed

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jSliderTransparencyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderTransparencyStateChanged
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setTransparencyLevel(jSliderTransparency.getValue() / 100.0);
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jSliderTransparencyStateChanged

    /**
     * Actualiza la propiedad indicada de la figura seleccionada actualmente o
     * de la nueva figura a crear
     *
     * @param evt
     */
    private void jToggleButtonSmoothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonSmoothActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setIsSmooth(jToggleButtonSmooth.isSelected());
            selectedWindow.getLienzo().updateShapeSelectedStyle();
        }
    }//GEN-LAST:event_jToggleButtonSmoothActionPerformed


    /* END SHAPES ATTRIBUTES */
 /* AUDIO BAR */
    /**
     * En caso de que se haya cargado alguna pista en la lista de reproducción y
     * haya alguna seleccionada, comienza la reproducción
     *
     * @param evt
     */
    private void jButtonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPlayActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaVLCPlayer) {
            VentanaInternaVLCPlayer viP = (VentanaInternaVLCPlayer) mainDesktop.getSelectedFrame();

            if (!isPlaying) {
                viP.play();
            } else {
                viP.stop();
            }
        } else {
            if (jListPlayList.getSelectedIndex() != -1) {
                if (!isPlaying) {
                    if (isPaused) {//start to play from pause

                    } else { //start to play from stop
                        try {
                            jTabbedPaneLists.setSelectedIndex(0);
                            File f = (File) playListModel.get(jListPlayList.getSelectedIndex());
                            audioPlayer = new SMSoundPlayer(f);

                            if (audioPlayer != null) {
                                // Get audio duration
                                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);
                                AudioFormat format = audioInputStream.getFormat();
                                long frames = audioInputStream.getFrameLength();
                                this.duration = 1000 * (int) ((frames + 0.) / format.getFrameRate());
                                // END get audio duration
                                //System.out.println("Duration (ms): " + (this.duration));
                                jLabelTotalTime.setText(formatSeconds(this.duration, "%02d:%02d"));
                                this.jProgressBar.setMinimum(0);
                                this.jProgressBar.setMaximum((int) (this.duration));

                                audioTimer = new Timer(1, new ActionListener() {
                                    private int elapsedMiliSeconds = 0;

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        //System.out.println(elapsedMiliSeconds);
                                        jLabelElapsedTime.setText(formatSeconds(elapsedMiliSeconds, "%02d:%02d"));
                                        jProgressBar.setValue(elapsedMiliSeconds += 5);
                                    }
                                });

                                ((SMSoundPlayer) audioPlayer).addLineListener((LineEvent event) -> {
                                    if (event.getType() == LineEvent.Type.START) {
                                        //System.out.println("Starting reproduction..");
                                        stateReadyToPause();

                                        audioTimer.start();
                                    }
                                    if (event.getType() == LineEvent.Type.STOP) {
                                        //System.out.println("Stopping reproduction..");
                                        stateReadyToPlayFromStop();
                                    }
                                    if (event.getType() == LineEvent.Type.CLOSE) {
                                        //System.out.println("Clossing reproduction..");
                                    }
                                });

                                this.audioPlayer.play();
                            }

                        } catch (UnsupportedAudioFileException | IOException ex) {
                            System.err.println("Error in audio windows..");
                        }

                    }
                } else { // to pause the song
                    // not implemented
                }
            }
        }
    }//GEN-LAST:event_jButtonPlayActionPerformed

    /**
     * En caso de que haya alguna pista en reproducción, para dicha reproducción
     *
     * @param evt
     */
    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        if (isPlaying || isPaused) {
            VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

            if (vi != null && vi instanceof VentanaInternaVLCPlayer) {
                VentanaInternaVLCPlayer viP = (VentanaInternaVLCPlayer) mainDesktop.getSelectedFrame();

                viP.stop();
                viP.stop();
            } else {
                this.audioTimer.stop();
                this.audioPlayer.stop();
            }
            stateReadyToPlayFromStop();
        }
    }//GEN-LAST:event_jButtonStopActionPerformed

    /**
     * Comienza la grabación de una pista de sonido, con la posibilidad de
     * guardarlo al final. Para la grabación de una nueva pista en caso de que
     * se haya empezado a grabar previamente.
     *
     * @param evt
     */
    private void jToggleButtonRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonRecordActionPerformed
        if (!jToggleButtonRecord.isSelected()) {
            this.audioRecorder.stop();
            jToggleButtonRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/record24x24.png")));

            JFileChooser dlg = new JFileChooser() {
                @Override
                public void approveSelection() {
                    File f = getSelectedFile();
                    if (f.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                super.approveSelection();
                                return;
                            case JOptionPane.NO_OPTION:
                                return;
                            case JOptionPane.CLOSED_OPTION:
                                return;
                            case JOptionPane.CANCEL_OPTION:
                                cancelSelection();
                                return;
                        }
                    }
                    super.approveSelection();
                }

            };

            // save audio record
            dlg.setSelectedFile(new File("new_record.wav"));

            dlg.setFileFilter(new FileNameExtensionFilter("Supported formats (*.wav, *.au)", "wav", "au"));
            dlg.setAcceptAllFileFilterUsed(false);

            int ans = dlg.showSaveDialog(this);
            if (ans == JFileChooser.APPROVE_OPTION) {
                try {

                    File f = new File(dlg.getSelectedFile().getAbsolutePath()) {
                        @Override
                        public String toString() {
                            return this.getName();
                        }
                    };

                    if (f.exists()) {
                        f.delete();

                        f = new File(dlg.getSelectedFile().getAbsolutePath()) {
                            @Override
                            public String toString() {
                                return this.getName();
                            }
                        };
                    }
                    String filename = f.getName();
                    String ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

                    if (ext.length() == 0 || !("wav".equals(ext) || "au".equals(ext))) {

                        ext = "wav";
                        f = new File(f.getAbsoluteFile() + ".wav");
                    }

                    if (tmpAudio.renameTo(f)) {
                        //System.out.print(f.toString());
                        playListModel.addElement(f);
                    } else {
                        System.err.println("Error saving record sound..");
                    }

                    jToggleButtonRecord.setToolTipText("Record");
                    jLabelElapsedTime.setText("00:00");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error saving record sound..");
                }
            }
        } else {
            jToggleButtonRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/stopRecord24x24.png")));
            try {
                this.tmpAudio = File.createTempFile("tmp_recording", ".tmp");
            } catch (IOException ex) {
                System.err.println("Error creating temporal file to audio.." + ex.getMessage());
            }
            this.audioRecorder = new SMSoundRecorder(this.tmpAudio);
            ((SMSoundRecorder) this.audioRecorder).addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.START) {
                        //System.out.println("Starting recording..");

                        audioTimer = new Timer(1000, new ActionListener() {
                            private int elapsedSeconds = 0;

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int minutes = (elapsedSeconds % 3600) / 60;
                                int seconds = elapsedSeconds % 60;

                                String timeString = String.format("%02d:%02d", minutes, seconds);

                                jLabelElapsedTime.setText(timeString);
                                elapsedSeconds++;
                            }
                        });

                        audioTimer.start();
                    }
                    if (event.getType() == LineEvent.Type.STOP) {
                        //System.out.println("Stopping recording..");
                        audioTimer.stop();
                        jLabelTotalTime.setText("00:00");
                    }
                    if (event.getType() == LineEvent.Type.CLOSE) {
                        //System.out.println("Clossing recording..");
                    }
                }
            }
            );
            this.audioRecorder.record();

            jToggleButtonRecord.setToolTipText("Stop recording...");
        }
    }//GEN-LAST:event_jToggleButtonRecordActionPerformed

    /**
     * Cambio al estado de preparado para reproducir desde stop
     */
    private void stateReadyToPlayFromStop() {
        isPlaying = false;
        isPaused = false;
        jButtonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/play24x24.png")));
        jButtonPlay.setToolTipText("Play");
        jLabelElapsedTime.setText("00:00");
        this.jProgressBar.setValue(0);
    }

    /**
     * Cambio al estado de preparado para reproducir desde pause
     */
    private void stateReadyToPlayFromPause() {
        isPlaying = false;
        isPaused = true;
        jButtonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/play24x24.png")));
        jButtonPlay.setToolTipText("Play");
    }

    /**
     * Cambio al estado de preparado para pausar desde reproduccion
     */
    private void stateReadyToPause() {
        isPlaying = true;
        jButtonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practicaFinal/iconos/audio/pausa24x24.png")));
        jButtonPlay.setToolTipText("Pause");
    }


    /* END AUDIO BAR */
 /* SHAPES LIST */
    /**
     * En caso de seleccionar una figura de la lista de figuras, se actualizan
     * las propiedades de la figura seleccionada en la ventana principal, se
     * cambia al cursor de movimiento (para poder mover la figura arrastrando y
     * soltando).
     *
     * @param evt
     */
    private void jListShapesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListShapesListValueChanged
        if (evt.getValueIsAdjusting() == false) {
            int index = jListShapesList.getSelectedIndex();
            VentanaInterna vi = (VentanaInterna) this.mainDesktop.getSelectedFrame();
            if (vi != null && vi instanceof VentanaInternaImage && index != -1) {
                VentanaInternaImage selectedWindow = (VentanaInternaImage) vi;
                //System.out.println("Index: " + index);
                selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.MOVE_CURSOR));
                selectedWindow.getLienzo().setSelectedShape(index);
                selectedWindow.getLienzo().updateProperties(index);
                updateToolbars(vi, false);
            }
        }
    }//GEN-LAST:event_jListShapesListValueChanged

    /* END SHAPES LIST */
 /* IMAGE TOOLBARS */
    /**
     * Aplica la operación de brillo a la posible imagen de la ventana interna
     * seleccionada, actualizando todas las ventanas internas del momento.
     *
     * @param evt
     */
    private void jSliderBrightnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderBrightnessStateChanged
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage && this.tmpImage != null) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            try {
                RescaleOp rop;
                if (this.tmpImage.getColorModel().hasAlpha()) {
                    float[] scales = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
                    float vBrillo = this.jSliderBrightness.getValue();
                    float[] offsets = new float[]{vBrillo, vBrillo, vBrillo, 0.0f};
                    rop = new RescaleOp(scales, offsets, null);
                } else {
                    rop = new RescaleOp(1.0f, this.jSliderBrightness.getValue(), null);
                }
                rop.filter(this.tmpImage, selectedWindow.getLienzo().getPanelImage(false));

                for (JInternalFrame internalFrame : this.mainDesktop.getAllFrames()) {
                    if (internalFrame instanceof VentanaInterna) {
                        ((VentanaInternaImage) internalFrame).getLienzo().repaint();
                    }
                }

            } catch (Exception e) {
                System.err.println("Error changing state of brigthness: " + e.getMessage() + e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_jSliderBrightnessStateChanged

    /**
     * Copia la imagen de la ventana actual seleccionada en caso de que haya
     * alguna al ganar el foco el slider de brillo en la variable imagen
     * auxiliar de la ventana.
     *
     * @param evt
     */
    private void jSliderBrightnessFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderBrightnessFocusGained
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            ColorModel cm = selectedWindow.getLienzo().getPanelImage(false).getColorModel();
            WritableRaster raster = selectedWindow.getLienzo().getPanelImage(false).copyData(null);
            boolean alfaPre = selectedWindow.getLienzo().getPanelImage(false).isAlphaPremultiplied();
            tmpImage = new BufferedImage(cm, raster, alfaPre, null);
        }

    }//GEN-LAST:event_jSliderBrightnessFocusGained

    /**
     * Apunta a null la imagen auxiliar y setea a cero el slider de brillo
     *
     * @param evt
     */
    private void jSliderBrightnessFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderBrightnessFocusLost
        this.tmpImage = null;
        this.jSliderBrightness.setValue(0);
    }//GEN-LAST:event_jSliderBrightnessFocusLost

    /**
     * Aplica el filtro seleccionado a la posible imagen de la ventana interna
     * seleccionada
     *
     * @param evt
     */
    private void jComboBoxFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFilterActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            this.tmpImage = selectedWindow.getLienzo().getPanelImage(false);
            if (this.tmpImage != null) {
                try {
                    int selectedIndexFilter = this.jComboBoxFilter.getSelectedIndex();
                    Kernel k = null;
                    if (selectedIndexFilter >= 1 && selectedIndexFilter <= 5) {
                        //System.out.println("Filter " + selectedIndexFilter);
                        k = KernelProducer.createKernel(selectedIndexFilter - 1);
                    } else {
                        // otros filtros a parte del paquete image
                        if (selectedIndexFilter == 6) { // media 5x5
                            // create the convolution kernel 5x5
                            float m[] = new float[25];
                            Arrays.fill(m, (float) 0.04);
                            k = new Kernel(5, 5, m);
                        } else if (selectedIndexFilter == 7) { // media 7x7
                            float cellValue = (float) (1.0 / 49);
                            float m[] = new float[49];
                            Arrays.fill(m, cellValue);
                            k = new Kernel(7, 7, m);
                        } else if (selectedIndexFilter == 8) { // Gaussian filter
                            int kernelSize = 5;
                            float sigma = (float) 1.0;
                            float mykernel[][] = generateKernel(sigma, kernelSize);
                            float m[] = new float[kernelSize * kernelSize];

//                            System.out.println();
//                            for (float[] fila : mykernel) {
//                                for (float elem : fila) {
//                                    System.out.print(elem + " ");
//                                }
//                                System.out.println();
//                            }
                            // Convert from matrix to array
                            for (int i = 0; i < kernelSize; i++) {
                                for (int j = 0; j < kernelSize; j++) {
                                    m[i * mykernel[0].length + j] = mykernel[i][j];
                                }
                            }

                            k = new Kernel(kernelSize, kernelSize, m);
                        }

                    }
                    ConvolveOp cop = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
                    BufferedImage imgDest = cop.filter(this.tmpImage, null);
                    selectedWindow.getLienzo().setPanelImage(imgDest);
                    selectedWindow.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println("Error setting filter..: " + e.getLocalizedMessage());
                }
            }
        }

    }//GEN-LAST:event_jComboBoxFilterActionPerformed

    /**
     * Genera un kernel Gaussiano de tamaño size y de varianza sigma.
     *
     * @param sigma
     * @param size
     * @return kernel gaussiano de size x size
     */
    private float[][] generateKernel(float sigma, int size) {
        float[][] kernel = new float[size][size];
        for (int j = 0; j < size; ++j) {
            for (int i = 0; i < size; ++i) {
                kernel[j][i] = gaussianDiscrete2D(sigma, i - (size / 2), j - (size / 2));
            }
        }

        return kernel;
    }

    /**
     * Calcula cada uno de las celdas de un kernel gaussiano, dadas las
     * posiciones x e y
     *
     * @param sigma
     * @param x
     * @param y
     * @return
     */
    private float gaussianDiscrete2D(float sigma, int x, int y) {
        float g = 0;
        for (double ySubPixel = y - 0.5; ySubPixel < y + 0.55; ySubPixel += 0.1) {
            for (double xSubPixel = x - 0.5; xSubPixel < x + 0.55; xSubPixel += 0.1) {
                g = g + (float) ((1 / (2 * Math.PI * sigma * sigma))
                        * Math.pow(Math.E, -(xSubPixel * xSubPixel + ySubPixel * ySubPixel)
                                / (2 * sigma * sigma)));
            }
        }
        g = g / 121;

        return g;
    }

    /**
     * Apunta la variable de imagen auxiliar a la imagen del la ventana
     * seleccionada en caso de haber alguna
     *
     * @param evt
     */
    private void jComboBoxFilterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxFilterFocusGained
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            this.tmpImage = selectedWindow.getLienzo().getPanelImage(false);
        }

    }//GEN-LAST:event_jComboBoxFilterFocusGained

    /**
     * Apunta la variable de imagen auxiliar a la imagen del la ventana
     * seleccionada en caso de haber alguna
     *
     * @param evt
     */
    private void jComboBoxFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxFilterFocusLost
        this.tmpImage = null;
    }//GEN-LAST:event_jComboBoxFilterFocusLost

    /**
     * Modifica el contraste de la imagen, para imágenes con luminosidad
     * equilibrada, aplicando una funcion tipo S
     *
     * @param evt
     */
    private void jButtonConstrastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConstrastActionPerformed
        this.setContrast(LookupTableProducer.TYPE_SFUNCION);
    }//GEN-LAST:event_jButtonConstrastActionPerformed

    /**
     * Modifica el contraste de la imagen para imágenes oscuras, aplicando una
     * funcion tipo logaritmo (aunque sea para imágenes muy oscuras)
     *
     * @param evt
     */
    private void jButtonBrightConstrastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrightConstrastActionPerformed
        this.setContrast(LookupTableProducer.TYPE_ROOT);
    }//GEN-LAST:event_jButtonBrightConstrastActionPerformed

    /**
     *
     * Modifica el contraste de la imagen para imagenes sobreiluminadas,
     * aplicando una funcion tipo potencia
     *
     * @param evt
     */
    private void jButtonDarkContrastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDarkContrastActionPerformed
        this.setContrast(LookupTableProducer.TYPE_POWER);
    }//GEN-LAST:event_jButtonDarkContrastActionPerformed

    /**
     * Según el type pasado, aplica un contraste u otro a la imagen de la
     * ventana interna activa
     *
     * @param type
     */
    private void setContrast(int type) {
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(type);

                    LookupOp lop = new LookupOp(lt, null);

                    // Al no dejar a null el segundo parámetro, no es necesario convertir la imagen al tipo ARGB
                    // para evitar el bug comentado en clase de prácticas.
                    // Este echo implica que la imagen sea compatible con la fuente (en este caso la misma).
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                } catch (Exception e) {
                    System.err.println("Error setting contrast: " + e.getLocalizedMessage());
                }
            }

        }

    }

    /**
     * Aplica la operación seleccionada sobre la posible imagen de la ventana
     * interna seleccionada
     *
     * @param evt
     */
    private void jButtonSinusoidalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSinusoidalActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                try {
                    LookupTable lt = this.createSinusLookupTable(180.0 / 255.0);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                } catch (Exception e) {
                    System.err.println("Error in sinus operaction: " + e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_jButtonSinusoidalActionPerformed

    /**
     * Crea la lookup tabla para el operador Lookup basado en la función seno
     * dada la velocidad angular w (vista en prácticas).
     *
     * @param w velocidad angular
     * @return
     */
    private LookupTable createSinusLookupTable(double w) {
        double K = 255.0; // Cte de normalización
        short st[] = new short[256];

        for (int x = 0; x < 255; x++) {
            st[x] = (short) (Math.abs(Math.sin(Math.toRadians(w * x))) * K);
            //System.out.println("x: " + x + " : " + st[x]);
        }
        ShortLookupTable slt = new ShortLookupTable(0, st);
        return slt;
    }

    /**
     * Operación negativo: Invierte los colores de la posible imagen de la
     * ventana interna seleccionada
     *
     * @param evt
     */
    private void jButtonNegativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNegativeActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                try {
                    LookupTable tl = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_NEGATIVE);
                    LookupOp lop = new LookupOp(tl, null);
                    lop.filter(imgSource, imgSource);
                    selectedWindow.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println("Error applying negative oper: " + e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_jButtonNegativeActionPerformed

    /**
     * Aplica la operación seleccionada sobre la posible imagen de la ventana
     * interna seleccionada
     *
     * @param evt
     */
    private void jButtonSepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSepiaActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                SepiaOp sepOperator = new SepiaOp();
                sepOperator.filter(imgSource, imgSource);
                selectedWindow.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_jButtonSepiaActionPerformed

    /**
     * Aplica la operación de ecualización sobre la posible imagen de la ventana
     * interna seleccionada.
     *
     * @param evt
     */
    private void jButtonEqualizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEqualizationActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                if (!isYCC(imgSource)) {
                    ColorSpace cs = new YCbCrColorSpace();
                    ColorConvertOp cop = new ColorConvertOp(cs, null);
                    cop.filter(imgSource, imgSource);
                    EqualizationOp eqOper = new EqualizationOp(0);
                    eqOper.filter(imgSource, imgSource);
                } else {
                    EqualizationOp eqOper = new EqualizationOp(0);
                    eqOper.filter(imgSource, imgSource);
                }

                selectedWindow.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_jButtonEqualizationActionPerformed

    /**
     * Aplica la operación seleccionada sobre la posible imagen de la ventana
     * interna seleccionada
     *
     * @param evt
     */
    private void jButtonTintedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTintedActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                TintOp tintado = new TintOp((Color) jComboBoxStrokeColor.getSelectedItem(), 0.5f);
                tintado.filter(imgSource, imgSource);
                selectedWindow.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_jButtonTintedActionPerformed

    /**
     * Copia en la variable de imagen auxiliar la imagen del la ventana
     * seleccionada en caso de haber alguna
     *
     * @param evt
     */
    private void jSliderUmbralizationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderUmbralizationFocusGained
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            ColorModel cm = selectedWindow.getLienzo().getPanelImage(false).getColorModel();
            WritableRaster raster = selectedWindow.getLienzo().getPanelImage(false).copyData(null);
            boolean alfaPre = selectedWindow.getLienzo().getPanelImage(false).isAlphaPremultiplied();
            tmpImage = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_jSliderUmbralizationFocusGained

    /**
     * Apunta la variable de imagen auxiliar a null
     *
     * @param evt
     */
    private void jSliderUmbralizationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderUmbralizationFocusLost
        tmpImage = null;
        jSliderUmbralization.setValue(127);
    }//GEN-LAST:event_jSliderUmbralizationFocusLost

    /**
     * Aplica la operación seleccionada sobre la posible imagen de la ventana
     * interna seleccionada
     *
     * @param evt
     */
    private void jButtonSobelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSobelActionPerformed

        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                try {
                    //SobelOp sobelOp = new SobelOp();
                    MySobelOp sobelOp = new MySobelOp();
                    sobelOp.filter(imgSource, imgSource);
                    vi.repaint();
                } catch (Exception e) {
                    System.err.println("Error applying sobel operator");
                }
            }
        }
    }//GEN-LAST:event_jButtonSobelActionPerformed

    /**
     * Aplica la operación seleccionada sobre la posible imagen de la ventana
     * interna seleccionada
     *
     * @param evt
     */
    private void jButtonPixelizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPixelizeActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage src = selectedWindow.getLienzo().getPanelImage(false);
            if (src != null) {
                PixelizeOp pexOp = new PixelizeOp();
                // Imagen origen y destino iguales
                pexOp.filter(src, src);
                mainDesktop.repaint();
            }
        }
    }//GEN-LAST:event_jButtonPixelizeActionPerformed

    /**
     * Extrae las bandas de la posible imagen de la ventana interna
     * seleccionada, creando una nueva ventana interna para cada una de las
     * bandas extraidas
     *
     * @param evt
     */
    private void jButtonExtractBandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExtractBandsActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSrc = selectedWindow.getLienzo().getPanelImage(false);

            if (imgSrc != null) {
                try {
                    //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY 
                    ColorSpace cs = new GreyColorSpace();
                    ComponentColorModel cm = new ComponentColorModel(cs, false, false,
                            Transparency.OPAQUE,
                            DataBuffer.TYPE_BYTE);

                    int bandList[] = {0};
                    //Creamos el nuevo raster a partir del raster de la imagen original 
                    for (int i = 0; i < imgSrc.getRaster().getNumBands(); i++) {
                        bandList[0] = i;

                        WritableRaster bandRaster = (WritableRaster) imgSrc.getRaster().createWritableChild(0, 0,
                                imgSrc.getWidth(), imgSrc.getHeight(), 0, 0, bandList);

                        //Creamos una nueva imagen que contiene como raster el correspondiente a la banda 
                        BufferedImage imgBanda = new BufferedImage(cm, bandRaster, false, null);

                        VentanaInternaImage newVi = new VentanaInternaImage(this);
                        newVi.getLienzo().addLienzoEventListener(lienzoEventHandler);

                        mainDesktop.add(newVi);
                        newVi.setVisible(true);
                        newVi.moveToFront();

                        if (isYCC(imgSrc)) {
                            newVi.setTitle(vi.getTitle() + " [Band " + (i == 0 ? "Y" : "C") + "]");
                        } else if (isGrey(imgSrc)) {
                            newVi.setTitle(vi.getTitle() + " [Band " + (i == 0 ? "G1" : "G2") + "]");
                        } else {
                            newVi.setTitle(vi.getTitle() + " [Band " + (i == 0 ? "R" : (i == 1) ? "G" : "B") + "]");
                        }

                        newVi.getLienzo().setPanelImage(imgBanda);
                    }
                } catch (Exception ex) {
                    System.err.println("Error extracting bands.." + ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_jButtonExtractBandsActionPerformed

    /**
     * Devuelve true si la imagen está en el espacio de color YCC
     *
     * @param img
     * @return
     */
    private boolean isYCC(BufferedImage img) {
        return img.getRaster().getNumBands() > 2 && !img.getColorModel().getColorSpace().isCS_sRGB();
    }

    /**
     * Devuelve true si la imagen está en el espacio de color Grey
     *
     * @param img
     * @return
     */
    private boolean isGrey(BufferedImage img) {
        return img.getRaster().getNumBands() < 3 && !isYCC(img);
    }

    /**
     * Cambia el espacio de color de la posible imagen de la ventana interna
     * seleccioanda
     *
     * @param evt
     */
    private void jComboBoxColorSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxColorSpaceActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgOut = null, srcImg = selectedWindow.getLienzo().getPanelImage(false);

            if (srcImg != null) {

                boolean isRgb = srcImg.getColorModel().getColorSpace().isCS_sRGB();
                boolean isYCC = isYCC(srcImg);
                boolean isGrey = isGrey(srcImg);

                String title = vi.getTitle();
                boolean converted = false;

                if (jComboBoxColorSpace.getSelectedItem().equals("sRGB")) {
                    if (!isRgb) {
                        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                        ColorConvertOp cop = new ColorConvertOp(cs, null);
                        imgOut = cop.filter(srcImg, null);
                        title += " [RGB]";
                        converted = true;
                    }
                } else if (jComboBoxColorSpace.getSelectedItem().equals("YCC")) {
                    if (!isYCC) {
                        ColorSpace cs = new YCbCrColorSpace();
                        ColorConvertOp cop = new ColorConvertOp(cs, null);
                        imgOut = cop.filter(srcImg, null);
                        title += " [YCC]";
                        converted = true;
                    }
                } else { // grey
                    if (!isGrey) {
                        // Utilizamos la clase GreyColorSpace del paquete proporcionado sm.image.color
                        ColorSpace cs = new GreyColorSpace();
                        ColorConvertOp cop = new ColorConvertOp(cs, null);
                        imgOut = cop.filter(srcImg, null);
                        title += " [GREY]";
                        converted = true;
                    }
                }

                if (converted) {
                    VentanaInternaImage vNew = new VentanaInternaImage(this);
                    selectedWindow.getLienzo().addLienzoEventListener(lienzoEventHandler);

                    mainDesktop.add(vNew);
                    vNew.setVisible(true);
                    vNew.moveToFront();
                    vNew.setTitle(title);

                    vNew.getLienzo().setPanelImage(imgOut);
                }
            }
        }
    }//GEN-LAST:event_jComboBoxColorSpaceActionPerformed

    /**
     * Rota la imagen dado el valor del slider en un momento determinado.
     *
     * @param evt
     */
    private void jSliderRotateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderRotateStateChanged
        this.rotateImg(this.jSliderRotate.getValue());
    }//GEN-LAST:event_jSliderRotateStateChanged

    /**
     * Apunta la variable de imagen auxiliar a la imagen del la ventana
     * seleccionada en caso de haber alguna
     *
     * @param evt
     */
    private void jSliderRotateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderRotateFocusGained
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            this.tmpImage = selectedWindow.getLienzo().getPanelImage(true);
        }
    }//GEN-LAST:event_jSliderRotateFocusGained

    /**
     * Rota la imagen 90 grados
     *
     * @param evt
     */
    private void jButtonRotate90ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRotate90ActionPerformed
        this.rotateImg(90);
    }//GEN-LAST:event_jButtonRotate90ActionPerformed

    /**
     * Rota la imagen 180 grados
     *
     * @param evt
     */
    private void jButtonRotate180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRotate180ActionPerformed
        this.rotateImg(180);
    }//GEN-LAST:event_jButtonRotate180ActionPerformed

    /**
     * Rota la imagen 270 grados
     *
     * @param evt
     */
    private void jButtonRotate270ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRotate270ActionPerformed
        this.rotateImg(270);
    }//GEN-LAST:event_jButtonRotate270ActionPerformed

    /**
     * Rota la posible imagen de la ventana interna seleccionada los ángulos
     * especificados
     *
     * @param angle Ángulos a girar la imagen
     */
    private void rotateImg(int angle) {
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            if (this.tmpImage == null) {
                this.tmpImage = selectedWindow.getLienzo().getPanelImage(false);
            }
            if (this.tmpImage != null) {
                double radians = Math.toRadians(angle);
                try {
                    // Establecemos el centro de la imagen como el origen de coordenadas para girarla desde ahi
                    AffineTransform at = AffineTransform.getRotateInstance(radians, this.tmpImage.getWidth() / 2, this.tmpImage.getHeight() / 2);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgDest = atop.filter(this.tmpImage, null);
                    selectedWindow.getLienzo().setPanelImage(imgDest);
                    selectedWindow.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println("Error rotating img: " + e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * Escala aumentando la posible imagen de la ventana interna seleccionada.
     *
     * @param evt
     */
    private void jButtonZoomPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomPlusActionPerformed
        this.scaleImage(1.25);
    }//GEN-LAST:event_jButtonZoomPlusActionPerformed

    /**
     * Escala reduciendo la posible imagen de la ventana interna seleccionada.
     *
     * @param evt
     */
    private void jButtonZoomMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomMinusActionPerformed
        this.scaleImage(0.75);
    }//GEN-LAST:event_jButtonZoomMinusActionPerformed

    /**
     * Escala una imagen según el factor de escala pasado como parámetro.
     *
     * @param scaleFactor Factor de escalado de la imagen
     */
    private void scaleImage(double scaleFactor) {
        VentanaInterna vi = (VentanaInterna) this.mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);

            if (imgSource != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(imgSource, null);
                    selectedWindow.getLienzo().setPanelImage(imgdest);
                    selectedWindow.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println("Error scaling image: " + e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * Aplica la operación de mezcla BlendOp para sumar las imágenes de las dos
     * ultimas ventanas internas seleccionadas.
     *
     * @param evt
     */
    private void jButtonBinaryPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBinaryPlusActionPerformed
        VentanaInterna vi = (VentanaInterna) this.mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            VentanaInternaImage viNext = (VentanaInternaImage) this.mainDesktop.selectFrame(false);
            if (viNext != null) {
                BufferedImage imgRight = selectedWindow.getLienzo().getPanelImage(false);
                BufferedImage imgLeft = viNext.getLienzo().getPanelImage(false);
                if (imgRight != null && imgLeft != null) {
                    try {
                        BlendOp op = new BlendOp(imgLeft);
                        BufferedImage imgdest = op.filter(imgRight, null);
                        VentanaInternaImage viNew = new VentanaInternaImage(this);
                        viNew.getLienzo().addLienzoEventListener(lienzoEventHandler);
                        viNew.getLienzo().setPanelImage(imgdest);
                        viNew.setTitle("Blend added image");
                        this.mainDesktop.add(viNew);
                        viNew.setVisible(true);
                        viNew.moveToFront();
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error binary oper (minus): " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }//GEN-LAST:event_jButtonBinaryPlusActionPerformed

    /**
     * Aplica la operación de mezcla SubstractionOp para sumar las imágenes de
     * las dos ultimas ventanas internas seleccionadas.
     *
     * @param evt
     */
    private void jButtonBinaryMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBinaryMinusActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            VentanaInterna viNext = (VentanaInterna) this.mainDesktop.selectFrame(false);
            if (viNext != null && viNext instanceof VentanaInternaImage) {
                VentanaInternaImage selectedWindowNext = (VentanaInternaImage) mainDesktop.getSelectedFrame();
                BufferedImage imgRight = selectedWindow.getLienzo().getPanelImage(false);
                BufferedImage imgLeft = selectedWindowNext.getLienzo().getPanelImage(false);
                if (imgRight != null && imgLeft != null) {
                    try {
                        SubtractionOp op = new SubtractionOp(imgLeft);
                        BufferedImage imgdest = op.filter(imgRight, null);
                        VentanaInternaImage viNew = new VentanaInternaImage(this);
                        viNew.getLienzo().addLienzoEventListener(lienzoEventHandler);
                        viNew.getLienzo().setPanelImage(imgdest);
                        viNew.setTitle("Blend sustracted image");
                        this.mainDesktop.add(viNew);
                        viNew.setVisible(true);
                        viNew.moveToFront();
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error binary oper (minus): " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }//GEN-LAST:event_jButtonBinaryMinusActionPerformed

    /**
     * Mezcla las imágenes de las dos ultimas ventanas internas seleccionadas en
     * proporción según el valor alfa elegido.
     *
     * @param evt
     */
    private void jSliderBinariesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderBinariesStateChanged
        if (this.blendWindow != null) {
            float alfa = (float) this.jSliderBinaries.getValue() / 100.0f;

            if (blendWindow.getLeftImage() != null && blendWindow.getRigthImage() != null) {
                //System.out.println("Alfa: " + alfa);
                BlendOp op = new BlendOp(blendWindow.getLeftImage(), alfa);

                BufferedImage imgdest = op.filter(blendWindow.getRigthImage(), null);

                this.blendWindow.getLienzo().setPanelImage(imgdest);
                this.blendWindow.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_jSliderBinariesStateChanged

    /**
     * Crea la ventana interna específica para la mezcla de imágenes asignándole
     * las dos imágenes a mezclar y la muestra
     *
     * @param evt
     */
    private void jSliderBinariesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderBinariesFocusGained
        VentanaInterna vi = (VentanaInterna) this.mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            VentanaInterna viNext = (VentanaInterna) this.mainDesktop.selectFrame(false);
            if (viNext != null && viNext instanceof VentanaInternaImage) {
                VentanaInternaImage selectedWindoNext = (VentanaInternaImage) mainDesktop.getSelectedFrame();
                BufferedImage imgRight = selectedWindow.getLienzo().getPanelImage(false);
                BufferedImage imgLeft = selectedWindoNext.getLienzo().getPanelImage(false);

                blendWindow = new VentanaInternaBlend(this, imgRight, imgLeft);

                blendWindow.setTitle("Slider blended images");
                mainDesktop.add(this.blendWindow);
                this.blendWindow.setVisible(true);
                this.blendWindow.moveToFront();
            }
        }
    }//GEN-LAST:event_jSliderBinariesFocusGained

    /**
     * Se reestablece el valor del slider de mezcla de imágenes al perder este
     * el foco.
     *
     * @param evt
     */
    private void jSliderBinariesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderBinariesFocusLost
        //blendWindow = null;
        //blendWindow.nullImages();
        this.jSliderBinaries.setValue(50);
    }//GEN-LAST:event_jSliderBinariesFocusLost

    /**
     * Aplica la operación de umbralización creada según el valor del slider en
     * un momento determinado, sobre la posible imagen de la ventana interna
     * seleccionada
     *
     * @param evt
     */
    private void jSliderUmbralizationStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderUmbralizationStateChanged
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            if (tmpImage != null) {
                try {
                    //System.err.println("Umbral: " + jSliderUmbralization.getValue());
                    UmbralizationOp umbOp = new UmbralizationOp(jSliderUmbralization.getValue());
                    umbOp.filter(tmpImage, selectedWindow.getLienzo().getPanelImage(false));
                    selectedWindow.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_jSliderUmbralizationStateChanged

    private void jButtonWebcamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWebcamActionPerformed

        VentanaInternaCamara viC = VentanaInternaCamara.getInstance(this);

        if (viC != null) {
            viC.setVisible(true);
            try {
                viC.setSelected(true);
            } catch (PropertyVetoException ex) {
                System.err.println("Error al select camera window: " + ex.getMessage());
            }

            mainDesktop.add(viC);
            viC.moveToFront();
        }

    }//GEN-LAST:event_jButtonWebcamActionPerformed

    private void jButtonScreenshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScreenshotActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi instanceof VentanaInternaCamara) {
            VentanaInternaCamara viC = (VentanaInternaCamara) vi;
            BufferedImage img = viC.getImage();
            VentanaInternaImage viI = new VentanaInternaImage(this);
            viI.getLienzo().addLienzoEventListener(lienzoEventHandler);
            viI.getLienzo().setPanelImage(img);
            viI.setTitle("Camara Screenshot");
            this.mainDesktop.add(viI);
            viI.setVisible(true);
            viI.moveToFront();
        } else if (vi instanceof VentanaInternaVLCPlayer) {
            VentanaInternaVLCPlayer viP = (VentanaInternaVLCPlayer) vi;
            BufferedImage img = viP.getImage();
            VentanaInternaImage viI = new VentanaInternaImage(this);
            viI.getLienzo().addLienzoEventListener(lienzoEventHandler);
            viI.getLienzo().setPanelImage(img);
            viI.setTitle("Video Screenshot");
            this.mainDesktop.add(viI);
            viI.setVisible(true);
            viI.moveToFront();
        }
    }//GEN-LAST:event_jButtonScreenshotActionPerformed

    private void jListPlayListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListPlayListValueChanged
        if (evt.getValueIsAdjusting() == false) {
            int index = jListPlayList.getSelectedIndex();

            if (index != -1) {
                try {
                    VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
                    if (vi != null) {
                        vi.setSelected(false);
                    }
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(VentanaPrincipal.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                mainDesktop.setSelectedFrame(null);
                isPlaying = false;
                isPaused = false;

            }
        }
    }//GEN-LAST:event_jListPlayListValueChanged

    private void jButtonBinaryProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBinaryProductActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            VentanaInterna viNext = (VentanaInterna) this.mainDesktop.selectFrame(false);
            if (viNext != null && viNext instanceof VentanaInternaImage) {
                VentanaInternaImage selectedWindowNext = (VentanaInternaImage) mainDesktop.getSelectedFrame();
                BufferedImage imgRight = selectedWindow.getLienzo().getPanelImage(false);
                BufferedImage imgLeft = selectedWindowNext.getLienzo().getPanelImage(false);
                if (imgRight != null && imgLeft != null) {
                    try {
                        MultiplicationOp op = new MultiplicationOp(imgLeft);
                        BufferedImage imgdest = op.filter(imgRight, null);
                        VentanaInternaImage viNew = new VentanaInternaImage(this);
                        viNew.getLienzo().addLienzoEventListener(lienzoEventHandler);
                        viNew.getLienzo().setPanelImage(imgdest);
                        viNew.setTitle("Blend multiplied image");
                        this.mainDesktop.add(viNew);
                        viNew.setVisible(true);
                        viNew.moveToFront();
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error binary oper (mult): " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }//GEN-LAST:event_jButtonBinaryProductActionPerformed

    private void jSliderOwnOperStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderOwnOperStateChanged
        jLabelOwnOper.setText(String.valueOf(jSliderOwnOper.getValue()));
//        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();
//
//        if (vi != null && vi instanceof VentanaInternaImage) {
//            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
//            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
//            if (imgSource != null) {
//                try {
//                    LookupTable lt = this.createOwnLookupTable(jSliderOwnOper.getValue());
//                    LookupOp lop = new LookupOp(lt, null);
//                    lop.filter(imgSource, imgSource);
//                    vi.repaint();
//                } catch (Exception e) {
//                    System.err.println("Error in sinus operaction: " + e.getLocalizedMessage());
//                }
//            }
//        }
    }//GEN-LAST:event_jSliderOwnOperStateChanged

    /**
     * Crea la lookup tabla para el operador propio definido basado en la
     * función absoluta dado el punto de cambio de monotonía de la función.
     *
     * Se normalzian los valores de salida de la función entre 0 y 255.
     *
     * @param w velocidad angular
     * @return
     */
    private LookupTable createOwnLookupTable(int value) {
        value = value / 2;
        double scale = 0;
        if (value >= 64) {
            scale = value;
        } else {
            scale = 128 - value;
        }

        double K = 255.0 / scale;
        System.out.println(scale + " " + K);
        short st[] = new short[256];

        for (int x = 0; x < 255; x++) {
            st[x] = (short) (Math.abs(value - x / 2) * K);
            System.out.println("x: " + x + " : " + st[x]);
            //System.out.println("Prueba " + p);
        }
        ShortLookupTable slt = new ShortLookupTable(0, st);
        return slt;
    }

    private void jSliderOwnOperFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderOwnOperFocusGained
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            ColorModel cm = selectedWindow.getLienzo().getPanelImage(false).getColorModel();
            WritableRaster raster = selectedWindow.getLienzo().getPanelImage(false).copyData(null);
            boolean alfaPre = selectedWindow.getLienzo().getPanelImage(false).isAlphaPremultiplied();
            tmpImage = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_jSliderOwnOperFocusGained

    private void jSliderOwnOperFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSliderOwnOperFocusLost
        tmpImage = null;
    }//GEN-LAST:event_jSliderOwnOperFocusLost

    private void jButtonOwnOperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOwnOperActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage imgSource = selectedWindow.getLienzo().getPanelImage(false);
            if (imgSource != null) {
                try {
                    LookupTable lt = this.createOwnLookupTable(Integer.valueOf(jLabelOwnOper.getText()));
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                } catch (Exception e) {
                    System.err.println("Error in sinus operaction: " + e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_jButtonOwnOperActionPerformed

    private void jButtonDiffuseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiffuseActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage src = selectedWindow.getLienzo().getPanelImage(false);
            if (src != null) {
                DiffuseOp diffOp = new DiffuseOp();
                // Imagen origen y destino iguales
                diffOp.filter(src, src);
                mainDesktop.repaint();
            }
        }
    }//GEN-LAST:event_jButtonDiffuseActionPerformed

    private void jButtonDessertOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDessertOpActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            BufferedImage src = selectedWindow.getLienzo().getPanelImage(false);
            if (src != null) {
                DessertOp dessertOp = new DessertOp();
                // Imagen origen y destino iguales
                dessertOp.filter(src, src);
                mainDesktop.repaint();
            }
        }
    }//GEN-LAST:event_jButtonDessertOpActionPerformed

    private void jToggleButtonTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTextActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.TEXT);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.TEXT_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Text");


    }//GEN-LAST:event_jToggleButtonTextActionPerformed

    private void jToggleButtonRoundRectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonRoundRectActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.ROUND_RECTANGLE);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Cubic Curve");
        //updateToolbars(selectedWindow);
    }//GEN-LAST:event_jToggleButtonRoundRectActionPerformed

    private void jToggleButtonCubicCurveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonCubicCurveActionPerformed
        VentanaInterna vi = (VentanaInterna) mainDesktop.getSelectedFrame();

        if (vi != null && vi instanceof VentanaInternaImage) {
            VentanaInternaImage selectedWindow = (VentanaInternaImage) mainDesktop.getSelectedFrame();
            selectedWindow.getLienzo().setCurrentPaintTool(PainToolType.CUBIC_CURVE);
            selectedWindow.getLienzo().setCursor(new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            selectedWindow.getLienzo().deselectSelectedShape();
        }
        jTextFieldToolSelected.setText("Round Rectangle");
        //updateToolbars(selectedWindow);
    }//GEN-LAST:event_jToggleButtonCubicCurveActionPerformed


    /* END IMAGE TOOLBARS */
    /**
     * Asegura que la ventana interna pasada como argumento queda seleccionada
     * como activa para prevenir problemas al intentar seleccionar la ventana
     * interna seleccionada en un determinado momento
     *
     * @param internalFrame Ventana interna a establecer como seleccionada
     */
    public void setSelectedFrame(JInternalFrame internalFrame) {
        mainDesktop.setSelectedFrame(internalFrame);
    }

    /**
     * Actualiza todos los controles editables y seleccionables (color,
     * localización de la figura seleccionada, tipo de degradado, etc..) de la
     * ventana principal, según estén dichos controles en la ventana interna
     * pasada como argumento y actualizando o no la lista de figuras, según el
     * parámetro pasado changeWindow
     *
     * @param vi VentanaInterna pasada de la cual se obtendrán los valores a
     * actualizar
     * @param changeWindow boolean que marca el comportamiento. Actualizar lista
     * de figuras o no.
     */
    public void updateToolbars(VentanaInterna vi, boolean changeWindow) {
        if (vi != null && vi instanceof VentanaInternaImage) {
            tmpImage = null;

            VentanaInternaImage selectedWindow = (VentanaInternaImage) vi;

            switch (selectedWindow.getLienzo().getCurrentPaintTool()) {
                case POINT:
                    jTextFieldToolSelected.setText("Point");
                    jToggleButtonPoint.setSelected(true);
                    break;
                case RECTANGLE:
                    jTextFieldToolSelected.setText("Rectangle");
                    jToggleButtonRect.setSelected(true);
                    break;
                case LINE:
                    jTextFieldToolSelected.setText("Line");
                    jToggleButtonLine.setSelected(true);
                    break;
                case ROUND_RECTANGLE:
                    jTextFieldToolSelected.setText("Round Rectangle");
                    jToggleButtonRoundRect.setSelected(true);
                    break;
                case CUBIC_CURVE:
                    jTextFieldToolSelected.setText("Cubic Curve");
                    jToggleButtonCubicCurve.setSelected(true);
                    break;
                case ELLIPSE:
                    jTextFieldToolSelected.setText("Ellipse");
                    jToggleButtonElli.setSelected(true);
                    break;
                case TEXT:
                    jTextFieldToolSelected.setText("Text");
                    jToggleButtonText.setSelected(true);
                    break;
                default:
                    break;
            }

            jComboBoxStrokeColor.setSelectedItem((Color) selectedWindow.getLienzo().getCurrentColor());
            jComboBoxFillColor.setSelectedItem((Color) selectedWindow.getLienzo().getFillColor());

            jSpinnerThickness.setValue(selectedWindow.getLienzo().getGrossorValue());

            if (selectedWindow.getLienzo().getCurrentTypeStroke() == TypeStroke.CONTINUOUS) {
                jComboBoxTypeStroke.setSelectedItem("CONTINUOUS");
            } else if (selectedWindow.getLienzo().getCurrentTypeStroke() == TypeStroke.DOTTED) {
                jComboBoxTypeStroke.setSelectedItem("DOTTED");
            }

            jToggleButtonFill.setSelected(selectedWindow.getLienzo().getCurrentTypeFill() == TypeFill.SMOOTH_COLOR);
            jToggleButtonNotFill.setSelected(selectedWindow.getLienzo().getCurrentTypeFill() == TypeFill.UNFILLED);
            jToggleButtonDegradateV.setSelected(selectedWindow.getLienzo().getCurrentTypeFill() == TypeFill.VERTICAL_DEGRADATE);
            jToggleButtonDegradateH.setSelected(selectedWindow.getLienzo().getCurrentTypeFill() == TypeFill.HORIZONTAL_DEGRADATE);

            // jSliderTransparency.setValue((int) selectedWindow.getLienzo().getTransparencyLevel() * 100);
            jToggleButtonSmooth.setSelected(selectedWindow.getLienzo().isSmooth());

            // jSliderBrightness.setValue(selectedWindow.getLienzo().getCurrentBrigthnessLevel());
            // Update shapes list
            if (changeWindow) {
                shapesListModel.clear();
                for (MyShape shape : selectedWindow.getLienzo().getMyShapes()) {
                    shapesListModel.addElement(shape);
                }
            }

            MyShape selectedShape = selectedWindow.getLienzo().getSelectedShape();
            if (selectedShape != null) {
                jTextCoordX.setText(String.valueOf((int) selectedShape.getLocation().getX()));
                jTextCoordY.setText(String.valueOf((int) selectedShape.getLocation().getY()));
            }
        }
    }

    /**
     * Actualiza la posición del puntero dentro de la ventana interna
     * seleccionada en el momento
     *
     * @param x Coordenada x del mouse
     * @param y Coordenada y del mouse
     */
    public void updatePosition(int x, int y) {
        jTextFieldCoordinates.setText("X: " + x + ", Y: " + y + ")");
    }

    /**
     * Actualiza el color del pixel justo debajo del puntero del mouse
     *
     * @param colorMouse Color del píxel debajo del puntero del mouse
     */
    public void updateColorMouse(Color colorMouse) {
        jTextFieldColorMouse.setText("Color in mouse: ");
        jButton1.setBackground(colorMouse);
    }

    /**
     * Formatea milisegundos a la forma xx:xx
     *
     * @param miliSeconds milisegundos a convertir
     * @return String de milisegundos formateada
     */
    private String formatSeconds(long miliSeconds, String format) {
        long seconds = miliSeconds / 1000;

        long minutes = (seconds % 3600) / 600;
        seconds %= 600;

        return String.format(format, minutes, seconds);
    }

    // END Events handlers

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupAttributes;
    private javax.swing.ButtonGroup buttonGroupColors;
    private javax.swing.ButtonGroup buttonGroupFilling;
    private javax.swing.ButtonGroup buttonGroupForms;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonBinaryMinus;
    private javax.swing.JButton jButtonBinaryPlus;
    private javax.swing.JButton jButtonBinaryProduct;
    private javax.swing.JButton jButtonBrightConstrast;
    private javax.swing.JButton jButtonConstrast;
    private javax.swing.JButton jButtonDarkContrast;
    private javax.swing.JButton jButtonDessertOp;
    private javax.swing.JButton jButtonDiffuse;
    private javax.swing.JButton jButtonDuplicate;
    private javax.swing.JButton jButtonEqualization;
    private javax.swing.JButton jButtonExtractBands;
    private javax.swing.JButton jButtonNegative;
    private javax.swing.JButton jButtonNew;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonOwnOper;
    private javax.swing.JButton jButtonPixelize;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JButton jButtonRotate180;
    private javax.swing.JButton jButtonRotate270;
    private javax.swing.JButton jButtonRotate90;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonScreenshot;
    private javax.swing.JButton jButtonSepia;
    private javax.swing.JButton jButtonSinusoidal;
    private javax.swing.JButton jButtonSobel;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JButton jButtonTinted;
    private javax.swing.JButton jButtonWebcam;
    private javax.swing.JButton jButtonZoomMinus;
    private javax.swing.JButton jButtonZoomPlus;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemFormsBar;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemStatusBar;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemToolsBar;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuSound;
    private javax.swing.JComboBox<String> jComboBoxColorSpace;
    private javax.swing.JComboBox<String> jComboBoxFillColor;
    private javax.swing.JComboBox<String> jComboBoxFilter;
    private javax.swing.JComboBox<String> jComboBoxStrokeColor;
    private javax.swing.JComboBox<String> jComboBoxTypeStroke;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelElapsedTime;
    private javax.swing.JLabel jLabelOwnOper;
    private javax.swing.JLabel jLabelTotalTime;
    private javax.swing.JList<String> jListPlayList;
    private javax.swing.JList<String> jListShapesList;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBinaries;
    private javax.swing.JPanel jPanelBinaries1;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JPanel jPanelCentralDerecha;
    private javax.swing.JPanel jPanelColorOpers;
    private javax.swing.JPanel jPanelImageBrightness;
    private javax.swing.JPanel jPanelImageContrast;
    private javax.swing.JPanel jPanelImageFilter;
    private javax.swing.JPanel jPanelImageOpers;
    private javax.swing.JPanel jPanelImageRotations;
    private javax.swing.JPanel jPanelImageUmbralization;
    private javax.swing.JPanel jPanelInferior;
    private javax.swing.JPanel jPanelLocation;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelSuperior;
    private javax.swing.JPanel jPanelTools;
    private javax.swing.JPanel jPanelZoom;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPanePlaylist;
    private javax.swing.JScrollPane jScrollPaneShapeslist;
    private javax.swing.JScrollPane jScrollPaneTools;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JSlider jSliderBinaries;
    private javax.swing.JSlider jSliderBrightness;
    private javax.swing.JSlider jSliderOwnOper;
    private javax.swing.JSlider jSliderRotate;
    private javax.swing.JSlider jSliderTransparency;
    private javax.swing.JSlider jSliderUmbralization;
    private javax.swing.JSpinner jSpinnerThickness;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPaneLists;
    private javax.swing.JTextField jTextCoordX;
    private javax.swing.JTextField jTextCoordY;
    private javax.swing.JTextField jTextFieldColorMouse;
    private javax.swing.JTextField jTextFieldCoordinates;
    private javax.swing.JTextField jTextFieldToolSelected;
    private javax.swing.JToggleButton jToggleButtonCubicCurve;
    private javax.swing.JToggleButton jToggleButtonDegradateH;
    private javax.swing.JToggleButton jToggleButtonDegradateV;
    private javax.swing.JToggleButton jToggleButtonElli;
    private javax.swing.JToggleButton jToggleButtonFill;
    private javax.swing.JToggleButton jToggleButtonLine;
    private javax.swing.JToggleButton jToggleButtonNotFill;
    private javax.swing.JToggleButton jToggleButtonPoint;
    private javax.swing.JToggleButton jToggleButtonPolyline;
    private javax.swing.JToggleButton jToggleButtonRecord;
    private javax.swing.JToggleButton jToggleButtonRect;
    private javax.swing.JToggleButton jToggleButtonRoundRect;
    private javax.swing.JToggleButton jToggleButtonSmooth;
    private javax.swing.JToggleButton jToggleButtonText;
    private javax.swing.JToolBar jToolBarAudio;
    private javax.swing.JToolBar jToolBarForms;
    private javax.swing.JToolBar jToolBarVideo;
    private javax.swing.JDesktopPane mainDesktop;
    // End of variables declaration//GEN-END:variables
}
