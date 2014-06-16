package poker.game.operations;

import poker.game.operations.exception.OperationFailureException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;

public class RefundOperation extends Operation {

	private TokenSet refundedTokenSet = null;
	
	public RefundOperation(){}
	
	public RefundOperation(TokenSet refundedTokenSet){
		this.refundedTokenSet = refundedTokenSet;
	}
	
	public void setRefundedTokenSet(TokenSet refundedTokenSet){
		this.refundedTokenSet = refundedTokenSet;
	}
	
	public TokenSet getRefundedTokenSet(){
		return this.refundedTokenSet;
	}

	@Override
	public void applyOperationToTokenSet(TokenSet tokenSet) {
		tokenSet.addTokenSet(this.refundedTokenSet);
	}
}
