import javafx.animation.AnimationTimer;
import javafx.scene.shape.Box;
import java.util.Random;

/**
 * Public class CellManager
 * Manages a collection of Cells.
 * Created by John on 9/23/2016.
 */
public class CellManager
{
  private static int MAXWIDTH;
  private static int MAXHEIGHT;
  private static int MAXDEPTH;
  private int generationCounter;
  private float densityPercentage;
  private int R1;
  private int R2;
  private int R3;
  private int R4;
  private Random rand = new Random();
  private boolean[][][] currentGrid;
  private boolean[][][] nextGrid;
  private Cell[][][] cellGroup;
  private Xform cellGroupXform;
  private MainGameLoop gameEngine;
  private GameOfLife application;
  private boolean xRotate = true;
  private boolean yRotate = true;
  private boolean zRotate = true;

  /**
   * Class Constructor
   * @param application Specifies the GameOfLife application to use the CellManager
   * @param cellGroupXform  Specifies the Xform to contain the cells that the application will render.
   * @param R1  Specifies the value of Rule: 1 for Conway's Game of Life
   * @param R2  Specifies the value of Rule: 2 for Conway's Game of Life
   * @param R3  Specifies the value of Rule: 3 for Conway's Game of Life
   * @param R4  Specifies the value of Rule: 4 for Conway's Game of Life
   */
  public CellManager(GameOfLife application, Xform cellGroupXform, int R1, int R2, int R3, int R4)
  {
    generationCounter = 0;
    densityPercentage = 0;
    MAXWIDTH = 31;
    MAXHEIGHT = 31;
    MAXDEPTH = 31;
    currentGrid = new boolean[MAXWIDTH + 1][MAXHEIGHT + 1][MAXDEPTH + 1];  //Current state of generation in the grid
    nextGrid = new boolean[MAXWIDTH + 1][MAXHEIGHT + 1][MAXDEPTH + 1];   //The state of the next generation
    cellGroup = new Cell[MAXWIDTH + 1][MAXHEIGHT + 1][MAXDEPTH + 1];  //Array of cells[x][z][y]
    this.application = application;
    this.cellGroupXform = cellGroupXform;
    this.R1 = R1;
    this.R2 = R2;
    this.R3 = R3;
    this.R4 = R4;
    initializeCellGroup();  //Make a new cell for every point on the grid
    gameEngine = new MainGameLoop();
    setGameEngineState(true);
  }

  private void initializeCellGroup()
  {
    Cell newCell;
    Box newCellBox;

    for(int i = 1; i<MAXWIDTH; i++) //For every x
    {
      for(int j = 1; j<MAXHEIGHT; j++) //For every z
      {
        for(int k = 1; k<MAXDEPTH; k++) //For every y
        {
          newCell = new Cell();
          cellGroup[i][j][k] = newCell;  //Make a new cell for every point on the grid
          newCellBox = newCell.getCellBox();
          newCellBox.setTranslateX(i*15);
          newCellBox.setTranslateZ(j*15);
          newCellBox.setTranslateY(k*15);
          cellGroupXform.getChildren().add(newCellBox);  //Add the new cell's box to the cellGroup Xform
        }
      }
    }
    getPreset();
  }

  private void addLiveCell(int i, int j, int k)
  {
    cellGroup[i][j][k].setAlive(true);
    currentGrid[i][j][k] = true;
  }

  private void initializePresetRandom()
  {
    int nxtRand;

    for(int i = 1; i<MAXWIDTH; i++) //For every x
    {
      for (int j = 1; j < MAXHEIGHT; j++) //For every z
      {
        for (int k = 1; k < MAXDEPTH; k++) //For every y
        //for(int k = 1; k<2; k++) //For every y
        {
          nxtRand = rand.nextInt(100);
          if(nxtRand==0)    //Sets 1% of random cells to alive
          {
            cellGroup[i][j][k].setAlive(true);
            currentGrid[i][j][k] = true;
          }
        }
      }
    }
  }

