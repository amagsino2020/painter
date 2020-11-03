/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package painters;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 * <h1>Painters App</h1>
 * The Painters program implements an application that has the user to control
 * the color, width and what shapes to use on the canvas to essentially create
 * pictures or to draw over an image that the user wants to edit.
 *
 * @author Allura Magsino
 * @version 2.0.3
 * @since 2020-08-18
 */
public class Painters extends Application {

    Stack<Shape> undoHistory = new Stack();
    Stack<Shape> redoHistory = new Stack();
    /**
     * Creating a new ImageView object
     */
    ImageView picture = new ImageView();

    /**
     * Creating a local variable file
     */
    File file;

    /**
     * Creating a local variable pixel
     */
    Pixel pixel;

    /**
     * Creating a local variable saved_file
     */
    File saved_file;

    /**
     * Creating a new WritableImgage object containing the height and width
     */
    WritableImage wit = new WritableImage(200, 200);

    /**
     * Creating a new object Canvas
     */
    Canvas canvas = new Canvas();

    /**
     * Initializing a Graphics context local variable
     */
    GraphicsContext gc = canvas.getGraphicsContext2D();

    /**
     * Boolean method to check if saving is true
     */
    boolean Saving = true;

    /**
     * Storing the set color for the color picker Line
     */
    ColorPicker cpLine = new ColorPicker(Color.BLACK);

