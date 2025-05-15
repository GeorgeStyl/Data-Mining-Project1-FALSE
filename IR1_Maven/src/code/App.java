import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.event.ChangeListener;


public class App extends Application {
    // Get current working directory
    static final String currentPath = System.getProperty("user.dir");
    // Specify the path of the Index directory(String)
    static final String indexDir = currentPath + "\\Index";
    static final String albumsIndexDir = currentPath + "\\AlbumsIndex";

    static final String LYRICS = "lyrics";
    static final String SONGS = "songs";
    static final String ALBUMS = "albums";
    static final String[] SongLyricsFields = {LuceneConstants.SONGS_SONG_NAME,
            LuceneConstants.SONGS_SINGER_NAME,
            LuceneConstants.LYRICS_LYRICS_TEXT};
    static final String[] AlbumsFields = {LuceneConstants.ALBUMS_ALBUM_NAME,
            LuceneConstants.ALBUMS_SINGER_NAME,
            LuceneConstants.ALBUMS_ALBUM_TYPE,
            LuceneConstants.ALBUMS_ALBUM_YEAR};


    int leftParenthCount=0;

    private Button booleanModelButt;
    private Button vectorSpaceModelButt;
    private Button otherQueries;
    private static String didIndexesCreated;
    private static String indexingTime;
    private static String numOfSongsLyricsDocs;
    private static String numOfAlbumDocs;
    private Alert alert;


