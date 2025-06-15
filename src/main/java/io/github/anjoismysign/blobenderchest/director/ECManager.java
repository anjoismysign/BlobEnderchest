package io.github.anjoismysign.blobenderchest.director;

import io.github.anjoismysign.blobenderchest.BlobEnderchest;
import io.github.anjoismysign.bloblib.entities.GenericManager;

public class ECManager extends GenericManager<BlobEnderchest, ECManagerDirector> {
    public ECManager(ECManagerDirector managerDirector) {
        super(managerDirector);
    }
}
