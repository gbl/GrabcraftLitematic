/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gbl
 */

class BlockWithStates {
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

public class BlockMap {
    
    private Map<String, BlockWithStates> map;
    public static final BlockWithStates BEDROCK = new BlockWithStates("minecraft:bedrock");
    public static final BlockWithStates AIRBLOCK = new BlockWithStates("minecraft:air");
    Set<String> warned;
    
    BlockMap(File file) throws IOException {
        // TODO this doesn't close the reader
        this(new BufferedReader(new FileReader(file)));
    }
    
    BlockMap(BufferedReader reader) throws IOException {
        String line;
        map=new HashMap<>();
        
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("\t")) {
                continue;
            }
            String[] fields = line.split("\t");
            if (fields.length < 2) {
                continue;
            } else if (fields.length == 2) {
                map.put(fields[0], new BlockWithStates(fields[1]));
            } else {
                map.put(fields[0], new BlockWithStates(fields[1], Arrays.copyOfRange(fields, 2, fields.length)));
            }
        }
        warned = new HashSet<>();
    }
    
    BlockWithStates get(String block) {
        BlockWithStates result = map.get(block);
        if (result != null) {
            return result;
        } else {
            if (!warned.contains(block)) {
                System.out.println("Unknown block "+block+" replaced by bedrock");
                warned.add(block);
            }
            return BEDROCK;
        }
    }
}