    /**
     * This method deletes all files in the index directory, so that
     * the program will not print the as many hits as the number of
     * documents in the index multiplied by the number of times the
     * program is executed.
     * @param index The path of the index directory
     */
    private static void deleteFiles(String index) {
        // Delete all files in the index directory, if any.
        // Otherwise, the program will print the as many
        // hits as the number of documents in the index
        // multiplied by the number of times the program
        // is executed.

        File indexDirFile = new File(index);
        try {
            for (File file : Objects.requireNonNull(indexDirFile.listFiles())) {
                boolean delete = file.delete();
                if (!delete) {
                    System.out.println("Error deleting the files in the Index directory");
                    System.exit(1);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Error finding files in the Index directory");
            System.exit(1);
        }
    }

    /**
     * This is a generic method, which retrieves the records from the
     * specified file, and returns them as an array of objects, depending
     * on the type of the T variable.
     * @param fileName The name of the file to be read
     * @param type The type of the objects to be returned(Songs, Albums or Lyrics)
     * @return An array of objects, depending on the type of the T variable
     * @param <T>
     */
    private static <T> T getRecords(String fileName, String type) {
        int numberOfRecords = 0;
        try {
            switch (type) {
                case LYRICS:
                    numberOfRecords = CSVFileFilter.getNumberOfLyricRecords(fileName);
                    break;
                case SONGS:
                    numberOfRecords = CSVFileFilter.getNumberOfRecords(fileName);
                    break;
                case ALBUMS:
                    numberOfRecords = CSVFileFilter.getNumberOfRecords(fileName);
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error finding out the number of records in the " + fileName + " file");
            System.exit(1);
        }

        switch (type) {
            case LYRICS:
                return (T) CSVFileFilter.createLyricsObjects(fileName, numberOfRecords);
            case SONGS:
                return (T) CSVFileFilter.createSongsObjects(fileName, numberOfRecords);
            case ALBUMS:
                return (T) CSVFileFilter.createAlbumsObjects(fileName, numberOfRecords);
        }
        return null;
    }

    /**
     * This method displays the menu of the program and returns the
     * choice of the user.
     * @return The choice of the user(1. Search songs, 2. Search albums, 3. Exit)
     */
    private static int displayMenu() {

        System.out.println("Do you want to search songs or albums?\n");
        System.out.println("Please enter the number of your choice");
        System.out.println("**************************************************");
        System.out.println("1. Search songs");
        System.out.println("2. Search albums");
        System.out.println("3. Exit");
        System.out.println("**************************************************");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while (true) {
            try {
                choice = scanner.nextInt();
                if (choice == 1 || choice == 2 || choice == 3) {
                    break;
                } else {
                    System.out.println("Please enter a valid number (1, 2 or 3)");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid integer");
                // Consume the invalid input and clear the buffer
                scanner.next();
            }
        }
        return choice;
    }

    /**
     * This method displays the hits of the search.
     * @param hits The hits of the search
     * @param searcher The Searcher object, for the searching of the index(either songs-lyrics or albums searcher)
     * @param topK The number of hits to be returned
     * @param type The type of the searcher(songs-lyrics or albums)
     */
    private static void displayHits(TopDocs hits, Searcher searcher, int topK, String type) {
        System.out.println(hits.totalHits + " documents found.");
        System.out.println("topK = " + topK);
        int topkCounter = 0;
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println("----------------------------------------");
            System.out.println("Hit " + ++topkCounter);
            System.out.println("----------------------------------------");
            Document doc = null;
            doc = searcher.getDocument(scoreDoc);
            switch (type) {
                case SONGS:
                    System.out.println(doc.get(LuceneConstants.SONGS_SINGER_NAME) + " : " +
                            doc.get(LuceneConstants.SONGS_SONG_NAME) +
                            doc.get(LuceneConstants.LYRICS_LYRICS_TEXT));
                    break;
                case ALBUMS:
                    System.out.println(doc.get(LuceneConstants.ALBUMS_SINGER_NAME) + " : " +
                            doc.get(LuceneConstants.ALBUMS_ALBUM_NAME) +
                            doc.get(LuceneConstants.ALBUMS_ALBUM_TYPE) +
                            doc.get(LuceneConstants.ALBUMS_ALBUM_YEAR));
                    break;
            }
        }
    }

    /**
     * This method displays the fields of the documents to be searched
     * and returns the choice of the user.
     * @param scanner The Scanner object, for the user input
     * @param type The type of the searcher(songs-lyrics or albums)
     * @return The choice of the user (0, 1 or 2 for songs-lyrics, 0, 1, 2 or 3 for albums)
     */
    private static int chooseField(Scanner scanner, String type){
        int fieldChoice = 0;
        if (type.equals(SONGS)){
            System.out.println("Enter the field name to search(Choose its number):");
                System.out.println("Accepted fields: " + Arrays.toString(SongLyricsFields));
                System.out.println("Choose 0 for " + SongLyricsFields[0]);
                System.out.println("Choose 1 for " + SongLyricsFields[1]);
                System.out.println("Choose 2 for " + SongLyricsFields[2]);
                while (true) {
                    try {
                        fieldChoice = scanner.nextInt();
                        if (fieldChoice == 0 || fieldChoice == 1 || fieldChoice == 2) {
                            break;
                        } else {
                            System.out.println("Please enter a valid number (0, 1 or 2)");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter a valid integer");
                        // Consume the invalid input and clear the buffer
                        scanner.next();
                    }
                }
        }
        else {
            System.out.println("Enter the field name to search(Choose its number):");
            System.out.println("Accepted fields: " + Arrays.toString(AlbumsFields));
            System.out.println("Choose 0 for " + AlbumsFields[0]);
            System.out.println("Choose 1 for " + AlbumsFields[1]);
            System.out.println("Choose 2 for " + AlbumsFields[2]);
            System.out.println("Choose 3 for " + AlbumsFields[3]);
            while (true) {
                try {
                    fieldChoice = scanner.nextInt();
                    if (fieldChoice == 0 || fieldChoice == 1 || fieldChoice == 2 || fieldChoice == 3) {
                        break;
                    } else {
                        System.out.println("Please enter a valid number (0, 1, 2 or 3)");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid integer");
                    // Consume the invalid input and clear the buffer
                    scanner.next();
                }
            }
        }
        // Consume the newline character
        scanner.nextLine();
        return fieldChoice;
    }

    /**
     * this function is called when "exit" button is clicked"
     * @param stage The stage object to be closed
     */

    @Override
    public void start(Stage stage) {
        booleanModelButt        = new Button("Boolean Model");
        vectorSpaceModelButt    = new Button("Vector Space Model");
        otherQueries            = new Button("Other Queries");
//        numOfAlbumDocs          = new Label("Number of Albums Docs :");


        VBox topPane = new VBox(3, otherQueries, booleanModelButt, vectorSpaceModelButt);
        topPane.setAlignment(Pos.CENTER);
        topPane.setPadding(new Insets(15));


        Label msLabel = new Label(indexingTime);
        msLabel.setAlignment(Pos.BOTTOM_LEFT);
        msLabel.setTextFill(Color.GREEN);

        Label idxCrtd = new Label(didIndexesCreated);
        msLabel.setAlignment(Pos.BOTTOM_LEFT);
        msLabel.setTextFill(Color.GREEN);

        Label noSLDocs = new Label(numOfSongsLyricsDocs);
        noSLDocs.setAlignment(Pos.BOTTOM_LEFT);
        noSLDocs.setTextFill(Color.GREEN);

        Label noADocs  = new Label(numOfAlbumDocs);
        noADocs.setAlignment(Pos.BASELINE_LEFT);
        noADocs.setTextFill(Color.GREEN);

        Button exitButt = new Button("Exit");

        VBox mainPane = new VBox(5);
        mainPane.getChildren().addAll(topPane, msLabel, noSLDocs, noADocs ,exitButt);
        mainPane.setAlignment(Pos.CENTER);




        //~~~~~~~~~~~~~~~~~~~~~~EVENTS~~~~~~~~~~~~~~~~~~~~~~
        otherQueries.setOnAction((e->{
            openOtherQueriesDialog();
        }));

        booleanModelButt.setOnAction((e->{
            openBooleanModelDialog();
        }));

        Scene scene = new Scene(mainPane, 350, 250);
        stage.setScene(scene);
//        stage.setTitle("FALSE search engine");
        stage.show();


    }

    public void openOtherQueriesDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Select your option");


        Button choice1  = new Button("Search songs");
        Button choice2  = new Button("Search albums");
        Button exitButt = new Button("Exit");
        exitButt.setAlignment(Pos.BASELINE_LEFT);

        VBox vb         = new VBox(3,choice1, choice2, exitButt);
        vb.setAlignment(Pos.CENTER);


        Scene dialogScene = new Scene(vb, 250, 150);
        dialogStage.setScene(dialogScene);


        exitButt.setOnAction((e->{
            dialogStage.close();
            e.consume();
        }));

        choice1.setOnAction((e->{
            openSongsDialog();
            e.consume();
        }));

        choice2.setOnAction((e->{
            openSearchAlbumDialog();
            e.consume();
        }));


        dialogStage.showAndWait();
    }

    private void openSongsDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Songs");

        Label l = new Label("Select the field name to search");

        ComboBox<String> choices = new ComboBox<>();
        choices.getItems().addAll("songsSongName", "songsSingerName", "songsLyrics");
        choices.getSelectionModel().select(0);


        HBox hb = new HBox(10, l, choices);
        hb.setAlignment(Pos.CENTER);

        Button selectButt = new Button("Select");
        Button exitButt = new Button("Exit");

        VBox vb = new VBox(10, hb, selectButt, exitButt);
        vb.setAlignment(Pos.CENTER);


        Scene dialogScene = new Scene(vb,350, 250);
        dialogStage.setScene(dialogScene);


        selectButt.setOnAction((e->{
             System.out.println("Selected item: " + choices.getSelectionModel().getSelectedItem()
             + "\nTODO:: get selected items");
        }));

        exitButt.setOnAction((e->{
            dialogStage.close();
            e.consume();
        }));


        dialogStage.showAndWait();
    }

    private void openSearchAlbumDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Select your option");


        Button exitButt = new Button("Exit");

        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll("Album Name", "album: Singer Name", "album: AlbumType", "album: AlbumYear");
        cb.getSelectionModel().select(0);

        Label l = new Label("Select the field name to search");

        HBox hb = new HBox(10, l, cb);


        Scene dialogScene = new Scene(hb, 350, 350);
        dialogStage.setScene(dialogScene);


        exitButt.setOnAction((e->{
            dialogStage.close();
            e.consume();
        }));


        dialogStage.showAndWait();
    }

    public void openSearchSongsDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Select your option");


        Button exitButt = new Button("Exit");


        exitButt.setOnAction((e->{
            dialogStage.close();
            e.consume();
        }));

        dialogStage.showAndWait();
    }
    public void openBooleanModelDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Boolean Model");


//        Label dummyLabel = new Label();

        Button leftParethButt       = new Button("(");
        Button rightParenthButt     = new Button(")");
        rightParenthButt.setDisable(true);
        Button quotationMarksButt   = new Button("\"");
        Button ANDButt              = new Button("AND");
        Button ORButt               = new Button("OR");
        Button proccedButt          = new Button("Procced");
        Button exitButt             = new Button("Exit");
//        exitButt.setAlignment(Pos.CENTER);
        exitButt.setPadding(new Insets(25));

        TextField tf = new TextField();
        tf.setPromptText("Insert your query here");
//        tf.setPrefColumnCount(50);
        tf.setPadding(new Insets(10));


        GridPane gp = new GridPane();
        gp.add(leftParethButt, 0, 0);
        gp.add(rightParenthButt, 0, 1);
        gp.add(ANDButt, 1,0);
        gp.add(ORButt, 1,1);
        gp.setAlignment(Pos.CENTER);

        HBox vb = new HBox(2, quotationMarksButt);
        vb.setAlignment(Pos.CENTER);

        VBox mainPane = new VBox(tf, gp, vb, proccedButt);
        mainPane.setAlignment(Pos.CENTER);


        leftParethButt.setOnAction((e->{
            leftParethButt.setDisable(true);
            rightParenthButt.setDisable(false);
            String str = tf.getText();
            tf.setText(str+" (");
            e.consume();

        }));

        rightParenthButt.setOnAction((e->{
            rightParenthButt.setDisable(true);
            leftParethButt.setDisable(false);
            String str = tf.getText();
            tf.setText(str+") ");
            e.consume();
        }));

        ANDButt.setOnAction((e->{
            String str = tf.getText();
            tf.setText(str+" AND ");
            e.consume();
        }));

        ORButt.setOnAction((e->{
            String str = tf.getText();
            tf.setText(str+" OR ");
            e.consume();
        }));

        quotationMarksButt.setOnAction((e->{
            String str = tf.getText();
            tf.setText(str+"\"");
            e.consume();
        }));

        tf.textProperty().addListener((observable, oldValue, newValue) -> {

        });

        exitButt.setOnAction((e->{
            dialogStage.close();
            e.consume();
        }));


        Scene dialogScene = new Scene(mainPane, 350, 350);
        dialogStage.setScene(dialogScene);

        proccedButt.requestFocus();
        dialogStage.showAndWait();

    }

