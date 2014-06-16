package gui.player.poker.token;

import com.sun.javafx.geom.Point2D;

import javafx.scene.image.Image;

public class BigBlindTokenIHM extends PokerTokenIHM {

	private static final String PICTURE_PATH = "images/token_big_blind.png";
	
	public BigBlindTokenIHM(){
		super();
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
	public BigBlindTokenIHM(Point2D center){
		super(center);
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
}
