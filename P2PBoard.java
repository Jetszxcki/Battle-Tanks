import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import java.net.*;
import java.io.*;

public class P2PBoard extends JPanel implements ActionListener, KeyListener {
	
	private InetAddress[] PEER_ADDRESSES;

	private Tank[] enemyTanks;
	private Tank myTank;
	private Map map;

	private int noOfPlayers;
	private int tileSize;
	private int position;

	private static int[][] initLocs;

	public P2PBoard(int noOfPlayers, String terrain, InetAddress[] PEER_ADDRESSES, int position) {
		addKeyListener(this);
		setFocusable(true);

		System.out.println("\nposition : " + position);
		this.position = position;
		this.noOfPlayers = noOfPlayers;
		this.PEER_ADDRESSES = PEER_ADDRESSES;

		System.out.println("PEERS CONNECTED:");
		for(int x = 0; x < noOfPlayers; x++) {
			System.out.println(PEER_ADDRESSES[x]);
		}

		// loads the map
		map = new Map(terrain);
		tileSize = map.getTileSize();
		initLocs = new int[][] { {45,45}, {tileSize*21+10,tileSize}, {tileSize*21+10,tileSize*14+10}, {tileSize,tileSize*14+10} };

		// sets the initial location of tank using its position no.
		myTank = new Tank();
		if(position == 0) {
			myTank.setInitLoc(45,45);
			myTank.setDirection(3);
		} else if(position == 1) {
			myTank.setInitLoc(tileSize*21+10,tileSize);
			myTank.setDirection(1);
		} else if(position == 2) {
			myTank.setInitLoc(tileSize*21+10,tileSize*14+10);
			myTank.setDirection(0);
		} else if(position == 3) {
			myTank.setInitLoc(tileSize,tileSize*14+10);
			myTank.setDirection(2);
		}

		enemyTanks = new Tank[noOfPlayers];
		for(int x = 0; x < noOfPlayers; x++) {
			if(isEnemy(x)) {
				enemyTanks[x] = new Tank();
				enemyTanks[x].setInitLoc(initLocs[x][0], initLocs[x][1]);
				int dir = 0;
				if(x == 0) dir = 3;
				else if(x == 1) dir = 1;
				else if(x == 2) dir = 0;
				else if(x == 3) dir = 2;
				enemyTanks[x].setDirection(dir);
			}
		} 

		// starts the timer for animations/collisions
		Timer timer = new Timer(1,this);
		timer.start();

		receiveEnemyTankPositions();
		receiveEnemyTankDirections();
		receiveEnemyBulletVelocity();
		receiveEnemyBullets();

	}


