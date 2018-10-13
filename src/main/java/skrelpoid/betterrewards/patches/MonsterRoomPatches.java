package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

import skrelpoid.betterrewards.BetterRewardsMod;

public class MonsterRoomPatches {

	// When a monster room is entered, shops should no longer auto restock.
	// Also, player loses all gold;
	@SpirePatch(cls = "com.megacrit.cardcrawl.rooms.MonsterRoom", method = "onPlayerEntry")
	public static class FinishedRewards {
		public static void Postfix(Object o) {
			BetterRewardsMod.finishedRewards();
		}
	}
}
