package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import skrelpoid.betterrewards.BetterRewardsMod;

public class AbstractEventPatches {

	@SpirePatch(cls = "com.megacrit.cardcrawl.events.AbstractEvent", method = "openMap")
	public static class StartRewards {
		public static void Replace(Object o) {
			if (BetterRewardsMod.shouldShowInfo()) {
				BetterRewardsMod.showInfo();
			} else {
				AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
				AbstractDungeon.dungeonMapScreen.open(false);
			}
		}
	}

	// Doesnt work, just opens map
	// @SpirePatch(cls = "com.megacrit.cardcrawl.events.AbstractEvent", method =
	// "openMap")
	public static class Start {
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getMethodName().equals("open") && m.getClass().equals(DungeonMapScreen.class)) {
						m.replace(
								"{ if (skrelpoid.betterrewards.BetterRewards.shouldShowInfo()) { skrelpoid.betterrewards.BetterRewards.showInfo(); } else {com.megacrit.cardcrawl.dungeons.AbstractDungeon.dungeonMapScreen.open(false); } }");

					}
				}
			};
		}
	}

}
