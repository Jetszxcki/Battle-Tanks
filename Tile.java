import javax.swing.*;
import java.awt.*;

public class Tile extends Rectangle {

	protected int x, y, initX, initY;
	protected int tileSize;
	protected Image img;

	public Tile(int tileSize) {
		this.tileSize = tileSize;
	}

	public void setImg(String name) {
		img = new ImageIcon(name).getImage();
	}

	public void setInitLoc(int ix, int iy) {
		setBounds(ix,iy,tileSize,tileSize);
		initX = ix;
		initY = iy;
		x = ix;
		y = iy;
	}

	public void setLoc(int locX, int locY) {
		x = locX;
		y = locY;
	}

	public Rectangle getBounds() {
		return (new Rectangle(x,y,tileSize,tileSize));
	}

	public int getTileSize() { return tileSize; }
	public Image getImg() { return img; }
	public int get_X() { return x; }
	public int get_Y() { return y; }

}