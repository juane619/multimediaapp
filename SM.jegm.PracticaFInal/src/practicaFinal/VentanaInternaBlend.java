/*
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */
 


package practicaFinal;

import java.awt.image.BufferedImage;

/**
 * Tipo de ventana interna que se usa para mezclar imágenes mediante deslizador.
 * 
 * @author juane
 */


public class VentanaInternaBlend extends VentanaInternaImage{
    private BufferedImage imageLeft, imageRight;
    
    public VentanaInternaBlend(VentanaPrincipal vp, BufferedImage left, BufferedImage right) {
        super(vp);
        
        imageLeft = left;
        imageRight = right;
    }
    
    public BufferedImage getLeftImage(){
        return imageLeft;
    }
    
    public BufferedImage getRigthImage(){
        return imageRight;
    }
    
    public void nullImages(){
        imageLeft = imageRight = null;
    }
    
}
