package gui.player;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PersoIHM extends Group {
	
	public enum Sens { GAUCHE, DROITE, HAUT, BAS }
	public PersoIHM(int x, int y, String pseudo, Sens position)
	{
		Circle circle = new Circle();
		circle.setRadius(15);
		circle.setFill(Color.AQUA);
		circle.setCenterX(x);
		circle.setCenterY(y);
		
		Label name = new Label(pseudo + " (575)");

		name.getStyleClass().add("pseudo");
		
		ImageView carte1 = new ImageView(new Image("images/arriere_carte.png"));
		ImageView carte2 = new ImageView(new Image("images/arriere_carte.png"));
		
		if(position.equals(Sens.HAUT))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y-40);
			
			carte1.setX(x+5);
			carte1.setY(y+20);
			carte2.setX(x+15);
			carte2.setY(y+20);
		}
		
		if(position.equals(Sens.GAUCHE))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+20);
			
			carte1.setX(x+30);
			carte1.setY(y-15);
			carte2.setX(x+40);
			carte2.setY(y-15);
		}
		
		if(position.equals(Sens.DROITE))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+20);
			
			carte1.setX(x-40);
			carte1.setY(y-15);
			carte2.setX(x-50);
			carte2.setY(y-15);
		}
		
		if(position.equals(Sens.BAS))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+25);
			
			carte1.setX(x+10);
			carte1.setY(y-45);
			carte2.setX(x+20);
			carte2.setY(y-45);
		}
		
		
		carte1.setFitWidth(20);
		carte1.setFitHeight(32);
		carte2.setFitWidth(20);
		carte2.setFitHeight(32);
		
		this.getChildren().add(carte1);
		this.getChildren().add(carte2);
		this.getChildren().add(circle);
		this.getChildren().add(name);
	}
}
