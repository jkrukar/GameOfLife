import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Public class GameOfLife
 * extends Application
 * Creates an implementation of Conway's Game of Life in 3D
 */
public class GameOfLife extends Application
{

    private static int R1 = 3;
    private static int R2 = 3;
    private static int R3 = 3;
    private static int R4 = 2;
    private static final double CAMERA_INITIAL_DISTANCE = -1500;
    private static final double CAMERA_INITIAL_X_POS = 225.0;
    private static final double CAMERA_INITIAL_Y_POS = 225.0;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    private BorderPane borderPane;
    private final Group root = new Group();
    private final Xform sceneXform = new Xform();
    private final Xform cellGroupXform = new Xform();
    private CellManager cellManager;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private Menu menuPresets;
    protected RadioMenuItem random;
    protected RadioMenuItem preset2D;
    protected RadioMenuItem pokeball;
    protected RadioMenuItem mazeCube;
    protected RadioMenuItem twoToad;
    protected RadioMenuItem pentaDecathlon;
    private CheckMenuItem xRotation;
    private CheckMenuItem yRotation;
    private CheckMenuItem zRotation;
    private Button playButton;
    private Button resetButton;
    protected ChoiceBox choiceR1;
    protected ChoiceBox choiceR2;
    protected ChoiceBox choiceR3;
    protected ChoiceBox choiceR4;
    protected Label generationVal;
    protected Label densityVal;
    private boolean isPlaying = true;

