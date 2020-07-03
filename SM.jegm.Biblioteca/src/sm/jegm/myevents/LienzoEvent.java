/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.myevents;

import java.util.EventObject;

/**
 * La clase LienzoEvent representa el evento que lanza un lienzo en un momento determinado.
 * Implementa la interfaz EventObject para dicho cometido.
 * 
 * @author juane
 */
public class LienzoEvent extends EventObject{
    
    public LienzoEvent(Object source) {
        super(source);
    }
    
}
