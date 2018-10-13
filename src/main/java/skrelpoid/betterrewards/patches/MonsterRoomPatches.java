package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;

import skrelpoid.betterrewards.BetterRewardsMod;

public class MonsterRoomPatches {

	// When a monster room is entered, shops should no longer auto restock.
	// Also, player loses all gold;
	@SpirePatch(clz = com.megacrit.cardcrawl.rooms.MonsterRoom.class, method = "onPlayerEntry")
	public static class FinishedRewards {
		@SpirePostfixPatch
		public static void Postfix(Object o) {
			BetterRewardsMod.finishedRewards();
		}
	}
}
