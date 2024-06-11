package Item;

import java.awt.*;
import java.io.Serializable;
import Interfaces.Debuggable;
import Entity.*;
import GameMap.*;
import Interfaces.Observable;

public abstract class Item extends Observable implements Serializable, Debuggable {

    /**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;

    /**
     * Az Item.Item egyedi azonosítója
     */
    private int ID;
    /**
     * Az Item.Item fennmaradó élettartama
     */
    private int durability;

    /**
     * Kép
     */
    protected transient Image image;

    /**
     * Konstruktor, beállítja az ID változót
     * @param aID - A tárgy ID-ja
     */
    protected Item(int aID){
        ID = aID;
    }

    /**
     * ID getter
     * @return - Item.Item ID-ja
     */
    public int getID(){
        return ID;
    }

    /**
     * ID setter
     * @param aID - A beállítandó ID
     */
    public void setID(int aID){
        ID = aID;
    }

    /**
     * ID komparátor
     * @param inID - az ellenőrizendő ID
     * @return - egyenlőség az ellenőrizendő, és az item példány IDja között
     */
    public boolean checkID(int inID){
        return ID == inID;
    }


    /**
     * Durability getter
     * @return - Item.Item durability-je
     */
    public int getDurability(){
        return durability;
    }

    /**
     * Durability setter
     * @param aDurability - A beállítandó durability
     */
    public void setDurability(int aDurability){
        durability = aDurability;
        this.notifyObservers();
    }

    /**
     * A Transistorhoz pár érték getter.
     * @return null virtuóz
     */
    public Transistor getPair(){
        return null;
    }

    /**
     * Két tárgy összekapcsolására szolgáló függvény. További bővíthetőség érdekében felül lehet definiálni hogy más tárgyak is tudjanak kapcsolódni. 
     * Alapesetben “false” értékkel tér vissza ha a két adott tárgy nem tud kapcsolódni.
     * @param i A másik tárgy, amivel összekapcsolódik
     * @return False, mivel csak a Transistornál lesz értelme, ott lesz override-olva.
     */
    public abstract boolean connect(Item i, Entity ent);

    /**
     * Két tárgy szétkapcsolására szolgáló függvény. Alapesetben “false” értékkel tér vissza ha a két adott tárgy nem tud szétkapcsolódni.
     * @param i A tárgy párja, amit szét szeretnénk kapcsolni a tárgytól.
     * @return False, mivel csak a Transistornál lesz értelme, ott lesz override-olva.
     */
    public abstract boolean disconnect(Item i);

    /**
     * Két tranzisztor összekapcsolására hivatott függvény
     * @param t tranzisztor amit össze akarunk kapcsolni
     * @param e az Entitás akié
     * @return sikeres volt-e a kapcsolás
     */
    public abstract boolean TransistorConnect(Transistor t, Entity e);

    /**
     * Két tranzisztor szétkapcsolására hivatott függvény
     * @param t tranzisztor amit szét akarunk kapcsolni
     * @return sikeres volt-e a szétkapcsolás
     */
    public abstract boolean TransistorDisconnect(Transistor t);

    /**
     * Amennyiben nincs felüldefiniálva a metódus az adott örökös tárgy osztályban akkor “false” értékkel tér vissza. 
     * Item.Beer,Item.Transistor és Item.Camembert osztályokban van felüldefiniálva ez a függvény, mely egyedi használati működésüket valósítja meg.
     * @param r - A szoba, ahol a tárgyat használó Entity.Entity tartózkodik.
     * @param e - A tárgyat használó Entity.Entity.
     * @return - False, a használható tárgyaknál felül kell definiálni.
     */
    public boolean use(Room r, Entity e){
        return false;
    }

    /**
     * Amennyiben az adott tárgy életmentő, akkor ez a függvény felül van definiálva (és az adott osztály leírásban részletezve). Alapesetben visszatér egy “false”-al.
     * @return - False, az életmentésre képes tárgyaknál felül kell definiálni.
     */
    public boolean save(Entity ent){
        return false;
    }

    /**
     * A játékos szobák közötti teleportálását teszi lehetővé. Ha egy teleportálást nem megvalósító tárgyon hívódik, művelet nélkül hamis értékkel tér vissza.
     * @param e - Az Entity.Entity, aki teleportál.
     * @return - False, mivel csak a Teleport tárgynál van értelme.
     */
    public boolean teleport(Entity e, Room r){
        return false;
    }

    /**
     * A tárgy eszméletvesztés okozásának logikai értékét adja meg
     * @return - alapesetben "false" értéket ad vissza
     */
    public boolean ableToStun(){ return false; }

    /**
     * Minden tárgyhoz külön definíciója van a felvételnek (visitor pattern), 
     * ezáltal minden tárgynál külön lesz felüldefiniálva ez az absztrakt függvény.
     * @param e - Az Entity.Entity, aki felveszi a tárgyat.
     * @return - Igaz, ha sikeres a felvétel, egyéb esetben hamis.
     */
    public abstract boolean pickUp(Entity e);

    /**
     * Minden tárgyhoz külön definíciója van az eldobáshoz (visitor pattern), 
     * ezáltal minden tárgynál külön lesz felüldefiniálva ez az absztrakt függvény.
     * @param e - Az Entity.Entity, aki lerakja a tárgyat.
     * @return - Igaz, ha sikeres a lerakás, egyéb esetben hamis.
     */
    public abstract boolean drop(Entity e);
    
    /**
     * A gáz által okozott bénítás kivédését végzi. Virtuális metódus,
     * ami hamis értékkel tér vissza. Ha egy tárgy véd a gáz ellen, ott felül kell definiálni.
     * @return A gáz elleni védekezés logikai értéke.
     */
    public boolean preventGasStun() {return false;}
    
    /**
     * Egy tárgy normál szobába bevitelével járó állapotváltozásokat kezeli.
     * Virtuális metódus ami üres, ha egy tárgy a működést megvalósítja, ott felül kell definiálni.
     */
    public void carriedIntoNormalRoom() {
    	//Üres, hiszen a tárgyak többsége nem végez műveletet normál szobába lépés során.
    }

    /**
     * A tárgy vizuális megjelenítéséhez használt kép gettere.
     * @return A tárgy vizuális megjelenítéséhez használt kép.
     */
    public Image getImage() {
        return image;
    }
}