    /**
     * Storing the set color for the color picker Fill
     */
    ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);

    /**
     * Double value starting point x0 for polygon
     */
    private double x0;

    /**
     * Double value starting point y0 for polygon
     */
    private double y0;

    Spinner<Integer> polyint = new Spinner<Integer>(1, 10, 1);

    public static WritableImage tmpSnap;
    public static int numSides;
    public static double polyStartX;
    public static double polyStartY;
    public static WritableImage selImg;

    private static final Integer STARTTIME = 15;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);
    public static boolean saveFlag = false;
    public static Timeline autosaveTimeline;
    public static File filepath;

    Canvas[] multiple = new Canvas[7];

    ScrollPane sp = new ScrollPane();

    /**
     * This is the main method
     *
     * @param args Unused.
     * @return Nothing.
     * @exception IOException On input error.
     * @see IOException
     */
    public void start(Stage primaryStage) throws Exception {
        // Setting the name of the application
        primaryStage.setTitle("Paint App");
        // Creating the menu bar and setting the width
        MenuBar menuBar = new MenuBar();
        menuBar.setMinWidth(1000);
        /*
         * Grid Pane is the main layout that is being used to display all the
         * methods onto the screen. Each grid pane contains various different
         * elements that are used to make an appealing layout to the users eye.
         */
        GridPane main = new GridPane();
        GridPane lower = new GridPane();
        GridPane Shapes = new GridPane();
        GridPane maincanvas = new GridPane();
        GridPane mainpicture = new GridPane();
        GridPane bottom = new GridPane();

        // Canvas width and height
        //canvas.setWidth(650);
        //canvas.setHeight(650);
        for (int p = 0; p < 7; p++) {
            multiple[p] = new Canvas();
            multiple[p].setHeight(650);
            multiple[p].setWidth(650);
        }

        canvas = multiple[0];
        gc = canvas.getGraphicsContext2D();
        /**
         * This is the Scroll Bar method that is used to implement a scroll bar
         * into the canvas by using the method "Scroll Pane". The horizontal and
         * vertical scroll bars are being declared with the canvas being inside
         * of the Scroll Pane method which will then have the scroll bars
         * attached to the canvas.
         */

        Group root = new Group();
        sp = new ScrollPane(canvas);
        sp.setPrefSize(650, 650);
        sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
        root.getChildren().add(sp);
        System.out.println(canvas.getClass());
        // create a tabpane 
        TabPane tabpane = new TabPane();
        Tab tab1 = new Tab("Image 1");
        Tab tab2 = new Tab("Image 2");
        Tab tab3 = new Tab("Image 3");
        Tab tab4 = new Tab("Image 4");
        Tab tab5 = new Tab("Image 5");
        Tab tab6 = new Tab("Image 6");
        Tab tab7 = new Tab("Image 7");

        tabpane.getTabs().add(tab1);
        tabpane.getTabs().add(tab2);
        tabpane.getTabs().add(tab3);
        tabpane.getTabs().add(tab4);
        tabpane.getTabs().add(tab5);
        tabpane.getTabs().add(tab6);
        tabpane.getTabs().add(tab7);

        tab1.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab1.isSelected()) {
                    canvas = multiple[0];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                    System.out.println("hi");
                }
            }
        }
        );

        tab2.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab2.isSelected()) {
                    canvas = multiple[1];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                    System.out.println("bye");
                }
            }
        }
        );

        tab3.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab3.isSelected()) {
                    canvas = multiple[2];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                    System.out.println("hello");
                }
            }
        }
        );
        tab4.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab4.isSelected()) {
                    canvas = multiple[3];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                }
            }
        }
        );
        tab5.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab5.isSelected()) {
                    canvas = multiple[4];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                }
            }
        }
        );
        tab6.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab6.isSelected()) {
                    canvas = multiple[5];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                }
            }
        }
        );
        tab7.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab7.isSelected()) {
                    canvas = multiple[6];
                    gc = canvas.getGraphicsContext2D();
                    sp = new ScrollPane(canvas);
                    sp.setPrefSize(650, 650);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
                    maincanvas.add(sp, 2, 2);
                }
            }
        }
        );

        /**
         * The main scene is being created that holds all the grid pane elements
         * that is also set to the color white for the background
         */
        Scene scene1 = new Scene(main, 1000, 900);
        main.setStyle("-fx-background-color: white");
        primaryStage.setScene(scene1);
        primaryStage.show();

        /**
         * The menu method was used to hold the different menu items that will
         * be displayed on the menu bar with different names
         */
        Menu File = new Menu("File");
        Menu Help = new Menu("Help");
        Menu Edit = new Menu("Edit");
        Menu Tool = new Menu("Tool");

        /**
         * A MenuItem method was being used to create an Open button under the
         * "File" Menu Item.
         *
         * The Open button has an event handler that when the user clicks open,
         * the file chooser method is called to open up the file chooser that
         * can open up PNG files and display the images on the screen.
         */
        MenuItem Open = new MenuItem("Open");
        Open.setMnemonicParsing(
                true);
        Open.setAccelerator(
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        Open.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Open File");
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Images", "*.*"),
                        new FileChooser.ExtensionFilter("PNG Files", "*.png")
                );
                File file = fc.showOpenDialog(primaryStage);
                if (file != null) {
                    try { // resize iamge on canvas
                        Image pic = new Image(file.toURI().toString());
                        picture.setImage(pic);
                        picture.setPreserveRatio(true);
                        picture.setFitWidth(50);
                        picture.setSmooth(true);
                        picture.setCache(true);

                        BufferedImage img = ImageIO.read(file);
                        WritableImage image = SwingFXUtils.toFXImage(img, null);
                        GraphicsContext gc = canvas.getGraphicsContext2D();
                        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
                    } catch (Exception ex) {
                        System.out.println("Error");
                        Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Saving = false;
                }

            }
        }
        );
        /**
         * A MenuItem method was being used to create an Open Multiple button
         * under the "File" Menu Item.
         *
         * The Open Multiple button has an event handler that will call the
         * method FileChooser that will bring up the File chooser and can allow
         * the user to open multiple different images such as PNG, ICON, and JPG
         * files. These files will be open and projected onto the canvas.
         */
        MenuItem OpenMultiple = new MenuItem("Open Multiple");
        OpenMultiple.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Open Multiple Files");
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Documents", "*.*"),
                        new FileChooser.ExtensionFilter("Desktop", "*.*"),
                        new FileChooser.ExtensionFilter("Download", "*.*"),
                        new FileChooser.ExtensionFilter("All Images", "*.*"),
                        new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                        new FileChooser.ExtensionFilter("ICON Files", "*.png"),
                        new FileChooser.ExtensionFilter("JPG Files", ".jpg")
                );
                File file = fc.showOpenDialog(primaryStage);
                if (file != null) {
                    try { // resize iamge on canvas
                        Image pic = new Image(file.toURI().toString());
                        picture.setImage(pic);
                        picture.setPreserveRatio(true);
                        picture.setFitWidth(50);
                        picture.setSmooth(true);
                        picture.setCache(true);

                        BufferedImage img = ImageIO.read(file);
                        WritableImage image = SwingFXUtils.toFXImage(img, null);
                        GraphicsContext gc = canvas.getGraphicsContext2D();
                        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
                    } catch (Exception ex) {
                        System.out.println("Error");
                        Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Saving = false;
                }

            }
        }
        );
        /**
         * A MenuItem method was being used to create a Save button under the
         * "File" Menu Item.
         *
         * The Save button has an event handler that when the user clicks
         * "Save", the image that the user has choose and modify will be saved.
         */

        MenuItem Save = new MenuItem("Save");

        Save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        Save.setMnemonicParsing(true);
        Save.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (file != null) {
                    try {
                        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                        canvas.snapshot(null, wi);
                        RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                        ImageIO.write(ri, "png", file);
                    } catch (IOException ex) {
                        Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Saving = true;
                }
            }
        });
        /**
         * A MenuItem method was being used to create a Save as button under the
         * "File" Menu Item.
         *
         * The Save As button has an event handler that when the user clicks
         * Save as, the FileChooser method will be called that will open up the
         * filechooser screen and the user will name the File with various files
         * to save as by using the filechooser extension filter. This will allow
         * the image that the user has modified to be save as another name.
         */
        MenuItem SaveAs = new MenuItem("Save As...");
        // File Chooser to Save as button to work
        SaveAs.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Save As...");
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Images", "*.*"),
                        new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                        new FileChooser.ExtensionFilter("ICON Files", "*.png"),
                        new FileChooser.ExtensionFilter("JPG Files", ".jpg"),
                        new FileChooser.ExtensionFilter("Documents", "*.*"),
                        new FileChooser.ExtensionFilter("Desktop", "*.*"),
                        new FileChooser.ExtensionFilter("Download", "*.*")
                );
                File save = fc.showSaveDialog(primaryStage);
                if (save != null) {
                    try {
                        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                        canvas.snapshot(null, wi);
                        RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                        ImageIO.write(ri, "png", save);
                        file = save;
                        saved_file = save;
                    } catch (IOException ex) {
                        Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Saving = true;
                }
            }

        }
        );
        /**
         * A MenuItem method was being used to create a ShortCut button under
         * the "Help" Menu Item.
         *
         * The Short Cut button has an event handler that when the user clicks
         * Short Cut, the Alert method will be called an a Information screen
         * will pop up and shows each shortcut that is used and to what tool
         * they are associated with.
         */
        MenuItem Short = new MenuItem("ShortCuts");
        Short.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Alert ShortCut = new Alert(Alert.AlertType.INFORMATION);

                ShortCut.setTitle("Information");
                ShortCut.setHeaderText("Shortcuts");
                ShortCut.setContentText("Open: Ctrl + O\nSave: Ctrl + S \n"
                        + "Cut: Ctrl + X\nPaste: Ctrl + V\nCopy: Ctrl + C\n"
                        + "Undo: Ctrl + Z\nRedo: Ctrl + Y");
                ShortCut.showAndWait();

            }
        });
        /**
         * * A MenuItem method was being used to create a Notes button under
         * the "Help" Menu Item.
         *
         * The Notes button has an event handler so when the user Clicks Notes,
         * the FileChooser method will be called displaying the File Chooser
         * screen and will only allow the user to open TXT files that lead to
         * the release notes.
         */
        MenuItem Notes = new MenuItem("Notes");
        Notes.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("TXT files (*.TXT)", "*.TXT"),
                        new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt"));
                File file = fc.showOpenDialog(null);
                if (file != null) {
                    try {
                        Files.lines(file.toPath()).forEach(System.out::println);
                    } catch (Exception ex) {
                        Logger.getLogger(JavaFXReadTextFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        /**
         * A MenuItem method was being used to create an About button under the
         * "Help" Menu Item.
         *
         * The About button has an event handler that when the user clicks
         * About, the Alert method is called and an information screen will be
         * displayed and tells the user how to use the various tools that the
         * application provides.
         */
        MenuItem About = new MenuItem("About");
        About.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Alert ShortCut = new Alert(Alert.AlertType.INFORMATION);

                ShortCut.setTitle("Information");
                ShortCut.setHeaderText("Using Paint");
                ShortCut.setContentText("* Toggle buttons are used to display the "
                        + "various shapes, text, and eraser.\n\n* Help Menu Item"
                        + "provides access to release notes and shortcuts \n\n"
                        + "* Tool Menu Item provides the Color Dropper and Zoom"
                        + "button\n\n* Edit Menu Item allows the user to redo and "
                        + "undo\n\n* There are two different color chooser that are "
                        + "labeled for the Fill and Line color of the shapes\n\n"
                        + "* Slider Bar is also provided to allow the user to "
                        + "have different widths of the line.");
                ShortCut.showAndWait();

            }
        });

        /**
         * Multiple MenuItems are being names with no actions tied to them
         * underneath the Edit Menu method with key combinations
         */
        MenuItem Cut = new MenuItem("Cut");
        Cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        MenuItem Paste = new MenuItem("Paste");
        Paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        MenuItem Copy = new MenuItem("Copy");
        Copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));

        /**
         * A MenuItem method was being used to create an Undo button under the
         * "Edit" Menu Item.
         *
         * An undo stack is being used to have the undo button function that can
         * undo the shapes
         */
        MenuItem Undo = new MenuItem("Undo");
        Undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        // Undo button
        Undo.setOnAction(e -> {
            if (!undoHistory.empty()) {
                gc.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if (removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(gc.getFill());
                    tempLine.setStroke(gc.getStroke());
                    tempLine.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));

                } else if (removedShape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) removedShape;
                    tempRect.setFill(gc.getFill());
                    tempRect.setStroke(gc.getStroke());
                    tempRect.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(gc.getLineWidth());
                    tempCirc.setFill(gc.getFill());
                    tempCirc.setStroke(gc.getStroke());
                    redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (removedShape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) removedShape;
                    tempElps.setFill(gc.getFill());
                    tempElps.setStroke(gc.getStroke());
                    tempElps.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastRedo = redoHistory.lastElement();
                lastRedo.setFill(removedShape.getFill());
                lastRedo.setStroke(removedShape.getStroke());
                lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
                undoHistory.pop();

                for (int i = 0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if (shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    } else if (shape.getClass() == Rectangle.class) {
                        Rectangle temp = (Rectangle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    } else if (shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    } else if (shape.getClass() == Ellipse.class) {
                        Ellipse temp = (Ellipse) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    }
                    Saving = false;
                }
            } else {
                System.out.println("there is no action to undo");
            }
        });
        /**
         * A MenuItem method was being used to create an Redo button under the
         * "Edit" Menu Item.
         *
         * An Redo stack is being used to have the undo button function that can
         * Redo the shapes
         */
        MenuItem Redo = new MenuItem("Redo");
        Redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        Redo.setOnAction(e -> {
            if (!redoHistory.empty()) {
                Shape shape = redoHistory.lastElement();
                gc.setLineWidth(shape.getStrokeWidth());
                gc.setStroke(shape.getStroke());
                gc.setFill(shape.getFill());

                redoHistory.pop();
                if (shape.getClass() == Line.class) {
                    Line tempLine = (Line) shape;
                    gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                    undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) shape;
                    gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                    undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (shape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) shape;

                    gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                    undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) shape;
                    gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                    gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                    undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastUndo = undoHistory.lastElement();
                lastUndo.setFill(gc.getFill());
                lastUndo.setStroke(gc.getStroke());
                lastUndo.setStrokeWidth(gc.getLineWidth());
            } else {
                System.out.println("there is no action to redo");
            }
        });

        SeparatorMenuItem separator = new SeparatorMenuItem();

        /**
         * A MenuItem method was being used to create an Exit button under the
         * "File" Menu Item.
         *
         * The exit button implements an event listener that when pressing the
         * exit button, it will exit the program as a whole. Also, when the user
         * has already modified an image and forgets to save the image but
         * presses the exit button, an alert will pop up asking if the user
         * wants to save their image or exit completely without saving
         */
        MenuItem Exit = new MenuItem("Exit", null);

        Exit.setMnemonicParsing(
                true);
        // Ctrl + X
        Exit.setAccelerator(
                new KeyCodeCombination(KeyCode.ESCAPE));
        Exit.setOnAction(e -> {
            if (Saving == true) {
                Platform.exit();
                System.exit(0);
            } else {
                Alert exit = new Alert(AlertType.CONFIRMATION);
                exit.setTitle("File has not been Saved");
                String text = "Would you like to save?";
                exit.setContentText(text);

                Optional<ButtonType> show = exit.showAndWait();

                if ((show.isPresent()) && (show.get() == ButtonType.OK)) {
                    //for some reason file is null
                    try {
                        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                        canvas.snapshot(null, wi);
                        RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                        ImageIO.write(ri, "png", saved_file);
                    } catch (IOException ex) {
                        Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //exits program
                    Platform.exit();
                    System.exit(0);
                } else {
                    //if cancel button is clicked
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
        /**
         * A MenuItem method was being used to create a Color Dropper button
         * under the "Tool" Menu Item.
         *
         * A colordropper item has been added to take the color of an image that
         * the user selects and shows what the color is by displaying the color
         * code onto the color chooser
         */
        MenuItem ColorDropper = new MenuItem("Color Dropper");

        ColorDropper.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        WritableImage wi = new WritableImage((int) canvas.getWidth(),
                                (int) canvas.getHeight());
                        SnapshotParameters parameters = new SnapshotParameters();
                        WritableImage snapshot = canvas.snapshot(parameters, wi);
                        PixelReader pixel = snapshot.getPixelReader();
                        cpFill.setValue(pixel.getColor((int) e.getX(), (int) e.getY()));
                        cpLine.setValue(pixel.getColor((int) e.getX(), (int) e.getY()));
                    }
                });
            }
        });
        /**
         * A MenuItem method was being used to create a Zoom button under the
         * "Tool" Menu Item.
         *
         * The zoom feature has the user type in an integer using the Text Input
         * Dialog method which displays what integers the user needs to use to
         * zoom in or out of the canvas.
         */
        MenuItem Zoom = new MenuItem("Zoom");
        Zoom.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                TextInputDialog zoom = new TextInputDialog("");
                zoom.setHeaderText("Enter Integers\n -1 = image upsidedown\n"
                        + "0 = ERROR\n 1 = Fit image to orginal size (Zoom out)\n"
                        + "2 - 10 = Zoom in");
                zoom.showAndWait();
                String z = zoom.getEditor().getText();
                int zoomRatio = Integer.parseInt(z);
                canvas.setScaleX(zoomRatio);
                canvas.setScaleY(zoomRatio);
            }
        });

        /**
         * Toggle Button method was created to store the Shapes inside the
         * buttons for the user to be allowed to choose a shape of their choice.
         * The button will turn from white to gray indicating what shape has
         * been selected
         */
        ToggleButton Line = new ToggleButton("Line");
        ToggleButton Rect = new ToggleButton("Rectangle");
        ToggleButton Circle = new ToggleButton("Circle");
        ToggleButton Ellipse = new ToggleButton("Ellipse");
        ToggleButton Text = new ToggleButton("Text");
        ToggleButton Draw = new ToggleButton("Pencil");
        ToggleButton Square = new ToggleButton("Square");
        ToggleButton Eraser = new ToggleButton("Eraser");
        ToggleButton Triangle = new ToggleButton("Triangle");
        ToggleButton Select = new ToggleButton("Select");
        ToggleButton Polysides = new ToggleButton("Polygon");
        ToggleButton Move = new ToggleButton("Move/Paste");
        ToggleButton Copys = new ToggleButton("Copy");

        ToggleButton[] toolsArr = {Draw, Line, Rect, Circle, Ellipse, Text,
            Square, Eraser, Triangle, Select, Polysides, Move, Copys};

        ToggleGroup tools = new ToggleGroup();

        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);
        }
        TextArea text = new TextArea();
        text.setPrefRowCount(1);

        Slider slider = new Slider(1, 50, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        Label lines = new Label("Line Color");
        Label fill = new Label("Fill Color");
        Label tickwidth = new Label("3.0");
        Label Tools = new Label("No Tool is Active");

        //New objects
        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();
        Rectangle square = new Rectangle();
        Rectangle selRect = new Rectangle();

        GraphicsContext pastegc = canvas.getGraphicsContext2D();
        GraphicsContext selectgc = canvas.getGraphicsContext2D();

        Draw.setTooltip(new Tooltip("This button allows you to free draw"));
        Rect.setTooltip(new Tooltip("Place a Rectangle"));
        Eraser.setTooltip(new Tooltip("Erase your horrible mistakes"));
        Line.setTooltip(new Tooltip("Place a stright line not a curvy one"));
        Triangle.setTooltip(new Tooltip("Place a triangle"));
        Text.setTooltip(new Tooltip("Type in anything you want"));
        Square.setTooltip(new Tooltip("Place a lesser cooler rectangle"));
        Circle.setTooltip(new Tooltip("Place a O"));
        Ellipse.setTooltip(new Tooltip("A cooler version of a circle"));
        Select.setTooltip(new Tooltip("Select the area that you wish"));

        /**
         * Mouse Press event is being used on the canvas for every toggle button
         * that is chosen with the resulting shape, it will get all the values
         * that are declared once that button was clicked
         */
        multiple[2].setOnMousePressed(e -> {
            if (Draw.isSelected()) {
                //Draw tool
                Tools.setText("Pencil is active");
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (Eraser.isSelected()) {
                //Eraser Tool
                Tools.setText("Eraser is active");
                gc.setStroke(Color.WHITE);
                //start new path to postition
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Line.isSelected()) {
                //Line tool
                Tools.setText("Line is active");
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());

            } else if (Rect.isSelected()) {
                //Rectangle tool
                Tools.setText("Rectangle is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (Square.isSelected()) {
                //Square tool
                Tools.setText("Square is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (Circle.isSelected()) {
                //Circle tool
                Tools.setText("Circle  is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            } else if (Triangle.isSelected()) {
                Tools.setText("Triangle is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                x0 = e.getX();
                y0 = e.getY();
            } else if (Ellipse.isSelected()) {
                //Ellipse tool
                Tools.setText("Ellipse is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
            } else if (Text.isSelected()) {
                //Text tool
                Tools.setText("Text is active");
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                gc.fillText(text.getText(), e.getX(), e.getY());
                gc.strokeText(text.getText(), e.getX(), e.getY());
            } //Select
            else if (Select.isSelected()) {
                //preliminary writing setup
                //start selection at press
                selRect.setX(e.getX());
                selRect.setY(e.getY());
                Tools.setText("Select");
            } else if (Polysides.isSelected()) {
                //preliminary writing setup
                //start polygon at press
                polyStartX = e.getX();
                polyStartY = e.getY();
                Tools.setText("Polygon");
            } else if (Copys.isSelected()) {
                //preliminary writing setup
                //start selection at press
                selRect.setX(e.getX());
                selRect.setY(e.getY());
                Tools.setText("Copy");
            }
            Saving = false;
        });
        /**
         * Mouse Dragged event is being used to drag each selected shape to show
         * the process of the shape that the user wants
         */
        multiple[2].setOnMouseDragged(e -> {

            if (Draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Eraser.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.setStroke(Color.WHITE);
                //line to mouse
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Line.isSelected()) {
                //Line is selected 
                gc.drawImage(tmpSnap, 0, 0);
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            } else if (Rect.isSelected()) {
                //Rectangle is selected
                gc.drawImage(tmpSnap, 0, 0);

                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            } else if (Square.isSelected()) {
               //Square is selected
                gc.drawImage(tmpSnap, 0, 0);

                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            } else if (Triangle.isSelected()) {

                gc.drawImage(tmpSnap, 0, 0);
                double point3X = e.getX();
                double point3Y = e.getY();
                double point1X = (x0 + point3X) / 2;
                double point1Y = y0;
                double point2Y = point3Y;
                double point2X = x0;

                double[] xpoints = {point1X, point2X, point3X};
                double[] ypoints = {point1Y, point2Y, point3Y};

                gc.fillPolygon(xpoints, ypoints, 3);
                gc.strokePolygon(xpoints, ypoints, 3);

            } else if (Circle.isSelected()) {
                //Circle is selected 
                gc.drawImage(tmpSnap, 0, 0);
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }
                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

            } else if (Ellipse.isSelected()) {
                //Ellipse is selected 
                gc.drawImage(tmpSnap, 0, 0);
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

            } else if (Polysides.isSelected()) {
                //take snapshot for shape preview
                gc.drawImage(tmpSnap, 0, 0);
                numSides = (int) polyint.getValue();
                double radius = ((Math.abs(e.getX() - polyStartX) + Math.abs(e.getY() - polyStartY)) / 2);
                //checks if it is dragged the other direction
                if (polyStartX > e.getX()) {
                    polyStartX = e.getX();
                }
                if (polyStartY > e.getY()) {
                    polyStartY = e.getY();
                }
                //new array for sides
                double[] xSides = new double[numSides];
                double[] ySides = new double[numSides];
                //create sides
                for (int i = 0; i < numSides; i++) {
                    xSides[i] = radius * Math.cos(2 * i * Math.PI / numSides) + polyStartX;
                    ySides[i] = radius * Math.sin(2 * i * Math.PI / numSides) + polyStartY;
                }

                gc.strokePolygon(xSides, ySides, numSides);
                gc.fillPolygon(xSides, ySides, numSides);
            } //Move
            else if (Move.isSelected()) {
                gc.drawImage(tmpSnap, 0, 0);
                gc.drawImage(selImg, e.getX(), e.getY());
                Tools.setText("Move/Paste");
            }
            Saving = false;
        });
        /**
         * Mouse Released method was used on the canvas to present the final
         * shape that the user has chosen. Each shape has their own method to
         * display the shape.
         */
        multiple[2].setOnMouseReleased(e -> {
            //Draw is seleceted 
            tmpSnap = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, tmpSnap);
            if (Draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            } else if (Eraser.isSelected()) {
                // Eraser is selected
                gc.closePath();
            } else if (Line.isSelected()) {
                //Line is selected 
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            } else if (Rect.isSelected()) {
                //Rectangle is selected
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (Square.isSelected()) {
                //Rectangle is selected
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (Triangle.isSelected()) {
                double point3X = e.getX();
                double point3Y = e.getY();
                double point1X = (x0 + point3X) / 2;
                double point1Y = y0;
                double point2Y = point3Y;
                double point2X = x0;

                double[] xpoints = {point1X, point2X, point3X};
                double[] ypoints = {point1Y, point2Y, point3Y};

                gc.fillPolygon(xpoints, ypoints, 3);
                gc.strokePolygon(xpoints, ypoints, 3);

                undoHistory.push(new Polygon(e.getX(), e.getY()));
                redoHistory.push(new Polygon(e.getX(), e.getY()));
            } else if (Circle.isSelected()) {
                //Circle is selected 
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            } else if (Ellipse.isSelected()) {
                //Ellipse is selected 
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
            } else if (Polysides.isSelected()) {
                numSides = (int) polyint.getValue();
                double radius = ((Math.abs(e.getX() - polyStartX) + Math.abs(e.getY() - polyStartY)) / 2);
                //checks if it is dragged the other direction
                if (polyStartX > e.getX()) {
                    polyStartX = e.getX();
                }
                if (polyStartY > e.getY()) {
                    polyStartY = e.getY();
                }
                //new array for sides
                double[] xSides = new double[numSides];
                double[] ySides = new double[numSides];
                //apply sides to polygon
                for (int i = 0; i < numSides; i++) {
                    xSides[i] = radius * Math.cos(2 * i * Math.PI / numSides) + polyStartX;
                    ySides[i] = radius * Math.sin(2 * i * Math.PI / numSides) + polyStartY;
                }
                //draw polygon
                gc.strokePolygon(xSides, ySides, numSides);
                gc.fillPolygon(xSides, ySides, numSides);
                //push snap for undo
            }//Select
            else if (Select.isSelected()) {
                selRect.setWidth(Math.abs(e.getX() - selRect.getX()));
                selRect.setHeight(Math.abs(e.getY() - selRect.getY()));
                //checks if it is dragged the other direction
                if (selRect.getX() > e.getX()) {
                    selRect.setX(e.getX());
                }
                if (selRect.getY() > e.getY()) {
                    selRect.setY(e.getY());
                }
                //get a new snap
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //fill selImg with new selection
                gc.fillRect(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight());
                PixelReader pixelReader = writableImage.getPixelReader();
                selImg = new WritableImage(pixelReader, (int) selRect.getX(), (int) selRect.getY(), (int) selRect.getWidth(), (int) selRect.getHeight());
                //clear selected area
                gc.clearRect(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight());
                //enable move button
                Move.setDisable(false);
                //push snap for undo
            } //Copy
            else if (Copys.isSelected()) {
                selRect.setWidth(Math.abs(e.getX() - selRect.getX()));
                selRect.setHeight(Math.abs(e.getY() - selRect.getY()));
                //checks if it is dragged the other direction
                if (selRect.getX() > e.getX()) {
                    selRect.setX(e.getX());
                }
                if (selRect.getY() > e.getY()) {
                    selRect.setY(e.getY());
                }
                //get a new snap
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //fill selImg with new copy area
                PixelReader pixelReader = writableImage.getPixelReader();
                selImg = new WritableImage(pixelReader, (int) selRect.getX(), (int) selRect.getY(), (int) selRect.getWidth(), (int) selRect.getHeight());
                //enable move button
                Move.setDisable(false);
                //push snap for undo
            } else if (Move.isSelected()) {
                gc.drawImage(selImg, e.getX(), e.getY());
            }
            Saving = false;
            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());

        });
        
        multiple[1].setOnMousePressed(e -> {
            if (Draw.isSelected()) {
                //Draw tool
                Tools.setText("Pencil is active");
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (Eraser.isSelected()) {
                //Eraser Tool
                Tools.setText("Eraser is active");
                gc.setStroke(Color.WHITE);
                //start new path to postition
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Line.isSelected()) {
                //Line tool
                Tools.setText("Line is active");
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());

            } else if (Rect.isSelected()) {
                //Rectangle tool
                Tools.setText("Rectangle is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (Square.isSelected()) {
                //Square tool
                Tools.setText("Square is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (Circle.isSelected()) {
                //Circle tool
                Tools.setText("Circle  is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            } else if (Triangle.isSelected()) {
                Tools.setText("Triangle is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                x0 = e.getX();
                y0 = e.getY();
            } else if (Ellipse.isSelected()) {
                //Ellipse tool
                Tools.setText("Ellipse is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
            } else if (Text.isSelected()) {
                //Text tool
                Tools.setText("Text is active");
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                gc.fillText(text.getText(), e.getX(), e.getY());
                gc.strokeText(text.getText(), e.getX(), e.getY());
            } //Select
            else if (Select.isSelected()) {
                //preliminary writing setup
                //start selection at press
                selRect.setX(e.getX());
                selRect.setY(e.getY());
                Tools.setText("Select");
            } else if (Polysides.isSelected()) {
                //preliminary writing setup
                //start polygon at press
                polyStartX = e.getX();
                polyStartY = e.getY();
                Tools.setText("Polygon");
            } else if (Copys.isSelected()) {
                //preliminary writing setup
                //start selection at press
                selRect.setX(e.getX());
                selRect.setY(e.getY());
                Tools.setText("Copy");
            }
            Saving = false;
        });
        /**
         * Mouse Dragged event is being used to drag each selected shape to show
         * the process of the shape that the user wants
         */
        multiple[1].setOnMouseDragged(e -> {

            if (Draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Eraser.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.setStroke(Color.WHITE);
                //line to mouse
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Line.isSelected()) {
                //Line is selected 
                gc.drawImage(tmpSnap, 0, 0);
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            } else if (Rect.isSelected()) {
                //Rectangle is selected
                gc.drawImage(tmpSnap, 0, 0);

                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            } else if (Square.isSelected()) {
               //Square is selected
                gc.drawImage(tmpSnap, 0, 0);

                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            } else if (Triangle.isSelected()) {

                gc.drawImage(tmpSnap, 0, 0);
                double point3X = e.getX();
                double point3Y = e.getY();
                double point1X = (x0 + point3X) / 2;
                double point1Y = y0;
                double point2Y = point3Y;
                double point2X = x0;

                double[] xpoints = {point1X, point2X, point3X};
                double[] ypoints = {point1Y, point2Y, point3Y};

                gc.fillPolygon(xpoints, ypoints, 3);
                gc.strokePolygon(xpoints, ypoints, 3);

            } else if (Circle.isSelected()) {
                //Circle is selected 
                gc.drawImage(tmpSnap, 0, 0);
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }
                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

            } else if (Ellipse.isSelected()) {
                //Ellipse is selected 
                gc.drawImage(tmpSnap, 0, 0);
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

            } else if (Polysides.isSelected()) {
                //take snapshot for shape preview
                gc.drawImage(tmpSnap, 0, 0);
                numSides = (int) polyint.getValue();
                double radius = ((Math.abs(e.getX() - polyStartX) + Math.abs(e.getY() - polyStartY)) / 2);
                //checks if it is dragged the other direction
                if (polyStartX > e.getX()) {
                    polyStartX = e.getX();
                }
                if (polyStartY > e.getY()) {
                    polyStartY = e.getY();
                }
                //new array for sides
                double[] xSides = new double[numSides];
                double[] ySides = new double[numSides];
                //create sides
                for (int i = 0; i < numSides; i++) {
                    xSides[i] = radius * Math.cos(2 * i * Math.PI / numSides) + polyStartX;
                    ySides[i] = radius * Math.sin(2 * i * Math.PI / numSides) + polyStartY;
                }

                gc.strokePolygon(xSides, ySides, numSides);
                gc.fillPolygon(xSides, ySides, numSides);
            } //Move
            else if (Move.isSelected()) {
                gc.drawImage(tmpSnap, 0, 0);
                gc.drawImage(selImg, e.getX(), e.getY());
                Tools.setText("Move/Paste");
            }
            Saving = false;
        });
        /**
         * Mouse Released method was used on the canvas to present the final
         * shape that the user has chosen. Each shape has their own method to
         * display the shape.
         */
        multiple[1].setOnMouseReleased(e -> {
            //Draw is seleceted 
            tmpSnap = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, tmpSnap);
            if (Draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            } else if (Eraser.isSelected()) {
                // Eraser is selected
                gc.closePath();
            } else if (Line.isSelected()) {
                //Line is selected 
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            } else if (Rect.isSelected()) {
                //Rectangle is selected
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (Square.isSelected()) {
                //Rectangle is selected
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (Triangle.isSelected()) {
                double point3X = e.getX();
                double point3Y = e.getY();
                double point1X = (x0 + point3X) / 2;
                double point1Y = y0;
                double point2Y = point3Y;
                double point2X = x0;

                double[] xpoints = {point1X, point2X, point3X};
                double[] ypoints = {point1Y, point2Y, point3Y};

                gc.fillPolygon(xpoints, ypoints, 3);
                gc.strokePolygon(xpoints, ypoints, 3);

                undoHistory.push(new Polygon(e.getX(), e.getY()));
                redoHistory.push(new Polygon(e.getX(), e.getY()));
            } else if (Circle.isSelected()) {
                //Circle is selected 
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            } else if (Ellipse.isSelected()) {
                //Ellipse is selected 
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
            } else if (Polysides.isSelected()) {
                numSides = (int) polyint.getValue();
                double radius = ((Math.abs(e.getX() - polyStartX) + Math.abs(e.getY() - polyStartY)) / 2);
                //checks if it is dragged the other direction
                if (polyStartX > e.getX()) {
                    polyStartX = e.getX();
                }
                if (polyStartY > e.getY()) {
                    polyStartY = e.getY();
                }
                //new array for sides
                double[] xSides = new double[numSides];
                double[] ySides = new double[numSides];
                //apply sides to polygon
                for (int i = 0; i < numSides; i++) {
                    xSides[i] = radius * Math.cos(2 * i * Math.PI / numSides) + polyStartX;
                    ySides[i] = radius * Math.sin(2 * i * Math.PI / numSides) + polyStartY;
                }
                //draw polygon
                gc.strokePolygon(xSides, ySides, numSides);
                gc.fillPolygon(xSides, ySides, numSides);
                //push snap for undo
            }//Select
            else if (Select.isSelected()) {
                selRect.setWidth(Math.abs(e.getX() - selRect.getX()));
                selRect.setHeight(Math.abs(e.getY() - selRect.getY()));
                //checks if it is dragged the other direction
                if (selRect.getX() > e.getX()) {
                    selRect.setX(e.getX());
                }
                if (selRect.getY() > e.getY()) {
                    selRect.setY(e.getY());
                }
                //get a new snap
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //fill selImg with new selection
                gc.fillRect(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight());
                PixelReader pixelReader = writableImage.getPixelReader();
                selImg = new WritableImage(pixelReader, (int) selRect.getX(), (int) selRect.getY(), (int) selRect.getWidth(), (int) selRect.getHeight());
                //clear selected area
                gc.clearRect(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight());
                //enable move button
                Move.setDisable(false);
                //push snap for undo
            } //Copy
            else if (Copys.isSelected()) {
                selRect.setWidth(Math.abs(e.getX() - selRect.getX()));
                selRect.setHeight(Math.abs(e.getY() - selRect.getY()));
                //checks if it is dragged the other direction
                if (selRect.getX() > e.getX()) {
                    selRect.setX(e.getX());
                }
                if (selRect.getY() > e.getY()) {
                    selRect.setY(e.getY());
                }
                //get a new snap
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //fill selImg with new copy area
                PixelReader pixelReader = writableImage.getPixelReader();
                selImg = new WritableImage(pixelReader, (int) selRect.getX(), (int) selRect.getY(), (int) selRect.getWidth(), (int) selRect.getHeight());
                //enable move button
                Move.setDisable(false);
                //push snap for undo
            } else if (Move.isSelected()) {
                gc.drawImage(selImg, e.getX(), e.getY());
            }
            Saving = false;
            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());

        });
        
        multiple[0].setOnMousePressed(e -> {
            if (Draw.isSelected()) {
                //Draw tool
                Tools.setText("Pencil is active");
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (Eraser.isSelected()) {
                //Eraser Tool
                Tools.setText("Eraser is active");
                gc.setStroke(Color.WHITE);
                //start new path to postition
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Line.isSelected()) {
                //Line tool
                Tools.setText("Line is active");
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());

            } else if (Rect.isSelected()) {
                //Rectangle tool
                Tools.setText("Rectangle is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (Square.isSelected()) {
                //Square tool
                Tools.setText("Square is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (Circle.isSelected()) {
                //Circle tool
                Tools.setText("Circle  is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            } else if (Triangle.isSelected()) {
                Tools.setText("Triangle is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                x0 = e.getX();
                y0 = e.getY();
            } else if (Ellipse.isSelected()) {
                //Ellipse tool
                Tools.setText("Ellipse is active");
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
            } else if (Text.isSelected()) {
                //Text tool
                Tools.setText("Text is active");
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                gc.fillText(text.getText(), e.getX(), e.getY());
                gc.strokeText(text.getText(), e.getX(), e.getY());
            } //Select
            else if (Select.isSelected()) {
                //preliminary writing setup
                //start selection at press
                selRect.setX(e.getX());
                selRect.setY(e.getY());
                Tools.setText("Select");
            } else if (Polysides.isSelected()) {
                //preliminary writing setup
                //start polygon at press
                polyStartX = e.getX();
                polyStartY = e.getY();
                Tools.setText("Polygon");
            } else if (Copys.isSelected()) {
                //preliminary writing setup
                //start selection at press
                selRect.setX(e.getX());
                selRect.setY(e.getY());
                Tools.setText("Copy");
            }
            Saving = false;
        });
        /**
         * Mouse Dragged event is being used to drag each selected shape to show
         * the process of the shape that the user wants
         */
        multiple[0].setOnMouseDragged(e -> {

            if (Draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Eraser.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.setStroke(Color.WHITE);
                //line to mouse
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (Line.isSelected()) {
                //Line is selected 
                gc.drawImage(tmpSnap, 0, 0);
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            } else if (Rect.isSelected()) {
                //Rectangle is selected
                gc.drawImage(tmpSnap, 0, 0);

                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            } else if (Square.isSelected()) {
               //Square is selected
                gc.drawImage(tmpSnap, 0, 0);

                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            } else if (Triangle.isSelected()) {

                gc.drawImage(tmpSnap, 0, 0);
                double point3X = e.getX();
                double point3Y = e.getY();
                double point1X = (x0 + point3X) / 2;
                double point1Y = y0;
                double point2Y = point3Y;
                double point2X = x0;

                double[] xpoints = {point1X, point2X, point3X};
                double[] ypoints = {point1Y, point2Y, point3Y};

                gc.fillPolygon(xpoints, ypoints, 3);
                gc.strokePolygon(xpoints, ypoints, 3);

            } else if (Circle.isSelected()) {
                //Circle is selected 
                gc.drawImage(tmpSnap, 0, 0);
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }
                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

            } else if (Ellipse.isSelected()) {
                //Ellipse is selected 
                gc.drawImage(tmpSnap, 0, 0);
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

            } else if (Polysides.isSelected()) {
                //take snapshot for shape preview
                gc.drawImage(tmpSnap, 0, 0);
                numSides = (int) polyint.getValue();
                double radius = ((Math.abs(e.getX() - polyStartX) + Math.abs(e.getY() - polyStartY)) / 2);
                //checks if it is dragged the other direction
                if (polyStartX > e.getX()) {
                    polyStartX = e.getX();
                }
                if (polyStartY > e.getY()) {
                    polyStartY = e.getY();
                }
                //new array for sides
                double[] xSides = new double[numSides];
                double[] ySides = new double[numSides];
                //create sides
                for (int i = 0; i < numSides; i++) {
                    xSides[i] = radius * Math.cos(2 * i * Math.PI / numSides) + polyStartX;
                    ySides[i] = radius * Math.sin(2 * i * Math.PI / numSides) + polyStartY;
                }

                gc.strokePolygon(xSides, ySides, numSides);
                gc.fillPolygon(xSides, ySides, numSides);
            } //Move
            else if (Move.isSelected()) {
                gc.drawImage(tmpSnap, 0, 0);
                gc.drawImage(selImg, e.getX(), e.getY());
                Tools.setText("Move/Paste");
            }
            Saving = false;
        });
        /**
         * Mouse Released method was used on the canvas to present the final
         * shape that the user has chosen. Each shape has their own method to
         * display the shape.
         */
        multiple[0].setOnMouseReleased(e -> {
            //Draw is seleceted 
            tmpSnap = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, tmpSnap);
            if (Draw.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            } else if (Eraser.isSelected()) {
                // Eraser is selected
                gc.closePath();
            } else if (Line.isSelected()) {
                //Line is selected 
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            } else if (Rect.isSelected()) {
                //Rectangle is selected
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (Square.isSelected()) {
                //Rectangle is selected
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());

                }
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (Triangle.isSelected()) {
                double point3X = e.getX();
                double point3Y = e.getY();
                double point1X = (x0 + point3X) / 2;
                double point1Y = y0;
                double point2Y = point3Y;
                double point2X = x0;

                double[] xpoints = {point1X, point2X, point3X};
                double[] ypoints = {point1Y, point2Y, point3Y};

                gc.fillPolygon(xpoints, ypoints, 3);
                gc.strokePolygon(xpoints, ypoints, 3);

                undoHistory.push(new Polygon(e.getX(), e.getY()));
                redoHistory.push(new Polygon(e.getX(), e.getY()));
            } else if (Circle.isSelected()) {
                //Circle is selected 
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            } else if (Ellipse.isSelected()) {
                //Ellipse is selected 
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
            } else if (Polysides.isSelected()) {
                numSides = (int) polyint.getValue();
                double radius = ((Math.abs(e.getX() - polyStartX) + Math.abs(e.getY() - polyStartY)) / 2);
                //checks if it is dragged the other direction
                if (polyStartX > e.getX()) {
                    polyStartX = e.getX();
                }
                if (polyStartY > e.getY()) {
                    polyStartY = e.getY();
                }
                //new array for sides
                double[] xSides = new double[numSides];
                double[] ySides = new double[numSides];
                //apply sides to polygon
                for (int i = 0; i < numSides; i++) {
                    xSides[i] = radius * Math.cos(2 * i * Math.PI / numSides) + polyStartX;
                    ySides[i] = radius * Math.sin(2 * i * Math.PI / numSides) + polyStartY;
                }
                //draw polygon
                gc.strokePolygon(xSides, ySides, numSides);
                gc.fillPolygon(xSides, ySides, numSides);
                //push snap for undo
            }//Select
            else if (Select.isSelected()) {
                selRect.setWidth(Math.abs(e.getX() - selRect.getX()));
                selRect.setHeight(Math.abs(e.getY() - selRect.getY()));
                //checks if it is dragged the other direction
                if (selRect.getX() > e.getX()) {
                    selRect.setX(e.getX());
                }
                if (selRect.getY() > e.getY()) {
                    selRect.setY(e.getY());
                }
                //get a new snap
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //fill selImg with new selection
                gc.fillRect(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight());
                PixelReader pixelReader = writableImage.getPixelReader();
                selImg = new WritableImage(pixelReader, (int) selRect.getX(), (int) selRect.getY(), (int) selRect.getWidth(), (int) selRect.getHeight());
                //clear selected area
                gc.clearRect(selRect.getX(), selRect.getY(), selRect.getWidth(), selRect.getHeight());
                //enable move button
                Move.setDisable(false);
                //push snap for undo
            } //Copy
            else if (Copys.isSelected()) {
                selRect.setWidth(Math.abs(e.getX() - selRect.getX()));
                selRect.setHeight(Math.abs(e.getY() - selRect.getY()));
                //checks if it is dragged the other direction
                if (selRect.getX() > e.getX()) {
                    selRect.setX(e.getX());
                }
                if (selRect.getY() > e.getY()) {
                    selRect.setY(e.getY());
                }
                //get a new snap
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //fill selImg with new copy area
                PixelReader pixelReader = writableImage.getPixelReader();
                selImg = new WritableImage(pixelReader, (int) selRect.getX(), (int) selRect.getY(), (int) selRect.getWidth(), (int) selRect.getHeight());
                //enable move button
                Move.setDisable(false);
                //push snap for undo
            } else if (Move.isSelected()) {
                gc.drawImage(selImg, e.getX(), e.getY());
            }
            Saving = false;
            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());

        });
        /**
         * Color Picker method was used to display the Line and Fill Color that
         * the chooser can pick to customize each shapes Line and Fill color.
         */
        cpLine.setOnAction(e -> {
            gc.setStroke(cpLine.getValue());
            Color c = cpLine.getValue();
        });
        cpFill.setOnAction(e -> {
            gc.setFill(cpFill.getValue());
            Color c = cpFill.getValue();
        });

        /**
         * Slider method was used to display a slider from 1-50 to be able to
         * change the opacity of the shapes, text, and pencil width
         */
        slider.valueProperty().addListener(e -> {
            double width = slider.getValue();
            if (Text.isSelected()) {
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                tickwidth.setText(String.format("%.1f", width));
                return;
            }
            tickwidth.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });

        Button timer = new Button("AutoSave");
        Label autosave = new Label("AutoSave off");
        Label countdown = new Label();
        countdown.setText(timeSeconds.toString());
        countdown.setTextFill(Color.RED);
        countdown.setStyle("-fx-font-size: 4em;");

        timer.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                //make this a toggle type button, if it is on turn it off, vice versa
                if (saveFlag == true && countdown != null) {
                    saveFlag = false;

                    autosave.setText("Autosave off");

                } else {
                    //set status label at the bottom of the screen
                    autosave.setText("Autosave on");
                    autosaveTimeline = new Timeline(new KeyFrame(Duration.seconds(15), new EventHandler<ActionEvent>() {
                        // update timerLabel
                        @Override
                        public void handle(ActionEvent event) {
                            saveFlag = true;
                            //Autosave Unit Test
                            System.out.println("Autosaved");
                            //save in the same location
                            //file1.renameTo(filename);

                            if (file != null) {
                                try {
                                    WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                                    canvas.snapshot(null, wi);
                                    RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                                    ImageIO.write(ri, "png", saved_file);
                                    Saving = true;
                                } catch (Exception ex) {
                                    Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                FileChooser fc = new FileChooser();
                                fc.setTitle("Save");
                                fc.getExtensionFilters().addAll(
                                        new FileChooser.ExtensionFilter("All Images", "*.*"),
                                        new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                                        new FileChooser.ExtensionFilter("ICON Files", "*.png"),
                                        new FileChooser.ExtensionFilter("JPG Files", ".jpg"),
                                        new FileChooser.ExtensionFilter("Documents", "*.*"),
                                        new FileChooser.ExtensionFilter("Desktop", "*.*"),
                                        new FileChooser.ExtensionFilter("Download", "*.*")
                                );
                                File save = fc.showSaveDialog(primaryStage);
                                if (save != null) {
                                    try {
                                        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                                        canvas.snapshot(null, wi);
                                        RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
                                        ImageIO.write(ri, "png", save);
                                        file = save;
                                        saved_file = save;
                                    } catch (IOException ex) {
                                        Logger.getLogger(Painters.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    Saving = true;
                                }

                            }

                        }
                    }));
                    autosaveTimeline.setCycleCount(Timeline.INDEFINITE);
                    autosaveTimeline.play();
                }
            }
        });

        /**
         * All the menu items are added to the menu bar to present an appealing
         * and organized bar on the top of the application
         */
        menuBar.getMenus().add(File);
        menuBar.getMenus().add(Help);
        menuBar.getMenus().add(Edit);
        menuBar.getMenus().add(Tool);

        /**
         * Each method is getting put towards one of the menu items on the menu
         * bar for an organized and easily to navigate application
         */
        File.getItems().add(Open);
        File.getItems().add(OpenMultiple);
        File.getItems().add(Save);
        File.getItems().add(SaveAs);
        File.getItems().add(separator);
        File.getItems().add(Exit);

        Help.getItems().add(Short);
        Help.getItems().add(Notes);
        Help.getItems().add(About);

        Edit.getItems().add(Undo);
        Edit.getItems().add(Redo);
        Edit.getItems().add(separator);
        Edit.getItems().add(Cut);
        Edit.getItems().add(Paste);
        Edit.getItems().add(Copy);

        Tool.getItems().add(ColorDropper);
        Tool.getItems().add(Zoom);

        /**
         * The grid methods that was declared at the top of the code are now
         * being used to implement every method onto the actual scene in an
         * organized way
         */
        main.setHgap(0);
        main.setVgap(-5);
        main.addRow(1, menuBar);
        main.addRow(2, tabpane);
        tabpane.setMinHeight(40);
        main.addRow(3, lower);
        main.addRow(4, maincanvas);
        main.addRow(4, mainpicture);
        main.addRow(5, bottom);
        //under mnubar
        lower.setVgap(-40);
        lower.setHgap(0);
        lower.addColumn(1, cpFill);
        lower.addColumn(1, fill);
        lower.addColumn(2, cpLine);
        lower.addColumn(2, lines);
        lower.addColumn(3, slider);
        lower.addColumn(3, tickwidth);
        //lower.addColumn(5,Tools);
        tickwidth.setMinHeight(30);
        lower.addColumn(5, Shapes);

        //add(name,column,row)
        Shapes.setHgap(25);
        Shapes.setVgap(5);
        Shapes.addRow(1, Circle);
        Shapes.addRow(2, Square);
        Shapes.add(Line, 2, 1);
        Shapes.add(Ellipse, 1, 1);
        Shapes.add(Draw, 1, 2);
        Shapes.add(Rect, 2, 2);
        Shapes.add(Text, 3, 1);
        Shapes.add(text, 3, 2);
        text.setMaxWidth(90);
        Shapes.add(Eraser, 4, 1);
        Shapes.add(Triangle, 4, 2);
        Shapes.add(Select, 2, 3);
        Shapes.add(Copys, 3, 3);
        Shapes.add(Move, 4, 3);
        Shapes.add(polyint, 0, 3);
        polyint.setMaxWidth(80);
        Shapes.add(Polysides, 1, 3);

        //Where the canvas and picture are used
        maincanvas.setHgap(100);
        maincanvas.setVgap(20);
        maincanvas.add(sp, 2, 2);

        mainpicture.setHgap(100);
        mainpicture.setVgap(20);
        mainpicture.add(picture, 2, 2);

        bottom.addRow(0, Tools);
        bottom.addRow(1, autosave);
        bottom.addRow(2, timer);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param e
     */
    private void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    private void mouseDragged(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    private void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     */
    private KeyFrame doSomething() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
