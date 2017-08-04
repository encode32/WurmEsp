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
	    this.resizable = true;
	    setInitialSize(85, 128, true);
	    this.mainPanel = new WurmBorderPanel("Esp");
	    
	    WurmArrayPanel<WButtonC> buttons = new WurmArrayPanel("Esp buttons", 0);
	    
	    WButtonC button = new WButtonC(WurmEspMod.players ? "Players On" : "Players Off", new ButtonListenerC()
	    {
	        public void buttonPressed(WButtonC p0) {}
	        
	        public void buttonClicked(WButtonC p0)
	        {
	        	WurmEspMod.players = !WurmEspMod.players;
	        	p0.label = WurmEspMod.players ? "Players On" : "Players Off";
	        	setWButtonStateColor(p0,WurmEspMod.players);
	        }
	        
	        public int buttonRender(WButtonC p0) {
				return p0.parent.parent.parent.width;
				
			}
	    },this.width);
	    
	    setWButtonStateColor(button,WurmEspMod.players);
	    buttons.addComponent(button);
	    
	    button = new WButtonC(WurmEspMod.mobs ? "Mobs On" : "Mobs Off", new ButtonListenerC()
	    {
	        public void buttonPressed(WButtonC p0) {}
	        
	        public void buttonClicked(WButtonC p0)
	        {
	        	WurmEspMod.mobs = !WurmEspMod.mobs;
	        	p0.label = WurmEspMod.mobs ? "Mobs On" : "Mobs Off";
	        	setWButtonStateColor(p0,WurmEspMod.mobs);
	        }
	        
	        public int buttonRender(WButtonC p0) {
	        	return p0.parent.parent.parent.width;
				
			}
	    },this.width);
	    
	    setWButtonStateColor(button,WurmEspMod.mobs);
	    buttons.addComponent(button);
	    
	    button = new WButtonC(WurmEspMod.specials ? "Specials On" : "Specials Off", new ButtonListenerC()
	    {
	        public void buttonPressed(WButtonC p0) {}
	        
	        public void buttonClicked(WButtonC p0)
	        {
	        	WurmEspMod.specials = !WurmEspMod.specials;
	        	p0.label = WurmEspMod.specials ? "Specials On" : "Specials Off";
	        	setWButtonStateColor(p0,WurmEspMod.specials);
	        }
	        
	        public int buttonRender(WButtonC p0) {
	        	return p0.parent.parent.parent.width;
				
			}
	    },this.width);
	    
	    setWButtonStateColor(button,WurmEspMod.specials);
	    buttons.addComponent(button);
	    
	    button = new WButtonC(WurmEspMod.uniques ? "Uniques On" : "Uniques Off", new ButtonListenerC()
	    {
	        public void buttonPressed(WButtonC p0) {}
	        
	        public void buttonClicked(WButtonC p0)
	        {
	        	WurmEspMod.uniques = !WurmEspMod.uniques;
	        	p0.label = WurmEspMod.uniques ? "Uniques On" : "Uniques Off";
	        	setWButtonStateColor(p0,WurmEspMod.uniques);
	        }
	        
	        public int buttonRender(WButtonC p0) {
	        	return p0.parent.parent.parent.width;
				
			}
	    },this.width);
	    
	    setWButtonStateColor(button,WurmEspMod.uniques);
	    buttons.addComponent(button);
	    
	    button = new WButtonC(WurmEspMod.champions ? "Champions On" : "Champions Off", new ButtonListenerC()
	    {
	        public void buttonPressed(WButtonC p0) {}
	        
	        public void buttonClicked(WButtonC p0)
	        {
	        	WurmEspMod.champions = !WurmEspMod.champions;
	        	p0.label = WurmEspMod.champions ? "Champions On" : "Champions Off";
	        	setWButtonStateColor(p0,WurmEspMod.champions);
	        }
	        
			public int buttonRender(WButtonC p0) {
				return p0.parent.parent.parent.width;
				
			}
	    },this.width);
	    
	    setWButtonStateColor(button,WurmEspMod.champions);
	    buttons.addComponent(button);
	    
	    button = new WButtonC(WurmEspMod.xray ? "Xray On" : "Xray Off", new ButtonListenerC()
	    {
	        public void buttonPressed(WButtonC p0) {}
	        
	        public void buttonClicked(WButtonC p0)
	        {
	        	WurmEspMod.xray = !WurmEspMod.xray;
	        	p0.label = WurmEspMod.champions ? "Xray On" : "Xray Off";
	        	setWButtonStateColor(p0,WurmEspMod.xray);
	        }
	        
	        public int buttonRender(WButtonC p0) {
	        	return p0.parent.parent.parent.width;
				
			}
	    },this.width);
	    
	    setWButtonStateColor(button,WurmEspMod.xray);
	    buttons.addComponent(button);
	    
	    this.mainPanel.setComponent(buttons, 1);
	    setComponent(this.mainPanel);
	}
	
	public void closePressed()
	{
		hud.toggleComponent(this);
	}
	
	public void toggle()
	{
		hud.toggleComponent(this);
	}
	
	private void setWButtonStateColor(WButtonC button, boolean state)
	{
		float[] colorText = state ? green : red;
		button.setTextColor(colorText[0], colorText[1], colorText[2]);
	}
}