	public void checkCollisions() {	

	//TANK COLLISIONS
		int speed = myTank.getSpeed();
		boolean[] facing = {myTank.isFacingUp(), myTank.isFacingDown(), myTank.isFacingLeft(), myTank.isFacingRight()};
		Rectangle myTankUp = new Rectangle(myTank.get_X(), myTank.get_Y()-speed, myTank.getTileSize(), myTank.getTileSize());
		Rectangle myTankDown = new Rectangle(myTank.get_X(), myTank.get_Y()+speed, myTank.getTileSize(), myTank.getTileSize());
		Rectangle myTankLeft = new Rectangle(myTank.get_X()-speed, myTank.get_Y(), myTank.getTileSize(), myTank.getTileSize());
		Rectangle myTankRight = new Rectangle(myTank.get_X()+speed, myTank.get_Y(), myTank.getTileSize(), myTank.getTileSize());
		Rectangle[] tankDirs = {myTankUp, myTankDown, myTankLeft, myTankRight};

		for(int y = 0; y < Map.WIDTH; y++) {
			for(int x = 0; x < Map.HEIGHT; x++) {
				//TANK-WALL
				if(collision(tankDirs, map.getWallAt(x,y), facing)) {
					//System.out.println("tank-wall");
					myTank.stop();
					break;
				}

				//TANK-OBSTACLE
				if(collision(tankDirs, map.getObstacleAt(x,y), facing) && !map.getObstacleAt(x,y).isDestroyed()) {
					//System.out.println("tank-obstacle");
					myTank.stop();
					break;
				}
			}
		}	

		//TANK-TANK
		/*
		for(int a = 0; a < noOfPlayers; a++) {
			if(collision(tankDirs, tanks[a].getBounds(), facing) && tanks[tankNo] != tanks[a]) {
				tanks[tankNo].stop();
			}
		}
		*/

	//BULLET COLLISIONS
		int bulletSpeed = myTank.bullet[0].getSpeed();
		for(int counter = 0; counter < Tank.MAX_BULLETS; counter++) {
			Rectangle bulletUp = createBulletRect(0, -bulletSpeed, counter, 9, myTank);
			Rectangle bulletDown = createBulletRect(0, bulletSpeed, counter, 9, myTank);
			Rectangle bulletLeft = createBulletRect(-bulletSpeed, 0, counter, 9, myTank);
			Rectangle bulletRight = createBulletRect(bulletSpeed, 0, counter, 9, myTank);
			Rectangle[] bulletDirs = {bulletUp, bulletDown, bulletLeft, bulletRight};

			for(int j = 0; j < Map.WIDTH; j++) {
				for(int i = 0; i < Map.HEIGHT; i++) {
					//BULLET-WALL
					if(collision(bulletDirs, map.getWallAt(i,j), facing) && !myTank.bullet[counter].hasHit()) {		
						myTank.bullet[counter].hit();
						//System.out.println("bullet-wall");
						break;
					}

					//BULLET-OBSTACLE
					if(collision(bulletDirs, map.getObstacleAt(i,j), facing) && !map.getObstacleAt(i,j).isDestroyed()) {
						myTank.bullet[counter].hit();
						map.getObstacleAt(i,j).damage();
						//System.out.println("bullet-obstacle");
						break;
					}
				}
			}
/*
			for(int k = 0; k < noOfPlayers; k++) {
				//BULLET-TANK
				if(collision(bulletDirs, tanks[k].getBounds()) && tanks[tankNo] != tanks[k]) {
					tanks[tankNo].bullet[counter].hit();
					tanks[k].damage();
					//System.out.println("Tank 0: " + tanks[0].getHealth());
					//System.out.println("Tank 1: " + tanks[1].getHealth());
				}
				//BULLET-BULLET
				if(collision(bulletDirs, tanks[k].bullet[counter].getBounds(), facing) && tanks[tankNo] != tanks[k] && !tanks[tankNo].bullet[counter].hasHit()) {
					tanks[k].bullet[counter].hit();
					tanks[tankNo].bullet[counter].hit();
				}
			}
*/
		}	
		//TANK-PUPS
		// to be implemented
	}


	public boolean collision(Rectangle[] entity, Rectangle target, boolean[] facing) {
		for(int a = 0; a < entity.length; a++) {
			if(entity[a].intersects(target) && target != entity[a] && facing[a]) {
				return true;
			}
		}
		return false;
	}

	public boolean collision(Rectangle[] entity, Rectangle target) {
		for(int a = 0; a < entity.length; a++) {
			if(entity[a].intersects(target) && target != entity[a]) {
				return true;
			}
		}
		return false;
	}

	private Rectangle createBulletRect(int dx, int dy, int counter, int size, Tank tank) {
		return (new Rectangle(tank.bullet[counter].get_X() + dx, tank.bullet[counter].get_Y() + dy, size, size));
	}

	private boolean isEnemy(int enemyPosition) {
		return position != enemyPosition;
	}


// ======================= ACTION HANDLERS ===========================

	public void actionPerformed(ActionEvent e) {
		checkCollisions();
		myTank.run();
		for(int x = 0; x < enemyTanks.length; x++) {
			//System.out.println("P2PBoard Enemy tank[" + x + "] : " + enemyTanks[x]);
			if(isEnemy(x)) {
				enemyTanks[x].run();
			}
		}
		repaint();
	}

