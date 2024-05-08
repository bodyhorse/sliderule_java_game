package Item;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Rag extends Item implements Decaying, Debuggable, Serializable {
    /**
     * A tárgy aktívitását jelző logikai érték
     */
    private boolean isActive;

    /**
     * Az osztály konstruktora, beállítja a tárgy ID-ját.
     * @param aID - A tárgy ID-ja.
     */
    public Rag(int aID) {
        super(aID);
        setDurability(5);
        GameController.getInstance().debuggableObjects.put(aID, this);
        image = new ImageIcon("rsc/rag.png").getImage();
    }

    /**
     * Az isActive attribútum gettere
     * @return - Az isActive attribútum értéke
     */
    public boolean getIsActive(){
        return isActive;
    }

    /**
     * Az isActive attribútum settere
     * @param b - A beállítandó isActive érték
     */
    public void setIsActive(boolean b){
        isActive = b;
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “pickUpItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki felveszi a rongyot.
     * @return - Az Entity.Entity erre a tárgyra használt pickUpItem függvényének visszatérési értéke.
     */
    @Override
    public boolean pickUp(Entity e) {
        boolean flag = e.pickUpItem(this);
        if (!isActive){
            isActive = true;
            GameController.getInstance().addDecayingItem(this);
        }
        return flag;
    }

    /**
     * A függvény célja, hogy a saját referenciájával tudja meghívni a paraméterként kapott Entity.Entity “dropItem” rá vonatkozó változtatát. (visitor pattern)
     * @param e - Az Entity.Entity, aki lerakja a rongyot.
     * @return - Az Entity.Entity erre a tárgyra használt drop() függvényének visszatérési értéke.
     */
    @Override
    public boolean drop(Entity e) {
        return e.dropItem(this);
    }

    /**
     * Ez a függvény a tárgy korának állításáért felel.
     */
    @Override
    public void age() {
        if (isActive){
            setDurability(getDurability() - 1);
        }
    }

    /**
     * A rongy tanárra gyakorolt eszmélet vesztési képességének logikai értéke
     * @return - ha a rongy aktiv és van benne még töltet akkor "true" értékkel tér vissza ellenkező esetben "false"
     */
    public boolean ableToStun(){
        if(isActive && getDurability() > 0) return true;
        return false;
    }

    /**
     * Visszatér egy Stringgel ami a maga nevéből és az azonosítójából áll
     * @return - A String
     */
    public String toString(){
        return "Item.Rag: #" + Integer.toString(getID());
    }
    @Override
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        return "---- Item.Rag " + this.getID() + " ----\ndurability : " + getDurability() + "\nisActive : " + isActive +"\n---- Item.Rag " + this.getID() + " ----\n";
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
