import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.table.*;

@SuppressWarnings("serial")

public class Menu extends JFrame implements ActionListener {

	DatagramSocket hostSocket = null;

	String[] playerNames;
	InetAddress[] CLIENT_IP_ADDRESSES = new InetAddress[4];
	InetAddress hostAddress = null;

	JButton[] multiplayerButtons = new JButton[3];
	JButton[] menuButtons = new JButton[3];
	JButton connectButton; // client
	JButton startGame;     // host starts the game
	JButton connectedClientBack;
	JButton clientBack;
	JButton hostBack;

	JTextField clientConnectField; // this is where client enters the host IP
	JTextField playerNameField; // this is where a player sets his/her name

	JPanel connectedClientPanel;
	JPanel multiplayerPanel;
	JPanel clientPanel;
	JPanel menuPanel;
	JPanel hostPanel;

	JTable clientLobby;
	JTable hostLobby;

	boolean listening;
	int playersJoined = 0;

	String playerName;


	public Menu() {
		setLayout(new BorderLayout());
		setMenuPanel();
	}

	public void setMenuPanel() {
		menuPanel = new JPanel();
		menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		playerNameField = new JTextField(10);
		menuPanel.add(playerNameField);

		String[] menuStrings = {"2 Players", "Multiplayer", "Quit"};
		for(int x = 0; x < menuButtons.length; x++) {
			menuButtons[x] = new JButton(menuStrings[x]);
			menuButtons[x].addActionListener(this);
			menuPanel.add(menuButtons[x]);
		}
		this.add(menuPanel);
		this.revalidate();
	}

	private void setMultiplayerPanel() {
		multiplayerPanel = new JPanel();
		multiplayerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		String[] mpStrings = {"HOST","CLIENT","BACK"};
		for(int x = 0; x < multiplayerButtons.length; x++) {
			multiplayerButtons[x] = new JButton(mpStrings[x]);
			multiplayerButtons[x].addActionListener(this);
			multiplayerPanel.add(multiplayerButtons[x]);
		}
		//this.remove(menuPanel);
		this.add(multiplayerPanel);
		this.revalidate();
	}

	private void setHostPanel() {
		String[] cols = {"Name","IP Address"};

		hostPanel = new JPanel();
		hostPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

// ------------------ Host Lobby Table -----------------------
		hostLobby = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		//hostLobby.setTableHeader(null);
		hostLobby.setModel(new DefaultTableModel(new String[0][2], cols));
		JScrollPane scroll = new JScrollPane(hostLobby);
		scroll.setPreferredSize(new Dimension(200,100));

		TableColumn[] hostColumns = { hostLobby.getColumn("Name"),
							  hostLobby.getColumn("IP Address") };
		
		for(int x = 0; x < hostColumns.length; x++)	{						  
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			hostColumns[x].setCellRenderer(dtcr);
			hostColumns[x].setHeaderRenderer(dtcr);
		}
// ----------------------------- END  ----------------------------

		startGame = new JButton("START");
		startGame.addActionListener(this);

		hostPanel.add(scroll);
		hostPanel.add(startGame);

		hostBack = new JButton("BACK");
		hostBack.addActionListener(this);
		hostPanel.add(hostBack);
		//this.remove(multiplayerPanel);
		this.add(hostPanel);
		this.revalidate();
	}

	private void setClientPanel() {
		clientPanel = new JPanel();
		clientPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		clientConnectField = new JTextField(10);
		connectButton = new JButton("CONNECT");
		connectButton.addActionListener(this);

		clientPanel.add(clientConnectField);
		clientPanel.add(connectButton);

		clientBack = new JButton("BACK");
		clientBack.addActionListener(this);
		clientPanel.add(clientBack);

		//this.remove(multiplayerPanel);
		this.add(clientPanel);
		this.revalidate();
	}

	private void setConnectedClientPanel() {
		this.remove(clientPanel);
		String[] cols = {"Name","IP Address"};

		connectedClientPanel = new JPanel();
		connectedClientPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

// ------------------ Client Lobby Table -----------------------
		clientLobby = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		//clientLobby.setTableHeader(null);
		clientLobby.setModel(new DefaultTableModel(new String[0][2], cols));
		JScrollPane scroll = new JScrollPane(clientLobby);
		scroll.setPreferredSize(new Dimension(200,100));

		TableColumn[] hostColumns = { clientLobby.getColumn("Name"),
							  clientLobby.getColumn("IP Address") };
		
		for(int x = 0; x < hostColumns.length; x++)	{						  
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			hostColumns[x].setCellRenderer(dtcr);
			hostColumns[x].setHeaderRenderer(dtcr);
		}
// ----------------------------- END  ----------------------------

		connectedClientBack = new JButton("Leave Lobby");
		connectedClientBack.addActionListener(this);
		connectedClientPanel.add(scroll);
		connectedClientPanel.add(connectedClientBack);
		//this.remove(multiplayerPanel);
		this.add(connectedClientPanel);
		this.revalidate();

	}

