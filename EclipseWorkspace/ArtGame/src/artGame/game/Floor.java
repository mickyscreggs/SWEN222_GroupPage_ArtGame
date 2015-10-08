package artGame.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import artGame.game.Character.Direction;

/**
 * game floor TODO proper docs
 *
 */
public class Floor {
	private int maxR = 3;
	private int maxC = 7;

	private int offset = 0; // used for calculating coordinate offsets
	private Tile[][] floor;
	private List<ExitTile> exits;
	private List<Guard> guards;

	private int itemIDC; // counter for item IDs

	public Floor(Tile[][] tiles, int maxR, int maxC, Collection<ExitTile> exits) {
		floor = tiles;
		this.exits = new ArrayList<ExitTile>(exits);
		this.guards = new ArrayList<Guard>();
		this.maxR = maxR;
		this.maxC = maxC;
		this.itemIDC = 0;
	}


	/**
	 * upgraded version of constructor, takes a variable number of floors
	 * and automatically determines maximum row and column numbers
	 */
	public Floor(Collection<ExitTile> exits,Tile[][]... floors) {
		//calculating offset
		int localC = 0; //local number of cols of a floor
		int localR = 0; //local number of rows
		for(Tile[][] tiles:floors){
			if(tiles.length>localR) localR=tiles.length;
			if(tiles[0].length>localC) localC=tiles[0].length;
		}
		offset = localC*2; //offset is double the maximum width of a floor
		//moving tiles of non 0 floors to correct location
		this.maxC = localC*(floors.length*2 - 1);
		floor = new Tile[localR][maxC];
		for(int i=0;i<floors.length;i++){
			Tile[][] currentFloor = floors[i];
			for(int r=0;r<currentFloor.length;r++){
				for(int c=0;c<currentFloor[0].length;c++){
					floor[r][c+offset*i] = currentFloor[r][c];
				}
			}
		}
		this.exits = new ArrayList<ExitTile>(exits);
		this.guards = new ArrayList<Guard>();
		this.maxR = localR;
		this.itemIDC = 0;
	}

	/**
	 * gets the next availible item id, incrementing it in the process
	 *
	 * @return
	 */
	public int nextItemID() {
		return itemIDC++;
	}

	/*
	 * generating the floor for demo version of game
	 */
	public Floor() {
		// initial sweep
		guards = new ArrayList<Guard>();
		floor = new Tile[maxR][maxC];
		for (int i = 0; i < maxC; i++) {
			floor[0][i] = new EmptyTile(true, false, false, false);
			floor[1][i] = new EmptyTile(false, false, false, false);
			floor[2][i] = new EmptyTile(false, false, true, false);
		}

		// cleanup
		// setting west walls
		floor[0][0].setWall(Direction.WEST, new Wall(new Art("Art1", 1000,
				itemIDC++)));
		floor[1][0] = new Chest(0, false, true, false, false);
		((Chest) floor[1][0]).setContent(new Key(1, itemIDC++));
		floor[1][0].setWall(Direction.WEST, new Wall());
		floor[2][0].setWall(Direction.WEST, new Wall(new Art("Art2", 4000,
				itemIDC++)));
		// east walls
		floor[0][5].setWall(Direction.EAST, new Wall(new Art("Art3", 9000,
				itemIDC++)));
		floor[2][5].setWall(Direction.EAST, new Wall());
		// mid walls
		floor[0][3].setWall(Direction.WEST, new Wall());
		floor[0][2].setWall(Direction.EAST, new Wall());
		floor[2][3].setWall(Direction.WEST, new Wall());
		floor[2][2].setWall(Direction.EAST, new Wall());
		// setting door
		Door door = new Door(true, 1);
		floor[1][2].setWall(Direction.EAST, door);
		floor[1][3].setWall(Direction.WEST, door);
		// setting exit
		floor[1][6] = new ExitTile(true, false, true, true);
		exits = new ArrayList<ExitTile>();
		exits.add((ExitTile) floor[1][6]);
		// setting guard
		Guard guard = new Guard(Character.Direction.WEST, 0);
		setCharacter(guard, 2, 5);
	}


	/**
	 * returns tile at target coordinate
	 */
	public Tile getTile(int row, int col) {
		return floor[row][col];
	}

	/**
	 * returns tile at target coordinate of a specified floor
	 */
	public Tile getTile(int row, int col,int floorNumber) {
		return floor[row][col+offset*floorNumber];
	}

