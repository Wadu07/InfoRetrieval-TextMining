package org.example;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;

import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage:");
            System.out.println("  -index -directory <path to docs> -stopwords <path to stopwords>");
            System.out.println("  -search -query <keyword> -stopwords <path to stopwords>");
            return;
        }

        String index_path = "D:\\Radu\\fac\\master\\anul2\\TextMining\\proiect\\index";
        try {
            if (args[0].equals("-index") && args[1].equals("-directory")) {
                String documents_path = args[2];
                String stopwords_path = args[4];
                System.out.println("Indexing documents from: " + documents_path);

                Indexer indexer = new Indexer(index_path, stopwords_path);
                indexer.set_index_directory(documents_path);
                System.out.println("Indexing completed successfully.");

            } else if (args[0].equals("-search") && args[1].equals("-query")) {
                String query = args[2];
                String stopwords_path = args[4];
                System.out.println("Searching for: " + query);

                Searcher searcher = new Searcher(index_path, stopwords_path);
                searcher.search(query);
            } else {
                System.out.println("Invalid arguments.");
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}