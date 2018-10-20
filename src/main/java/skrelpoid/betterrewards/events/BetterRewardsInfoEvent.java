package skrelpoid.betterrewards.events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.neow.NeowEvent;

import skrelpoid.betterrewards.BetterRewardsMod;
import skrelpoid.betterrewards.GoldHelper;

public class BetterRewardsInfoEvent extends AbstractImageEvent {

	public static final int ENTER_PORTAL = 0;
	public static final int GET_MORE_GOLD = 1;

	public static final int GO_TO_MAP = 0;
	public static final int GO_TO_NEOW = 1;
	public static final int TAKE_GOLD = 2;

	public static final int INFO = 0;
	public static final int SCALED_GOLD = 1;
	public static final int FINISHED = 2;

	public int state = INFO;

	private int maxGold = 0;
	private int gold = 0;
	private double goldPercent = 1;
	private double stepPercent = 0.1;

	private int loseHP;

	public BetterRewardsInfoEvent() {
		super("BetterRewards", getBody(), "event/betterRewardsEvent.jpg");
		imageEventText.clearAllDialogs();
		imageEventText.clearRemainingOptions();

		imageEventText.setDialogOption("[Leave] Go to the Map.");
		imageEventText.setDialogOption("[Turn Around] Return to Neow.");
		if (BetterRewardsMod.canGetRewards) {
			imageEventText.setDialogOption("[Take coins] #yPocket #ythe #ystrange #yGold.");
		}
	}

	private static String getBody() {
		String body = "You turned around, because you heard a strange noise behind you. "
				+ "You see a Portal and a pot full of #yGold. NL "
				+ "This #yGold looks strangely alien. You've never seen anything like it. "
				+ "It probably can't be used in the Spire. ";
		if (BetterRewardsMod.canGetRewards) {
			body += " NL Your intuition tells you there's exactly " + BetterRewardsMod.lastRun.score
					+ " coins in the pot.";
		} else {
			body += " NL As you approach the pot, it suddenly disappears! You can only spot a small note on the ground: "
					+ "~S~ ~C~ ~O~ ~R~ ~E~";
		}
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
				imageEventText.clearRemainingOptions();
				GenericEventDialog.hide();
				NeowEvent event = new NeowEvent(BetterRewardsMod.isNeowDone);
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
			imageEventText.updateBodyText("There's still some #yGold left. ");
			imageEventText.updateDialogOption(GET_MORE_GOLD,
					"[Grab more Gold] #rLose #r" + loseHP + " #rHP. #yGet #y" + gold + " #yGold.");
		}
	}

	private void startScaledGold() {
		state = SCALED_GOLD;
		imageEventText.updateBodyText("As you try to pick up the pot of #yGold, it breaks. "
				+ "All the strange coins fell on the ground. You pick up " + gold + " coins. "
				+ "Unfortunately, some of the coins rolled away and into some spikes. ");
		calculateHPandGold();
		imageEventText.clearAllDialogs();
		imageEventText.setDialogOption("[Enter Portal] Leave with the #yGold you have.");
		imageEventText.setDialogOption("[Grab more Gold] #rLose #r" + loseHP + " #rHP. #yGet #y" + gold + " #yGold.");
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
		imageEventText.updateBodyText("You pocketed all the #yGold. There's nothing of it left.");
		imageEventText.clearAllDialogs();
		imageEventText.setDialogOption("[Enter Portal] Go through it and see where you end up.");
	}

}
