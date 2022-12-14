/*
 * Author: Neumann Davila
 * Date:   Oct 7, 2022
 * Description:
 * Events store and run choices as well as display them to the user in order to allow for interaction
 *
 *
 * Events Store multiple things necessary for the program
 * 		- Event description
 * 		- Choices custom to the event
 * 		- Default Events that can run in every event conditionally
 * 		- NPC's within the event and the interactions that are possible with that npc
 * 			- This is built in as it is easier to return to this event (may change)
 * 
 * 
 */

package finalProject.EventStructure;

import java.util.ArrayList;
import java.util.Scanner;

import finalProject.Items.Item;
import finalProject.TextGame;
import finalProject.CharacterTypes.*;
import finalProject.CharacterTypes.Character;

public class Event {
	private Scanner input = new Scanner(System.in);
	
	private String description = "\033[0;96m";;
	private ArrayList<Choice> eventChoices = new ArrayList<Choice>();
	private ArrayList<Character> eventNPC = new ArrayList<Character>();
	private boolean isDefault = true;


	public Boolean isDefault() {
		return isDefault;
	}
	
									//	---Display Methods---	\\
	
	public void displayEvent() {
		//	automatically runs if there is only one choice in the Event
		if (eventChoices.size() == 1) {
			System.out.println(description);
			eventChoices.get(0).choiceRun();
		} else {
			//	If the event has default choices then it will run starting at 0
			if (isDefault) {
				System.out.printf(description);
				System.out.println("");
				sleep(300);

				for (int i = 0; i < eventChoices.size(); i++) {
					System.out.println("\033[0;37m" + i + ":\033[93m " + eventChoices.get(i));
					sleep(300);
				}
				try {
					int tempInt = Integer.parseInt(input.nextLine().strip());
					eventChoices.get(tempInt).choiceRun();
				} catch (Exception e) {
					System.out.println(e);
					System.out.println("Invalid Input: Event");
					displayEvent();
				}
			}
			//	Choices will be run stating at 1 for ease of use
			//	only if default choices are not being used
			else {
				System.out.println(description);
				sleep(300);

				for (int i = 1; i < eventChoices.size() + 1; i++) {
					System.out.println("\033[0;37m" + i + ":\033[93m " + eventChoices.get(i - 1));
					sleep(300);
				}

				getDecision();
			}
		}
	}
	
								//	---NPC Methods---	\\
	
	public void addNPC(NPC npc) {
		addChoice(new Choice("Interact with " + npc, () -> {NPCEvent(npc);displayEvent();}));
		eventNPC.add(npc);
	}
	
	public void removeNPC(NPC npc) {
		eventNPC.remove(npc);

		for(int i = 0; i < eventChoices.size(); i++) {
			if(("Interact with " + npc).equals("" + eventChoices.get(i))) {
				eventChoices.remove(i);
				break;
			}
		}
	}
	
	/*
	 * 									---Sub Events---
	 * 
	 * These are sub events built into every Event so that I can call and return to my old event
	 * 
	 * If I created a seperate event then I would need to keep track of the current event so that I can return to the event
	 * 
	 */
	
		//	Events that are created when an NPC is interacted with
	public void NPCEvent(NPC npc) {
		ArrayList<Choice> NPCChoices = new ArrayList<Choice>();
		Stat friendStat = TextGame.player.getStats().getFriendStat(npc);
		
		NPCChoices.add(new Choice("Talk to " + npc, () -> {
			System.out.println(npc.getDialogue(friendStat));
		}));

		NPCChoices.add(new Choice("Give something to " + npc, () -> {TextGame.player.giveItem(npc);}));
		NPCChoices.add(new Choice("Attack " + npc, () -> {
			new CombatEvent(TextGame.player, npc);
		}));
		NPCChoices.add(new Choice("Pickpocket " + npc, () -> {
			if(TextGame.player.getStats().rollDexterity(npc.getStats().getDexterity())) {
				TextGame.player.pickPocket(npc);
			}
			else {
				System.out.println("You were caught!");
				TextGame.player.getStats().adjustFriendStat(friendStat, -20);

			}

		}));
		NPCChoices.add(new Choice("Back", () -> {}));
		
		for (int i = 1; i < NPCChoices.size() + 1;i++ ) {
			sleep(300);
			System.out.println(i + ": " + NPCChoices.get(i - 1));
		}
		
		try {
			int tempInt = Integer.parseInt(input.nextLine().strip());
			NPCChoices.get(tempInt - 1).choiceRun();
			
			}
			catch(Exception e){
				System.out.println("Invalid input: NPC");
				NPCEvent(npc);
			}	
		}
	
									//	---Choice Methods---  \\

	public void addChoice(Choice choice) {
		this.eventChoices.add(choice);
	}

	public void removeChoice(Choice choice) {
		this.eventChoices.remove(choice);
	}

	public ArrayList<Choice> getChoices () {return this.eventChoices;}

		//	Collects and runs the decision for the event 
	public void getDecision() {
		try {
		int tempInt = Integer.parseInt(input.nextLine().strip());
		eventChoices.get(tempInt - 1).choiceRun();
		}
		catch(Exception e){
			System.out.println("Invalid input: Decision");
			displayEvent();
		}	
	}

	private void slowText(String text) {

		for(int i = 0; i < text.length(); i++) {
			if(text.substring(i, i + 1).equals(" ")) {
				System.out.print(text.charAt(i));
				continue;
			}
			else if(text.substring(i, i + 1).equals("\n")) {
				System.out.print(text.charAt(i));

				try{
					Thread.sleep(500);
				}
				catch(Exception e) {
					System.out.println(e);
				}
				continue;
			}

			System.out.print(text.charAt(i));
			try{
				Thread.sleep(40);
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		System.out.println("");
	}

	private void sleep(long milliSecs) {
		try{
			Thread.sleep(milliSecs);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
									//	---Constructors---	\\
	
	public Event() {
		this.description += "test \033[0m";
		eventChoices.add(new Choice("Show Inventory", () -> {TextGame.player.getInventory().display();displayEvent();}));
		addNPC(new NPC());
	}
	
	public Event(String description) {
		this.description += description + "\033[0m";
		eventChoices.add(new Choice("Show Inventory", () -> {TextGame.player.getInventory().display();displayEvent();}));
	}
		//	Special Constructor for events without default choice like Display Inventory
	public Event(String description, boolean containsDefaultChoices) {
		this.description += description + "\033[0m";
		this.isDefault = containsDefaultChoices;
	}

}

