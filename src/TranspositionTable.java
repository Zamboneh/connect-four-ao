import java.util.*;

public class TranspositionTable {
	// map of zobrist keys and elements
	TreeMap<Long, TranspositionElement> elements;
	
	// array containing our random values for creating zobrist keys
	// toPlayZobrist is a single value we can XOR in if it is p2's turn only
	long[][] zobristRandoms = new long[2][42];
	long toPlayZobrist;
	
	public TranspositionTable() {
		// init elements map
		elements = new TreeMap<Long, TranspositionElement>();
		
		// init zobrist random values
		Random r = new Random();
		for (int colors = 0; colors < 2; colors++) {
			for (int squares = 0; squares < 42; squares++) {
				zobristRandoms[colors][squares] = r.nextLong();
			}
		}
		toPlayZobrist = r.nextLong();
	}
	
	public MoveInfo probeHash(GameBoard gb, int t, int a, int b) {
		long z = createZobristKey(gb);
		if (elements.containsKey(z)) {
			//System.out.println("[ZOBRIST] Table contained this position!");
			TranspositionElement te = elements.get(z);
			if (te.move.turn <= t) {
				if (te.flags == 1) // exact value
					return te.move;
				if (te.flags == 2 && te.move.score <= a) // irrelevant alpha
					return new MoveInfo(te.move.move, a, te.move.turn);
				if (te.flags == 3 && te.move.score >= b) // irrelevant beta
					return new MoveInfo(te.move.move, b, te.move.turn);
			}
		}
		return new MoveInfo(-1, -1, -1);
	}
	
	public void recordHash(GameBoard gb, int d, int v, int f, int m) {
		//System.out.println("[ZOBRIST] New position, recording.");
		TranspositionElement te = new TranspositionElement();
		te.hashKey = createZobristKey(gb);
		te.flags = f;
		te.move = new MoveInfo(m, v, d);
		
		elements.put(te.hashKey, te);
	}
	
	public long createZobristKey(GameBoard gb) {
		long key = 0L;
		
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				if (gb.getPos(col, row) == 1) {
					key = key ^ zobristRandoms[0][(row*7)+col];
				} else if (gb.getPos(col, row) == 2) {
					key = key ^ zobristRandoms[1][(row*7)+col];
				}
			}
		}
		
		if (gb.getCurrentPlayer() == 2) {
			key = key ^ toPlayZobrist;
		}
		
		return key;
	}
}