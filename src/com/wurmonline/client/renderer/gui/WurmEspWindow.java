package com.wurmonline.client.renderer.gui;

import com.wurmonline.client.game.World;

import net.encode.wurmesp.WurmEspMod;

public class WurmEspWindow extends WWindow{
	private WurmBorderPanel mainPanel;
	
	@SuppressWarnings("unchecked")
	public WurmEspWindow(World world)
	{
		super("Esp", true);
	    setTitle("Esp");
	    this.mainPanel = new WurmBorderPanel("Esp");
	    
	    WurmArrayPanel<WButton> buttons = new WurmArrayPanel("Esp buttons", 0);
	    buttons.addComponent(new WButton(WurmEspMod.players ? "Players On" : "Players Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.players = !WurmEspMod.players;
	        	p0.label = WurmEspMod.players ? "Players On" : "Players Off";
	        }
	    }));
	    
	    buttons.addComponent(new WButton(WurmEspMod.mobs ? "Mobs On" : "Mobs Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.mobs = !WurmEspMod.mobs;
	        	p0.label = WurmEspMod.mobs ? "Mobs On" : "Mobs Off";
	        }
	    }));
	    
	    buttons.addComponent(new WButton(WurmEspMod.specials ? "Specials On" : "Specials Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.specials = !WurmEspMod.specials;
	        	p0.label = WurmEspMod.specials ? "Specials On" : "Specials Off";
	        }
	    }));
	    
	    buttons.addComponent(new WButton(WurmEspMod.dragons ? "Dragons On" : "Dragons Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.dragons = !WurmEspMod.dragons;
	        	p0.label = WurmEspMod.dragons ? "Dragons On" : "Dragons Off";
	        }
	    }));
	    
	    buttons.addComponent(new WButton(WurmEspMod.champions ? "Champions On" : "Champions Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.champions = !WurmEspMod.champions;
	        	p0.label = WurmEspMod.champions ? "Champions On" : "Champions Off";
	        }
	    }));
	    
	    this.mainPanel.setComponent(buttons, 1);
	    setComponent(this.mainPanel);
	    setInitialSize(85, 112, false);
	    layout();
	    this.sizeFlags = 3;
	}
}
