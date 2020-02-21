import java.util.regex.Matcher;

/**
 * 二次元の女性タレントを表すクラス。誕生年は無いが年齢はあり、中の人がいる
 */
public class FemaleAnimeTalent extends FemaleTalent {
	/**
	 * 年齢
	 */
	private int age;

	/**
	 * 中の人
	 */
	private FemaleRealTalent voiceBy;

	/**
	 * @param name            名前
	 * @param birthMonth      誕生月
	 * @param birthDayOfMonth 誕生日
	 * @param height          身長(cm)
	 * @param weight          体重(kg)
	 * @param bustSize        バストサイズ(cm)
	 * @param waistSize       ウエストサイズ(cm)
	 * @param hipSize         ヒップサイズ(cm)
	 * @param cupSize         カップサイズ(A,B,C...)
	 * @param age             年齢
	 * @param voiceBy         中の人
	 */
	public FemaleAnimeTalent(String name, int birthMonth, int birthDayOfMonth, double height, double weight, double bustSize, double waistSize, double hipSize, String cupSize, int age, FemaleRealTalent voiceBy) {
		super(name, birthMonth, birthDayOfMonth, height, weight, bustSize, waistSize, hipSize, cupSize);
		this.age = age;
		this.voiceBy = voiceBy;
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public void setAge(int age) {
		this.age = age;
	}

	public FemaleRealTalent getVoiceBy() {
		return voiceBy;
	}

	public void setVoiceBy(FemaleRealTalent voiceBy) {
		this.voiceBy = voiceBy;
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
				生年月日: %d月%d日(%d歳)
				スリーサイズ: %.1fcm - %.1fcm - %.1fcm
				カップサイズ: %s
				中の人: %s
				""", getName(), getHeight(), getWeight(), getBirthMonth(), getBirthDayOfMonth(), getAge(), getBustSize(), getWaistSize(), getHipSize(), getCupSize(), getVoiceBy());
	}

	/**
	 * 正規表現オブジェクトからインスタンスを生成
	 *
	 * @param matcher 正規表現オブジェクト
	 * @return インスタンス
	 */
	public static FemaleAnimeTalent parse(Matcher matcher, FemaleRealTalent voiceBy) {
		FemaleTalent t = FemaleTalent.parse(matcher);
		int age = Integer.parseInt(matcher.group("age"));
		return new FemaleAnimeTalent(t.getName(), t.getBirthMonth(), t.getBirthDayOfMonth(), t.getHeight(), t.getWeight(), t
				.getBustSize(), t.getWaistSize(), t.getHipSize(), t.getCupSize(), age, voiceBy);
	}
}
