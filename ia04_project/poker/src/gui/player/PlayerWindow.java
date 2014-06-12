package gui.player;

import gui.player.PersoIHM.Sens;
import gui.player.TokenPlayerIHM.ColorToken;
import gui.player.WaitGameWindow.WaitGameGuiEvent;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import poker.card.model.Card;
import poker.card.model.CardRank;
import poker.card.model.CardSuit;
import poker.game.player.model.Player;
import sma.agent.HumanPlayerAgent;

/**
 * 
 * Class used for testing purpose only.
 *
 */
public class PlayerWindow extends Application implements PropertyChangeListener {
		
	public enum PlayerGuiEvent {
		INITIALIZING_ME,
		INITIALIZING_OTHER,
		PLAYER_RECEIVED_UNKNOWN_CARD,
		PLAYER_RECEIVED_CARD,
		ADD_COMMUNITY_CARD,
		EMPTY_COMMUNITY_CARD,
		PLAYER_FOLDED,
		PLAYER_RECEIVED_TOKENSET,
		PLAYER_BET,
		PLAYER_CHECK,
		BLIND_VALUE,
		CURRENT_PLAYER_CHANGED,
		
		IHM_READY,
		SHOW_IHM
	}
	
	private Button button_fold;
	private Button button_follow;
	private Button button_relaunch;
	private Button button_check;
	private Button button_add_bet;
	private Button button_sub_bet;
	
	private Label label_hand;
	private Label label_min_blind;
	
	private TextArea textarea_log;
	
	private Slider slider_bet;
	private TextField textfield_bet;
	
	private CommunautyCardIHM communauty_card;
	
	private Rectangle zone_carte;
	
	private HumanPlayerAgent human_player_agent;
	
	// scaling:
	private double scaleRatio = 1;
	private double stageInitialWidth = 0;
	private double stageInitialHeight = 0;
	private double aspectRatio;
	
	private Stage primaryStage;
	
