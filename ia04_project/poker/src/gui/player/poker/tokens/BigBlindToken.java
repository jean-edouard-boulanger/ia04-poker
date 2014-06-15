package gui.player.poker.tokens;

import com.sun.javafx.geom.Point2D;

import javafx.scene.image.Image;

public class BigBlindToken extends PokerToken {

	private static final String PICTURE_PATH = "images/token_big_blind.png";
	
	public BigBlindToken(){
		super();
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
	public BigBlindToken(Point2D center){
		super(center);
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
}
