import javax.swing.JFrame;

public class Main {
	
	public static void main(String args[]) {
		Menu menu = new Menu();
		menu.setSize(Map.HEIGHT*45, Map.WIDTH*45);
		//menu.setUndecorated(true);
		//menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
		menu.setVisible(true);
		menu.setResizable(false);
		menu.setLocationRelativeTo(null);
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}