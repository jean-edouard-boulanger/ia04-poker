package gui.player;

import java.util.Map;

import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PersoIHM extends Group {
	
	private Label name;
	
	private String pseudo;
	
	private ImageView image_current_player;
	private ImageView image_player;
	
	Timeline t_current_player;
	
	public enum Sens { GAUCHE, DROITE, HAUT, BAS }
	public PersoIHM(int x, int y, String pseudo, Sens position)
	{

		
		image_player = new ImageView(new Image("images/player_other.png"));
		image_player.setLayoutX(x - image_player.getBoundsInLocal().getWidth() / 2);
		image_player.setLayoutY(y - image_player.getBoundsInLocal().getWidth() / 2);
		image_player.setVisible(true);
		
		image_current_player = new ImageView(new Image("images/player_active_background.png"));
		image_current_player.setLayoutX(x - image_current_player.getBoundsInLocal().getWidth() / 2);
		image_current_player.setLayoutY(y - image_current_player.getBoundsInLocal().getWidth() / 2);
		image_current_player.setVisible(false);
		
		
		name = new Label(pseudo + " (0)");

		name.getStyleClass().add("pseudo");

		this.pseudo = pseudo;
		
		t_current_player = new Timeline();
		
		if(position.equals(Sens.HAUT))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y-40);
		}
		
		if(position.equals(Sens.GAUCHE))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+20);
		}
		
		if(position.equals(Sens.DROITE))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+20);
		}
		
		if(position.equals(Sens.BAS))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+25);
		}
		
		this.getChildren().add(name);
		this.getChildren().add(image_current_player);
		this.getChildren().add(image_player);
		
		setAnimationCurrentPlayer();
	}
	
	public void setAnimationCurrentPlayer()
	{
		 t_current_player = new Timeline();
		 t_current_player.setCycleCount(Animation.INDEFINITE);
		 
	        KeyFrame moveCurrentPlayer1 = new KeyFrame(Duration.seconds(.5),
	                new EventHandler<ActionEvent>() {

	                    public void handle(ActionEvent event) {

	                        image_current_player.setTranslateX(image_current_player.getTranslateX() - 15);

	                    }
	                });
	        KeyFrame moveCurrentPlayer2 = new KeyFrame(Duration.seconds(.5),
	                new EventHandler<ActionEvent>() {

	                    public void handle(ActionEvent event) {

	                        image_current_player.setTranslateX(image_current_player.getTranslateX() + 15);

	                    }
	                });

	        t_current_player.getKeyFrames().add(moveCurrentPlayer1);
	        t_current_player.getKeyFrames().add(moveCurrentPlayer2);
	        t_current_player.play();
	}
	
	public void setPseudo(String pseudo)
	{
		this.pseudo = pseudo;
		name.setText(pseudo + " (0)");
	}
	
	public void setMe()
	{
		image_player.setImage(new Image("images/player_me.png"));
	}
	
	public void setCurrentPlayer()
	{
		image_current_player.setVisible(true);
		t_current_player.play();
	}
	
	public void unsetCurrentPlayer()
	{
		image_current_player.setVisible(false);
		t_current_player.stop();
	}
	
	public void setScore(int score)
	{
		name.setText(pseudo + " (" + score + ")");
	}
	
	/*
	 * Regarde poker.token.helpers.TokenSetValueEvaluator
	 * Il calcule a valeur d'une main à partir d'un TokenSet, et de la definition de la valeur des jetons
	 */
	public static int calculateScore(TokenSet token_set)
	{
		int score = 0;
				
		for (Map.Entry<TokenType, Integer> entry : token_set.getTokensAmount().entrySet())
		{
		    score += entry.getValue().intValue();
		}
		
		return score;
	}
}
