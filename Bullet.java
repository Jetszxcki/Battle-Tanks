
public class Bullet extends Tile implements Runnable {

	private int dx, dy, speed;
	private Thread fire;

	public Bullet() {
		super(9);
		speed = 5;
		setImg("bullet.png");
		dx = 0;
		dy = 0;

		fire = new Thread(this);
		fire.start();
	}

	public void run() {
		while(true) {
			x += dx;
			y += dy;
			try {
				fire.sleep(10);
			}catch(InterruptedException e){}
		}
	}

	public void hit() {
		dx = 0;
		dy = 0;
		x = 0;
		y = 0;		
	}

	public void setMovement(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public boolean hasHit() { return (x == 0 && y == 0); }
	public int getSpeed() { return speed; }

}