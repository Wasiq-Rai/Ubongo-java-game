import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class UbongoGame extends Application {
	private double ScreenWidth = Screen.getPrimary().getVisualBounds().getWidth();
	private double ScreenHeight = Screen.getPrimary().getVisualBounds().getHeight();
	
	private List<ImageView> puzzles;

    private int focusedPuzzleIndexPlayer1 = 0;
    private int focusedPuzzleIndexPlayer2 = 3;	
    private List<ImageView> puzzlesData;
    private static List<Integer> player1_puzzleData = new ArrayList<>();
    private static List<Integer> player2_puzzleData = new ArrayList<>();
    private int currentPuzzleIndex = 0;
    static Random random = new Random();
    private static int player1_puzzle = random.nextInt(8) + 1;
    private static int player2_puzzle = random.nextInt(8) + 1;
    TextField player1Input;
    TextField player2Input;
    Image cursorImage = new Image("file:Assets/Cursores/player4.png");
    VBox countdownBox ;
    int countdownDurationSeconds = 60;
    Timeline countdownTimeline = new Timeline();
    private MediaPlayer mediaPlayer;

    

    
    

    public static void main(String[] args) {
    	parsePuzzleData("Assets/Puzzles/PiezasPuzzleNormal.txt");
        launch(args);
    }
    
    @Override
    public void start(final Stage primaryStage) {
    	playBackgroundMusic();
    	System.out.println(player1_puzzle);
    	System.out.println(player2_puzzle);
        showSplashScreen(new Runnable() {
            public void run() {
                showMainMenu(primaryStage);
            }
        });
    }
    
    private void showGameScreen(Stage primaryStage, String Player1Name, String Player2Name){
    	primaryStage.setTitle("Ubongo Game");

        final VBox root = new VBox();
        root.setBackground(createBackground());

        root.setOnMouseEntered(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseEntered(root, cursorImage);
            }
        });

        root.setOnMouseExited(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseExited(root);
            }
        });
        
        

        // Top Section
        HBox topSection = createTopSection();

        // Middle Section (Player Sections)
        Pane middleSection = createMiddleSection(Player1Name,Player2Name);

        // Bottom Section
        HBox bottomSection = createBottomSection();
        root.getChildren().addAll(topSection, middleSection, bottomSection);

        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
        
        public void handle(KeyEvent event) {
            handleKeyPress(event.getCode());
        }
    });

        primaryStage.setScene(scene);

        // Set full screen
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");  // Empty string removes the hint text
//        primaryStage.setFullScreenExitKeyCombination(null);  // Disabling ESC key for exiting full screen

        primaryStage.show();
    }
    
    private static void parsePuzzleData(String filePath) {
        try (
             BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#Piezas "+player1_puzzle)) {
                    while (!(line = reader.readLine()).startsWith("#Fin")) {
                        String[] numbers = line.split(",");
                        for (String number : numbers) {
                        	player1_puzzleData.add(Integer.parseInt(number.trim()));
                        }
                    }
                }else if (line.startsWith("#Piezas "+player2_puzzle)) {
                    List<Integer> puzzleNumbers = new ArrayList<>();
                    while (!(line = reader.readLine()).startsWith("#Fin")) {
                        String[] numbers = line.split(",");
                        for (String number : numbers) {
                        	player2_puzzleData.add(Integer.parseInt(number.trim()));
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HBox createTopSection() {
        HBox topSection = new HBox(); // No spacing needed
        countdownBox=restart_Game();
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.setPrefHeight(250);  // 1/4 of the screen height

        // Top Section Background
        ImageView topBackground = createImageView("file:Assets/Fondos/fondo mesa.png", Screen.getPrimary().getVisualBounds().getWidth(), 250);

        // Ubungo Logo
        ImageView ubungoLogo = createImageIconView("file:Assets/Logos/ubongo logo.png", 250, 100);
        ubungoLogo.setX((Screen.getPrimary().getVisualBounds().getWidth() / 2) - 150);

        // Gamma Image
        List<ImageView> gammaImages = new ArrayList<ImageView>();
        gammaImages.addAll(createGammaImageCopies("file:Assets/Gemas/Gema ", 50, 50, ScreenWidth / 9, 0, 6,false));

        // Dado Image
        List<ImageView> dadoImages = new ArrayList<ImageView>();
        dadoImages.addAll(createDadoImageCopies("file:Assets/Dado/Dado ", 50, 50, ScreenWidth / 9,1,false));
        
        List<ImageView> dadoImages2 = new ArrayList<ImageView>();
        dadoImages2.addAll(createDadoImageCopies("file:Assets/Dado/Dado ", 50, 50, ScreenWidth / 9,4,true));


        // Create buttons with icons
        Button homeButton = createSquareButton("Home", "file:Assets/Icons/home.png", 50);
        homeButton.setLayoutX(10);
        homeButton.setLayoutY(10);
        Button refreshButton = createSquareButton("Refresh", "file:Assets/Icons/refresh.png", 50);
        refreshButton.setLayoutX(10);
        refreshButton.setLayoutY(90);
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	countdownDurationSeconds=60;
            }
        });
        Button exitButton = createSquareButton("Exit", "file:Assets/Icons/exit.png", 50);
        exitButton.setLayoutX(10);
        exitButton.setLayoutY(170);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });
        
     

		// Add all elements to the topSection
        Group imageGroup = new Group(topBackground, ubungoLogo, countdownBox);
        imageGroup.getChildren().addAll(gammaImages);
        imageGroup.getChildren().addAll(dadoImages);
        imageGroup.getChildren().addAll(dadoImages2);
        imageGroup.getChildren().addAll(homeButton, refreshButton, exitButton);
        topSection.getChildren().addAll(imageGroup);
        


        return topSection;
    }

    protected VBox restart_Game() {
    	// Create a countdown timer animation
    	countdownTimeline.stop();
        countdownDurationSeconds = 60;
        
        final Label countdownLabel = new Label();
        countdownLabel.setStyle("-fx-text-fill: black; -fx-font-size: 34;");
        VBox countdownBox = new VBox(countdownLabel);
        countdownBox.setAlignment(Pos.CENTER);
        countdownBox.setLayoutX((ScreenWidth/2)-200);
        countdownBox.setLayoutY(170);

        
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame countdownKeyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                countdownDurationSeconds--;
                countdownLabel.setText("Time Remaining: " + countdownDurationSeconds + "s");

                if (countdownDurationSeconds <= 0) {
                    countdownLabel.setText("TIME OUT !!");
                    countdownTimeline.stop();
                }
            }
        });


        countdownTimeline.getKeyFrames().add(countdownKeyFrame);
        countdownTimeline.play();
        
        return countdownBox;
	}

	private Button createSquareButton(String text, String iconPath, double iconSize) {
        Button button = new Button(text);
        button.setPrefSize(iconSize, iconSize);

        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitWidth(iconSize);
        icon.setFitHeight(iconSize);

        button.setGraphic(icon);

        return button;
    }
    private List<ImageView> createGammaImageCopies(String imagePath, double fitWidth, double fitHeight, double initialX, double y, int count, boolean bottom) {
        List<ImageView> gammaImages = new ArrayList<ImageView>();
        Random random = new Random();

        for (int j = 0; j < 2; j++) {
            for (int i = 1; i <= count; i++) {
                ImageView gammaImage = createImageIconView(imagePath + i + ".png", fitWidth, fitHeight);
                gammaImage.setX(initialX += 120); // Adjust the range as needed
                if(bottom)
                	gammaImage.setY(250);
                gammaImages.add(gammaImage);
            }
        }

        return gammaImages;
    }
    private List<ImageView> createDadoImageCopies(String imagePath, double fitWidth, double fitHeight, double initialX ,int count, boolean player2) {
        List<ImageView> dadoImages = new ArrayList<ImageView>();
        Random random = new Random();
        int total=0;
        if(player2){
        	initialX+=900;
        	total=6;	
        }
        else
        	total=3;

            for (int i = count; i <= total; i++) {
                ImageView dadoImage = createImageIconView(imagePath + i + ".png", fitWidth, fitHeight);
                dadoImage.setX(initialX += 120); // Adjust the range as needed
                dadoImage.setY(170);
                dadoImages.add(dadoImage);
            }

        return dadoImages;
    }


    

    private Pane createMiddleSection(String player1Name, String player2Name) {
        Pane middleSection = new Pane();
        middleSection.setPrefHeight(500);  // 2/4 of the screen height

        // Player 1 Section (Left)
        VBox player1Section = createPlayerSection("file:Assets/Puzzles/Normal/NPuzzle "+player1_puzzle+".png", player1Name, ScreenWidth / 2, 500,true);
        player1Section.setLayoutX(0);


        // Player 2 Section (Right)
        VBox player2Section = createPlayerSection("file:Assets/Puzzles/Normal/NPuzzle "+player2_puzzle+".png", player2Name, ScreenWidth / 2, 500,false);
        player2Section.setLayoutX(Screen.getPrimary().getVisualBounds().getWidth() / 2);
        
        

        middleSection.getChildren().addAll(player1Section, player2Section);

        return middleSection;
    }


    private VBox createPlayerSection(String puzzleImagePath, String playerName, double width, double height,boolean player1) {
        VBox playerSection = new VBox(); // Adjust spacing as needed
        playerSection.setAlignment(Pos.CENTER);
        playerSection.setPrefSize(width / 3, height);

        // Player Puzzle Background
        ImageView puzzleBackground = createImageView(puzzleImagePath, width / 3, height);
        if(player1)
        VBox.setMargin(puzzleBackground, new javafx.geometry.Insets(0, 20, 0, 200));
        else
        	VBox.setMargin(puzzleBackground, new javafx.geometry.Insets(0, 0, 0, 20));

        playerSection.getChildren().add(puzzleBackground);

        // Player Information
        Label playerInfo = new Label(playerName);
        
        // Apply styles to the player name label
        playerInfo.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: #333333;" +
                "-fx-padding: 5px;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;"
        );

        playerSection.getChildren().add(playerInfo);

        return playerSection;
    }


    private HBox createBottomSection() {
        HBox bottomSection = new HBox(); // No spacing needed
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.setPrefHeight(250);  // 1/4 of the screen height

        // Create a group to hold both background images
        
        ImageView  image1= new ImageView(new Image("file:Assets/Fondos/fondo mesa.png"));
        image1.setOpacity(0);
        image1.setFitWidth(ScreenWidth);
        image1.setFitHeight(50);
        
     // Gamma Image
        List<ImageView> gammaImages = new ArrayList<ImageView>();
        gammaImages.addAll(createGammaImageCopies("file:Assets/Gemas/Gema ", 50, 50, ScreenWidth / 9, 0, 6,true));
        
        

        Group backgroundGroup = new Group(image1);
        backgroundGroup.getChildren().addAll(gammaImages);

        puzzles = new ArrayList<ImageView>();
        puzzles.addAll(createPuzzles("file:Assets/Piezas/Pieza ", 350, 250, (ScreenWidth / 9)-200, 0, 3));
        backgroundGroup.getChildren().addAll(puzzles);

        bottomSection.getChildren().add(backgroundGroup);

        return bottomSection;
    }
    
    private List<ImageView> createPuzzles(String imagePath, double fitWidth, double fitHeight, double initialX, double y, int count) {
        puzzles = new ArrayList<ImageView>();
        Random random = new Random();
        final ImageView[] puzzleArray = new ImageView[6];

        for (int i = 0; i < count; i++) {
            ImageView puzzle = new ImageView(new Image(imagePath + player1_puzzleData.get(i) + ".png"));
            puzzle.setPreserveRatio(false);
            puzzle.setFitWidth(puzzle.getImage().getWidth()-100);
            
            puzzle.setFitHeight((puzzle.getImage().getHeight()/2)+30);

            puzzleArray[i] = puzzle;


            puzzle.setFocusTraversable(true);
            puzzle.setX(initialX += 200); // Adjust the range as needed
            puzzle.setY(70);
            puzzles.add(puzzle);
        }
            initialX+=400;
            int index=3;
            for (int i = 0; i < count; i++) {
                ImageView puzzle = new ImageView(new Image(imagePath + player2_puzzleData.get(i) + ".png"));
                puzzle.setPreserveRatio(false);
                puzzle.setFitWidth(puzzle.getImage().getWidth()-100);
                
                puzzle.setFitHeight((puzzle.getImage().getHeight()/2)+30);

            	puzzleArray[index] = puzzle;
            	index++;

                puzzle.setFocusTraversable(true);
                puzzle.setX(initialX += 200); // Adjust the range as needed
                puzzle.setY(70);
                puzzles.add(puzzle);
            }
        

        return puzzles;
    }
    
    private void handleKeyPress(KeyCode code) {
        if (code.isDigitKey()) {
            int digit = Integer.parseInt(code.getName());
            if (digit >= 1 && digit <= 3) {
                focusedPuzzleIndexPlayer1 = digit - 1;
            } else if (digit >= 7 && digit <= 9) {
                focusedPuzzleIndexPlayer2 = digit - 4;
            }
        } else {

            	switch (code) {
            	case W:
            		moveUp(puzzles.get(focusedPuzzleIndexPlayer1));
            		break;
            	case A:
            		moveLeft(puzzles.get(focusedPuzzleIndexPlayer1));
            		break;
            	case S:
            		moveDown(puzzles.get(focusedPuzzleIndexPlayer1));
            		break;
            	case D:
            		moveRight(puzzles.get(focusedPuzzleIndexPlayer1));
            		break;
            	case X:
            		rotate(puzzles.get(focusedPuzzleIndexPlayer1));
            		break;
            	case F:
            		flip(puzzles.get(focusedPuzzleIndexPlayer1));
            		break;
            	case UP:
            		moveUp(puzzles.get(focusedPuzzleIndexPlayer2));
            		break;
            	case LEFT:
            		moveLeft(puzzles.get(focusedPuzzleIndexPlayer2));
            		break;
            	case DOWN:
            		moveDown(puzzles.get(focusedPuzzleIndexPlayer2));
            		break;
            	case RIGHT:
            		moveRight(puzzles.get(focusedPuzzleIndexPlayer2));
            		break;
            	case K:
            		rotate(puzzles.get(focusedPuzzleIndexPlayer2));
            		break;
            	case L:
            		flip(puzzles.get(focusedPuzzleIndexPlayer2));
            		break;

            	
            }
        }
    }
    
    private void moveRight(ImageView puzzle) {
        if (puzzle.getX() + 10 + puzzle.getFitWidth() <= ScreenWidth && puzzle.getX() + 10 + puzzle.getFitWidth() != ScreenWidth/2) {
            puzzle.setX(puzzle.getX() + 10);
        }
    }

    private void moveLeft(ImageView puzzle) {
        if (puzzle.getX() - 10 >= 0) {
            puzzle.setX(puzzle.getX() - 10);
        }
    }

    private void moveUp(ImageView puzzle) {
    	System.out.println(puzzle.getY());
        if (puzzle.getY() - 10 > -500) 
            puzzle.setY(puzzle.getY() - 10);
        
    }

    private void moveDown(ImageView puzzle) {
        if (puzzle.getY() + 10 + puzzle.getFitHeight() <= 250) {
            puzzle.setY(puzzle.getY() + 10);
        }
    }
    
    private void rotate(ImageView puzzle) {
        puzzle.setRotate((puzzle.getRotate() + 90) % 360);
    }

    private void flip(ImageView puzzle) {
        puzzle.setScaleX(puzzle.getScaleX() * -1);
    }

  

    private ImageView createImageView(String imagePath, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setPreserveRatio(false);
        imageView.minWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        return imageView;
    }
    private ImageView createImageIconView(String imagePath, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(fitWidth);
//        imageView.setFitHeight(fitHeight);
        return imageView;
    }


    

    private void addStackedImages(HBox container, String imageUrl, int count, double spacing, double size) {
        for (int i = 0; i < count; i++) {
            ImageView imageView = createImageView(imageUrl, size, size);
            container.getChildren().add(imageView);
            if (i < count - 1) {
                container.setSpacing(spacing);
            }
        }
    }

    private Background createBackground() {
        // Replace "your_image_path.jpg" with the actual path to your image file
        Image backgroundImage = new Image("file:Assets/Fondos/fondo mesa.png");
        BackgroundFill backgroundFill = new BackgroundFill(
                new ImagePattern(backgroundImage),
                null,
                null);

        return new Background(backgroundFill);
    }
    


    private void showSplashScreen(final Runnable onSplashFinish) {
        final Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);

        VBox splashRoot = new VBox();
        splashRoot.setBackground(createBackground()); // Set background

        // Use an ImageView with an image for the background
        ImageView splashImage = new ImageView(new Image("file:Assets/Fondos/Ubongo-Hero.jpg")); // Change to the path of your splash image
        splashImage.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth());
        splashImage.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight());

        // Set blend mode to ensure the image is displayed on top of the black background
        splashImage.setBlendMode(BlendMode.SRC_OVER);

        splashRoot.getChildren().add(splashImage);

        Scene splashScene = new Scene(splashRoot);
        splashStage.setScene(splashScene);
        splashStage.setFullScreen(false);  // Set to false to avoid full screen for splash screen

        // Delay for 3 seconds
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    
                    public void run() {
                        splashStage.close();
                        onSplashFinish.run(); // Run the provided code after the splash screen closes
                    }
                });
            }
        }, 3000);

        splashStage.show();
    }
    
    private void showMainMenu(final Stage stage) {
        // Set up background image
        ImageView backgroundImage = new ImageView(new Image("file:Assets/Fondos/Ubongo-Hero.jpg"));
        backgroundImage.setFitWidth(ScreenWidth);
        backgroundImage.setFitHeight(ScreenHeight);

        // Player 1 Name Input
        Label player1Label = new Label("Enter Player 1 Name");
        player1Label.setStyle("-fx-font-size: 34; -fx-text-fill: white;");
        player1Input = new TextField();
        player1Input.setStyle("-fx-font-size: 20; -fx-background-color: #333333; -fx-text-fill: white;");
        player1Input.setMaxWidth(300);

        // Player 2 Name Input
        Label player2Label = new Label("Enter Player 2 Name");
        player2Label.setStyle("-fx-font-size: 34; -fx-text-fill: white;");
        player2Input = new TextField();
        player2Input.setStyle("-fx-font-size: 20; -fx-background-color: #333333; -fx-text-fill: white;");
        player2Input.setMaxWidth(300);

        final Label errorMessageLabel = new Label();
        errorMessageLabel.setStyle("-fx-font-size: 26; -fx-text-fill: white;");
        StackPane.setAlignment(errorMessageLabel, javafx.geometry.Pos.BOTTOM_CENTER);
        StackPane.setMargin(errorMessageLabel, new javafx.geometry.Insets(0, 0, 150, 0));

        // Next Button
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-font-size: 40; -fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-background-radius: 10; -fx-border-radius: 10;");

        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (player1Input.getText().isEmpty() || player2Input.getText().isEmpty()) {
                    errorMessageLabel.setText("Both player names are required!");
                } else {
                    stage.close();
                    showGameScreen(stage, player1Input.getText(), player2Input.getText());
                }
            }
        });
        // StackPane to overlay components
        final StackPane root = new StackPane();
        root.getChildren().addAll(backgroundImage, player1Label, player1Input, player2Label, player2Input, nextButton , errorMessageLabel);

        // Positioning components
        StackPane.setAlignment(player1Label, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(player1Label, new javafx.geometry.Insets(450, 0, 0, 250));

        StackPane.setAlignment(player1Input, javafx.geometry.Pos.CENTER_LEFT);
        StackPane.setMargin(player1Input, new javafx.geometry.Insets(50, 0, 0, 250));

        StackPane.setAlignment(player2Label, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(player2Label, new javafx.geometry.Insets(450, 250, 0, 0));

        StackPane.setAlignment(player2Input, javafx.geometry.Pos.CENTER_RIGHT);
        StackPane.setMargin(player2Input, new javafx.geometry.Insets(50, 250, 0, 0));

        StackPane.setAlignment(nextButton, javafx.geometry.Pos.BOTTOM_CENTER);
        StackPane.setMargin(nextButton, new javafx.geometry.Insets(0, 0, 200, 0));

        // Create scene
        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        root.setOnMouseEntered(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                root.getScene().setCursor(new ImageCursor(cursorImage, 150, 50));
            }
        });

        root.setOnMouseExited(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                root.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        // Set up stage
        stage.setTitle("Ubongo Puzzle Game - Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    
    private void handleMouseEntered(VBox root, Image cursorImage) {
        setCustomCursor(root, cursorImage);
    }

    private void handleMouseExited(VBox root) {
        setDefaultCursor(root);
    }

    private void setCustomCursor(VBox root, Image cursorImage) {
        root.getScene().setCursor(new ImageCursor(cursorImage,150,50));
    }

    private void setDefaultCursor(VBox root) {
        root.getScene().setCursor(Cursor.DEFAULT);
    }
  
    private void playBackgroundMusic() {
        // Provide the path to your music file
        String musicFilePath = "Assets/Music/Fighting_Love.wav";
        Media media = new Media(new File(musicFilePath).toURI().toString());

        // Create a MediaPlayer
        mediaPlayer = new MediaPlayer(media);

        // Set the music to loop indefinitely
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Play the music
        mediaPlayer.play();
    }

    // Stop the music when the application is closed
    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    
    

}