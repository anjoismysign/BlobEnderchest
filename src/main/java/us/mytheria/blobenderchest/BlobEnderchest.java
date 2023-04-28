package us.mytheria.blobenderchest;

import us.mytheria.blobenderchest.director.ECManagerDirector;
import us.mytheria.bloblib.managers.BlobPlugin;

public final class BlobEnderchest extends BlobPlugin {
    private ECManagerDirector director;

    @Override
    public void onEnable() {
        director = new ECManagerDirector(this);
    }

    @Override
    public void onDisable() {
        director.unload();
        unregisterFromBlobLib();
    }

    @Override
    public ECManagerDirector getManagerDirector() {
        return director;
    }
}
