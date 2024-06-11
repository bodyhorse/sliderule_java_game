package Item;

import java.io.Serializable;
import Entity.*;
import GameLogic.*;
import Interfaces.Debuggable;


public class FakeMask extends Mask implements Debuggable, Serializable {

    /**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * Konstruktor
     * @param fMID egyedi fakeMask azonosító
     */
    public FakeMask(int fMID){
        super(fMID);
        GameController.getInstance().debuggableObjects.put(fMID, this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a maszkot.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a maszkot.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {
        return e.dropItem(this);
    }

    /**
     * Az eredeti függvény felüldefiniálása false visszatérésűre.
     *  @return - False, mivel fake item
     */
    @Override
    public boolean getLastRoomWasToxic(){
        return false;
    }
    
    /**
     * Az eredeti függvény felüldefiniálása false visszatérésűre.
     *  @return - False, mivel fake item
     */
    @Override
    public boolean preventGasStun() {
        return false;
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    @Override
    public String toString(){
        return "Mask #" + Integer.toString(getID());
    }
    
    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        return "---- Item.FakeMask " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.FakeMask " + this.getID() + " ----\n";
    }
}
