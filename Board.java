import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import java.net.*;
import java.io.*;

@SuppressWarnings("serial")

public class Board extends JPanel implements ActionListener, KeyListener {
	
	private int tileSize, noOfPlayers;

	private PowerUp[] powerUp;
	private Tank[] tanks;
	private Timer timer;
	private Map map;

	private Random randomizer;
	
	public Board(int noOfPlayers, String terrain) {
		addKeyListener(this);
		setFocusable(true);

		map = new Map(terrain);
		randomizer = new Random();
		tileSize = map.getTileSize();

		this.noOfPlayers = noOfPlayers;
		tanks = new Tank[noOfPlayers];
		powerUp = new PowerUp[noOfPlayers];

		for(int x = 0; x < noOfPlayers; x++) {
			tanks[x] = new Tank();
			powerUp[x] = new PowerUp();
		}

		tanks[0].setInitLoc(45,45);
		tanks[1].setInitLoc(tileSize*17,tileSize*13);

		//thread = new Thread(this);
		//thread.start();
		timer = new Timer(1, this);
		timer.start();
		//randomizePowerUps();
	}

	public void checkCollisions() {	

	//TANK COLLISIONS
		for(int tankNo = 0; tankNo < tanks.length; tankNo++) {
			int speed = tanks[tankNo].getSpeed();
			boolean[] facing = {tanks[tankNo].isFacingUp(), tanks[tankNo].isFacingDown(), tanks[tankNo].isFacingLeft(), tanks[tankNo].isFacingRight()};
			Rectangle tankUp = new Rectangle(tanks[tankNo].get_X(), tanks[tankNo].get_Y()-speed, tanks[tankNo].getTileSize(), tanks[tankNo].getTileSize());
			Rectangle tankDown = new Rectangle(tanks[tankNo].get_X(), tanks[tankNo].get_Y()+speed, tanks[tankNo].getTileSize(), tanks[tankNo].getTileSize());
			Rectangle tankLeft = new Rectangle(tanks[tankNo].get_X()-speed, tanks[tankNo].get_Y(), tanks[tankNo].getTileSize(), tanks[tankNo].getTileSize());
			Rectangle tankRight = new Rectangle(tanks[tankNo].get_X()+speed, tanks[tankNo].get_Y(), tanks[tankNo].getTileSize(), tanks[tankNo].getTileSize());
			Rectangle[] tankDirs = {tankUp, tankDown, tankLeft, tankRight};

			for(int y = 0; y < Map.WIDTH; y++) {
				for(int x = 0; x < Map.HEIGHT; x++) {
					//TANK-WALL
					if(collision(tankDirs, map.getWallAt(x,y), facing)) {
						//System.out.println("tank-wall");
						tanks[tankNo].stop();
						break;
					}

					//TANK-OBSTACLE
					if(collision(tankDirs, map.getObstacleAt(x,y), facing) && !map.getObstacleAt(x,y).isDestroyed()) {
						//System.out.println("tank-obstacle");
						tanks[tankNo].stop();
						break;
					}
				}
			}	

			//TANK-TANK
			for(int a = 0; a < noOfPlayers; a++) {
				if(collision(tankDirs, tanks[a].getBounds(), facing) && tanks[tankNo] != tanks[a]) {
					tanks[tankNo].stop();
				}
			}

		//BULLET COLLISIONS
			int bulletSpeed = tanks[tankNo].bullet[0].getSpeed();
			for(int counter = 0; counter < Tank.MAX_BULLETS; counter++) {
				Rectangle bulletUp = createBulletRect(0, -bulletSpeed, counter, 9, tanks[tankNo]);
				Rectangle bulletDown = createBulletRect(0, bulletSpeed, counter, 9, tanks[tankNo]);
				Rectangle bulletLeft = createBulletRect(-bulletSpeed, 0, counter, 9, tanks[tankNo]);
				Rectangle bulletRight = createBulletRect(bulletSpeed, 0, counter, 9, tanks[tankNo]);
				Rectangle[] bulletDirs = {bulletUp, bulletDown, bulletLeft, bulletRight};

				for(int j = 0; j < Map.WIDTH; j++) {
					for(int i = 0; i < Map.HEIGHT; i++) {
						//BULLET-WALL
						if(collision(bulletDirs, map.getWallAt(i,j), facing) && !tanks[tankNo].bullet[counter].hasHit()) {		
							tanks[tankNo].bullet[counter].hit();
							//System.out.println("bullet-wall");
							break;
						}

						//BULLET-OBSTACLE
						if(collision(bulletDirs, map.getObstacleAt(i,j), facing) && !map.getObstacleAt(i,j).isDestroyed()) {
							tanks[tankNo].bullet[counter].hit();
							map.getObstacleAt(i,j).damage();
							//System.out.println("bullet-obstacle");
							break;
						}
					}
				}

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
			}	

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

	private void randomizePowerUps() {
		int[][] obstaclePositions = map.getObstaclePositions();
		int[][] powUpPositions = new int[powerUp.length][2];
		int rx = 0, ry = 0;

		for(int x = 0; x < powerUp.length; x++) {
			do {
				rx = randomizer.nextInt(16);
				ry = randomizer.nextInt(16);
			} while(invalid_P_UPS_Position(rx,ry,obstaclePositions,powUpPositions));
			powUpPositions[x][0] = rx;
			powUpPositions[x][1] = ry;
		}
		for(int x = 0; x < powerUp.length; x++) {
			//System.out.println(powUpPositions[x][0] + "," + powUpPositions[x][1] + " | ");
			powerUp[x].setLoc(powUpPositions[x][0] * 45 + 9, powUpPositions[x][1] * 45 + 9);
		}
	}

	private boolean invalid_P_UPS_Position(int rx, int ry, int[][] obstaclePositions, int[][] powUpPositions) {
		boolean invalid = true;
		for(int x = 0; x < obstaclePositions.length; x++) {
			if(rx == obstaclePositions[x][0] && ry == obstaclePositions[x][1]) {
				invalid = false;
				break;
			}
		}
		for(int x = 0; x < powUpPositions.length; x++) {
			if(rx == powUpPositions[x][0] && ry == powUpPositions[x][1]) {
				invalid = true;
				break;
			}
		}
		return invalid;
	}


// ========================    ACTION HANDLERS   ================================

	
	public void actionPerformed(ActionEvent a) {
		for(int x = 0; x < noOfPlayers; x++) {
			checkCollisions();
			tanks[x].run();	
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
		
		for(int a = 0; a < noOfPlayers; a++) {
			g.drawImage(tanks[a].getImg(), tanks[a].get_X(), tanks[a].get_Y(), null);	
			for(int b = 0; b < Tank.MAX_BULLETS; b++) {
				g.drawImage(tanks[a].bullet[b].getImg(), tanks[a].bullet[b].get_X() == 0 ? -100 : tanks[a].bullet[b].get_X(), tanks[a].bullet[b].get_Y() == 0 ? -100 : tanks[a].bullet[b].get_Y(), null);
			}
		}
		for(int b = 0; b < powerUp.length; b++) {
			g.drawImage(powerUp[b].getImg(), powerUp[b].get_X(), powerUp[b].get_Y(), null);
		}
	}	
	
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		int[] p1 = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
		int[] p2 = {KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D};

		for(int a = 0; a < p1.length; a++) {
			if(keyCode == p1[a]) tanks[0].move(a);
			if(keyCode == p2[a]) tanks[1].move(a);
		}

		if(keyCode == KeyEvent.VK_M) tanks[0].shoot();
		if(keyCode == KeyEvent.VK_V) tanks[1].shoot();
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if(keyCode == KeyEvent.VK_UP 
		|| keyCode == KeyEvent.VK_DOWN 
		|| keyCode == KeyEvent.VK_LEFT
		|| keyCode == KeyEvent.VK_RIGHT) {
			tanks[0].stop();
		} 

		if(keyCode == KeyEvent.VK_W 
		|| keyCode == KeyEvent.VK_S 
		|| keyCode == KeyEvent.VK_A
		|| keyCode == KeyEvent.VK_D) {
			tanks[1].stop();
		} 	
	}

}