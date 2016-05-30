package com.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.midi.MidiDevice.Info;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//爬取时光网电影，并写入文件movies.txt
public class Crawler {
	// 获取网页
	public String GetHTML(String url) throws IOException {
		String html = null;
		// HttpClient
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// HttpGet
		HttpGet httpGet = new HttpGet(url);
		try {
			// 执行get请求
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			// 得到响应结果的状态代码
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = httpResponse.getEntity();
				// 判断实体是否为空
				if (entity != null) {
					html = EntityUtils.toString(entity);// 获得html源代码
				}
			}
		} catch (Exception e) {
			System.out.println("访问【" + url + "】出现异常!");
			// e.printStackTrace();
		} finally {
			httpClient.close();
		}

		return html;
	}

	// 信息类
	public class Info {
		private String name;
		private String link;
		private double point;
		private int comment;

		public Info(String name, String link, double point, int comment) {
			this.name = name;
			this.link = link;
			this.point = point;
			this.comment = comment;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public String getLink() {
			return this.link;
		}

		public void setLink(String link) {
			this.name = link;
		}

		public void setPoint(double point) {
			this.point = point;
		}

		public double getPoint() {
			return this.point;
		}

		public void setComment(int comment) {
			this.comment = comment;
		}

		public int getComment() {
			return comment;
		}
	};

	// 解析网页
	public ArrayList<Info> FarseContent(String html) {
		// 每个页面有10条信息，存放在ArrayList中
		ArrayList<Info> infos = new ArrayList<Info>();
		infos.clear();
		// 判断传入参赛html是否为空
		try {
			if (!html.isEmpty()) {

				// 解析html
				Document doc = Jsoup.parse(html);
				Element father = doc.getElementById("asyncRatingRegion");
				Elements sons = father.getElementsByTag("li");
				for (Element son : sons) {
					Element div = son.select("[class=mov_pic]").get(0);

					// 电影名称
					String name = div.getElementsByTag("a").attr("title");
					// 链接
					String link = div.getElementsByTag("a").attr("href");
					// 电影评分
					double point = Double.parseDouble(son
							.select("[class=point]").get(0).text());
					// 评论人数
					String comStr = son.select("[class=mov_point]").get(0)
							.getElementsByTag("p").text();
					int comment = Integer.parseInt(comStr.substring(0,
							comStr.length() - 3));
					// 添加到ArrayList中;
					Info info = new Info(name, link, point, comment);
					infos.add(info);

				}
			}
		} catch (Exception e) {
		}
		return infos;
	}

	// 爬取网页
	public void Crawl() throws IOException {

		String fileName = "E:\\movies/contents/movies.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
		String url = "";
		for (int i = 1; i <= 30; i++) {
			if (i <= 10) {
				if (i == 1) {
					url = "http://www.mtime.com/top/movie/top100_chinese/index.html";
				} else {
					url = "http://www.mtime.com/top/movie/top100_chinese/index-"
							+ i + ".html";
				}

			} else if (i <= 20) {
				if (i - 10 == 1) {
					url = "http://www.mtime.com/top/movie/top100_japan/index.html";
				} else {
					url = "http://www.mtime.com/top/movie/top100_japan/index-"
							+ (i - 10) + ".html";
				}
			} else {
				if (i - 20 == 1) {
					url = "http://www.mtime.com/top/movie/top100_south_korea/index.html";
				} else {
					url = "http://www.mtime.com/top/movie/top100_south_korea/index-"
							+ (i - 20) + ".html";
				}
			}
			// 获取页面
			String html = GetHTML(url);
			// 解析页面内容

			ArrayList<Info> infos = FarseContent(html);
			// 写入文件
			if (!infos.isEmpty()) {
				for (int j = 0; j < infos.size(); j++) {
					Info info = infos.get(j);
					// 追加内容到文件末尾
					bw.append("title:" + info.getName() + ";" + "link:"
							+ info.getLink() + ";" + "comments:"
							+ info.getComment() + ";" + "score:"
							+ info.getPoint());
					bw.newLine();
				}
			}
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		// 抓取网页
		try {
			Crawler exp = new Crawler();
			exp.Crawl();
		} catch (Exception ex) {
			ex.getStackTrace();
		}
	}
}
