package net.encode.wurmesp;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.Tiles.Tile;

public class XrayColors
{
  private static final Map<Tiles.Tile, Color> mappings = new HashMap<Tile, Color>();
  
  public static void addMapping(Tiles.Tile tile, Color color)
  {
    mappings.put(tile, color);
  }
  
  public static void addMapping(Tiles.Tile tile, float[] color)
  {
    mappings.put(tile, new Color(color[0], color[1], color[2]));
  }
  
  public static Color getColorFor(Tiles.Tile tile)
  {
    return (Color)mappings.getOrDefault(tile, Color.PINK);
  }
}