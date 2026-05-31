/*
Gruppnummer:45
Abdullahi Hared abab1819
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

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setOnCloseRequest(e -> {
      if (!confirmDiscardChanges()) e.consume();
    });

    stage.setTitle("PathFinder");
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

  private void handleFindPath(){
      if (selected.size() != 2) {
          showError("Mark exactly two places before searching for a path.");
          return;
      }

      Place from = selected.get(0);
      Place to = selected.get(1);

      Path<Place> path = model.findPath(from, to);

      if (path == null) {
          showInfo("No path", "No path found between " + from + " and " + to + ".");
          return;
      }

      StringBuilder sb = new StringBuilder();
      sb.append("Path from ").append(from).append(" to ").append(to).append(":\n\n");

      Place current = path.getStart();

      for (Edge<Place> edge : path) {
          sb.append(current)
                  .append(" -> ")
                  .append(edge.getDestination())
                  .append(" by ")
                  .append(edge.getName())
                  .append(" takes ")
                  .append(edge.getWeight())
                  .append("\n");

          current = edge.getDestination();
      }

      sb.append("\nTotal travel time: ").append(path.getTotalWeight());

      showInfo("Path", sb.toString());

  }

  private void handleRemove() {
    //Check if selected only place
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
      if (selected.size() != 2) {
          showError("Mark exactly two places before creating a connection.");
          return;
      }

      Place a = selected.get(0);
      Place b = selected.get(1);

      if (model.getEdgeBetween(a, b) != null) {
          showError("There is already a connection between " + a + " and " + b + ".");
          return;
      }

      Optional<ConnectionInput> result = askForConnection(a, b);
      if (result.isEmpty()) return;

      ConnectionInput input = result.get();

      if (input.name.isEmpty()) {
          showError("Connection name cannot be empty.");
          return;
      }

      if (input.weight < 0) {
          showError("Weight cannot be negative.");
          return;
      }

      model.connect(a, b, input.name, input.weight);
      drawConnection(a, b);
      clearSelection();
  }


    private Optional<ConnectionInput> askForConnection(Place a, Place b) {
        Dialog<ConnectionInput> dialog = new Dialog<>();
        dialog.setTitle("New Connection");
        dialog.setHeaderText("Connection from " + a + " to " + b);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField weightField = new TextField();

        VBox box = new VBox(5);
        box.getChildren().addAll(
                new Label("Name:"),
                nameField,
                new Label("Travel time / weight:"),
                weightField
        );

        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) return null;

            String name = nameField.getText().trim();
            int weight;

            try {
                weight = Integer.parseInt(weightField.getText().trim());
            } catch (NumberFormatException e) {
                showError("Weight must be an integer.");
                return null;
            }

            return new ConnectionInput(name, weight);
        });

        return dialog.showAndWait();
    }


  private void handleExit() {
      if (confirmDiscardChanges()) {
          primaryStage.close();
      }
  }

  private void handleSave() {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Save");
      chooser.getExtensionFilters().add(
              new FileChooser.ExtensionFilter("Text files", "*.txt")
      );

      File file = chooser.showSaveDialog(primaryStage);
      if (file == null) return;

      try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
          if (model.getImagePath() != null) {
              out.println("image:" + model.getImagePath());
          }

          for (Place p : model.getPlaces()) {
              out.println("PLACE;" + p.getName() + ";" + p.getX() + ";" + p.getY());
          }

          for (ConnectionLine cl : connectionLines) {
              Edge<Place> edge = model.getEdgeBetween(cl.a, cl.b);

              if (edge != null) {
                  out.println("EDGE;" + cl.a.getName() + ";" + cl.b.getName()
                          + ";" + edge.getName() + ";" + edge.getWeight());
              }
          }

          model.resetChanges();
          showInfo("Saved", "File saved successfully.");

      } catch (IOException e) {
          showError("Could not save file: " + e.getMessage());
      }

  }

  private void handleOpen() {
      if (!confirmDiscardChanges()) return;

      FileChooser chooser = new FileChooser();
      chooser.setTitle("Open");
      chooser.getExtensionFilters().add(
              new FileChooser.ExtensionFilter("Text files", "*.txt")
      );

      File file = chooser.showOpenDialog(primaryStage);
      if (file == null) return;

      clearEverything();

      Map<String, Place> placesByName = new HashMap<>();

      try {
          List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));

          for (String line : lines) {
              if (line.startsWith("image:")) {
                  String path = line.substring("image:".length());
                  model.setImagePath(path);
                  showBackgroundImage(path);

              } else if (line.startsWith("PLACE;")) {
                  String[] parts = line.split(";");

                  String name = parts[1];
                  double x = Double.parseDouble(parts[2]);
                  double y = Double.parseDouble(parts[3]);

                  Place p = new Place(name, x, y);
                  model.addPlace(p);
                  drawPlace(p);
                  placesByName.put(name, p);

              } else if (line.startsWith("EDGE;")) {
                  String[] parts = line.split(";");

                  Place a = placesByName.get(parts[1]);
                  Place b = placesByName.get(parts[2]);
                  String edgeName = parts[3];
                  int weight = Integer.parseInt(parts[4]);

                  if (a != null && b != null) {
                      model.connect(a, b, edgeName, weight);
                      drawConnection(a, b);
                  }
              }
          }

          model.resetChanges();
          showInfo("Opened", "File opened successfully.");

      } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
          showError("Could not open file: " + e.getMessage());
      }

  }

  private void handleNewMap() {
      if (!confirmDiscardChanges()) return;

      FileChooser chooser = new FileChooser();
      chooser.setTitle("Open background image");
      chooser.getExtensionFilters().add(
              new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif")
      );

      File file = chooser.showOpenDialog(primaryStage);
      if (file == null) return;

      clearEverything();
      model.setImagePath(file.getAbsolutePath());
      showBackgroundImage(file.getAbsolutePath());
      model.resetChanges();
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


    String name = result.get().trim();
     //Verify
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
    Text label = new Text(p.getX() + 10, p.getY() + 4, p.getName());

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
      offset.distanceHorisontally = e.getX() - circle.getCenterX();
      offset.distanceVertically = e.getY() - circle.getCenterY();
    });

    //Event 2 Dragged
    circle.setOnMouseDragged(e -> {
      double newX = e.getX() - offset.distanceHorisontally;
      double newY = e.getY() - offset.distanceVertically;

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

  private void drawConnection(Place a, Place b) {
      Circle cicleA = placeCircles.get(a);
      Circle cicleB = placeCircles.get(b);

      if (cicleA == null || cicleB == null) {
          showError("Could not draw connection.");
          return;
      }

      Line line = new Line();

      line.startXProperty().bind(cicleA.centerXProperty());
      line.startYProperty().bind(cicleA.centerYProperty());
      line.endXProperty().bind(cicleB.centerXProperty());
      line.endYProperty().bind(cicleB.centerYProperty());

      line.setStrokeWidth(2);

      if (backgroundImage == null) {
          mapPane.getChildren().add(0, line);
      } else {
          mapPane.getChildren().add(1, line);
      }
      connectionLines.add(new ConnectionLine(a, b, line));
  }
  private void clearEverything() {
      mapPane.getChildren().clear();

      placeCircles.clear();
      placeLabels.clear();
      selected.clear();
      connectionLines.clear();

      backgroundImage = null;

      model.clear();
  }
  private void showBackgroundImage(String path) {
      Image image = new Image("file:" + path);
      backgroundImage = new ImageView(image);

      backgroundImage.setPreserveRatio(true);
      backgroundImage.setFitWidth(800);

      mapPane.getChildren().add(0, backgroundImage);
  }
  private boolean confirmDiscardChanges() {
      if (!model.hasUnsavedChanges()) {
          return true;
      }

      Alert alert = new Alert(
              Alert.AlertType.CONFIRMATION,
              "You have unsaved changes. Continue and discard them?",
              ButtonType.OK,
              ButtonType.CANCEL
      );

      alert.setTitle("Unsaved changes");
      alert.setHeaderText(null);

      Optional<ButtonType> result = alert.showAndWait();

      return result.isPresent() && result.get() == ButtonType.OK;
  }


  private static class ConnectionLine {
    Place a;
    Place b;
    Line line;
    ConnectionLine(Place a, Place b, javafx.scene.shape.Line line) {
      this.a = a; this.b = b; this.line = line;
    }
  }


    private static class ConnectionInput {
        String name;
        int weight;

        ConnectionInput(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
    }

  //Helper class to store mousepointers loc relative to circle
  private static class DragOffSet {
    double distanceHorisontally; //Distance hor
    double distanceVertically; //Distance vert
  }
}