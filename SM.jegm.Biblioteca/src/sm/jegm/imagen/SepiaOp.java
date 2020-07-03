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
 * Clase que define la operación sepia sobre las imágenes.
 * Pixel a pixel (en cada uno de las bandas de estos)  se aplica la función correspondiente al
 * filtro sepia.
 *
 * @see BufferedImageOpAdapter
 * @author juane
 */
public class SepiaOp extends BufferedImageOpAdapter {

    /**
     * Sobrecarga filter de BufferedImageOpAdapter para el filtro sepia
     *
     * @param src imagen origen
     * @param dest imagen destino
     * @return imagen destino.
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                Color colorCurrentPixel = new Color(src.getRGB(x, y));
                int colorR = colorCurrentPixel.getRed();
                int colorG = colorCurrentPixel.getGreen();
                int colorB = colorCurrentPixel.getBlue();

                int sepiaR = Integer.min(255, (int) (0.393 * colorR + 0.769 * colorG + 0.189 * colorB));
                int sepiaG = Integer.min(255, (int) (0.349 * colorR + 0.686 * colorG + 0.168 * colorB));
                int sepiaB = Integer.min(255, (int) (0.272 * colorR + 0.534 * colorG + 0.131 * colorB));

                Color newColor = new Color(sepiaR, sepiaG, sepiaB);

                dest.setRGB(x, y, newColor.getRGB());
            }
        }

        return dest;
    }
}
