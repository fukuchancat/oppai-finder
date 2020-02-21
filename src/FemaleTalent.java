import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 女性タレントを表すクラス
 */
public class FemaleTalent {
	/**
	 * 名前
	 */
	private String name;

	/**
	 * 誕生月
	 */
	private int birthMonth;

	/**
	 * 誕生日
	 */
	private int birthDayOfMonth;

	/**
	 * 身長(cm)
	 */
	private double height;

	/**
	 * 体重(kg)
	 */
	private double weight;

	/**
	 * バストサイズ(cm)
	 */
	private double bustSize;

	/**
	 * ウエストサイズ(cm)
	 */
	private double waistSize;

	/**
	 * ヒップサイズ(cm)
	 */
	private double hipSize;

	/**
	 * カップサイズ(A,B,C...)
	 */
	private String cupSize;

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
	 */
	public FemaleTalent(String name, int birthMonth, int birthDayOfMonth, double height, double weight, double bustSize, double waistSize, double hipSize, String cupSize) {
		this.name = name;
		this.birthMonth = birthMonth;
		this.birthDayOfMonth = birthDayOfMonth;
		this.height = height;
		this.weight = weight;
		this.bustSize = bustSize;
		this.waistSize = waistSize;
		this.hipSize = hipSize;
		this.cupSize = cupSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(int birthMonth) {
		this.birthMonth = birthMonth;
	}

	public int getBirthDayOfMonth() {
		return birthDayOfMonth;
	}

	public void setBirthDayOfMonth(int birthDayOfMonth) {
		this.birthDayOfMonth = birthDayOfMonth;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getBustSize() {
		return bustSize;
	}

	public void setBustSize(double bustSize) {
		this.bustSize = bustSize;
	}

	public double getWaistSize() {
		return waistSize;
	}

	public void setWaistSize(double waistSize) {
		this.waistSize = waistSize;
	}

	public double getHipSize() {
		return hipSize;
	}

	public void setHipSize(double hipSize) {
		this.hipSize = hipSize;
	}

	public String getCupSize() {
		return cupSize;
	}

	public void setCupSize(String cupSize) {
		this.cupSize = cupSize;
	}

	public int getAge() {
		throw new UnsupportedOperationException();
	}

	public void setAge(int age) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return 文字列にフォーマットされた詳細情報
	 */
	public String toDetailString() {
		return String.format("""
				%s
				身長: %.1fcm
				体重: %.1fkg
				生年月日: %d月%d日
				スリーサイズ: %.1fcm - %.1fcm - %.1fcm
				カップサイズ: %s
				""", getName(), getHeight(), getWeight(), getBirthMonth(), getBirthDayOfMonth(), getBustSize(), getWaistSize(), getHipSize(), getCupSize());
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * 各フィールドの名前とその値のMapを生成する
	 *
	 * @return 各フィールドの名前とその値のMap
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public Map<String, String> fieldMap() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		Map<String, String> map = new LinkedHashMap<>();
		for (PropertyDescriptor descriptor : Introspector.getBeanInfo(getClass()).getPropertyDescriptors()) {
			Method method = descriptor.getReadMethod();
			String key = descriptor.getName();
			String value = method.invoke(this).toString();
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 正規表現オブジェクトからインスタンスを生成
	 *
	 * @param matcher 正規表現オブジェクト
	 * @return インスタンス
	 */
	public static FemaleTalent parse(Matcher matcher) {
		// 正規表現オブジェクトからプロフィールを抽出
		String name = matcher.group("name");
		int birthMonth = Integer.parseInt(matcher.group("birthMonth"));
		int birthDayOfMonth = Integer.parseInt(matcher.group("birthDayOfMonth"));
		double height = Double.parseDouble(matcher.group("height"));
		double weight = Double.parseDouble(matcher.group("weight"));
		double bustSize = Double.parseDouble(matcher.group("bustSize"));
		double waistSize = Double.parseDouble(matcher.group("waistSize"));
		double hipSize = Double.parseDouble(matcher.group("hipSize"));
		String cupSize = Normalizer.normalize(matcher.group("cupSize"), Normalizer.Form.NFKC); // 全角アルファベットは半角に変換

		// インスタンスを作成
		return new FemaleTalent(name, birthMonth, birthDayOfMonth, height, weight, bustSize, waistSize, hipSize, cupSize);
	}
}
