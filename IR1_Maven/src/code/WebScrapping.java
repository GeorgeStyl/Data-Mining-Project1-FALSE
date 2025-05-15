import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * WebScrapping.java <br>
 * This class gets the artist name and song name <br>
 * Trims of whitespaces except of "-" because "Radiohead - Airbag" is a valid request <br>
 * When the trim is over, it creates an URL and then proceed to make an HTTP Request. On success, it returns a String of lyrics
 */
public class WebScrapping {
    String artist       = "";
    String songName     = "";
    String textClean    = "";

    public WebScrapping(String artist, String songName) {
        // trim whitespaced and special characters except of "-"
        this.artist     = prepareURL(artist);
        this.songName   = prepareURL(songName);
    }


    public static String prepareURL(String input) {
        if (input == null) {
            return null;
        }

        // Remove whitespaces and special characters (except '-')
        String result = input.replaceAll("[^\\w\\s-]", "").replaceAll("\\s+", "").trim();
        result = result.toLowerCase();
        System.out.println(result);
        return result;
    }

    public String getLyrics(){
        try {
            String url = "https://www.azlyrics.com/lyrics/" + this.artist + "/" + this.songName + ".html";
            System.out.println(url);


            // Fetch the HTML content of the page
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            // Find the div with the specified class
            Elements targetDivs = document.select(".col-xs-12.col-lg-8.text-center");

            // Check if the div is found
            if (!targetDivs.isEmpty()) {
                Element targetDiv = targetDivs.first();

                // get all children divs tht have no CSS class
                Elements divChildrenNoClass = targetDiv.select("div:not([class])");

                // the div containing the lyrics is the first such div
                Element divContainingLyrics = divChildrenNoClass.get(0);

                // the lyrics are text residing in the found div (but there are other <br> and comment tags in between)
                String textWithNewline = divContainingLyrics.wholeText();

                // need to remove unnecessary with spaces
                this.textClean = textWithNewline.replaceAll("(?m)^[ \t]*\r?\n", "");
                System.out.println(this.textClean);

            } else {
                System.out.println("Target div not found.");
            }

            return this.textClean;

        } catch (IOException e) {
            e.printStackTrace();
        }
//        this should not happen
        return null;
    }
}
