import java.util.*;
import java.net.*;
import java.io.*;

public class Connect {
	public static void main(String[] args) throws IOException {
		// this class acts as a man in the middle that talks to the already-running AI
		// the game board calls the script like this:
		// launch.bat -b <boardJson> -p <player> -t <time>
		
		try (
			Socket socket = new Socket("127.0.0.1", 37373);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		) {
			// send the args to the AI
			String outArgs = args[1] + "|" + args[3];
			out.println(outArgs);
			out.flush();
			
			// await a response
			String move = in.readLine();
			int returnMove = Integer.parseInt(move);
			
			System.exit(returnMove);
			
		} catch (IOException e) {
			System.out.println("Exception caught when trying to connect");
			System.out.println(e.getMessage());
		}
		
		
	}
}