    public static void main(String[] args) {
        String artist   = "Taylor Swift";
        String songName = "Lover";
        String  result  = "";
        WebScrapping webScrapping = new WebScrapping(artist, songName);
        result = webScrapping.getLyrics();
        if (result == null){
            System.err.println("Error in finding Lyrics");
        } else System.out.println(result);

        //        launch();
//        // Get the current time, for the calculation of the indexing time
//        long startTime = System.currentTimeMillis();
//
//        // Get the songs, albums and lyrics information and store them in memory
//        Songs[] songs = getRecords(currentPath + "\\Data\\songs.csv", SONGS);
//        if (songs == null) {
//            System.out.println("Error creating the songs objects");
//            System.exit(1);
//        }
//
//        Albums[] albums = getRecords(currentPath + "\\Data\\albums.csv", ALBUMS);
//        if (albums == null) {
//            System.out.println("Error creating the albums objects");
//            System.exit(1);
//        }
//
//        Lyrics[] lyrics = getRecords(currentPath + "\\Data\\lyrics.csv", LYRICS);
//        if (lyrics == null) {
//            System.out.println("Error creating the lyrics objects");
//            System.exit(1);
//        }
//
//        // ------------------- Lucene Setup -------------------
//
//        // Delete all files in the Index directory, if any.
//        deleteFiles(indexDir);
//        // Delete all files in the AlbumsIndex directory, if any.
//        deleteFiles(albumsIndexDir);
//
//        // ------------------- Create the indexes -------------------
//
//        AlbumsIndexer albumsIndexer = new AlbumsIndexer(albumsIndexDir);
//        try {
//            albumsIndexer.indexAlbumsFile(albums);
//        } catch (IOException e) {
//            System.err.println("Error indexing the albums.csv file");
//            System.exit(1);
//        }
//        try {
//            albumsIndexer.close();
//        } catch (IOException e) {
//            System.err.println("Error closing the IndexWriter object");
//            System.exit(1);
//        }
//        SongLyricsIndexer songAndLyricsIndexer = new SongLyricsIndexer(indexDir);
//        try {
//            songAndLyricsIndexer.indexSongLyricsFile(songs, lyrics);
//        } catch (IOException e) {
//            System.err.println("Error indexing the songs.csv and lyrics.csv files");
//            System.exit(1);
//        }
//        try {
//            songAndLyricsIndexer.close();
//        } catch (IOException e) {
//            System.err.println("Error closing the IndexWriter object");
//            System.exit(1);
//        }
////        System.out.println("Indexes created successfully");
//        didIndexesCreated = "Indexes created successfully";
//
//        long endTime = System.currentTimeMillis();
//
//        indexingTime = "Indexing completed in : " + (endTime - startTime) + " ms";
//
//
//        // ------------------- Search the index -------------------
//
//        // Create the Searcher object, for the searching of the songs-lyrics index
//        Searcher songLyricsSearcher = new Searcher(indexDir);
//        // Display the total number of documents in the index
////        System.out.println("Number of Songs-Lyrics Docs : " + songLyricsSearcher.getTotalNumberOfDocuments());
//        numOfSongsLyricsDocs = "Number of Songs-Lyrics Docs : " + songLyricsSearcher.getTotalNumberOfDocuments();
//        // Create the Searcher object, for the searching of the albums index
//        Searcher albumsSearcher = new Searcher(albumsIndexDir);
//        // Display the total number of documents in the index
////        System.out.println("Number of Albums Docs : " + albumsSearcher.getTotalNumberOfDocuments());
//        numOfAlbumDocs = "Number of Albums Docs : " + albumsSearcher.getTotalNumberOfDocuments();
//
//
//        // Create the Scanner object, for the user input
//        Scanner scanner = new Scanner(System.in);
    }
}
