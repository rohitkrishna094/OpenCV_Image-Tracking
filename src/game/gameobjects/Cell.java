package game.gameobjects;

import game.color.Colors;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Cell extends Rectangle {
	
	public static enum Type {
		SPACE,
		BODY,
		HEAD,
		WALL,
		FOOD
	};
	
	private Type type;
	private boolean filled;
	private int cellX, cellY;
	
	public Cell(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		this.type = Type.SPACE;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public void draw(Graphics g) {
		switch (type) {
		case SPACE:
			//g.setColor(Color.transparent);
			g.setColor(Colors.normalWhite);
			filled = false;
			break;
		case BODY:
			g.setColor(Colors.normalWhite);
			filled = true;
			break;
		case HEAD:
			g.setColor(Colors.darkBlack);
			filled = true;
			break;
		case WALL:
			g.setColor(Colors.lightGreen);
			filled = true;
			break;
		case FOOD:
			g.setColor(Colors.lightRed);
			filled = true;
			break;
		}
		
		if (type != Type.SPACE) {
			if (filled) {
				g.fill(this);
			} else {
				g.draw(this);
			}
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public int getCellX() {
		return cellX;
	}
	
	public int getCellY() {
		return cellY;
	}
	
	public void setCellX(int cellX) {
		this.cellX = cellX;
	}
	
	public void setCellY(int cellY) {
		this.cellY = cellY;
	}
	
	
}
