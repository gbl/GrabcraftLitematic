/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 *
 * @author gbl
 */

class BlockInfo {
    String mat_id;
    float opacity;
    int x, y, z;
    String name;
    boolean transparent;
    String hex;
    String file;
    String texture;
    int[] rgb;
}

public class RenderObject {
    
    private Map<String, Map<String, Map<String, BlockInfo>>> data;
    private int minX, minY, minZ, maxX, maxY, maxZ;
    private static final java.lang.reflect.Type MAP_TYPE =
            new TypeToken<Map<String, Map<String, Map<String, BlockInfo>>>>(){}.getType();

    
    public RenderObject() {
        
    }
    
    public void read(File jsonFile) throws IOException {
        read(new FileReader(jsonFile));
    }
    
    public void read(Reader is) throws IOException {
        try (JsonReader reader = new JsonReader(is)) {
            Gson gson=new Gson();
            data=gson.fromJson(reader, MAP_TYPE);
        }
        
        if (data == null) {
            throw new IOException("Grabcraft didn't return any data");
        }
        
        minX = minY = minZ = Integer.MAX_VALUE;
        maxX = maxY = maxZ = Integer.MIN_VALUE;
        for (Map.Entry<String, Map<String, Map<String, BlockInfo>>> pairY: data.entrySet()) {
            for (Map.Entry<String, Map<String, BlockInfo>> pairX: pairY.getValue().entrySet()) {
                for (Map.Entry<String, BlockInfo> pairZ: pairX.getValue().entrySet()) {
                    BlockInfo block = pairZ.getValue();
                    if (block.x > maxX) { maxX = block.x; }
                    if (block.y > maxY) { maxY = block.y; }
                    if (block.z > maxZ) { maxZ = block.z; }
                    if (block.x < minX) { minX = block.x; }
                    if (block.y < minY) { minY = block.y; }
                    if (block.z < minZ) { minZ = block.z; }
                }
            }
        }
        checkRange("X", minX, maxX);
        checkRange("Y", minY, maxY);
        checkRange("Z", minZ, maxZ);
    }
    
    private void checkRange(String dimension, int min, int max) throws IOException {
        if (min == Integer.MIN_VALUE || max == Integer.MAX_VALUE) {
            throw new IOException("Bad file format, no "+dimension+" values");
        }
    }
    
    public int getSizeX() { return maxX - minX + 1 ; }
    public int getSizeY() { return maxY - minY + 1 ; }
    public int getSizeZ() { return maxZ - minZ + 1 ; }

    String getBlockNameAt(int x, int y, int z) {
        try {
            return data.get(Integer.toString(y+minY)).get(Integer.toString(x+minX)).get(Integer.toString(z+minZ)).name;
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
