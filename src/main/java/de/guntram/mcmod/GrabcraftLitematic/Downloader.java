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
    
    public static final String urlStart = "https://www.grabcraft.com/minecraft/";
    private static final String urlRender = "https://www.grabcraft.com/js/RenderObject/";
    
    private static String author;
    
    public static String download(String urlString) {
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
            downloadResult = downloadRenderObject(urlString, urlRender+renderObject, urlParts[4]);
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
    
    private static String downloadRenderObject(String origURL, String urlObject, String objectName) throws IOException {
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
        schema.setAuthor(author);
        schema.setDescription(origURL);
        schema.save(new File("schematics", objectName+".litematic"));
        return "Download to "+objectName+" ok";
    }
}
