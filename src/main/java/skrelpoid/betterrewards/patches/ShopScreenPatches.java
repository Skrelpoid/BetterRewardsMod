package skrelpoid.betterrewards.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.shop.ShopScreen;

import skrelpoid.betterrewards.BetterRewardsMod;

public class ShopScreenPatches {

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "init")
	public static class InitShopItems {
		@SpireInsertPatch(rloc = 52)
		public static void Insert(Object o1, Object o2, Object o3) {
			ShopScreen shopScreen = (ShopScreen) o1;
			BetterRewardsMod.initShopItems(shopScreen);
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "update")
	public static class UpdateShopItems {
		@SpireInsertPatch(rloc = 12)
		public static void Insert(Object o1) {
			ShopScreen shopScreen = (ShopScreen) o1;
			BetterRewardsMod.updateShopItems(shopScreen);
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "render")
	public static class RenderShopItems {
		@SpireInsertPatch(rloc = 5)
		public static void Insert(Object o1, Object o2) {
			SpriteBatch sb = (SpriteBatch) o2;
			BetterRewardsMod.renderShopItems(sb);
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "applyDiscount")
	public static class DiscountShopItems {
		@SpirePrefixPatch
		public static void Prefix(Object o, float f) {
			BetterRewardsMod.discountShopItems(f);
		}
	}

}
