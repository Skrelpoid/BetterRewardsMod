package skrelpoid.betterrewards.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.neow.NeowEvent;

import skrelpoid.betterrewards.BetterRewardsMod;

public class BetterRewardsInfoEvent extends AbstractImageEvent {

	public static final int GO_TO_MAP = 0;
	public static final int GO_TO_NEOW = 1;
	public static final int GET_REWARDS = 2;

	public BetterRewardsInfoEvent() {
		super("BetterRewards", getBody(), "event/betterRewardsEvent.jpg");
		AbstractDungeon.dialog.clear();
		GenericEventDialog.clearAllDialogs();
		GenericEventDialog.clearRemainingOptions();

		GenericEventDialog.setDialogOption("[Leave] Go to the Map");
		GenericEventDialog.setDialogOption("[Go Back] Return to Neow");
		if (BetterRewardsMod.canGetRewards) {
			GenericEventDialog.setDialogOption("[Enter Portal] Go to a special shop");
		}
	}

	private static String getBody() {
		String body = "The BetterRewards Mod was properly loaded! Congratulations! "
				+ "This Mod makes it possible to get special rewards instead of getting a reward from Neow, "
				+ "even when you didn't reach the first boss. Instead of choosing from rewards, "
				+ "you get Gold equal to the score of your last run with this Character "
				+ "and get to purchase Items from a special shop. After this shop, you lose all your Gold.";
		if (BetterRewardsMod.canGetRewards) {
			body += "You will have " + BetterRewardsMod.lastRun.score + " Gold if you choose to get rewards.";
		} else {
			body += "You either don't have a run with this Character yet or your last run had a score of 0. "
					+ "Therefore, you can not get rewards.";
		}
		return body;
	}

	@Override
	public void onEnterRoom() {
		RoomEventDialog.waitForInput = true;
	}

	@Override
	protected void buttonEffect(int buttonPressed) {
		switch (buttonPressed) {
		case GO_TO_MAP:
			openMap();
			break;
		case GET_REWARDS:
			BetterRewardsMod.startRewards();
			break;
		case GO_TO_NEOW:
			BetterRewardsMod.setIsGettingRewards(false);
			AbstractDungeon.dialog.clear();
			GenericEventDialog.clearAllDialogs();
			GenericEventDialog.clearRemainingOptions();
			GenericEventDialog.hide();
			NeowEvent event = new NeowEvent(false);
			AbstractDungeon.currMapNode.room.event = event;
			event.onEnterRoom();
			break;

		}
	}

}
