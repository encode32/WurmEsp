package net.encode.wurmesp;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.Tiles.Tile;

public class XrayColors
{
  private static final Map<Tiles.Tile, Color> mappings = new HashMap<Tile, Color>();
  
  static
  {
    addMapping(Tiles.Tile.TILE_CAVE_WALL, Color.DARK_GRAY);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_REINFORCED, Color.DARK_GRAY);
    addMapping(Tiles.Tile.TILE_CAVE, Color.PINK);
    addMapping(Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED, Color.PINK);
    addMapping(Tiles.Tile.TILE_CAVE_EXIT, Color.PINK);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_IRON, Color.RED.darker());
    addMapping(Tiles.Tile.TILE_CAVE_WALL_LAVA, Color.RED);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_COPPER, Color.GREEN);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_TIN, Color.GRAY);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_GOLD, Color.YELLOW.darker());
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_ADAMANTINE, Color.CYAN);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL, Color.YELLOW.brighter());
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_SILVER, Color.LIGHT_GRAY);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_LEAD, Color.PINK.darker().darker());
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ORE_ZINC, new Color(235, 235, 235));
    addMapping(Tiles.Tile.TILE_CAVE_WALL_SLATE, Color.BLACK);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_MARBLE, Color.WHITE);
    addMapping(Tiles.Tile.TILE_CAVE_WALL_ROCKSALT, Color.ORANGE);
  }
  
  private static void addMapping(Tiles.Tile tile, Color color)
  {
    mappings.put(tile, color);
  }
  
  public static Color getColorFor(Tiles.Tile tile)
  {
    return (Color)mappings.getOrDefault(tile, Color.PINK);
  }
}
