package artGame.xml.load;

import artGame.game.Coordinate;
import artGame.game.Character.Direction;
import artGame.xml.XMLHandler;

public abstract class CharacterBuilder implements BuildStrategy {
	
	private Direction d;
	private Coordinate coord;
	private int iD;
	private GameMaker gameMaker;

	public CharacterBuilder(GameMaker gameMaker, int id){
		this.gameMaker = gameMaker;
		this.iD = id;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		//TODO:Inventory handling
		if(name.equals(XMLHandler.DIRECTION_ELEMENT)){
			addDirection((String) values[0]);
		} else if(name.equals(XMLHandler.ID_ATTRIBUTE)){
			iD = Integer.parseInt((String) values[0]);
		}
		if(name.equals(XMLHandler.POSITION_ELEMENT)){
			if(values[0] instanceof Coordinate){
				this.coord = (Coordinate) values[0];
			} else {
				throw new IllegalArgumentException(String.format("Error when building character: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						Coordinate.class.getName()));
			}
		}
	}
	
	/**
	 * Sets the direction field to the value represented by the argument string
	 * @param value String representation of direction value
	 */
	private void addDirection(String value) {
		if(value.equals(XMLHandler.NORTH_VALUE)){
			d = Direction.NORTH;
		} else if(value.equals(XMLHandler.WEST_VALUE)){
			d = Direction.WEST;
		} else if(value.equals(XMLHandler.SOUTH_VALUE)){
			d = Direction.SOUTH;
		} else if(value.equals(XMLHandler.EAST_VALUE)){
			d = Direction.EAST;
		}
	}

	@Override
	public abstract void addToGame();

	/**
	 * @return the d
	 */
	public Direction getDirection() {
		return d;
	}

	/**
	 * @return the coord
	 */
	public Coordinate getCoord() {
		return coord;
	}

	/**
	 * @return the iD
	 */
	public int getiD() {
		return iD;
	}

	/**
	 * @return the gameMaker
	 */
	public GameMaker getGameMaker() {
		return gameMaker;
	} 
	
	

}