  private void initializePreset2D()
  {

    addLiveCell(14,14,16);
    addLiveCell(15,14,16);
    addLiveCell(14,15,16);
    addLiveCell(15,15,16);
    addLiveCell(14,15,15);
    addLiveCell(15,15,15);
    addLiveCell(14,16,15);
    addLiveCell(15,16,15);
    addLiveCell(14,14,14);
    addLiveCell(15,14,14);

    /*
    //Block $
    addLiveCell(1,1,1);
    addLiveCell(2,1,1);
    addLiveCell(1,2,1);
    addLiveCell(2,2,1);
    //Block $
    addLiveCell(1,29,1);
    addLiveCell(2,29,1);
    addLiveCell(1,30,1);
    addLiveCell(2,30,1);
    //Block $
    addLiveCell(29,1,1);
    addLiveCell(29,2,1);
    addLiveCell(30,1,1);
    addLiveCell(30,2,1);
    //Block $
    addLiveCell(29,29,1);
    addLiveCell(29,30,1);
    addLiveCell(30,29,1);
    addLiveCell(30,30,1);
    //Beehive $
    addLiveCell(2,14,1);
    addLiveCell(1,15,1);
    addLiveCell(1,16,1);
    addLiveCell(3,15,1);
    addLiveCell(3,16,1);
    addLiveCell(2,17,1);
    //Beehive $
    addLiveCell(14,2,1);
    addLiveCell(15,1,1);
    addLiveCell(15,3,1);
    addLiveCell(16,1,1);
    addLiveCell(16,3,1);
    addLiveCell(17,2,1);
    //Beehive $
    addLiveCell(14,29,1);
    addLiveCell(15,28,1);
    addLiveCell(15,30,1);
    addLiveCell(16,28,1);
    addLiveCell(16,30,1);
    addLiveCell(17,29,1);
    //Beehive $
    addLiveCell(29,14,1);
    addLiveCell(28,15,1);
    addLiveCell(30,15,1);
    addLiveCell(28,16,1);
    addLiveCell(30,16,1);
    addLiveCell(29,17,1);
    //Blinker $
    addLiveCell(2,7,1);
    addLiveCell(2,8,1);
    addLiveCell(2,9,1);
    //Blinker $
    addLiveCell(2,22,1);
    addLiveCell(2,23,1);
    addLiveCell(2,24,1);
    //Blinker$
    addLiveCell(29,7,1);
    addLiveCell(29,8,1);
    addLiveCell(29,9,1);
    //Blinker$
    addLiveCell(29,22,1);
    addLiveCell(29,23,1);
    addLiveCell(29,24,1);
    //Pulsar
    addLiveCell(9,11,1);
    addLiveCell(9,12,1);
    addLiveCell(9,13,1);
    addLiveCell(9,17,1);
    addLiveCell(9,18,1);
    addLiveCell(9,19,1);

    addLiveCell(11,9,1);
    addLiveCell(12,9,1);
    addLiveCell(13,9,1);
    addLiveCell(17,9,1);
    addLiveCell(18,9,1);
    addLiveCell(19,9,1);

    addLiveCell(11,14,1);
    addLiveCell(12,14,1);
    addLiveCell(13,14,1);
    addLiveCell(17,14,1);
    addLiveCell(18,14,1);
    addLiveCell(19,14,1);

    addLiveCell(11,16,1);
    addLiveCell(12,16,1);
    addLiveCell(13,16,1);
    addLiveCell(17,16,1);
    addLiveCell(18,16,1);
    addLiveCell(19,16,1);

    addLiveCell(11,21,1);
    addLiveCell(12,21,1);
    addLiveCell(13,21,1);
    addLiveCell(17,21,1);
    addLiveCell(18,21,1);
    addLiveCell(19,21,1);

    addLiveCell(14,11,1);
    addLiveCell(14,12,1);
    addLiveCell(14,13,1);
    addLiveCell(14,17,1);
    addLiveCell(14,18,1);
    addLiveCell(14,19,1);

    addLiveCell(16,11,1);
    addLiveCell(16,12,1);
    addLiveCell(16,13,1);
    addLiveCell(16,17,1);
    addLiveCell(16,18,1);
    addLiveCell(16,19,1);

    addLiveCell(21,11,1);
    addLiveCell(21,12,1);
    addLiveCell(21,13,1);
    addLiveCell(21,17,1);
    addLiveCell(21,18,1);
    addLiveCell(21,19,1);
    */
  }

