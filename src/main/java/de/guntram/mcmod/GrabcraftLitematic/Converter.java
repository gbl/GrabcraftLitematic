/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

/**
 *
 * @author gbl
 */
public class Converter {
    
    public static void run(RenderObject object, BlockMap map, Litematic schema) {
        
        for (int y=0; y<object.getSizeY(); y++) {
            for (int x=0; x<object.getSizeX(); x++) {
                for (int z=0; z<object.getSizeZ(); z++) {
                    String block = object.getBlockNameAt(x, y, z);
                    
                    if (block == null || block.isEmpty()) {
                        // System.out.println("no block found at "+x+"/"+y+"/"+z);
                        schema.put(x, y, z, map.AIRBLOCK);
                    }
                    else {
                        schema.put(x, y, z, map.get(block));
                    }
                }
            }
        }
    }
}
