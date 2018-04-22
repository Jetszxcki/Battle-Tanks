import javax.swing.*;
import java.awt.event.*;
import java.io.Serializable;

//@SuppressWarnings("serial")

public class Tank extends Tile implements Condition, Serializable {

	private final String[] tankImgs = {"Images/Tanks/desert_up.png", "Images/Tanks/desert_down.png", 
									   "Images/Tanks/desert_left.png", "Images/Tanks/desert_right.png"};
	
	private boolean[] bulletReleased = new boolean[MAX_BULLETS];
	private boolean[] facing = {false, false, false, false};
	
	public static final int MAX_BULLETS = 5;
	private int bulletsLeft, health;
	private int speed, key;

	public Bullet[] bullet = new Bullet[MAX_BULLETS];
	private Timer reload;
	//private Thread run;

	public Tank() {
		super(35);
		//setImg(tankImgs[3]);
		bulletsLeft = MAX_BULLETS;
		health = 3;
		speed = 3;
		//x = 45;
		//y = 45;
		dx = 0;
		dy = 0;

		for(int ctr = 0; ctr < MAX_BULLETS; ctr++) {
			bullet[ctr] = new Bullet();
		}
		
	//	run = new Thread(this);
	//	run.start();

		reload = new Timer(5000, 
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(bulletsLeft == 0) {
						bulletsLeft = MAX_BULLETS;
						for(int a = 0; a < MAX_BULLETS; a++) {
							//bullet[a].setImg("Images/bullet.png");
							bulletReleased[a] = false;
						}
						System.out.println("RELOADED, Current Bullets : " + bulletsLeft);
					}
				}
			}
		);
		
	}

	public void run() { 
		//while(true) {
			x += dx;
			y += dy;
			setLoc(x,y);

		//	try {
		//		run.sleep(10);
		//	} catch(InterruptedException ie) {}
		//}
	}

	public void stop() {
		dx = 0;
		dy = 0;
	}

	public void move(int key) {
		if(key == 0) {
			dx = 0;
			dy = -speed;
		} 
		else if(key == 1) {
			dx = 0;
			dy = speed;
		}
		else if(key == 2) {
			dx = -speed;
			dy = 0;
		}
		else if(key == 3) {
			dx = speed;
			dy = 0;
		}
		setDirection(key);
	}

	public void shoot() {
		if(bulletsLeft != 0) {
			for(int a = 0; a < MAX_BULLETS; a++) {
				if(bulletsLeft <= MAX_BULLETS && bulletsLeft > 0 && !bulletReleased[a]) {
					bulletsLeft--;
					bulletReleased[a] = true;

					int vel = bullet[a].getSpeed();
					if(isFacingUp()) {
						int upX = x + 14;
						int upY = y;
						bullet[a].setLoc(upX,upY);
						bullet[a].setMovement(0, -vel);
					} 
					else if(isFacingDown()) {
						int downX = x + 14;
						int downY = y + 28;
						bullet[a].setLoc(downX,downY);
						bullet[a].setMovement(0, vel);
					} 
					else if(isFacingLeft()) {
						int leftX = x;
						int leftY = y + 14;
						bullet[a].setLoc(leftX,leftY);
						bullet[a].setMovement(-vel, 0);
					} 
					else if(isFacingRight()) {
						int rightX = x + 28;
						int rightY = y + 14;
						bullet[a].setLoc(rightX,rightY);
						bullet[a].setMovement(vel, 0);
					}
					break;
				}
			}
		} else reload.start();
	}

	public void damage() {
		if(health > 0)
			--health;
		if(health == 0) {
			System.out.println(getBounds() + " is defeated.");
			destroy();
		}
	}

	public void destroy() {
		x = initX;
		y = initY;
		//-heart
	}

	public void setDirection(int key) {
		this.key = key;
		setImg(tankImgs[key]);
		for(int x = 0; x < 4; x++) {
			facing[x] = false;
		}
		facing[key] = true;
	}


	public void setSpeed(int speed) {  this.speed = speed; }
	public int getDirection() { return key; }
	public int getSpeed() { return speed; }
	public int getHealth() { return health; }
	public boolean isFacingUp() { return facing[0]; }
	public boolean isFacingDown() { return facing[1]; }
	public boolean isFacingLeft() { return facing[2]; }
	public boolean isFacingRight() { return facing[3]; }

}
