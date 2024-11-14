package org.example;

import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class Searcher {
    private final Directory index_directory;
    private final RomanianAnalyzer analyzer;

    public Searcher(String indexDir, String stopwords_path) throws IOException {
        index_directory = FSDirectory.open(Paths.get(indexDir));
        analyzer = get_romanian_analyzer_with_stopwords(stopwords_path);
    }

    // Method to load RomanianAnalyzer with custom stopwords
    private RomanianAnalyzer get_romanian_analyzer_with_stopwords(String stopwordsPath) throws IOException {
        List<String> stopwords_list = Files.readAllLines(Paths.get(stopwordsPath));
        CharArraySet stopwords_set = new CharArraySet(stopwords_list, true);
        return new RomanianAnalyzer(stopwords_set);
    }

    // Utility method to remove diacritics
    public static String remove_diacritics(String text) {
        if (text == null) return null;
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    public void search(String queryStr) throws IOException, ParseException {
        try (DirectoryReader directory_reader = DirectoryReader.open(index_directory)) {
            IndexSearcher index_searcher = new IndexSearcher(directory_reader);

            // Normalize the query string to remove diacritics
            String normalized_query = remove_diacritics(queryStr);

            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(normalized_query);
            TopDocs results = index_searcher.search(query, 10);

            System.out.println("Found " + results.totalHits + " hits.");
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = index_searcher.doc(scoreDoc.doc);
                System.out.println("File: " + doc.get("filename"));
            }
        }
    }
}
