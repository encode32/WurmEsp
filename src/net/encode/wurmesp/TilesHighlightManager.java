package net.encode.wurmesp;

import java.util.Iterator;

import com.wurmonline.client.game.PlayerPosition;
import com.wurmonline.client.game.World;
import com.wurmonline.client.renderer.backend.Queue;
import net.encode.wurmesp.util.RenderUtils;

public class TilesHighlightManager {
	private Queue _queuePick;
	private World _world;
	public boolean _first = true;
	
	public TilesHighlightManager(){
		
	}
	
	public void _setW(World world) {
		this._world = world;
	}
	
	public void _setWQ(World world, Queue queue) {
		this._world = world;
		this._queuePick = queue;
	}
	
	public void _addData(String direction, int tiles, int times, int space) {
		
		PlayerPosition pos = this._world.getPlayer().getPos();
		
		int baseTileX = pos.getTileX();
		int baseTileY = pos.getTileY();
		
		WurmEspMod._tilesHighlightBase.add(new int[]{baseTileX,baseTileY});
		
		for (int i = 0 ; i < times ; i++) {
			if(direction.equals("n")) {
				baseTileY -= (tiles);
				for(int s = 1 ;s < space+1 ; s++) {
					baseTileY -= 1;
					WurmEspMod._tilesHighlightBase.add(new int[]{baseTileX,baseTileY});
				}
			}else if(direction.equals("s")) {
				baseTileY += (tiles);
				for(int s = 1 ;s < space+1 ; s++) {
					baseTileY += 1;
					WurmEspMod._tilesHighlightBase.add(new int[]{baseTileX,baseTileY});
				}
			}else if(direction.equals("e")) {
				baseTileX += (tiles);
				for(int s = 1 ;s < space+1 ; s++) {
					baseTileX += 1;
					WurmEspMod._tilesHighlightBase.add(new int[]{baseTileX,baseTileY});
				}
			}else if(direction.equals("w")) {
				baseTileX -= (tiles);
				for(int s = 1 ;s < space+1 ; s++) {
					baseTileX -= 1;
					WurmEspMod._tilesHighlightBase.add(new int[]{baseTileX,baseTileY});
				}
			}
		}
	}
	
	public void _addData(int x, int y) {
		WurmEspMod._tilesHighlightBase.add(new int[]{x,y});
	}
	
	public void _addData(int radius) {
		int tileX = this._world.getPlayer().getPos().getTileX();
		int tileY = this._world.getPlayer().getPos().getTileY();
		int startX = tileX - radius;
		int startY = tileY - radius;
		int endX = tileX + radius;
		int endY = tileY+ radius;
		
		this._addData(startX, startY, endX, endY);
	}
	
	public void _addData(int startX, int startY, int endX, int endY) {
		WurmEspMod._tilesHighlightBase.add(new int[]{startX,startY});
		WurmEspMod._tilesHighlightBase.add(new int[]{startX,endY});
		WurmEspMod._tilesHighlightBase.add(new int[]{endX,startY});
		WurmEspMod._tilesHighlightBase.add(new int[]{endX,endY});
	}
	
	public void _refreshData() {
		WurmEspMod._tilesHighlightTerrain.clear();
		WurmEspMod._terrainBuffer = this._world.getNearTerrainBuffer();
		
		final float ox = this._world.getRenderOriginX();
        final float oy = this._world.getRenderOriginY();
		
		for(int[] base : WurmEspMod._tilesHighlightBase) {
			float curX = base[0] * 4 - ox;
			float curY = base[1] * 4 - oy;
			float nextX = (base[0] + 1) * 4 - ox;
			float nextY = (base[1] + 1) * 4 - oy;
			
			
			float x0 = curX + 0.1F;
			float y0 = curY + 0.1F;
			float x1 = nextX - 0.1F;
			float y1 = nextY - 0.1F;
			
			float z0 = WurmEspMod._terrainBuffer.getHeight((int)base[0], (int)base[1]);
			float z1 = WurmEspMod._terrainBuffer.getHeight((int)base[0] +1, (int)base[1]);
			float z2 = WurmEspMod._terrainBuffer.getHeight((int)base[0], (int)base[1] +1);
			float z3 = WurmEspMod._terrainBuffer.getHeight((int)base[0] +1, (int)base[1] +1);
			
			WurmEspMod._tilesHighlightTerrain.add(new float[]{
					x0,z0,y0,//0
					x1,z1,y0,//1
					x0,z2,y1,//2
					x1,z3,y1,//3
					x0,0.0f,y0,//4
					x1,0.0f,y0,//5
					x0,0.0f,y1,//6
					x1,0.0f,y1//7
					});
		}
	}
	
	public void _queueTilesHighlight() {
		if(WurmEspMod._tilesHighlightTerrain == null)
		{
			return;
		}
		for (Iterator<float[]> iterator = WurmEspMod._tilesHighlightTerrain.iterator();iterator.hasNext();) {
			float[] t = (float[]) iterator.next();
			
			float[] color = new float[]{1.0F, 0.0F, 0.0F, 1.0F};
			
			int[] indexdata = new int[] { 1, 0, 0, 2, 2, 3, 3, 1, 1, 2, 0, 3, 5, 4, 4, 6, 6, 7, 7, 5, 0, 4, 1, 5, 2, 6, 3, 7 };
			
			RenderUtils.renderPrimitiveLines(8, t, indexdata, this._queuePick, color);
		}
	}
}
