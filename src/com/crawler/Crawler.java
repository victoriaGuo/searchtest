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

//��ȡʱ������Ӱ����д���ļ�movies.txt
public class Crawler {
	// ��ȡ��ҳ
	public String GetHTML(String url) throws IOException {
		String html = null;
		// HttpClient
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// HttpGet
		HttpGet httpGet = new HttpGet(url);
		try {
			// ִ��get����
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			// �õ���Ӧ�����״̬����
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				// ��ȡ��Ӧʵ��
				HttpEntity entity = httpResponse.getEntity();
				// �ж�ʵ���Ƿ�Ϊ��
				if (entity != null) {
					html = EntityUtils.toString(entity);// ���htmlԴ����
				}
			}
		} catch (Exception e) {
			System.out.println("���ʡ�" + url + "�������쳣!");
			// e.printStackTrace();
		} finally {
			httpClient.close();
		}

		return html;
	}

	// ��Ϣ��
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

	// ������ҳ
	public ArrayList<Info> FarseContent(String html) {
		// ÿ��ҳ����10����Ϣ�������ArrayList��
		ArrayList<Info> infos = new ArrayList<Info>();
		infos.clear();
		// �жϴ������html�Ƿ�Ϊ��
		try {
			if (!html.isEmpty()) {

				// ����html
				Document doc = Jsoup.parse(html);
				Element father = doc.getElementById("asyncRatingRegion");
				Elements sons = father.getElementsByTag("li");
				for (Element son : sons) {
					Element div = son.select("[class=mov_pic]").get(0);

					// ��Ӱ����
					String name = div.getElementsByTag("a").attr("title");
					// ����
					String link = div.getElementsByTag("a").attr("href");
					// ��Ӱ����
					double point = Double.parseDouble(son
							.select("[class=point]").get(0).text());
					// ��������
					String comStr = son.select("[class=mov_point]").get(0)
							.getElementsByTag("p").text();
					int comment = Integer.parseInt(comStr.substring(0,
							comStr.length() - 3));
					// ��ӵ�ArrayList��;
					Info info = new Info(name, link, point, comment);
					infos.add(info);

				}
			}
		} catch (Exception e) {
		}
		return infos;
	}

	// ��ȡ��ҳ
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
			// ��ȡҳ��
			String html = GetHTML(url);
			// ����ҳ������

			ArrayList<Info> infos = FarseContent(html);
			// д���ļ�
			if (!infos.isEmpty()) {
				for (int j = 0; j < infos.size(); j++) {
					Info info = infos.get(j);
					// ׷�����ݵ��ļ�ĩβ
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
		// ץȡ��ҳ
		try {
			Crawler exp = new Crawler();
			exp.Crawl();
		} catch (Exception ex) {
			ex.getStackTrace();
		}
	}
}
