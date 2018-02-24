import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class Obstacle extends Tile implements Condition {

	//private Timer respawnTime;
	private String originalImg, road;

	private boolean destroyed = false;
	private int health = 3;

	public Obstacle(String skin, String road) {
		super(45);
		this.road = road;
		originalImg = skin;
		setImg(skin);
		/*
		respawnTime = new Timer(30000,
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					health = 3;
					destroyed = false;
					setImg(originalImg);
				}
			}
		);
		*/
	}

	public void damage() {
		if(health > 0) {
			--health;
		}
		if(health == 0) {
			destroyed = true;
			setImg(road);
			//respawnTime.start();
			Thread respawn = new Thread() {
				public void run() {
					try{
						Thread.sleep(50000);
						health = 3;
						destroyed = false;
						setImg(originalImg);
					}catch(InterruptedException i) {}
				}
			};
			respawn.start();
		}
	}

	public boolean isDestroyed() { 
		return destroyed; 
	}

}