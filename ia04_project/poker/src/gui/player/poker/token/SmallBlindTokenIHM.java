package gui.player.poker.token;

import javafx.scene.image.Image;

import com.sun.javafx.geom.Point2D;

public class SmallBlindTokenIHM extends PokerTokenIHM {

	private static final String PICTURE_PATH = "images/token_small_blind.png";
	
	public SmallBlindTokenIHM(){
		super();
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
	public SmallBlindTokenIHM(Point2D center){
		super(center);
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
}