	public void paint(Graphics g) {
		super.paint(g);

		for(int y = 0; y < Map.WIDTH; y++) {
			for(int x = 0; x < Map.HEIGHT; x++) {
				if(map.isRoad(x,y)) {
					g.drawImage(map.getRoadAt(x,y).getImg(), x*tileSize, y*tileSize, null);
				} 
				else if(map.isWall(x,y)) {
					g.drawImage(map.getWallAt(x,y).getImg(), x*tileSize, y*tileSize, null);
				}
				else if(map.isObstacle(x,y)) {
					g.drawImage(map.getObstacleAt(x,y).getImg(), x*tileSize, y*tileSize, null);
				}
			}
		}

		//prints your tank's position
		g.drawImage(myTank.getImg(), myTank.get_X(), myTank.get_Y(), null);	
		for(int b = 0; b < Tank.MAX_BULLETS; b++) {
			g.drawImage(myTank.bullet[b].getImg(), myTank.bullet[b].get_X() == 0 ? -100 : myTank.bullet[b].get_X(), myTank.bullet[b].get_Y() == 0 ? -100 : myTank.bullet[b].get_Y(), null);
		}
		// prints the enemy tanks' positions
		for(int c = 0; c < noOfPlayers; c++) {
			//System.out.println(enemyTanks[c].get_X() +" : "+enemyTanks[c].get_Y());
			if(isEnemy(c)) {
				g.drawImage(enemyTanks[c].getImg(), enemyTanks[c].get_X(), enemyTanks[c].get_Y(), null);
				for(int d = 0; d < Tank.MAX_BULLETS; d++) {
					g.drawImage(enemyTanks[c].bullet[d].getImg(), enemyTanks[c].bullet[d].get_X() == 0 ? -100 : enemyTanks[c].bullet[d].get_X(), enemyTanks[c].bullet[d].get_Y() == 0 ? -100 : enemyTanks[c].bullet[d].get_Y(), null);
				}
			}
		}
		
	}

	public void keyTyped(KeyEvent k) {}
	public void keyPressed(KeyEvent k) {
		int keyCode = k.getKeyCode();
		int[] p1 = { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };

		for(int a = 0; a < p1.length; a++) {
			if(keyCode == p1[a]) {
				myTank.move(a);
				sendMyTankPosition();
				sendMyTankDirection();
			}
		}

		if(keyCode == KeyEvent.VK_F) {
			myTank.shoot();
			sendMyBullets();
			sendMyBulletVelocity();
		}
	}
	public void keyReleased(KeyEvent k) {
		int keyCode = k.getKeyCode();
		
		if(keyCode == KeyEvent.VK_UP 
		|| keyCode == KeyEvent.VK_DOWN 
		|| keyCode == KeyEvent.VK_LEFT
		|| keyCode == KeyEvent.VK_RIGHT) {
			myTank.stop();
			sendMyTankPosition();
			sendMyTankDirection();
		} 
	}

// ====================== NETWORKING COMPONENTS ==================================

