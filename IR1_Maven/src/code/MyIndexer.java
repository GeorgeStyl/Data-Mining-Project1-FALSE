import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class is the parent class of the AlbumsIndexer and the SongLyricsIndexer classes. <br>
 * It contains the common code of the two classes. <br>
 * It is abstract, because the behavior of the two classes is different, and we only need to
 * sum the common behavior of the two subclasses together<br>
 * @see AlbumsIndexer
 * @see SongLyricsIndexer
 */

/**
 * Constructor of the MyIndexer class. <br>
 * It creates the IndexWriter object, for the creation of the index. <br>
 * It uses the StandardAnalyzer for the tokenization of the text data, <br>
 * without ignoring the stopwords. <br>
 */
public abstract class MyIndexer {
    protected IndexWriter writer;

    public MyIndexer(String indexDir){
        // The indexDir parameter specifies the location where the index files will be stored.
        Directory indexDirectory = null;
        try {
            indexDirectory = FSDirectory.open(Paths.get(indexDir));
        } catch (IOException e) {
            System.out.println("Error creating the directory of the index");
            System.exit(1);
        }
        // Create the analyzer for the tokenization of the text data
        // There is no need to ignore the stopwords(neither on lyrics nor on song-album titles, not artist names),
        // because our Index will become ~ 3MB larger, and the index-build
        // time will be ~ 0.6 seconds faster (very small difference)
        Analyzer analyzer = new StandardAnalyzer();
        // Create the IndexWriterConfig object
        IndexWriterConfig iwconfig = new IndexWriterConfig(analyzer);
        // Create the IndexWriter object, for the creation of the index
        writer = null;
        try {
            writer = new IndexWriter(indexDirectory, iwconfig);
        } catch (IOException e) {
            System.out.println("Error creating the IndexWriter object");
            System.exit(1);
        }
    }

    /**
     * This method closes the IndexWriter object, for the creation of the index. <br>
     * It writes the directory of the index to the disk. <br>
     * @throws IOException
     */
    public void close() throws IOException {
        // Close the IndexWriter object
        writer.close();
    }

}
