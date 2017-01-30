package com.wurmonline.client.renderer.gui;

import com.wurmonline.client.game.World;

import net.encode.wurmesp.WurmEspMod;

public class WurmEspWindow extends WWindow{
	private WurmBorderPanel mainPanel;
	private float[] red = {1.0f,0.0f,0.0f};
    private float[] green = {0.0f,1.0f,0.0f};
    
	@SuppressWarnings("unchecked")
	public WurmEspWindow(World world)
	{
		super("Esp", true);
	    setTitle("Esp");
	    this.mainPanel = new WurmBorderPanel("Esp");
	    
	    WurmArrayPanel<WButton> buttons = new WurmArrayPanel("Esp buttons", 0);
	    
	    WButton button = new WButton(WurmEspMod.players ? "Players On" : "Players Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.players = !WurmEspMod.players;
	        	p0.label = WurmEspMod.players ? "Players On" : "Players Off";
	        	setWButtonStateColor(p0,WurmEspMod.players);
	        }
	    });
	    
	    setWButtonStateColor(button,WurmEspMod.players);
	    buttons.addComponent(button);
	    
	    button = new WButton(WurmEspMod.mobs ? "Mobs On" : "Mobs Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.mobs = !WurmEspMod.mobs;
	        	p0.label = WurmEspMod.mobs ? "Mobs On" : "Mobs Off";
	        	setWButtonStateColor(p0,WurmEspMod.mobs);
	        }
	    });
	    
	    setWButtonStateColor(button,WurmEspMod.mobs);
	    buttons.addComponent(button);
	    
	    button = new WButton(WurmEspMod.specials ? "Specials On" : "Specials Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.specials = !WurmEspMod.specials;
	        	p0.label = WurmEspMod.specials ? "Specials On" : "Specials Off";
	        	setWButtonStateColor(p0,WurmEspMod.specials);
	        }
	    });
	    
	    setWButtonStateColor(button,WurmEspMod.specials);
	    buttons.addComponent(button);
	    
	    button = new WButton(WurmEspMod.uniques ? "Uniques On" : "Uniques Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.uniques = !WurmEspMod.uniques;
	        	p0.label = WurmEspMod.uniques ? "Uniques On" : "Uniques Off";
	        	setWButtonStateColor(p0,WurmEspMod.uniques);
	        }
	    });
	    
	    setWButtonStateColor(button,WurmEspMod.uniques);
	    buttons.addComponent(button);
	    
	    button = new WButton(WurmEspMod.champions ? "Champions On" : "Champions Off", new ButtonListener()
	    {
	        public void buttonPressed(WButton p0) {}
	        
	        public void buttonClicked(WButton p0)
	        {
	        	WurmEspMod.champions = !WurmEspMod.champions;
	        	p0.label = WurmEspMod.champions ? "Champions On" : "Champions Off";
	        	setWButtonStateColor(p0,WurmEspMod.champions);
	        }
	    });
	    
	    setWButtonStateColor(button,WurmEspMod.champions);
	    buttons.addComponent(button);
	    
	    this.mainPanel.setComponent(buttons, 1);
	    setComponent(this.mainPanel);
	    setInitialSize(85, 112, false);
	    layout();
	    this.sizeFlags = 3;
	}
	
	private void setWButtonStateColor(WButton button, boolean state)
	{
		float[] colorText = state ? green : red;
		button.setTextColor(colorText[0], colorText[1], colorText[2]);
	}
}
