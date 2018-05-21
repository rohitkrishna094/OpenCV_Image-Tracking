package game.state;

import game.Game;
import game.MenuSounds;
import game.color.Colors;
import game.gameobjects.Food;
import game.gameobjects.Grid;
import game.gameobjects.Snake;
import game.gameobjects.Snake.Move;
import game.gui.Box;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import util.Util;

public class PlayState extends BasicGameState {
	
	private static final Color DEFAULT = Colors.normalWhite;
	
	private static final int PAUSE_WIDTH = 70;
	private static final int PAUSE_HEIGHT = 40;
	private Box pauseBox;
	
	private static final int RESUME_WIDTH = 150;
	private static final int RESUME_HEIGHT = 60;
	private Box resumeBox;
	
	private static final int MENU_WIDTH = 130;
	private static final int MENU_HEIGHT = 55;
	private Box menuBox;
	
	private boolean pause;
	
	private final int id;
	
	public static final int WIDTH = 450;
	public static final int HEIGHT = 400;
	public static final int WORLD_X = Game.WIDTH / 2 - WIDTH / 2;
	public static final int WORLD_Y = Game.HEIGHT / 2 - HEIGHT / 2 + PAUSE_HEIGHT;
	private Grid world;
	private Snake snake;
	private Food food;
	private int life = 0;
	
	private FadeOutTransition out;
	private FadeInTransition in;
	
	public PlayState(int id) {
		this.id = id;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	
		world = new Grid(WORLD_X, WORLD_Y, WIDTH, HEIGHT);
		snake = new Snake(world,4);
		food = new Food(world.getGrid());
		
		pause = false;
		
		pauseBox = new Box(
				50 - PAUSE_WIDTH / 2,
				30 - PAUSE_HEIGHT / 2,
				PAUSE_WIDTH, PAUSE_HEIGHT,"Pause");
		pauseBox.init(container.getGraphics());
		pauseBox.setHoverForeColor(Colors.lightGreen);
		
		resumeBox = new Box(
				Game.WIDTH / 2 - RESUME_WIDTH / 2,
				Game.HEIGHT / 2 - RESUME_HEIGHT / 2 - 40,
				RESUME_WIDTH, RESUME_HEIGHT,"Resume");
		resumeBox.init(container.getGraphics());
		resumeBox.setHoverForeColor(Colors.lightGreen);
		
		menuBox = new Box(
				Game.WIDTH / 2 - MENU_WIDTH / 2,
				Game.HEIGHT / 2 - MENU_HEIGHT / 2 + 40,
				MENU_WIDTH, MENU_HEIGHT,"Menu Screen");
		menuBox.init(container.getGraphics());
		menuBox.setHoverForeColor(Colors.lightGreen);
		
		out = new FadeOutTransition(Colors.lightGreen, 300);
		in = new FadeInTransition(Colors.lightGreen, 300);
		container.getInput().clearKeyPressedRecord();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		//world grid
		world.draw(g);
		food.draw(g);
		
		g.setColor(Colors.lightGreen);
		g.drawString("Score: " + snake.getScore(), Game.WIDTH - 200, 25);
		if (!pause) {
			pauseBox.draw(g);
			
		} else {
			g.setColor(Colors.alphaDarkGreen);
			g.fillRect(10, 10, Game.WIDTH - 20, Game.HEIGHT - 20);
			g.setColor(Colors.lightGreen);
			g.drawRect(10,10, Game.WIDTH - 20, Game.HEIGHT - 20);
			
			resumeBox.draw(g);
			menuBox.draw(g);
		}
		
		//set color to default
		g.setColor(DEFAULT);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		if (!container.hasFocus()) {
//			pause = true;
		}
		
		if (!pause) {
			
			//update game
			
			if (pauseBox.isHover(input.getMouseX(), input.getMouseY()) && 
					input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				
				MenuSounds.select.play();
				//pause the game
				pause = true;
			}

			
			snake.update(delta, food, input, true);
			
			if (!world.doesFoodExists()) {
				food.randomFood();
				world.setFoodExists(true);
			}
			
			if (snake.isDead()) {
				snake.playDeath();
				if (life > 0) {
					life--;
					snake.playDeath();
					game.init(container);
				} else {
					
					Game.PLAY.init(container, game);
					out.init(Game.PLAY, Game.PLAY);
					in.init(Game.PLAY, Game.PLAY);
					
					game.enterState(Game.PLAY.getID(), out, in);
				}
		
			}
			if (snake.haveEaten()) snake.playEat();
			
		} else {
			
			if (resumeBox.isHover(input.getMouseX(), input.getMouseY()) && 
					input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				
				MenuSounds.select.play();
				//resume the game
				pause = false;
				container.getInput().clearKeyPressedRecord();
			}
			
			if (menuBox.isHover(input.getMouseX(), input.getMouseY()) && 
					input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				
				MenuSounds.select.play();
				//go to menu screen
				//exit
				out.init(Game.PLAY, Game.PLAY);
				in.init(Game.PLAY, Game.PLAY);
				
				Game.PLAY.init(container, game);
				game.enterState(Game.PLAY.getID(), out, in);
			}
			
		}
		
	}

	@Override
	public int getID() {
		return id;
	}

}
