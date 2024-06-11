package Entity;

import GameLogic.*;
import GameMap.*;
import Item.*;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class Janitor extends Entity implements Serializable {

    /**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Konstruktor
     * @param n - Entity.Janitor neve
     * @param nID - Entity.Janitor egyedig ID-je
     * @param curr  - A szoba amelyben az Entity.Janitor tartózkodik
     */
	public Janitor(String n, int nID, Room curr){
		super(n,nID,curr);
        GameController.getInstance().debuggableObjects.put(nID, this);
        image = new ImageIcon("./rsc/janitor.png").getImage();
	}
	
	/**
     *Kezeli a takarító pozícióváltását a szobák között ajtókon keresztül. Meghívja
     *a paraméterül adott ajtó moveEntity() függvényét, és annak visszatérési értékével tér vissza.
     * Valamint ha sikeres a szobaváltoztatás, az ezzel járó műveletek végrehajtását is kezdeményezi.
     * @param door Ajtó, amelyen keresztül az entity szobát változtat.
     * @return A szobaváltás sikerességének logikai értéke
     */
	@Override
    public boolean moveTo(Door door){
    	
    	//ha a kapott ajtó referencia null, helyben akar maradni az Entity.Entity
    	if(door == null) {
    		roundIsOver = currentRoom.acceptEntity(this);
    	}else {
    		if(remainingStun == 0) {
        		boolean ret = door.moveEntity(this, currentRoom); 
        		
        		//Ha sikerült a szobaváltás, ki is kell takarítani.
        		if(ret) currentRoom.janitorEntered();
        		
        		//Ha sikerül a szobaváltoztatás az Entity.Entity körének vége
        		roundIsOver = ret;
        	}else {
            	return false;
        	}
    	} 
    	return roundIsOver;
     }
	
	/**
     * Item.SlideRule tárgy felvételére szolgáló függvény.
     * @param slideRule - A tárgy amit fel akar venni
     * @return - True ha fel tudja venni, False hogyha nem
     */
	@Override
    public boolean pickUpItem(SlideRule slideRule){
        return false;
	}

    /**
     * Tranzisztor tárgy felvételére szolgáló tárgy
     * @param transistor - A tárgy amit fel akar venni.
     * @return - True hogyha fel tudja venni, False hogyha nem
     */
	@Override
    public boolean pickUpItem(Transistor transistor){
        return false;
	}

	/**
     * Egy takarító és egy másik  Entity.Entity találkozását kezelő függvény. Célja, hogy
     * a találkozásnak megfelelő függvény hívódjon az adott entity-ből származtatott osztályban. (Visitor)
     * Ehhez a paraméterül kapott entity-re meghívja a saját maga referenciájával a meet 
     * metódust.
     * @param entity Az entity referenciája akivel a találkozás történik.
     */
	public void meetMe(Entity entity) {
		entity.meet(this);
	}


	/**
     * Az Entity.Entity ősosztályban található metódus felüldefiniálása.
     * Kezeli a fordulón belül a takaritó találkozását a szobában található többi entityvel, úgy, hogy meghívja a jelenlegi szobájának meetAll() függvényét.
     * Ha a diák a találkozások hatására elveszti a lelkét visszatérési értéke false, true különben.
     * @return - Az Entity.Student kör végén való életben maradásának logika értéke
     */
	@Override
    public boolean newEntRound() {
		GameController gC = GameController.getInstance();
    	
    	//Ha a játéknak vége, nem történhet semmi
    	if(gC.gameIsOver) return false;

    	roundIsOver = false;
    	gC.setCurrentEntity(this);
    	
    	//Ha a játék teszt módban van a tanárokkat is lehet irányítani.
    	if(gC.testmode) {
    		String playerInputString = null;
        	
        	GameController.getInstance().println("You're currently playing as Entity.Janitor: " + name + " in room : " + currentRoom.getID());
        	
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
     * Takarító egy gázzal telített szobába lépésével járó állapotváltozásait kezeli.
     * Mivel a takarító immunis a gázra, ezért nem történik esetében semmi.
     */
	@Override
	public void toxicate(){
		//Üres, hiszen a takarító nem bénül meg a gázos szobában.
	}
	
    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
	@Override
    public String toString(){
        return "Entity.Janitor: " + getName();
    }

	/**
	 * Random nevet generál a benne statikusan tárolt vezeték és kereszt nevek ötvözésével
	 * @return - random generált név
	 */
	@Override
	public String randomName(){
		String[] lastNames = {"Kanalas", "Musk", "Lakatos", "Zuckerberg"};
		String[] firstNames = {"Klárika", "Manyi néni", "Zoli bácsi", "Coby", "TechBro"};

		return lastNames[GameController.getInstance().random.nextInt(lastNames.length - 1)] + " " +
				firstNames[GameController.getInstance().random.nextInt(firstNames.length - 1)];
	}



}