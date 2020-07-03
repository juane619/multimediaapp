/*
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */
package sm.jegm.imagen;

import java.awt.image.BufferedImage;
import java.util.Random;
import sm.image.BufferedImageOpAdapter;

/**
 * La clase DiffuseFilter representa una operación píxel a píxel para difuminar
 * una imagen.
 *
 * @author juane
 */
public class DiffuseOp extends BufferedImageOpAdapter {

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int[] imgArray = new int[width * height];
        int[] shufflePixels = new int[4];

        // Aunque gastando mas memoria, ahorramos velocidad al trabajar con array en vez de con la imagen
        src.getRGB(0, 0, width, height, imgArray, 0, width);

        for (int y = 0, yaux = 0; y < height - 1; y++, yaux += width) {
            for (int x = 0, xaux = yaux; x < width - 1; x++, xaux++) {
                // Se eligen 4 píxeles (los de alrededor del actual) 
                shufflePixels[0] = imgArray[xaux];
                shufflePixels[1] = imgArray[xaux + 1];
                shufflePixels[2] = imgArray[xaux + width];
                shufflePixels[3] = imgArray[xaux + width + 1];

                // Se mezclan
                shuffle(shufflePixels);
                //Collections.shuffle(Arrays.asList(shufflePixels));

                // En la misma posicion que los pixeles escogidos
                // insertamos los píxeles mezclados
                imgArray[xaux] = shufflePixels[0];
                imgArray[xaux + 1] = shufflePixels[1];
                imgArray[xaux + width] = shufflePixels[2];
                imgArray[xaux + width + 1] = shufflePixels[3];
            }
        }
        dest.setRGB(0, 0, width, height, imgArray, 0, width);
        return dest;
    }

    private void shuffle(int[] array) {
        Random randomGenerator = new Random();
        int arrayLen = array.length;
        for (int i = 0; i < arrayLen; i++) {
            int rndPos = randomGenerator.nextInt(arrayLen);
            int aux = array[i];
            array[i] = array[rndPos];
            array[rndPos] = aux;
        }
    }
}
