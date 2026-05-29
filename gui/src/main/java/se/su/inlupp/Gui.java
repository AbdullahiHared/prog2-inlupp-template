/*
Gruppnummer:45
Abdullahi Hared
Devran Cinar
Viktor Askergren
 */
package se.su.inlupp;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import javafx.scene.shape.Line;

public class Gui extends Application {

  private final RouteModel model = new RouteModel();
  private final Pane mapPane = new Pane();
  private final ComboBox<String> algorithmBox = new ComboBox<>();
  private Stage primaryStage;
  private boolean newPlaceMode = false;
  private final Map<Place, Circle> placeCircles = new HashMap<>();
  private final Map<Place, Text> placeLabels = new HashMap<>();
  private final List<Place> selected = new ArrayList<>();
  private final List<ConnectionLine> connectionLines = new ArrayList<>();
  private ImageView backgroundImage = null;


  @Override
  public void start(Stage stage) {
    this.primaryStage = stage;
    BorderPane root = new BorderPane();
    VBox topBox = new VBox();
    topBox.getChildren().addAll(buildMenuBar(), buildToolBar());

    root.setTop(topBox);
    root.setCenter(mapPane);
    mapPane.setStyle("-fx-background-color: #efefef;");
    mapPane.setOnMouseClicked(e -> handleMapClick(e.getX(), e.getY()));

    Scene scene = new Scene(root, 400, 400);
    stage.setScene(scene);
    stage.setOnCloseRequest(e -> {
      if (!confirmDiscardChanges()) e.consume();
    });
    stage.show();

  }

  private MenuBar buildMenuBar() {
    Menu fileMenu = new Menu("File");
    MenuItem newMapItem = new MenuItem("New map");
    MenuItem openItem = new MenuItem("Open");
    MenuItem saveItem = new MenuItem("Save");
    MenuItem exitItem = new MenuItem("Exit");

    fileMenu.getItems().addAll(newMapItem, openItem, saveItem, exitItem);
    newMapItem.setOnAction(e-> handleNewMap());
    openItem.setOnAction(e-> handleOpen());
    saveItem.setOnAction(e-> handleSave());
    exitItem.setOnAction(e-> handleExit());


    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(fileMenu);
    return menuBar;
  }

  private HBox buildToolBar() {
    Button findPath = new Button("Find path");
    Button newPlaceBtn = new Button("New place");
    Button removeBtn = new  Button("Remove");
    Button newConnBtn = new Button("New Connection");
    algorithmBox.getItems().addAll("BFS", "DFS");
    algorithmBox.setValue("BFS");

    algorithmBox.setOnAction(e-> handleAlgorithmChange());
    newConnBtn.setOnAction(e-> handleNewConnection());
    newPlaceBtn.setOnAction(e-> handleNewPlace());
    removeBtn.setOnAction(e-> handleRemove());
    findPath.setOnAction(e-> handleFindPath());
    HBox bar = new HBox(10);
    bar.getChildren().addAll( findPath, newConnBtn, newPlaceBtn, removeBtn, algorithmBox);
    bar.setStyle("-fx-padding:8");
    return bar;
  }

  private void handleAlgorithmChange() {
    String currentAlgorithm = algorithmBox.getValue();
    if(currentAlgorithm.equals("BFS")) {
      model.setPathFinder(new BFSPathFinder<>());
    } else if(currentAlgorithm.equals("DFS")) {
      model.setPathFinder(new DFSPathFinder<>());
    }
  }

  private void showError(String message) {
    Alert a = new Alert(Alert.AlertType.ERROR);
    a.setTitle("Error");
    a.setHeaderText(null);
    a.setContentText(message);
    a.showAndWait();
  }

  private void showInfo(String title, String message) {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setTitle(title);
    a.setHeaderText(null);
    a.setContentText(message);
    a.showAndWait();
  }

  private void handleFindPath() {
  }

