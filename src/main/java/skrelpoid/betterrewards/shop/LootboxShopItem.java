package skrelpoid.betterrewards.shop;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BlackBlood;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import skrelpoid.betterrewards.BetterRewardsMod;

public class LootboxShopItem extends AbstractShopItem {

	// good
	public static final int BOSS_RELIC = 200;
	public static final int RELIC = 1000;
	public static final int SPECIAL_RELIC = 200;
	public static final int COLORED_CARD = 1500;
	public static final int COLORLESS_CARD = 500;

	// sometimes good sometimes bad
	public static final int REMOVE_RANDOM_CARD = 700;
	public static final int TRANSFORM = 700;

	// bad
	public static final int REMOVE_RANDOM_RELIC = 300;
	public static final int TAKE_DAMAGE = 500;
	public static final int LOSE_MAX_HP = 350;
	public static final int LOSE_GOLD = 400;
	public static final int CURSE = 350;

	public int range = BOSS_RELIC + RELIC + SPECIAL_RELIC + COLORED_CARD + COLORLESS_CARD + REMOVE_RANDOM_CARD
			+ TRANSFORM + REMOVE_RANDOM_RELIC + TAKE_DAMAGE + LOSE_MAX_HP + LOSE_GOLD + CURSE - 1;

	public int bossRelic = BOSS_RELIC;
	public int relic = RELIC;
	public int specialRelic = SPECIAL_RELIC;
	public int coloredCard = COLORED_CARD;
	public int colorlessCard = COLORLESS_CARD;
	public int removeRandomCard = REMOVE_RANDOM_CARD;
	public int transform = TRANSFORM;
	public int removeRandomRelic = REMOVE_RANDOM_RELIC;
	public int takeDamage = TAKE_DAMAGE;
	public int loseMaxHp = LOSE_MAX_HP;
	public int loseGold = LOSE_GOLD;
	public int curse = CURSE;

	private Random rng;
	private PowerTip tip;

	private Logger logger = LogManager.getLogger(LootboxShopItem.class.getName());

	public LootboxShopItem(ShopScreen shopScreen, float x, float y) {
		super(shopScreen, "shop/lootbox.png", "BetterRewards Lootbox",
				"Can give you anything from a Curse to a Boss relic. Cost increases by 25. Chances for good rewards increase. Autorestocks.",
				50, x, y);
		rng = new Random(Settings.seed);
		tip = new PowerTip("Last Roll", "You didn't roll yet! Do you dare?");
		tips.add(tip);
		printChances();
	}

	@Override
	protected void onPurchase() {
		isVisible = true;
		price += 25;
		applyDiscount(1);
		int roll = rng.random(range);
		logger.info("ROLL " + roll);
		getForRoll(roll);
		updateChances();
	}

	private void updateChances() {
		bossRelic += 150;
		relic += 250;
		specialRelic += 150;
		colorlessCard += 100;
		coloredCard += 50;
		removeRandomCard += 25;
		transform += 25;
		removeRandomRelic += 20;
		takeDamage += 20;
		loseMaxHp += 20;
		loseGold += 20;
		curse += 20;

		logger.info("old range: " + range);
		range = bossRelic + relic + specialRelic + coloredCard + colorlessCard + removeRandomCard + transform
				+ removeRandomRelic + takeDamage + loseMaxHp + loseGold + curse - 1;
		logger.info("new range: " + range);
		// printChances();
	}

	public void printChances() {
		logger.info("printing chances");
		logger.info("Boss relic: " + (float) bossRelic / range);
		logger.info("relic: " + (float) relic / range);
		logger.info("special relic: " + (float) specialRelic / range);
		logger.info("coloredCard: " + (float) coloredCard / range);
		logger.info("colorlessCard: " + (float) colorlessCard / range);
		logger.info("removeRandomCard: " + (float) removeRandomCard / range);
		logger.info("transform: " + (float) transform / range);
		logger.info("removeRandomRelic: " + (float) removeRandomRelic / range);
		logger.info("takeDamage: " + (float) takeDamage / range);
		logger.info("loseMaxHp: " + (float) loseMaxHp / range);
		logger.info("loseGold: " + (float) loseMaxHp / range);
		logger.info("curse: " + (float) curse / range);
	}

