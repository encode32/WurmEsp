package net.encode.wurmesp;

import com.wurmonline.client.renderer.Color;
import com.wurmonline.client.renderer.PickRenderer;
import com.wurmonline.client.renderer.PickRenderer.CustomPickFillDepthRender;
import com.wurmonline.client.renderer.PickRenderer.CustomPickFillRender;
import com.wurmonline.client.renderer.PickRenderer.CustomPickOutlineRender;
import com.wurmonline.client.renderer.PickableUnit;
import com.wurmonline.client.renderer.backend.Primitive;
import com.wurmonline.client.renderer.backend.Queue;
import com.wurmonline.client.renderer.backend.RenderState;
import net.encode.wurmesp.WurmEspMod.SEARCHTYPE;

public class Unit {
	private long id;
	private String modelName;
	private String hoverName;
	private PickableUnit pickableUnit;
	private float[] color = new float[]{0.0f,0.0f,0.0f};
	private float[] conditionedcolor = new float[]{0.0f,0.0f,0.0f};
	private String condition;
	
	public static float[] colorPlayers = {0.0f, 0.0f, 0.0f};
	public static float[] colorPlayersEnemy = {0.0f, 0.0f, 0.0f};
	public static float[] colorMobs = {0.0f, 0.0f, 0.0f};
	public static float[] colorMobsAggro = {0.0f, 0.0f, 0.0f};
	public static float[] colorSpecials = {0.0f, 0.0f, 0.0f};
	public static float[] colorSpotted = {0.0f, 0.0f, 0.0f};
	public static float[] colorUniques = {0.0f, 0.0f, 0.0f};
	public static float[] colorAlert = {0.0f, 0.0f, 0.0f};
	public static float[] colorAngry = {0.0f, 0.0f, 0.0f};
	public static float[] colorChampion = {0.0f, 0.0f, 0.0f};
	public static float[] colorDiseased = {0.0f, 0.0f, 0.0f};
	public static float[] colorFierce = {0.0f, 0.0f, 0.0f};
	public static float[] colorGreenish = {0.0f, 0.0f, 0.0f};
	public static float[] colorHardened = {0.0f, 0.0f, 0.0f};
	public static float[] colorLurking = {0.0f, 0.0f, 0.0f};
	public static float[] colorRaging = {0.0f, 0.0f, 0.0f};
	public static float[] colorScared = {0.0f, 0.0f, 0.0f};
	public static float[] colorSlow = {0.0f, 0.0f, 0.0f};
	public static float[] colorSly = {0.0f, 0.0f, 0.0f};
	
	public static String[] aggroMOBS;
	
	public static String[] uniqueMOBS;
	
	public static String[] specialITEMS;
	
	public static String[] spottedITEMS;
	
	public static String[] conditionedMOBS;
	
