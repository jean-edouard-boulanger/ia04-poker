package application;
	
import server.ServerWindow;
import application.PersoIHM.Sens;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	
	private Button btn;
	
	private Rectangle table;
	
	private Rectangle zone_carte;
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Poker");
        Group root = new Group();
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().addAll(this.getClass().getResource("application.css").toExternalForm());

        btn = new Button();
        btn.setLayoutX(25);
        btn.setLayoutY(550);
        btn.setText("Miser");
        
        table = new Rectangle();
        table.setX(100);
        table.setY(100);
        table.setWidth(700);
        table.setHeight(250);
        table.setFill(Color.GREEN);
        table.setStroke(Color.DARKGREEN);
        table.setStrokeWidth(5);
        table.setArcHeight(30);
        table.setArcWidth(30);
        
        zone_carte = new Rectangle();
        zone_carte.setX(5);
        zone_carte.setY(450);
        zone_carte.setWidth(900);
        zone_carte.setHeight(150);
        zone_carte.setFill(Color.BEIGE);
        
        ImageView im = new ImageView(new Image("images/as_carreau.png"));
        im.setX(150);
        im.setY(500);
        im.setFitWidth(50);
        im.setFitHeight(72);
        ImageView im2 = new ImageView(new Image("images/as_pique.png"));
        im2.setX(225);
        im2.setY(500);
        im2.setFitWidth(50);
        im2.setFitHeight(72);
         
        root.getChildren().add(table);
        root.getChildren().add(zone_carte);
        root.getChildren().add(new PersoIHM(150, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(350, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(550, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(750, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(50, 215, "pseudo", Sens.GAUCHE));
        root.getChildren().add(new PersoIHM(850, 215, "pseudo", Sens.DROITE));
        root.getChildren().add(new PersoIHM(150, 400, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(350, 400, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(550, 400, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(750, 400, "pseudo", Sens.BAS));
        root.getChildren().add(btn); 
        root.getChildren().add(im);
        root.getChildren().add(im2);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        ServerWindow server_window = new ServerWindow();
        
        initializeAction();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initializeAction()
	{
		 btn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                System.out.println("Hello World");
            }
        });
	}
}
