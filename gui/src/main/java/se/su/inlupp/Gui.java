/*
Gruppnummer:45
Abdullahi Hared
Devran Cinar
Viktor Askergren
 */
package se.su.inlupp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
  }

  private void handleNewPlace() {
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
  }

  private void handleNewMap() {
    System.out.println("new map");
  }

  private void handleMapClick(double x, double y) { }
  private void drawPlace(Place p) { }
  private void handlePlaceClicked(Place p) { }
  private void clearSelection() { }
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
}