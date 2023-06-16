package us.mytheria.blobenderchest.director;

import us.mytheria.blobenderchest.BlobEnderchest;
import us.mytheria.bloblib.entities.GenericManager;

public class ECManager extends GenericManager<BlobEnderchest, ECManagerDirector> {
    public ECManager(ECManagerDirector managerDirector) {
        super(managerDirector);
    }
}
