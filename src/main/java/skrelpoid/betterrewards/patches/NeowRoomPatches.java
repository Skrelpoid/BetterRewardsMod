package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import skrelpoid.betterrewards.BetterRewardsMod;

public class NeowRoomPatches {
	@SpirePatch(clz = com.megacrit.cardcrawl.neow.NeowRoom.class, method = SpirePatch.CONSTRUCTOR)
	public static class RunHistoryRefresh {
		// before Neow is initialized, check if can get rewards
		// should happen before every run, before Neow is shown
		@SpirePrefixPatch
		public static void Prefix(Object o, boolean b) {
			AbstractPlayer.PlayerClass player = AbstractDungeon.player.chosenClass;
			BetterRewardsMod.refreshRunHistory();
			BetterRewardsMod.checkCanGetRewards(player.name());
		}
	}
}
