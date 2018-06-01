package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

import skrelpoid.betterrewards.BetterRewardsMod;

public class RelicLibraryPatches {

	@SpirePatch(cls = "com.megacrit.cardcrawl.helpers.RelicLibrary", method = "getRelic")
	public static class PatchBaseModIssue {
		public static void Prefix(@ByRef(type = "java.lang.String") String[] str) {
			BetterRewardsMod.fixBaseModIssue(str);
		}
	}
}
