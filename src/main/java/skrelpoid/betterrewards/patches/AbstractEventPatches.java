package skrelpoid.betterrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

// @formatter:off
public class AbstractEventPatches {

	@SpirePatch(clz = com.megacrit.cardcrawl.events.AbstractEvent.class, method = "openMap")
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
}
