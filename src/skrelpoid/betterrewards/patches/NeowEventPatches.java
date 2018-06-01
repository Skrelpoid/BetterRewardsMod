package skrelpoid.betterrewards.patches;

import java.lang.reflect.Field;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.neow.NeowEvent;

import skrelpoid.betterrewards.BetterRewardsMod;

public class NeowEventPatches {

	// Only for Testing
	// @SpirePatch(cls = "com.megacrit.cardcrawl.neow.NeowEvent", method =
	// "ctor", paramtypes = "boolean")
	public static class ForceBlessing {
		@SpireInsertPatch(rloc = 1)
		public static void Insert(Object o, boolean b) {
			Settings.isTestingNeow = true;
		}
	}

	@SpirePatch(cls = "com.megacrit.cardcrawl.neow.NeowEvent", method = "ctor", paramtypes = "boolean")
	public static class AddBetterRewardsButton {
		@SpireInsertPatch(rloc = 45)
		public static void Insert(Object o, boolean b) {
			BetterRewardsMod.isNeowDone = b;
			if (!Settings.isDailyRun && !b) {
				BetterRewardsMod.button = RoomEventDialog.optionList.size();
				RoomEventDialog.addDialogOption("[Turn Around]");
			}
		}
	}

	@SpirePatch(cls = "com.megacrit.cardcrawl.neow.NeowEvent", method = "buttonEffect")
	public static class MaybeStartRewards {
		public static void Prefix(Object o, int buttonPressed) {
			try {
				Field screenNumField = NeowEvent.class.getDeclaredField("screenNum");
				screenNumField.setAccessible(true);
				int sn = screenNumField.getInt(o);
				// buttonPressed = 1 is the better rewards button
				if (buttonPressed == BetterRewardsMod.button && acceptableScreenNum(sn)) {
					BetterRewardsMod.setIsGettingRewards(true);
					// screenNum = 99 is the default value for leave event. This
					// calls openMap, which is patched to start a BetterRewards
					screenNumField.setInt(o, 99);
				} else {
					BetterRewardsMod.setIsGettingRewards(false);
					if (sn != 3 && RoomEventDialog.optionList.size() > 1) {
						RoomEventDialog.removeDialogOption(1);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// screenNum = 0, 1 or 2 mean talk option
		private static boolean acceptableScreenNum(int sn) {
			return sn == 0 || sn == 1 || sn == 2;
		}
	}

}
