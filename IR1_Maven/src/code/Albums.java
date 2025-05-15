/**
 * This class is used to represent a record from the albums.csv file. <br>
 * It has four attributes: singerName, albumName, albumType, albumYear.
 * It has a constructor and getters for each attribute.
 * It is used in the class AlbumList.
 */
public class Albums {
    private String singerName;
    private String albumName;
    private String albumType;
    private String albumYear;

    public Albums(String singerName, String albumName, String albumType, String albumYear) {
        this.singerName = singerName;
        this.albumName = albumName;
        this.albumType = albumType;
        this.albumYear = albumYear;
    }

    public String getSingerName() {
        return singerName;
    }
    public String getAlbumName() {
        return albumName;
    }
    public String getAlbumType() {
        return albumType;
    }
    public String getAlbumYear() {
        return albumYear;
    }
}
