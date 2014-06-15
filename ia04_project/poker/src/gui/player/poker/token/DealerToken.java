package gui.player.poker.token;

import gui.player.PersoIHM;

import com.sun.javafx.geom.Point2D;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DealerToken extends PokerToken {

	private static final String PICTURE_PATH = "images/token_dealer.png";
	
	public DealerToken(){
		super();
		this.tokenImageView.setImage(new Image(PICTURE_PATH));
	}
	
	public DealerToken(Point2D center){
		super(center);
		this.tokenImageView = new ImageView(PICTURE_PATH);
	}
	
	@Override
	public void animatedMoveToPlayer(PersoIHM perso){
		Point2D destPoint = perso.getDealerTokenPosition();
		this.animatedMoveTo(destPoint);
	}
}
