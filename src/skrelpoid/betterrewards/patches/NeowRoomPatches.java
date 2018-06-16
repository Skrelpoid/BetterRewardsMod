package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import skrelpoid.betterrewards.BetterRewardsMod;

public class NeowRoomPatches {
	@SpirePatch(cls = "com.megacrit.cardcrawl.neow.NeowRoom", method = "<ctor>")
	public static class RunHistoryRefresh {
		// before Neow is initialized, check if can get rewards
		// should happen before every run, before Neow is shown
		public static void Prefix(Object o, boolean b) {
			AbstractPlayer.PlayerClass player = AbstractDungeon.player.chosenClass;
			BetterRewardsMod.refreshRunHistory();
			BetterRewardsMod.checkCanGetRewards(player.name());
		}
	}
}
