import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.InetAddress;

public class GameFrame extends JFrame implements ActionListener {

	//private InetAddress[] IP_ADDRESSES;

	private JButton[] menuButtons = new JButton[3];
	private JPanel centerPanel, rightPanel;

	//public GameFrame(boolean multiplayer, InetAddress[] IP_ADDRESSES, int noOfPlayers) {
	public GameFrame(JPanel board) {
		setLayout(new BorderLayout());

		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());

		/*this.IP_ADDRESSES = IP_ADDRESSES;
		if(multiplayer) {
			centerPanel.add(new P2PBoard(noOfPlayers, "colosseum.tnk", IP_ADDRESSES));
		}else{
			centerPanel.add(new Board(2, "colosseum.tnk"));
		}
		*/
		centerPanel.add(board);
		this.add(centerPanel, BorderLayout.CENTER);

		setSize(Map.HEIGHT*45, Map.WIDTH*45);
		//setUndecorated(true);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setComponents() {

		//rightPanel = new JPanel();
		centerPanel = new JPanel();
		//Insets insets = rightPanel.getInsets();

		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(new Board(2, "colosseum.tnk"), BorderLayout.CENTER);
		
		//rightPanel.setBackground(Color.BLACK);
		//rightPanel.setPreferredSize(new Dimension(329,700));

		add(centerPanel, BorderLayout.CENTER);
		//add(rightPanel, BorderLayout.EAST);

	}


	public void actionPerformed(ActionEvent a) {
		
	}

}