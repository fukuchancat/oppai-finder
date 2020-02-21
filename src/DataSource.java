import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 女性タレントの一覧を表すクラス
 */
public class DataSource {
	/**
	 * 女性タレントのリスト
	 */
	private List<FemaleTalent> talents;

	/**
	 * @param talents 女性タレントのリスト
	 */
	public DataSource(List<FemaleTalent> talents) {
		this.talents = talents;
	}

	public List<FemaleTalent> getTalents() {
		return talents;
	}

	public void setTalents(List<FemaleTalent> talents) {
		this.talents = talents;
	}

	/**
	 * 女性タレントの一部を適当に抽出する。数の少ない2次元タレントは必ず含め、またできるだけ各カップごとの3次元タレントの数は均等になるようにする
	 *
	 * @param limit  各カップごとに抽出する3次元タレントの数
	 * @param random シャッフル用のRandomオブジェクト
	 * @return 抽出された女性タレントのList
	 */
	public List<FemaleTalent> sampling(int limit, Random random) {
		Set<FemaleTalent> sampledTalents = new LinkedHashSet<>();

		// アニメタレントとその声優はあらかじめ全て含める
		talents.stream().filter(t -> t instanceof FemaleAnimeTalent).map(t -> (FemaleAnimeTalent) t).forEach(t -> {
			sampledTalents.add(t.getVoiceBy());
			sampledTalents.add(t);
		});

		// カップサイズごとにタレントをグルーピングする
		Map<String, List<FemaleTalent>> map = talents.stream().collect(Collectors.groupingBy(FemaleTalent::getCupSize));

		// 各カップサイズごとに5人までリストに追加する
		map.values().forEach(value -> {
			Collections.shuffle(value, random);
			value.stream().limit(limit).filter(t -> !sampledTalents.contains(t)).forEach(sampledTalents::add);
		});

		return new ArrayList<>(sampledTalents);
	}

	/**
	 * CSVファイルに書き込む
	 *
	 * @param path 書き込むファイルのパス
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public void writeCSV(Path path) throws IllegalAccessException, IntrospectionException, InvocationTargetException, IOException {
		// Writerを作る
		PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path));

		// ヘッダ行を生成
		Set<String> headers = new LinkedHashSet<>();
		for (FemaleTalent talent : talents)
			headers.addAll(talent.fieldMap().keySet());

		// ヘッダ行を書き込む
		writer.println(String.join(",", headers));

		// 2行目以降を書き込む
		for (FemaleTalent talent : talents) {
			Map<String, String> fields = talent.fieldMap();
			writer.println(headers.stream().map(h -> fields.getOrDefault(h, "")).collect(Collectors.joining(",")));
		}

		// Writerを閉じる
		writer.close();
	}

	/**
	 * CSVファイルから読み込む
	 *
	 * @param path 読み込むファイルのパス
	 * @return DataSource
	 * @throws IOException
	 */
	public static DataSource readCSV(Path path) throws IOException {
		// ファイルを全行読み込む
		List<String> lines = Files.readAllLines(path);

		// 1行目から、読み込み用の正規表現を生成
		String regexp = lines.remove(0).replaceAll("([^,]+)", "(?<$1>[^,]*)");
		Pattern pattern = Pattern.compile(regexp);

		// 2行目以降の各行を正規表現にかけ、女性タレントのListを作る
		List<FemaleTalent> talents = new ArrayList<>();
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find()) {
				continue;
			}
			if (matcher.group("class").equals("class FemaleRealTalent")) {
				// 3次元女性タレントの場合
				talents.add(FemaleRealTalent.parse(matcher));
			} else if (matcher.group("class").equals("class FemaleAnimeTalent")) {
				// 2次元女性タレントの場合
				// 中の人を取得
				Optional<FemaleTalent> optional = talents.stream()
						.filter(t -> t.getName().equals(matcher.group("voiceBy")))
						.findFirst();
				// 中の人が取得出来たらリストに追加
				optional.ifPresent(talent -> talents.add(FemaleAnimeTalent.parse(matcher, (FemaleRealTalent) talent)));
			}
		}
		return new DataSource(talents);
	}

	/**
	 * コピペ用。女性タレントを読み込みランダムに抽出してから、その一覧を表やJavaのコードで出力する
	 *
	 * @param args
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws IOException, IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
		// CSVから女性タレントを全て読み込み、その一部をランダムに抽出する
		DataSource source = DataSource.readCSV(Paths.get("./resources/talents.csv"));
		List<FemaleTalent> talents = source.sampling(3, new Random(123));

		/*
		 * タレントの一覧をMD記法の表で出力する
		 */
		// 表のヘッダを生成
		Set<String> headers = new TreeSet<>();
		for (FemaleTalent talent : talents)
			headers.addAll(talent.fieldMap().keySet());

		// ヘッダ行と2行目を出力
		System.out.println("|" + String.join("|", headers) + "|");
		System.out.println("|" + IntStream.range(0, headers.size())
				.mapToObj(i -> "---")
				.collect(Collectors.joining("|")) + "|");

		// 以降の行を出力
		for (FemaleTalent talent : talents) {
			Map<String, String> fields = talent.fieldMap();
			System.out.println("|" + headers.stream()
					.map(h -> fields.getOrDefault(h, ""))
					.collect(Collectors.joining("|")) + "|");
		}
		System.out.println();

		/*
		タレントの一覧をリストに収納するときのJavaのコードを出力する
		 */
		// パラメータの名前を取得できるかチェック
		if (!talents.get(0).getClass().getConstructors()[0].getParameters()[0].isNamePresent())
			return;

		// リスト初期化のコードを出力
		System.out.println("List<FemaleTalent> talents = new ArrayList<>();");

		// リスト追加のコードを出力
		for (FemaleTalent talent : talents) {
			// コンストラクタに渡す引数を取得
			Map<String, String> fields = talent.fieldMap();
			List<String> arguments = new ArrayList<>();
			for (Parameter parameter : talent.getClass().getConstructors()[0].getParameters()) {
				if (parameter.getName().equals("voiceBy"))
					arguments.add("(FemaleRealTalent) talents.get(talents.size() - 1)");
				else if (parameter.getType().equals(String.class))
					arguments.add("\"" + fields.get(parameter.getName()) + "\"");
				else
					arguments.add(fields.get(parameter.getName()));
			}

			System.out.println("talents.add(new " + talent.getClass()
					.getName() + "(" + String.join(", ", arguments) + "));");
		}
	}
}
