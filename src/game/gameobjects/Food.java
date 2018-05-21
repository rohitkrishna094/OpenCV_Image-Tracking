package game.gameobjects;

import game.gameobjects.Cell.Type;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

public class Food {
	
	private Cell[][] grid;
	private ArrayList<Cell> spaces;
	
	private int cellX;
	private int cellY;
	
	public Food(Cell[][] grid) {
		this.grid = grid;
		spaces = new ArrayList<Cell>();
	}
	
	public void randomFood() {
		for (int i = 0; i< grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				if (grid[i][j].getType() == Type.SPACE) {
					spaces.add(grid[i][j]);
				}
			}
		}
		
		int size = spaces.size();
		if (size != 0) {
			int randIndex = (int) (Math.random() * size);
			spaces.get(randIndex).setType(Type.FOOD);
			cellX = spaces.get(randIndex).getCellX();
			cellY = spaces.get(randIndex).getCellY();
			spaces.clear();
		}
	}
	
	public int getCellX() {
		return cellX;
	}
	
	public int getCellY() {
		return cellY;
	}
	
	public void draw(Graphics g) {
		Cell food = grid[cellY][cellX];
		food.draw(g);
	}
	
	
}
