/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

/**
 *
 * @author gbl
 */
public class FabricMod implements ClientModInitializer, EndTick {

    static final String MODID="grabcraft-litematic";
    static final String MODNAME="GrabcraftLitematic";
    static KeyBinding openDownloadScreen;

    @Override
    public void onInitializeClient() {
        final String category="key.categories.grabcraft-litematic";
        openDownloadScreen = new KeyBinding("key.grabcraft-litematic.download", GLFW_KEY_Z, category);
//        CrowdinTranslate.downloadTranslations(MODID);
        KeyBindingHelper.registerKeyBinding(openDownloadScreen);
        ClientTickEvents.END_CLIENT_TICK.register(this);        
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        if (openDownloadScreen.wasPressed()) {
            MinecraftClient.getInstance().openScreen(new DownloadGui());
        }
    }
    
}
