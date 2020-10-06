/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author gbl
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        RenderObject object = new RenderObject();
        object.read(new File(args[0]));
        
        System.out.println("File has dimensions "+object.getSizeX()+"/"+object.getSizeY()+"/"+object.getSizeZ());
        BlockMap map = new BlockMap(new File("blockmap.csv"));
        
        Litematic schema = new Litematic("Test", object.getSizeX(), object.getSizeY(), object.getSizeZ());
        
        Converter.run(object, map, schema);
        
        schema.save(new File("output.litematic"));
    }
}
