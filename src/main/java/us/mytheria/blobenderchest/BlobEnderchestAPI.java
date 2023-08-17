package us.mytheria.blobenderchest;

import us.mytheria.blobenderchest.director.ECManagerDirector;

public class BlobEnderchestAPI {
    private static BlobEnderchestAPI instance;
    private final ECManagerDirector director;

    public static BlobEnderchestAPI getInstance(ECManagerDirector director) {
        if (instance == null) {
            if (director == null)
                throw new NullPointerException("injected dependency is null");
            BlobEnderchestAPI.instance = new BlobEnderchestAPI(director);
        }
        return instance;
    }

    public static BlobEnderchestAPI getInstance() {
        return getInstance(null);
    }

    private BlobEnderchestAPI(ECManagerDirector director) {
        this.director = director;
    }
}
