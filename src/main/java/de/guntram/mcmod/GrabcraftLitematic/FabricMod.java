/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

/**
 *
 * @author gbl
 */
public class FabricMod implements ClientModInitializer, EndTick, PreLaunchEntrypoint {

    static final String MODID="grabcraft-litematic";
    static final String MODNAME="GrabcraftLitematic";
    static KeyBinding openDownloadScreen;
    
    @Override
    public void onPreLaunch() {
    }

    @Override
    public void onInitializeClient() {
        final String category="key.categories.grabcraft-litematic";

// Do NOT register the confHandler; we only want people to enable expert mode
// who know a bit about editing files and keeping the format.
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));

        openDownloadScreen = new KeyBinding("key.grabcraft-litematic.download", GLFW_KEY_Z, category);
//        CrowdinTranslate.downloadTranslations(MODID);
        KeyBindingHelper.registerKeyBinding(openDownloadScreen);
        extractBundledFile("blockmap.csv", "schematics");
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        if (openDownloadScreen.wasPressed()) {
            MinecraftClient.getInstance().openScreen(new DownloadGui());
        }
    }
    
    private void extractBundledFile(String fileName, String targetDir) {
        InputStream is;
        try {
            new File(targetDir).mkdirs();
            CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                is = jar.openStream();
                ZipInputStream zis = new ZipInputStream(is);
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equalsIgnoreCase(fileName)) {
                        extractZipEntry(zis, new File(targetDir, fileName));
                        break;
                    }
                }
                zis.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void extractZipEntry(ZipInputStream zipStream, File outputFile) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            byte[] buf=new byte[16384];
            int length;
            while ((length=zipStream.read(buf, 0, buf.length))>=0) {
                fos.write(buf, 0, length);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}
