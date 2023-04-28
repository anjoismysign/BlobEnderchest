package us.mytheria.blobenderchest.director;

import us.mytheria.blobenderchest.BlobEnderchest;
import us.mytheria.bloblib.managers.Manager;

public class ECManager extends Manager {
    public ECManager(ECManagerDirector managerDirector) {
        super(managerDirector);
    }

    @Override
    public BlobEnderchest getPlugin() {
        return (BlobEnderchest) super.getPlugin();
    }

    @Override
    public ECManagerDirector getManagerDirector() {
        return (ECManagerDirector) super.getManagerDirector();
    }
}
