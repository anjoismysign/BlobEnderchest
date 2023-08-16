package us.mytheria.blobenderchest;

import org.jetbrains.annotations.NotNull;
import us.mytheria.blobenderchest.director.ECManagerDirector;
import us.mytheria.bloblib.entities.PluginUpdater;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.IManagerDirector;

public final class BlobEnderchest extends BlobPlugin {
    private ECManagerDirector director;
    private IManagerDirector proxy;
    private PluginUpdater updater;

    @Override
    public void onEnable() {
        director = new ECManagerDirector(this);
        proxy = director.proxy();
        updater = generateGitHubUpdater("anjoismysign", "BlobEnderchest");
    }

    @Override
    public IManagerDirector getManagerDirector() {
        return proxy;
    }

    @Override
    @NotNull
    public PluginUpdater getPluginUpdater() {
        return updater;
    }
}
