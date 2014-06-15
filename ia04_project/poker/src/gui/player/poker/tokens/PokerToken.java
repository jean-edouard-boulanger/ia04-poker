package gui.player.poker.tokens;

import com.sun.javafx.geom.Point2D;

import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public abstract class PokerToken extends Group {

	protected ImageView tokenImageView;
	
	public PokerToken(){
		this.tokenImageView = new ImageView();
		this.tokenImageView.setFitWidth(30);
		this.tokenImageView.setPreserveRatio(true);
		this.tokenImageView.setSmooth(true);
		this.tokenImageView.setCache(true);
		this.tokenImageView.setLayoutX(0);
		this.tokenImageView.setLayoutY(0);
		
		this.getChildren().add(tokenImageView);
	}
	
	public PokerToken(Point2D center){
		this();
		
		this.tokenImageView.setLayoutX(center.x - this.tokenImageView.getFitWidth() / 2);
		this.tokenImageView.setLayoutY(center.y - this.tokenImageView.getFitHeight() / 2);
	}
	
	public void setCenter(Point2D center){
		this.tokenImageView.setLayoutX(center.x - this.tokenImageView.getFitWidth() / 2);
		this.tokenImageView.setLayoutY(center.y - this.tokenImageView.getFitHeight() / 2);
	}
	
	public void animatedMoveTo(Point2D newPosition){		
		TranslateTransition transition = new TranslateTransition();
		transition.setToX(newPosition.x);
		transition.setToY(newPosition.y);

		transition.setDuration(Duration.seconds(1));

		transition.setNode(tokenImageView);
		transition.play();
	}
}
