package rbasamoyai.betsyross.config;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.Config;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.flag_block.FlagAnimationDetail;

@Translation(prefix = BetsyRossConfig.CONFIG_ID + "client")
public class CfgClient extends Config {

    public CfgClient() {
        super(BetsyRoss.path("client_config"));
    }

	public FlagAnimationDetail animationDetail = FlagAnimationDetail.WAVE;

}
