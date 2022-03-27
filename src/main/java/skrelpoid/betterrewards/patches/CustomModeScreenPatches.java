package skrelpoid.betterrewards.patches;


import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import javassist.CtBehavior;
import skrelpoid.betterrewards.BetterRewardsMod;

public class CustomModeScreenPatches {

	@SpirePatch(clz = CustomModeScreen.class, method = "initializeMods")
	public static class AddCustomModeModPatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "sealedMod", "draftMod" })
		public static void Insert(CustomModeScreen screen, CustomMod sealedMod, CustomMod draftMod) {
			BetterRewardsMod.addCustomModeMods(screen, sealedMod, draftMod);
		}
		
		private static class Locator extends SpireInsertLocator {

			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				return LineFinder.findInOrder(ctMethodToPatch,
						new MethodCallMatcher(UnlockTracker.class, "isAchievementUnlocked"));
			}

		}
	}

}
