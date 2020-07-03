/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import sm.image.BufferedImageOpAdapter;

/**
 * Operador de umbralizacion propio, recorre la imagen y se genera otra nueva de la siguiente forma:
 * para cada pixel, aplicamos la operación (r(x,y) + g(x,y) + b(x,y))/3, asignando 0 o 255 en la imagen dstino en función de si 
 * dicha operación supera o no el valor umbral (slider).
 * 
 * @author JuanE
 * 
 * @see BufferedImageOpAdapter
 */
public class UmbralizationOp extends BufferedImageOpAdapter{
    private int umbralValue;
    
    /**
     * Constructor operador umbralizacion
     * @param umbralValue el valor del umbral
     */
    public UmbralizationOp(int umbralValue)
    {
        this.umbralValue = umbralValue;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
            
                Color currentPixelColor = new Color(src.getRGB(x, y));
                int averagePixelColor = (currentPixelColor.getRed() + currentPixelColor.getGreen() + currentPixelColor.getBlue())/3;
                
                if (averagePixelColor >= umbralValue)
                    dst.setRGB(x, y, Color.WHITE.getRGB());
                else
                    dst.setRGB(x, y, Color.BLACK.getRGB());
            }
        }
        
        return dst;
    }
    
}
