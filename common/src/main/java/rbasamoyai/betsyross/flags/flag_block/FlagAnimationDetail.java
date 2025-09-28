package rbasamoyai.betsyross.flags.flag_block;

import javax.annotation.Nonnull;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import rbasamoyai.betsyross.config.BetsyRossConfig;

public enum FlagAnimationDetail implements EnumTranslatable {
	NO_WAVE,
	WAVE;

    @Nonnull
    @Override
    public String prefix() {
        return BetsyRossConfig.CONFIG_ID + "client.animationDetail";
    }

}