	private void getForRoll(int roll) {
		AbstractPlayer player = AbstractDungeon.player;
		int reward = bossRelic;
		if (roll < reward) {
			AbstractRelic relic = BetterRewardsMod.returnRandomScreenlessBossRelic();
			if (relic instanceof BlackBlood) {
				player.loseRelic("Burning Blood");
			}
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, relic);
			logger.info("bossrelic");
			displayLastRoll("You got a Boss Relic: " + relic.name + ".");
			return;
		}
		reward += relic;
		if (roll < reward) {
			AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, relic);
			logger.info("relic");
			displayLastRoll("You got a Relic: " + relic.name + ".");
			return;
		}
		reward += specialRelic;
		if (roll < reward) {
			AbstractRelic relic = returnSpecialRelic();
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, relic);
			logger.info("specialRelic");
			displayLastRoll("You got a Special Relic: " + relic.name + ".");
			return;
		}
		reward += coloredCard;
		if (roll < reward) {
			AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity());
			AbstractDungeon.effectList
					.add(new ShowCardAndObtainEffect(card.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			logger.info("coloredCard");
			displayLastRoll("You got a Card: " + card.name + ".");
			return;
		}
		reward += colorlessCard;
		if (roll < reward) {
			AbstractCard card = AbstractDungeon.returnColorlessCard();
			AbstractDungeon.effectList
					.add(new ShowCardAndObtainEffect(card.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			logger.info("colorlessCard");
			displayLastRoll("You got a Colorless Card: " + card.name + ".");
			return;
		}
		reward += removeRandomCard;
		if (roll < reward) {
			AbstractCard card = getRandomDeckCard(true);
			if (card != null) {
				String name = card.name;
				AbstractDungeon.effectList.add(new PurgeCardEffect(card));
				player.masterDeck.removeCard(card);
				logger.info("removeRandomCard");
				displayLastRoll("You lost a Card from your Deck: " + name);
			} else {
				logger.info("unable to removeRandomCard");
				displayLastRoll("No card to remove :(");
			}
			return;
		}
		reward += transform;
		if (roll < reward) {
			AbstractCard card = getRandomDeckCard(true);
			if (card != null) {
				String oldName = card.name;
				player.masterDeck.removeCard(card);
				AbstractDungeon.transformCard(card);
				AbstractCard transformedCard = AbstractDungeon.getTransformedCard();
				AbstractDungeon.effectsQueue.add(
						new ShowCardAndObtainEffect(transformedCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				String transformedName = transformedCard.name;
				logger.info("transform");
				displayLastRoll("A Card from your Deck was transformed: " + oldName + " -> " + transformedName + ".");
			} else {
				logger.info("unable to transform");
				displayLastRoll("No card to transform :(");
			}
			return;
		}
		reward += removeRandomRelic;
		if (roll < reward) {
			if (player.relics != null && player.relics.size() > 0) {
				AbstractRelic relic = player.relics.get(rng.random(player.relics.size() - 1));
				String name = relic.name;
				player.loseRelic(relic.relicId);
				logger.info("removeRandomRelic");
				displayLastRoll("You lost a relic: " + name + ".");
			} else {
				logger.info("unable to removeRandomRelic");
				displayLastRoll("No relic to remove :(");
			}
			return;
		}
		reward += takeDamage;
		if (roll < reward) {
			player.damage(new DamageInfo(null, 8, DamageInfo.DamageType.HP_LOSS));
			logger.info("takeDamage");
			displayLastRoll("You took 8 damage.");
			return;
		}
		reward += loseMaxHp;
		if (roll < reward) {
			player.decreaseMaxHealth(5);
			logger.info("loseMaxHp");
			displayLastRoll("You lost 5 Max HP.");
			return;
		}
		reward += loseGold;
		if (roll < reward) {
			int goldLost = 50;
			if (player.gold >= goldLost) {
				player.loseGold(goldLost);
			} else {
				goldLost = player.gold;
				player.gold = 0;
			}
			logger.info("loseGold");
			displayLastRoll("You lost " + goldLost + " Gold.");
			return;
		}
		reward += curse;
		if (roll < reward) {
			AbstractCard card = AbstractDungeon.returnRandomCurse();
			AbstractDungeon.topLevelEffects
					.add(new ShowCardAndObtainEffect(card, Settings.WIDTH / 2, Settings.HEIGHT / 2));
			logger.info("curse");
			displayLastRoll("You got a Curse: " + card.name + ".");
			return;
		}
		// if reached this point, something went wrong
		logger.info("This should not have happened. Giving player relic as backup");
		AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, relic);
		displayLastRoll("You got a Relic: " + relic.name + ".");
	}

	private AbstractCard getRandomDeckCard(boolean purge) {
		ArrayList<AbstractCard> deck = null;
		if (purge) {
			deck = AbstractDungeon.player.masterDeck.getPurgeableCards().group;
		} else {
			deck = AbstractDungeon.player.masterDeck.group;
		}
		if (deck == null || deck.size() == 0) {
			return null;
		}
		return deck.get(rng.random(deck.size() - 1));
	}

	private AbstractRelic returnSpecialRelic() {
		ArrayList<AbstractRelic> toRemove = new ArrayList<>();
		ArrayList<AbstractRelic> specialRelics = new ArrayList<>();
		specialRelics.addAll(RelicLibrary.specialList);

		// Don't give unwanted relics
		for (String s : BetterRewardsMod.UNWANTED_SPECIAL_RELICS) {
			for (AbstractRelic r : specialRelics) {
				if (r != null && r.relicId.equals(s)) {
					toRemove.add(r);
					break;
				}
			}
		}

		// Don't give relics player already has
		ArrayList<AbstractRelic> playerRelics = new ArrayList<>();
		playerRelics.addAll(AbstractDungeon.player.relics);
		for (AbstractRelic p : playerRelics) {
			for (AbstractRelic r : specialRelics) {
				if (r != null && p != null && r.relicId.equals(p.relicId) && !toRemove.contains(r)) {
					toRemove.add(r);
					break;
				}
			}
		}

		specialRelics.removeAll(toRemove);

		if (specialRelics.isEmpty()) {
			return new Circlet();
		} else {
			return specialRelics.get(rng.random(specialRelics.size() - 1)).makeCopy();
		}
	}

	private void displayLastRoll(String text) {
		tip.body = text;
	}

}
