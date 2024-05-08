package Item;

import Interfaces.Debuggable;

import java.io.Serializable;
import GameLogic.*;
import Entity.*;

import javax.swing.*;

public class SlideRule extends Item implements Debuggable, Serializable {

    /**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public SlideRule(int aID) {
        super(aID);
        setDurability(1);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("logarlec/rsc/sliderule2.png").getImage();
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
        return false;
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Item.SlideRule: #" + Integer.toString(getID());
    }
    @Override
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        return "---- Item.SlideRule " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.SlideRule " + this.getID() + " ----\n";
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