	public void setHumanPlayerAgent(HumanPlayerAgent agent)
	{
		this.human_player_agent = agent;
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		//--------------------------------------
		
		this.primaryStage = primaryStage;
		
		primaryStage.setTitle("Poker");
        final Pane root = new Pane();
        root.setId("root");
                
        label_hand = new Label("Main n�1");
        label_hand.setLayoutX(15);
        label_hand.setLayoutY(15);
        label_hand.getStyleClass().add("hand");
        
        label_min_blind = new Label("Min blind : 1");
        label_min_blind.setLayoutX(15);
        label_min_blind.setLayoutY(425);
        label_min_blind.getStyleClass().add("min-blind");
        
        textarea_log = new TextArea();
        textarea_log.setLayoutX(5);
        textarea_log.setLayoutY(470);
        textarea_log.setPrefHeight(125);
        textarea_log.setPrefWidth(200);
        textarea_log.setWrapText(true);
        textarea_log.setText("Log for the game");
        textarea_log.setEditable(false);
        textarea_log.getStyleClass().add("log");

        button_follow = new Button();
        button_follow.setLayoutX(335);
        button_follow.setLayoutY(490);
        button_follow.setText("Suivre � 2");
        button_follow.setPrefWidth(100);
        button_follow.getStyleClass().add("button_play");
        
        button_check = new Button();
        button_check.setLayoutX(335);
        button_check.setLayoutY(550);
        button_check.setText("Checker");
        button_check.setPrefWidth(100);
        button_check.getStyleClass().add("button_play");
        
        button_fold = new Button();
        button_fold.setLayoutX(225);
        button_fold.setLayoutY(490);
        button_fold.setText("Se coucher");
        button_fold.setPrefWidth(100);
        button_fold.getStyleClass().add("button_play");
        
        button_relaunch = new Button();
        button_relaunch.setLayoutX(225);
        button_relaunch.setLayoutY(550);
        button_relaunch.setText("Relancer � 5");
        button_relaunch.setPrefWidth(100);
        button_relaunch.getStyleClass().add("button_play");
        
        button_add_bet = new Button();
        button_add_bet.setLayoutX(660);
        button_add_bet.setLayoutY(550);
        button_add_bet.setText("+");
        button_add_bet.getStyleClass().add("button_slider");
        
        button_sub_bet = new Button();
        button_sub_bet.setLayoutX(450);
        button_sub_bet.setLayoutY(550);
        button_sub_bet.setText("-");
        button_sub_bet.getStyleClass().add("button_slider");
        
        zone_carte = new Rectangle();
        
        slider_bet = new Slider();
        slider_bet.setMin(0);
        slider_bet.setMax(50);
        slider_bet.setValue(5);
        slider_bet.setShowTickLabels(true);
        slider_bet.setShowTickMarks(true);
        slider_bet.setMajorTickUnit(5);
        slider_bet.setMinorTickCount(0);
        slider_bet.setSnapToTicks(true);
        slider_bet.setBlockIncrement(25);
        slider_bet.setLayoutX(485);
        slider_bet.setLayoutY(550);
        slider_bet.setPrefWidth(175);
        
        textfield_bet = new TextField();
        textfield_bet.setText("5");
        textfield_bet.setLayoutX(485);
        textfield_bet.setLayoutY(525);
        textfield_bet.setPrefWidth(175);
        textfield_bet.setEditable(false);
        
        communauty_card = new CommunautyCardIHM(250, 185);
        
        zone_carte.setX(0);
        zone_carte.setY(455);
        zone_carte.setWidth(950);
        zone_carte.setHeight(170);
        zone_carte.setFill(Color.DARKCYAN);
        
        /**************************************
         *  Player's tokens
         */
        TokenPlayerIHM token_white = new TokenPlayerIHM(485, 500, 25, ColorToken.WHITE);
        TokenPlayerIHM token_black = new TokenPlayerIHM(515, 500, 25, ColorToken.BLACK);
        TokenPlayerIHM token_blue = new TokenPlayerIHM(545, 500, 25, ColorToken.BLUE);
        TokenPlayerIHM token_green = new TokenPlayerIHM(575, 500, 25, ColorToken.GREEN);
        TokenPlayerIHM token_red = new TokenPlayerIHM(605, 500, 25, ColorToken.RED);
        
        /**************************************
         *  Players's bets
         */
        TokenBetPlayerIHM bet_player_1 = new TokenBetPlayerIHM(170, 145, 400);
        TokenBetPlayerIHM bet_player_2 = new TokenBetPlayerIHM(250, 115, 400);
        TokenBetPlayerIHM bet_player_3 = new TokenBetPlayerIHM(440, 115, 400);
        TokenBetPlayerIHM bet_player_4 = new TokenBetPlayerIHM(530, 145, 400);
        TokenBetPlayerIHM bet_player_5 = new TokenBetPlayerIHM(550, 205, 400);
        TokenBetPlayerIHM bet_player_6 = new TokenBetPlayerIHM(170, 260, 400);
        TokenBetPlayerIHM bet_player_7 = new TokenBetPlayerIHM(250, 290, 400);
        TokenBetPlayerIHM bet_player_8 = new TokenBetPlayerIHM(440, 290, 400);
        TokenBetPlayerIHM bet_player_9 = new TokenBetPlayerIHM(530, 260, 400);
        TokenBetPlayerIHM bet_player_10 = new TokenBetPlayerIHM(150, 205, 400);
        
        /**************************************
         *  Player's cards
         */
        ImageView im = new ImageView(new Image("images/ACE_DIAMONDS.png"));
        im.setX(295);
        im.setY(415);
        im.setFitWidth(40);
        im.setFitHeight(62);
        im.setRotate(-15);
        ImageView im2 = new ImageView(new Image("images/ACE_SPADES.png"));
        im2.setX(350);
        im2.setY(415);
        im2.setFitWidth(40);
        im2.setFitHeight(62);
        im2.setRotate(15);
        
        /**************************************
         *  Table
         */
        ImageView table = new ImageView(new Image("images/table_resize.png"));
        table.setX(75);
        table.setY(75);
        
        root.getChildren().add(label_hand);
        root.getChildren().add(label_min_blind);
        root.getChildren().add(table);
        root.getChildren().add(communauty_card);
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
        root.getChildren().add(bet_player_1);
        root.getChildren().add(bet_player_2);
        root.getChildren().add(bet_player_3);
        root.getChildren().add(bet_player_4);
        root.getChildren().add(bet_player_5);
        root.getChildren().add(bet_player_6);
        root.getChildren().add(bet_player_7);
        root.getChildren().add(bet_player_8);
        root.getChildren().add(bet_player_9);
        root.getChildren().add(bet_player_10);
        root.getChildren().add(textarea_log);
        root.getChildren().add(button_add_bet);
        root.getChildren().add(button_sub_bet);
        root.getChildren().add(button_check);
        root.getChildren().add(button_fold);
        root.getChildren().add(button_follow);
        root.getChildren().add(button_relaunch);
        root.getChildren().add(slider_bet);
        root.getChildren().add(textfield_bet);
        root.getChildren().add(token_white);
        root.getChildren().add(token_black);
        root.getChildren().add(token_blue);
        root.getChildren().add(token_green);
        root.getChildren().add(token_red);
        root.getChildren().add(im);
        root.getChildren().add(im2);
        
        final Pane background = new Pane();
        background.setId("background");
        background.getChildren().add(root);
        Scene scene = new Scene(background, 700, 600);
        scene.setFill(Color.TRANSPARENT);
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.getScene().getStylesheets().setAll(PlayerWindow.class.getResource("/gui/player/application.css").toString());
        
        // scale the entire scene as the stage is resized (see https://community.oracle.com/thread/2415190).
        this.stageInitialWidth = scene.getWidth();
        this.stageInitialHeight = scene.getHeight();
        this.aspectRatio = ((double)scene.getWidth())/scene.getHeight();

        background.getScene().widthProperty().addListener(new ChangeListener<Number>() {
	         @Override
	         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	             double newWidth = newValue.doubleValue();
	        	 if(background.getWidth()/background.getHeight() > aspectRatio)
	        		 newWidth = (aspectRatio) * background.getHeight();
	        	 
	              scaleRatio = newWidth / stageInitialWidth;
	              root.getTransforms().clear();
	              Scale scale = new Scale(scaleRatio, scaleRatio, 0, 0);
	              root.getTransforms().add(scale);
	         }
	    });
		