	/**
	 * links the two tiles specified 
	 * throws a error if either tile is not a stair tile
	 */
	public void linkStairs(int row1, int col1, int floor1, int row2, int col2, int floor2){
		if(!(floor[row1][col1+offset*floor1] instanceof StairTile)){
			throw new GameError("First position not stair, link fail");
		}
		if(!(floor[row2][col2+offset*floor2] instanceof StairTile)){
			throw new GameError("Second position not stair, link fail");
		}
		StairTile st1 = (StairTile)floor[row1][col1+offset*floor1];
		StairTile st2 = (StairTile)floor[row2][col2+offset*floor2];
		st1.setLinkedTile(st2);
		st2.setLinkedTile(st1);
	}
	/**
	 * sets the character c to position row, col without regard to legality of
	 * move or face direction. useful for initialising positions
	 */
	public void setCharacter(Character c, int row, int col) {
		floor[row][col].setOccupant(c);
		c.setRow(row);
		c.setCol(col);
		if(c instanceof Guard && !guards.contains((Guard)c)){
			guards.add((Guard)c);
		}
	}

	/**
	 * sets the character c to position row, col of a given floor without regard
	 * to legality of move or face direction. useful for initialising positions
	 */
	public void setCharacter(Character c, int row, int col,int floorNumber) {
		floor[row][col+offset*floorNumber].setOccupant(c);
		c.setRow(row);
		c.setCol(col);
		if(c instanceof Guard && !guards.contains((Guard)c)){
			((Guard)c).offsetPath(offset*floorNumber);
			guards.add((Guard)c);
		}
	}

	/**
	 * checks if any characters are on exit tiles
	 */
	public Character isOnExit() {
		for (ExitTile exit : exits) {
			if (exit.getOccupant() != null) {
				return exit.getOccupant();
			}
		}
		return null;
	}

	/**
	 * prints the floor
	 */
	public void printFloor() {
		for (int i = 0; i < maxR; i++) {
			for (int j = 0; j < maxC; j++) {
				Tile toPrint = floor[i][j];
				if (toPrint == null) {
					System.out.print(" ");
				} else {
					System.out.print(toPrint);
				}
			}
			System.out.println("");
		}
	}

	/**
	 * moves a character 1 tile in the direction theyre facing(does nothing if
	 * invalid move)
	 */
	public void moveCharacter(Character c) {
		int oldRow = c.getRow();
		int oldCol = c.getCol();
		// can walk there
		if (tileCharacterFacing(c) != null && tileCharacterFacing(c).walkable()) {
			// no walls blocking in direction char is facing
			if (floor[c.getRow()][c.getCol()].getWall(c.getDir()) == null
					|| floor[c.getRow()][c.getCol()].getWall(c.getDir())
							.passable()) {
				// valid move
				if (c.getDir() == Direction.NORTH) {
					setCharacter(c, oldRow - 1, oldCol);
					floor[oldRow][oldCol].setOccupant(null);
				} else if (c.getDir() == Direction.WEST) {
					setCharacter(c, oldRow, oldCol - 1);
					floor[oldRow][oldCol].setOccupant(null);
				} else if (c.getDir() == Direction.SOUTH) {
					setCharacter(c, oldRow + 1, oldCol);
					floor[oldRow][oldCol].setOccupant(null);
				} else if (c.getDir() == Direction.EAST) {
					setCharacter(c, oldRow, oldCol + 1);
					floor[oldRow][oldCol].setOccupant(null);
				}
			}
		}
	}

	/**
	 * moves a character to a targetted square and faces them the right way.
	 * throws a error if invalid move
	 */
	public void moveCharacter(Character c, int row, int col) {
		int colDiff = c.getCol() - col;
		int rowDiff = c.getRow() - row;
		//invalid move if one of them not 0 and trying to move >1 in any direction
		if (colDiff * rowDiff != 0 && (Math.abs(colDiff)>1 || Math.abs(rowDiff)>1))
			throw new GameError("trying to move more than 1 square at once");
		if(colDiff==1){
			c.setDir(Direction.WEST);
			this.moveCharacter(c);
		}
		else if(colDiff==-1){
			c.setDir(Direction.EAST);
			this.moveCharacter(c);
		}
		else if(rowDiff==1){
			c.setDir(Direction.NORTH);
			this.moveCharacter(c);
		}
		else if(rowDiff==-1){
			c.setDir(Direction.SOUTH);
			this.moveCharacter(c);
		}
		//base case character not moving, direction update not required
	}

	/**
	 * updates the positions of all guards as specified by their path
	 * TODO for server version, potentially put this on a seperate thread
	 * TODO similar to pacman handling ghosts?
	 */
	public void moveGuards() {
		for (Guard g : guards) {
			Coordinate nextCoord = g.nextCoord();
			moveCharacter(g,nextCoord.getY(),nextCoord.getX());
		}
//		for(int i=0;i<guards.size();i++){
//			Coordinate nextCoord = guards.get(i).nextCoord();
//			moveCharacter(guards.get(i),nextCoord.getY(),nextCoord.getX());
//		}
	}

