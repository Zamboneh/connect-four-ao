import java.util.*;

public class GameBoard {
	int[][] board;
	int maximizingPlayer;
	int currentPlayer;
	
	
	public GameBoard(String boardJson, int player) {
		// parse json board into our int array
		// this does not use an actual json parser...yet
		board = parseJsonBoard(boardJson);
		maximizingPlayer = player;
		currentPlayer = player;
		System.out.println("Parsed game board and ready to go.");
	}
	
	public GameBoard(GameBoard gb) {
		// copies a game board
		//System.out.println("[BOARD] Fed a board, copying.");
		//System.out.println("[BOARD] I was fed this board...");
		//gb.printBoard();
		board = new int[6][7];
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				board[row][col] = gb.getPos(col, row);
			}
		}
		maximizingPlayer = gb.getMaximizingPlayer();
		currentPlayer = gb.getCurrentPlayer();
		//System.out.println("[BOARD] Copied. Here's me:");
		//printBoard();
	}
	
	public void printBoard() {
		System.out.println("====BOARD, P" + maximizingPlayer + "====");
		System.out.println(" 0  1  2  3  4  5  6");
		String b = "";
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++) {
				b += "[" + getPos(c, r) + "]";
			}
			b += " " + r + "\n";
		}
		System.out.println(b);
	}
	
	public int getMaximizingPlayer() {
		return maximizingPlayer;
	}
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	public int getPos(int x, int y) {
		// backwards indeces retain the x-horizontal, y-vertical paradigm
		return board[y][x];
	}
	public void setPos(int x, int y) {
		board[y][x] = currentPlayer;
	}
	
	public int getOtherPlayer(int p) {
		if (p == 1)
			return 2;
		else if (p == 2)
			return 1;
		return -1;
	}
	
	public void switchPlayer() {
		if (currentPlayer == 1)
			currentPlayer = 2;
		else
			currentPlayer = 1;
	}
	
	public boolean makeMove(int col) {
		// updates the board with a move if it is a valid one
		// returns true if valid move, false if invalid (i.e. full column or out of range, or the player isn't 1 or 2)
		if (col < 0 || col > 6)
			return false;
		
		if (getPos(col, 0) != 0)
			return false;
			
		// we know it's a valid move at this point, so go up the column and find the first empty position
		for (int curRow = 5; curRow >= 0; curRow--) {
			if (getPos(col, curRow) == 0) {
				setPos(col, curRow);
				break;
			}
		}
		switchPlayer();
		//System.out.println("Simulated move " + col + " for player " + maximizingPlayer + ". Board is now:");
		//printBoard();
		return true;
	}
	
	public int scorePos(int x, int y, int dx, int dy) {
		int myPoints = 0;
		int theirPoints = 0;
		
		for (int i = 0; i < 4; i++) {
			if (getPos(x, y) == maximizingPlayer) {
				myPoints++;
			} else if (getPos(x, y) == getOtherPlayer(maximizingPlayer)) {
				theirPoints++;
			}
			
			x += dx;
			y += dy;
		}
		
		if (myPoints == 4) {
			//System.out.println("====WIN DETECTED.==== x:" + x + " y:" + y);
			//printBoard();
			return 100000;
		}
		else if (theirPoints == 4) {
			//System.out.println("====WIN DETECTED.==== x:" + x + " y:" + y);
			//printBoard();
			return -100000;
		}
		else
			return myPoints;
	}
	
	public int scoreBoard() {
		int total = 0;
		int v = 0;
		int h = 0;
		int dLR = 0;
		int dRL = 0;
		
		// check vertical
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 7; c++) {
				int s = scorePos(c, r, 0, 1);
				if (s == 100000)
					return 100000;
				if (s == -100000)
					return -100000;
				v += s;
			}
		}
		
		// horizontal
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 4; c++) {
				int s = scorePos(c, r, 1, 0);
				if (s == 100000)
					return 100000;
				if (s == -100000)
					return -100000;
				h += s;
			}
		}
		
		// diagonal l-r
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 4; c++) {
				int s = scorePos(c, r, 1, 1);
				if (s == 100000)
					return 100000;
				if (s == -100000)
					return -100000;
				dLR += s;
			}
		}
		
		// diagonal r-l
		for (int r = 3; r < 6; r++) {
			for (int c = 0; c < 4; c++) {
				int s = scorePos(c, r, 1, -1);
				if (s == 100000)
					return 100000;
				if (s == -100000)
					return -100000;
				dRL += s;
			}
		}
		
		total = v + h + dLR + dRL;
		return total;
	}
	
	public boolean isFull() {
		for (int c = 0; c < 7; c++) {
			if (getPos(c, 0) == 0) {
				return false;
			}
		}
		System.out.println("Shit's full.");
		return true;
	}
	
	public boolean isLeafFinished(int d, int s) {
		if (d == 0 || s == 100000 || s == -100000 || isFull()) {
			//System.out.println("[LEAF] Reached end of branch... d=" + d + " s=" + s);
			return true;
		}
		return false;
	}
	
	public ArrayList<Integer> getValidMoves() {
		ArrayList<Integer> valid = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			if (getPos(i, 0) == 0) {
				valid.add(i);
			}
		}
		return valid;
	}
	
	private static int[][] parseJsonBoard(String json) {
		// until I get an actual JSON parser in here (which is coming!) this does the trick
		// essentially "[[1,2,3],[5,4,7]]" will become "123547"
		int[][] parsedBoard = new int[6][7];
		String[] ripped = json.split("[\\[, \\]]"); // strips out any brackets, commas, and spaces
        int pointer = 0; // index into the "compressed" json string
        
        // rip out all the empty spaces from the ripped array
        List<String> list = new ArrayList<String>(Arrays.asList(ripped));
        list.removeAll(Arrays.asList("", null));
        ripped = list.toArray(ripped);
        
        // populate the gameboard
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                try {
                    parsedBoard[i][j] = Integer.parseInt(ripped[pointer]);
                    //System.out.println("Set {" + (i + 1) + "," + (j + 1) + "} to " + board[i][j] + " by pointer: " + pointer);
                } catch (Exception npe) {
                    //System.out.println("Could not parse : " + npe.getMessage());
                    parsedBoard[i][j] = 0;
                    //System.out.println("Pointer failed - set {" + (i + 1) + "," + (j + 1) + "} to " + board[i][j]);
                }
                
                pointer++;
            }
        }
		
		return parsedBoard;
	}
}