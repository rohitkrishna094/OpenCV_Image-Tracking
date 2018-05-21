package game.gameobjects;

import game.gameobjects.Cell.Type;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

public class Grid extends Rectangle {
	
	public static final int ROW_LENGTH = 40;
	public static final int COL_LENGTH = 40;
	
	private Cell[][] grid = new Cell[ROW_LENGTH][COL_LENGTH];
	
	private int cellWidth;
	private int cellHeight;
	
	private boolean foodExists;

	public Grid(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		cellWidth = (int) (width / COL_LENGTH);
		cellHeight = (int) (height / ROW_LENGTH);
		
		for (int i = 0; i < ROW_LENGTH; i++) {
			for (int j = 0; j < COL_LENGTH; j++) {
				grid[i][j] = new Cell(x + j * cellWidth, y + i * cellHeight, cellWidth, cellHeight );
				grid[i][j].setCellX(j);
				grid[i][j].setCellY(i);
			}
		}
		
		init();
	}
	
	public void init() {
		// init walls
		for (int i = 0; i < COL_LENGTH; i++) {
			grid[i][0].setType(Type.WALL);
			grid[i][ROW_LENGTH - 1].setType(Type.WALL);
		}
		
		for (int i = 0; i < ROW_LENGTH; i++) {
			grid[0][i].setType(Type.WALL);
			grid[COL_LENGTH-1][i].setType(Type.WALL);
		}
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) {
		
	}
	
	public void draw(Graphics g) {
		for (int i = 0; i < ROW_LENGTH; i++) {
			for (int j = 0; j < COL_LENGTH; j++) {
				grid[i][j].draw(g);
			}
		}
	}
	
	public Cell[][] getGrid() {
		return grid;
	}
	
	public void setFoodExists (boolean b) {
		foodExists = b;
	}
	
	public boolean doesFoodExists() {
		return foodExists;
	}
	
	
}
