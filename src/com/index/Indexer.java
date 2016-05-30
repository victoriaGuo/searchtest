package com.index;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Indexer {

	public static void main(String[] args) throws Exception {

		String indexDir = "E:\\movies/index";
		String dataDir = "E:\\movies/contents";

		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();

		System.out.println("Indexing " + numIndexed + " rows took "
				+ (end - start) + " milliseconds");
	}

	private IndexWriter writer;

	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));

		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_32,
				new StandardAnalyzer(Version.LUCENE_32));
		writer = new IndexWriter(dir, conf);
	}

	public void close() throws IOException {
		writer.close();
	}

	public int index(String dataDir, FileFilter filter) throws Exception {

		File[] files = new File(dataDir).listFiles();

		for (File f : files) {
			if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()
					&& (filter == null || filter.accept(f))) {
				indexFile(f);
			}
		}

		return writer.numDocs();
	}

	public static class TextFilesFilter implements FileFilter {
		public boolean accept(File path) {
			return path.getName().toLowerCase().endsWith(".txt");
		}
	}

	protected List getDocument(File f) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(f)));
		String line = "";
		String result = "";
		List docs = new ArrayList();
		while ((line = reader.readLine()) != null) {
			String[] infos = line.split(";");
			String title = "";
			String link = "";
			String comments = "";
			String score = "";
			for (String info : infos) {
				if (info.contains("title")) {
					title = info
							.substring(info.indexOf(":") + 1, info.length());

				}
				if (info.contains("link")) {
					link = info.substring(info.indexOf(":") + 1, info.length());

				}
				if (info.contains("comments")) {
					comments = info.substring(info.indexOf(":") + 1,
							info.length());
				}
				if (info.contains("score")) {
					score = info
							.substring(info.indexOf(":") + 1, info.length());

				}
			}
			Document doc = new Document();// 向lucene中添加document

			doc.add(new Field("title", title, Field.Store.YES,
					Field.Index.ANALYZED));
			doc.add(new Field("link", link, Field.Store.YES,// document都是由field组成的
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("comments", comments, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("score", score, Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			docs.add(doc);
		}
		return docs;
	}

	private void indexFile(File f) throws Exception {
		System.out.println("Indexing " + f.getCanonicalPath());
		List docs;
		try {
			// 提取信息，放入document中
			docs = getDocument(f);
			for (Object o : docs) {
				// 将document放入到indexwriter中
				writer.addDocument((Document) o);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
