package model.token;

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
	
	public int getChipType(){
		return this.tokenType;
	}	
}
