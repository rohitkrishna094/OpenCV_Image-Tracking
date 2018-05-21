package game.gui;

import game.color.Colors;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Box extends Rectangle {

	private Color backColor;
	private Color foreColor;
	private String text;
	
	private Color hoverBackColor;
	private Color hoverForeColor;
	private String hoverText;
	
	private int textWidth;
	private int textHeight;
	
	private boolean hover;
	
	public Box(float x, float y, float width, float height, String text) {
		this(x,y,width,height,Color.transparent, Colors.normalWhite, text);
	}
	
	public Box(float x, float y, float width, float height, Color backColor, Color foreColor, String text) {
		super(x, y, width, height);
		this.backColor = this.hoverBackColor = backColor;
		this.foreColor = this.hoverForeColor = foreColor;
		this.text = text;
	}
	
	public void init(Graphics g) {
		Font f = g.getFont();
		this.textWidth = f.getWidth(text);
		this.textHeight = f.getHeight(text);
	}
	
	public void draw(Graphics g) {
		g.setColor((hover)? hoverBackColor : backColor);
		g.fill(this);
		g.setColor((hover)? hoverForeColor : foreColor);
		g.draw(this);
		g.drawString(text, x + width / 2 - textWidth / 2, y + height / 2 - textHeight / 2);
	}
	
	public void setHover(Color hoverBackColor, Color hoverForeColor, String hoverText) {
		this.hoverBackColor = hoverBackColor;
		this.hoverForeColor = hoverForeColor;
		this.hoverText = hoverText;
	}
	
	public void setHoverBackColor(Color hoverBackColor) {
		this.hoverBackColor = hoverBackColor;
	}
	
	public void setHoverForeColor(Color hoverForeColor) {
		this.hoverForeColor = hoverForeColor;
	}
	
	public boolean isHover(float x, float y) {
		if (x < minX || x > maxX) {
			hover = false;
			return false;
		}
		if (y < minY || y > maxY) {
			hover = false;
			return false;
		}
		else {
			hover = true;
			return true;
		}
	}
	
}
