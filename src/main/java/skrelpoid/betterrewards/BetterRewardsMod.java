package skrelpoid.betterrewards;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.stats.RunData;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModToggleButton;
import basemod.ReflectionHacks;
import basemod.devcommands.history.History;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import skrelpoid.betterrewards.events.BetterRewardsInfoEvent;
import skrelpoid.betterrewards.shop.AbstractShopItem;
import skrelpoid.betterrewards.shop.LootboxShopItem;
import skrelpoid.betterrewards.shop.RandomRareRelicItem;
import skrelpoid.betterrewards.shop.RerollShopItem;

@SpireInitializer
public class BetterRewardsMod implements PostInitializeSubscriber, EditStringsSubscriber {

	public static final String[] UNWANTED_SPECIAL_RELICS = { "Circlet", "Red Circlet", "Spirit Poop" };
	public static final String[] SCREEN_BOSS_RELICS = { "Calling Bell", "Orrery", "Tiny House" };
	private static final int DEFAULT_SCORE = 1000;

	public static boolean shouldShowButton = false;
	public static boolean alreadyStartedRewards = false;
	public static boolean isGettingRewards = false;
	public static boolean alreadyGotRewards = false;
	public static boolean isFunMode = false;
	public static CustomMod customMod;
	public static RunData lastRun;

	public static ArrayList<AbstractShopItem> shopItems;
	public static boolean isNeowDone;
	public static int playerGold;
	public static int button;

	public static final String MOD_NAME = "BetterRewards";
	public static final String BASE_RESOURCE_FOLDER = "betterrewardsmod";
	public static final String LOCALIZATION_FOLDER = BASE_RESOURCE_FOLDER + "/local/";
	public static final String DESCRIPTION = MOD_NAME;
	public static final String AUTHOR = "Skrelpoid";
	
	public static final String FALLBACK_LANG = "eng/";

	private static SpireConfig config;

	public static final Logger logger = LogManager.getLogger(BetterRewardsMod.class.getName());

	public static void initialize() {
		BaseMod.subscribe(new BetterRewardsMod());
	}

	public static void setIsGettingRewards(boolean b) {
		isGettingRewards = b;
		alreadyGotRewards = false;
		alreadyStartedRewards = false;
	}

	public static void startRewards(AbstractEvent e) {
		alreadyStartedRewards = true;
		e.imageEventText.clearAllDialogs();
		GenericEventDialog.hide();
		ShopRoom room = new ShopRoom();
		AbstractDungeon.currMapNode.room = room;
		room.onPlayerEntry();
	}

	public static boolean shouldShowInfo() {
		return customModeOnOrNotDailyNotTrial() && isGettingRewards && !alreadyStartedRewards && !alreadyGotRewards;
	}

	public static boolean isCurrentlyInShop() {
		return isGettingRewards && alreadyStartedRewards && !alreadyGotRewards;
	}

	public static void showInfo() {
		// copied from NeowEvent.dismissBubble()
		for (com.megacrit.cardcrawl.vfx.AbstractGameEffect e : AbstractDungeon.effectList) {
			if ((e instanceof InfiniteSpeechBubble)) {
				((InfiniteSpeechBubble) e).dismiss();
			}
		}
		BetterRewardsMod.updateLastRun();
		logger.info("Showing BetterRewardsInfoEvent");
		AbstractEvent info = new BetterRewardsInfoEvent();
		AbstractDungeon.getCurrRoom().event = info;
		AbstractDungeon.getCurrRoom().event.onEnterRoom();
	}

