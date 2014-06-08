package gui.player;

import gui.player.PersoIHM;
import gui.player.PersoIHM.Sens;
import gui.server.ServerWindow;
import jade.lang.acl.ACLMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import poker.card.exception.CommunityCardsFullException;
import poker.card.helper.CardPickerHelper;
import poker.card.helper.CustomPickSequence;
import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.exception.UnexpectedCombinationIdenticCards;
import poker.card.heuristics.combination.helper.CardCombinations;
import poker.card.heuristics.combination.helper.HandComparator;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.combination.model.Hand;
import poker.card.heuristics.probability.ProbabilityEvaluator;
import poker.card.heuristics.probability.ProbabilityEvaluator.CombinationProbabilityReport;
import poker.card.model.Card;
import poker.card.model.CardDeck;
import poker.card.model.CardRank;
import poker.card.model.CardSuit;
import poker.card.model.CommunityCards;
import poker.card.model.GameDeck;
import poker.card.model.UserDeck;
import sma.agent.HumanPlayerAgent;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

/**
 * 
 * Class used for testing purpose only.
 *
 */
public class PlayerWindow extends Application implements PropertyChangeListener {
		
	private Button button_fold;
	private Button button_follow;
	private Button button_relaunch;
	private Button button_check;
	private Button button_add_bet;
	private Button button_sub_bet;
	
	private JLabel label_bet;
	
	private Slider slider_bet;
	private TextField textfield_bet;
	
	private Rectangle zone_carte;
	
	private HumanPlayerAgent human_player_agent;
	
	public void setHumanPlayerAgent(HumanPlayerAgent agent)
	{
		this.human_player_agent = human_player_agent;
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		//--------------------------------------
		
		primaryStage.setTitle("Poker");
        Group root = new Group();
        Scene scene = new Scene(root, 700, 600);
        URL applicationCss = this.getClass().getResource("/gui/player/application.css");
        scene.getStylesheets().addAll(applicationCss.toExternalForm());
        
        button_follow = new Button();
        button_follow.setLayoutX(100);
        button_follow.setLayoutY(490);
        button_follow.setText("Suivre");
        
        button_check = new Button();
        button_check.setLayoutX(100);
        button_check.setLayoutY(550);
        button_check.setText("Checker");
        
        button_fold = new Button();
        button_fold.setLayoutX(15);
        button_fold.setLayoutY(490);
        button_fold.setText("Se coucher");
        
        button_relaunch = new Button();
        button_relaunch.setLayoutX(15);
        button_relaunch.setLayoutY(550);
        button_relaunch.setText("Relancer");
        
        button_add_bet = new Button();
        button_add_bet.setLayoutX(650);
        button_add_bet.setLayoutY(550);
        button_add_bet.setText("+");
        
        button_sub_bet = new Button();
        button_sub_bet.setLayoutX(450);
        button_sub_bet.setLayoutY(550);
        button_sub_bet.setText("-");
        
        zone_carte = new Rectangle();
        
        slider_bet = new Slider();
        slider_bet.setMin(0);
        slider_bet.setMax(100);
        slider_bet.setValue(0);
        slider_bet.setShowTickLabels(true);
        slider_bet.setShowTickMarks(true);
        slider_bet.setMajorTickUnit(50);
        slider_bet.setMinorTickCount(5);
        slider_bet.setBlockIncrement(10);
        slider_bet.setLayoutX(500);
        slider_bet.setLayoutY(550);
        
        textfield_bet = new TextField();
        textfield_bet.setText("0");
        textfield_bet.setLayoutX(500);
        textfield_bet.setLayoutY(500);
        
        zone_carte.setX(0);
        zone_carte.setY(455);
        zone_carte.setWidth(950);
        zone_carte.setHeight(170);
        zone_carte.setFill(Color.BEIGE);
        
        ImageView im = new ImageView(new Image("images/as_carreau.png"));
        im.setX(250);
        im.setY(500);
        im.setFitWidth(50);
        im.setFitHeight(72);
        ImageView im2 = new ImageView(new Image("images/as_pique.png"));
        im2.setX(350);
        im2.setY(500);
        im2.setFitWidth(50);
        im2.setFitHeight(72);
        
        ImageView table = new ImageView(new Image("images/table_resize.png"));
        table.setX(75);
        table.setY(75);
         
        root.getChildren().add(table);
        root.getChildren().add(zone_carte);
        root.getChildren().add(new PersoIHM(105, 90, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(250, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(440, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(600, 90, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(50, 215, "pseudo", Sens.GAUCHE));
        root.getChildren().add(new PersoIHM(650, 215, "pseudo", Sens.DROITE));
        root.getChildren().add(new PersoIHM(105, 330, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(250, 370, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(440, 370, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(600, 330, "pseudo", Sens.BAS));
        root.getChildren().add(button_add_bet);
        root.getChildren().add(button_sub_bet);
        root.getChildren().add(button_check);
        root.getChildren().add(button_fold);
        root.getChildren().add(button_follow);
        root.getChildren().add(button_relaunch);
        root.getChildren().add(slider_bet);
        root.getChildren().add(im);
        root.getChildren().add(im2);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        //Need to init the window via the SwingUtilities.invokeLater method on Mac to work
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
               // ServerWindow server_window = new ServerWindow(null);                
        	}
        });
        
        initializeAction();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initializeAction()
	{
		 button_add_bet.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                slider_bet.setValue(slider_bet.getValue() + 1);
                textfield_bet.setText(Double.toString(slider_bet.getValue() + 1));
            }
        });
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		
	}
}
