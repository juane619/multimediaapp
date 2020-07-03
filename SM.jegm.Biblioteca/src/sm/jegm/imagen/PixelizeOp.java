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
 * Pixelation operator. Pixelize an image, setting size of pixel generated.
 * Operation of own design not seen in class (adaptated from stackoverflow.com)
 *
 * Explicación mas detallada: operación de pixelización de una imagen,
 * especificando el tamaño de cada uno de los píxeles que claramente se verán al
 * aplicar la operación (desde uno de los botones en la sección de opraciones de
 * la interfaz de usuario, una vez haya una imagen cargada)
 *
 * Su funcionalidad, en términos generales, es la siguiente: obteniendo
 * información de píxeles de alrededor (y haciendo la media) (de ahí el
 * parámetro a especificar) genera nuevos píxeles, con mucha menos información
 * que la imagen original, viéndose claramente el efecto de pixelación.
 *
 *
 * @author juane
 */
public class PixelizeOp extends BufferedImageOpAdapter {
    int pixelSize;

    /**
     * Default Constructor. Set size of pixelation.
     */
    public PixelizeOp() {
        pixelSize = 8;
    }

    /**
     * Constructor.
     *
     * @param pixelSize size of generated pixel.
     */
    public PixelizeOp(int pixelSize) {
        this.pixelSize = pixelSize;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }

        for (int x = 0; x < src.getWidth(); x += pixelSize) {
            for (int y = 0; y < src.getHeight(); y += pixelSize) {
                int pixelAlpha = 0;
                int pixelR = 0;
                int pixelG = 0;
                int pixelB = 0;
                int numSums = 0;

                for (int x2 = x; x2 < x + pixelSize && x2 < src.getWidth(); x2++) {
                    for (int y2 = y; y2 < y + pixelSize && y2 < src.getHeight(); y2++) {
                        int currentRGB = src.getRGB(x2, y2);

                        pixelAlpha += (currentRGB >> 24) & 0xff;
                        pixelR += (currentRGB >> 16) & 0xff;
                        pixelG += (currentRGB >> 8) & 0xff;
                        pixelB += (currentRGB) & 0xff;
                        numSums++;
                    }
                }

                pixelAlpha = pixelAlpha / numSums;
                pixelR = pixelR / numSums;
                pixelG = pixelG / numSums;
                pixelB = pixelB / numSums;

                int averageColor = pixelAlpha << 24;
                averageColor = averageColor | (pixelR << 16);
                averageColor = averageColor | (pixelG << 8);
                averageColor = averageColor | (pixelB);

                for (int x2 = x; x2 < x + pixelSize && x2 < src.getWidth(); x2++) {
                    for (int y2 = y; y2 < y + pixelSize && y2 < src.getHeight(); y2++) {
                        src.setRGB(x2, y2, averageColor);
                    }
                }
            }
        }

        return dest;
    }

}
