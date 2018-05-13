package skrelpoid.betterrewards.shop;

import com.megacrit.cardcrawl.shop.ShopScreen;

import skrelpoid.betterrewards.BetterRewardsMod;

public class RerollShopItem extends AbstractShopItem {

	public RerollShopItem(ShopScreen screen, float x, float y) {
		super(screen, "shop/reroll.png", "BetterRewards Shop Item", "Reroll and restock the Shop.", 150, x, y);
	}

	@Override
	protected void onPurchase() {
		BetterRewardsMod.rerollShop(shopScreen);
		isVisible = true;
	}

}