	/**
	 * returns the tile that the given character is currently facing
	 */
	public Tile tileCharacterFacing(Character c) {
		if (c.getDir() == Direction.NORTH) {
			if (validLocation(c.getRow() - 1, c.getCol()))
				return floor[c.getRow() - 1][c.getCol()];
			else
				return null;
		} else if (c.getDir() == Direction.WEST) {
			if (validLocation(c.getRow(), c.getCol() - 1))
				return floor[c.getRow()][c.getCol() - 1];
			else
				return null;
		} else if (c.getDir() == Direction.SOUTH) {
			if (validLocation(c.getRow() + 1, c.getCol()))
				return floor[c.getRow() + 1][c.getCol()];
			else
				return null;
		} else if (c.getDir() == Direction.EAST) {
			if (validLocation(c.getRow(), c.getCol() + 1))
				return floor[c.getRow()][c.getCol() + 1];
			else
				return null;
		} else
			return null;// shouldnt reach this
	}

	/**
	 * helper method to check if target coord is valid
	 */
	private boolean validLocation(int row, int col) {
		if (row >= maxR || row < 0)
			return false;
		if (col >= maxC || col < 0)
			return false;
		return true;
	}

	public Wall wallCharacterFacing(Character c) {
		if (c.getDir() == Direction.NORTH) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else if (c.getDir() == Direction.WEST) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else if (c.getDir() == Direction.SOUTH) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else if (c.getDir() == Direction.EAST) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else
			return null;// shouldnt reach this
	}

	/**
	 * has the player interact with whatever is in front of or on their tile
	 * priority is art>chest>door
	 */
	public void interact(Player p) {
		// checking if wall
		Wall wall = wallCharacterFacing(p);
		if (wall != null) {
			if (wall instanceof Door) {
				((Door) wall).unlock(p);
			} else if (wall.getArt() != null) {
				p.addItem(wall.getArt());
				wall.setArt(null);
			}
		}
		// dealing with chests
		else if (tileCharacterFacing(p) instanceof Chest) {
			Chest chest = (Chest) tileCharacterFacing(p);
			chest.takeItem(p);
		}
		// stealing sculptures
		else if (tileCharacterFacing(p).getOccupant() instanceof Sculpture) {
			Art stolenSculpture = ((Sculpture) tileCharacterFacing(p)
					.getOccupant()).toItem(this);
			p.addItem(stolenSculpture);
			tileCharacterFacing(p).setOccupant(null);
		}
		//guards
		else if (tileCharacterFacing(p).getOccupant() instanceof Guard){
			Guard g = ( Guard)tileCharacterFacing(p).getOccupant();
			for(Item i:g.getInventory()){
				p.addItem(i);
			}
			g.clearInventory();
		}
		// otherwise no action should be taken
	}

	/**
	 * checks if a specific guard can see a player. if not, returns a null
	 */
	private Player checkGuard(Guard g) {
		Direction dir = g.getDir();
		int cOff = 0; // col offset
		int rOff = 0; // row offset
		// first determine offsets
		if (dir == Direction.NORTH) {
			rOff = -1;
		} else if (dir == Direction.WEST) {
			cOff = -1;
		} else if (dir == Direction.SOUTH) {
			rOff = 1;
		} else if (dir == Direction.EAST) {
			cOff = 1;
		}
		// cycle through offset tiles and check for players
		// using distance = 3, straight line
		for (int i = 1; i < 4; i++) {
			// target coords. works because only one of
			// row or col offset will be nonzero
			int tarRow = g.getRow() + i * rOff;
			int tarCol = g.getCol() + i * cOff;
			if (floor[tarRow][tarCol].getOccupant() instanceof Player) {
				return (Player) floor[tarRow][tarCol].getOccupant();
			}
			if (floor[tarRow][tarCol].getWall(dir) != null) {
				break;
			}
		}
		return null;
	}

	/**
	 * returns set of caught players
	 */
	public Set<Player> checkGuards() {
		Set<Player> caught = new HashSet<Player>();
		for (Guard g : guards) {
			// right now only checks for a single player
			// will be extended to collection in final version
			Player victim = checkGuard(g);
			if (victim != null) {
				victim.gotCaught();
				caught.add(victim);
			} //
		}
		return caught;
	}

	/**
	 * has the player p inspect whatever is in front of them
	 */
	public void inspect(Player p) {
		// checking if wall
		Wall wall = wallCharacterFacing(p);
		if (wall != null) {
			if (wall instanceof Door) {
				System.out.println(((Door) wall).getDescription());
			} else if (wall.getArt() != null) {
				System.out.println(wall.getArt().getDescription());
			}
		}
		// dealing with chests
		else if (tileCharacterFacing(p) instanceof Chest) {
			Chest chest = (Chest) tileCharacterFacing(p);
			System.out.println(chest.getDescription());
		}
		// sculpture
		else if (tileCharacterFacing(p).getOccupant() instanceof Sculpture) {
			Sculpture sculpture = ((Sculpture) tileCharacterFacing(p)
					.getOccupant());
			System.out.println(sculpture.getDescription());
		}
		// otherwise no action should be taken

	}

	public int getHeight() {
		return floor.length;
	}

	public int getWidth() {
		return floor[0].length;
	}

	public List<Guard> getGuards(){
		return guards;
	}

}
