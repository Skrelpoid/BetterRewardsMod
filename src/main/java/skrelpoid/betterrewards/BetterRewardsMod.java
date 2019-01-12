package skrelpoid.betterrewards;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.screens.stats.RunData;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModToggleButton;
import basemod.interfaces.PostInitializeSubscriber;
import skrelpoid.betterrewards.events.BetterRewardsInfoEvent;
import skrelpoid.betterrewards.shop.AbstractShopItem;
import skrelpoid.betterrewards.shop.LootboxShopItem;
import skrelpoid.betterrewards.shop.RandomRareRelicItem;
import skrelpoid.betterrewards.shop.RerollShopItem;

@SpireInitializer
public class BetterRewardsMod implements PostInitializeSubscriber {

	public static final String[] UNWANTED_SPECIAL_RELICS = { "Circlet", "Red Circlet", "Spirit Poop" };
	public static final String[] SCREEN_BOSS_RELICS = { "Calling Bell", "Orrery", "Tiny House" };
	
	
	public static boolean shouldShowButton = false;
	public static boolean canGetRewards = false;
	public static boolean alreadyStartedRewards = false;
	public static boolean isGettingRewards = false;
	public static boolean alreadyGotRewards = false;
	public static boolean isFunMode = false;
	public static RunData lastRun;

	public static RunHistory runHistory;
	public static ArrayList<AbstractShopItem> shopItems;
	public static boolean isNeowDone;
	public static int playerGold;
	public static int button;

	public static final String MOD_NAME = "BetterRewards";
	public static final String DESCRIPTION = MOD_NAME;
	public static final String AUTHOR = "Skrelpoid";

	private static SpireConfig config;

	public static final Logger logger = LogManager.getLogger(BetterRewardsMod.class.getName());

	public static void initialize() {
		runHistory = new RunHistory();
		BaseMod.subscribe(new BetterRewardsMod());
	}

	public static void setIsGettingRewards(boolean b) {
		isGettingRewards = b;
		alreadyGotRewards = false;
		alreadyStartedRewards = false;
	}

	public static void startRewards(AbstractEvent e) {
		alreadyStartedRewards = true;
		// Probably only one of these is needed
		e.imageEventText.updateBodyText("");
		e.imageEventText.clearAllDialogs();
		e.imageEventText.clearRemainingOptions();
		GenericEventDialog.hide();
		ShopRoom room = new ShopRoom();
		AbstractDungeon.currMapNode.room = room;
		room.onPlayerEntry();
	}

	public static boolean shouldShowInfo() {
		return isGettingRewards && !alreadyStartedRewards && !alreadyGotRewards;
	}

	public static void showInfo() {
		// copied from NeowEvent.dismissBubble()
		for (com.megacrit.cardcrawl.vfx.AbstractGameEffect e : AbstractDungeon.effectList) {
			if ((e instanceof InfiniteSpeechBubble)) {
				((InfiniteSpeechBubble) e).dismiss();
			}
		}
		logger.info("Showing BetterRewardsInfoEvent");
		AbstractEvent info = new BetterRewardsInfoEvent();
		AbstractDungeon.getCurrRoom().event = info;
		AbstractDungeon.getCurrRoom().event.onEnterRoom();
	}

	public static void checkCanGetRewards(String playerName) {
		lastRun = runHistory.getLastRunByCharacter(playerName);
		canGetRewards = lastRun != null && lastRun.score > 0;
		if (canGetRewards) {
			logger.info(playerName + " had a score of " + lastRun.score + " last run, Therefore they can get rewards.");
		} else {
			logger.info(playerName + " can not get rewards.");
		}
	}

	public static void refreshRunHistory() {
		runHistory.refreshData();
	}

	public static void finishedRewards() {
		if (isGettingRewards && alreadyStartedRewards && !alreadyGotRewards) {
			logger.info("Finished Rewards");
			alreadyGotRewards = true;
			AbstractDungeon.player.gold = playerGold;
		}
	}

	public static void initShopItems(ShopScreen shopScreen) {
		float x = 200;
		float y = 850;
		if (isGettingRewards && !alreadyGotRewards) {
			shopItems = new ArrayList<>();
			shopItems.add(new RerollShopItem(shopScreen, x, y));
			y -= 150;
			shopItems.add(new LootboxShopItem(shopScreen, x, y));
			y -= 150;
			shopItems.add(new RandomRareRelicItem(shopScreen, x, y));
			y -= 150;
		}
	}

