package net.encode.wurmesp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;
import com.wurmonline.client.game.World;
import com.wurmonline.client.renderer.GroundItemData;
import com.wurmonline.client.renderer.PickableUnit;
import com.wurmonline.client.renderer.cell.CreatureCellRenderable;
import com.wurmonline.client.renderer.gui.HeadsUpDisplay;
import com.wurmonline.client.renderer.gui.MainMenu;
import com.wurmonline.client.renderer.gui.WurmComponent;
import com.wurmonline.client.renderer.gui.WurmEspWindow;
import com.wurmonline.client.settings.SavePosManager;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class WurmEspMod implements WurmClientMod, Initable, PreInitable, Configurable {
	public static HeadsUpDisplay hud;

	public static Logger logger = Logger.getLogger("WurmEspMod");
	private List<Unit> pickableUnits = new ArrayList<Unit>();
	private List<Unit> toRemove = new ArrayList<Unit>();

	public static boolean players = true;
	public static boolean mobs = false;
	public static boolean specials = true;
	public static boolean uniques = true;
	public static boolean champions = true;
	
	
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
				case "uniques":
					uniques = !uniques;
					hud.consoleOutput("ESP uniques changed");
					break;
				case "champions":
					champions = !champions;
					hud.consoleOutput("ESP champions changed");
					break;
				default:
					hud.consoleOutput("Usage: esp {players|mobs|specials|uniques|champions}");
				}
				return true;
			} else {
				hud.consoleOutput("Usage: esp {players|mobs|specials|uniques|champions}");
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
		players = Boolean.valueOf(
				properties.getProperty("players", 
						Boolean.toString(players)));
		mobs = Boolean.valueOf(
				properties.getProperty("mobs", 
						Boolean.toString(mobs)));
		specials = Boolean.valueOf(
				properties.getProperty("specials", 
						Boolean.toString(specials)));
		uniques = Boolean.valueOf(
				properties.getProperty("uniques", 
						Boolean.toString(uniques)));
		champions = Boolean.valueOf(
				properties.getProperty("champions", 
						Boolean.toString(champions)));
		
		Unit.colorPlayers = colorStringToFloatA(
				properties.getProperty("colorPlayers", 
						colorFloatAToString(Unit.colorPlayers)));
		Unit.colorPlayersEnemy = colorStringToFloatA(
				properties.getProperty("colorPlayersEnemy", 
						colorFloatAToString(Unit.colorPlayersEnemy)));
		Unit.colorMobs = colorStringToFloatA(
				properties.getProperty("colorMobs", 
						colorFloatAToString(Unit.colorMobs)));
		Unit.colorMobsAggro = colorStringToFloatA(
				properties.getProperty("colorMobsAggro", 
						colorFloatAToString(Unit.colorMobsAggro)));
		Unit.colorSpecials = colorStringToFloatA(
				properties.getProperty("colorSpecials", 
						colorFloatAToString(Unit.colorSpecials)));
		Unit.colorUniques = colorStringToFloatA(
				properties.getProperty("colorUniques", 
						colorFloatAToString(Unit.colorUniques)));
		Unit.colorChampions = colorStringToFloatA(
				properties.getProperty("colorChampions", 
						colorFloatAToString(Unit.colorChampions)));
		
		logger.log(Level.INFO, "Config loaded");
	}
	
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
						this.initEspWR();
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.WorldRender", "renderPickedItem",
					"()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						for (Unit unit : this.pickableUnits) {
							if ((players && unit.isPlayer())
									|| (uniques && unit.isUnique())
									|| (champions && unit.isChampion())
									|| (mobs && unit.isMob())
									|| (specials && unit.isSpecial()))
							{
								unit.renderUnit();
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

						Unit unit = new Unit(item.getId(), (PickableUnit) proxy, false);
						
						if(unit.isSpecial()) 
						{
							this.pickableUnits.add(unit);
						}
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.GroundItemCellRenderable",
					"removed", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						Class<?> cls = proxy.getClass();
						GroundItemData item = ReflectionUtil.getPrivateField(proxy,
								ReflectionUtil.getField(cls, "item"));

						for (Unit unit : pickableUnits) {
							if (unit.getId() == item.getId()) {
								toRemove.add(unit);
							}
						}
						
						for (Unit unit : toRemove) {
							if (unit.getId() == item.getId()) {
								pickableUnits.remove(unit);
							}
						}
						
						toRemove.clear();
						
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.CreatureCellRenderable",
					"initialize", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						
						CreatureCellRenderable creature = (CreatureCellRenderable) proxy;
						
						Unit unit = new Unit(creature.getId(), (PickableUnit) proxy, true);
						
						
						if(unit.isPlayer() || unit.isMob())
						{
							this.pickableUnits.add(unit);
						}
						
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.CreatureCellRenderable",
					"removed", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);

						CreatureCellRenderable creature = (CreatureCellRenderable) proxy;

						for (Unit unit : pickableUnits) {
							if (unit.getId() == creature.getId()) {
								toRemove.add(unit);
							}
						}
						
						for (Unit unit : toRemove) {
							if (unit.getId() == creature.getId()) {
								pickableUnits.remove(unit);
							}
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

	}
	
	
	@SuppressWarnings("unchecked")
	private void initEspWR()
	{
		try
        {
          World world = (World)ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "world"));
          
          WurmEspWindow wurmEspWindow = new WurmEspWindow(world);
          
          MainMenu mainMenu = (MainMenu)ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "mainMenu"));
          mainMenu.registerComponent("Esp", wurmEspWindow);
          
          List<WurmComponent> components = (List)ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "components"));
          components.add(wurmEspWindow);
          
          SavePosManager savePosManager = (SavePosManager)ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "savePosManager"));
          savePosManager.registerAndRefresh(wurmEspWindow, "wurmespwindow");
        }
        catch (IllegalArgumentException|IllegalAccessException|ClassCastException|NoSuchFieldException e)
        {
          throw new RuntimeException(e);
        }
	}
}