  private void initializePresetCornerBeacons2D()
  {
    //Beacons in each corner
    addLiveCell(1,1,1);
    addLiveCell(1,2,1);
    addLiveCell(1,29,1);
    addLiveCell(1,30,1);

    addLiveCell(2,1,1);
    addLiveCell(2,2,1);
    addLiveCell(2,29,1);
    addLiveCell(2,30,1);

    addLiveCell(29,1,1);
    addLiveCell(29,2,1);
    addLiveCell(29,29,1);
    addLiveCell(29,30,1);

    addLiveCell(30,1,1);
    addLiveCell(30,2,1);
    addLiveCell(30,29,1);
    addLiveCell(30,30,1);

    addLiveCell(3,3,1);
    addLiveCell(3,4,1);
    addLiveCell(3,27,1);
    addLiveCell(3,28,1);

    addLiveCell(4,3,1);
    addLiveCell(4,4,1);
    addLiveCell(4,27,1);
    addLiveCell(4,28,1);

    addLiveCell(27,3,1);
    addLiveCell(27,4,1);
    addLiveCell(27,27,1);
    addLiveCell(27,28,1);

    addLiveCell(28,3,1);
    addLiveCell(28,4,1);
    addLiveCell(28,27,1);
    addLiveCell(28,28,1);
    //Next level
    addLiveCell(1,1,2);
    addLiveCell(1,2,2);
    addLiveCell(1,29,2);
    addLiveCell(1,30,2);

    addLiveCell(2,1,2);
    addLiveCell(2,2,2);
    addLiveCell(2,29,2);
    addLiveCell(2,30,2);

    addLiveCell(29,1,2);
    addLiveCell(29,2,2);
    addLiveCell(29,29,2);
    addLiveCell(29,30,2);

    addLiveCell(30,1,2);
    addLiveCell(30,2,2);
    addLiveCell(30,29,2);
    addLiveCell(30,30,2);

    addLiveCell(3,3,2);
    addLiveCell(3,4,2);
    addLiveCell(3,27,2);
    addLiveCell(3,28,2);

    addLiveCell(4,3,2);
    addLiveCell(4,4,2);
    addLiveCell(4,27,2);
    addLiveCell(4,28,2);

    addLiveCell(27,3,2);
    addLiveCell(27,4,2);
    addLiveCell(27,27,2);
    addLiveCell(27,28,2);

    addLiveCell(28,3,2);
    addLiveCell(28,4,2);
    addLiveCell(28,27,2);
    addLiveCell(28,28,2);
  }

  private void initializePresetCornerBeacons3D()
  {
    //Beacons in each corner
    addLiveCell(1,1,1);
    addLiveCell(1,2,1);
    addLiveCell(1,29,30);
    addLiveCell(1,30,30);

    addLiveCell(2,1,1);
    addLiveCell(2,2,1);
    addLiveCell(2,29,30);
    addLiveCell(2,30,30);

    addLiveCell(29,1,30);
    addLiveCell(29,2,30);
    addLiveCell(29,29,1);
    addLiveCell(29,30,1);

    addLiveCell(30,1,30);
    addLiveCell(30,2,30);
    addLiveCell(30,29,1);
    addLiveCell(30,30,1);

    addLiveCell(3,3,1);
    addLiveCell(3,4,1);
    addLiveCell(3,27,30);
    addLiveCell(3,28,30);

    addLiveCell(4,3,1);
    addLiveCell(4,4,1);
    addLiveCell(4,27,30);
    addLiveCell(4,28,30);

    addLiveCell(27,3,30);
    addLiveCell(27,4,30);
    addLiveCell(27,27,1);
    addLiveCell(27,28,1);

    addLiveCell(28,3,30);
    addLiveCell(28,4,30);
    addLiveCell(28,27,1);
    addLiveCell(28,28,1);
    //Next level
    addLiveCell(1,1,2);
    addLiveCell(1,2,2);
    addLiveCell(1,29,29);
    addLiveCell(1,30,29);

    addLiveCell(2,1,2);
    addLiveCell(2,2,2);
    addLiveCell(2,29,29);
    addLiveCell(2,30,29);

    addLiveCell(29,1,29);
    addLiveCell(29,2,29);
    addLiveCell(29,29,2);
    addLiveCell(29,30,2);

    addLiveCell(30,1,29);
    addLiveCell(30,2,29);
    addLiveCell(30,29,2);
    addLiveCell(30,30,2);

    addLiveCell(3,3,2);
    addLiveCell(3,4,2);
    addLiveCell(3,27,29);
    addLiveCell(3,28,29);

    addLiveCell(4,3,2);
    addLiveCell(4,4,2);
    addLiveCell(4,27,29);
    addLiveCell(4,28,29);

    addLiveCell(27,3,29);
    addLiveCell(27,4,29);
    addLiveCell(27,27,2);
    addLiveCell(27,28,2);

    addLiveCell(28,3,29);
    addLiveCell(28,4,29);
    addLiveCell(28,27,2);
    addLiveCell(28,28,2);
  }