    private void buildCamera()
    {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.t.setX(CAMERA_INITIAL_X_POS);
        cameraXform.t.setY(CAMERA_INITIAL_Y_POS);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void handleKeyboard(Scene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z:
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                }
            }
        });
    }

    private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
                }
            }
        });
    }

    private ToolBar addGUI()
    {
        playButton = createButton("Pause", 100, 20);
        resetButton = createButton("Reset", 100, 20);

        Label labelR1 = new Label("Rule 1: ");
        Label labelR2 = new Label("Rule 2: ");
        Label labelR3 = new Label("Rule 3: ");
        Label labelR4 = new Label("Rule 4: ");

        Label generationLabel = new Label("  Generation: ");
        generationVal = new Label(String.valueOf(0));
        Label densityLabel = new Label("  Density: ");
        densityVal = new Label(String.valueOf(0) + "%");

        choiceR1 = createChoiceBox();
        choiceR2 = createChoiceBox();
        choiceR3 = createChoiceBox();
        choiceR4 = createChoiceBox();

        choiceR1.setValue(R1);
        choiceR2.setValue(R2);
        choiceR3.setValue(R3);
        choiceR4.setValue(R4);

        choiceR1.setTooltip(new Tooltip("A cell is born if it has this many neighbors minimum"));
        choiceR2.setTooltip(new Tooltip("A cell is born if it has this many neighbors maximum"));
        choiceR3.setTooltip(new Tooltip("A cell will die if it has more than this many neighbors "));
        choiceR4.setTooltip(new Tooltip("A cell will die if it has fewer than this many neighbors"));


        ToolBar toolbar = new ToolBar(playButton,resetButton,labelR1, choiceR1, labelR2, choiceR2, labelR3, choiceR3, labelR4, choiceR4,
                generationLabel, generationVal, densityLabel, densityVal);
        toolbar.setOrientation(Orientation.HORIZONTAL);
        return toolbar;
    }

    private MenuBar addMenu()
    {
        MenuBar menuBar = new MenuBar();

        menuPresets = new Menu("Presets");
        ToggleGroup presetToggleGroup = new ToggleGroup();
        random = createRadioItem("Random", presetToggleGroup);
        random.setSelected(true);
        preset2D = createRadioItem("Cross Section", presetToggleGroup);
        pokeball = createRadioItem("Oscillating Pokeball", presetToggleGroup);
        mazeCube = createRadioItem("Maze Cube", presetToggleGroup);
        twoToad = createRadioItem("Two Toad", presetToggleGroup);
        pentaDecathlon = createRadioItem("PentaDecathlon", presetToggleGroup);

        menuPresets.getItems().add(random);
        menuPresets.getItems().add(preset2D);
        menuPresets.getItems().add(pokeball);
        menuPresets.getItems().add(mazeCube);
        menuPresets.getItems().add(twoToad);
        menuPresets.getItems().add(pentaDecathlon);
        menuPresets.setDisable(true);


        Menu menuRotation = new Menu("Rotation");
        xRotation = createCheckItem("X-Axis");
        yRotation = createCheckItem("Y-Axis");
        zRotation = createCheckItem("Z-Axis");
        menuRotation.getItems().add(xRotation);
        menuRotation.getItems().add(yRotation);
        menuRotation.getItems().add(zRotation);
        menuBar.getMenus().addAll(menuPresets,menuRotation);
        return menuBar;
    }

    private Button createButton(String title, int width, int height)
    {
        Button newButton = new Button(title);
        newButton.setPrefSize(width,height);
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Button source = (Button) event.getSource();
                if(source == playButton)
                {
                    if(isPlaying)
                    {
                        pauseGame();
                    }
                    else
                    {
                        playGame();
                    }
                }

                if(source == resetButton)
                {
                    resetGame();
                }
            }
        });
        return newButton;
    }

    private CheckMenuItem createCheckItem(String title)
    {
        CheckMenuItem checkItem = new CheckMenuItem(title);
        checkItem.setSelected(true);
        checkItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                cellManager.updateRotationSettings(xRotation.isSelected(), yRotation.isSelected(), zRotation.isSelected());
            }
        });

        return checkItem;
    }

    private RadioMenuItem createRadioItem(String title, ToggleGroup toggleGroup)
    {
        RadioMenuItem radioItem = new RadioMenuItem(title);
        radioItem.setSelected(false);
        radioItem.setToggleGroup(toggleGroup);

        return radioItem;
    }

    private ChoiceBox createChoiceBox()
    {
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(0,1,2,3,4,5,6,7,8,9,10);
        choiceBox.setValue(0);
        choiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Integer> observableValue, Integer oldVal, Integer newVal) ->
            {
                if(choiceBox == choiceR1)
                {
                    R1 = newVal;
                }

                if(choiceBox == choiceR2)
                {
                    R2 = newVal;
                }

                if(choiceBox == choiceR3)
                {
                    R3 = newVal;
                }

                if(choiceBox == choiceR4)
                {
                    R4 = newVal;
                }
            });
        return choiceBox;
    }

    private void setPivotPoint()
    {
        cellGroupXform.ry.pivotXProperty().set(230);
        cellGroupXform.ry.pivotYProperty().set(230);
        cellGroupXform.ry.pivotZProperty().set(230);

        cellGroupXform.rx.pivotXProperty().set(230);
        cellGroupXform.rx.pivotYProperty().set(230);
        cellGroupXform.rx.pivotZProperty().set(230);

        cellGroupXform.rz.pivotXProperty().set(230);
        cellGroupXform.rz.pivotYProperty().set(230);
        cellGroupXform.rz.pivotZProperty().set(230);
    }

    private void playGame()
    {
        isPlaying = true;
        playButton.setText("Pause");
        cellManager.setGameEngineState(true);
        resetButton.setDisable(true);
        menuPresets.setDisable(true);
    }

    private void pauseGame()
    {
        isPlaying = false;
        playButton.setText("Play");
        cellManager.setGameEngineState(false);
        resetButton.setDisable(false);
        menuPresets.setDisable(false);
    }

    private void resetGame()
    {
        //cellManager.reset();
        cellGroupXform.getChildren().clear();
        cellManager = new CellManager(this,cellGroupXform, R1, R2, R3, R4);
        cellManager.updateRotationSettings(xRotation.isSelected(), yRotation.isSelected(), zRotation.isSelected());
        pauseGame();
    }

    protected void updateGenerationCount(int newVal)
    {
        generationVal.setText(String.valueOf(newVal));
    }

    protected void updateDensityVal(int newVal)
    {
        densityVal.setText(String.valueOf(newVal) + "%");
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        setPivotPoint();
        sceneXform.getChildren().add(cellGroupXform);
        root.getChildren().add(sceneXform);
        buildCamera();
        SubScene cellSubScene = new SubScene(root, 800,600,true,SceneAntialiasing.BALANCED);
        cellSubScene.setFill(Color.DARKBLUE);
        cellSubScene.setCamera(camera);
        borderPane = new BorderPane();
        borderPane.setTop(addMenu());
        borderPane.setBottom(addGUI());
        borderPane.setCenter(cellSubScene);
        borderPane.setPrefSize(800,500);
        cellManager = new CellManager(this,cellGroupXform, R1, R2, R3, R4);
        Scene scene = new Scene(borderPane);
        scene.setFill(Color.GREY);
        handleKeyboard(scene, cellGroupXform);
        handleMouse(scene, cellGroupXform);
        primaryStage.setTitle("Conway's Game of Life in 3D");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
