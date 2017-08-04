package com.wurmonline.client.renderer.gui;

abstract interface ButtonListenerC
{
	public abstract void buttonPressed(WButtonC paramWButtonC);

	public abstract void buttonClicked(WButtonC paramWButtonC);

	public abstract int buttonRender(WButtonC paramWButtonC);
}
