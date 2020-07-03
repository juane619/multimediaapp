/*
/* Copyright (C). All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by JuanE <juane619@correo.ugr.es>
 */
package sm.jegm.imagen;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 * Operación que aplica un filtro de calidez a la imagen, simulando 
 * el estar sobre un desierto. (empujando los valores de los componentes hacia el naranja)
 * @author juane
 */
public class DessertOp extends BufferedImageOpAdapter {

    public DessertOp() {
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null.");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }

        WritableRaster srcRaster = src.getRaster();
        WritableRaster destRaster = dest.getRaster();
        
        int auxband = 0;

        for (int x = 0; x < srcRaster.getWidth(); x++) {
            for (int y = 0; y < srcRaster.getHeight(); y++) {
                for (int band = 0; band < srcRaster.getNumBands(); band++) {
                    if(srcRaster.getNumBands() == 4 && band > 0){
                        auxband = band-1; 
                    }else{
                        auxband = band;
                    }
                    int sample = srcRaster.getSample(x, y, auxband);

                    switch (auxband) {
                        case 0:
                            if(sample < 85){
                                sample *= 1.35;
                            }else if(sample < 170){
                                sample *= 1.20;
                            }   break;
                        case 1:
                            if(sample < 85){
                                sample *= 1.15;
                            }else if(sample > 170){
                                sample *= 0.85;
                            }   break;
                        default:
                            if(sample > 170){
                                sample *=0.65;
                            }else if(sample > 85){
                                sample *=0.75;
                            }   break;
                    }

                    destRaster.setSample(x, y, auxband, sample);
                }
            }

        }
        return dest;
    }
}
