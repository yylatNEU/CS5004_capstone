package model;


import java.util.Map;

public class Room {
	public enum Direction {
		NORTH, SOUTH, EAST, WEST
	}
	private int roomNumber;
	private String roomName;
	private String description;
	private Map<Direction, Integer> exits;
	private String picture;
	private Item item;
	private Puzzle puzzle;
	private Monster monster;
	
	private Fixture fixture;
	
	public Room(roomNumber, roomName, description,
													 exits, picture){
		this.roomNumber = roomNumber;
		this.roomName = roomName;
		this.description = description;
		this.exits = exits;
		this.picture = picture;
	}
	public Room(Room room) {
		this.roomNumber = room.roomNumber;
		this.roomName = room.roomName;
		this.description = room.description;
		this.exits = room.exits;
		this.picture = room.picture;
		this.item = room.item;
		this.puzzle = room.puzzle;
		this.monster = room.monster;
	}
	public Room(Item item) {
		this.item = item;
	}
	public Room(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	public Room(Monster monster) {
		this.monster = monster;
	}
	public Room(Fixture fixture) {
		this.fixture = fixture;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public String getRoomName() {
		return roomName;
	}
	public String getDescription() {
		return description;
	}
	public void setExits(Direction direction, int destination) {
		exits.put(direction, destination);
	}
	public int getExit(Direction direction) {
		return exits.get(direction);
	}
	public boolean passable(Direction direction) {
		if(exits.get(direction) > 0){
				return true;
		}
		return false;
	}
	public Room transition(Direction direction) {
		if(passable(direction)) {
			return GameWorld.getInstance().getRoom(exits.get(direction));
		}
		return null;
	}
}
