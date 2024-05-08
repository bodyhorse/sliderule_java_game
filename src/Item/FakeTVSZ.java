package Item;

import java.io.Serializable;
import GameMap.*;
import Entity.*;
import GameLogic.*;
import Interfaces.Debuggable;


public class FakeTVSZ extends TVSZ implements Debuggable, Serializable {

    /**
     * Konstruktor, beállítja az ID változót
     * @param aID - A tárgy ID-ja
     */
    public FakeTVSZ(int aID) {
        super(aID);
        GameController.getInstance().debuggableObjects.put(aID, this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a Item.TVSZ-t.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a Item.TVSZ-t.
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
    public boolean save(Entity ent){
    	return false;
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Item.TVSZ: #" + Integer.toString(getID());
    }
    @Override
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        return "---- Item.FakeTVSZ " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.FakeTVSZ " + this.getID() + " ----\n";
    }

}
