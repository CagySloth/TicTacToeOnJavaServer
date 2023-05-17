import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Handles the game logic and communicates with the clients.
 * 
 * @author Robin
 *
 */
public class GameServer implements Runnable{
	private static GameServer server;
	private Socket p1sock;
	private Socket p2sock;
	private PrintWriter p1writer;
	private PrintWriter p2writer;
	private BufferedReader p1reader;
	private BufferedReader p2reader;
	private Game game;
	private boolean isRunning;
	/**
	 * Create a server.
	 * @param args
	 */
	public static void main(String[] args) {
		server = new GameServer();
		server.go();
	}
	/**
	 * Create 2 sockets for clients, once both players submitted their names,
	 * start multi-threading to receive data from both clients.
	 */
	public void go() {
		game = new Game();
		try {
			ServerSocket serverSock = new ServerSocket(6000);
			server.p1sock = serverSock.accept();
			server.p1writer = new PrintWriter(server.p1sock.getOutputStream(), true);
			server.p1reader = new BufferedReader(new InputStreamReader(server.p1sock.getInputStream()));
			server.p2sock = serverSock.accept();
			server.p2writer = new PrintWriter(server.p2sock.getOutputStream(), true);
			server.p2reader = new BufferedReader(new InputStreamReader(server.p2sock.getInputStream()));
			Thread p1 = new Thread(server);
			p1.setName("p1");
			Thread p2 = new Thread(server);
			p2.setName("p2");
			isRunning = true;
			p1.start();
			p2.start();
			
			p1.join();
			p2.join();
			serverSock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Receive, process and send data from and to the clients.
	 */
	public void run() {
		int whichPlayer;
		if (Thread.currentThread().getName() == "p1")
			whichPlayer = 1;
		else
			whichPlayer = 2;
		String action;
		while (isRunning) {
			try {
				action = read(whichPlayer);
				action(whichPlayer, action);
			} catch(IOException e) {
				isRunning = false;
				broadcast("E", "Game Ends. One of the players left.");
			}
		}
	}
	/**
	 * Call checkMove in Game to check validity of moves.
	 * Updates boards in clients if the move is valid.
	 * @param whichPlayer: which player made the move.
	 * @param action: row and column index of the move.
	 * @throws IOException
	 */
	private void action(int whichPlayer, String action) throws IOException {
		String[] move = action.split(",");
		int row = Integer.parseInt(move[0]);
		int col = Integer.parseInt(move[1]);
		boolean validMove = game.checkMove(whichPlayer, row, col);
		if (validMove) {
			int gameStatus = game.checkGameStatus();
			broadcast("B", game.getBoardString());
			switch (gameStatus) {
				case (0): {
					if (whichPlayer == 1) {
						p1writer.println("M");
						p1writer.println("Valid move, wait for your opponent.");
						p2writer.println("M");
						p2writer.println("Your opponent has moved, now is your turn.");
					}
					else {
						p1writer.println("M");
						p1writer.println("Your opponent has moved, now is your turn.");
						p2writer.println("M");
						p2writer.println("Valid move, wait for your opponent.");
					}
					break;
				}
				case (1): {
					p1writer.println("E");
					p1writer.println("Congratulations. You Win.");
					p2writer.println("E");
					p2writer.println("You lose.");
					isRunning = false;
					break;
				}
				case (2): {
					p1writer.println("E");
					p1writer.println("You lose.");
					p2writer.println("E");
					p2writer.println("Congratulations. You Win.");
					isRunning = false;
					break;
				}
				case (3): {
					broadcast("E", "Draw.");
					isRunning = false;
					break;
				}
			}
		}
		else {
			broadcast("I", " ");
		}
	}
	/**
	 * Read data from the clients.
	 * @param whichPlayer: read from which client.
	 * @return action: the data read from the client.
	 * @throws IOException
	 */
	private String read(int whichPlayer) throws IOException {
		String action;
		if (whichPlayer == 1)
			action = p1reader.readLine();
		else
			action = p2reader.readLine();
		return action;
	}
	/**
	 * Broadcast information to both clients.
	 * @param subject: what is the information about.
	 * @param content: the information in a string.
	 */
	private void broadcast(String subject, String content) {
		p1writer.println(subject);
		p1writer.println(content);
		p2writer.println(subject);
		p2writer.println(content);
	}
}
