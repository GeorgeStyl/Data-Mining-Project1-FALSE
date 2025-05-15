import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class is responsible for the searching of the index.
 * It contains methods for the searching of the index, and for the getting of the documents
 *
 *
 */
public class Searcher {
    private IndexSearcher indexSearcher;
    private Directory indexDirectory;
    private IndexReader reader;
    private Query query;
    private QueryParser queryParser;

    /**
     * Constructor of the class
     * It creates the directory of the index, the IndexReader object, and the IndexSearcher object
     *
     * @param indexDir The directory of the index
     */
    public Searcher(String indexDir){
        // Create the directory of the index
        indexDirectory = null;
        try {
            indexDirectory = FSDirectory.open(Paths.get(indexDir));
        } catch (IOException e) {
            System.out.println("Error creating the directory of the index");
            System.exit(1);
        }
        // Create a IndexReader object, for the reading of the index
        reader = null;
        try {
            reader = DirectoryReader.open(indexDirectory);
        } catch (IOException e) {
            System.out.println("Error creating the IndexReader object");
            System.exit(1);
        }
        // Create the IndexSearcher object, for the searching of the index
        indexSearcher = new IndexSearcher(reader);
    }

    /**
     * This method searches the index, based on the searchQuery, the field, and the topK parameter
     * @param searchQuery The query for the searching of the index
     * @param field The field of the index, on which the searching will be based on (SONGS_SINGER_NAME etc.)
     * @param topK The number of the top documents that will be returned
     * @return The TopDocs object, which contains the topK documents
     * @throws IOException
     * @throws ParseException
     */
    public TopDocs search(String searchQuery, String field, int topK) throws IOException,
            ParseException {
        // Create the analyzer for the tokenization of the text data
        // No need to ignore stopwords, because if we ignore them
        // the index will become only ~ 3MB smaller
        StandardAnalyzer analyzer = new StandardAnalyzer();
        // Create the QueryParser object, for the parsing of the query, with the StandardAnalyzer
        // and the SONGS_SINGER_NAME field for the searching of the index, based on the singer name
        queryParser = new QueryParser(field, analyzer);
        query = null;
        try {
            query = queryParser.parse(searchQuery);
            System.out.println(query.toString());
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            System.out.println("Error creating the query");
            System.exit(1);
        }
        // Search the index
        TopDocs hits = null;
        try {
            hits = indexSearcher.search(query, topK);
        } catch (IOException e) {
            System.out.println("Error searching the index");
            System.exit(1);
        }
        // Return the TopDocs object
        return hits;
    }

    /**
     * This method returns the document, based on the ScoreDoc object
     * @param scoreDoc The ScoreDoc object, which contains the document id
     * @return The Document object, which contains the document
     */
    public Document getDocument(ScoreDoc scoreDoc) {
        // Get the document from the index
        Document doc = null;
        try {
            doc = indexSearcher.doc(scoreDoc.doc);
        } catch (IOException e) {
            System.out.println("Error getting the document");
            System.exit(1);
        }
        // Return the Document object
        return doc;
    }

    /**
     * This method returns the total number of documents of the index
     * @return The total number of documents of the index
     */
    public int getTotalNumberOfDocuments() {
        // Return the total number of documents of the index
        return reader.numDocs();
    }
}
