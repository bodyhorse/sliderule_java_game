package Entity;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;

import javax.swing.*;
import java.io.Serializable;

public class Student extends Entity implements Serializable {

    /**
     * Konstruktor, az ősosztály konstruktorát hívja meg
     * @param name - Név
     * @param id - ID
     * @param currentRoom - Jelenlegi szoba
     */
    public Student(String name, int id, Room currentRoom){
        super(name, id, currentRoom);
        GameController.getInstance().debuggableObjects.put(id, this);
        image = new ImageIcon("logarlec/rsc/resizedStudent.png").getImage();
    }

    /**
     * Item.SlideRule tárgyfelvétel logika Entity.Student számára.
     * Hogyha fel tudja venni, meg kell hívni a GameMap.Map gameWon() függvényét. Ez a win condition, azaz a játéknak vége.
     * @param slideRule - A tárgy amit fel akar venni
     * @return - True hogyha fel tudta venni, False hogyha nem
     */
    @Override
    public boolean pickUpItem(SlideRule slideRule) {
        if(!canPickUp(slideRule.getID()))
            return false;

        Item slideRuleRemoved = currentRoom.removeItem(slideRule);
        if (slideRuleRemoved == null) return false;

        addItem((SlideRule)slideRuleRemoved);
        GameController.getInstance().gameWon();
        return true;
    }

    /**
     * Item.Transistor táárgy felvételi logikája. Hogyha egy tranisztor le van téve egy másik
     * @param transistor - A tárgy amit fel akar venni.
     * @return
     */
    @Override
    public boolean pickUpItem(Transistor transistor) {
        if(!canPickUp(transistor.getID())) return false;

        //remomve from room, add to inventory
        Item tranRemoved = currentRoom.removeItem(transistor);
        if (tranRemoved == null) return false;

        addItem((Transistor)tranRemoved);

        return true;
    }

    /**
     * Egy diák tanárral való találkozását kezeli. Mivel ebben az esetben
     * a tanár megkísérni a diák lelkét kiszívni, meghívódik a diák save()
     * függvénye mely kezeli a találkozás következményeit.
     * @param teacher Tanár akivel a diák találkozik.
     */
    @Override
    public void meet(Teacher teacher) {
    	
    	if(isAlive) save();
 
    }

    /**
     * Egy diák és egy másik  Entity.Entity találkozását kezelő függvény. Célja, hogy
     * a találkozásnak megfelelő függvény hívódjon az adott entity-ből származtatott osztályban. (Visitor)
     * Ehhez a paraméterül kapott entity-re meghívja a saját maga referenciájával a meet 
     * metódust.
     * @param entity Az entity referenciája akivel a találkozás történik.
     */
	 public void meetMe(Entity entity) {
	 	entity.meet(this);
	 }

    /**
     * A diák lelkét hivatott megmenteni. Végignézi a diák inventoryját, hogy van-e nála bármilyen
     * lélekmentő ami használható. Ehhez a játékos inventoryjában lévő összes tárgyon végig meghívja
     * azoknak a save() függvényét. Ha ezek közül valamelyik true értékkel tér vissza a diák lelke megmenekült.
     * Ha a diák egy ilyen tárggyal, vagy egyetlen tárggyal sem rendelkezik , az isAlive attribútum értéke false lesz.
     */
	 
    public void save(){
    	
    	for(Item item : inventory) {
    		if(item.save(this)) {
    			return;
    		} 
    	}
    	
    	setIsAlive(false);
    	GameController.getInstance().addALostSoul();	
    }

    /**
     * Amikor a sör megmenti a diák életét, a diákkal ez a függévny dobat el egy tárgyat
     */
    @Override
    public void saveDrop(){

    	for(int i = inventory.size() - 1; i >= 0; --i) {
    		boolean dropWasSuccesful = inventory.get(i).drop(this);
    		if(dropWasSuccesful) return;
    	}
    }

    /**
     * Az Entity.Entity ősosztályban található metódus felüldefiniálása.
     * Kezeli a fordulón belül a diák találkozását a szobában található többi entityvel, úgy, hogy meghívja a jelenlegi szobájának meetAll() függvényét.
     * Ha a diák a találkozások hatására elveszti a lelkét visszatérési értéke false, true különben.
     * @return - Az Entity.Student kör végén való életben maradásának logika értéke
     */
    @Override
    public boolean newEntRound() {
    	
    	//Ha a játéknak vége, nem történhet semmi
    	if(GameController.getInstance().gameIsOver) return false;
    	
    	roundIsOver = false;
    	GameController.getInstance().currentEntity = this;
    	String playerInputString = null;
    	GameController gC = GameController.getInstance();
    	
    	GameController.getInstance().println("You're currently playing as: " + name + " in room : " + currentRoom.getID());
    	
    	do {
    		if(!isAlive) {
    			GameController.getInstance().println("Your character does not have a soul anymore.\nPress enter to continue:");
    			if(gC.manualInputMode || !gC.testmode) gC.scanner.nextLine();
    			roundIsOver = true;
    		}else if(remainingStun > 0) {
    			GameController.getInstance().println("Your character is currently stunned. Remaining stunned rounds: " + remainingStun +"\nPress enter to continue:");
    			if(gC.manualInputMode || !gC.testmode) gC.scanner.nextLine();
    			roundIsOver = true;
    		}else {
        		GameController.getInstance().println(name + ">");
        		if(gC.testmode && !gC.manualInputMode)
        			gC.terminal(gC.nextTestLine());
        		else {
        			playerInputString = gC.scanner.nextLine();
            		gC.terminal(playerInputString);
        		}
    		}
    	}while(!roundIsOver && !gC.gameIsOver);
    	
    	if(remainingStun > 0) remainingStun--;
    	  	
        return getIsAlive();
    }

    /**
     * Egy diák és egy ledobott aktív rongy interakcióját kezeli.
     */
    @Override
    public void encounterRag() {
    	//Üres, hiszen a diákra nincs hatással a ledobott rongy.
    }

    /**
     * Vissza adja diák nevét
     * @return string
     */
    public String toString(){
        return "Entity.Student: " + getName();
    }
}
