/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 *
 * @author gbl
 */
class Downloader {
    
    public static final String urlStart = "https://www.grabcraft.com/minecraft/";
    private static final String urlRender = "https://www.grabcraft.com/js/RenderObject/";
    
    private static String author;
    
    public static String download(String urlString, boolean mapOnly) {
        author = "GrabcraftLitematica";
        if (!urlString.startsWith(urlStart)) {
            return "This seems to be wrong; needs to start with\n"+urlStart;
        }
        String renderObject;
        try {
            renderObject = findRenderObjectInHTML(urlString);
        } catch (IOException ex) {
            return "Problem with URL:\n"+ex.getMessage();
        }
        if (renderObject == null) {
            return "No schema data found";
        }
        
        String downloadResult;
        String[] urlParts;
        try {
            urlParts = urlString.split("/");
            downloadResult = downloadRenderObject(urlString, urlRender+renderObject, urlParts[4], mapOnly);
        } catch (IOException ex) {
            return "Problem with schema data:\n"+ex.getMessage();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace(System.err);
            return "No object name in URL";
        } catch (IllegalStateException ex) {
            return "bad file format:\n"+ex.getMessage();
        }
        return downloadResult;
    }

    private static String findRenderObjectInHTML(String urlString) throws IOException {
        Pattern renderObjectPattern = Pattern.compile("myRenderObject_[0-9]+.js");
        String authorString = "Author:&nbsp;";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
            String line;
            while ((line=in.readLine()) != null) {
                int pos;
                if ((pos=line.indexOf(authorString)) > 0) {
                    try {
                        author=line.substring(pos+authorString.length());
                        author=author.substring(0, author.indexOf("<"));
                    } catch (IndexOutOfBoundsException ex) {
                        author = "";
                    }
                }
                Matcher matcher = renderObjectPattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
        }
        return null;
    }
    
    private static String downloadRenderObject(String origURL, String urlObject, String objectName, boolean mapOnly) throws IOException {
        RenderObject object = new RenderObject();
        InputStreamReader is = new InputStreamReader(new URL(urlObject).openStream());
        int c;
        // skip over "var blabla ="
        while ((c=is.read()) != -1 && c != '=')
            ;
        object.read(is);
        File mapFile = new File("schematics", objectName+".csv");
        File mapFileWritten = mapFile;
        if (mapOnly || !mapFile.exists()) {
            mapFile = new File("schematics", "blockmap.csv");
        }
        BlockMap map = new BlockMap(mapFile);
        if (mapOnly) {
            try (PrintWriter writer  = new PrintWriter(new FileWriter(mapFileWritten))) {
                SortedSet<String> usedBlockNames = new TreeSet<String>();
                for (int x=0; x<object.getSizeX(); x++) {
                    for (int y=0; y<object.getSizeY(); y++) {
                        for (int z=0; z<object.getSizeZ(); z++) {
                            usedBlockNames.add(object.getBlockNameAt(x, y, z));
                        }
                    }
                }
                Map<String, BlockWithStates> blockStates = map.getUsedBlocks();
                for (String key: usedBlockNames) {
                    writer.append(key);
                    BlockWithStates bws = blockStates.get(key);
                    if (bws == null) {
                        writer.append("\tminecraft:barrier");
                    } else if (!Registry.BLOCK.containsId(new Identifier(bws.blockName))) {
                        writer.append("\tminecraft:bedrock");
                        System.err.println("blockmap contains unknown block "+bws.blockName);
                    } else {
                        writer.append('\t').append(bws.blockName);
                        for (Map.Entry<String, String> state: bws.states.entrySet()) {
                            writer.append('\t').append(state.getKey()).append('\t').append(state.getValue());
                        }
                    }
                    writer.append('\n');
                }
                if (writer.checkError()) {
                    throw new IOException("Can't write to "+mapFileWritten.getName());
                }
                writer.close();
                return "Downloaded the map to "+mapFileWritten.getName();
            }
        } else {
            Litematic schema = new Litematic(objectName, object.getSizeX(), object.getSizeY(), object.getSizeZ());
            Converter.run(object, map, schema);
            schema.setAuthor(author);
            schema.setDescription(origURL);
            schema.save(new File("schematics", objectName+".litematic"));
            return "Download to "+objectName+" ok";
        }
    }
}
