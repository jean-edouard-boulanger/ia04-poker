package application;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PersoIHM extends Group {
	
	public enum Sens { GAUCHE, DROITE, HAUT, BAS }
	public PersoIHM(int x, int y, String pseudo, Sens position)
	{
		Circle circle = new Circle();
		circle.setRadius(25);
		circle.setFill(Color.YELLOW);
		circle.setCenterX(x);
		circle.setCenterY(y);
		
		Label name = new Label(pseudo);
		
		
		Rectangle bras1 = new Rectangle();
		bras1.setStrokeWidth(10);
		bras1.setFill(Color.YELLOWGREEN);
		bras1.setWidth(25);
		bras1.setHeight(15);
		
		Rectangle bras2 = new Rectangle();
		bras2.setStrokeWidth(10);
		bras2.setFill(Color.YELLOWGREEN);
		bras2.setWidth(25);
		bras2.setHeight(15);
		
		if(position.equals(Sens.HAUT))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y-40);
			
			bras1.setX(x+15);
			bras1.setY(y+15);
			bras1.setRotate(45);
			
			bras2.setX(x-40);
			bras2.setY(y+15);
			bras2.setRotate(-45);
		}
		
		if(position.equals(Sens.GAUCHE))
		{
			name.setLayoutX(x-15);
			name.setLayoutY(y+40);
			
			bras1.setX(x+12);
			bras1.setY(y-35);
			bras1.setRotate(-45);
			
			bras2.setX(x+15);
			bras2.setY(y+15);
			bras2.setRotate(45);
		}
		
		if(position.equals(Sens.DROITE))
		{
			name.setLayoutX(x-15);
			name.setLayoutY(y+40);
			
			bras1.setX(x-35);
			bras1.setY(y-35);
			bras1.setRotate(45);
			
			bras2.setX(x-35);
			bras2.setY(y+22);
			bras2.setRotate(-45);
		}
		
		if(position.equals(Sens.BAS))
		{
			name.setLayoutX(x-20);
			name.setLayoutY(y+25);
			
			bras1.setX(x+15);
			bras1.setY(y-25);
			bras1.setRotate(-45);
			
			bras2.setX(x-40);
			bras2.setY(y-25);
			bras2.setRotate(45);
		}
		
		/*this.getChildren().add(bras1);
		this.getChildren().add(bras2);*/
		this.getChildren().add(circle);
		this.getChildren().add(name);
	}
}
