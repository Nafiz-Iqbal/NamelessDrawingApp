package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {

    private static MenuBar menuBar = new MenuBar();     // the menu bar
    private static GridPane toolbar = new GridPane();    // the toolbar
    private static ToggleGroup toolbarGroup = new ToggleGroup(); // toggle group for the tools, so that at most one tool can be selected at a time
    private static VBox toolbarHolder = new VBox(5);
    private static AnchorPane toolbarArea = new AnchorPane(toolbarHolder);
    private static Canvas canvas;
    private static Pane canvasHolder = new Pane();
    private static ScrollPane canvasSection = new ScrollPane(canvasHolder);  // scrollable pane for holding the canvas
    private static ScrollPane optionsArea = new ScrollPane();  // scrollable pane for holding the options panel
    private static BorderPane appBody = new BorderPane(canvasSection, null, optionsArea, null, toolbarArea);  // container for everything except the menu bar
    private static VBox root = new VBox(menuBar, appBody);  // root node; container for everything

    // Menus
    private static Menu menuFile = new Menu("File");
    private static Menu menuEdit = new Menu("Edit");
    private static Menu menuHelp = new Menu("Help");

    // File menu items
    private static MenuItem mitemNew = new MenuItem("New…");
    private static MenuItem mitemOpen = new MenuItem("Open…");
    private static MenuItem mitemSave = new MenuItem("Save");
    private static MenuItem mitemSaveAs = new MenuItem("Save As…");
    private static MenuItem mitemExit = new MenuItem("Exit");

    // Edit menu items
    // TODO

    // Help menu items
    // TODO

    // Drawing tools
    private static ToolButton toolbtnLine = new ToolButton("Line");
    private static ToolButton toolbtnArrow = new ToolButton("Arrow");
    private static ToolButton toolbtnPolygon = new ToolButton("Polygon");
    private static ToolButton toolbtnCircle = new ToolButton("Circle");
    private static ToolButton toolbtnSemicircle = new ToolButton("Semi-circle");
    private static ToolButton toolbtnArc = new ToolButton("Arc");
    private static ToolButton rubberbtc=new ToolButton("Erase");

    Point2D prevPoint;
    Line hintLine;
    Circle hintCircle;

    Stage window=new Stage();
    GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window=primaryStage;
        window.setTitle("NamelessDrawingApp");

        appBody.prefHeightProperty().bind(root.heightProperty().subtract(menuBar.heightProperty()));

        setupMenus();
        setupToolbar();
        setupOptionsArea();

        canvas = new Canvas(400, 300);

        gc = canvas.getGraphicsContext2D();


        //set Request for closing window
        window.setOnCloseRequest(e->{
            e.consume();
            confirmbox();
        });






        canvas.setOnMousePressed(e->{

            if(toolbtnLine.isSelected()){
                double x = e.getX(), y = e.getY();
                prevPoint = new Point2D(x, y);
                gc.fillOval(x - 2.5, y - 2.5, 5, 5);

            }
            if(toolbtnCircle.isSelected()){
                double x = e.getX(), y = e.getY();
                prevPoint = new Point2D(x, y);
                gc.fillOval(x - 2.5, y - 2.5, 5, 5);
            }

            if(rubberbtc.isSelected()){
                gc.setLineWidth(3.8);
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);

            }





        });
        canvas.setOnMouseDragged(e->{

            if (toolbtnLine.isSelected()){
                double x = e.getX(), y = e.getY();
                canvasHolder.getChildren().remove(hintLine);
                DoubleProperty mouseX = new SimpleDoubleProperty(e.getX());
                DoubleProperty mouseY = new SimpleDoubleProperty(e.getY());
                hintLine = new Line(prevPoint.getX(), prevPoint.getY(), mouseX.get(), mouseY.get());
                mouseX.addListener(ov -> hintLine.endXProperty().bind(mouseX));
                mouseY.addListener(ov -> hintLine.endYProperty().bind(mouseY));
                hintLine.setStroke(Color.BLUE);
                canvasHolder.getChildren().add(hintLine);

            }
            if(toolbtnCircle.isSelected()){
                double x = e.getX(), y = e.getY();
                canvasHolder.getChildren().remove(hintCircle);
                DoubleProperty radius = new SimpleDoubleProperty(prevPoint.distance(x, y));
                hintCircle = new Circle(prevPoint.getX(), prevPoint.getY(), radius.get());
                radius.addListener(ov -> hintCircle.radiusProperty().bind(radius));
                hintCircle.setStroke(Color.BLUE);
                hintCircle.setFill(null);
                canvasHolder.getChildren().add(hintCircle);
            }
            if(rubberbtc.isSelected()){
                gc.setLineWidth(3.8);
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);

            }



        });


        canvas.setOnMouseReleased(e->{

            if (toolbtnLine.isSelected()){
                double x = e.getX(), y = e.getY();
                gc.strokeLine(prevPoint.getX(), prevPoint.getY(), x, y);
                prevPoint = null;
                canvasHolder.getChildren().remove(hintLine);
                gc.fillOval(x - 2.5, y - 2.5, 5, 5);
            }
            if (toolbtnCircle.isSelected()){

                double x = e.getX(), y = e.getY();
                double radius = prevPoint.distance(x, y);
                gc.strokeOval(prevPoint.getX() - radius, prevPoint.getY() - radius, 2 * radius, 2 * radius);
                prevPoint = null;
                canvasHolder.getChildren().remove(hintCircle);
                gc.fillOval(x - 2.5, y - 2.5, 5, 5);


            }
            if(rubberbtc.isSelected()){
                gc.setLineWidth(3.8);
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);

            }




        });




        canvasHolder.setStyle(
                "-fx-background-color: #FFFFFF;"
        );
        canvasHolder.getChildren().add(canvas);

        Scene scene=new Scene(root,1000,500);

        // primaryStage.setScene(new Scene(root, 1000, 500));
        window.setScene(scene);
        window.show();
    }

    private void setupOptionsArea() {
        optionsArea.setTooltip(new Tooltip("Options"));
        optionsArea.prefHeightProperty().bind(appBody.heightProperty());


    }

    private void setupToolbar() {
        toolbar.getColumnConstraints().add(new ColumnConstraints(50)); // for 1st column in the toolbar
        toolbar.getColumnConstraints().add(new ColumnConstraints(50)); // for 2nd column
        toolbar.addColumn(0, toolbtnLine, toolbtnArrow, toolbtnPolygon,rubberbtc);
        toolbar.addColumn(1, toolbtnArc, toolbtnSemicircle, toolbtnCircle);
        Label toolsLabel = new Label("Tools");
        toolsLabel.setFont(new Font(18));
        toolbarHolder.getChildren().addAll(toolsLabel, toolbar);
        AnchorPane.setLeftAnchor(toolbarHolder, 20.0);
        AnchorPane.setTopAnchor(toolbarHolder, 20.0);
        AnchorPane.setRightAnchor(toolbarHolder, 20.0);
    }

    private void setupMenus() {
        menuFile.getItems().addAll(
                mitemNew,
                mitemOpen,
                mitemSave,
                mitemSaveAs,
                mitemExit);
        mitemNew.setOnAction(e -> showNewFileDialog());
        mitemExit.setOnAction(e -> Platform.exit());
        mitemSave.setOnAction((e)-> saveFile());
        mitemOpen.setOnAction((e)-> openFile());
        mitemSaveAs.setOnAction((e)->saveFile());
        menuBar.getMenus().addAll(
                menuFile,
                menuEdit,
                menuHelp
        );
    }

    /*------- Save & Open ------*/
    public void saveFile() {
        //Saving Process off File;\

        FileChooser saveFile=new FileChooser();
        saveFile.setTitle("Save");

        File file=saveFile.showSaveDialog(window);

        if(file!=null){

            try {
                WritableImage writableImage=new WritableImage(400,300);

                canvas.snapshot(null,writableImage);

                RenderedImage renderedImage= SwingFXUtils.fromFXImage(writableImage,null);

                ImageIO.write(renderedImage,"png",file);


            }catch (IOException ex){

                System.out.println("Error!");



            } }

    }
