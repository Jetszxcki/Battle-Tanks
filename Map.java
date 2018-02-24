import java.io.File;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;

public class Map {
	
	private int tileSize = 45;
	public static final int WIDTH = 15;
	public static final int HEIGHT = 19;

	private Obstacle[][] obstacle = new Obstacle[HEIGHT][WIDTH];
	private Wall[][] wall = new Wall[HEIGHT][WIDTH];
	private Road[][] road = new Road[HEIGHT][WIDTH];
	private String tiles[] = new String[WIDTH];

	private String obstacleImgDirectory = "Images/MapTiles/";
	private String wallImgDirectory = "Images/MapTiles/";
	private String roadImgDirectory = "Images/MapTiles/";
	
	public Map(String filename) {
		readFile(filename);

		if(filename.equals("colosseum.tnk")) {
			obstacleImgDirectory += "colosseum/colosseum_obstacle.png";
			wallImgDirectory += "colosseum/colosseum_wall.png";
			roadImgDirectory += "colosseum/colosseum_road.png";
		} else if(filename.equals("warzone.tnk")) {
			wallImgDirectory += "desert/desert_wall.png";
			roadImgDirectory += "desert/desert_road.png";
		}

		for(int a = 0; a < HEIGHT; a++) {
			for(int b = 0; b < WIDTH; b++) {
				if(getMap(a,b).equals("|")) {
					road[a][b] = new Road( roadImgDirectory); 
					road[a][b].setBounds(a*tileSize, b*tileSize, tileSize, tileSize);
				}
				else if(getMap(a,b).equals("0")) {
					wall[a][b] = new Wall(wallImgDirectory); 
					wall[a][b].setBounds(a*tileSize, b*tileSize, tileSize, tileSize);
				}
				else if(getMap(a,b).equals("A")) {
					obstacle[a][b] = new Obstacle(obstacleImgDirectory, roadImgDirectory);
					obstacle[a][b].setBounds(a*tileSize, b*tileSize, tileSize, tileSize);
				}
			}
		}
	}	
	
	public String getMap(int x, int y) {
		String index = tiles[y].substring(x,x+1);
		return index;
	}
	
	public void readFile(String filename) {
		try{
			Scanner sc = new Scanner(new File("Maps/" + filename));
			while(sc.hasNext())	{
				for(int x = 0; x < WIDTH; x++) {
					tiles[x] = sc.next();
				}
			}
			sc.close();
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Error in loading map.", "Map Not Found", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public int getTileSize() { return tileSize; }
	public Wall getWallAt(int x, int y) { return wall[x][y]; }
	public Road getRoadAt(int x, int y) { return road[x][y]; }
	public Obstacle getObstacleAt(int x, int y) { return obstacle[x][y]; }
	//public Image getWallImg() { return (new ImageIcon(wallImgDirectory).getImage()); }
	//public Image getRoadImg() { return (new ImageIcon(roadImgDirectory).getImage()); }
	//public Image getObstacleImg() { return (new ImageIcon(obstacleImgDirectory).getImage()); }

}