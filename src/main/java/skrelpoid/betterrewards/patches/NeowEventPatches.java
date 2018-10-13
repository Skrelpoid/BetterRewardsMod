package skrelpoid.betterrewards.patches;

import java.lang.reflect.Field;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.neow.NeowEvent;

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

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class AddBetterRewardsButton {
		@SpireInsertPatch(rloc = 45)
		public static void Insert(AbstractEvent e, boolean b) {
			BetterRewardsMod.isNeowDone = b;
			if (!Settings.isDailyRun && !b) {
				BetterRewardsMod.button = RoomEventDialog.optionList.size();
				e.roomEventText.addDialogOption("[Turn Around]");
			}
		}
	}

	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowEvent.class, method = SpirePatch.CONSTRUCTOR, paramtypez = boolean.class)
	public static class FixEventImage {
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
				// buttonPressed = 1 is the better rewards button
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
