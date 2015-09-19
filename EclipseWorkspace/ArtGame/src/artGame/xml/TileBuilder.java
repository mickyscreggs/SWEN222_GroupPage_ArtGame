package artGame.xml;

import artGame.game.Coordinate;
import artGame.game.EmptyTile;
import artGame.game.Tile;

public class TileBuilder implements ObjectBuilder {
	
	private boolean northWall = false;
	private boolean westWall = false;
	private boolean southWall = false;
	private boolean eastWall = false;
	private Coordinate coord;
	
	@Override
	public void addFeild(String name, String value) {
		if(name.equals(XMLReader.WALL_ELEMENT)){
			setWall(value);
		}
		
	}

	private void setWall(String value) {
		if(value.equals(XMLReader.NORTH_VALUE)){
			this.northWall = true;
		} else if(value.equals(XMLReader.WEST_VALUE)){
			this.westWall = true;
		} else if(value.equals(XMLReader.SOUTH_VALUE)){
			this.southWall = true;
		} else if(value.equals(XMLReader.EAST_VALUE)){
			this.eastWall = true;
		}
		
	}

	@Override
	public Tile buildObject() {
		return new EmptyTile(northWall, westWall, southWall, eastWall);
	}
	
	public Coordinate getCoordinate(){
		return this.coord;
	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		if(name.equals(XMLReader.POSITION_ELEMENT)){
			if(value instanceof Coordinate){
				this.coord = (Coordinate) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building tile: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(), 
						Coordinate.class.getName()));
			}
		}
		
	}
	
	
	
}
