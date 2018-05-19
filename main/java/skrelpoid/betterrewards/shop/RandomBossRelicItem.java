package skrelpoid.betterrewards.shop;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;

import skrelpoid.betterrewards.BetterRewardsMod;

@Deprecated
public class RandomBossRelicItem extends AbstractShopItem {

	public RandomBossRelicItem(ShopScreen screen, float x, float y) {
		super(screen, "shop/bossRelic.png", "BetterRewards Shop Item",
				"Obtain a random Boss relic. (Sadly some relics that open screens are excluded)", 800, x, y);
	}

	@Override
	protected void onPurchase() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				BetterRewardsMod.returnRandomScreenlessBossRelic());
	}

}
