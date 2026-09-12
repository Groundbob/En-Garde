package net.engarde.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.engarde.EnGarde;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EnGardeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(EnGarde.MOD_ID + "/game_config.json").toFile();
    private static EnGardeConfig config;

    public boolean parryToggleable = true;
    public boolean disableDurability = true;

    public boolean defaultTridentLoyalty = true;
    public boolean tridentDamageBuff = true;

    public boolean enableBowRework = true;

    public boolean maceRework = true;
    
    public static synchronized EnGardeConfig loadConfig() {
        if (config != null) return config;
        return reloadConfig();
    }
    
    public static synchronized void saveConfig() {
        if (config == null) {
            config = new EnGardeConfig();
        }
        
        File parent = CONFIG_FILE.getParentFile();
        if (parent != null && !parent.exists()) {
            CONFIG_FILE.getParentFile().mkdirs();
        }
        
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            EnGarde.LOGGER.error("Could not save config file", e);
        }
    }

    public static synchronized EnGardeConfig reloadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, EnGardeConfig.class);
            } catch (IOException | JsonSyntaxException e) {
                EnGarde.LOGGER.error("Could not load config file", e);
            }
        } else {
            config = new EnGardeConfig();
            saveConfig();
        }
        if (config == null) {
            config = new EnGardeConfig();
            saveConfig();
        }
        return config;
    }
}