	public static void updateShopItems(ShopScreen shopScreen) {
		if (isGettingRewards && alreadyStartedRewards && !alreadyGotRewards) {
			try {
				Field rugY = ShopScreen.class.getDeclaredField("rugY");
				rugY.setAccessible(true);
				for (AbstractShopItem i : shopItems) {
					i.update(rugY.getFloat(shopScreen));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void renderShopItems(SpriteBatch sb) {
		if (isGettingRewards && alreadyStartedRewards && !alreadyGotRewards) {
			for (AbstractShopItem i : shopItems) {
				i.render(sb);
			}
		}
	}

	public static AbstractRelic returnRandomScreenlessBossRelic() {
		ArrayList<String> bossRelics = new ArrayList<>();
		bossRelics.addAll(AbstractDungeon.bossRelicPool);
		bossRelics.removeAll(Arrays.asList(SCREEN_BOSS_RELICS));
		if (bossRelics.isEmpty()) {
			return new Circlet();
		} else {
			String relicToRemove = bossRelics.remove(0);
			AbstractDungeon.bossRelicPool.remove(relicToRemove);
			return RelicLibrary.getRelic(relicToRemove);
		}
	}

	@SuppressWarnings("unchecked")
	public static void rerollShop(ShopScreen shopScreen) {
		try {

			Field colorlessCards = ShopScreen.class.getDeclaredField("colorlessCards");
			colorlessCards.setAccessible(true);
			colorlessCards.set(shopScreen, rollColorlessCards());
			Field coloredCards = ShopScreen.class.getDeclaredField("coloredCards");
			coloredCards.setAccessible(true);
			coloredCards.set(shopScreen, rollColoredCards());
			Method initCards = ShopScreen.class.getDeclaredMethod("initCards");
			initCards.setAccessible(true);
			initCards.invoke(shopScreen, new Object[] {});

			ArrayList<StoreRelic> relics = new ArrayList<>();
			Field shopRelics = ShopScreen.class.getDeclaredField("relics");
			shopRelics.setAccessible(true);
			relics.addAll((ArrayList<StoreRelic>) shopRelics.get(shopScreen));
			// Add rerolled Items back to relicPool
			for (StoreRelic sr : relics) {
				AbstractRelic relic = sr.relic;
				if (relic != null && !AbstractDungeon.player.hasRelic(relic.relicId)) {
					ArrayList<String> tmp = new ArrayList<>();
					switch (relic.tier) {
						case COMMON:
							tmp.add(relic.relicId.toString());
							tmp.addAll(AbstractDungeon.commonRelicPool);
							AbstractDungeon.commonRelicPool = tmp;
							break;
						case UNCOMMON:
							tmp.add(relic.relicId.toString());
							tmp.addAll(AbstractDungeon.uncommonRelicPool);
							AbstractDungeon.uncommonRelicPool = tmp;
							break;
						case RARE:
							tmp.add(relic.relicId.toString());
							tmp.addAll(AbstractDungeon.rareRelicPool);
							AbstractDungeon.rareRelicPool = tmp;
							break;
						case SHOP:
							tmp.add(relic.relicId.toString());
							tmp.addAll(AbstractDungeon.shopRelicPool);
							AbstractDungeon.shopRelicPool = tmp;
							break;
						default:
							logger.info("Unexpected Relic Tier: " + relic.tier);
							break;
					}
				}
			}
			Method initRelics = ShopScreen.class.getDeclaredMethod("initRelics");
			initRelics.setAccessible(true);
			initRelics.invoke(shopScreen);

			Method potions = ShopScreen.class.getDeclaredMethod("initPotions");
			potions.setAccessible(true);
			potions.invoke(shopScreen);

			shopScreen.purgeAvailable = true;

			for (AbstractShopItem i : shopItems) {
				i.setVisible(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// From Merchant
	private static ArrayList<AbstractCard> rollColoredCards() {
		ArrayList<AbstractCard> cards = new ArrayList<>();
		cards.add(AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true)
				.makeCopy());

		AbstractCard addCard = AbstractDungeon
				.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true).makeCopy();
		while (Objects.equals(addCard.cardID, cards.get(cards.size() - 1).cardID)) {
			addCard = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true)
					.makeCopy();
		}
		cards.add(addCard);

		cards.add(AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true)
				.makeCopy());
		addCard = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true)
				.makeCopy();
		while (Objects.equals(addCard.cardID, cards.get(cards.size() - 1).cardID)) {
			addCard = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true)
					.makeCopy();
		}
		cards.add(addCard);

		cards.add(AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.POWER, true)
				.makeCopy());
		return cards;
	}

	// From Merchant
	private static ArrayList<AbstractCard> rollColorlessCards() {
		ArrayList<AbstractCard> cards = new ArrayList<>();
		cards.add(AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.UNCOMMON).makeCopy());
		cards.add(AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.RARE).makeCopy());
		return cards;
	}

	public static void discountShopItems(float multiplier) {
		if (isGettingRewards && alreadyStartedRewards && !alreadyGotRewards) {
			for (AbstractShopItem i : shopItems) {
				i.applyDiscount(multiplier);
			}
		}
	}

	public static final float X = 400;
	public static final float Y = 700;

	@Override
	public void receivePostInitialize() {
		loadSettings();
		ModPanel panel = new ModPanel();
		ModLabeledToggleButton fun = new ModLabeledToggleButton("Enable FUN mode (No HP cost)", X, Y, Color.WHITE,
				FontHelper.buttonLabelFont, isFunMode, panel, (l) -> {}, BetterRewardsMod::funToggle);
		panel.addUIElement(fun);
		BaseMod.registerModBadge(new Texture("modBadge.png"), MOD_NAME, AUTHOR, DESCRIPTION, panel);

	}

	private static void funToggle(ModToggleButton t) {
		isFunMode = t.enabled;
		saveSettings();
	}

	private static void saveSettings() {
		config.setBool("isFunMode", isFunMode);
		try {
			config.save();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void loadSettings() {
		try {
			config = new SpireConfig(MOD_NAME, MOD_NAME + "Config");
			config.load();
		} catch (Exception ex) {
			logger.catching(ex);
		}
		isFunMode = config.getBool("isFunMode");
	}

	// TODO FIX rewardsscreen in shop sometimes forces player to leave for now
	// fixed by not giving relics that show reward screen
	// could be really fixed by patching proceed button
}
