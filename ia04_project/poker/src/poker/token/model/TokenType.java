package poker.token.model;

public enum TokenType {
	WHITE(1),
	RED(2),
	GREEN(3),
	BLUE(4),
	BLACK(5);
	
	private int tokenType;
	
	private TokenType(int type){
		this.tokenType = type;
	}
	
	private TokenType(){
		this.tokenType = 0;
	}
	
	public int getChipType(){
		return this.tokenType;
	}	
}
