/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gbl
 */
public class BlockWithStates {
    String blockName;
    Map<String, String> states;
    
    BlockWithStates(String name) {
        this.blockName = name;
        this.states = Collections.EMPTY_MAP;
    }
    
    BlockWithStates(String name, String[] states) {
        this.blockName = name;
        this.states = new HashMap<>();
        for (int i=0; i<states.length-1; i+=2) {
            // System.out.printf("put %s as %s\n", states[i], states[i+1]);
            this.states.put(states[i], states[i+1]);
        }
    }
}
