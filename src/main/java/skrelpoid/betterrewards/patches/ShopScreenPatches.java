package skrelpoid.betterrewards.patches;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;
import skrelpoid.betterrewards.BetterRewardsMod;

public class ShopScreenPatches {

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "init")
	public static class InitShopItems {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(Object o1, Object o2, Object o3) {
			ShopScreen shopScreen = (ShopScreen) o1;
			BetterRewardsMod.initShopItems(shopScreen);
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(),
						new Matcher.MethodCallMatcher(ShopScreen.class, "initCards"));
			}
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "update")
	public static class UpdateShopItems {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(Object o1) {
			ShopScreen shopScreen = (ShopScreen) o1;
			BetterRewardsMod.updateShopItems(shopScreen);
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(),
						new Matcher.MethodCallMatcher(ShopScreen.class, "updateCards"));
			}
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class, method = "render")
	public static class RenderShopItems {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(Object o1, Object o2) {
			SpriteBatch sb = (SpriteBatch) o2;
			BetterRewardsMod.renderShopItems(sb);
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(),
						new Matcher.MethodCallMatcher(ShopScreen.class, "renderCardsAndPrices"));
			}
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
