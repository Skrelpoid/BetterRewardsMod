package skrelpoid.betterrewards.patches;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CtBehavior;
import skrelpoid.betterrewards.BetterRewardsMod;

public class RunStartEndPatches {
	
	private static final Logger logger = LogManager.getLogger(RunStartEndPatches.class);

	@SpirePatch(clz = CardCrawlGame.class, method = "update")
	public static class RunStart {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(Object o) {
			BetterRewardsMod.shouldShowButton = true;
			logger.info("Run Started");
		}

		private static class Locator extends SpireInsertLocator {

			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				return LineFinder.findInOrder(ctMethodToPatch,
						new FieldAccessMatcher(CardCrawlGame.class, "monstersSlain"));
			}

		}
	}
	
	@SpirePatch(clz = AbstractPlayer.class, method = "playDeathAnimation")
	public static class EndOfRun {
		@SpirePostfixPatch
		public static void Postfix(AbstractPlayer p) {
			BetterRewardsMod.shouldShowButton = true;
			logger.info("Run Ended");
		}
		
	}

}
