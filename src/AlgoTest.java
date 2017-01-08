
public class AlgoTest {

	public static void main(String[] args) {
		String table = "[[0,0,0,1,0,0,0],[0,0,0,2,0,0,0],[0,0,0,1,0,0,2],[0,0,0,2,2,0,1],[0,1,2,2,2,0,1],[0,1,1,2,1,0,1]]";
		String player = "player-two";
		TranspositionTable tt = new TranspositionTable();
		
		AI ai = new AI(table, player, tt, 1);
		
		int move = ai.generateMinimaxMove();
	}

}
