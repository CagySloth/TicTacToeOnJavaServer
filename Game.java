/**
 * Holds TicTacToe game logic.
 * 
 * @author Robin
 *
 */
public class Game {
	private int[][] board;
	private int moveCount;
	private int turnOfPlayer;
	/**
	 * Initiate a new game session with a new 3x3 board.
	 */
	Game(){
		this.board = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++)
				this.board[i][j] = 0;
		}
		this.moveCount = 0;
		this.turnOfPlayer = 1;
	}
	/**
	 * Check is the game has ended or not after players made a move.
	 * Game Status: 0 = not ended, 1 = player 1 won, 2 = player 2 won, 3 = draw.
	 * @return Game Status
	 */
	public int checkGameStatus() {
		for (int i = 0; i < 3; i++) {
			int row = board[i][0] + board[i][1] + board[i][2];
			if (row == 3)
				return 1;
			else if (row == -3)
				return 2;
		}
		for (int i = 0; i < 3; i++) {
			int column = board[0][i] + board[1][i] + board[2][i];
			if (column == 3)
				return 1;
			else if (column == -3)
				return 2;
		}
		int diagonal1 = board[0][0] + board[1][1] + board[2][2];
		if (diagonal1 == 3)
			return 1;
		else if (diagonal1 == -3)
			return 2;
		int diagonal2 = board[0][2] + board[1][1] + board[2][0];
		if (diagonal2 == 3)
			return 1;
		else if (diagonal2 == -3)
			return 2;
		if (moveCount == 9)
			return 3;
		return 0;
	}
	/**
	 * Updates the information in the 3x3 board after players made a move.
	 * @param whichPlayer: which player made the move.
	 * @param row: row index of the move.
	 * @param col: column index of the move.
	 */
	public void updateBoard(int whichPlayer, int row, int col) {
		if (whichPlayer == 1)
			board[col][row] = 1;
		else
			board[col][row] = -1;
	}
	/**
	 * Check and store the move if the move is valid.
	 * @param whichPlayer: which player made the move.
	 * @param row: row index of the move.
	 * @param col: column index of the move.
	 * @return true if the move is valid, false if invalid.
	 */
	public boolean checkMove(int whichPlayer, int row, int col) {
		if (turnOfPlayer != whichPlayer)
			return false;
		if (board[col][row] != 0)
			return false;
		this.updateBoard(whichPlayer, row, col);
		moveCount += 1;
		if (turnOfPlayer == 1)
			turnOfPlayer = 2;
		else
			turnOfPlayer = 1;
		return true;
	}
	/**
	 * Generate a string that contains information of the board.
	 * @return string containing the board.
	 */
	public String getBoardString() {
		String str = "";
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				str += board[i][j] + ",";
			}
		}
		return str.substring(0, str.length()-1);
	}
}
