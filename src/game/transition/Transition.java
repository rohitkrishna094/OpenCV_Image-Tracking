package game.transition;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Transition {
	public static final FadeInTransition in = new FadeInTransition(Color.black,300);
	public static final FadeOutTransition out = new FadeOutTransition(Color.black,300);
}
