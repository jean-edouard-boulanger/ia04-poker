package gui.player;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PersoIHM extends Group {
	
	private Label name;
	
	public enum Sens { GAUCHE, DROITE, HAUT, BAS }
	public PersoIHM(int x, int y, String pseudo, Sens position)
	{
		Circle circle = new Circle();
		circle.setRadius(15);
		circle.setFill(Color.AQUA);
		circle.setCenterX(x);
		circle.setCenterY(y);
		
		name = new Label(pseudo + " (0)");

		name.getStyleClass().add("pseudo");
		
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
		
		this.getChildren().add(circle);
		this.getChildren().add(name);
	}
	
	public void setPseudo(String pseudo)
	{
		name.setText(pseudo);
	}
	
	public void setCurrentPlayer()
	{
		
	}
	
	public void unsetCurrentPlayer()
	{
		
	}
}
