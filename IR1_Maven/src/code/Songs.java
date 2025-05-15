/**
 * Songs.java <br>
 * This class is used to store the information of songs.
 * It contains the name of singer, the name of song and the href of song. <br>
 * This class is used to store the information of songs from the songs.csv file.
 */
public class Songs {
    private String singerName;
    private String songName;
    private String songHref;

    public Songs(String singerName, String songName, String songHref) {
        this.singerName = singerName;
        this.songName = songName;
        this.songHref = songHref;
    }

    public String getSingerName() {
        return singerName;
    }
    public String getSongName() {
        return songName;
    }
    public String getSongHref() {
        return songHref;
    }
}
