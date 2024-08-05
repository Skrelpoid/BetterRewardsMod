package skrelpoid.betterrewards.events;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;

import skrelpoid.betterrewards.BetterRewardsMod;
import skrelpoid.betterrewards.GoldHelper;

public class BetterRewardsInfoEvent extends AbstractImageEvent {

	public static final String ID = "betterrewardsmod:Better_Rewards_Event";
	
	private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
	private static final String NAME = eventStrings.NAME;
	private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
	private static final String[] OPTIONS = eventStrings.OPTIONS;
	
	public static final int ENTER_PORTAL = 0;
	public static final int GET_MORE_GOLD = 1;

	public static final int GO_TO_MAP = 0;
	public static final int GO_TO_NEOW = 1;
	public static final int TAKE_GOLD = 2;

	public static final int INFO = 0;
	public static final int SCALED_GOLD = 1;
	public static final int FINISHED = 2;
	public static final int MAP = 99;

	public int state = INFO;

	private int maxGold = 0;
	private int gold = 0;
	private double goldPercent = 1;
	private double stepPercent = 0.1;

	private int loseHP;

	public BetterRewardsInfoEvent() {
		super(NAME, getBody(), BetterRewardsMod.BASE_RESOURCE_FOLDER + "/event/betterRewardsEvent.jpg");

		imageEventText.clearAllDialogs();
		imageEventText.clearRemainingOptions();

		imageEventText.setDialogOption(OPTIONS[0]);
		imageEventText.setDialogOption(getNeowOrHeartText());
		imageEventText.setDialogOption(OPTIONS[3]);
	}

	private String getNeowOrHeartText() {
		if (Loader.isModLoaded("downfall") && isDownfallEvilMode()) {
			return OPTIONS[1];
		}
		return OPTIONS[2];
	}

	private static String getBody() {
		String body = DESCRIPTIONS[0] + BetterRewardsMod.lastRun.score + DESCRIPTIONS[1];

		return body;
	}

	@Override
	public void onEnterRoom() {
		RoomEventDialog.waitForInput = true;
	}

	@Override
	protected void buttonEffect(int buttonPressed) {
		switch (state) {
		case INFO:
			switch (buttonPressed) {
			case GO_TO_MAP:
				openMap();
				break;
			case TAKE_GOLD:
				BetterRewardsMod.playerGold = AbstractDungeon.player.gold;
				maxGold = BetterRewardsMod.lastRun.score;
				gold = BetterRewardsMod.isFunMode ? maxGold : GoldHelper.getGold(maxGold);
				AbstractDungeon.player.gold = gold;
				goldPercent = gold / (double) maxGold;
				if (AbstractDungeon.player.gold < maxGold) {
					startScaledGold();
				} else {
					finish();
				}
				break;
			case GO_TO_NEOW:
				BetterRewardsMod.setIsGettingRewards(false);
				imageEventText.clearAllDialogs();
				GenericEventDialog.hide();
				AbstractEvent event = getNeowOrHeartEvent();
				AbstractDungeon.currMapNode.room.event = event;
				event.onEnterRoom();
				break;
			default:
				openMap();
				break;
			}
			break;
		case SCALED_GOLD:
			switch (buttonPressed) {
			case ENTER_PORTAL:
				BetterRewardsMod.startRewards(this);
				break;
			case GET_MORE_GOLD:
				getMoreGold();
				break;
			default:
				BetterRewardsMod.startRewards(this);
				break;
			}
			break;
		case FINISHED:
			BetterRewardsMod.startRewards(this);
			break;
		default:
			openMap();
			break;
		}

	}

	private AbstractEvent getNeowOrHeartEvent() {
		if (Loader.isModLoaded("downfall")) {
			try {
				if (isDownfallEvilMode()) {
					Class<? extends AbstractEvent> heartEventClass = Class.forName("downfall.events.HeartEvent")
							.asSubclass(AbstractEvent.class);
					Constructor<? extends AbstractEvent> constructor = heartEventClass.getConstructor(boolean.class);
					return constructor.newInstance(BetterRewardsMod.isNeowDone);
				}
			} catch (Exception ex) {
				BetterRewardsMod.logger.error("Could not load or instantiate Downfall HeartEvent", ex);
			}
		}
		return new NeowEvent(BetterRewardsMod.isNeowDone);
	}

	private boolean isDownfallEvilMode() {
		try {
			Class<?> evilModeCharactersClass = Class.forName("downfall.patches.EvilModeCharacterSelect");
			Field evilMode = evilModeCharactersClass.getDeclaredField("evilMode");
			return evilMode.getBoolean(evilModeCharactersClass);
		} catch (Exception ex) {
			BetterRewardsMod.logger.error("Could not load or instantiate Downfall EvilModeCharacterSelect", ex);
		}
		return false;
	}

	private void getMoreGold() {
		AbstractDungeon.player.gold += gold;
		AbstractDungeon.player.damage(new DamageInfo(null, loseHP, DamageInfo.DamageType.HP_LOSS));
		goldPercent = AbstractDungeon.player.gold / (double) maxGold;
		if (AbstractDungeon.player.gold == maxGold) {
			// player has obtained max gold
			finish();
		} else {
			state = SCALED_GOLD;
			calculateHPandGold();
			imageEventText.updateBodyText(DESCRIPTIONS[2]);
			imageEventText.updateDialogOption(GET_MORE_GOLD,
					OPTIONS[4] + loseHP + OPTIONS[5] + gold + OPTIONS[6]);
		}
	}

	private void startScaledGold() {
		state = SCALED_GOLD;
		imageEventText.updateBodyText(DESCRIPTIONS[3] + gold + DESCRIPTIONS[4]);
		calculateHPandGold();
		imageEventText.clearAllDialogs();
		imageEventText.setDialogOption(OPTIONS[7]);
		imageEventText.setDialogOption(OPTIONS[4] + loseHP + OPTIONS[5] + gold + OPTIONS[6]);
	}

	private void calculateHPandGold() {
		if (1 - goldPercent > 0.2) {
			stepPercent = 0.1;
		} else {
			stepPercent = 1 - goldPercent;
		}
		loseHP = (int) Math.round(stepPercent * AbstractDungeon.player.maxHealth);
		gold = (int) Math.round(stepPercent * maxGold);
		// make sure there are no rounding errors
		if (stepPercent > 0.1) {
			gold = maxGold - AbstractDungeon.player.gold;
		}
	}

	private void finish() {
		state = FINISHED;
		imageEventText.updateBodyText(DESCRIPTIONS[5]);
		imageEventText.clearAllDialogs();
		imageEventText.setDialogOption(OPTIONS[8]);
	}

}
