package rbasamoyai.betsyross.remix;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;

public class FrustumCache {

    private static Frustum frustum;

    public static void cacheFrustum(Frustum newFrustum) { frustum = newFrustum; }
    public static boolean isVisible(AABB aabb) { return frustum != null && frustum.isVisible(aabb); }

}
