package net.encode.wurmesp.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import com.wurmonline.client.renderer.PickRenderer;
import com.wurmonline.client.renderer.PickRenderer.CustomPickOutlineRender;
import com.wurmonline.client.renderer.backend.IndexBuffer;
import com.wurmonline.client.renderer.backend.Primitive;
import com.wurmonline.client.renderer.backend.Queue;
import com.wurmonline.client.renderer.backend.RenderState;
import com.wurmonline.client.renderer.backend.VertexBuffer;
import net.encode.wurmesp.WurmEspMod;

public class RenderUtils {
	
	public static void renderPrimitiveLines(int numvertex, float[] vertexdata, int[] indexdata, Queue queue, float[] color) {
		VertexBuffer _vBuffer = VertexBuffer.create(VertexBuffer.Usage.PICK, numvertex, true, false, false, false,
				false, 0, 0, false, true);
		FloatBuffer vdata = _vBuffer.lock();
		vdata.put(vertexdata);
		_vBuffer.unlock();

		IndexBuffer _iBuffer = IndexBuffer.create(indexdata.length, false, true);
		IntBuffer idata = _iBuffer.lock();
		idata.put(indexdata);
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

		Primitive p = queue.reservePrimitive();

		p.vertex = _vBuffer;
		p.index = _iBuffer;
		p.num = _iBuffer.getNumIndex() / 2;
		p.type = Primitive.Type.LINES;
		p.nolight = true;
		p.nofog = true;
		p.texture[0] = null;
		p.setColor(color[0], color[1], color[2], color[3]);

		p.copyStateFrom(renderStateOutline);
		queue.queue(p, null);
	}
}
