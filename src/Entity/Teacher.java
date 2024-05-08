package Entity;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Teacher extends Entity implements Serializable {

    /**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Konstruktor, meghívja az ősosztály konstruktorát a kapott paraméterekkel
     * @param name - A Entity.Teacher neve
     * @param id - A Entity.Teacher saját egyedi azonosítója
     * @param currentRoom - A szoba, amelyben jelenleg tartózkodik a Tanár
     */
    public Teacher(String name, int id, Room currentRoom){
        super(name, id, currentRoom);
        GameController.getInstance().debuggableObjects.put(id, this);
        image = new ImageIcon("rsc/teacher.png").getImage();
    }

    /**
     * Item.SlideRule tárgyfelvétel logika Teachernek. Nem veheti fel, úgyhogy csak válaszol False-al
     * @param slideRule - A tárgy amit fel szeretne venni a Entity.Teacher
     * @return - False, mivel nem veheti fel
     */
    @Override
    public boolean pickUpItem(SlideRule slideRule) {
        return false;
    }

    /**
     * Item.Transistor tárgyfelvétel logika Teachernek. Nem veheti fel, úgyhogy csak válaszol False-al
     * @param transistor - A tárgy amit fel akar venni.
     * @return - False, mivel nem veheti fel
     */
    @Override
    public boolean pickUpItem(Transistor transistor) {
        return false;
    }

    /**
     * Egy tanár diákkal való találkozását kezeli Ha a tanár éppen nem rendelkezik
     * semmilyen (gázzal telített szoba vagy lehelyezett rongy általi) bénító hatással saját referenciájával
     * meghívja a paraméterként átadott diák meet() metódusát. Ellenkező esetben művelet nélkül visszatér.
     * @param student A diák akivel a tanár találkozik.
     */
    @Override
    public void meet(Student student) {
    	if(remainingStun == 0) student.meet(this);
    }

    /**
     * Egy tanár és egy másik  Entity.Entity találkozását kezelő függvény. Célja, hogy
     * a találkozásnak megfelelő függvény hívódjon az adott entity-ből származtatott osztályban. (Visitor)
     * Ehhez a paraméterül kapott entity-re meghívja a saját maga referenciájával a meet 
     * metódust.
     * @param entity Az entity referenciája akivel a találkozás történik.
     */
	 public void meetMe(Entity entity) {
		 if(remainingStun > 0)
			 return;
		entity.meet(this);
	 }

	/**
     * Az Entity.Entity ősosztályban található metódus felüldefiniálása.
     * Kezeli a fordulón belül a tanár találkozását a szobában található többi Entityvel, úgy, hogy meghívja a jelenlegi szobájának meetAll() függvényét.
     * Visszatérési értéke mindig igaz, mivel a tanár nem tudja a lelkét elveszteni.
     * @return A Entity.Teacher kör végén való életben maradásának logika értéke
     */
    @Override
    public boolean newEntRound() {
    	GameController gC = GameController.getInstance();
    	
    	//Ha a játéknak vége, nem történhet semmi
    	if(gC.gameIsOver) return false;

    	roundIsOver = false;
    	gC.currentEntity = this;
    	
    	//Ha a játék teszt módban van a tanárokkat is lehet irányítani.
    	if(gC.testmode) {
    		String playerInputString = null;
        	
        	GameController.getInstance().println("You're currently playing as Entity.Teacher: " + name + " in room : " + currentRoom.getID());
        	
        	do {
        		if(!isAlive) {
        			GameController.getInstance().println("Your character does not have a soul anymore.\nPress enter to continue:");
        			if(gC.manualInputMode) gC.scanner.nextLine();
        			roundIsOver = true;
        		}else if(remainingStun > 0) {
        			GameController.getInstance().println("Your character is currently stunned. Remaining stunned rounds: " + remainingStun +"\nPress enter to continue:");
        			if(gC.manualInputMode) gC.scanner.nextLine();
        			roundIsOver = true;
        		}else {
            		GameController.getInstance().println(name + ">");
            		if(gC.manualInputMode) {
            			playerInputString = gC.scanner.nextLine();
                		gC.terminal(playerInputString);
            		}else gC.terminal(gC.nextTestLine());
            		
        		}
        	}while(!roundIsOver&& !gC.gameIsOver);
    	}else {
    		int pickupTry =  gC.random.nextInt(3);
        	int moveTry =  gC.random.nextInt(3);
        	
        	if(remainingStun <= 0) {
        		//Random próbálkozik a felvétellel
        		for(int i = 0; i < pickupTry ; i++) {
        			Item itemToPickUp = currentRoom.giveAnItem();
        			if(itemToPickUp != null) itemToPickUp.pickUp(this);
        		}
        		
        		//Random megpróbál továbbmenni
        		boolean moveWasSuccesful = currentRoom.tryToMove(moveTry, this, currentRoom);
        		
        		//Ha nem sikerült semelyik ajtón átmenni maradunk itt.
        		if(!moveWasSuccesful) currentRoom.acceptEntity(this);
        	}
    	}
    	
    	
    	
    	if(remainingStun != 0) remainingStun--;
    	
    	return true;
    }

    /**
     * Egy tanár és egy ledobott aktív rongy interakcióját kezeli.
     * Ekkor a tanár eszmélet vesztési ideje egy körrel nő.
     */
    @Override
    public void encounterRag() {
    	remainingStun += 1;
    }

	/**
	 * Vissza adja tanár nevét
	 * @return string
	 */
    public String toString(){
        return "Entity.Teacher: " + getName();
    }

	/**
	 * Random nevet generál a benne statikusan tárolt vezeték és kereszt nevek ötvözésével
	 * @return - random generált név
	 */
	public String randomName(){
		String[] lastNames = {"Gajdos" , "Szirmay" , "Goldschmidt"};
		String[] firstNames = {"Britney Spears", "Csanád", "Luigi", "Yuki", "Jordan"};

		return lastNames[GameController.getInstance().random.nextInt(lastNames.length - 1)] + " " +
				firstNames[GameController.getInstance().random.nextInt(firstNames.length - 1)];
	}
}
