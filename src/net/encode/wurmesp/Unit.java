package net.encode.wurmesp;

import java.util.logging.Level;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.lwjgl.opengl.GL11;

import com.wurmonline.client.renderer.GroundItemData;
import com.wurmonline.client.renderer.PickableUnit;
import com.wurmonline.client.renderer.cell.CreatureCellRenderable;
import com.wurmonline.client.renderer.cell.GroundItemCellRenderable;

public class Unit {
	private long id;
	private PickableUnit pickableUnit;
	private boolean isCreature;
	private float[] color;
	private GroundItemData item;
	
	public static float[] colorPlayers = {0.0f, 0.0f, 0.0f};
	public static float[] colorPlayersEnemy = {0.0f, 0.0f, 0.0f};
	public static float[] colorMobs = {0.0f, 0.0f, 0.0f};
	public static float[] colorMobsAggro = {0.0f, 0.0f, 0.0f};
	public static float[] colorSpecials = {0.0f, 0.0f, 0.0f};
	public static float[] colorUniques = {0.0f, 0.0f, 0.0f};
	public static float[] colorChampions = {0.0f, 0.0f, 0.0f};
	
	private String[] aggroMOBS = {
			" anaconda",
			" black bear",
			" black wolf",
			" brown bear",
			" cave bug",
			" crab",
			" crocodile",
			" fog Spider",
			" goblin",
			" hell hound",
			" hell scorpious",
			" huge spider",
			" large rat",
			" lava fiend",
			" lava spider",
			" mountain lion",
			" rabid hyena",
			" scorpion",
			" sea serpent",
			" seal",
			" shark",
			" troll",
			" unicorn",
			" wild cat"
			};
	
	private String[] uniqueMOBS = {
			" forest giant",
			" goblin leader",
			" kyklops",
			" troll king",
			" dragon"
			};
	
	private String[] specialITEMS = {
			"source",
			"treasure"
			};
	
	public Unit(long id, PickableUnit pickableUnit, boolean isCreature)
	{
		this.id = id;
		this.pickableUnit = pickableUnit;
		this.isCreature = isCreature;
		
		if(!this.isCreature)
		{
			try {
				this.item = ReflectionUtil.getPrivateField(pickableUnit,
						ReflectionUtil.getField(((GroundItemCellRenderable)pickableUnit).getClass(), "item"));
			} catch (IllegalArgumentException e) {
				WurmEspMod.logger.log(Level.WARNING, e.getMessage());
			} catch (IllegalAccessException e) {
				WurmEspMod.logger.log(Level.WARNING, e.getMessage());
			} catch (ClassCastException e) {
				WurmEspMod.logger.log(Level.WARNING, e.getMessage());
			} catch (NoSuchFieldException e) {
				WurmEspMod.logger.log(Level.WARNING, e.getMessage());
			}
		}
		
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
	
	public String getHoverName()
	{
		return this.pickableUnit.getHoverName();
	}
	
	public String getModelName()
	{
		return this.isCreature ? ((CreatureCellRenderable)this.pickableUnit).getModelName().toString() : this.item.getModelName().toString();
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
		for(String mob : this.aggroMOBS)
		{
			if(this.pickableUnit.getHoverName().contains(mob))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isChampion()
	{
		return this.getModelName().contains("champion");
	}
	
	public boolean isUnique()
	{
		for(String mob : this.uniqueMOBS)
		{
			if(this.pickableUnit.getHoverName().contains(mob))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isSpecial()
	{
		for(String item : this.specialITEMS)
		{
			if(this.pickableUnit.getHoverName().contains(item))
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
			this.color = colorPlayers;
		}
		else if(this.isEnemyPlayer())
		{
			this.color = colorPlayersEnemy;
		}
		else if(this.isUnique())
		{
			this.color = colorUniques;
		}
		else if(this.isChampion())
		{
			this.color = colorChampions;
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
		else
		{
			this.color = new float[]{0.0f,0.0f,0.0f};
		}
	}
	
	public void renderUnit()
	{
		this.render();
	}
	
	private void render() {
		if (this.pickableUnit == null) {
			return;
		}
		float br = 3.5f;
		GL11.glPushMatrix();

		GL11.glDisable(2884);

		float[] col = this.color;

		GL11.glDepthRange(0.0D, 0.009999999776482582D);

		GL11.glColorMask(false, false, false, false);
		this.pickableUnit.renderPicked();

		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);

		GL11.glColor4f(col[0], col[1], col[2], br);
		GL11.glDepthFunc(513);
		GL11.glDepthRange(0.0D, 1.0D);
		GL11.glLineWidth(br);
		GL11.glPolygonMode(1028, 6913);

		GL11.glColorMask(true, true, true, true);
		this.pickableUnit.renderPicked();

		GL11.glClear(256);

		GL11.glDepthFunc(519);
		GL11.glDepthRange(0.0D, 0.009999999776482582D);
		GL11.glPolygonMode(1028, 6914);

		GL11.glColorMask(false, false, false, false);
		this.pickableUnit.renderPicked();

		GL11.glColor4f(col[0], col[1], col[2], br * 0.25F);
		GL11.glDepthFunc(513);
		GL11.glDepthRange(0.0D, 1.0D);
		GL11.glPolygonMode(1028, 6913);

		GL11.glColorMask(true, true, true, true);
		this.pickableUnit.renderPicked();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthFunc(515);
		GL11.glDepthRange(0.0D, 1.0D);
		GL11.glLineWidth(1.0F);
		GL11.glPolygonMode(1028, 6914);

		GL11.glDisable(3042);

		GL11.glEnable(2884);

		GL11.glPopMatrix();
	}
}
