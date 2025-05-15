import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.io.IOException;
import java.util.LinkedList;

/**
 * This class contains the code for the creation of the index of the song and lyrics. <br>
 *
 */
public class SongLyricsIndexer extends MyIndexer {
    public SongLyricsIndexer(String indexDir){
        super(indexDir);
    }

    /**
     * This method creates the index of the song and lyrics. <br>
     * It uses the songs and lyrics arrays, that are generated from the
     * createSongsObjects and createLyricsObjects methods from CSVFileFilter class . <br>
     * @param songs The array of the songs
     * @param lyrics The array of the lyrics
     * @throws IOException
     * @see CSVFileFilter
     * @see CSVFileFilter#createSongsObjects(String fileName, int numberOfRecords)
     * @see CSVFileFilter#createLyricsObjects(String fileName, int numberOfLyrics)
     */
    public void indexSongLyricsFile(Songs[] songs, Lyrics[] lyrics)  throws IOException {
        // For each song:
        // 1. Find the lyrics of the song in the lyrics
        // 2. Create a document and add it to the index
        for (Songs song : songs) {
            String songName = song.getSongName();
            String singerName = song.getSingerName();
            LinkedList<String> lyricsOfSong = null;
            for (Lyrics lyric : lyrics) {
                if (lyric.getSongName().equals(songName) &&
                        lyric.getArtistName().equals(singerName)) {
                    lyricsOfSong = lyric.getLyricsList();
                    break;
                }
            }
            // Check if lyrics were not found
            if (lyricsOfSong == null) {
                // lyrics.csv file does not always contain the same artist name for the same song
                // So, if the lyrics were not found, continue to the next song
                // Additionally since the number of records in the songs.csv file is  26041
                // and the number of records in the lyrics.csv file is  25742,
                // some songs will not be found in the lyrics.csv file
                continue;
            }
            Document songDocument = getSongLyricsDocument(songName, singerName, lyricsOfSong);
            writer.addDocument(songDocument);
        }

    }

    /**
     * This method creates a document for the song and the lyrics. <br>
     * @param songName
     * @param singerName
     * @param lyricsOfSong
     * @return The document(Document) for the song and the lyrics, that contains
     * the song name, the singer name and the lyrics text
     */
    private Document getSongLyricsDocument(String songName, String singerName, LinkedList<String> lyricsOfSong) {
        // Create a document for the song and the lyrics
        Document document = new Document();
        // Add the song name to the document
        Field songNameField = new TextField(LuceneConstants.SONGS_SONG_NAME, songName, Field.Store.YES);
        document.add(songNameField);
        // Add the singer name to the document
        Field singerNameField = new TextField(LuceneConstants.SONGS_SINGER_NAME, singerName, Field.Store.YES);
        document.add(singerNameField);
        // Add the lyrics to the document
        StringBuilder lyricsStringBuilder = new StringBuilder();
        for (String line : lyricsOfSong) {
            lyricsStringBuilder.append(line).append("\n");
        }
        String lyricsString = lyricsStringBuilder.toString();
        Field lyricsField = new TextField(LuceneConstants.LYRICS_LYRICS_TEXT, lyricsString, Field.Store.YES);
//        Field lyricsField = new TextField(LuceneConstants.LYRICS_LYRICS_TEXT, String.join("\n", lyricsOfSong), Field.Store.YES);
//        document.add(lyricsField);
        return document;
    }

}
