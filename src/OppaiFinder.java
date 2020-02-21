import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * 実行用クラス
 */
public class OppaiFinder {
	/**
	 * 好きなカップサイズの入力を受け付け、該当する女性タレントの一覧を出力する。
	 * その中でも気になるタレントの番号の入力を受け付け、その詳細情報を出力する。
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Scannerを初期化
		Scanner scanner = new Scanner(System.in);

		// 好みのカップサイズの入力を受け付ける
		System.out.println("好みのカップサイズ(A,B,C…)を入力してください");
		String inputCupSize = scanner.nextLine();
		System.out.println(inputCupSize + "カップの女性タレント一覧:");

		// 女性タレントの一覧を読み込む
		DataSource source = DataSource.readCSV(Paths.get("./resources/talents.csv"));
		List<FemaleTalent> talents = source.getTalents();

		// 各女性タレントについてループを回す
		for (int i = 0; i < talents.size(); i++) {
			FemaleTalent talent = talents.get(i);

			// 女性タレントのカップサイズが入力のカップサイズと同じ場合
			String cupSize = talent.getCupSize();
			if (cupSize.equals(inputCupSize)) {
				// 番号と名前をコンソールに出力する
				System.out.println("[" + i + "] " + talent.getName());
			}
		}
		System.out.println();

		// 気になる女性タレントの番号の入力を受け付ける
		System.out.println("気になる女性タレントの番号(0,1,2…)を入力してください");
		int inputIndex = scanner.nextInt();

		// 番号に当てはまる女性タレントの詳細情報を出力する
		FemaleTalent talent = talents.get(inputIndex);
		System.out.println(talent.toDetailString());

		// Scannerを閉じる
		scanner.close();
	}
}
