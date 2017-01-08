import java.util.*;

public class AI {
	// the AI class is the meaty portion of the program.
	
	GameBoard theBoard;
	int me;
	TranspositionTable transTable;
	int turn;
	
	public AI(String boardJson, String player, TranspositionTable table, int turn) {
		System.out.println("Received args!");
		System.out.println("\t The board: " + boardJson);
		System.out.println("\t The player: " + player);
		System.out.println("AI is ready to go.");
		
		if ("player-one".equals(player))
			me = 1;
		else
			me = 2;
		
		theBoard = new GameBoard(boardJson, me);
		turn = turn;
		
		transTable = table;
	}
	
	public int returnRandomMove() {
		// returns a random valid move - deprecated, really
		ArrayList<Integer> validMoves = theBoard.getValidMoves();
		Random r = new Random();
		int move = validMoves.get(r.nextInt(validMoves.size()));
		return move;
	}
	
	// minimax algorithm
	public int generateMinimaxMove() {
		// uses the minimax algorithm below to find the best move
		System.out.println("Thinking...");
		MoveInfo myMove = minimax(theBoard, turn, 10, -1000000, 1000000, true);
		System.out.println("I ended up with move " + myMove.move + " with a score of " + myMove.score);
		return myMove.move;
	}
	
	// initial attempts at an MTD(f) that I couldn't get working correctly
	
	/*public int generateMTDFMove() {
		System.out.println("Thinking...");
		//MoveInfo myMove = itDeep(theBoard, 8);
		MoveInfo myMove = MTDF(theBoard, 0, 8);
		System.out.println("I ended up with move " + myMove.move + " with a score of " + myMove.score);
		return myMove.move;
	}
	
	public MoveInfo MTDF(GameBoard gb, int f, int d) {
		int g = f;
		int ub = Integer.MAX_VALUE;
		int lb = Integer.MIN_VALUE;
		int beta = 0;
		MoveInfo move = new MoveInfo(-1, -1, -1);
		while (lb < ub) {
			beta = Math.max(g, lb+1);
			System.out.println("[mtdf] g:" + g + " ub:" + ub + " lb:" + lb + " b:" + beta);
			move = maximizePlay(gb, 1, d, beta-1, beta, true);
			g = move.score;
			if (g < beta)
				ub = g;
			else
				lb = g;
		}
		System.out.println("Done MTDF'ing... g:" + g + " ub:" + ub + " lb:" + lb + " b:" + beta);
		return move;
	}
	
	public MoveInfo itDeep(GameBoard gb, int maxDepth) {
		int firstGuess = 0;
		MoveInfo theMove = new MoveInfo(-1, -1, -1);
		for (int i = maxDepth; i >= 0; i--) {
			theMove = MTDF(gb, firstGuess, i);
			firstGuess = theMove.score;
		}
		return theMove;
	}*/
	
	// The minimax algorithm!
	private MoveInfo minimax(GameBoard gb, int turn, int depth, int alpha, int beta, boolean isMax) {
		// we want to keep track of the turns, because we want to win SOONER! not later. that was a major bug.
		// isMax defines if we're maximizing on this turn or minimizing
		
		// firstly, look up position in transposition table
		MoveInfo lookup = transTable.probeHash(gb, turn, alpha, beta);
		if (lookup.score != -1) {
			// oh look, we've seen this before! let's use it.
			return lookup;
		}
		
		// not in the table, score the board
		int boardScore = gb.scoreBoard();
		
		// break if needed, i.e. winning position or max search depth
		if (gb.isLeafFinished(depth, boardScore)) {
			transTable.recordHash(gb, turn, boardScore, 1, -1);
			return new MoveInfo(-1, boardScore, turn);
		}
		
		// my turn, or the opponent's turn?
		if (isMax) {
		
			MoveInfo max = new MoveInfo(-1, -99999, -1);
			
			// check all possible moves
			for (int c = 0; c < 7; c++) {
				
				// copy the board
				GameBoard copiedBoard = new GameBoard(gb);
				
				// we only want to examine a move if it's valid
				if (copiedBoard.getPos(c, 0) == 0) {
					copiedBoard.makeMove(c);
					
					// recursively examine further moves
					MoveInfo nextMove = minimax(copiedBoard, turn + 1, depth - 1, alpha, beta, false);
					
					// evaluate the next move and keep the one that maximizes our score
					if (max.move == -1 || nextMove.score > max.score) {
						max.move = c;
						max.score = nextMove.score;
						max.turn = nextMove.turn;
					} else if (nextMove.score == 100000 && max.score == 100000 && nextMove.turn < max.turn) {
						// we get here if we find a winning position at a closer turn - we don't want to delay our inevitable win
						max.move = c;
						max.turn = nextMove.turn;
					}
					// alpha-beta pruning
					if (max.score > alpha)
						alpha = max.score;
					if (alpha >= beta) {
						transTable.recordHash(gb, turn, max.score, 2, max.move);
						return max;
					}
				}
			}
			transTable.recordHash(gb, turn, max.score, 2, max.move);
			return max;
		} else {
			// we're minimizing - you dirty trickster; trying to outsmart me, eh?
			MoveInfo min = new MoveInfo(-1, 99999, depth);
		
			for (int c = 0; c < 7; c++) {
				GameBoard copiedBoard = new GameBoard(gb);
				
				if (copiedBoard.getPos(c, 0) == 0) {
					copiedBoard.makeMove(c);
					
					MoveInfo nextMove = minimax(copiedBoard, turn + 1, depth - 1, alpha, beta, true);
					// evaluate and keep track of minimizing score
					if (min.move == -1 || nextMove.score < min.score) {
						min.move = c;
						min.score = nextMove.score;
						min.turn = nextMove.turn;
					} else if (nextMove.score == -100000 && nextMove.score == -100000 && nextMove.turn > min.turn) {
						// opponent probably wants to speed up their inevitable win too, unfortunately
						min.move = c;
						min.turn = nextMove.turn;
					}
					// alpha-beta pruning
					if (min.score < beta)
						beta = min.score;
					if (alpha >= beta) {
						transTable.recordHash(gb, turn, min.score, 3, min.move);
						return min;
					}
				}
			}
			transTable.recordHash(gb, turn, min.score, 3, min.move);
			return min;
		}
	}
}