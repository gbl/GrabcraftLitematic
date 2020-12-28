package de.guntram.mcmod.GrabcraftLitematic;

import de.guntram.mcmod.fabrictools.ConfigChangedEvent;
import de.guntram.mcmod.fabrictools.Configuration;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import java.io.File;

public class ConfigurationHandler implements ModConfigurationHandler {

    private static ConfigurationHandler instance;

    private Configuration config;
    private String configFileName;

    private boolean expertMode;
    
    public static ConfigurationHandler getInstance() {
        if (instance==null)
            instance=new ConfigurationHandler();
        return instance;
    }
    
    private ConfigurationHandler() {
        expertMode = false;
    }

    public void load(final File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            configFileName=configFile.getPath();
            loadConfig();
        }
    }
    
    @Override
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(FabricMod.MODID)) {
            loadConfig();
        }
    }
    
    private void loadConfig() {
        
        expertMode=config.getBoolean("grabcraft-litematic.config.expertmode", Configuration.CATEGORY_CLIENT, false, "grabcraft-litematic.config.tt.expertmode");
        if (config.hasChanged())
            config.save();
    }
    
    @Override
    public Configuration getConfig() {
        return config;
    }

    public static String getConfigFileName() {
        return getInstance().configFileName;
    }
    
    public static boolean isExpertMode() {
        return getInstance().expertMode;
    }
}