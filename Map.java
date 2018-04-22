import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.*;

public class Map {
	
	private int tileSize = 45;
	public static final int WIDTH = 16;
	public static final int HEIGHT = 23;
	private static final int trash = -100;

	private Obstacle[][] obstacle = new Obstacle[HEIGHT][WIDTH];
	private Wall[][] wall = new Wall[HEIGHT][WIDTH];
	private Road[][] road = new Road[HEIGHT][WIDTH];
	private String tiles[] = new String[WIDTH];

	private String obstacleImgDirectory = "Images/MapTiles/";
	private String wallImgDirectory = "Images/MapTiles/";
	private String roadImgDirectory = "Images/MapTiles/";

	public ArrayList<Integer> obstacleCoorX = new ArrayList<Integer>();
	public ArrayList<Integer> obstacleCoorY = new ArrayList<Integer>();

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
				if(isRoad(a,b)) {
					road[a][b] = new Road(roadImgDirectory); 
					road[a][b].setLoc(a*tileSize, b*tileSize);
				} else {
					road[a][b] = new Road("");
					road[a][b].setLoc(trash,trash);
				}
				
				if(isWall(a,b)) {
					wall[a][b] = new Wall(wallImgDirectory); 
					wall[a][b].setLoc(a*tileSize, b*tileSize);
				} else {
					wall[a][b] = new Wall("");
					wall[a][b].setLoc(trash,trash);
				}
				
				if(isObstacle(a,b)) {
					obstacle[a][b] = new Obstacle(obstacleImgDirectory, roadImgDirectory);
					obstacle[a][b].setLoc(a*tileSize, b*tileSize);
					obstacleCoorX.add(a);
					obstacleCoorY.add(b);
				} else {
					obstacle[a][b] = new Obstacle("", "");
					obstacle[a][b].setLoc(trash,trash);
				}
			}
		}
	}	

	public boolean isRoad(int x, int y) { 
		return tiles[y].substring(x,x+1).equals("|"); 
	}

	public boolean isWall(int x, int y) {
		return tiles[y].substring(x,x+1).equals("0");
	}

	public boolean isObstacle(int x, int y) {
		return tiles[y].substring(x,x+1).equals("A");
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
	
	public int getTileSize() { 
		return tileSize; 
	}

	public Wall getWallAt(int x, int y) { return wall[x][y]; }
	public Road getRoadAt(int x, int y) { return road[x][y]; }
	public Obstacle getObstacleAt(int x, int y) { return obstacle[x][y]; }

	public int[][] getObstaclePositions() {
		int[][] positions = new int[obstacleCoorX.size()][2];
		int counter = 0;

		for(Integer x : obstacleCoorX) {
			positions[counter][0] = x;
			counter++;
		}
		counter = 0;

		for(Integer y : obstacleCoorY) {
			positions[counter][1] = y;
			counter++;
		}
		/*
		for(int x = 0; x < obstacleCoorX.size(); x++) {
			for(int y = 0; y < 2; y++) {
				System.out.print(" " + positions[x][y] + ":");
			}
		}
		*/
		return positions;
	}

}