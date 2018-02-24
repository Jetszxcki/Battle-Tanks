import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener, Runnable {
	
	private int tileSize, noOfPlayers;

	private Tank[] tanks;
	private Thread painting;
	private Timer timer;
	private Map map;

	public Board(int noOfPlayers, String terrain) {
		addKeyListener(this);
		setFocusable(true);

		map = new Map(terrain);
		tileSize = map.getTileSize();

		this.noOfPlayers = noOfPlayers;
		tanks = new Tank[noOfPlayers];
		for(int x = 0; x < noOfPlayers; x++) {
			tanks[x] = new Tank();
		}

		tanks[0].setInitLoc(45,45);
		tanks[1].setInitLoc(tileSize*17,tileSize*13);

		painting = new Thread(this);
		painting.start();
		timer = new Timer(5, this);
		timer.start();
	}

	public void checkCollisions(Tank tank) {	

	//TANK COLLISIONS
		int speed = tank.getSpeed();
		boolean[] facing = {tank.isFacingUp(), tank.isFacingDown(), tank.isFacingLeft(), tank.isFacingRight()};
		Rectangle tankUp = new Rectangle(tank.get_X(), tank.get_Y()-speed, tank.getTileSize(), tank.getTileSize());
		Rectangle tankDown = new Rectangle(tank.get_X(), tank.get_Y()+speed, tank.getTileSize(), tank.getTileSize());
		Rectangle tankLeft = new Rectangle(tank.get_X()-speed, tank.get_Y(), tank.getTileSize(), tank.getTileSize());
		Rectangle tankRight = new Rectangle(tank.get_X()+speed, tank.get_Y(), tank.getTileSize(), tank.getTileSize());
		Rectangle[] tankDirs = {tankUp, tankDown, tankLeft, tankRight};

		for(int y = 0; y < Map.WIDTH; y++) {
			for(int x = 0; x < Map.HEIGHT; x++) {
				//TANK-WALL
				try{
					if(collision(tankDirs, map.getWallAt(x,y), facing)) {
						tank.stop();
					}
				}catch(NullPointerException n) {}

				//TANK-OBSTACLE
				try{
					if(collision(tankDirs, map.getObstacleAt(x,y), facing) && !map.getObstacleAt(x,y).isDestroyed()) {
						tank.stop();
					}
				}catch(NullPointerException n) {}
			}
		}	

		//TANK-TANK
		for(int a = 0; a < noOfPlayers; a++) {
			if(collision(tankDirs, tanks[a].getBounds(), facing) && tank != tanks[a]) {
				tank.stop();
			}
		}

	//BULLET COLLISIONS
		int bulletSpeed = tank.bullet[0].getSpeed();
		for(int counter = 0; counter < tank.MAX_BULLETS; counter++) {
			Rectangle bulletUp = createBulletRect(0, -bulletSpeed, counter, 9, tank);
			Rectangle bulletDown = createBulletRect(0, bulletSpeed, counter, 9, tank);
			Rectangle bulletLeft = createBulletRect(-bulletSpeed, 0, counter, 9, tank);
			Rectangle bulletRight = createBulletRect(bulletSpeed, 0, counter, 9, tank);
			Rectangle[] bulletDirs = {bulletUp, bulletDown, bulletLeft, bulletRight};

			for(int j = 0; j < Map.WIDTH; j++) {
				for(int i = 0; i < Map.HEIGHT; i++) {
					//BULLET-WALL
					try{
						if(collision(bulletDirs, map.getWallAt(i,j), facing) && !tank.bullet[counter].hasHit()) {		
							tank.bullet[counter].hit();
						}
					}catch(NullPointerException n) {}

					//BULLET-OBSTACLE
					try{
						if(collision(bulletDirs, map.getObstacleAt(i,j), facing) && !map.getObstacleAt(i,j).isDestroyed()) {
							tank.bullet[counter].hit();
							map.getObstacleAt(i,j).damage();
						}
					}catch(NullPointerException n) {}
				}
			}

			//BULLET-TANK
			for(int k = 0; k < noOfPlayers; k++) {
				if(collision(bulletDirs, tanks[k].getBounds()) && tank != tanks[k]) {
					tank.bullet[counter].hit();
					tanks[k].damage();
					//System.out.println("Tank 0: " + tanks[0].getHealth());
					//System.out.println("Tank 1: " + tanks[1].getHealth());
				}
			}

			//BULLET-BULLET
			for(int l = 0; l < noOfPlayers; l++) {
				if(collision(bulletDirs, tanks[l].bullet[counter].getBounds(), facing) && tank != tanks[l] && !tank.bullet[counter].hasHit()) {
					tanks[l].bullet[counter].hit();
					tank.bullet[counter].hit();
				}
			}
		}	
	}

	public boolean collision(Rectangle[] entity, Rectangle target, boolean[] facing) {
		for(int a = 0; a < entity.length; a++) {
			if(entity[a].intersects(target) && facing[a]) {
				return true;
			}
		}
		return false;
	}

	public boolean collision(Rectangle[] entity, Rectangle target) {
		for(int a = 0; a < entity.length; a++) {
			if(entity[a].intersects(target)) {
				return true;
			}
		}
		return false;
	}

	private Rectangle createBulletRect(int dx, int dy, int counter, int size, Tank tank) {
		return (new Rectangle(tank.bullet[counter].get_X() + dx, tank.bullet[counter].get_Y() + dy, size, size));
	}
	
	public void actionPerformed(ActionEvent a) {
		for(int x = 0; x < noOfPlayers; x++) {
			checkCollisions(tanks[x]);
			tanks[x].run();	
		}
		//repaint();
	}

	public void run() {
		while(true) {
			repaint();
			try{
				painting.sleep(5);
			}catch(InterruptedException ie){}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		
		for(int y = 0; y < Map.WIDTH; y++) {
			for(int x = 0; x < Map.HEIGHT; x++) {
				if(map.getMap(x,y).equals("|")) {
					g.drawImage(map.getRoadAt(x,y).getImg(), x*tileSize, y*tileSize, null);
				} 
				else if(map.getMap(x,y).equals("0")) {
					g.drawImage(map.getWallAt(x,y).getImg(), x*tileSize, y*tileSize, null);
				}
				else if(map.getMap(x,y).equals("A")) {
					g.drawImage(map.getObstacleAt(x,y).getImg(), x*tileSize, y*tileSize, null);
				}
			}
		}

		for(int a = 0; a < noOfPlayers; a++) {
			g.drawImage(tanks[a].getImg(), tanks[a].get_X(), tanks[a].get_Y(), null);	
			for(int b = 0; b < Tank.MAX_BULLETS; b++) {
				g.drawImage(tanks[a].bullet[b].getImg(), tanks[a].bullet[b].get_X() == 0 ? -45 : tanks[a].bullet[b].get_X(), tanks[a].bullet[b].get_Y() == 0 ? -45 : tanks[a].bullet[b].get_Y(), null);
			}
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