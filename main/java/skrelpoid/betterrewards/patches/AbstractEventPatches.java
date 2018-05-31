package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import skrelpoid.betterrewards.BetterRewardsMod;

// @formatter:off
public class AbstractEventPatches {

	@SpirePatch(cls = "com.megacrit.cardcrawl.events.AbstractEvent", method = "openMap")
	public static class Start {
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getMethodName().equals("open")) {
						m.replace("{ if (skrelpoid.betterrewards.BetterRewardsMod.shouldShowInfo()) {"
								+ "skrelpoid.betterrewards.BetterRewardsMod.showInfo();"
								+ "} else { "
								+ " $proceed($$); "
								+ "} }");
					}
				}

				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if (f.getFieldName().equals("phase")) {
						f.replace("{ if (!skrelpoid.betterrewards.BetterRewardsMod.shouldShowInfo()) {"
								+ "$_ = $proceed($$); "
								+ "} }");
					}
				}
			};
		}
	}


	// Not needed anymore, probably will be removed
	// @SpirePatch(cls = "com.megacrit.cardcrawl.events.AbstractEvent", method =
	// "openMap")
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


}
