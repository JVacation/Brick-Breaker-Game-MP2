import java.awt.Color;

/**
 * Hide the specific internal representation of colours
 *  from most of the program.
 * Map to Swing color when required.
 */
public enum Colour  
{ 
  RED(Color.RED), GREEN(Color.GREEN), GRAY(Color.GRAY), BLUE(Color.BLUE), ORANGE(Color.ORANGE), MAGENTA(Color.MAGENTA), WHITE(Color.WHITE);

  private final Color c;

  Colour( Color c ) { this.c = c; }

  public Color forSwing() { return c; }
}


