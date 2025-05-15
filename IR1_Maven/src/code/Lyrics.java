import java.util.*;

/**
 * Lyrics.java <br>
 * This class is used to store the information of lyrics. <br>
 * It contains the name of singer, the name of song, the href of song and the lyrics of song.
 */
public class Lyrics {
    LinkedList<String> lyricsList;
    String artistName;
    String songName;
    String songHref;


    public Lyrics(String artistName, String songName, String songHref) {
        lyricsList = new LinkedList<>();
        this.artistName = artistName;
        this.songName = songName;
        this.songHref = songHref;
    }

    public LinkedList<String> getLyricsList() {
        return lyricsList;
    }


    public String getArtistName() {
        return artistName;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongHref() { return songHref; }

}
