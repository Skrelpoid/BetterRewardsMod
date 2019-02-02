package skrelpoid.betterrewards.patches;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import basemod.ReflectionHacks;
import javassist.CtBehavior;
import skrelpoid.betterrewards.BetterRewardsMod;
import skrelpoid.betterrewards.shop.AbstractShopItem;

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

	@SpirePatch(clz = com.megacrit.cardcrawl.shop.ShopScreen.class,
			method = "updateControllerInput")
	public static class UpdateControllerInput {
		public static boolean nothingLeft = false;
		public static ArrayList<AbstractShopItem> items = new ArrayList<>(3);

		@SpirePrefixPatch
		public static SpireReturn<?> Prefix(ShopScreen screen) {
			if (!Settings.isControllerMode || AbstractDungeon.topPanel.selectPotionMode
					|| !AbstractDungeon.topPanel.potionUi.isHidden) {
				return SpireReturn.Return(null);
			}
			if (!BetterRewardsMod.isCurrentlyInShop()) {
				return SpireReturn.Continue();
			}
			items.clear();
			for (AbstractShopItem item : BetterRewardsMod.shopItems) {
				if (item.isVisible()) {
					items.add(item);
				}
			}
			if (items.isEmpty()) {
				return SpireReturn.Continue();
			}
			
			Hitbox hb = getAvailableHitbox(screen);
			if (hb.hovered && !nothingLeft) {
				if (CInputActionSet.left.isJustPressed()
						|| CInputActionSet.altLeft.isJustPressed()) {
					Gdx.input.setCursorPosition(
							(int) items.get(0).getHb().cX,
							(int) (Settings.HEIGHT
									- items.get(0).getHb().cY));
					return SpireReturn.Return(null);
				}
			} else {
				int index = 0;
				boolean foundHovered = false;
				for (AbstractShopItem item : items) {
					if (item.getHb().hovered) {
						foundHovered = true;
						break;
					}
					index++;
				}
				if (foundHovered) {
					if (CInputActionSet.right.isJustPressed()
							|| CInputActionSet.altRight.isJustPressed()) {
						Gdx.input.setCursorPosition((int) hb.cX,
								(int) (Settings.HEIGHT - hb.cY));
					} else if (CInputActionSet.up.isJustPressed()
							|| CInputActionSet.altUp.isJustPressed()) {
						if (--index < 0) {
							index = 0;
						}
						Gdx.input.setCursorPosition(
								(int) items.get(index).getHb().cX,
								(int) (Settings.HEIGHT
										- items.get(index).getHb().cY));
					} else if (CInputActionSet.down.isJustPressed()
							|| CInputActionSet.altDown.isJustPressed()) {
						if (++index > items.size() - 1) {
							index = items.size() - 1;
						}
						Gdx.input.setCursorPosition(
								(int) items.get(index).getHb().cX,
								(int) (Settings.HEIGHT
										- items.get(index).getHb().cY));
					}
					return SpireReturn.Return(null);
				}
			}
			return SpireReturn.Continue();
		}

		@SuppressWarnings("unchecked")
		private static Hitbox getAvailableHitbox(ShopScreen screen) {
			nothingLeft = false;
			ArrayList<AbstractCard> coloredCards = (ArrayList<AbstractCard>) ReflectionHacks
					.getPrivate(screen, ShopScreen.class, "coloredCards");
			if (!coloredCards.isEmpty()) {
				return coloredCards.get(0).hb;
			}
			ArrayList<AbstractCard> colorlessCards = (ArrayList<AbstractCard>) ReflectionHacks
					.getPrivate(screen, ShopScreen.class, "colorlessCards");
			if (!colorlessCards.isEmpty()) {
				return colorlessCards.get(0).hb;
			}
			ArrayList<StoreRelic> storeRelics = (ArrayList<StoreRelic>) ReflectionHacks
					.getPrivate(screen, ShopScreen.class, "relics");
			if (!storeRelics.isEmpty()) {
				return storeRelics.get(0).relic.hb;
			}
			ArrayList<StorePotion> storePotions = (ArrayList<StorePotion>) ReflectionHacks
					.getPrivate(screen, ShopScreen.class, "potions");
			if (!storePotions.isEmpty()) {
				return storePotions.get(0).potion.hb;
			}
			Hitbox purge = createPurgeHitbox(screen);
			if (purge != null) {
				return purge;
			}
			boolean someItemHovered = false;
			for (AbstractShopItem item : items) {
				if (item.getHb().hovered) {
					someItemHovered = true;
					break;
				}
			}
			if (!someItemHovered) {
				Gdx.input.setCursorPosition(
						(int) items.get(0).getHb().cX,
						(int) (Settings.HEIGHT
								- items.get(0).getHb().cY));
			}
			nothingLeft = true;
			return items.get(0).getHb();

		}

		private static Hitbox createPurgeHitbox(ShopScreen screen) {
			float x = (float) ReflectionHacks.getPrivate(screen, ShopScreen.class, "purgeCardX");
			float y = (float) ReflectionHacks.getPrivate(screen, ShopScreen.class, "purgeCardY");
			float CARD_W = 110.0f * Settings.scale;
			float CARD_H = 150.0f * Settings.scale;
			boolean purgeHovered =
					(boolean) ReflectionHacks.getPrivate(screen, ShopScreen.class, "purgeHovered");
			boolean purgeAvailable =
					(boolean) ReflectionHacks.getPrivate(screen, ShopScreen.class,
							"purgeAvailable");
			Hitbox hb = new Hitbox(x, y, CARD_W, CARD_H);
			hb.hovered = purgeHovered;
			return purgeAvailable ? hb : null;
		}

	}

}
