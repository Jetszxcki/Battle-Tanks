import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
		
	private JPanel leftPanel, rightPanel, buttonPanel;
	private JButton[] menuButtons = new JButton[3];
	private Board board;

	public Menu() {
		setLayout(new BorderLayout());		
		setComponents();
	}

	private void setComponents() {
		buttonPanel = new JPanel();
		//buttonPanel.setLayout();
		String[] strings = {"2 Players", "Multiplayer", "Quit"};
		for(int x = 0; x < menuButtons.length; x++) {
			menuButtons[x] = new JButton(strings[x]);
			menuButtons[x].setFocusable(false);
			buttonPanel.add(menuButtons[x]);
		}
		buttonPanel.setFocusable(false);
		add(buttonPanel, BorderLayout.SOUTH);


		add(board = new Board(2, "warzone.tnk"), BorderLayout.CENTER);
		//add(rightPanel = new JPanel(), BorderLayout.EAST);
		//leftPanel.setBackground(Color.BLACK);
		//rightPanel.setBackground(Color.ORANGE);
		//rightPanel.setPreferredSize(new Dimension(300,700));
	}

}