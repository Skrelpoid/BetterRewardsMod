package skrelpoid.betterrewards.shop;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.shop.ShopScreen;

import skrelpoid.betterrewards.BetterRewardsMod;

@Deprecated
public class RandomBossRelicItem extends AbstractShopItem {
	
	public static final String ID = "betterrewardsmod:RandomBossRelicItem";
	
	private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
	private static final String NAME = eventStrings.NAME;
	private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

	public RandomBossRelicItem(ShopScreen screen, float x, float y) {
		super(screen, "shop/bossRelic.png", NAME,
				DESCRIPTIONS[0], 800, x, y);
	}

	@Override
	protected void onPurchase() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				BetterRewardsMod.returnRandomScreenlessBossRelic());
	}

}
