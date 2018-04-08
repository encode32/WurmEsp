package net.encode.wurmesp;

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

public class TilesCloseByManager {
	private Queue _queuePick;
	private World _world;
	public boolean _first = true;
	
	public TilesCloseByManager(){
		
	}
	
	public void _setWQ(World world, Queue queue) {
		this._world = world;
		this._queuePick = queue;
	}
	
	public void _refreshData() {
		WurmEspMod._closeByTerrain.clear();
		if(!this._world.isOwnBodyAdded())
		{
			return;
		}
		WurmEspMod._terrainBuffer = this._world.getNearTerrainBuffer();
		
		PlayerPosition pos = this._world.getPlayer().getPos();
		
		int px = pos.getTileX();
		int py = pos.getTileY();
		int size = 6;
		int sx = px - size / 2;
		int sy = py - size / 2;
		
		final float ox = this._world.getRenderOriginX();
        final float oy = this._world.getRenderOriginY();
		
		for (int x = 0; x < size+1; x++) {
			for (int y = 0; y < size+1; y++) {
				float tileX = x + sx;
				float tileY = y + sy;
				
				float curX = tileX * 4 - ox;
				float curY = tileY * 4 - oy;
				float nextX = (tileX + 1) * 4 - ox;
				float nextY = (tileY + 1) * 4 - oy;

				float x0 = curX + 0.1F;
				float y0 = curY + 0.1F;
				float x1 = nextX - 0.1F;
				float y1 = nextY - 0.1F;
				
				float z0 = WurmEspMod._terrainBuffer.getHeight((int)tileX, (int)tileY);
				float z1 = WurmEspMod._terrainBuffer.getHeight((int)tileX +1, (int)tileY);
				float z2 = WurmEspMod._terrainBuffer.getHeight((int)tileX, (int)tileY +1);
				float z3 = WurmEspMod._terrainBuffer.getHeight((int)tileX +1, (int)tileY +1);
				
				WurmEspMod._closeByTerrain.add(new float[]{
						x0,z0,y0,
						x1,z1,y0,
						x0,z2,y1,
						x1,z3,y1
						});
			}
		}
	}
	public void _queueTiles() {
		if(WurmEspMod._closeByTerrain == null)
		{
			return;
		}
		for (Iterator iterator = WurmEspMod._closeByTerrain.iterator();iterator.hasNext();) {
			float[] t = (float[]) iterator.next();
			
			VertexBuffer _vBuffer = VertexBuffer.create(VertexBuffer.Usage.PICK, 4, true, false, false, false,
					false, 0, 0, false, true);
			FloatBuffer vdata = _vBuffer.lock();
			vdata.put(t);
			_vBuffer.unlock();

			IndexBuffer _iBuffer = IndexBuffer.create(24, false, true);
			ShortBuffer idata = _iBuffer.lock();
			idata.put(new short[] { 1, 0, 0, 2, 2, 3, 3, 1});
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
			p.setColor(1.0F, 1.0F, 0.0F, 0.5F);

			p.copyStateFrom(renderStateOutline);
			this._queuePick.queue(p, null);
		}
	}
}
