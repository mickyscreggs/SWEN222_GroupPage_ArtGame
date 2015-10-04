package artGame.xml.load;

import java.util.ArrayList;
import java.util.HashMap;

import artGame.game.Character;
import artGame.game.Character.Direction;
import artGame.game.Art;
import artGame.game.Coordinate;
import artGame.game.Door;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.Player;
import artGame.game.StairTile;
import artGame.game.Tile;
import artGame.main.Game;

public class GameMaker {
	
	private HashMap<Coordinate, Tile> tiles = new HashMap<Coordinate, Tile>();
	private ArrayList<ExitTile> exits = new ArrayList<ExitTile>();
	private Tile[][] tileArray;
	private ArrayList<LinkedTileReference> stairLinks = new ArrayList<LinkedTileReference>();
	private HashMap<Coordinate, HashMap<Direction, Integer>> doorMap 
		= new HashMap<Coordinate, HashMap<Direction, Integer>>();
	private HashMap<Coordinate, HashMap<Direction, Integer>> artMap 
		= new HashMap<Coordinate, HashMap<Direction, Integer>>();
	private HashMap<Integer, Door> doors = new HashMap<Integer, Door>();
	private HashMap<Integer, Art> paintings = new HashMap<Integer, Art>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Character> nPCs = new ArrayList<Character>();
	private int maxCol = 0;
	private int maxRow = 0;

	public void addTile(Coordinate coord, Tile tile) {
		tiles.put(coord, tile);
		if(coord.getX() > maxCol) {maxCol = coord.getX();}
		if(coord.getY() > maxRow) {maxRow = coord.getY();}
		if(tile instanceof ExitTile){
			exits.add((ExitTile) tile);
		}
	}

	public void addDoorMap(Coordinate coord,
			HashMap<Direction, Integer> doorReference) {
		doorMap.put(coord, doorReference);
		
	}

	public void addArtMap(Coordinate coord,
			HashMap<Direction, Integer> artReference) {
		artMap.put(coord, artReference);		
	}
	
	public void addDoor(String[] doorInfo){
		int keyId = -1;
		boolean locked = Boolean.parseBoolean(doorInfo[2]);
		if(doorInfo.length > 3){
			keyId = Integer.parseInt(doorInfo[3]);
		}
		int doorId = Integer.parseInt(doorInfo[0]);
		doors.put(doorId, new Door(locked, keyId));
	}

	public void addLinkedTileReference(StairTile tile, int linkedLevel,
			Coordinate linkedCoord) {
		stairLinks.add(new LinkedTileReference(tile, linkedLevel, linkedCoord));
		
	}
	
	private class LinkedTileReference{
		private StairTile stairTile;
		private int linkedLevel;
		private Coordinate linkedCoordinate;
		
		public LinkedTileReference(StairTile stairTile, int linkedLevel,
				Coordinate linkedCoordinate) {
			super();
			this.stairTile = stairTile;
			this.linkedLevel = linkedLevel;
			this.linkedCoordinate = linkedCoordinate;
		}

		/**
		 * @return the stairTile
		 */
		public StairTile getStairTile() {
			return stairTile;
		}

		/**
		 * @return the linkedLevel
		 */
		public int getLinkedLevel() {
			return linkedLevel;
		}

		/**
		 * @return the linkedCoordinate
		 */
		public Coordinate getLinkedCoordinate() {
			return linkedCoordinate;
		}
	}

	public void addPlayer(Player toAdd) {
		players.add(toAdd);	
	}

	public void addPainting(Art art) {
		paintings.put(art.ID, art);
	}
	
	public void addNPC(Character nPC){
		nPCs.add(nPC);
	}

	public Game makeGame() {
		buildTileArray();
		addArtToTiles();
		addDoorsToTiles();
		Floor floor = new Floor(tileArray, tileArray.length, tileArray[0].length, exits);
		addNPCsToFloor(floor);
		return new Game(floor, players);
	}
	
	private void addNPCsToFloor(Floor floor) {
		for(Character nPC : nPCs){
			floor.setCharacter(nPC, nPC.getRow(), nPC.getCol());
		}
	}

	private void addDoorsToTiles() {
		for(Coordinate coord : doorMap.keySet()){
			HashMap<Direction, Integer> tileDoorMap = doorMap.get(coord);
			Tile tile = tiles.get(coord);
			for(Direction dir : tileDoorMap.keySet()){
				tile.setWall(dir, doors.get(tileDoorMap.get(dir)));
			}
		}
	}

	private void addArtToTiles() {
		for(Coordinate coord : artMap.keySet()){
			HashMap<Direction, Integer> tileArtMap = artMap.get(coord);
			Tile tile = tiles.get(coord);
			for(Direction dir : tileArtMap.keySet()){
				tile.getWall(dir).setArt(paintings.get(tileArtMap.get(dir)));
			}
		}
	}

	/**
	 * Creates a 2d array of tiles from the given height and width and populates it from
	 * the map of coordinates to tiles, using the coordinate keys to find the array positions
	 * for each of the tiles.
	 */
	private void buildTileArray() {
		tileArray = new Tile[maxRow+1][maxCol+1];
		for(Coordinate coord : tiles.keySet()){
			tileArray[coord.getY()][coord.getX()] = tiles.get(coord);
		}
	}
}
