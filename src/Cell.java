import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * Public class Cell
 * A cell has its own lifecycle behavior and occupies a single point on a grid.
 * Created by John on 9/23/2016.
 */
public class Cell
{
  private static final int MAXBOXSIZE = 14;
  private static final int MINBOXSIZE = 2;
  private boolean growing;
  private boolean shrinking;
  private boolean stable;
  private boolean alive;
  private Box cellBox;
  private PhongMaterial cellMaterial = new PhongMaterial();
  private Color cellColor;
  private int redValue = 250;
  private int greenValue = 250;

  /**
   * Class Constructor
   */
  public Cell()
  {
    alive = false;
    growing = false;
    shrinking = false;
    stable = false;
    cellColor = Color.rgb(redValue,greenValue,0);
    cellMaterial.setDiffuseColor(cellColor);
    cellBox = new Box(2,2,2);
    cellBox.setMaterial(cellMaterial);
    cellBox.setVisible(false);
  }

  protected Box getCellBox()
  {
    return cellBox;
  }

  /**
   * Returns a boolean value indicating if the cell is currently growing.
   * @return boolean growing
   */
  protected boolean isGrowing()
  {
    return growing;
  }

  /**
   * Returns a boolean value indicating if the cell is currently shrinking.
   * @return boolean shrinking
   */
  protected boolean isShrinking()
  {
    return shrinking;
  }

  /**
   * Returns a boolean value indicating if the cell is currently alive.
   * @return boolean alive
   */
  public boolean isAlive()
  {
    return alive;
  }

  protected void setAlive(boolean state)
  {
    if(state)
    {
      if(!alive)  //If the cell was dead before
      {
        alive = true;
        growing = true;
        shrinking = false;
        stable = false;
        cellBox.setVisible(true);
        redValue = 250;
        greenValue = 250;
      }
      else if(shrinking)
      {
        growing = true;
        shrinking = false;
        stable = false;
        redValue = 250;
        greenValue = 250;
      }
      else if(growing)   //If the cell was growing
      {
        growing = false;
        shrinking = false;
        stable = true;
        redValue = 0;
        greenValue = 250;
        cellColor = Color.rgb(redValue,greenValue,0);
        cellMaterial.setDiffuseColor(cellColor);
        cellBox.setDepth(MAXBOXSIZE);
        cellBox.setWidth(MAXBOXSIZE);
        cellBox.setHeight(MAXBOXSIZE);
      }
    }
    else
    {
      if(alive)   //If the cell is alive
      {
        if(growing)   //If the cell was still growing, start dying
        {
          growing = false;
          shrinking = true;
          stable = false;
        }
        else if(shrinking)  //Cell should die
        {
          alive = false;
          shrinking = false;
          growing = false;
          stable = false;
          cellBox.setVisible(false);
        }
        else  //Cell should start dying
        {
          shrinking = true;
          growing = false;
          stable = false;
        }
      }
    }
  }

  protected void tick()
  {
    if(alive && !stable)
    {
      double currentHeight = cellBox.getHeight();

      if(growing)
      {
        if(currentHeight < MAXBOXSIZE)
          currentHeight += 0.6;

        if(currentHeight >= MAXBOXSIZE-2)
        {
          if(redValue >= 25)
            redValue -= 25;

          if(greenValue <= 225)
            greenValue += 25;
        }
      }

      if(shrinking)
      {
        if(currentHeight > MINBOXSIZE)
          currentHeight -= 0.6;

        if(redValue <= 240)
          redValue += 10;

        if(greenValue >= 10)
          greenValue -= 10;
      }

      cellColor = Color.rgb(redValue,greenValue,0);
      cellMaterial.setDiffuseColor(cellColor);
      cellBox.setDepth(currentHeight);
      cellBox.setHeight(currentHeight);
      cellBox.setWidth(currentHeight);
    }
  }
}
