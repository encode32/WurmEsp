package net.encode.wurmesp;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;
import org.lwjgl.opengl.GL11;

import com.wurmonline.client.renderer.GroundItemData;
import com.wurmonline.client.renderer.PickableUnit;
import com.wurmonline.client.renderer.cell.CreatureCellRenderable;
import com.wurmonline.client.renderer.cell.GroundItemCellRenderable;
import com.wurmonline.client.renderer.gui.HeadsUpDisplay;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class WurmEspMod implements WurmClientMod, Initable, PreInitable, Configurable {
	public static HeadsUpDisplay hud;

	private static Logger logger = Logger.getLogger("WurmEspMod");
	private HashMap<Entry<Long, ESPTYPE>, Entry<PickableUnit, float[]>> pickableUnits = new HashMap<Entry<Long, ESPTYPE>, Entry<PickableUnit, float[]>>();
	private List<Entry<Long, ESPTYPE>> toRemove = new ArrayList<Entry<Long, ESPTYPE>>();

	private List<String> modelNames = new ArrayList<String>();

	static boolean players = true;
	static boolean mobs = false;
	static boolean specials = true;
	static boolean dragons = true;
	static boolean champions = true;

	float[] colorPlayers = {0.0f, 0.0f, 0.0f};
	float[] colorPlayersEnemy = {0.0f, 0.0f, 0.0f};
	float[] colorMobs = {0.0f, 0.0f, 0.0f};
	float[] colorMobsAggro = {0.0f, 0.0f, 0.0f};
	float[] colorSpecials = {0.0f, 0.0f, 0.0f};
	float[] colorDragons = {0.0f, 0.0f, 0.0f};
	float[] colorChampions = {0.0f, 0.0f, 0.0f};
	

	private enum ESPTYPE {
		PLAYER, MOB, SPECIAL, DRAGON, CHAMPION
	}
	
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
	
	private boolean isAggroMob(String name)
	{
		for(String mob : aggroMOBS)
		{
			if(name.contains(mob))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleInput(final String cmd, final String[] data) {
		if (cmd.equals("esp")) {
			if (data.length == 2) {

				switch (data[1]) {
				case "players":
					players = !players;
					hud.consoleOutput("ESP players changed");
					break;
				case "mobs":
					mobs = !mobs;
					hud.consoleOutput("ESP mobs changed");
					break;
				case "specials":
					specials = !specials;
					hud.consoleOutput("ESP specials changed");
					break;
				case "dragons":
					dragons = !dragons;
					hud.consoleOutput("ESP dragons changed");
					break;
				case "champions":
					champions = !champions;
					hud.consoleOutput("ESP champions changed");
					break;
				default:
					hud.consoleOutput("Usage: esp {players|mobs|specials|dragons|champions}");
				}
				return true;
			} else {
				hud.consoleOutput("Usage: esp {players|mobs|specials|dragons|champions}");
			}
			return true;
		}
		return false;
	}

	private float[] colorStringToFloatA(String color)
	{
		String[] colors = color.split(",");
		float[] colorf = {
				Float.valueOf(colors[0])/255.0f,
				Float.valueOf(colors[1])/255.0f,
				Float.valueOf(colors[2])/255.0f};
		return colorf;
	}
	
	private String colorFloatAToString(float[] color)
	{
		String colors = 
				String.valueOf(color[0]*255.0f) + "," +
				String.valueOf(color[1]*255.0f) + "," +
				String.valueOf(color[2]*255.0f);
		return colors;
	}
	
	@Override
	public void configure(Properties properties) {
		colorPlayers = colorStringToFloatA(
				properties.getProperty("colorPlayers", 
						colorFloatAToString(colorPlayers)));
		colorPlayersEnemy = colorStringToFloatA(
				properties.getProperty("colorPlayersEnemy", 
						colorFloatAToString(colorPlayersEnemy)));
		colorMobs = colorStringToFloatA(
				properties.getProperty("colorMobs", 
						colorFloatAToString(colorMobs)));
		colorMobsAggro = colorStringToFloatA(
				properties.getProperty("colorMobsAggro", 
						colorFloatAToString(colorMobsAggro)));
		colorSpecials = colorStringToFloatA(
				properties.getProperty("colorSpecials", 
						colorFloatAToString(colorSpecials)));
		colorDragons = colorStringToFloatA(
				properties.getProperty("colorDragons", 
						colorFloatAToString(colorDragons)));
		colorChampions = colorStringToFloatA(
				properties.getProperty("colorChampions", 
						colorFloatAToString(colorChampions)));
		
		logger.log(Level.INFO, "Config loaded");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		logger.fine("Initializing");

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();

			CtClass ctWurmConsole = classPool.getCtClass("com.wurmonline.client.console.WurmConsole");
			ctWurmConsole.getMethod("handleDevInput", "(Ljava/lang/String;[Ljava/lang/String;)Z")
					.insertBefore("if (net.encode.wurmesp.WurmEspMod.handleInput($1,$2)) return true;");

			CtClass ctWurmArrow = classPool.getCtClass("com.wurmonline.client.renderer.cell.ProjectileCellRenderable");
			CtMethod m = CtNewMethod.make("public void initialize() { return; }", ctWurmArrow);
			ctWurmArrow.addMethod(m);

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.HeadsUpDisplay", "init", "(II)V",
					() -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						hud = (HeadsUpDisplay) proxy;
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.WorldRender", "renderPickedItem",
					"()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						for (Entry<Entry<Long, ESPTYPE>, Entry<PickableUnit, float[]>> unit : pickableUnits
								.entrySet()) {
							if ((players && unit.getKey().getValue() == ESPTYPE.PLAYER)
									|| (mobs && unit.getKey().getValue() == ESPTYPE.MOB)
									|| (specials && unit.getKey().getValue() == ESPTYPE.SPECIAL)
									|| (dragons && unit.getKey().getValue() == ESPTYPE.DRAGON)
									|| (champions && unit.getKey().getValue() == ESPTYPE.CHAMPION)){
								renderPickedItem(unit.getValue().getKey(), unit.getValue().getValue());
							}
						}
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.GroundItemCellRenderable",
					"initialize", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						Class<?> cls = proxy.getClass();
						GroundItemData item = ReflectionUtil.getPrivateField(proxy,
								ReflectionUtil.getField(cls, "item"));

						String name = ((GroundItemCellRenderable) proxy).getHoverName();
						if (name.contains("source") || name.contains("treasure")) {
							pickableUnits.put(new AbstractMap.SimpleEntry<Long, ESPTYPE>(item.getId(), ESPTYPE.SPECIAL),
									new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
											colorSpecials));
						}
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.GroundItemCellRenderable",
					"removed", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						Class<?> cls = proxy.getClass();
						GroundItemData item = ReflectionUtil.getPrivateField(proxy,
								ReflectionUtil.getField(cls, "item"));

						for (@SuppressWarnings("rawtypes")
						Entry entry : pickableUnits.keySet()) {
							if (entry.getKey().equals(item.getId())) {
								toRemove.add(entry);
							}
						}
						for (@SuppressWarnings("rawtypes")
						Entry entry : toRemove) {
							pickableUnits.remove(entry);
						}
						toRemove.clear();
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.CreatureCellRenderable",
					"initialize", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						String modelName = ((CreatureCellRenderable) proxy).getModelName().toString();
						String hoverName = ((CreatureCellRenderable) proxy).getHoverName();

						for (String name : modelNames) {
							if (modelName.contains(name)) {

								if (modelName.contains("model.creature.humanoid.human.player")) {
									if (!modelName.contains("zombie")) {
										long id = ((CreatureCellRenderable) proxy).getId();
										
										if(hoverName.contains("ENEMY"))
										{
											pickableUnits.put(
													new AbstractMap.SimpleEntry<Long, ESPTYPE>(id, ESPTYPE.PLAYER),
													new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
															colorPlayersEnemy));
										}
										else
										{
											pickableUnits.put(
													new AbstractMap.SimpleEntry<Long, ESPTYPE>(id, ESPTYPE.PLAYER),
													new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
															colorPlayers));
										}
									}
								} else if (modelName.contains("dragon") || modelName.contains("drake")) {
									long id = ((CreatureCellRenderable) proxy).getId();

									pickableUnits.put(new AbstractMap.SimpleEntry<Long, ESPTYPE>(id, ESPTYPE.DRAGON),
											new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
													colorDragons));
								} else {
									if(champions && hoverName.contains("champion"))
									{
										long id = ((CreatureCellRenderable) proxy).getId();

										pickableUnits.put(new AbstractMap.SimpleEntry<Long, ESPTYPE>(id, ESPTYPE.CHAMPION),
												new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
														colorChampions));
									}
									else
									{
										if(isAggroMob(hoverName))
										{
											long id = ((CreatureCellRenderable) proxy).getId();

											pickableUnits.put(new AbstractMap.SimpleEntry<Long, ESPTYPE>(id, ESPTYPE.MOB),
													new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
															colorMobsAggro));
										}
										else
										{
											long id = ((CreatureCellRenderable) proxy).getId();

											pickableUnits.put(new AbstractMap.SimpleEntry<Long, ESPTYPE>(id, ESPTYPE.MOB),
													new AbstractMap.SimpleEntry<PickableUnit, float[]>((PickableUnit) proxy,
															colorMobs));
										}
									}
								}
							}
						}
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.CreatureCellRenderable",
					"removed", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);

						long id = ((CreatureCellRenderable) proxy).getId();

						for (@SuppressWarnings("rawtypes")
						Entry entry : pickableUnits.keySet()) {
							if (entry.getKey().equals(id)) {
								toRemove.add(entry);
							}
						}
						for (@SuppressWarnings("rawtypes")
						Entry entry : toRemove) {
							pickableUnits.remove(entry);
						}
						toRemove.clear();
						return null;
					});

			logger.fine("Loaded");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Error loading mod", e);
		}
	}

	@Override
	public void preInit() {
		modelNames.add("model.creature.humanoid.human.player");
		modelNames.add("model.creature");
		// modelNames.add("");
	}

	private void renderPickedItem(PickableUnit pickedItem, float[] color) {
		if (pickedItem == null) {
			return;
		}
		float br = 3.5f;
		GL11.glPushMatrix();

		GL11.glDisable(2884);

		float[] col = color;

		GL11.glDepthRange(0.0D, 0.009999999776482582D);

		GL11.glColorMask(false, false, false, false);
		pickedItem.renderPicked();

		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);

		GL11.glColor4f(col[0], col[1], col[2], br);
		GL11.glDepthFunc(513);
		GL11.glDepthRange(0.0D, 1.0D);
		GL11.glLineWidth(br);
		GL11.glPolygonMode(1028, 6913);

		GL11.glColorMask(true, true, true, true);
		pickedItem.renderPicked();

		GL11.glClear(256);

		GL11.glDepthFunc(519);
		GL11.glDepthRange(0.0D, 0.009999999776482582D);
		GL11.glPolygonMode(1028, 6914);

		GL11.glColorMask(false, false, false, false);
		pickedItem.renderPicked();

		GL11.glColor4f(col[0], col[1], col[2], br * 0.25F);
		GL11.glDepthFunc(513);
		GL11.glDepthRange(0.0D, 1.0D);
		GL11.glPolygonMode(1028, 6913);

		GL11.glColorMask(true, true, true, true);
		pickedItem.renderPicked();

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