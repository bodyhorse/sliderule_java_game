package Item;

import java.io.Serializable;
import GameMap.*;
import Entity.*;
import GameLogic.*;
import Interfaces.Debuggable;


public class FakeMask extends Mask implements Debuggable, Serializable {

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
     * Az eredeti függvény felüldefiniálása üres függvényre, hiszen a fake itemnél ez nem csinál semmit.
     */
    @Override
    public void carriedIntoNormalRoom() {}

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Item.Mask: #" + Integer.toString(getID());
    }
    @Override
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        return "---- Item.FakeMask " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.FakeMask " + this.getID() + " ----\n";
    }
}
