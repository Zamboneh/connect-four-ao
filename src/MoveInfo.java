// a POJO for move information, used in various ways by the transpo table
// and the minimax algorithm
public class MoveInfo {
	public int move;
	public int score;
	public int turn;
	
	public MoveInfo(int m, int s, int d) {
		move = m;
		score = s;
		turn = d;
	}
}