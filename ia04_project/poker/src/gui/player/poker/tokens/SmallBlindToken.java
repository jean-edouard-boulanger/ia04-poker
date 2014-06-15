package gui.player.poker.tokens;

import javafx.scene.image.Image;

import com.sun.javafx.geom.Point2D;

public class SmallBlindToken extends PokerToken {

	private static final String PICTURE_PATH = "images/token_small_blind.png";
	
	public SmallBlindToken(){
		super();
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
	public SmallBlindToken(Point2D center){
		super(center);
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
}
