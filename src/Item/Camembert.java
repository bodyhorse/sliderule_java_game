package Item;

import java.io.Serializable;

import javax.swing.ImageIcon;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Interfaces.*;

public class Camembert extends Item implements Debuggable, Serializable {

    /**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public Camembert(int aID) {
        super(aID);
        setDurability(1);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("./rsc/camembert.png").getImage();
    }

    /**
     * A függvény célja, hogy a Camembertet használva elgázosítja a szobát.
     * @param r - A szoba, ahol használva lesz a tárgy.
     * @param e - Az Entity.Entity, aki használja.
     * @return - True, ha sikeres a használat, False egyéb esetben.
     */
    @Override
    public boolean use(Room r, Entity e){
        r.makeToxic();
        e.removeItem(this);
        
        this.notifyObservers(); //TODO nem biztos hogy kell ide
        
        return true;
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a Camembertet.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a Camembertet.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {
        return e.dropItem(this);
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Camembert #" + Integer.toString(getID());
    }
    
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        return "---- Item.Camembert " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.Camembert " + this.getID() + " ----\n";
    }

    /**
     * Ezzel az itemmel nem lehet connect-elni, így false a visszatérés.
     */
    @Override
    public boolean connect(Item i, Entity ent) {
        return false;
    }

    /**
     * Ezzel az itemmel nem lehet disconnectelni, így false a visszatérés.
     */
    @Override
    public boolean disconnect(Item i) {
        return false;
    }


    /**
     * Ezzel az itemmel nem lehet connect-elni, így false a visszatérés.
     */
    @Override
    public boolean TransistorConnect(Transistor t, Entity e) {
        return false;
    }

    /**
     * Ezzel az itemmel nem lehet disconnectelni, így false a visszatérés.
     */
    @Override
    public boolean TransistorDisconnect(Transistor t) {
        return false;
    }
}
