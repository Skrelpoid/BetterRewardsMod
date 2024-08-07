package skrelpoid.betterrewards.patches;

import java.lang.reflect.Field;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;

import skrelpoid.betterrewards.BetterRewardsMod;

public class NeowEventPatches {

	// Only for Testing
	// @SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method =
	// "ctor", paramtypes = "boolean")
	public static class ForceBlessing {
		@SpireInsertPatch(rloc = 1)
		public static void Insert(Object o, boolean b) {
			Settings.isTestingNeow = true;
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowRoom.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class AddBetterRewardsButton {
		@SpirePostfixPatch
		public static void Postfix(NeowRoom room, boolean b) {
			BetterRewardsMod.isNeowDone = b;
			if (BetterRewardsMod.customModeOnOrNotDailyNotTrial() && !BetterRewardsMod.isNeowDone && BetterRewardsMod.shouldShowButton) {
				BetterRewardsMod.shouldShowButton = false;
				final String translatedText = CardCrawlGame.languagePack.getEventString("betterrewardsmod:NeowEventPatches").OPTIONS[0];
				if (BetterRewardsMod.isCustomModRun()) {
					BetterRewardsMod.button = 0;
					room.event.roomEventText.updateDialogOption(0, translatedText);
				} else {
					BetterRewardsMod.button = RoomEventDialog.optionList.size();
					room.event.roomEventText.addDialogOption(translatedText);
				}
			}
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class FixEventImage {
		@SpirePostfixPatch
		public static void Postfix(NeowEvent e, boolean b) {
			e.imageEventText.clear();
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method = "buttonEffect")
	public static class MaybeStartRewards {
		@SpirePrefixPatch
		public static void Prefix(AbstractEvent e, int buttonPressed) {
			try {
				Field screenNumField = NeowEvent.class.getDeclaredField("screenNum");
				screenNumField.setAccessible(true);
				int sn = screenNumField.getInt(e);
				maybeStartRewards(e, buttonPressed, screenNumField, sn);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
	@SpirePatch(cls = "downfall.events.HeartEvent", method = "buttonEffect", optional = true)
	public static class MaybeStartRewardsDownfall {
		@SpirePrefixPatch
		public static void Prefix(AbstractEvent e, int buttonPressed) {
			try {
				Class<? extends AbstractEvent> heartEventClass = Class.forName("downfall.events.HeartEvent").asSubclass(AbstractEvent.class);
				Field screenNumField = heartEventClass.getDeclaredField("screenNum");
				screenNumField.setAccessible(true);
				int sn = screenNumField.getInt(e);
				maybeStartRewards(e, buttonPressed, screenNumField, sn);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	// screenNum = 0, 1 or 2 mean talk option
	// 10 is only ok for trial (Custom Mode) I think
	private static boolean acceptableScreenNum(int sn) {
		return sn == 0 || sn == 1 || sn == 2 ||
				(Settings.isTrial && sn == 10 && BetterRewardsMod.customMod != null && BetterRewardsMod.customMod.selected  );
	}

	private static void maybeStartRewards(AbstractEvent e, int buttonPressed, Field screenNumField, int sn)
			throws IllegalAccessException {
		if (buttonPressed == BetterRewardsMod.button && acceptableScreenNum(sn)) {
			BetterRewardsMod.setIsGettingRewards(true);
			// screenNum = 99 is the default value for leave event. This
			// calls openMap, which is patched to start a BetterRewards
			screenNumField.setInt(e, 99);
		} else {
			BetterRewardsMod.setIsGettingRewards(false);
			if (sn != 3 && RoomEventDialog.optionList.size() > 1) {
				e.roomEventText.removeDialogOption(BetterRewardsMod.button);
			}
		}
	}
}
