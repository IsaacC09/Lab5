package poker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import exceptions.DeckException;
import netgame.common.Hub;
import poker.app.view.RootLayoutController;
import pokerBase.Action;
import pokerBase.Card;
import pokerBase.CardDraw;
import pokerBase.Deck;
import pokerBase.GamePlay;
import pokerBase.GamePlayPlayerHand;
import pokerBase.Player;
import pokerBase.Rule;
import pokerBase.Table;
import pokerEnums.eAction;
import pokerEnums.eCardDestination;
import pokerEnums.eDrawCount;
import pokerEnums.eGame;
import pokerEnums.eGameState;

//Lab 5 by Isaac Chouri, Kevin Doak, and Victor Forman

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;
	private eGameState eGameState;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {

			Action action = (Action) message;
			switch (action.getAction()) {
			case StartGame:
				resetOutput();
				//Attempt at starting game
				RootLayoutController pokerGame = new RootLayoutController();
				pokerGame.BuildMenus();
				String ruleName = pokerGame.getRuleName();
				//Setting up players for game
				HashMap PlayerHashMap = HubGamePlay.getGamePlayers();
				//Setting up a dealer for the game, Rules and Dealer should be involved with GamePlay
				//instance but not quite sure how to approach. 
				Random generator = new Random();
				Object[] values = PlayerHashMap.values().toArray();
				UUID Dealer = (UUID) values[generator.nextInt(values.length)];
				Rule GameRule = HubGamePlay.getRule();
				GamePlay gamePlay = new GamePlay(GameRule, Dealer);
				//Assigning players with for loop for HubGamePlay
				for (UUID players : HubGamePlay.getGamePlayers().keySet()) {
					HubGamePlay.addPlayerToGame(HubGamePlay.getGamePlayer(players));
				}

				//Establishing deck in HubGamePlay
				Deck GameDeck = HubGamePlay.getGameDeck();
				eGame Game = eGame.getGame(1);
				sendToAll(HubGamePlay);

				
				break;
			// TODO: If the Action = StartGame, start the game...
			// Create an instance of GamePlay, set all the parameters
			 
			case Sit:
				resetOutput();
				HubPokerTable.AddPlayerToTable(action.getPlayer());
				sendToAll(HubPokerTable);
				break;
			// FINISHED: If Action = Sit, add the player to the table

			case Leave:
				resetOutput();
				HubPokerTable.RemovePlayerFromTable(action.getPlayer());
				sendToAll(HubPokerTable);
				break;
			// FINISHED: If Action = Leave, remove the player from the table
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			// FINISHED: If Action = Sit or Leave, send the Table
			// back to the client
			case GameState:
				sendToAll(HubGamePlay);
				break;
			// FINISHED: If Action = GameState, send HubGamePlay
			// back to the client
			}
		}

		System.out.println("Message Received by Hub");

		sendToAll("Sending Message Back to Client");
	}

}
