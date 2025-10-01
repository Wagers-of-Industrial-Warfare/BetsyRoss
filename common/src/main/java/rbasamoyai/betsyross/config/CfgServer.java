package rbasamoyai.betsyross.config;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import rbasamoyai.betsyross.BetsyRoss;

@Translation(prefix = BetsyRossConfig.CONFIG_ID + "server")
public class CfgServer extends Config {

    public CfgServer() {
        super(BetsyRoss.path("server_config"));
    }

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
    public int flagBlockMaxWidth = 0;

	@ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
    public int flagBlockMaxHeight = 0;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int armorBannerMaxWidth = 1;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int armorBannerMaxHeight = 2;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int flagStandardMaxWidth = 4;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int flagStandardMaxHeight = 2;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int bannerStandardMaxWidth = 2;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int bannerStandardMaxHeight = 3;

}
