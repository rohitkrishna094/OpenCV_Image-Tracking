package game.gameobjects;

import game.Global;
import game.gameobjects.Cell.Type;

import java.util.LinkedList;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Snake {
	private int length;
	private int cellX;
	private int cellY;

	private LinkedList<Cell> body;

	private Grid world;
	private Cell[][] grid;

	private int numRows;
	private int numCols;

	// delay time in milliseconds
	private long speed = 120;
	private int counter = 0;

	private Move move;
	private boolean collidesWall;
	private boolean collidesBody;
	private boolean ate;

	private int score;

	public static enum Move {
		UP, DOWN, LEFT, RIGHT
	}

	private Sound death, eat;

	public Snake(Grid world, int length) throws SlickException {
		this.world = world;
		this.grid = world.getGrid();
		this.length = length;

		death = new Sound("sound/death.wav");
		eat = new Sound("sound/eat.wav");

		this.move = Move.RIGHT;

		body = new LinkedList<Cell>();

		numRows = grid.length;
		numCols = grid[0].length;

		init();
	}

	public void init() {

		// init starting position;
		cellX = this.length;
		cellY = numRows / 2;

		body.add(grid[cellY][this.length]);
		grid[cellY][this.length].setType(Type.HEAD);

		for (int i = cellX; i > 0; i--) {
			grid[cellY][i].setType(Type.BODY);
			body.add(grid[cellY][i]);
		}
	}

	public void update(int delta, Food food, Input input, boolean isTrackThere) {

		counter += delta;
		if(counter > speed) {
			if(isTrackThere) {
				if(Global.quadValue == 1) {
					setMove(Move.UP);
				} else if(Global.quadValue == 3) {
					setMove(Move.LEFT);
				} else if(Global.quadValue == 7) {
					setMove(Move.DOWN);
				} else if(Global.quadValue == 5) {
					setMove(Move.RIGHT);
				}
			} else {
				if(input.isKeyPressed(Input.KEY_W)) {
					setMove(Move.UP);
				} else if(input.isKeyPressed(Input.KEY_A)) {
					setMove(Move.LEFT);
				} else if(input.isKeyPressed(Input.KEY_S)) {
					setMove(Move.DOWN);
				} else if(input.isKeyPressed(Input.KEY_D)) {
					setMove(Move.RIGHT);
				}
			}
			counter = 0;
			makeMove(food);
		}

	}

	private void makeMove(Food food) {

		int tempCellX = cellX;
		int tempCellY = cellY;
		if(!collidesWall) {
			switch (move) {
			case UP:
				tempCellY -= 1;
				break;
			case DOWN:
				tempCellY += 1;
				break;
			case LEFT:
				tempCellX -= 1;
				break;
			case RIGHT:
				tempCellX += 1;
				break;
			}

			if(grid[tempCellY][tempCellX].getType() == Type.WALL) {
				collidesWall = true;

			} else if(grid[tempCellY][tempCellX].getType() == Type.BODY) {
				collidesBody = true;
			} else {
				if(grid[cellY][cellX].getType() != Type.FOOD) grid[cellY][cellX].setType(Type.BODY);
				cellX = tempCellX;
				cellY = tempCellY;

				if(collides(food)) {
					world.setFoodExists(false);
					ate = true;
					grid[cellY][cellX].setType(Type.HEAD);
					body.addFirst(grid[cellY][cellX]);
				} else {
					ate = false;
					grid[cellY][cellX].setType(Type.HEAD);
					body.addFirst(grid[cellY][cellX]);
					if(body.getLast().getType() != Type.FOOD) {
						body.removeLast().setType(Type.SPACE);
					} else {
						body.removeLast();
					}
				}
			}
		}

	}

	public boolean isCollidesWall() {
		return collidesWall;
	}

	public void setMove(Move move) {
		if(this.move == Move.UP && move != Move.DOWN || this.move == Move.DOWN && move != Move.UP || this.move == Move.LEFT && move != Move.RIGHT
				|| this.move == Move.RIGHT && move != Move.LEFT) {

			this.move = move;
		}

	}

	private boolean collides(Food food) {
		int foodCol = food.getCellX();
		int foodRow = food.getCellY();
		int snakeCol = cellX;
		int snakeRow = cellY;

		if(foodCol == snakeCol && foodRow == snakeRow) {
			score += 10;
			return true;
		}

		return false;
	}

	public int getScore() {
		return score;
	}

	public boolean isDead() {
		return collidesWall || collidesBody;
	}

	public boolean haveEaten() {
		return ate;
	}

	public void playDeath() {
		death.play(0.5f, 0.5f);
	}

	public void playEat() {
		eat.play(0.2f, 0.3f);
	}
}
