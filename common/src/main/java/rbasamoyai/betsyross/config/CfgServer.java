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
    public int flagBlockMaxHeight;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int armorBannerMaxWidth;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int armorBannerMaxHeight;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int flagStandardMaxWidth;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int flagStandardMaxHeight;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int bannerStandardMaxWidth;

    @ValidatedInt.Restrict(min = 0, max = Byte.MAX_VALUE)
	public int bannerStandardMaxHeight;

}
