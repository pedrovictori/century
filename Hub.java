package century_core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sorts incoming data and communicate with the relevant module
 */
public class Hub {
	//COMMANDS
	private static final String start = "start";
	private static final String help = "h";
	private static final String rest = "rest";
	private static final String acquire = "acquire";
	private static final String play = "play";
	private static final String claim = "claim";

	//ANSWERS
	private static final String startText = "Welcome. To start a new game, input \"start\"";
	private static final String inputNumberPlayers = "Please input how many human and AI players are going to be playing, in the format \"3,1\"";
	private static final String errorPlayerCount = "The maximum total number of players is five";
	private static final String inputStarterPointCards = "Please input the five point cards from left to right, one per line, in the format \"p21p4110\"";
	private static final String errorFormat = "Please use the right format";
	private static final String waitingCards = "Received. Waiting for more cards";
	private static final String inputStarterMerchantCards = "Please input the six merchant cards from left to right, one per line, in the formats \"s0012\", \"u2\" or \"t1000=0022\"";
	private static final String startDone = "All done, thank you. To continue, input the move (type \"h\" for a list of commands) of core.Player ";
	private static final String moveDone = "Move computed. To continue, input the move (type \"h\" for a list of commands) of core.Player ";
	private static final String helpText = "Possible player moves:\n" +
			"\"rest\"\n" +
			"\"acquire\", followed by the index of the merchant card acquired, the cubes spent in the format \"yyyr\" (if no cubes spent, leave blank), and the new card that enters at the end of the row, in the formats \"s0012\", \"u2\" or \"t1000=0022\"\n" +
			"\"play\", followed by the card played, in the formats \"s0012\", \"u2\" or \"t1000=0022\"\n. In the case of a trade card, include the number of iterations afterwards (or leave blank for 1). In the case of a upgrade card, include the cubes you want to upgrade, in the format 0011" +
			"\"claim\", followed by the index of the point card claimed and the new card that enters the row in the format \"p21p4110\".\n";
	private static final String errorCommand = "Command not recognised. Try again.";
	private static final String tryAgain = "Please try again";
	private static final String gameEnded = "Game ended. The winner is: ";
	private static final String scores = "Congratulations! The scores are ";

	//STARTER STUFF
	List<PointCard> startPointCards = new ArrayList<>();
	List<MerchantCard> startMerchantCards = new ArrayList<>();
	int numberPlayers;
	int numberAI;

	GameState gameState;

	private enum State {
		WAITING_TO_START(false),
		STARTING_PLAYERS(false),
		STARTING_POINT(false),
		STARTING_MERCHANT(false),
		STARTED(true);

		private boolean started;

		State(boolean started) {
			this.started = started;
		}

		boolean hasStarted() {
			return started;
		}
	}

	private State currentState = State.WAITING_TO_START;

	public String interpretLine(String line) {
		line = line.toLowerCase().trim();
		if (currentState.hasStarted()) {
			if (line.equals(help)) {
				return helpText;
			} else { //execute move
				String[] parsed = line.split(" ");
				String command = parsed[0];
				Action action;
				if (command.equals(rest)) action = Action.REST;
				else if (command.equals(acquire)) action = Action.ACQUIRE;
				else if (command.equals(play)) action = Action.PLAY;
				else if (command.equals(claim)) action = Action.CLAIM;
				else return errorCommand;

				boolean wasSuccessful = action.doAction(gameState, parsed);
				if (wasSuccessful){
					if (gameState.hasGameEnded()) {
						String answer = gameEnded + gameState.getWinner() + "\n" + scores + "\n";
						Map<Player, Integer> scores = gameState.getScores();
						for (Player player : gameState.getPlayers()) {
							answer += "Player " + player.getTurnOrder() + ": " + Integer.valueOf(scores.get(player)) + " points\n";
						}
						answer += "\n\n" + startText;
						currentState = State.WAITING_TO_START;
						return answer;
					}else{
						return moveDone + gameState.getCurrentTurn();
					}
				}
				else return action.getErrorMessage() + "/n" + tryAgain;
			}

		} else {
			if (currentState == State.WAITING_TO_START) {
				if (line.equals(start)) {
					currentState = State.STARTING_PLAYERS;
					return inputNumberPlayers;
				} else {
					return errorCommand;
				}
			} else if (currentState == State.STARTING_PLAYERS) {
				String[] numbers = line.split(",");
				try {
					numberPlayers = Integer.parseInt(numbers[0]);
					numberAI = Integer.parseInt(numbers[1]);
				} catch (NumberFormatException nfe) {
					return errorFormat;
				}
				int total = numberAI + numberPlayers;
				if (total > 5) {
					return errorPlayerCount;
				} else {
					currentState = State.STARTING_POINT;
					return inputStarterPointCards;
				}
			} else if (currentState == State.STARTING_POINT) {
				try {
					startPointCards.add(PointCard.factory(line));
				} catch (NumberFormatException nfe) {
					return errorFormat;
				}

				if (startPointCards.size() == 5) {
					currentState = State.STARTING_MERCHANT;
					return inputStarterMerchantCards;
				} else {
					return waitingCards;
				}
			} else if (currentState == State.STARTING_MERCHANT) {
				try {
					startMerchantCards.add(MerchantCardFactory.importCard(line));
				} catch (NumberFormatException nfe) {
					return errorFormat;
				}
				if (startMerchantCards.size() == 6) {
					currentState = State.STARTED;
					gameState = new GameState(startPointCards, startMerchantCards, numberPlayers, numberAI);

					//cleanup
					startPointCards = null;
					startMerchantCards = null;

					return startDone + gameState.getCurrentTurn();
				} else {
					return waitingCards;
				}

			} else {
				throw new IllegalStateException("State not recognised");
			}
		}
	}
}
