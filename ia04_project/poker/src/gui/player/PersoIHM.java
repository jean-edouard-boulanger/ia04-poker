package gui.player;

import jade.core.behaviours.SenderBehaviour;

import java.util.Map;

import com.sun.javafx.geom.Point2D;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;

public class PersoIHM extends Group {

	private Label name;

	private String pseudo;

	private Point2D refPosition;
	private Sens refSens;
	
	private ImageView image_current_player;
	private ImageView image_player;

	FadeTransition t_current_player;

	public enum Sens { GAUCHE, DROITE, HAUT, BAS, HAUT_GAUCHE, HAUT_DROITE, BAS_GAUCHE, BAS_DROITE }
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

		this.refPosition = new Point2D(x, y);
		this.refSens = position;
		
		name = new Label(pseudo + " (0)");

		name.getStyleClass().add("pseudo");

		this.pseudo = pseudo;

		if(position.equals(Sens.HAUT) || this.refSens == Sens.HAUT_GAUCHE || this.refSens == Sens.HAUT_DROITE)
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

		if(position.equals(Sens.BAS)  || this.refSens == Sens.BAS_GAUCHE || this.refSens == Sens.BAS_DROITE)
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+25);
		}

		this.getChildren().add(name);
		this.getChildren().add(image_current_player);
		this.getChildren().add(image_player);

		//SequentialTransition transition_image_current = createTransition(iv, img)
		t_current_player = new FadeTransition(Duration.millis(800), image_current_player);
		t_current_player.setFromValue(1.0);
		t_current_player.setToValue(0.3);
		t_current_player.setCycleCount(Timeline.INDEFINITE);
		t_current_player.setAutoReverse(true);
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

	public Point2D getBlindTokenPosition(){
		Point2D pos = new Point2D();
				
		if(this.refSens == Sens.HAUT){
			pos.x = this.refPosition.x + 15;
			pos.y = this.refPosition.y + 50;
		}
		else if(this.refSens == Sens.BAS){
			pos.x = this.refPosition.x - 15;
			pos.y = this.refPosition.y - 60;
		}
		else if(this.refSens == Sens.GAUCHE){
			pos.x = this.refPosition.x + 60;
			pos.y = this.refPosition.y + 15;
		}
		else if(this.refSens == Sens.DROITE){
			pos.x = this.refPosition.x - 60;
			pos.y = this.refPosition.y - 15;
		}
		else if(this.refSens.equals(Sens.HAUT_DROITE)){
			pos.x = this.refPosition.x - 35;
			pos.y = this.refPosition.y + 45;
		}
		else if(this.refSens.equals(Sens.HAUT_GAUCHE)){
			pos.x = this.refPosition.x + 35;
			pos.y = this.refPosition.y + 45;
		}
		else if(this.refSens.equals(Sens.BAS_DROITE)){
			pos.x = this.refPosition.x - 50;
			pos.y = this.refPosition.y - 40;
		}
		else if(this.refSens.equals(Sens.BAS_GAUCHE)){
			pos.x = this.refPosition.x + 50;
			pos.y = this.refPosition.y - 40;
		}
		
		return pos;	
	}
	
	public Point2D getDealerTokenPosition(){
		Point2D pos = new Point2D();
		
		if(this.refSens == Sens.HAUT){
			pos.x = this.refPosition.x - 15;
			pos.y = this.refPosition.y + 50;
		}
		else if(this.refSens == Sens.BAS){
			pos.x = this.refPosition.x + 15;
			pos.y = this.refPosition.y - 60;
		}
		else if(this.refSens == Sens.GAUCHE){
			pos.x = this.refPosition.x + 60;
			pos.y = this.refPosition.y - 15;
		}
		else if(this.refSens == Sens.DROITE){
			pos.x = this.refPosition.x - 60;
			pos.y = this.refPosition.y + 15;
		}
		else if(this.refSens.equals(Sens.HAUT_DROITE)){
			pos.x = this.refPosition.x - 45;
			pos.y = this.refPosition.y + 35;
		}
		else if(this.refSens.equals(Sens.HAUT_GAUCHE)){
			pos.x = this.refPosition.x + 45;
			pos.y = this.refPosition.y + 35;
		}
		else if(this.refSens.equals(Sens.BAS_DROITE)){
			pos.x = this.refPosition.x - 40;
			pos.y = this.refPosition.y - 50;
		}
		else if(this.refSens.equals(Sens.BAS_GAUCHE)){
			pos.x = this.refPosition.x + 40;
			pos.y = this.refPosition.y - 50;
		}
		
		return pos;	
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

	public void setRefPosition(Point2D refPosition) {
		this.refPosition = refPosition;
	}
}
