package poker.game.model;

import jade.core.AID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerRole;
import poker.game.player.model.PlayerStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayersContainer {

	private ArrayList<Player> players;
	private Player currentPlayer;
	
	public PlayersContainer(){
		this.players = new ArrayList<Player>();
	}
	
	public PlayersContainer(ArrayList<Player> players){
		this.players = players;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public Player getCurrentPlayer(){
		return this.currentPlayer;
	}
	
	public void setCurrentPlayer(Player currentPlayer) throws NotRegisteredPlayerException{
		if(currentPlayer != null && this.getPlayerByAID(currentPlayer.getAID()) == null){
			throw new NotRegisteredPlayerException(currentPlayer);
		}
		this.currentPlayer = currentPlayer;
	}
	
	public Player getPlayerByAID(AID playerAID){
		for(Player player : this.players){
			if(player.getAID().equals(playerAID)){
				return player;
			}
		}
		return null;
	}
	
	public Player getPlayerByName(String playerName) {
		for(Player p : this.players){
			if(p.getNickname().equals(playerName)){
				return p;
			}
		}
		return null;
	}
	
	public Player getPlayerAtIndex(int index){
		if(index <= 0 || index > 10){return null;}
		
		for(Player p : this.players){
			if(p.getTablePositionIndex() == index){
				return p;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public ArrayList<Integer> getUsedTablePlaces(){
		ArrayList<Integer> usedPlacesIndex = new ArrayList<Integer>();
		for(Player player : this.players){
			usedPlacesIndex.add(player.getTablePositionIndex());
		}
		return usedPlacesIndex;
	}
	
	@JsonIgnore
	public ArrayList<Integer> getAvailableTablePlaces(){
		ArrayList<Integer> freePlacesIndex = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		for(Player p : this.players){
			freePlacesIndex.remove(p.getTablePositionIndex());
		}
		return freePlacesIndex;
	}
	
	@JsonIgnore
	private int getFirstAvailableTablePlace() throws NoPlaceAvailableException{
		ArrayList<Integer> places = this.getAvailableTablePlaces();
		
		if(places.isEmpty()){
			throw new NoPlaceAvailableException();
		}
		
		return places.get(0);
	}
	
	@JsonIgnore
	private int getRandomAvailableTablePlace() throws NoPlaceAvailableException{
		ArrayList<Integer> places = this.getAvailableTablePlaces();
		
		if(places.isEmpty()){
			throw new NoPlaceAvailableException();
		}
		
		Random random = new Random();
		return places.get(random.nextInt(places.size() - 1));		
	}
	
	public void addPlayer(Player p) throws PlayerAlreadyRegisteredException, NoPlaceAvailableException{
		if(this.getPlayerByAID(p.getAID()) != null){
			throw new PlayerAlreadyRegisteredException(p);
		}
				
		if(p.getTablePositionIndex() == null || (p.getTablePositionIndex() != null && this.getPlayerAtIndex(p.getTablePositionIndex())  != null)){
			p.setTablePositionIndex(this.getFirstAvailableTablePlace());
		}
		
		this.players.add(p);
		Collections.sort(this.players, new Player.PlayerTablePositionComparator());
	}
	
	public void addPlayerAtRandomTablePlace(Player p) throws PlayerAlreadyRegisteredException, NoPlaceAvailableException{
		if(this.getPlayerByAID(p.getAID()) != null){
			throw new PlayerAlreadyRegisteredException(p);
		}
		
		p.setTablePositionIndex(this.getRandomAvailableTablePlace());
		this.players.add(p);
		
		Collections.sort(this.players, new Player.PlayerTablePositionComparator());
	}
	
	public void dropPlayer(AID playerAID){
		Player player = this.getPlayerByAID(playerAID);
		
		if(player != null){
			this.players.remove(player);
		}
		
		Collections.sort(this.players, new Player.PlayerTablePositionComparator());
	}
	
	public void dropPlayer(Player p){
		this.players.remove(p);
		Collections.sort(this.players, new Player.PlayerTablePositionComparator());
	}	
	
	public Player getPlayerNextTo(Player po){		
		int playerIndex = players.indexOf(po);
		if(playerIndex == -1){
			return null;
		}
		
		if(playerIndex == players.size() - 1){
			return players.get(0);
		}
		
		return players.get(playerIndex + 1);
	}	
	
	public class PlayerIterator implements Iterator<Player>{

		private ArrayList<Player> tmpPlayers; 
		
		private final int initialIndex;
		private int currentPlayerIndex;
		
		public PlayerIterator(){
			this.tmpPlayers = new ArrayList<Player>(players);
			Collections.sort(this.tmpPlayers, new Player.PlayerTablePositionComparator());
			
			this.initialIndex = 0;
			this.currentPlayerIndex = 0;
		}
		
		public PlayerIterator(Player first){
			this.tmpPlayers = new ArrayList<Player>(players);
			Collections.sort(this.tmpPlayers, new Player.PlayerTablePositionComparator());

			int tmpInitialIndex = tmpPlayers.indexOf(first);
			
			if(tmpInitialIndex == -1){
				this.initialIndex = 0;
			}
			else {
				this.initialIndex = tmpInitialIndex;
			}
			
			this.currentPlayerIndex = this.initialIndex;
			
		}
		
		@Override
		public boolean hasNext() {
			if(this.getNextPlayerIndex() != this.initialIndex){
				return true;
			}
			return false;
		}

		@Override
		public Player next() {
			this.currentPlayerIndex = this.getNextPlayerIndex();
			return this.tmpPlayers.get(this.currentPlayerIndex);
		}

		public void rewind(){
			this.currentPlayerIndex = this.initialIndex;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		@JsonIgnore
		private int getNextPlayerIndex(){
			if(this.currentPlayerIndex == this.tmpPlayers.size() - 1){
				return 0;
			}
			return this.currentPlayerIndex + 1;
		}
	}
	
	@JsonIgnore
	public PlayerIterator getIterator(){
		return new PlayerIterator();
	}
	
	@JsonIgnore
	public PlayerIterator getIterator(Player firstPlayer){
		return new PlayerIterator(firstPlayer);
	}
	
	@JsonIgnore
	public PlayerCircularIterator getCircularIterator(){
		return new PlayerCircularIterator();
	}
	
	@JsonIgnore
	public PlayerCircularIterator getCircularIterator(Player firstPlayer){
		return new PlayerCircularIterator(firstPlayer);
	}
	
	public class PlayerCircularIterator implements Iterator<Player>{
		
		private final int initialIndex;
		private int currentPlayerIndex;
		private int loopNumber = 1;
		
		public PlayerCircularIterator(){
			Collections.sort(players, new Player.PlayerTablePositionComparator());

			this.initialIndex = 0;
			this.currentPlayerIndex = 0;
		}
		
		public PlayerCircularIterator(Player first){
			players = new ArrayList<Player>(players);
			Collections.sort(players, new Player.PlayerTablePositionComparator());

			int tmpInitialIndex = players.indexOf(first);
			
			if(tmpInitialIndex == -1){
				this.initialIndex = 0;
			}
			else {
				this.initialIndex = tmpInitialIndex;
			}
			
			this.currentPlayerIndex = this.initialIndex;
			
		}
		
		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Player next() {
			this.currentPlayerIndex = this.getNextPlayerIndex();
			
			if(this.currentPlayerIndex == this.initialIndex){
				this.loopNumber++;
			}
			
			return players.get(this.currentPlayerIndex);
		}

		public void rewind(){
			this.currentPlayerIndex = this.initialIndex;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		@JsonIgnore
		public int getLoopNumber(){
			return this.loopNumber;
		}
		
		@JsonIgnore
		private int getNextPlayerIndex(){
			if(this.currentPlayerIndex == players.size() - 1){
				return 0;
			}
			return this.currentPlayerIndex + 1;
		}
	}
	
	@JsonIgnore
	public ArrayList<AID> getPlayersAIDs() {
		if(players.size() == 0)
			return null;
		
		ArrayList<AID> playersAIDs = new ArrayList<AID>();
		
		for(Player p : players) {
			playersAIDs.add(p.getAID());
		}
		
		return playersAIDs;
	}

	@JsonIgnore
	public void setDealer(AID dealer) throws NotRegisteredPlayerException {
		setDealer(this.getPlayerByAID(dealer));
	}
	
	@JsonIgnore
	public void setDealer(Player dealer) throws NotRegisteredPlayerException {
		if(dealer == null || !this.players.contains(dealer))
			throw new NotRegisteredPlayerException(dealer);
		
		// we clear roles:
		
		Player oldDealer = getDealer();
		if(oldDealer != null)
			oldDealer.setRole(PlayerRole.USUAL);
		
		Player oldSmallBlind = getSmallBlind();
		if(oldSmallBlind != null)
			oldSmallBlind.setRole(PlayerRole.USUAL);
		
		Player oldBigBlind = getSmallBlind();
		if(oldBigBlind != null)
			oldBigBlind.setRole(PlayerRole.USUAL);
		
		// we set roles:
		
		dealer.setRole(PlayerRole.DEALER_BUTTON);
		
		Player smallBlind = this.getPlayerNextTo(dealer);
		while (smallBlind.getStatus() != PlayerStatus.IN_GAME)
			smallBlind = this.getPlayerNextTo(smallBlind);
		smallBlind.setRole(PlayerRole.SMALL_BLIND);
		
		Player bigBlind = this.getPlayerNextTo(smallBlind);
		while (bigBlind.getStatus() != PlayerStatus.IN_GAME)
			bigBlind = this.getPlayerNextTo(bigBlind);
		bigBlind.setRole(PlayerRole.BIG_BLIND);
	}
	
	@JsonIgnore
	public Player getDealer(){
		for (Player player : this.players){
			if(player.getRole() == PlayerRole.DEALER_BUTTON);
		}
		return null;
	}
	
	@JsonIgnore
	public Player getSmallBlind(){
		for (Player player : this.players){
			if(player.getRole() == PlayerRole.SMALL_BLIND);
		}
		return null;
	}
	
	@JsonIgnore
	public Player getBigBlind(){
		for (Player player : this.players){
			if(player.getRole() == PlayerRole.BIG_BLIND);
		}
		return null;
	}
	
	
}
