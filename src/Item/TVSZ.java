package Item;

import java.io.Serializable;

import javax.swing.ImageIcon;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;

public class TVSZ extends Item implements Debuggable, Serializable {

    /**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public TVSZ(int aID) {
        super(aID);
        setDurability(3);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("rsc/TVSZ.png").getImage();
    }
    
    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a Item.TVSZ-t.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        boolean flag = e.pickUpItem(this);

        return flag;
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a Item.TVSZ-t.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {    	
    	boolean ret = e.dropItem(this);
    	
        return ret ;
        

    	
    }
    
    /**
     * A függvény célja, hogy ha a durability nagyobb mint 0, akkor “true” értékkel tér vissza és levon egyet a durabilityből. Ellenkező esetben a visszatérési érték “false”.
     * @return - True, ha sikeres a megmentés, False egyéb esetben.
     */
    @Override
    public boolean save(Entity ent){
    	boolean ret = false;
    	
        int d = getDurability();

        if (d > 0){
            setDurability(d - 1);
            ret =  true;
        }

        return ret;
    }

    /**
     * Visszatér egy Stringgel, az azonosítójával kiegészítve
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
        return "---- Item.TVSZ " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Item.TVSZ " + this.getID() + " ----\n";
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
