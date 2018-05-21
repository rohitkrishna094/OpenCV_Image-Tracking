package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import game.state.PlayState;

public class Game extends StateBasedGame {

	public static final GameState PLAY = new PlayState(0);

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int FPS = 60;
	
	public Game(String title) throws SlickException {
		super(title);
		MenuSounds.select = new Sound("sound/select.wav");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(PLAY);
	}

	public static void main(String[] args) throws SlickException, InterruptedException {
		CameraFrame cf = new CameraFrame();
		while (true) {
			Thread.sleep(1000);
			if(cf.cp.play) {
				AppGameContainer app = new AppGameContainer(new Game("Snake"));
				app.setDisplayMode(WIDTH, HEIGHT, false);
				app.setTargetFrameRate(FPS);
				app.setShowFPS(false);
				app.setAlwaysRender(true);
				app.start();
			}
		}
	}
	
}
