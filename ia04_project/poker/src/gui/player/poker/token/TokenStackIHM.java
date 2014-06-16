package gui.player.poker.token;

import java.util.Random;

import com.sun.javafx.geom.Point2D;

import poker.token.model.TokenType;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

public class TokenStackIHM extends Group {

	private ImageView tokenPileImageView;
	
	public TokenStackIHM(TokenType t){
		
		this.tokenPileImageView = new ImageView();
		
		this.tokenPileImageView.setSmooth(true);
		this.tokenPileImageView.setScaleX(20);
		this.tokenPileImageView.setPreserveRatio(true);
		this.tokenPileImageView.setLayoutX(0);
		this.tokenPileImageView.setLayoutY(0);
		
		if(t == TokenType.WHITE){
			this.tokenPileImageView.setImage(new Image("images/token_stack_white.png"));
		}
		else if(t == TokenType.RED) {
			this.tokenPileImageView.setImage(new Image("images/token_stack_red.png"));
		}
		else if(t == TokenType.GREEN){
			this.tokenPileImageView.setImage(new Image("images/token_stack_green.png"));
		}
		else if(t == TokenType.BLUE){
			this.tokenPileImageView.setImage(new Image("images/token_stack_blue.png"));
		}
		else{
			this.tokenPileImageView.setImage(new Image("images/token_stack_black.png"));
		}
		
		this.getChildren().add(tokenPileImageView);
	}

	public TokenStackIHM(TokenType t, Point2D center){
		this(t);
		this.setCenter(center);
	}
	
	public void setCenter(Point2D center){
		this.tokenPileImageView.setLayoutX(center.x - this.tokenPileImageView.getFitWidth() / 2);
		this.tokenPileImageView.setLayoutX(center.y - this.tokenPileImageView.getFitHeight() / 2);
	}
	
	public void simulateAddTokens(){
		Random r = new Random();
		this.tokenPileImageView.setRotate(r.nextDouble() * 360);
	}
	
	public double getFitHeight(){
		return this.tokenPileImageView.getFitHeight();
	}
	
	public double getFitWidth(){
		return this.tokenPileImageView.getFitWidth();
	}
	
}
