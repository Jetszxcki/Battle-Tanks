import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.Serializable;

//@SuppressWarnings("serial")
public class Tile extends Rectangle implements Serializable {

	protected int x, y, initX, initY;
	protected int dx, dy;
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
		setBounds(locX, locY, tileSize, tileSize);
		x = locX;
		y = locY;
	}

	public Rectangle getBounds() {
		return (new Rectangle(x,y,tileSize,tileSize));
	}

	public int[] getInitLoc() { 
		return new int[] {initX, initY}; 
	}

	public int getTileSize() { return tileSize; }
	public Image getImg() { return img; }

	public void setDX(int dx) { this.dx = dx; }
	public void setDY(int dy) { this.dy = dy; }
	public int getDX() { return dx; }
	public int getDY() { return dy; }
	public int get_X() { return x; }
	public int get_Y() { return y; }

}