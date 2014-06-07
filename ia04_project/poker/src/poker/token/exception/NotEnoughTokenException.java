package poker.token.exception;

import poker.token.model.TokenType;

public class NotEnoughTokenException extends Exception {
	public NotEnoughTokenException(){
		super("The token sets could not be substracted: not enough tokens in first set");
	}
	
	public NotEnoughTokenException(TokenType tt, int amount, int subAmount){
		super("The token sets could not be substracted: the first set did not have enough " + tt + " tokens ("+ subAmount +" needed, "+ amount +" possessed)");
	}	
}
