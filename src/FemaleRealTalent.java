import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Matcher;

/**
 * 三次元の女性タレントを表すクラス。誕生年がある
 */
public class FemaleRealTalent extends FemaleTalent {
	/**
	 * 誕生年
	 */
	private int birthYear;

	/**
	 * @param name            名前
	 * @param birthYear       誕生年
	 * @param birthMonth      誕生月
	 * @param birthDayOfMonth 誕生日
	 * @param height          身長(cm)
	 * @param weight          体重(kg)
	 * @param bustSize        バストサイズ(cm)
	 * @param waistSize       ウエストサイズ(cm)
	 * @param hipSize         ヒップサイズ(cm)
	 * @param cupSize         カップサイズ(A,B,C...)
	 */
	public FemaleRealTalent(String name, int birthYear, int birthMonth, int birthDayOfMonth, double height, double weight, double bustSize, double waistSize, double hipSize, String cupSize) {
		super(name, birthMonth, birthDayOfMonth, height, weight, bustSize, waistSize, hipSize, cupSize);
		this.birthYear = birthYear;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	/**
	 * 誕生日から年齢を計算する。
	 *
	 * @return 年齢
	 */
	@Override
	public int getAge() {
		LocalDate birthDate = LocalDate.of(birthYear, getBirthMonth(), getBirthDayOfMonth());
		LocalDate currentDate = LocalDate.now();
		return Period.between(birthDate, currentDate).getYears();
	}

	/**
	 * 年齢から誕生念を逆算し設定する。
	 *
	 * @param age 年齢
	 */
	@Override
	public void setAge(int age) {
		LocalDate currentDate = LocalDate.now();
		LocalDate birthDate = LocalDate.of(currentDate.getYear(), getBirthMonth(), getBirthDayOfMonth());
		int birthYear = currentDate.isBefore(birthDate) ? getBirthYear() - age - 1 : getBirthYear() - age;
		setBirthYear(birthYear);
	}

	/**
	 * @return 文字列にフォーマットされた詳細情報
	 */
	@Override
	public String toDetailString() {
		return String.format("""
				%s
				身長: %.1fcm
				体重: %.1fkg
				生年月日: %d年%d月%d日(%d歳)
				スリーサイズ: %.1fcm - %.1fcm - %.1fcm
				カップサイズ: %s
				""", getName(), getHeight(), getWeight(), getBirthYear(), getBirthMonth(), getBirthDayOfMonth(), getAge(), getBustSize(), getWaistSize(), getHipSize(), getCupSize());
	}

	/**
	 * 正規表現オブジェクトからインスタンスを生成
	 *
	 * @param matcher 正規表現オブジェクト
	 * @return インスタンス
	 */
	public static FemaleRealTalent parse(Matcher matcher) {
		FemaleTalent t = FemaleTalent.parse(matcher);
		int birthYear = Integer.parseInt(matcher.group("birthYear"));
		return new FemaleRealTalent(t.getName(), birthYear, t.getBirthMonth(), t.getBirthDayOfMonth(), t.getHeight(), t.getWeight(), t
				.getBustSize(), t.getWaistSize(), t.getHipSize(), t.getCupSize());
	}
}