	@SuppressWarnings("unchecked")
	public static void updateLastRun() {
		List<RunData> runs = null;
		int character = History.characterIndex(AbstractDungeon.player);
		try {
			RunHistoryScreen runHistory = new RunHistoryScreen();
			runHistory.refreshData();

			if (character > 0) {
				((DropdownMenu) ReflectionHacks.getPrivate(runHistory, RunHistoryScreen.class, "characterFilter"))
						.setSelectedIndex(character);
			}

			Method resetRunsDropdown = RunHistoryScreen.class.getDeclaredMethod("resetRunsDropdown");
			resetRunsDropdown.setAccessible(true);
			resetRunsDropdown.invoke(runHistory);
			runs = (List<RunData>) ReflectionHacks.getPrivate(runHistory, RunHistoryScreen.class, "filteredRuns");
		} catch (Exception ex) {
			logger.error("Could not load run", ex);
		}

		if (runs == null || runs.isEmpty()) {
			lastRun = null;
		} else {
			try {
				lastRun = runs.stream().sorted(RunData.orderByTimestampDesc).findFirst().get();
			} catch (Exception ex) {
				lastRun = runs.get(0);
			}
		}
		if (lastRun == null || lastRun.score == 0) {
			logger.info("No last run available, setting score to default: " + DEFAULT_SCORE);
			lastRun = new RunData();
			lastRun.score = DEFAULT_SCORE;
		}
		logger.info("Player had a score of " + lastRun.score + " last run, Therefore they can get rewards.");
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
			return RelicLibrary.getRelic(relicToRemove).makeCopy();
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
			initCards.invoke(shopScreen);

			ArrayList<StoreRelic> relics = new ArrayList<>();
			Field shopRelics = ShopScreen.class.getDeclaredField("relics");
			shopRelics.setAccessible(true);
			relics.addAll((ArrayList<StoreRelic>) shopRelics.get(shopScreen));
			// Add rerolled Items back to relicPool and shuffle them
			for (StoreRelic sr : relics) {
				AbstractRelic relic = sr.relic;
				if (relic != null && !AbstractDungeon.player.hasRelic(relic.relicId)) {
					ArrayList<String> tmp = new ArrayList<>();
					switch (relic.tier) {
					case COMMON:
						tmp.add(relic.relicId);
						tmp.addAll(AbstractDungeon.commonRelicPool);
						AbstractDungeon.commonRelicPool = tmp;
						Collections.shuffle(AbstractDungeon.commonRelicPool);
						break;
					case UNCOMMON:
						tmp.add(relic.relicId);
						tmp.addAll(AbstractDungeon.uncommonRelicPool);
						AbstractDungeon.uncommonRelicPool = tmp;
						Collections.shuffle(AbstractDungeon.uncommonRelicPool);
						break;
					case RARE:
						tmp.add(relic.relicId);
						tmp.addAll(AbstractDungeon.rareRelicPool);
						AbstractDungeon.rareRelicPool = tmp;
						Collections.shuffle(AbstractDungeon.rareRelicPool);
						break;
					case SHOP:
						tmp.add(relic.relicId);
						tmp.addAll(AbstractDungeon.shopRelicPool);
						AbstractDungeon.shopRelicPool = tmp;
						Collections.shuffle(AbstractDungeon.shopRelicPool);
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

			Method setStartingCardPositions = ShopScreen.class.getDeclaredMethod("setStartingCardPositions");
			setStartingCardPositions.setAccessible(true);
			setStartingCardPositions.invoke(shopScreen);

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
				FontHelper.buttonLabelFont, isFunMode, panel, (l) -> {
				}, BetterRewardsMod::funToggle);
		panel.addUIElement(fun);
		BaseMod.registerModBadge(new Texture(BASE_RESOURCE_FOLDER + "/modBadge.png"), MOD_NAME, AUTHOR, DESCRIPTION, panel);

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

	public static void addCustomModeMods(CustomModeScreen screen, CustomMod sealedMod, CustomMod draftMod) {
		customMod = new CustomMod("BetterRewards", "b", false);
		EventStrings translation = CardCrawlGame.languagePack.getEventString("betterrewardsmod:Trial");
		customMod.name = translation.NAME;
		customMod.description = translation.DESCRIPTIONS[0];
		String label = FontHelper.colorString("[" + customMod.name + "]", customMod.color) + " "
				+ customMod.description;
		ReflectionHacks.setPrivate(customMod, CustomMod.class, "label", label);
		float height = -FontHelper.getSmartHeight(FontHelper.charDescFont, label, 1050.0F * Settings.scale,
				32.0F * Settings.scale) + 70.0F * Settings.scale;
		ReflectionHacks.setPrivate(customMod, CustomMod.class, "height", height);
		customMod.setMutualExclusionPair(sealedMod);
		customMod.setMutualExclusionPair(draftMod);
		ReflectionHacks.<List<CustomMod>>getPrivate(screen, CustomModeScreen.class, "modList").add(customMod);
	}

	public static boolean customModeOnOrNotDailyNotTrial() {
		return customMod == null || customMod.selected || (!Settings.isDailyRun && !Settings.isTrial);
	}
	
	public static boolean isCustomModRun() {
		return customMod != null && customMod.selected;
	}

	private String maybeLoadLanguage() {
		logger.info("Determining Language for BetterRewards");
		return Settings.language.toString().toLowerCase(Locale.ROOT) + "/";
	}

	@Override
	public void receiveEditStrings() {

		String localLanguage = maybeLoadLanguage();

		logger.info("Loading localization Strings for BetterRewards");

		final String json = loadJson(LOCALIZATION_FOLDER + localLanguage + "events.json");
		if (json != null) {
			BaseMod.loadCustomStrings(EventStrings.class, json);
		} else {
			BaseMod.loadCustomStrings(EventStrings.class, loadJson(LOCALIZATION_FOLDER + FALLBACK_LANG + "events.json"));
		}
	}

	private static String loadJson(String jsonPath) {
		FileHandle file = Gdx.files.internal(jsonPath);
		if (file.exists()) {
			return file.readString(String.valueOf(StandardCharsets.UTF_8));
		}
		return null;
	}

	// TODO FIX rewardsscreen in shop sometimes forces player to leave for now
	// fixed by not giving relics that show reward screen
	// could be really fixed by patching proceed button
}
