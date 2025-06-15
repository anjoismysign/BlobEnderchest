package io.github.anjoismysign.blobenderchest;

import org.jetbrains.annotations.NotNull;
import io.github.anjoismysign.blobenderchest.director.ECManagerDirector;
import io.github.anjoismysign.bloblib.entities.PluginUpdater;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.IManagerDirector;

public final class BlobEnderchest extends BlobPlugin {
    private BlobEnderchestAPI api;
    private IManagerDirector proxy;
    private PluginUpdater updater;

    @Override
    public void onEnable() {
        ECManagerDirector director = new ECManagerDirector(this);
        proxy = director.proxy();
        updater = generateGitHubUpdater("anjoismysign", "BlobEnderchest");
        api = BlobEnderchestAPI.getInstance(director);
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

    public BlobEnderchestAPI getApi() {
        return api;
    }
}
