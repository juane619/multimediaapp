/*
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */

package sm.jegm.imagen;

import java.awt.image.BufferedImage;
import sm.image.BinaryOp;

/**
 * Clase que define la operación binaria sobre imágenes "multipicación".
 * @author juane
 */


public class MultiplicationOp extends BinaryOp{

    public MultiplicationOp(BufferedImage img) {
        super(img);
    }

    /**
     * Sobrecarga de binaryOp, para conseguir la multiplicación de dos imagenes (el color de estas).
     * Se trunca el valor por arriba (255) y por abajo (0).
     * @param v1 valor del pixel1
     * @param v2 valor del pixel2
     * @return resultado de multiplicar los valores de los píxeles
     */
    @Override
    public int binaryOp(int v1, int v2) {
        int result = v1*v2;
        //result = result>255?255:result<255?0:result;
        result = Math.max(0, Math.min(255, result));
        return result;
    }
    
}
