
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class contains the code for the creation of the index of the albiums. <br>
 *
 */
public class AlbumsIndexer extends MyIndexer{
    // This class is used to index the albums
    // The information in the albums.csv file is cut off
    // from the lyrics.csv and songs.csv files, so we will
    // index the albums.csv file, in a separate index.

    public AlbumsIndexer(String indexDir)
    {
        super(indexDir);
    }


    /**
     * This method creates the index of the albums. <br>
     * It uses the albums array, that is generated from the
     * createAlbumsObjects method from CSVFileFilter class . <br>
     * @param albums
     * @throws IOException
     * @see CSVFileFilter
     * @see CSVFileFilter#createAlbumsObjects(String fileName, int numberOfAlbums)
     */
    public void indexAlbumsFile(Albums[] albums) throws IOException {
        // For each album stored in the albums, create a document and add it to the index
        for (Albums album : albums) {
            Document albumsDocument = getAlbumsDocument(album);
            writer.addDocument(albumsDocument);
        }
    }

    /**
     * This method creates a document for the albums. <br>
     * @param albumsObj
     * @return The document(Document) of the albums object, that
     * contains the metadata of the albums(singer name, album name, album type, album year)
     */
    public Document getAlbumsDocument(Albums albumsObj){
        Document document = new Document();
        // Add the fields of the document
        // The fields represent the metadata of the albums, that we have saved in the Albums object
        document.add(new TextField(LuceneConstants.ALBUMS_SINGER_NAME, albumsObj.getSingerName(), Field.Store.YES));
        document.add(new TextField(LuceneConstants.ALBUMS_ALBUM_NAME, albumsObj.getAlbumName(), Field.Store.YES));
        document.add(new TextField(LuceneConstants.ALBUMS_ALBUM_TYPE, albumsObj.getAlbumType(), Field.Store.YES));
        document.add(new TextField(LuceneConstants.ALBUMS_ALBUM_YEAR, albumsObj.getAlbumYear(), Field.Store.YES));
        return document;
    }
}
