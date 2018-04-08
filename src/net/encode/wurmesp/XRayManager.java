package net.encode.wurmesp;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

import com.wurmonline.client.game.PlayerPosition;
import com.wurmonline.client.game.World;
import com.wurmonline.client.renderer.PickRenderer;
import com.wurmonline.client.renderer.PickRenderer.CustomPickOutlineRender;
import com.wurmonline.client.renderer.backend.IndexBuffer;
import com.wurmonline.client.renderer.backend.Primitive;
import com.wurmonline.client.renderer.backend.Queue;
import com.wurmonline.client.renderer.backend.RenderState;
import com.wurmonline.client.renderer.backend.VertexBuffer;
//import com.wurmonline.client.renderer.gui.text.TextFont;
import com.wurmonline.mesh.Tiles.Tile;

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
		for (Iterator iterator = WurmEspMod._terrain.iterator();iterator.hasNext();) {
			float[] terraindata = (float[]) iterator.next();
			float x0 = terraindata[0];
			float y0 = terraindata[1];
			float x1 = terraindata[2];
			float y1 = terraindata[3];
			
			float[] colorF = new float[]{terraindata[4],terraindata[5],terraindata[6]};

			PlayerPosition pos = this._world.getPlayer().getPos();

			float z0 = pos.getH();
			float z1 = z0 + 3;

			VertexBuffer _vBuffer = VertexBuffer.create(VertexBuffer.Usage.PICK, 8, true, false, false, false,
					false, 0, 0, false, true);
			FloatBuffer vdata = _vBuffer.lock();
			vdata.put(new float[] { 
					x1, z0, y0, 
					x1, z1, y0, 
					x0, z1, y0, 
					x0, z0, y0, 
					x1, z0, y1, 
					x1, z1, y1, 
					x0, z1, y1, 
					x0, z0, y1 });
			_vBuffer.unlock();

			IndexBuffer _iBuffer = IndexBuffer.create(24, false, true);
			ShortBuffer idata = _iBuffer.lock();
			idata.put(new short[] { 0, 1, 1, 2, 2, 3, 3, 0, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7 });
			_iBuffer.unlock();

			PickRenderer tmp1257_1254 = WurmEspMod._pickRenderer;
			CustomPickOutlineRender customPickOutline = tmp1257_1254.new CustomPickOutlineRender();

			RenderState renderStateOutline = new RenderState();
			renderStateOutline.alphaval = 0.5F;
			renderStateOutline.twosided = false;
			renderStateOutline.depthtest = Primitive.TestFunc.LESS;
			renderStateOutline.depthwrite = false;
			renderStateOutline.blendmode = Primitive.BlendMode.ALPHABLEND;
			renderStateOutline.customstate = customPickOutline;

			Primitive p = this._queuePick.reservePrimitive();

			p.vertex = _vBuffer;
			p.index = _iBuffer;
			p.num = _iBuffer.getNumIndex() / 2;
			p.type = Primitive.Type.LINES;
			p.nolight = true;
			p.nofog = true;
			p.texture[0] = null;
			p.setColor(colorF[0], colorF[1], colorF[2], 1.0F);

			p.copyStateFrom(renderStateOutline);
			this._queuePick.queue(p, null);

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
