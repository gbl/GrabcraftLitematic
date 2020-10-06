/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtIo;

/**
 *
 * @author gbl
 */
public class Litematic {
    
    private CompoundTag data;
    private CompoundTag metaData;
    private CompoundTag regions;
    private CompoundTag region;
    
    private String name;
    
    private List<BlockWithStates> palette;
    private int[][][] states;
    private int x, y, z;
    
    public Litematic(String name, int x, int y, int z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;

        data =  new CompoundTag();
        data.putInt("MinecraftDataVersion", 2578);
        data.putInt("Version", 5);
        
        metaData =  new CompoundTag();
        data.put("Metadata", metaData);
        metaData.putLong("TimeCreated", System.currentTimeMillis());
        metaData.putLong("TimeModified", System.currentTimeMillis());
        metaData.putString("Description", "GrabcraftDownload");
        metaData.putInt("RegionCount", 1);
        metaData.putInt("TotalBlocks", x*y*z);
        metaData.putString("Author", "GrabcraftLitematic");
        metaData.putInt("TotalVolume", x*y*z);
        metaData.putString("Name", name);
        CompoundTag size = new CompoundTag();
        size.putInt("x", x);
        size.putInt("y", y);
        size.putInt("z", z);
        metaData.put("EnclosingSize", size);
        
        regions = new CompoundTag();
        data.put("Regions", regions);
        
        region = new CompoundTag();
        // Do not put this into regions yet as there might be a name change later
        CompoundTag position = new CompoundTag();
        position.putInt("x", 0);
        position.putInt("y", 0);
        position.putInt("z", 0);
        region.put("Position", position);

        region.put("Size", size);
        
        states = new int[x][][];
        for (int dx=0; dx<x; dx++) {
            states[dx]=new int[y][];
            for (int dy=0; dy<y; dy++) {
                states[dx][dy]=new int[z];
            }
        }
        palette = new ArrayList<>();
        palette.add(BlockMap.AIRBLOCK);
    }
    
    public void setDescription(String s) { metaData.putString("Description", s); }
    public void setAuthor(String s) { metaData.putString("Author", s); }
    public void setName(String s) {
        metaData.putString("Name", s);
        name = s;
    }

    public void put(int x, int y, int z, BlockWithStates blockname) {
        int index;
        if ((index=palette.indexOf(blockname)) == -1) {
            index=palette.size();
            palette.add(blockname);
        }
        states[x][y][z] = index;
    }
    
    private void generateStates() {
        int neededBits = 1;
        int possibleEntries = 2;
        while (possibleEntries < palette.size()) {
            neededBits++;
            possibleEntries*=2;
        }
        int nonAirBlocks = 0;
        
        int neededLongs = ((x*y*z * neededBits + 63 ) / 64 );
        long[] longData = new long[neededLongs];
        int shift = 0;
        int longIndex = 0;
        
        for (int dy=0; dy<y; dy++) {
            for (int dz=0; dz<z; dz++) {
                for (int dx=0; dx<x; dx++) {
                    long state = states[dx][dy][dz];
                    longData[longIndex] |= (state << shift);
//                    System.out.println("state "+state+" writing to index "+longIndex+" shift "+shift+" is now "+Long.toBinaryString(longData[longIndex]));
                    shift += neededBits;
                    if (shift >= 64) {
                        shift -= 64;
                        longData[++longIndex] |= (state >>> (neededBits-shift));
//                        System.out.println("=> next is now "+Long.toBinaryString(longData[longIndex]));
//                        System.out.println("distributed a "+Long.toBinaryString(state)+", shift is "+shift+"\n"+
//                                ", previous byte is "+Long.toBinaryString(longData[longIndex-1])+"\n"+
//                                ", current byte is "+Long.toBinaryString(longData[longIndex]));
                    }
                    if (state != 0) {
                        nonAirBlocks++;
                    }
                }
            }
        }
        LongArrayTag tag = new LongArrayTag(longData);
        region.put("BlockStates", tag);
        metaData.putInt("TotalBlocks", nonAirBlocks);
    }
    
    private void generatePalette() {
        ListTag blockStatePalette = new ListTag();
        for (BlockWithStates entry: palette) {
            CompoundTag blockEntry = new CompoundTag();
            blockEntry.putString("Name", entry.blockName);
            blockStatePalette.add(blockEntry);
            // TODO add Properties
        }
        region.put("BlockStatePalette", blockStatePalette);
    }

    public void save(File file) throws IOException {
        generateStates();
        generatePalette();
        regions.put(name, region);
        NbtIo.method_30614(data, file);
    }
}
