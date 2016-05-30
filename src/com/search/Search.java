package com.search;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Search {
	private Logger logger = Logger.getLogger(Search.class);

	public void search(String name) {
		try {
			// ��������ŵ�Ŀ¼
			Directory dic = FSDirectory.open(new File("E:\\movies\\index"));
			// ������������
			IndexSearcher search = new IndexSearcher(dic);
			// �������ķִ�
			Analyzer analyzer = new IKAnalyzer();
			// ��ʼ����
			QueryParser parser = new QueryParser(Version.LUCENE_32, "title",
					analyzer);
			Query query = parser.parse(name);
			TopDocs hits = search.search(query, 100);
			// ��ʾ�������
			logger.info("���ҵ�" + hits.totalHits + "����¼");
			logger.info("=====================================================");
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = search.doc(scoreDoc.doc);
				logger.info("���ƣ�" + doc.get("title"));
				logger.info("���ӣ�" + doc.get("link"));
				logger.info("����������" + doc.get("comments"));
				logger.info("���֣�" + doc.get("score"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Search search = new Search();
		search.search("��");

	}
}
