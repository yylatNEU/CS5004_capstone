package model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Room {
	private int roomNumber;
	private String roomName;
	private String description;
	private Map<Direction, Integer> exits;
	private String picture;
	private List<Item> items;
	private Puzzle puzzle;
	private Monster monster;
	
	private List<Fixture> fixture;
	
	public Room(int roomNumber, String roomName, String description,
													 Map<Direction, Integer> exits, String picture){
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
		this.items = room.items;
		this.puzzle = room.puzzle;
		this.monster = room.monster;
	}
	public Room(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	public Room(Monster monster) {
		this.monster = monster;
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
	public void setExit(Direction direction, int destination) {
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
			Room destination = GameWorld.getRoom(exits.get(direction));
			return destination;
		}
		return null;
	}
	public Map<Direction, Integer> getExits() {
		return exits;
	}
	
	public void addItem(Item item) {
		if(items == null) {
			items = new ArrayList<>();
		}
		this.items.add(item);
	}
	
	public void removeItem(Item item) {
		if(items != null) {
			this.items.remove(item);
		}
	}
	public List<Item> getItems() {
		return items;
	}
	public void setMonster(Monster monster) {
		this.monster = monster;
	}
	public void setPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	public Puzzle getPuzzle() {
		return puzzle;
	}
	
	public void addFixture(Fixture fixture) {
		if(this.fixture == null) {
			this.fixture = new ArrayList<>();
		}
		this.fixture.add(fixture);
	}
	
	public Monster getMonster() {
		return this.monster;
	}
}
