import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * スクレイピング用クラス
 */
public class Scraper {
	/**
	 * 参照するURL
	 */
	private String address;

	/**
	 * 正規表現
	 */
	private String regexp;

	/**
	 * @param address 参照するURL
	 * @param regexp  正規表現
	 */
	public Scraper(String address, String regexp) {
		this.address = address;
		this.regexp = regexp;
	}

	/**
	 * 参照先のWebページを取得し、正規表現にかける
	 *
	 * @return 正規表現オブジェクト
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 */
	public Matcher matcher() throws IOException, InterruptedException, URISyntaxException {
		// HTTPクライアントを作る
		HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

		// GETリクエストしてレスポンス本文をテキストで取得
		HttpRequest request = HttpRequest.newBuilder(new URI(address)).GET().build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String body = response.body();

		// GETのたびに1秒待つ
		Thread.sleep(100);

		// 正規表現にかける
		Pattern pattern = Pattern.compile(regexp);
		return pattern.matcher(body);
	}

	/**
	 * Wikipediaなどのサイトをスクレイピングし、女性タレントのプロフィールを収集、CSVファイルに出力する
	 *
	 * @param args
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 */
	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, IllegalAccessException, IntrospectionException, InvocationTargetException {
		List<FemaleTalent> talents = new ArrayList<>();

		/*
		Wikipediaを検索
		 */
		{
			// 生年月日、身長、体重、スリーサイズ、カップサイズの記載がある記事を検索
			String searchAddress = "https://ja.wikipedia.org/w/index.php?limit=5000&search=%E8%BA%AB%E9%95%B7+%E4%BD%93%E9%87%8D+%E3%82%AB%E3%83%83%E3%83%97%E3%82%B5%E3%82%A4%E3%82%BA+%E7%94%9F%E5%B9%B4%E6%9C%88%E6%97%A5&ns0=1";
			String searchRegexp = "mw-search-result-heading.*?href=\"(?<address>\\/wiki\\/[\\w%]+?)\"";
			Scraper searchScraper = new Scraper(searchAddress, searchRegexp);

			for (Matcher searchMatcher = searchScraper.matcher(); searchMatcher.find(); ) {
				// 検索にかかった記事を探索
				String address = "https://ja.wikipedia.org/" + searchMatcher.group("address");
				String regexp = ">(?<name>.*?)</h1>[\\s\\S]*生年月日[\\s\\S]*?(?<birthYear>\\d{4})年.*?(?<birthMonth>\\d{1,2})月(?<birthDayOfMonth>\\d{1,2})日[\\s\\S]*?[^%](?<height>\\d{3}(?:\\.\\d+)?).*?cm.*?[^%](?<weight>\\d{2,3}(?:\\.\\d+)?).*?kg[\\s\\S]*?(?<bustSize>\\d{2,3}) - (?<waistSize>\\d{2,3}) - (?<hipSize>\\d{2,3}) cm[\\s\\S]*?カップサイズ[\\s\\S]*?(?<cupSize>[A-ZＡ-Ｚ]+)";
				Scraper scraper = new Scraper(address, regexp);
				for (Matcher matcher = scraper.matcher(); matcher.find(); ) {
					// 記事からプロフィールを抽出
					FemaleRealTalent talent = FemaleRealTalent.parse(matcher);
					System.out.println(talent);

					// リストに追加
					talents.add(talent);
				}
			}
		}

		/*
		ラブライブの声優を追加
		 */
		{
			String address = "https://xn--zck3adi4kpbxc7d2131c340f.jp/%E6%AD%8C%E6%89%8B/%CE%BCs%E3%83%9F%E3%83%A5%E3%83%BC%E3%82%BA%E3%83%A1%E3%83%B3%E3%83%90%E3%83%BC%E7%B4%B9%E4%BB%8B%E3%83%A9%E3%83%96%E3%83%A9%E3%82%A4%E3%83%96%E5%A3%B0%E5%84%AA%E3%81%AE%E5%B9%B4%E9%BD%A2/";
			String regexp = "(?<name>.+?)\\(.+\\)さん[\\s\\S]*?(?<birthYear>\\d+)年(?<birthMonth>\\d+)月(?<birthDayOfMonth>\\d+)日[\\s\\S]*?(?<height>\\d+)cm[\\s\\S]*?(?<weight>\\d+)kg[\\s\\S]*?B(?<bustSize>\\d+)：W(?<waistSize>\\d+)：H(?<hipSize>\\d+)[\\s\\S]*?(?<cupSize>[A-Z]+)";
			Scraper scraper = new Scraper(address, regexp);
			for (Matcher matcher = scraper.matcher(); matcher.find(); ) {
				// 記事からプロフィールを抽出
				FemaleRealTalent talent = FemaleRealTalent.parse(matcher);
				System.out.println(talent);

				// リストに追加
				talents.add(talent);
			}
		}

		/*
		ラブライブのキャラクターを追加
		 */
		{
			String address = "http://lcsious.com/other/lovelive_cupsize.php";
			String regexp = ">(?<name>[^※]\\S+ \\S+) （\\S+ \\S+）<[\\s\\S]*?>(?<birthMonth>\\d+)月(?<birthDayOfMonth>\\d+)日<[\\s\\S]*?>(?<age>\\d+)歳<[\\s\\S]*?>(?<height>\\d+)cm<[\\s\\S]*?>(?<bustSize>\\d+)<[\\s\\S]*?>(?<waistSize>\\d+)<[\\s\\S]*?>(?<hipSize>\\d+)<[\\s\\S]*?>(?<cupSize>[A-Z]+)<[\\s\\S]*?>(?<weight>\\d+\\.\\d+) kg<[\\s\\S]*?>(?<voiceBy>.*)?<\\/td>\\n<\\/tr>";
			Scraper scraper = new Scraper(address, regexp);
			for (Matcher matcher = scraper.matcher(); matcher.find(); ) {
				// 名前からCVを見つける
				Optional<FemaleTalent> optional = talents.stream()
						.filter(t -> t.getName().equals(matcher.group("voiceBy")))
						.findFirst();

				// 見つかったら
				if (optional.isPresent()) {
					// 記事からプロフィールを抽出
					FemaleRealTalent voiceBy = (FemaleRealTalent) optional.get();
					FemaleAnimeTalent talent = FemaleAnimeTalent.parse(matcher, voiceBy);

					// 名前から空白を削除
					talent.setName(talent.getName().replace(" ", ""));
					System.out.println(talent);

					// リストに追加
					talents.add(talent);
				}
			}
		}

		// リストをCSVファイルに書き出す
		Path path = Paths.get("./resources/talents.csv");
		DataSource source = new DataSource(talents);
		source.writeCSV(path);
	}
}
