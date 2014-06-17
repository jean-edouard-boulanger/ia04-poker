package gui.player.poker.token;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.javafx.geom.Point2D;

import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Label;

public class PotIHM extends Group {
	
	private HashMap<TokenType, TokenStackIHM> tokenStacks;
	private TokenSet tokenSet;
	private Point2D center;
	private Label label_score;
	private int score;
	
	public PotIHM(){
		this.tokenSet = new TokenSet();
		this.tokenStacks = new HashMap<TokenType, TokenStackIHM>();
		this.score = 0;
		this.label_score = new Label();
		this.label_score.getStyleClass().add("label-bet");
		this.label_score.setVisible(false);
		this.getChildren().add(this.label_score);
	}
	
	public PotIHM(Point2D center){
		this();
		this.center = center;
		this.score = 0;
		this.label_score = new Label();
		this.label_score.setLayoutX(center.x - 25);
		this.label_score.setLayoutY(center.y + 25);
		this.label_score.getStyleClass().add("label-bet");
		this.label_score.setVisible(false);
		this.getChildren().add(this.label_score);
	}
	
	public PotIHM(Point2D center, TokenSet t, int score){
		this(center);
		this.tokenSet = t;
		this.score = score;
		this.label_score = new Label(String.valueOf(score));
		this.label_score.setLayoutX(center.x - 25);
		this.label_score.setLayoutY(center.y + 25);
		this.label_score.getStyleClass().add("label-bet");
		this.label_score.setVisible(false);
		this.getChildren().add(this.label_score);
		this.refresh();
	}
	
	private void refresh(){
		ArrayList<TokenType> drawnTokenStacks = new ArrayList<>();
		ArrayList<TokenType> discardedTokenStacks = new ArrayList<>();
		
		Point2D startDrawingPoint = new Point2D();
		int amount = 0;
		for(TokenType tokenType : TokenType.values()){
			amount = this.tokenSet.getAmountForTokenType(tokenType);
			if(amount > 0){
				drawnTokenStacks.add(tokenType);
			}
			else{
				discardedTokenStacks.add(tokenType);
			}
		}
		
		int totalWidth = 0;
		ArrayList<TokenStackIHM> newTokenStacks = new ArrayList<TokenStackIHM>();
		for(TokenType tokenType : drawnTokenStacks){
			TokenStackIHM newTokenStack = new TokenStackIHM(tokenType);
			totalWidth += newTokenStack.getFitWidth();
			newTokenStacks.add(newTokenStack);
		}
		
		startDrawingPoint.x = (float) (this.center.x - totalWidth / 2.0);
		
		TokenStackIHM tokenStack = null;
		for(TokenType tokenType : discardedTokenStacks){
			tokenStack = this.tokenStacks.get(tokenType);
			if(tokenStack != null){
				this.getChildren().remove(tokenStack);
			}
		}
		
		Point2D currentDrawingCenter = new Point2D(startDrawingPoint.x, this.center.y);
		for(TokenType tokenType : drawnTokenStacks){
			tokenStack = this.tokenStacks.get(tokenType);
			if(tokenStack != null){
				tokenStack.setCenter(currentDrawingCenter);
				tokenStack.simulateAddTokens();
			}
			else {
				tokenStack = new TokenStackIHM(tokenType);
				tokenStack.setCenter(currentDrawingCenter);
				this.getChildren().add(tokenStack);
			}
			currentDrawingCenter.x += tokenStack.getFitWidth();
		}
		
	}
	
	public void AddTokenSet(TokenSet t){
		this.tokenSet.addTokenSet(t);
		this.refresh();
	}
	
	public void SubstractTokenSet(TokenSet t){
		try {
			this.tokenSet.substractTokenSet(t);
		} catch (InvalidTokenAmountException e) {
			System.err.println("ERROR [IHM : gui.player.poker.token.Pot] " + e.getMessage());
		}
		this.refresh();
	}
	
	public void addBet(int bet){
		this.score += bet;
		this.label_score.setText("Pot : " + String.valueOf(this.score));
		if(!this.label_score.isVisible())
			this.label_score.setVisible(true);
	}
	
	public void clear(){
		this.score = 0;
		this.label_score.setVisible(false);
		this.tokenSet.clear();
		for(TokenStackIHM ts : this.tokenStacks.values()){
			this.getChildren().remove(ts);
		}
	}
}
