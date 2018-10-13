package com.wurmonline.client.renderer.gui;

import net.encode.wurmesp.WurmEspMod;

public class WurmEspWindow extends WWindow{
	private WurmBorderPanel mainPanel;
	
	public WurmEspWindow()
	{
		super("Esp", true);
	    setTitle("Esp");
	    this.resizable = true;
	    this.closeable = true;
	    //setInitialSize(85, 128, true);
	    setInitialSize(100, 300, true);
	    this.mainPanel = new WurmBorderPanel("Esp");
	    
	    WurmArrayPanel<EspWCheckBox> checkboxes = new WurmArrayPanel<EspWCheckBox>("Esp CheckBoxes", 0);
	    
	    //------------------------------------------
	    EspWCheckBox playersCheckBox = new EspWCheckBox("Players", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.players = checkbox.checked;
			}
	    	
	    });
	    playersCheckBox.checked = WurmEspMod.players;
	  //------------------------------------------
	    EspWCheckBox mobsCheckBox = new EspWCheckBox("Mobs", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.mobs = checkbox.checked;
			}
	    	
	    });
	    mobsCheckBox.checked = WurmEspMod.mobs;
	  //------------------------------------------
	    EspWCheckBox specialsCheckBox = new EspWCheckBox("Specials", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.specials = checkbox.checked;
			}
	    	
	    });
	    specialsCheckBox.checked = WurmEspMod.specials;
	  //------------------------------------------
	    EspWCheckBox uniquesCheckBox = new EspWCheckBox("Uniques", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.uniques = checkbox.checked;
			}
	    	
	    });
	    uniquesCheckBox.checked = WurmEspMod.uniques;
	  //------------------------------------------
	    
	    EspWCheckBox championsCheckBox = new EspWCheckBox("Conditioned", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.conditioned = checkbox.checked;
			}
	    	
	    });
	    championsCheckBox.checked = WurmEspMod.conditioned;
	  //------------------------------------------
	    EspWCheckBox xrayCheckBox = new EspWCheckBox("Xray", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.xray = checkbox.checked;
			}
	    	
	    });
	    xrayCheckBox.checked = WurmEspMod.xray;
	  //------------------------------------------
	    EspWCheckBox tilesCheckBox = new EspWCheckBox("Tiles", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.tilescloseby = checkbox.checked;
			}
	    	
	    });
	    tilesCheckBox.checked = WurmEspMod.tilescloseby;
	  //------------------------------------------
	    EspWCheckBox deedCheckBox = new EspWCheckBox("Deed", new CheckBoxListener() {

			@Override
			public void checkboxClicked(EspWCheckBox checkbox) {
				WurmEspMod.deedsize = checkbox.checked;
			}
	    	
	    });
	    deedCheckBox.checked = WurmEspMod.deedsize;
	  //------------------------------------------
	    checkboxes.addComponent(playersCheckBox);
	    checkboxes.addComponent(mobsCheckBox);
	    checkboxes.addComponent(specialsCheckBox);
	    checkboxes.addComponent(uniquesCheckBox);
	    checkboxes.addComponent(championsCheckBox);
	    checkboxes.addComponent(xrayCheckBox);
	    checkboxes.addComponent(tilesCheckBox);
	    checkboxes.addComponent(deedCheckBox);
	    
	    //------------------------------------------
	    
	    this.mainPanel.setComponent(checkboxes, 1);
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
}
