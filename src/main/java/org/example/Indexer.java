package org.example;

import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import java.text.Normalizer;
import org.apache.lucene.analysis.CharArraySet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

public class Indexer {

    private static  Tika tika;
    private final Directory indexDirectory;
    private IndexWriter indexWriter;
    private static final String stopwords_path = "stopwords.txt";

    public Indexer(String indexDirPath) throws IOException {
        //crate the analyzer
        RomanianAnalyzer analyzer = get_romanian_analyzer_with_stopwords();

        //initialize the index directory
        indexDirectory = FSDirectory.open(Paths.get(indexDirPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(indexDirectory, config);
        tika = new Tika();
    }
    private RomanianAnalyzer get_romanian_analyzer_with_stopwords() throws IOException {
        List<String> stopwordsList = Files.readAllLines(Paths.get(stopwords_path));
        CharArraySet stopwordsSet = new CharArraySet(stopwordsList, true);
        return new RomanianAnalyzer(stopwordsSet);
    }

    public void set_index_directory(String documentsDirPath) throws IOException{

        File folder = new File(documentsDirPath);
        if (!folder.isDirectory()){
            throw new IllegalArgumentException("This path is not a directory: " + documentsDirPath);
        }
        for(File file : folder.listFiles()){
            if(file.isFile() && (file.getName().endsWith(".txt") || file.getName().endsWith(".pdf") || file.getName().endsWith(".docx") || file.getName().endsWith(".doc"))) {
                String content_document = extract_content(file);
                if (content_document != null) {
                    System.out.println("Indexing file: " + file.getName());
                    index_document(file.getName(), content_document);
                }
            }
        }
        indexWriter.close();
    }

    private void index_document(String fileName, String content) throws IOException{
        Document doc = new Document();
        doc.add(new TextField("filename", fileName, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        indexWriter.addDocument(doc);
    }

    private String extract_content(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return remove_diacritics(tika.parseToString(stream));
        } catch (IOException | TikaException e) {
            System.out.println("Error parsing the file: " + file.getName() + " - " + e.getMessage());
            return null;
        }
    }

    public static String remove_diacritics(String text) {
        if (text == null)
            return null;
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

}