  private void handleRemove() {
    //Checks
    if (selected.size() != 1) {
      showError("Mark one place to remove, not more");
      return;
    }

    //Get place
    Place p = selected.get(0);

    //Remove
    model.removePlace(p);
    mapPane.getChildren().remove(placeCircles.remove(p));
    mapPane.getChildren().remove(placeLabels.remove(p));

    //Create temp list for removal
    List<ConnectionLine> toRemove = new ArrayList<>();
    for (ConnectionLine cl : connectionLines) {
      if (cl.a == p || cl.b == p) toRemove.add(cl);
    }

    //Remove collected connections
    for (ConnectionLine cl : toRemove) {
      mapPane.getChildren().remove(cl.line);
      connectionLines.remove(cl);
    }

    //Clear marked spots
    selected.clear();

  }

  private void handleNewPlace() {
    newPlaceMode = true;
    mapPane.setCursor(Cursor.CROSSHAIR);
  }

  private void handleNewConnection() {
  }


  private void handleExit() {
    System.out.println("exit");
  }

  private void handleSave() {
    System.out.println("Save");

  }

  private void handleOpen() {
    System.out.println("open");
    FileChooser fileChooser = new FileChooser();
    File selectedFile = fileChooser.showOpenDialog(primaryStage);
  }

  private void handleNewMap() {
    System.out.println("new map");
  }

  private void handleMapClick(double x, double y) {
    if (!newPlaceMode) return;
    newPlaceMode = false;
    mapPane.setCursor(Cursor.DEFAULT);

    //Create text-window for user input
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Name");
    dialog.setHeaderText(null);
    dialog.setContentText("Name of place:");

    //Check if we got input
    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) return;

    //Verify
    String name = result.get().trim();
    if (name.isEmpty()) {
      showError("Place name cannot be empty.");
      return;
    }
    //Create & add new placeobj
    Place p = new Place(name, x, y);
    model.addPlace(p);
    drawPlace(p);
  }
  private void drawPlace(Place p) {
    Circle circle = new Circle(p.getX(), p.getY(), 8, Color.BLUE);
    Text label = new Text(p.getX(), p.getY() + 4, p.getName());

    //Add to map
    mapPane.getChildren().addAll(circle, label);
    //Save connections
    placeCircles.put(p, circle);
    placeLabels.put(p, label);

    //Activate drag
    enableDrag(p, circle, label);
    //Connect click to event
    circle.setOnMouseClicked(e -> handlePlaceClicked(p));
  }
  private void handlePlaceClicked(Place p) {
    //Get circle belonging to click
    Circle c = placeCircles.get(p);
    if (selected.contains(p)) {
      //If already marked
      selected.remove(p);
      c.setFill(Color.BLUE);
    } else {
      //Check if two spots not marked
      if (selected.size() >= 2) {
        showError("You can only mark two places, press marked place to unmark.");
        return;
      }
      selected.add(p);
      c.setFill(Color.RED);
    }
  }
  private void clearSelection() {

    //Loop all marked spots and clear
    for (Place p : selected) placeCircles.get(p).setFill(Color.BLUE);
    selected.clear();
  }

  private void enableDrag(Place p, Circle circle, Text label) {

    //Create offset obj
    DragOffSet offset = new DragOffSet();

    //Event 1 pressed
    circle.setOnMousePressed(e -> {
      offset.dx = e.getX() - circle.getCenterX();
      offset.dy = e.getY() - circle.getCenterY();
    });

    //Event 2 Dragged
    circle.setOnMouseDragged(e -> {
      double newX = e.getX() - offset.dx;
      double newY = e.getY() - offset.dy;

      //Move to new loc
      circle.setCenterX(newX);
      circle.setCenterY(newY);

      //Move label
      label.setX(newX + 10);
      label.setY(newY + 4);

      //Update pos
      p.setPosition(newX, newY);
    });
  }

  private void drawConnection(Place a, Place b) { }
  private void clearEverything() { }
  private void showBackgroundImage(String path) { }
  private boolean confirmDiscardChanges() { return true; }


  private static class ConnectionLine {
    Place a;
    Place b;
    Line line;
    ConnectionLine(Place a, Place b, javafx.scene.shape.Line line) {
      this.a = a; this.b = b; this.line = line;
    }
  }

  //Helper class to store mousepointers loc relative to circle
  private static class DragOffSet {
    double dx; //Distance hor
    double dy; //Distance vert
  }
}