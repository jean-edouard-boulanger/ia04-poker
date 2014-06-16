package gui.player.poker.token;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.javafx.geom.Point2D;

import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import javafx.scene.Group;
import javafx.scene.control.Label;

public class PotIHM extends Group {
	
	private HashMap<TokenType, TokenStackIHM> tokenStacks;
	private TokenSet tokenSet;
	private Point2D center;
	
	public PotIHM(){
		this.tokenSet = new TokenSet();
		this.tokenStacks = new HashMap<TokenType, TokenStackIHM>();
	}
	
	public PotIHM(Point2D center){
		this();
		this.center = center;
	}
	
	public PotIHM(Point2D center, TokenSet t){
		this(center);
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
		}
		
	}
	
	public void AddTokenSet(TokenSet t){
		this.tokenSet.AddTokenSet(t);
		this.refresh();
	}
	
	public void SubstractTokenSet(TokenSet t){
		try {
			this.tokenSet.SubstractTokenSet(t);
		} catch (InvalidTokenAmountException e) {
			System.err.println("ERROR [IHM : gui.player.poker.token.Pot] " + e.getMessage());
		}
		this.refresh();
	}
	
	public void clear(){
		this.tokenSet.clear();
		for(TokenStackIHM ts : this.tokenStacks.values()){
			this.getChildren().remove(ts);
		}
	}
}
