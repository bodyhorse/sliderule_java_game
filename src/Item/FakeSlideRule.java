package Item;

import java.io.Serializable;
import Entity.*;
import GameLogic.*;
import Interfaces.Debuggable;


public class FakeSlideRule extends SlideRule implements Debuggable, Serializable {

    /**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * Konstruktor, beállítja az ID változót
     * @param fSID - A tárgy ID-ja
     */
    public FakeSlideRule(int fSID) {
        super(fSID);
        setDurability(1);
        GameController.getInstance().debuggableObjects.put(fSID, this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a Logarlécet.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a Logarlécet.
     * @return - False, mivel a Logarléc felvétele a játék végét jelenti, ezért nincs értelme az eldobásnak.
     */
    @Override
    public boolean drop(Entity e) {
        return e.dropItem(this);
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    @Override
    public String toString(){
        return "SlideRule #" + Integer.toString(getID());
    }
    
    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        return "---- Item.FakeSlideRule " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.FakeSlideRule " + this.getID() + " ----\n";
    }
}
