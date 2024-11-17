package org.example;
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;


public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage:");
            System.out.println("  -index -directory <path to docs> ");
            System.out.println("  -search -query <keyword> ");
            return;
        }

        String index_path = "index/";
        try {
            if (args[0].equals("-index") && args[1].equals("-directory")) {
                String documents_path = args[2];
                System.out.println("Indexing documents from: " + documents_path);

                Indexer indexer = new Indexer(index_path);
                indexer.set_index_directory(documents_path);
                System.out.println("Indexing has been completed.");

            } else if (args[0].equals("-search") && args[1].equals("-query")) {
                String query = args[2];
                Searcher searcher = new Searcher(index_path);
                searcher.search(query);
            } else {
                System.out.println("Invalid arguments.");
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}