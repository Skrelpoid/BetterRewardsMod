package skrelpoid.betterrewards.patches;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DebugPatches {
	public static final Logger logger = LogManager.getLogger(DebugPatches.class.getName());

	// @SpirePatch(cls = "com.megacrit.cardcrawl.helpers.RelicLibrary", method =
	// "getRelic")
	public static class DebugGetRelic {
		public static void Prefix(Object o1) {
			String key = (String) o1;
			logger.info("RelicLibrary.getRelic with " + key);

		}
	}

	// @SpirePatch(cls = "com.megacrit.cardcrawl.shop.ShopScreen", method =
	// "initRelics")
	public static class DebugReturnRandomRelicEnd {
		@SpireInsertPatch(rlocs = { 6, 8 }, localvars = { "tempRelic" })
		public static void Insert(Object o1, Object o2) {
			AbstractRelic relic = (AbstractRelic) o2;
			logger.info("returnRandomRelicEnd with " + relic.relicId);
			logger.info("named " + relic.name);
		}
	}
}
