package rbasamoyai.betsyross.flags;

import immersive_paintings.network.s2c.RegisterPaintingResponse;

public interface BetsyRossFlagScreen {

    void onReceivePaintingResponse(RegisterPaintingResponse response);
    void refreshPage();

}
