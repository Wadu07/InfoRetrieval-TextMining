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

public class Searcher {
    private final Directory index_directory;
    private final RomanianAnalyzer analyzer;
    private static final String stopwords_path = "./stopwords.txt";

    public Searcher(String indexDir) throws IOException {
        index_directory = FSDirectory.open(Paths.get(indexDir));
        analyzer = get_romanian_analyzer_with_stopwords();
    }

    //load RomanianAnalyzer with custom stopwords
    private RomanianAnalyzer get_romanian_analyzer_with_stopwords() throws IOException {
        List<String> stopwords_list = Files.readAllLines(Paths.get(stopwords_path));
        CharArraySet stopwords_set = new CharArraySet(stopwords_list, true);
        return new RomanianAnalyzer(stopwords_set);
    }

    //remove diacritics
    public static String remove_diacritics(String text) {
        if (text == null)
            return null;
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
    public void search(String queryStr) throws IOException, ParseException {
        try (DirectoryReader directory_reader = DirectoryReader.open(index_directory)) {
            IndexSearcher index_searcher = new IndexSearcher(directory_reader);

            //normalize the query string to remove diacritics
            String normalized_query = remove_diacritics(queryStr);

            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(normalized_query);
            TopDocs results = index_searcher.search(query, 5);

            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = index_searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("filename"));
            }
        }
    }
}
