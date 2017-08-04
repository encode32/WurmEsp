package com.wurmonline.client.renderer.gui;

import com.wurmonline.client.renderer.PickData;
import org.lwjgl.opengl.GL11;

class WButtonC
extends FlexComponent
implements Comparable<Object>
{
	String label;
	private final int hPadding;
	private final int vPadding;
	private boolean isClosing;
	boolean isCloseHovered;
	boolean isDown;
	private ButtonListenerC buttonListener = null;
	float rText = 1.0F;
	float gText = 1.0F;
	float bText = 1.0F;
	private boolean enabled = true;
	boolean hoverMode = false;
	boolean hovered = false;
	private boolean needsConfirm = false;
	private String confirmMessage;
	private String confirmQuestion;
	private String hoverString;

	WButtonC(String aLabel)
	{
		this(aLabel, 1, 0);
	}

	WButtonC(String aLabel, int hPadding, int vPadding)
	{
		super("Button with label " + aLabel);
		this.hPadding = hPadding;
		this.vPadding = vPadding;
		setLabel(aLabel);
		this.sizeFlags = 2;
	}

	WButtonC(String aLabel, ButtonListenerC abuttonListener, int hPadding, int vPadding)
	{
		this(aLabel, hPadding, vPadding);
		setButtonListener(abuttonListener);
	}

	WButtonC(String aLabel, ButtonListenerC abuttonListener)
	{
		this(aLabel, abuttonListener, 1, 0);
	}
	
	WButtonC(String aLabel, ButtonListenerC abuttonListener, int width)
	{
		this(aLabel, abuttonListener, 1, 0);
		this.width = width;
	}

	WButtonC(String aLabel, ButtonListenerC abuttonListener, String confirmQuestion)
	{
		this(aLabel, abuttonListener, "", confirmQuestion);
	}

	WButtonC(String aLabel, ButtonListenerC abuttonListener, String confirmMessage, String confirmQuestion)
	{
		this(aLabel, abuttonListener, 1, 0);
		this.confirmMessage = confirmMessage;
		this.confirmQuestion = confirmQuestion;
		this.needsConfirm = true;
	}

	void setLabel(String aLabel)
	{
		this.label = aLabel;
		setSize(this.text.getWidth(this.label) + 8 + 2 * this.hPadding, this.text.getHeight() + 2 * this.vPadding);
	}

	final void setButtonListener(ButtonListenerC abuttonListener)
	{
		this.buttonListener = abuttonListener;
	}

	final void setDown(boolean down)
	{
		this.isDown = down;
	}

	protected void renderComponent(float alpha)
	{
		this.width = this.buttonListener.buttonRender(this);
		if ((!this.hoverMode) || (this.hovered))
		{
			GL11.glEnable(3553);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);

			float r2 = this.r;
			float g2 = this.g;
			float b2 = this.b;
			if (!this.enabled)
			{
				r2 /= 2.0F;
				g2 /= 2.0F;
				b2 /= 2.0F;
			}
			GL11.glColor4f(r2, g2, b2, 1.0F);
			int yo = ((this.isCloseHovered) || (this.isDown)) && (this.enabled) ? 16 : 0;

			panelTexture.switchTo();

			drawTexture(this.x, this.y, 8, this.height, 64, 16 + yo, 8, 16);

			drawTexture(this.x + this.width - 8, this.y, 8, this.height, 88, 16 + yo, 8, 16);
			if (this.width > 16)
			{
				panelTextureTilingH.switchTo();
				drawTexTilingH(this.x + 8, this.y, this.width - 16, this.height, 61 + yo, 16);
			}
			GL11.glDisable(3553);
			GL11.glDisable(3042);
		}
		int yo = ((this.isCloseHovered) || (this.isDown)) && (this.enabled) ? 1 : 0;
		float c = ((this.isCloseHovered) || (this.isDown)) && (this.enabled) ? 0.8F : 1.0F;
		GL11.glColor4f(this.rText * c, this.gText * c, this.bText * c, 1.0F);
		this.text.moveTo(this.x + 4 + this.hPadding, this.y + this.text.getHeight() + yo + this.vPadding);
		this.text.paint(this.label);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	final void setTextColor(float r, float g, float b)
	{
		this.rText = r;
		this.gText = g;
		this.bText = b;
	}

	protected void leftPressed(int xMouse, int yMouse, int clickCount)
	{
		if ((this.enabled) && (yMouse >= this.y) && (yMouse < this.y + this.height) && (xMouse >= this.x) && (xMouse < this.x + this.width))
		{
			this.isClosing = true;
			this.isCloseHovered = true;
			if (this.buttonListener != null) {
				this.buttonListener.buttonPressed(this);
			}
		}
	}

	protected void mouseDragged(int xMouse, int yMouse)
	{
		if (this.isClosing) {
			if ((yMouse >= this.y) && (yMouse < this.y + this.height) && (xMouse >= this.x) && (xMouse < this.x + this.width)) {
				this.isCloseHovered = true;
			} else {
				this.isCloseHovered = false;
			}
		}
	}

	protected final void leftReleased(int xMouse, int yMouse)
	{
		if (this.isClosing)
		{
			if ((this.isCloseHovered) && (this.buttonListener != null)) {
				this.buttonListener.buttonClicked(this);
			}
			this.isClosing = false;
			this.isCloseHovered = false;
		}
	}

	final boolean isDown()
	{
		return this.isDown;
	}

	protected void mouseExited()
	{
		this.hovered = false;
	}

	protected void mouseMoved(int xMouse, int yMouse)
	{
		this.hovered = true;
	}

	protected final int getMouseCursor(int mousePos, int mousePos2)
	{
		return 1;
	}

	final boolean isEnabled()
	{
		return this.enabled;
	}

	final void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	final void setConfirm(boolean confirm)
	{
		this.needsConfirm = confirm;
	}

	final void setConfirmMessage(String message)
	{
		this.confirmMessage = message;
	}

	final void setConfirmQuestion(String question)
	{
		this.confirmQuestion = question;
		this.needsConfirm = true;
	}

	public void pick(PickData pickData, int xMouse, int yMouse)
	{
		if (this.hoverString != null) {
			pickData.addText(this.hoverString);
		}
	}

	public void setHoverString(String description)
	{
		this.hoverString = description;
	}

	public String getHoverString()
	{
		return this.hoverString;
	}

	public boolean needsConfirmation()
	{
		return this.needsConfirm;
	}

	public String getConfirmMessage()
	{
		return this.confirmMessage;
	}

	public String getConfirmQuestion()
	{
		return this.confirmQuestion;
	}

	public int compareTo(Object object)
	{
		if ((object instanceof WButton)) {
			return this.label.compareTo(((WButton)object).label);
		}
		return -1;
	}
}