	public Unit(long id, PickableUnit pickableUnit, String modelName, String hoverName)
	{
		this.id = id;
		this.pickableUnit = pickableUnit;
		this.modelName = modelName;
		this.hoverName = hoverName;
		
		this.determineColor();
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public PickableUnit getPickableUnit()
	{
		return this.pickableUnit;
	}
	
	public float[] getColor()
	{
		return this.color;
	}
	
	public float[] getConditionedColor()
	{
		return this.conditionedcolor;
	}
	
	public String getHoverName()
	{
		return this.hoverName;
	}
	
	public String getModelName()
	{
		return this.modelName;
	}
	
	public boolean isPlayer()
	{
		return (this.getModelName().contains("model.creature.humanoid.human.player") && !this.getModelName().contains("zombie"));
	}
	
	public boolean isEnemyPlayer()
	{
		return this.getModelName().contains("ENEMY");
	}
	
	public boolean isMob()
	{
		return this.getModelName().contains("model.creature") && !this.getModelName().contains("humanoid.human");
	}
	
	public boolean isAggroMob()
	{
		for(String mob : aggroMOBS)
		{
			if(this.getHoverName().contains(mob))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isConditioned()
	{
		for(String condition : conditionedMOBS)
		{
			if(this.getHoverName().contains(condition))
			{
				this.condition = condition;
				return true;
			}
		}
		return false;
	}
	
	public boolean isUnique()
	{
		for(String mob : uniqueMOBS)
		{
			if(this.getHoverName().contains(mob))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isSpecial()
	{
		for(String item : specialITEMS)
		{
			if(this.getHoverName().contains(item))
			{
				return true;
			}
			if(this.getModelName().contains(WurmEspMod.search))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isSpotted()
	{
		for(String item : spottedITEMS)
		{
			if(this.getHoverName().contains(item))
			{
				return true;
			}
			if(this.getModelName().contains(WurmEspMod.search))
			{
				return true;
			}
		}
		if(WurmEspMod.searchType == SEARCHTYPE.HOVER)
		{
			if(this.getHoverName().contains(WurmEspMod.search))
			{
				return true;
			}
		}else if(WurmEspMod.searchType == SEARCHTYPE.MODEL)
		{
			if(this.getModelName().contains(WurmEspMod.search))
			{
				return true;
			}
		}else if(WurmEspMod.searchType == SEARCHTYPE.BOTH)
		{
			if(this.getHoverName().contains(WurmEspMod.search))
			{
				return true;
			}else if(this.getModelName().contains(WurmEspMod.search))
			{
				return true;
			}
		}
		return false;
	}
	
	private void determineColor()
	{
		if(this.isPlayer())
		{
			if(!this.isEnemyPlayer())
			{
				this.color = colorPlayers;
			}
			else
			{
				this.color = colorPlayersEnemy;
			}
		}
		else if(this.isUnique())
		{
			this.color = colorUniques;
		}
		else if(this.isAggroMob())
		{
			this.color = colorMobsAggro;
		}
		else if(this.isMob())
		{
			this.color = colorMobs;
		}
		else if(this.isSpecial())
		{
			this.color = colorSpecials;
		}
		else if(this.isSpotted())
		{
			this.color = colorSpotted;
		}
		if(this.isConditioned())
		{
			float[] color = new float[] {0.0f, 0.0f, 0.0f};
			switch(this.condition) {
				case "alert":
					color = colorAlert;
					break;
				case "angry":
					color = colorAngry;
					break;
				case "champion":
					color = colorChampion;
					break;
				case "diseased":
					color = colorDiseased;
					break;
				case "fierce":
					color = colorFierce;
					break;
				case "greenish":
					color = colorGreenish;
					break;
				case "hardened":
					color = colorHardened;
					break;
				case "lurking":
					color = colorLurking;
					break;
				case "raging":
					color = colorRaging;
					break;
				case "scared":
					color = colorScared;
					break;
				case "slow":
					color = colorSlow;
					break;
				case "sly":
					color = colorSly;
					break;
			}
						
			
			this.conditionedcolor = color;
		}
	}
	
	public void renderUnit(Queue queue, boolean showconditioned) {
		if (this.pickableUnit == null) {
			return;
		}
		
	    float br = 3.5F;
	    
	    RenderState renderStateFill = new RenderState();
	    RenderState renderStateFillDepth = new RenderState();
	    RenderState renderStateOutline = new RenderState();
	    Color color = new Color();
	    if(this.isConditioned() && showconditioned)
	    {
	    	color.set(this.conditionedcolor[0], this.conditionedcolor[1], this.conditionedcolor[2]);
	    }
	    else
	    {
	    	color.set(this.color[0], this.color[1], this.color[2]);
	    }
	    
	    color.a = br;
	    
	    renderStateFill.alphaval = color.a;
	    color.a *= this.pickableUnit.getOutlineColor().a;
	    
	    PickRenderer tmp1217_1214 = WurmEspMod._pickRenderer;
	    CustomPickFillRender customPickFill = tmp1217_1214.new CustomPickFillRender();
	    
	    PickRenderer tmp1237_1234 = WurmEspMod._pickRenderer;
	    CustomPickFillDepthRender customPickFillDepth = tmp1237_1234.new CustomPickFillDepthRender();
	    
	    PickRenderer tmp1257_1254 = WurmEspMod._pickRenderer;
	    CustomPickOutlineRender customPickOutline = tmp1257_1254.new CustomPickOutlineRender();
	      
	    renderStateFill.twosided = false;
	    renderStateFill.depthtest = Primitive.TestFunc.ALWAYS;
	    renderStateFill.depthwrite = true;
	    renderStateFill.customstate = customPickFill;
	    
	    this.pickableUnit.renderPicked(queue, renderStateFill, color);
	    
	    color.a = (br * 0.25F);
	    renderStateOutline.alphaval = color.a;
	    color.a *= this.pickableUnit.getOutlineColor().a;
	    
	    renderStateOutline.twosided = false;
	    renderStateOutline.depthtest = Primitive.TestFunc.LESS;
	    renderStateOutline.depthwrite = false;
	    renderStateOutline.blendmode = Primitive.BlendMode.ALPHABLEND;
	    renderStateOutline.customstate = customPickOutline;
	    
	    this.pickableUnit.renderPicked(queue, renderStateOutline, color);
	    
	    renderStateFillDepth.customstate = customPickFillDepth;
	    renderStateFillDepth.depthtest = Primitive.TestFunc.ALWAYS;
	    this.pickableUnit.renderPicked(queue, renderStateFillDepth, color);
	}
}
