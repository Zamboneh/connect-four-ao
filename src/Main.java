import java.net.*;
import java.io.*;
import java.util.*;

public class Main {
	// Main is the class that handles the operation of the AI itself.
	// Essentially it will hold the transposition table and instantiate an AI object
	// every time it is called by the Connect.
	
	public static void main(String[] args) throws IOException {
		int portNumber = 37373;
		
		TranspositionTable table = new TranspositionTable();
		
		int turnNumber = 0;
		
		while (true) {
			try (
				ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket client = serverSocket.accept();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			) {
				String recv = in.readLine();
				
				// input will be formatted as "<board json>|<player>"
				//                         e.g. [[0,0,0...]]|player-one
				String boardIn = recv.substring(0, recv.indexOf("|"));
				String playerIn = recv.substring(recv.indexOf("|") + 1, recv.length());
				
				// here's the actual AI part
				turnNumber++;
				AI ai = new AI(boardIn, playerIn, table, turnNumber);
				int move = ai.generateMinimaxMove();
				
				// print the move to the connection
				out.println(Integer.toString(move));
				out.flush();
				
			} catch (IOException e) {
				System.out.println("Exception caught");
                System.out.println(e.getMessage());
			}
		}
	}
}