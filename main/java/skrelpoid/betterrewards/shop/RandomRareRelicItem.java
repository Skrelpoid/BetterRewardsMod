package skrelpoid.betterrewards.shop;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;

public class RandomRareRelicItem extends AbstractShopItem {

	public RandomRareRelicItem(ShopScreen screen, float x, float y) {
		super(screen, "shop/rareRelic.png", "BetterRewards Shop Item",
				"Obtain a random Rare relic. (Sadly some relics that open screens are excluded)", 300, x, y);
	}

	@Override
	protected void onPurchase() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE));
	}
}
