package rbasamoyai.betsyross.platform;

import rbasamoyai.betsyross.platform.client.IClientIndexPlatform;

public class BetsyRossClientServices {

    public static final IClientIndexPlatform CLIENT_INDEX_PLATFORM = BetsyRossServices.load(IClientIndexPlatform.class);

    private BetsyRossClientServices() {}

}