//FIle opening Process;

    private void openFile() {

        FileChooser openfile=new FileChooser();
        openfile.setTitle("Open");

        File file=openfile.showOpenDialog(window);

        try {
            InputStream inputStream=new FileInputStream(file);

            Image image=new Image(inputStream);
            gc.drawImage(image,0,0);

        }catch (IOException ex){
            System.out.println("Error!");
        }






    }







    private void showNewFileDialog() {
        Stage newFileDialog = new Stage();
        newFileDialog.setTitle("Create a new file");
        newFileDialog.setResizable(false);

        Label instruction = new Label("Enter the dimensions of the canvas in centimeters.");
        HBox topPane = new HBox(instruction);

        Label widthLabel = new Label("Width: ");
        Label multiplicationSign = new Label(" × ");
        Label heightLabel = new Label("Height: ");
        TextField widthField = new TextField();
        widthField.setPrefColumnCount(5);
        TextField heightField = new TextField();
        heightField.setPrefColumnCount(5);
        HBox middlePane = new HBox(widthLabel, widthField, multiplicationSign, heightLabel, heightField);
        middlePane.setAlignment(Pos.CENTER);

        Button btnCancel = new Button("Cancel");
        btnCancel.setCancelButton(true);
        btnCancel.setOnAction(e -> newFileDialog.close());

        Button btnCreate = new Button("Create");
        btnCreate.setDefaultButton(true);
        btnCreate.setOnAction(e -> {
            String width = widthField.getText(), height = heightField.getText();
            if (width.matches("^[1-9]\\d*") && height.matches("^[1-9]\\d*")) {  // check valid input using regex
                createCanvas(Integer.parseInt(width), Integer.parseInt(height));
                newFileDialog.close();
            }
        });
        HBox bottomPane = new HBox(10, btnCancel, btnCreate);
        bottomPane.setAlignment(Pos.CENTER_RIGHT);

        VBox dialogHolder = new VBox(20, topPane, middlePane, bottomPane);
        dialogHolder.setPadding(new Insets(10, 10, 10, 10));
        newFileDialog.setScene(new Scene(dialogHolder));
        newFileDialog.initModality(Modality.APPLICATION_MODAL);
        newFileDialog.show();
    }

    public void createCanvas(int width, int height) {
        canvas = new Canvas(width, height);

        // JavaFX dimensions are in points, and 1 cm = 28.3465 pt
//        canvas.setPrefWidth(width * 28.3465);
//        canvas.setPrefHeight(height * 28.3465);

        // Make the canvas white
        canvas.setStyle(
                "-fx-background-color: #FFFFFF;"
        );

        // Since canvasHolder is a StackPane, the canvas will automatically be centered within it.
        canvasHolder.getChildren().add(canvas);

        // But canvasHolder has to be manually centered within the scrollable canvasSection,
        // else the canvasHolder (and hence the canvas itself) appears at the top-left corner of
        // the scrollable canvasSection.
//        canvasHolder.prefWidthProperty().bind(canvasSection.widthProperty());
//        canvasHolder.prefHeightProperty().bind(canvasSection.heightProperty());
    }

    private static class ToolButton extends ToggleButton {
        ToolButton(String name) {
            super(name);
            this.setPrefWidth(50);
            this.setTooltip(new Tooltip(name));  // add a mouse-over tooltip that says the tool's name
            setToggleGroup(toolbarGroup);  // add it to the toolbar's toggle group
            this.setUserData(name);
        }
    }

    //confirm when the Application is closing; and ask do he want to save file or not;
    public  void confirmbox(){

        Stage confirmWindow=new Stage();
        confirmWindow.setTitle("NamelessDrawingApp");
        confirmWindow.initModality(Modality.APPLICATION_MODAL);

        Button ybutton=new Button("Save");
        ybutton.setOnAction(e->{
            fileSaveAndClose();
            confirmWindow.close();

        });

        Button nbutton=new Button("Don't Save");
        nbutton.setOnAction(e->{
            confirmWindow.close();
            window.close();
        });

        Label label=new Label("Do you want to save changes to File?");




        HBox lay=new HBox(5);
        lay.getChildren().addAll(ybutton, nbutton);

        VBox vBox=new VBox(10);
        vBox.getChildren().addAll(label,lay);

        lay.setAlignment(Pos.CENTER);
        Scene scene=new Scene(vBox,500,100);

        confirmWindow.setScene(scene);
        confirmWindow.showAndWait();


    }

    public void fileSaveAndClose() {

        FileChooser saveFile=new FileChooser();
        saveFile.setTitle("Save");

        File file=saveFile.showSaveDialog(window);

        if(file!=null){

            try {
                WritableImage writableImage=new WritableImage(400,300);

                canvas.snapshot(null,writableImage);

                RenderedImage renderedImage= SwingFXUtils.fromFXImage(writableImage,null);

                ImageIO.write(renderedImage,"png",file);
                window.close();


            }catch (IOException ex){

                System.out.println("Error!");



            } }



    }


    public static void main(String[] args) {
        launch(args);
    }
}

