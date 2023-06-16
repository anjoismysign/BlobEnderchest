package us.mytheria.blobenderchest;

import us.mytheria.blobenderchest.director.ECManagerDirector;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.IManagerDirector;

public final class BlobEnderchest extends BlobPlugin {
    private ECManagerDirector director;
    private IManagerDirector proxy;

    @Override
    public void onEnable() {
        director = new ECManagerDirector(this);
        proxy = director.proxy();
    }

    @Override
    public void onDisable() {
        director.unload();
        unregisterFromBlobLib();
    }

    @Override
    public IManagerDirector getManagerDirector() {
        return proxy;
    }
}