  private void initializePresetTwoToad()
  {
    addLiveCell(2,14,1);
    addLiveCell(3,14,1);
    addLiveCell(4,14,1);
    addLiveCell(27,14,1);
    addLiveCell(28,14,1);
    addLiveCell(29,14,1);

    addLiveCell(1,15,1);
    addLiveCell(2,15,1);
    addLiveCell(3,15,1);
    addLiveCell(28,15,1);
    addLiveCell(29,15,1);
    addLiveCell(30,15,1);
  }

  private void initializePresetPentaDeca()
  {
    addLiveCell(13,13,1);
    addLiveCell(18,13,1);
    addLiveCell(13,15,1);
    addLiveCell(18,15,1);

    addLiveCell(11,14,1);
    addLiveCell(12,14,1);
    addLiveCell(19,14,1);
    addLiveCell(20,14,1);
    addLiveCell(14,14,1);
    addLiveCell(15,14,1);
    addLiveCell(16,14,1);
    addLiveCell(17,14,1);

    addLiveCell(13,13,30);
    addLiveCell(18,13,30);
    addLiveCell(13,15,30);
    addLiveCell(18,15,30);

    addLiveCell(11,14,30);
    addLiveCell(12,14,30);
    addLiveCell(19,14,30);
    addLiveCell(20,14,30);
    addLiveCell(14,14,30);
    addLiveCell(15,14,30);
    addLiveCell(16,14,30);
    addLiveCell(17,14,30);
  }

  private void getPreset()
  {
    if(application.random.isSelected()) //Random
    {
      initializePresetRandom();
    }

    if(application.preset2D.isSelected()) //Cross section w/ pulsar
    {
      MAXDEPTH = 2;
      initializePreset2D();
    }

    if(application.pokeball.isSelected())  //Pokeball with oscillating center
    {
      MAXDEPTH = 3;
      R1 = 3;
      R2 = 4;
      R3 = 7;
      R4 = 2;
      application.choiceR1.setValue(R1);
      application.choiceR2.setValue(R2);
      application.choiceR3.setValue(R3);
      application.choiceR4.setValue(R4);
      initializePresetCornerBeacons2D();
    }

    if(application.mazeCube.isSelected())  //Symmetric Maze Cube
    {
      R1 = 3;
      R2 = 4;
      R3 = 7;
      R4 = 2;
      application.choiceR1.setValue(R1);
      application.choiceR2.setValue(R2);
      application.choiceR3.setValue(R3);
      application.choiceR4.setValue(R4);
      initializePresetCornerBeacons3D();
    }

    if(application.twoToad.isSelected())
    {
      initializePresetTwoToad();
    }

    if(application.pentaDecathlon.isSelected())  //PentaDecathlon
    {
      initializePresetPentaDeca();
    }
  }

  private int countNeighbors(int i, int j, int k)
  {
    int neighbors = 0;

    neighbors += checkSurrounding(i,j,k); //surrounding cells at cell level

    if(neighbors <= R3)
      neighbors += checkSurrounding(i,j,k-1); //surrounding cells at level below

    if(neighbors <= R3)
      neighbors += checkSurrounding(i,j,k+1); //surrounding cells at level above

    if(neighbors <= R3) {
      if (currentGrid[i][j][k + 1]) //Cell above
      {
        neighbors++;
      }

      if (currentGrid[i][j][k - 1]) //Cell below
      {
        neighbors++;
      }
    }

    return neighbors;
  }

  private int checkSurrounding(int i, int j, int k)
  {
    int neighbors = 0;

    if(neighbors <= R3 && currentGrid[i-1][j][k])    //E neighbor
    {
      neighbors ++;
    }
    if(neighbors <= R3 && currentGrid[i-1][j-1][k])    //NE neighbor
    {
      neighbors++;
    }
    if(neighbors <= R3 && currentGrid[i-1][j+1][k])    //SE neighbor
    {
      neighbors++;
    }
    if(neighbors <= R3 && currentGrid[i+1][j][k])    //W neighbor
    {
      neighbors++;
    }
    if(neighbors <= R3 && currentGrid[i+1][j-1][k])    //NW neighbor
    {
      neighbors++;
    }
    if(neighbors <= R3 && currentGrid[i+1][j+1][k])    //SW neighbor
    {
      neighbors++;
    }
    if(neighbors <= R3 && currentGrid[i][j-1][k])    //N neighbor
    {
      neighbors++;
    }
    if(neighbors <= R3 && currentGrid[i][j+1][k])    //S neighbor
    {
      neighbors++;
    }

    return neighbors;
  }