	private boolean hasName() {
		String name = playerNameField.getText();
		if(name.equals("") || name == null) {
			JOptionPane.showMessageDialog(null, "Input your player name first.", "No Player Name", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		playerName = name;
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuButtons[0] && hasName()) {
			this.dispose();
			new GameFrame(new Board(2, "colosseum.tnk"));
		}
		else if(e.getSource() == menuButtons[1] && hasName()) {
			this.remove(menuPanel);
			setMultiplayerPanel();
		}
		else if(e.getSource() == menuButtons[2]) {
			System.exit(0);
		}
		else if(e.getSource() == multiplayerButtons[0]) {
			this.remove(multiplayerPanel);
			setHostPanel();
			listen();
		}
		else if(e.getSource() == multiplayerButtons[1]) {
			this.remove(multiplayerPanel);
			setClientPanel();
		}
		else if(e.getSource() == multiplayerButtons[2]) {
			this.remove(multiplayerPanel);
			setMenuPanel();
		}
		else if(e.getSource() == connectedClientBack) {
			//this.remove();
		}
		else if(e.getSource() == clientBack) {
			this.remove(clientPanel);
			setMultiplayerPanel();
		}
		else if(e.getSource() == hostBack) {
			this.remove(hostPanel);
			setMultiplayerPanel();
			cleanNetwork();
		}
		else if(e.getSource() == startGame) {
			if(playersJoined == 1) {
				JOptionPane.showMessageDialog(null, "There are no other players in lobby.", "Unable to Start", JOptionPane.INFORMATION_MESSAGE);
			} else {
				establishPeerConnections();
			}
		}
		else if(e.getSource() == connectButton) {
			String field = clientConnectField.getText();
			if(field.equals("") || field == null) {
				JOptionPane.showMessageDialog(null, "    Input IP Address first.", "No IP Address", JOptionPane.INFORMATION_MESSAGE);
			} else {
				try {
					InetAddress ip = InetAddress.getByName(field);
					if(playersJoined == 4) {
						JOptionPane.showMessageDialog(null, "Max of 4 Players only.", "Lobby Full", JOptionPane.WARNING_MESSAGE);
					}
					else if(playersJoined < 4) {
						setConnectedClientPanel();
						connect(ip);
					}
					clientBack.setEnabled(false);
					connectButton.setEnabled(false);
				}catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "Host does not exist or invalid IP input.", "Unknown Host", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}


// ==================  networking part ==================================

	public void establishPeerConnections() {
		try {
			int position = 1;
			listening = false;
			DatagramSocket socket = new DatagramSocket();
			for(int x = 1; x < playersJoined; x++) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));
				oos.writeObject(CLIENT_IP_ADDRESSES);
				oos.flush();
				byte[] data = baos.toByteArray();

				socket.send(new DatagramPacket(data, data.length, CLIENT_IP_ADDRESSES[x], 8888));
				oos.close();

				// HOST SENDS THE NO. OF PLAYERS TO THE CLIENTS
				ByteArrayOutputStream baos2 = new ByteArrayOutputStream(256);
				ObjectOutputStream oos2 = new ObjectOutputStream(new BufferedOutputStream(baos2));
				oos2.writeObject(playersJoined);
				oos2.flush();
				byte[] data2 = baos2.toByteArray();

				socket.send(new DatagramPacket(data2, data2.length, CLIENT_IP_ADDRESSES[x], 8888));
				oos2.close();

				// HOST SENDS EACH PLAYER THEIR RESPECTIVE POSITIONS
				ByteArrayOutputStream baos3 = new ByteArrayOutputStream(256);
				ObjectOutputStream oos3 = new ObjectOutputStream(new BufferedOutputStream(baos3));
				oos3.writeObject(position);
				oos3.flush();
				position++;
				byte[] data3 = baos3.toByteArray();

				socket.send(new DatagramPacket(data3, data3.length, CLIENT_IP_ADDRESSES[x], 8888));
				oos3.close();
			}
			socket.close();
			this.dispose();
			new GameFrame(new P2PBoard(playersJoined, "colosseum.tnk", CLIENT_IP_ADDRESSES, 0));
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	
	public void connect(InetAddress hostIP) {
		try {
		// connects to host
			DatagramSocket clientSocket = new DatagramSocket();

			ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));
			oos.writeObject(new Object[]{playerName, InetAddress.getByName(getSelfIpAddress())});
			oos.flush();
			byte[] sendingData = baos.toByteArray();

			clientSocket.send(new DatagramPacket(sendingData, sendingData.length, hostIP, 4545));	
			clientSocket.close();

		// receive other peers' IP addresses from host before gameplay
			DatagramSocket confirmationSocket = new DatagramSocket(8888);
			byte[] confirmData = new byte[256];
			confirmationSocket.receive(new DatagramPacket(confirmData, confirmData.length));
			ByteArrayInputStream bais = new ByteArrayInputStream(confirmData);
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
			
			InetAddress[] ips = (InetAddress[])ois.readObject();
			
		// receive no of players
			byte[] confirmData2 = new byte[256];
			confirmationSocket.receive(new DatagramPacket(confirmData2, confirmData2.length));
			ByteArrayInputStream bais2 = new ByteArrayInputStream(confirmData2);
			ObjectInputStream ois2 = new ObjectInputStream(new BufferedInputStream(bais2));
			
			int noOfPlayers = (int)ois2.readObject();

		// receive position
			byte[] confirmData3 = new byte[256];
			confirmationSocket.receive(new DatagramPacket(confirmData3, confirmData3.length));
			ByteArrayInputStream bais3 = new ByteArrayInputStream(confirmData3);
			ObjectInputStream ois3 = new ObjectInputStream(new BufferedInputStream(bais3));
			
			int position = 0;
			position = (int)ois3.readObject();

			confirmationSocket.close();
			this.dispose();
			new GameFrame(new P2PBoard(noOfPlayers, "colosseum.tnk", ips, position));

		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Host does not exist or invalid IP input.", "Unknown Host", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch(ClassNotFoundException c) {
			JOptionPane.showMessageDialog(null, "Receive operation interrupted.", "Class Mismatch", JOptionPane.ERROR_MESSAGE);
			c.printStackTrace();
		}
	}

