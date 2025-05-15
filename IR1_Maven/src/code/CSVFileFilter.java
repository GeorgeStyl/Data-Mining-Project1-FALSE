import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;

public class CSVFileFilter implements FileFilter {




    @Override
    public boolean accept(File pathname) {
        // Returns true if the file name ends with .csv, and false otherwise.
        return pathname.getName().toLowerCase().endsWith(".csv");
    }


    /**
     * This method returns the number of the last record of the file.
     * Since the records are sorted in an ascending order, based on the number of the record,
     * the number of the last record is equal to the number of the records of the file.
     * @param fileName The name of the file
     * @return The number of the last record of the file - the number of the records of the file
     * @throws IOException
     */
    public static int getNumberOfRecords(String fileName) throws IOException
    {
        // Move file pointer to the end of the file
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        // We want to reach the second last \n, so we move the pointer to the last byte of the file - 1 byte
        // Otherwise we will read the last line, which is empty (after the last \n)
        raf.seek(raf.length() - 1);
        // Move backwards, and read each byte
        // until a line break is detected, which indicates the last line
        long pos = raf.getFilePointer();
        while (pos > 0) {
            pos--;
            raf.seek(pos);
            if (raf.readByte() == '\n') {
                break;
            }
        }
        // If the file is not empty, read the line
        if (pos == 0) {
            System.out.println("The file : " + fileName + " is empty");
            System.exit(1);
        }
        String lastLine = raf.readLine();
        // Tokenize the line, and get the first token, which is the number of the last record
        String[] tokens = lastLine.split(",");
        int numberOfRecords = 0;
        numberOfRecords = Integer.parseInt(tokens[0]);
        // Counting starts from 0, so we add 1
        numberOfRecords++;
        // Close the RandomAccessFile object
        raf.close();
        return numberOfRecords;
    }

    /**
     * This method scans the lyrics.csv file, and returns the number of the records of the file.
     * @param fileName The name of the file
     * @return The number of the records of the file lyrics.csv
     * @throws IOException
     */
    public static int getNumberOfLyricRecords(String fileName) throws IOException {
        // We can't use the getNumberOfRecords method, because the lyrics.csv file is not
        // sorted with an ascending order, based on the number of the records.
        // We will read the file line by line, and count the lines
        int numberOfRecords = 0;
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            String[] line ;
            // Skip first line, because it contains the headers of the columns
            csvReader.readNext();
            while ((line = csvReader.readNext()) != null) {
                numberOfRecords++;
            }
        } catch (CsvValidationException e) {
            System.out.println("Error reading the songs.csv file");
            System.exit(1);
        }

        return numberOfRecords;
    }

    /**
     * This method creates the Lyrics objects, and returns an array of Lyrics objects.
     * @param fileName The name of the file
     * @param numberOfLyrics The number of the records of the file
     * @return An array of Lyrics objects
     */
    public static Lyrics[] createLyricsObjects(String fileName, int numberOfLyrics) {
        Lyrics[] lyrics = new Lyrics[numberOfLyrics];
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            String[] line;
            // Skip first line, because it contains the headers of the columns
            csvReader.readNext();
            int i = 0;
            while ((line = csvReader.readNext()) != null) {
                // singer_name is the third column of the lyrics.csv file
                String singerName = line[2];
                // Remove the word Lyrics from the singer name
                singerName = singerName.replace(" Lyrics", "");
                // Remove anything that is inside the parenthesis
                singerName = singerName.replaceAll("\\(.*?\\) ?", "");
                // Remove anything that is after a & symbol (keep only the first singer)
                singerName = singerName.replaceAll(" &.*", "");
                // Remove anything that is after a (comma), symbol (keep only the first singer)
                singerName = singerName.replaceAll(",.*", "");
                // Remove white spaces from the beginning and the end of the string
                singerName = singerName.trim();
                // song_name is the fourth column of the lyrics.csv file
                String songName = line[3];
                // song_href is the second column of the lyrics.csv file
                String songHref = line[1];
                // Create the Lyrics object
                lyrics[i] = new Lyrics(singerName, songName, songHref);
                // Add each row of the lyrics to the lyricsList of the Lyrics object
                String[] lyricsRows = line[4].split("\n");
                for (String lyricsRow : lyricsRows) {
                    lyrics[i].lyricsList.add(lyricsRow);
                }
                i++;
            }
        } catch (CsvValidationException | IOException e) {
            System.out.println("Error reading the songs.csv file");
            System.exit(1);
        }
        return lyrics;
    }

    /**
     * This method creates the Songs objects, and returns an array of Songs objects.
     * @param fileName The name of the file
     * @param numberOfRecords The number of the records of the file
     * @return An array of Songs objects
     */
    public static Songs[] createSongsObjects(String fileName, int numberOfRecords) {
        Songs[] songs = new Songs[numberOfRecords];
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            String[] line ;
            // Skip first line, because it contains the headers of the columns
            csvReader.readNext();
            int i = 0;
            while ((line = csvReader.readNext()) != null) {
                // singer_name is the third column of the songs.csv file
                String singerName = line[2];
                // Remove the word Lyrics from the singer name
                singerName = singerName.replace(" Lyrics", "");
                // song_name is the fourth column of the songs.csv file
                String songName = line[3];
                // song_href is the fifth column of the songs.csv file
                String songHref = line[4];
                // Create the Songs object
                songs[i] = new Songs(singerName, songName, songHref);
                i++;
            }
        } catch (CsvValidationException | IOException e) {
            System.out.println("Error reading the songs.csv file");
            System.exit(1);
        }
        return songs;
    }

    /**
     * This method creates the Albums objects, and returns an array of Albums objects.
     * @param fileName The name of the file
     * @param numberOfAlbums  The number of the records of the file
     * @return An array of Albums objects
     */
    public static Albums[] createAlbumsObjects(String fileName, int numberOfAlbums) {
        Albums[] albums = new Albums[numberOfAlbums];
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            String[] line ;
            // Skip first line, because it contains the headers of the columns
            csvReader.readNext();
            int i = 0;
            while ((line = csvReader.readNext()) != null) {
                // singer_name is the third column of the songs.csv file
                String singerName = line[2];
                // Remove the word Lyrics from the singer name
                singerName = singerName.replace(" Lyrics", "");
                // album_name is the fourth column of the songs.csv file
                String albumName = line[3];
                // album_type is the fifth column of the songs.csv file
                String albumType = line[4];
                // album_year is the sixth column of the songs.csv file
                String albumYear = line[5];
                // Create the Albums object
                albums[i] = new Albums(singerName, albumName, albumType, albumYear);
                i++;
            }
        } catch (CsvValidationException | IOException e) {
            System.out.println("Error reading the songs.csv file");
            System.exit(1);
        }
        return albums;
    }
}