        background.getScene().heightProperty().addListener(new ChangeListener<Number>() {
             @Override
             public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            	 double newHeight = newValue.doubleValue();
	        	 if(background.getWidth()/background.getHeight() < aspectRatio)
	        		 newHeight = background.getWidth() / aspectRatio;
	        	 
	        	 	scaleRatio = newHeight / stageInitialHeight;
	        	 	root.getTransforms().clear();
	              Scale scale = new Scale(scaleRatio, scaleRatio, 0, 0);
	              root.getTransforms().add(scale);
             }
        });


        initializeAction();
        
        // Rajouter notification property pour dire � l'agent qu'on est pr�t et n'afficher qu'� ce moment l� les interfaces
        
        GuiEvent ev = new GuiEvent(this, PlayerGuiEvent.IHM_READY.ordinal());
		human_player_agent.postGuiEvent(ev);
	}

	public static PlayerWindow launchWindow(HumanPlayerAgent agent, PropertyChangeSupport changes) {
		try {
			PlayerWindow app1 = PlayerWindow.class.newInstance();
			app1.setHumanPlayerAgent(agent);
			changes.addPropertyChangeListener(app1);
			Stage newStage = new Stage();
			app1.start(newStage);
			
			return app1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void show()
	{
		this.primaryStage.show();
	}
	
	public void initializeAction()
	{
		/**************************************
         *  Slide interaction to see the player's bet
         */
		 button_add_bet.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                slider_bet.setValue(slider_bet.getValue() + 1);
                textfield_bet.setText(String.valueOf(Double.valueOf(slider_bet.getValue()).intValue() + 1));
            }
        });
		 
		 button_sub_bet.setOnAction(new EventHandler<ActionEvent>() {

	            public void handle(ActionEvent event) {
	                slider_bet.setValue(slider_bet.getValue() - 1);
	                textfield_bet.setText(String.valueOf(Double.valueOf(slider_bet.getValue()).intValue() - 1));
	            }
	     });
		 
		 slider_bet.valueProperty().addListener(new ChangeListener<Number>() {
			    @Override
			    public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
			        PlayerWindow.this.textfield_bet.setText(String.valueOf(newValue.intValue()));
			    }
			});
		 
		 button_check.setOnAction(new EventHandler<ActionEvent>() {

	            public void handle(ActionEvent event) {
	                communauty_card.addCommunautyCard(new Card(CardRank.ACE, CardSuit.CLUBS));
	            }
	        });
		 
		 button_fold.setOnAction(new EventHandler<ActionEvent>() {

	            public void handle(ActionEvent event) {
	                communauty_card.emptyCommunautyCard();
	            }
	        });
	}

	/**************************************
     *  Notification
     */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		/**
         *  -----  INITIALIZING ME -----
         */
		if(evt.getPropertyName().equals(PlayerGuiEvent.SHOW_IHM.toString()))
		{
			this.primaryStage.show();
			
			System.out.println("Initialiazing");
		}
		
		/**
         *  -----  INITIALIZING ME -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.INITIALIZING_ME.toString()))
		{
			if(evt.getNewValue() instanceof Player)
			{
				
			}
			
			System.out.println("Initialiazing");
		}
		
		/**
         *  -----  INITIALIZING AN OTHER PLAYER-----
         */
		if(evt.getPropertyName().equals(PlayerGuiEvent.INITIALIZING_OTHER.toString()))
		{
			if(evt.getNewValue() instanceof Player)
			{
				
			}
			
			System.out.println("Initialiazing");
		}
		
		/**
         *  -----  RECEIVED UKNOWN CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_UNKNOWN_CARD.toString()))
		{
				System.out.println("Empty community card");
		}
		
		/**
         *  -----  RECEIVED CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_CARD.toString()))
		{
				System.out.println("Empty community card");
		}
		
		/**
         *  -----  ADD COMMUNITY CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.ADD_COMMUNITY_CARD.toString()))
		{
			if(evt.getNewValue() instanceof Card)
			{
				communauty_card.addCommunautyCard((Card)evt.getNewValue());
				System.out.println("Add community card");
			}
		}
		
		/**
         *  -----  EMPTY COMMUNITY CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.EMPTY_COMMUNITY_CARD.toString()))
		{
			communauty_card.emptyCommunautyCard();
			System.out.println("Empty community card");
		}
	}
}
