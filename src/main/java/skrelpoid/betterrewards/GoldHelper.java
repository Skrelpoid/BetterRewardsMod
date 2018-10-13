package skrelpoid.betterrewards;

import java.math.BigDecimal;

import com.badlogic.gdx.math.MathUtils;

public class GoldHelper {

	public static final BigDecimal FACTOR_A = new BigDecimal("0.00000014285");
	public static final BigDecimal FACTOR_B = new BigDecimal("-0.00065714285");
	public static final BigDecimal FACTOR_C = new BigDecimal("1.064285714");

	public static BigDecimal parabola(int x) {
		BigDecimal result = FACTOR_A.multiply(new BigDecimal(x * x)).add(FACTOR_B.multiply(new BigDecimal(x)))
				.add(FACTOR_C);
		return result;
	}

	public static int getGold(int score) {
		int giveGold = score;
		if (score > 1500) {
			return (int) Math.round(score * 0.4);
		}
		if (score > 200) {
			giveGold = (int) MathUtils.clamp(Math.round(parabola(score).doubleValue() * score),
					(int) Math.round(score * 0.4), score);
		}
		return giveGold;
	}

}