	public void receiveEnemyTankPositions() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket receivingSocket = new DatagramSocket(1212);
					byte[] receivedData = new byte[256];
					while(true) { // to be changed to while(playing)
						for(int x = 0; x < noOfPlayers; x++) {
							if(isEnemy(x)) {
								DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
								receivingSocket.receive(receivePacket);

								ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
								ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
								int[] enemyCoors = (int[])ois.readObject();  //receives the dx and dy of an enemy
								int[] enemyXY = (int[])ois.readObject();     // receives the location of an enemy
								enemyTanks[x].setDX(enemyCoors[0]);
								enemyTanks[x].setDY(enemyCoors[1]);
								enemyTanks[x].setLoc(enemyXY[0],enemyXY[1]);
								//enemyTanks[x] = (Tank)ois.readObject();
								ois.close();
								bais.close();
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendMyTankPosition() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket sendingSocket = new DatagramSocket();
					//while(true) {
						for(int x = 0; x < noOfPlayers; x++) {
							if(isEnemy(x)) {
							//if(!PEER_ADDRESSES[x].toString().equals(Menu.getSelfIpAddress())) {
								ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
								ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));
								oos.writeObject(new int[] {myTank.getDX(), myTank.getDY()}); // sends the dx and dy 
								oos.writeObject(new int[] {myTank.get_X(),myTank.get_Y()});  // sends the x and y coordinates 
								oos.flush();
								byte[] data = baos.toByteArray();

								DatagramPacket sendPacket = new DatagramPacket(data, data.length, PEER_ADDRESSES[x], 4445);
								sendingSocket.send(sendPacket);

								oos.close();	
							//}
							}
						}
					//}
					sendingSocket.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void receiveEnemyTankDirections() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket receiveDirSocket = new DatagramSocket(1213);
					byte[] receivedData = new byte[100];
					while(true) { // to be changed to while(playing)
						for(int x = 0; x < noOfPlayers; x++) {
							if(isEnemy(x)) {
								DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
								receiveDirSocket.receive(receivePacket);

								ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
								ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
								enemyTanks[x].setDirection((int)ois.readObject());
								ois.close();
								bais.close();
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendMyTankDirection() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket directionSocket = new DatagramSocket();
					for(int x = 0; x < noOfPlayers; x++) {
						if(isEnemy(x)) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
							ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));
							int direction = myTank.getDirection();
							oos.writeObject(direction);
							oos.flush();
							byte[] data = baos.toByteArray();

							DatagramPacket packet = new DatagramPacket(data, data.length, PEER_ADDRESSES[x], 4446);
							directionSocket.send(packet);
							oos.close();
						}
					}
					directionSocket.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void receiveEnemyBullets() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket receivebulletSocket = new DatagramSocket(1214);
					byte[] receivedData = new byte[100];
					while(true) {
						for(int x = 0; x < noOfPlayers; x++) {
							if(isEnemy(x)) {
								DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
								receivebulletSocket.receive(receivePacket);

								ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
								ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));

								int[] bulletCoorX = (int[])ois.readObject();
								int[] bulletCoorY = (int[])ois.readObject();
									
								for(int a = 0; a < Tank.MAX_BULLETS; a++) {
									enemyTanks[x].bullet[a].setLoc(bulletCoorX[a], bulletCoorY[a]);
								}
								ois.close();
								bais.close();
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendMyBullets() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket sendbulletSocket = new DatagramSocket();
					for(int x = 0; x < noOfPlayers; x++) {
						if(isEnemy(x)) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
							ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));

							int[] bulletCoorX = new int[Tank.MAX_BULLETS];
							int[] bulletCoorY = new int[Tank.MAX_BULLETS];

							for(int a = 0; a < Tank.MAX_BULLETS; a++) {
								bulletCoorX[a] = myTank.bullet[a].get_X();
								bulletCoorY[a] = myTank.bullet[a].get_Y();
							}
							oos.writeObject(bulletCoorX);
							oos.writeObject(bulletCoorY);
							oos.flush();
							byte[] data = baos.toByteArray();
							sendbulletSocket.send(new DatagramPacket(data, data.length, PEER_ADDRESSES[x], 4447));
							oos.close();
						}
					}
					sendbulletSocket.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void receiveEnemyBulletVelocity() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket receivebulletVelSocket = new DatagramSocket(1215);
					byte[] receivedData = new byte[100];
					while(true) {
						for(int x = 0; x < noOfPlayers; x++) {
							if(isEnemy(x)) {
								DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
								receivebulletVelSocket.receive(receivePacket);

								ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
								ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));

								int[] bulletVelX = (int[])ois.readObject();
								int[] bulletVelY = (int[])ois.readObject();
									
								for(int a = 0; a < Tank.MAX_BULLETS; a++) {
									enemyTanks[x].bullet[a].setMovement(bulletVelX[a], bulletVelY[a]);
								}
								ois.close();
								bais.close();
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendMyBulletVelocity() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket sendingSocket = new DatagramSocket();
					for(int x = 0; x < noOfPlayers; x++) {
						if(isEnemy(x)) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
							ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));

							int[] bulletVelX = new int[Tank.MAX_BULLETS];
							int[] bulletVelY = new int[Tank.MAX_BULLETS];

							for(int a = 0; a < Tank.MAX_BULLETS; a++) {
								bulletVelX[a] = myTank.bullet[a].getDX();
								bulletVelY[a] = myTank.bullet[a].getDY();
							}
							oos.writeObject(bulletVelX);
							oos.writeObject(bulletVelY);
							oos.flush();
							byte[] data = baos.toByteArray();
							sendingSocket.send(new DatagramPacket(data, data.length, PEER_ADDRESSES[x], 4448));
							oos.close();
						}
					}
					sendingSocket.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}