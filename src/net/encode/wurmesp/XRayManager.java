package net.encode.wurmesp;

import java.awt.Color;
import java.util.Iterator;

import com.wurmonline.client.game.PlayerPosition;
import com.wurmonline.client.game.World;
import com.wurmonline.client.renderer.backend.Queue;
//import com.wurmonline.client.renderer.gui.text.TextFont;
import com.wurmonline.mesh.Tiles.Tile;

import net.encode.wurmesp.util.RenderUtils;

public class XRayManager {
	private Queue _queuePick;
	private World _world;
	public boolean _first = true;
	
	public XRayManager() {
	}
	
	public void _setWQ(World world, Queue queue) {
		this._world = world;
		this._queuePick = queue;
	}
	
	public void _refreshData() {
		WurmEspMod._terrain.clear();
		//WurmEspMod._oreql.clear();
		if(!this._world.isOwnBodyAdded())
		{
			return;
		}
		WurmEspMod._caveBuffer = this._world.getCaveBuffer();
		
		PlayerPosition pos = this._world.getPlayer().getPos();

		int px = pos.getTileX();
		int py = pos.getTileY();
		int size = WurmEspMod.xraydiameter;
		int sx = px - size / 2;
		int sy = py - size / 2;
		
		final float ox = this._world.getRenderOriginX();
        final float oy = this._world.getRenderOriginY();

		for (int x = 0; x < size; x++) {
			for (int y = size - 1; y >= 0; y--) {
				float tileX = x + sx;
				float tileY = y + sy;
				
				Tile tile = WurmEspMod._caveBuffer.getTileType((int)tileX, (int)tileY);
				if (tile != null && tile.isOreCave()) {
					Color color = XrayColors.getColorFor(tile);
					float[] colorF = { (float) color.getRed() / 255, (float) color.getGreen() / 255,
							(float) color.getBlue() / 255 };

					float curX = tileX * 4 - ox;
					float curY = tileY * 4 - oy;
					float nextX = (tileX + 1) * 4 - ox;
					float nextY = (tileY + 1) * 4 - oy;

					float x0 = curX + 0.2F;
					float y0 = curY + 0.2F;
					float x1 = nextX - 0.2F;
					float y1 = nextY - 0.2F;
					
					WurmEspMod._terrain.add(new float[]{x0,y0,x1,y1,colorF[0],colorF[1],colorF[2]});
				}
			}
		}
		/*
		if(WurmEspMod.xrayshowql)
		{
			size = WurmEspMod.xrayqldiameter;
			sx = px - size / 2;
			sy = py - size / 2;

			for (int x = 0; x < size; x++) {
				for (int y = size - 1; y >= 0; y--) {
					float tileX = x + sx;
					float tileY = y + sy;
					
					Tile tile = WurmEspMod._caveBuffer.getTileType((int)tileX, (int)tileY);
					if (tile != null && tile.isOreCave()) {

						float curX = tileX * 4;
						float curY = tileY * 4;

						float x0 = curX + 2F;
						float y0 = curY + 2F;
						float ql = WurmEspMod.getQl((int)tileX, (int)tileY);
						
						WurmEspMod._oreql.add(new float[]{x0,y0,ql});
					}
				}
			}
		}
		*/
	}
	
	public void _queueXray() {
		if(WurmEspMod._terrain == null)
		{
			return;
		}
		for (Iterator<float[]> iterator = WurmEspMod._terrain.iterator();iterator.hasNext();) {
			float[] terraindata = (float[]) iterator.next();
			float x0 = terraindata[0];
			float y0 = terraindata[1];
			float x1 = terraindata[2];
			float y1 = terraindata[3];
			
			float[] color = new float[]{terraindata[4],terraindata[5],terraindata[6], 1.0F};

			PlayerPosition pos = this._world.getPlayer().getPos();

			float z0 = pos.getH();
			float z1 = z0 + 3;
			
			float[] vertexdata = new float[] { 
					x1, z0, y0, 
					x1, z1, y0, 
					x0, z1, y0, 
					x0, z0, y0, 
					x1, z0, y1, 
					x1, z1, y1, 
					x0, z1, y1, 
					x0, z0, y1 };
			
			int[] indexdata = new int[] { 0, 1, 1, 2, 2, 3, 3, 0, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7 };
			
			RenderUtils.renderPrimitiveLines(8, vertexdata, indexdata, this._queuePick, color);

		}
	}
	/*
	public void _queueQl()
	{
		TextFont text = TextFont.getText();
		
		for(float[] qlinfo : WurmEspMod._oreql)
		{
			//TODO DigitRenderer
		}
	}
	*/
}
