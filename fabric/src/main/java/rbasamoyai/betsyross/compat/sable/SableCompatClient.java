package rbasamoyai.betsyross.compat.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.phys.AABB;

public class SableCompatClient {

    public static AABB transformRenderBB(AABB globalRenderBox) {
        SubLevel subLevel = Sable.HELPER.getContainingClient(globalRenderBox.getCenter());
        if (subLevel != null) {
            BoundingBox3d bb = new BoundingBox3d(globalRenderBox);
            globalRenderBox = bb.transform(subLevel.logicalPose(), bb).toMojang();
        }
        return globalRenderBox;
    }

}
