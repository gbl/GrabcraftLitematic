/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gbl
 */
class Downloader {
    
    private static final String urlStart = "https://www.grabcraft.com/minecraft/";
    private static final String urlRender = "https://www.grabcraft.com/js/RenderObject/";
    
    public static String download(String urlString) {
        if (!urlString.startsWith(urlStart)) {
            return "This seems to be wrong; needs to start with "+urlStart;
        }
        String renderObject;
        try {
            renderObject = findRenderObjectInHTML(urlString);
        } catch (IOException ex) {
            return "Problem with URL: "+ex.getMessage();
        }
        if (renderObject == null) {
            return "No schema data found";
        }
        
        String downloadResult;
        String[] urlParts;
        try {
            urlParts = urlString.split("/");
            downloadResult = downloadRenderObject(urlRender+renderObject, urlParts[4]);
        } catch (IOException ex) {
            return "Problem with schema data: "+ex.getMessage();
        } catch (IndexOutOfBoundsException ex) {
            return "No object name in URL";
        } catch (IllegalStateException ex) {
            return "bad file format: "+ex.getMessage();
        }
        return "Downloaded to "+urlParts[4];
    }

    private static String findRenderObjectInHTML(String urlString) throws IOException {
        Pattern pattern = Pattern.compile("myRenderObject_[0-9]+.js");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
            String line;
            while ((line=in.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
        }
        return null;
    }
    
    private static String downloadRenderObject(String urlObject, String objectName) throws IOException {
        RenderObject object = new RenderObject();
        InputStreamReader is = new InputStreamReader(new URL(urlObject).openStream());
        int c;
        // skip over "var blabla ="
        while ((c=is.read()) != -1 && c != '=')
            ;
        object.read(is);
        BlockMap map = new BlockMap(new File("schematics", "blockmap.csv"));
        Litematic schema = new Litematic(objectName, object.getSizeX(), object.getSizeY(), object.getSizeZ());
        Converter.run(object, map, schema);
        schema.save(new File("schematics", objectName+".litematic"));
        return "Download to "+objectName+" ok";
    }
}
