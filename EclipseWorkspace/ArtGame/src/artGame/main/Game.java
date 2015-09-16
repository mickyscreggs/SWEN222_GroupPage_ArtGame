package artGame.main;

import java.util.Scanner;
import java.util.Set;

import artGame.game.*;
import artGame.game.Character.Direction;

public class Game {


	private Floor floor;

	private Player p;
	
	public void initialise(){
		floor = new Floor();
		p = new Player(Direction.EAST,1);
		floor.addCharacter(p, 2, 2);
	}
	
	/**
	 * prints menu options
	 */
	public void printMenu(){
		Set<Item> playerInv = p.getInventory();
		System.out.println("Inventory: ");
		for(Item i:playerInv) {
			System.out.println(i);
		}
		System.out.println("what to do??:");
		System.out.println("W: move up");
		System.out.println("A: move left");
		System.out.println("S: move down");
		System.out.println("D: move right");
		System.out.println("F: interact");
	}
	
	/**
	 * executes a action for the player
	 */
	public void doAction(Player p, char id){
		int currentRow = p.getRow();
		int currentCol = p.getCol();
		if(id=='w'){
			p.setDir(Direction.NORTH);
			floor.moveCharacter(p, currentRow, currentCol, currentRow-1, currentCol);
		}
		else if(id=='a'){
			p.setDir(Direction.WEST);
			floor.moveCharacter(p, currentRow, currentCol, currentRow, currentCol-1);
		}
		else if(id=='s'){
			p.setDir(Direction.SOUTH);
			floor.moveCharacter(p, currentRow, currentCol, currentRow+1, currentCol);
		}		
		else if(id=='d'){
			p.setDir(Direction.EAST);
			floor.moveCharacter(p, currentRow, currentCol, currentRow, currentCol+1);
		}		
		else if(id=='f'){
			floor.interact(p);
		}		
		else{
			
		}
	}
	
	public Floor getFloor() {
		return floor;
	}

	public Player getPlayer() {
		return p;
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.initialise();
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while(game.getFloor().isOnExit()==null){
			if(game.getFloor().checkGuards()){
				game.getPlayer().gotCaught();
				break;
			}
			game.floor.printFloor();//replace with gui display
			game.printMenu();
			String s = sc.next();
			game.doAction(game.getPlayer(),s.charAt(0)); //replace with keylistener
		}
		if(game.getPlayer().isCaught()){
			System.out.println("you got arrested");
		}
		else{
			System.out.println("you ran off");
			int score = 0;
			for(Item i:game.getPlayer().getInventory()){
				if(i instanceof Art){
					score = score + ((Art)i).value;
				}
			}
			System.out.println("you made off with $"+score+" worth of art");
		}
	}	
}