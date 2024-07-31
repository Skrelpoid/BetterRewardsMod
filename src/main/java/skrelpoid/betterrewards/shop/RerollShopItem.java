package skrelpoid.betterrewards.shop;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.shop.ShopScreen;

import skrelpoid.betterrewards.BetterRewardsMod;

public class RerollShopItem extends AbstractShopItem {
	
	public static final String ID = "betterrewardsmod:RerollShopItem";
	
	private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
	private static final String NAME = eventStrings.NAME;
	private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

	public RerollShopItem(ShopScreen screen, float x, float y) {
		super(screen, "shop/reroll.png", NAME, DESCRIPTIONS[0], 125, x, y);
	}

	@Override
	protected void onPurchase() {
		BetterRewardsMod.rerollShop(shopScreen);
		isVisible = true;
	}

}
