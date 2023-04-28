package us.mytheria.blobenderchest.director;

import org.bukkit.configuration.file.FileConfiguration;
import us.mytheria.blobenderchest.BlobEnderchest;
import us.mytheria.bloblib.utilities.TextColor;

public class ConfigManager extends ECManager {
    private static String enderchestTitle;
    private static int enderchestRows;

    public ConfigManager(ECManagerDirector managerDirector) {
        super(managerDirector);
        reload();
    }

    @Override
    public void reload() {
        BlobEnderchest main = getManagerDirector().getPlugin();
        main.reloadConfig();
        main.saveDefaultConfig();
        main.getConfig().options().copyDefaults(true);
        main.saveConfig();
        FileConfiguration configuration = main.getConfig();
        if (!configuration.isString("Enderchest.Title"))
            throw new RuntimeException("'Enderchest.Title' is not valid/found in config.yml");
        enderchestTitle = TextColor.PARSE(configuration.getString("Enderchest.Title"));
        if (!configuration.isInt("Enderchest.Rows"))
            throw new RuntimeException("'Enderchest.Rows' is not valid/found in config.yml");
        enderchestRows = configuration.getInt("Enderchest.Rows");
    }

    public static String getEnderchestTitle() {
        return enderchestTitle;
    }

    public static int getEnderchestRows() {
        return enderchestRows;
    }
}
