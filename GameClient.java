import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Handles the GUI of the game client and communicates with the server.
 * 
 * @author Robin
 *
 */
public class GameClient implements Runnable {
	Socket sock;
	static JFrame frame;
	static JPanel layoutPanel;
	static JPanel boardLayoutPanel;
	static JPanel inputLayoutPanel;
	static JLabel messageLabel;
	static JLabel[] boardLabel;
	static PrintWriter writer;
	static BufferedReader reader;
	/**
	 * Connects to the server after the player enters his/her name.
	 */
	public void go() {
		try {
			sock = new Socket("127.0.0.1", 6000);
			writer = new PrintWriter(sock.getOutputStream(), true);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			System.out.println("Player: Connected."); //DEBUG
			for (int i = 0; i < 9; i++) {
				boardLabel[i].setEnabled(true);
			}
			
			String subject, content;
			while (true) {
				subject = reader.readLine();
				content = reader.readLine();
				
				if (subject.equals("B")) {
					updateBoard(content);
				}
				else if (subject.equals("M")) {
					messageLabel.setText(content);
				}
				else if (subject.equals("E")) {
					JOptionPane.showMessageDialog(frame, content);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Call go() after player enter his/her name.
	 */
	public void run() {
		this.go();
	}
	/**
	 * update the graphics of the 3x3 board.
	 * @param content: string holding information of the board.
	 */
	private void updateBoard(String content) {
		String[] board = content.split(",");
		for (int i = 0; i < 9; i++) {
			if (board[i].equals("0")) {
				boardLabel[i].setText("");
			}
			else if (board[i].equals("1")) {
				boardLabel[i].setForeground(Color.green);
				boardLabel[i].setText("X");
			}
			else if (board[i].equals("-1")) {
				boardLabel[i].setForeground(Color.red);
				boardLabel[i].setText("O");
			}
		}
	}
	/**
	 * Initiate the GUI.
	 * @param args
	 */
	public static void main(String[] args) {
		JMenuBar menuBar = new JMenuBar();
		JMenu controlMenu = new JMenu("Control");
		JMenu helpMenu = new JMenu("Help");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		JMenuItem instructionMenuItem = new JMenuItem("Instruction");
		
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		
		instructionMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instruction = "";
				instruction += "Some information about the game:\n";
				instruction += "Criteria for a valid move:\n";
				instruction += "-The move is not occupied by any mark.\n";
				instruction += "-The move is made in the player's turn.\n";
				instruction += "-The move is made within the 3 x 3 board.\n";
				instruction += "The game would continue and switch among the opposite player until it reaches either one of the following conditions:\n";
				instruction += "-Player 1 wins.\n";
				instruction += "-Player 2 wins.\n";
				instruction += "-Draw.";
				JOptionPane.showMessageDialog(frame, instruction);
			}
		});
		
		controlMenu.add(exitMenuItem);
		helpMenu.add(instructionMenuItem);
		menuBar.add(controlMenu);
		menuBar.add(helpMenu);
		
		layoutPanel = new JPanel();
		layoutPanel.setLayout(new BorderLayout());
		
		messageLabel = new JLabel("Enter your player name...");
		messageLabel.setFont(new Font("Arial Black", 0, 25));
		
		boardLayoutPanel = new JPanel();
		boardLayoutPanel.setLayout(new GridLayout(3, 3));
		
		boardLabel = new JLabel[9];
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				JLabel label = new JLabel();
				label.setPreferredSize(new Dimension(100,100));
				label.setBackground(Color.white);
				label.setBorder(BorderFactory.createLineBorder(Color.black, 3));
				label.setFont(new Font("Arial Black", 0, 100));
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setEnabled(false);
				
				boardLabel[i + j*3] = label;
				
				final int iFinal = i;
				final int jFinal = j;
				label.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						String move = iFinal + "," + jFinal;
						writer.println(move);
					}
					public void mouseEntered(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
				});
				boardLayoutPanel.add(label);
			}
		}
		inputLayoutPanel = new JPanel(new GridLayout(1, 2));
		JTextField inputTextField = new JTextField("");
		JButton submitButton = new JButton("Submit");
		inputLayoutPanel.add(inputTextField);
		inputLayoutPanel.add(submitButton);
		
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageLabel.setText("WELCOME "+inputTextField.getText());
				inputTextField.setEnabled(false);
				submitButton.setEnabled(false);
				for (int i = 0; i < 9; i++)
					boardLabel[i].setEnabled(true);
				GameClient client = new GameClient();
				Thread thread = new Thread(client);
				thread.start();
			}
		});
		layoutPanel.add(messageLabel, BorderLayout.NORTH);
		layoutPanel.add(boardLayoutPanel, BorderLayout.CENTER);
		layoutPanel.add(inputLayoutPanel, BorderLayout.SOUTH);
		
		frame = new JFrame();
		frame.setTitle("Tic Tac Toe");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(layoutPanel);
		frame.setJMenuBar(menuBar);
		frame.setSize(500, 600);
		frame.setVisible(true);
	}
}