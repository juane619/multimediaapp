/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.jegm.myevents;

import java.util.EventListener;

/**
 *  La interfaz LienzoEventListenr define la situación a controlar
 * una vez se lanza y se captura el evento LienzoEvent.
 * 
 * Dicha interfaz deberá ser implementada por la clase manejadora de dicho evento.
 * 
 * @author juane
 */
public interface LienzoEventListener extends EventListener{
    public abstract void onShapeAdded(LienzoEvent ev);
}