	public void listen() {
		new Thread() {
			public void run() {
				try {
					hostAddress = InetAddress.getByName(getSelfIpAddress());
					storeInformation(hostAddress,playerName);
					hostSocket = new DatagramSocket(4545);
					byte[] receivedData = new byte[256];
					listening = true;

					while(listening) {
						DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
						hostSocket.receive(packet);

						ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
						ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
						Object[] receivedInfo = (Object[])ois.readObject();
						String pname = (String)receivedInfo[0];
						InetAddress newIP = (InetAddress)receivedInfo[1];
						storeInformation(packet.getAddress(),pname);
					}
					hostSocket.close();
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void storeInformation(InetAddress newIP, String pname) {
		CLIENT_IP_ADDRESSES[playersJoined] = newIP;
		playersJoined++;
		System.out.print("\nPlayers in Lobby:");
		for(int x = 0; x < playersJoined; x++) {
			System.out.print(" | " + CLIENT_IP_ADDRESSES[x]);
		}

		// store name of new player
		if(playersJoined == 1) {
			playerNames = new String[playersJoined];
			playerNames[0] = pname;
		} else {
			String[] temp = playerNames;
			playerNames = new String[playersJoined];
			for(int x = 0; x < temp.length; x++) {
				playerNames[x] = temp[x];
			}
			playerNames[playersJoined-1] = pname;
		}

		// display information of player's in lobby
		//if(hostAddress != newIP) {
			DefaultTableModel dtm = (DefaultTableModel)hostLobby.getModel();
			dtm.addRow(new String[]{pname,String.valueOf(newIP)});
		//}
		
	}

	private void cleanNetwork() {
		hostSocket.close();
		CLIENT_IP_ADDRESSES = new InetAddress[4];
		playerNames = null;
		playersJoined = 0;
	}

	public static String getSelfIpAddress() { 
	    try {
	        for(Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = (NetworkInterface)en.nextElement();
	            for(Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
	                if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
	                    String ipAddress=inetAddress.getHostAddress().toString();
	                    return ipAddress;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	    	ex.printStackTrace();
	    	return null;
	    }
	    return null; 
    }

}