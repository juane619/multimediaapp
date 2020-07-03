/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.imagen;

import java.awt.image.BufferedImage;
import sm.image.BufferedImageOpAdapter;

/**
 * Pixelation operator. Pixelize an src, setting size of pixel generated.
 * Operation of own design not seen in class (adaptated from stackoverflow.com)
 *
 * Explicación mas detallada: operación de pixelización de una srcn,
 * especificando el tamaño de cada uno de los píxeles que claramente se verán al
 * aplicar la operación (desde uno de los botones en la sección de opraciones de
 * la interfaz de usuario, una vez haya una srcn cargada)
 *
 * Su funcionalidad, en términos generales, es la siguiente: obteniendo
 * información de píxeles de alrededor (y haciendo la media) (de ahí el
 * parámetro a especificar) genera nuevos píxeles, con mucha menos información
 * que la srcn original, viéndose claramente el efecto de pixelación.
 *
 *
 * @author juane
 */
public class MySobelOp extends BufferedImageOpAdapter {

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src src is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }

        int[][] edgeColors = new int[src.getWidth()][src.getHeight()];
        int maxGradient = -1;

        for (int i = 1; i < src.getWidth()-1; i++) {
            for (int j = 1; j < src.getHeight()-1; j++) {
                // Calculamos los vectores gradiente en x y en y, sumando los vectores gradiente de cada banda.
                
                // Llamamos a getGrayScale para trabajar con la correspondiente luminancia en escala de grises, si no
                //tendríamos que trabajar con las tres bandas de color
                int val00 = getGrayScale(src.getRGB(i - 1, j - 1));
                int val01 = getGrayScale(src.getRGB(i - 1, j));
                int val02 = getGrayScale(src.getRGB(i - 1, j + 1));

                int val10 = getGrayScale(src.getRGB(i, j - 1));
                int val12 = getGrayScale(src.getRGB(i, j + 1));

                int val20 = getGrayScale(src.getRGB(i + 1, j - 1));
                int val21 = getGrayScale(src.getRGB(i + 1, j));
                int val22 = getGrayScale(src.getRGB(i + 1, j + 1));

                int gx = -val00 + val02
                        -2 * val10 + 2 * val12
                        -val20 + val22;

                int gy = (-val00) + (-2 * val01) + (-val02)
                        + (val20) + (2 * val21) + (val22);

                // Calculamos la magnitud del gradiente 
                int gval = (int) Math.hypot(gx, gy);

                // Calculamos el máximo valor de magnitud obtenido para una posterior normalizacion
                if (maxGradient < gval) {
                    maxGradient = gval;
                }

                edgeColors[i][j] = gval;
            }
        }

        // Podriamos haber simplificado truncando el valor a 255 si se superara dicho valor en algún pixel
        // pero se opta por normalizar correctamente calculando la escala por la cual multiplicar cada valor de pixel
        double scale = 255.0 / maxGradient;

        for (int i = 1; i < src.getWidth() - 1; i++) {
            for (int j = 1; j < src.getHeight() - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int) (edgeColor * scale);
                // Se tienen en cuenta imágenes con canal alfa
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                src.setRGB(i, j, edgeColor);
            }
        }

        return dest;
    }

    /**
     * Devuelve la corresponidiente luminancia del color rgb pasado en escala de grises
     * @param rgb Color del cual obtener la luminancia
     * @return Luminancia del color en escala de grises
     */
    private int getGrayScale(int rgb) {
        // Operaciones a nivel de bit para obtener las tres bandas dado un entero
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        int gray = (r + g + b) / 3;

        return gray;
    }


}
