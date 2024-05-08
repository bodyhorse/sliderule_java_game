package Item;

import Interfaces.Debuggable;
import GameMap.*;
import Entity.*;
import GameLogic.*;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class AirFreshner extends Item implements Debuggable, Serializable {

    /**
     * Konstruktor
     * @param AFID - a légfirssítő egyedi ID-ja
     */
    public AirFreshner(int AFID){
        super(AFID);
        GameController.getInstance().debuggableObjects.put(AFID, this);
        setDurability(1);
        image = new ImageIcon("rsc/airFreshener.png").getImage();
    }

    /**
     * Amennyiben nincs felüldefiniálva a metódus az adott örökös tárgy osztályban akkor “false” értékkel tér vissza.
     * Item.Beer,Item.Transistor és Item.Camembert osztályokban van felüldefiniálva ez a függvény, mely egyedi használati működésüket valósítja meg.
     * @param r - A szoba, ahol a tárgyat használó Entity.Entity tartózkodik.
     * @param e - A tárgyat használó Entity.Entity.
     * @return - False, a használható tárgyaknál felül kell definiálni.
     */
    public boolean use(Room r, Entity e){
        r.makeNotToxic();
        int d = this.getDurability();
        this.setDurability(d - 1);
        return true;
    }

    /**
     * Minden tárgyhoz külön definíciója van a felvételnek (visitor pattern),
     * ezáltal minden tárgynál külön lesz felüldefiniálva ez az absztrakt függvény.
     * @param e - Az Entity.Entity, aki felveszi a tárgyat.
     * @return - Igaz, ha sikeres a felvétel, egyéb esetben hamis.
     */
    public boolean pickUp(Entity e) {
        return e.pickUpItem(this);
    }

    /**
     * Minden tárgyhoz külön definíciója van az eldobáshoz (visitor pattern),
     * ezáltal minden tárgynál külön lesz felüldefiniálva ez az absztrakt függvény.
     * @param e - Az Entity.Entity, aki lerakja a tárgyat.
     * @return - Igaz, ha sikeres a lerakás, egyéb esetben hamis.
     */
    public boolean drop(Entity e){
        return e.dropItem(this);
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Item.AirFreshner: #" + Integer.toString(getID());
    }


    @Override
    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        return "---- Air Freshener " + this.getID() + " ----\ndurability : " + getDurability() + "\n---- Air Freshener " + this.getID() + " ----\n";
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