  private void updateNextGrid()
  {
    int neighbors;

    for(int i = 1; i<MAXWIDTH; i++) //For every x
    {
      for (int j = 1; j < MAXHEIGHT; j++) //For every z
      {
        for (int k = 1; k < MAXDEPTH; k++) //For every y
        {
          neighbors = countNeighbors(i,j,k);

          if(currentGrid[i][j][k])  //If there is a living cell at this location
          {
            if(neighbors > R3 || neighbors < R4)  //See if the cell should die
            {
              nextGrid[i][j][k] = false;    //Update next grid to be empty at old cell location
            }
            else
            {
              nextGrid[i][j][k] = true;   //The location will remain occupied
            }
          }
          else    //If there is no cell at this location
          {
            if(neighbors >= R1 && neighbors <= R2)  //See if a cell should be born
            {
              nextGrid[i][j][k] = true;   //Update next grid with new born cell
            }
            else
            {
              nextGrid[i][j][k] = false;  //This location will remain empty
            }
          }
        }
      }
    }
  }

  private void updateCells()
  {
    Cell nextCell;

    for(int i = 1; i<MAXWIDTH; i++) //For every x
    {
      for (int j = 1; j < MAXHEIGHT; j++) //For every z
      {
        for (int k = 1; k < MAXDEPTH; k++) //For every y
        {
          nextCell = cellGroup[i][j][k];
          currentGrid[i][j][k] = nextGrid[i][j][k];   //Update currentGrid to hold the new state

          if(nextGrid[i][j][k])   //If there should be a cell at this location
          {
            nextCell.setAlive(true);
          }
          else    //If there should NOT be a cell at this location
          {
            nextCell.setAlive(false);
          }
        }
      }
    }
  }

  protected void setGameEngineState(boolean state)
  {
    if(state)
    {
      gameEngine.start();
    }
    else
    {
      gameEngine.stop();
    }
  }

  protected void updateRotationSettings(boolean x, boolean y, boolean z)
  {
    if(x)
    {
      xRotate = true;
    }
    else
    {
      xRotate = false;
    }

    if(y)
    {
      yRotate = true;
    }
    else
    {
      yRotate = false;
    }

    if(z)
    {
      zRotate = true;
    }
    else
    {
      zRotate = false;
    }
  }

  private void updateAutoRotate()
  {

    if(xRotate)
    {
      double currentXAngle = cellGroupXform.rx.getAngle();
      cellGroupXform.rx.setAngle((currentXAngle + 1)%360);
    }

    if(yRotate)
    {
      double currentYAngle = cellGroupXform.ry.getAngle();
      cellGroupXform.ry.setAngle((currentYAngle + 1)%360);
    }

    if(zRotate)
    {
      double currentZAngle = cellGroupXform.rz.getAngle();
      cellGroupXform.rz.setAngle((currentZAngle + 1)%360);
    }
  }

  /**
   * private class MainGameLoop
   * extends AnimationTimer
   * Called every frame while the application is running. Handles animations and timed calls to the CellManager for updating the game state.
   */
  private class MainGameLoop extends AnimationTimer
  {
    private int frameCount = 0;

    public void handle(long now)
    {
      frameCount++;

      if((frameCount%3) == 0)
      {
        densityPercentage = 0;

        for (int i = 1; i < MAXWIDTH; i++) //For every x
        {
          for (int j = 1; j < MAXHEIGHT; j++) //For every z
          {
            for (int k = 1; k < MAXDEPTH; k++) //For every y
            {
              cellGroup[i][j][k].tick();
              if(cellGroup[i][j][k].isAlive())
              {
                densityPercentage ++;
              }
            }
          }
        }
        updateAutoRotate();
      }

      if(frameCount == 60)
      {
        generationCounter ++;
        application.updateGenerationCount(generationCounter);
        float newDensity = ((densityPercentage/27000)*100)/1;
        application.updateDensityVal((int)newDensity);
        updateNextGrid();
        updateCells();
        frameCount = 0;
      }
    }
  }
}
