package skrelpoid.betterrewards.patches;

import java.lang.reflect.Field;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import skrelpoid.betterrewards.BetterRewardsMod;

public class ProceedButtonPatches {

	@SpirePatch(cls = "com.megacrit.cardcrawl.ui.buttons.ProceedButton", method = "update")
	public static class FixProceedHitbox {
		@SpireInsertPatch(rloc = 13)
		public static void Insert(Object o) {
			try {
				Field f = ProceedButton.class.getDeclaredField("hitbox");
				f.setAccessible(true);
				BetterRewardsMod.fixProceedHitbox((Hitbox) f.get(o));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
