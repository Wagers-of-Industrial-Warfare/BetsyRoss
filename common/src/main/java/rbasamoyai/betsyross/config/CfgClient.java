package rbasamoyai.betsyross.config;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.flag_block.FlagAnimationDetail;

@Translation(prefix = BetsyRossConfig.CONFIG_ID + "client")
public class CfgClient extends Config {

    public CfgClient() {
        super(BetsyRoss.path("client_config"));
    }

	public FlagAnimationDetail animationDetail = FlagAnimationDetail.WAVE;
    public ValidatedInt viewRangeInChunks = new ValidatedInt(24, 512, 0);

}
