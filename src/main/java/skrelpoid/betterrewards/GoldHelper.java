package skrelpoid.betterrewards;

import java.math.BigDecimal;

import com.badlogic.gdx.math.MathUtils;

public class GoldHelper {

	public static final BigDecimal FACTOR_A = new BigDecimal("0.00000014285");
	public static final BigDecimal FACTOR_B = new BigDecimal("-0.00065714285");
	public static final BigDecimal FACTOR_C = new BigDecimal("1.064285714");
	
	public static final double K_OVER_LOG = 1.25834776329D;

	public static BigDecimal parabola(int x) {
		BigDecimal result = FACTOR_A.multiply(new BigDecimal(x * x)).add(FACTOR_B.multiply(new BigDecimal(x)))
				.add(FACTOR_C);
		return result;
	}

	public static int getGold(int score) {
		int giveGold = score;
		if (score > 1500) {
			return (int) Math.round(score * K_OVER_LOG / Math.log10(score));
		}
		if (score > 200) {
			giveGold = (int) MathUtils.clamp(Math.round(parabola(score).doubleValue() * score),
					(int) Math.round(score * 0.4), score);
		}
		return giveGold;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 8000; i++) {
			System.out.println(i + "\t" + getGold(i));
		}
	}

}
