package rbasamoyai.betsyross.flags;

import net.conczin.immersive_paintings.network.payload.s2c.PaintingRegisterErrorPayload;

public interface BetsyRossFlagScreen {

    void onReceivePaintingResponse(PaintingRegisterErrorPayload response);
    void refreshPage();

}
