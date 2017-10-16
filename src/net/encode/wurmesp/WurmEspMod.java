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

import com.wurmonline.client.game.CaveDataBuffer;
import com.wurmonline.client.game.World;
import com.wurmonline.client.renderer.GroundItemData;
import com.wurmonline.client.renderer.PickRenderer;
import com.wurmonline.client.renderer.PickableUnit;
import com.wurmonline.client.renderer.backend.Queue;
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
	
	private CronoManager cronoManager;
	private XRayManager xrayManager;
	
	public static CaveDataBuffer _caveBuffer = null;
	public static List<float[]> _terrain = new ArrayList<float[]>();

	public static enum SEARCHTYPE {
		NONE, HOVER, MODEL, BOTH
	};

	public static String search = "defaultnosearch";
	public static SEARCHTYPE searchType = SEARCHTYPE.NONE;

	public static boolean players = true;
	public static boolean mobs = false;
	public static boolean specials = true;
	public static boolean uniques = true;
	public static boolean champions = true;
	public static boolean xray = false;
	public static boolean xraythread = true;
	public static boolean xrayrefreshthread = true;
	public static int xraydiameter = 32;
	public static int xrayrefreshrate = 5;

	public static PickRenderer _pickRenderer;

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
				case "xray":
					xray = !xray;
					hud.consoleOutput("ESP xray changed");
					break;
				default:
					hud.consoleOutput("Usage: esp {players|mobs|specials|uniques|champions|xray}");
				}
				return true;
			} else if (data.length > 2) {

				switch (data[1]) {
				case "search":
					if (data[2].equals("h")) {
						search = data[3];
						searchType = SEARCHTYPE.HOVER;
						hud.consoleOutput("Searching for " + search + " in HoverName");
					} else if (data[2].equals("m")) {
						search = data[3];
						searchType = SEARCHTYPE.MODEL;
						hud.consoleOutput("Searching for " + search + " in ModelName");
					} else if (data[2].equals("hm")) {
						search = data[3];
						searchType = SEARCHTYPE.BOTH;
						hud.consoleOutput("Searching for " + search + " in HoverName and ModelName");
					} else if (data[2].equals("off")) {
						search = "";
						searchType = SEARCHTYPE.NONE;
						hud.consoleOutput("Searching off");
					} else {
						hud.consoleOutput("Usage: esp search {h/m/hm/off} <name>");
					}
					break;
				default:
					hud.consoleOutput("Usage: esp search {h/m/hm/off} <name>");
				}
				return true;
			} else {
				hud.consoleOutput("Error.");
			}
			return true;
		}
		return false;
	}

	private float[] colorStringToFloatA(String color) {
		String[] colors = color.split(",");
		float[] colorf = { Float.valueOf(colors[0]) / 255.0f, Float.valueOf(colors[1]) / 255.0f,
				Float.valueOf(colors[2]) / 255.0f };
		return colorf;
	}

	private String colorFloatAToString(float[] color) {
		String colors = String.valueOf(color[0] * 255.0f) + "," + String.valueOf(color[1] * 255.0f) + ","
				+ String.valueOf(color[2] * 255.0f);
		return colors;
	}

	@Override
	public void configure(Properties properties) {
		players = Boolean.valueOf(properties.getProperty("players", Boolean.toString(players)));
		mobs = Boolean.valueOf(properties.getProperty("mobs", Boolean.toString(mobs)));
		specials = Boolean.valueOf(properties.getProperty("specials", Boolean.toString(specials)));
		uniques = Boolean.valueOf(properties.getProperty("uniques", Boolean.toString(uniques)));
		champions = Boolean.valueOf(properties.getProperty("champions", Boolean.toString(champions)));
		xray = Boolean.valueOf(properties.getProperty("xray", Boolean.toString(xray)));
		xraythread = Boolean.valueOf(properties.getProperty("xray", Boolean.toString(xraythread)));
		xrayrefreshthread = Boolean.valueOf(properties.getProperty("xray", Boolean.toString(xrayrefreshthread)));
		xraydiameter = Integer.parseInt(properties.getProperty("xraydiameter", Integer.toString(xraydiameter)));
		xrayrefreshrate = Integer.parseInt(properties.getProperty("xrayrefreshrate", Integer.toString(xrayrefreshrate)));

		Unit.colorPlayers = colorStringToFloatA(
				properties.getProperty("colorPlayers", colorFloatAToString(Unit.colorPlayers)));
		Unit.colorPlayersEnemy = colorStringToFloatA(
				properties.getProperty("colorPlayersEnemy", colorFloatAToString(Unit.colorPlayersEnemy)));
		Unit.colorMobs = colorStringToFloatA(properties.getProperty("colorMobs", colorFloatAToString(Unit.colorMobs)));
		Unit.colorMobsAggro = colorStringToFloatA(
				properties.getProperty("colorMobsAggro", colorFloatAToString(Unit.colorMobsAggro)));
		Unit.colorSpecials = colorStringToFloatA(
				properties.getProperty("colorSpecials", colorFloatAToString(Unit.colorSpecials)));
		Unit.colorUniques = colorStringToFloatA(
				properties.getProperty("colorUniques", colorFloatAToString(Unit.colorUniques)));
		Unit.colorChampions = colorStringToFloatA(
				properties.getProperty("colorChampions", colorFloatAToString(Unit.colorChampions)));

		Unit.aggroMOBS = properties.getProperty("aggroMOBS").split(";");
		Unit.uniqueMOBS = properties.getProperty("uniqueMOBS").split(";");
		Unit.specialITEMS = properties.getProperty("specialITEMS").split(";");

		logger.log(Level.INFO, "Config loaded");
	}

	@Override
	public void init() {
		logger.fine("Initializing");

		try {
			xrayManager = new XRayManager();
			cronoManager = new CronoManager(xrayrefreshrate*1000);
			
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
					"(Lcom/wurmonline/client/renderer/backend/Queue;)V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						Class<?> cls = proxy.getClass();

						World world = ReflectionUtil.getPrivateField(proxy, ReflectionUtil.getField(cls, "world"));
						
						PickRenderer pickRenderer = ReflectionUtil.getPrivateField(proxy,
								ReflectionUtil.getField(cls, "pickRenderer"));
						_pickRenderer = pickRenderer;

						Queue queuePick = ReflectionUtil.getPrivateField(proxy,
								ReflectionUtil.getField(cls, "queuePick"));

						for (Unit unit : this.pickableUnits) {
							if ((players && unit.isPlayer()) || (uniques && unit.isUnique())
									|| (champions && unit.isChampion()) || (mobs && unit.isMob())
									|| (specials && unit.isSpecial())) {
								unit.renderUnit(queuePick);
							}
						}
						if (xray && world.getPlayer().getPos().getLayer() < 0) {
							xrayManager._setWQ(world,queuePick);
							
							if(cronoManager.hasEnded())
							{
								if(xrayrefreshthread)
								{
									Thread refreshThread = new Thread(() -> {
										xrayManager._refreshData();
									});
									
									refreshThread.setPriority(Thread.MAX_PRIORITY);
									
									refreshThread.start();
								}
								else
								{
									xrayManager._refreshData();
								}
								cronoManager.restart(xrayrefreshrate*1000);
							}
							if(xrayManager._first)
							{ 
								if(xrayrefreshthread)
								{
									Thread refreshThread = new Thread(() -> {
										xrayManager._refreshData();
									});
									
									refreshThread.setPriority(Thread.MAX_PRIORITY);
									
									refreshThread.start();
								}
								else
								{
									xrayManager._refreshData();
								}
								xrayManager._first = false;
							}
							
							if(xraythread)
							{
								Thread xrayThread = new Thread(() -> {
									xrayManager._queueXray();
								});
								
								xrayThread.setPriority(Thread.MAX_PRIORITY);
								
								xrayThread.start();
							}
							else
							{
								xrayManager._queueXray();
							}
						}

						return null;
					});
			
			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.MobileModelRenderable",
					"initialize", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						PickableUnit item = (PickableUnit) proxy;

						Unit unit = new Unit(item.getId(), item, true);

						if (unit.isPlayer() || unit.isMob()) {
							this.pickableUnits.add(unit);
						} else if (unit.isSpecial()) {
							this.pickableUnits.add(unit);
						}
						return null;
					});

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.MobileModelRenderable",
					"removed", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						PickableUnit item = (PickableUnit) proxy;

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

			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.GroundItemCellRenderable",
					"initialize", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						Class<?> cls = proxy.getClass();
						GroundItemData item = ReflectionUtil.getPrivateField(proxy,
								ReflectionUtil.getField(cls, "item"));

						Unit unit = new Unit(item.getId(), (PickableUnit) proxy, false);

						if (unit.isSpecial()) {
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

			logger.fine("Loaded");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Error loading mod", e);
		}
	}

	@Override
	public void preInit() {

	}

	@SuppressWarnings("unchecked")
	private void initEspWR() {
		try {
			World world = (World) ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "world"));

			WurmEspWindow wurmEspWindow = new WurmEspWindow(world);

			MainMenu mainMenu = (MainMenu) ReflectionUtil.getPrivateField(hud,
					ReflectionUtil.getField(hud.getClass(), "mainMenu"));
			mainMenu.registerComponent("Esp", wurmEspWindow);

			List<WurmComponent> components = (List) ReflectionUtil.getPrivateField(hud,
					ReflectionUtil.getField(hud.getClass(), "components"));
			components.add(wurmEspWindow);

			SavePosManager savePosManager = (SavePosManager) ReflectionUtil.getPrivateField(hud,
					ReflectionUtil.getField(hud.getClass(), "savePosManager"));
			savePosManager.registerAndRefresh(wurmEspWindow, "wurmespwindow");
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}