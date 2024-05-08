package GameMap;

import Interfaces.Debuggable;
import Interfaces.Observable;
import Interfaces.Observer;
import Item.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import GameMap.*;
import Entity.*;
import GameLogic.*;


public class Room extends Observable implements Debuggable, Serializable, Observer {

    /**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Egyedi azonosító
     */
    private int ID;

    /**
     * A szoba toxicitásának logikai értéke
     */
    private boolean isToxic;

    /**
     * A szoba összeovlasztás szüzeiességének logikai értéke
     */
    private boolean isMerged;

    /**
     * A szoba ragadósságának logikai értéke
     */
    private boolean isSticky;

    /**
     * A szoba befogadó képességének száma
     */
    private int capacity;
    
    /**
     * A szoba (legalább egyszer) kitakarított mivoltának logikai értéke
     */
    private boolean hasBeenCleaned;
    
    /**
     * Az utolsó takarítás óta a szobába lépett Entity.Entity-k száma, csak akkor érvényes
     * ha a hasBeenCleaned igaz értékű
     */
    private int entitiesSinceCleanup;

    /**
     * A szobában tárolt tárgyak listája
     */
    private ArrayList<Item> containedItems;

    /**
     * A szobában lévő entitások listája
     */
    private ArrayList<Entity> entitiesInside;
    
    /**
     * A szobába belépni várakozó entitások listája
     */
    private ArrayList<Entity> waitingToEnter;

    /**
     * A szoba ajtajainak listája
     */
    private ArrayList<Door> doors;

    /**
     * Konstruktor
     * @param toxic - szoba toxicitása
     * @param cap - szoba befogadóképessége
     */
    public Room(boolean toxic, int cap, int id){
        isToxic = toxic;
        isMerged = false;
        capacity = cap;
        hasBeenCleaned = false;
        entitiesSinceCleanup = 0;
        ID = id;

        containedItems = new ArrayList<>();
        entitiesInside = new ArrayList<>();
        waitingToEnter = new ArrayList<>();
        doors = new ArrayList<>();
        GameController.getInstance().debuggableObjects.put(ID, this);
        addObserver(GameController.getInstance());
    }

    /**
     * Az egyedi azonosítót adja vissza
     * @return - Azonosító
     */
    public int getID(){
        return ID;
    }

    /**
     * Az egyedi azonosítót állítja
     * @param id -Azonosító
     */
    public void setID(int id){
        ID = id;
    }

    /**
     * isToxic logikai érték getter függvénye
     * @return - isToxic logikai érték
     */
    public boolean getIsToxic(){ return isToxic; }

    /**
     * isToxic alapvető setter függvénye
     * @param t - beállítandó toxicitás logikai értéke
     */
    public void setIsToxic(boolean t){ isToxic = t; }

    /**
     * Beállítja az isToxic logikai értéket a kapott int alapján
     * @param state - amennyiben 0, hamis amennyiben 1 igaz értéket állít be a függvény
     */
    public void setIsToxic(int state){ isToxic = state == 1; }

    /**
     * A szoba összeovlasztás szüzeiességének logikai értékét vissza adó getter függvény
     * @return - szoba összeovlasztás szüzeiessége
     */
    public boolean getIsMerged(){ return isMerged; }

    /**
     * A szoba összeovlasztás szüzeiességének logikai értékét beállító setter függvény
     * @param m - szoba összeovlasztás szüzeiessége
     */
    public void setIsMerged(boolean m){ isMerged = m; }

    /**
     * Az isSticky logikai változó getter függvénye
     * @return - isSticky
     */
    public boolean getIsSticky(){ return isSticky; }

    /**
     * Az isSticky logikai változó setter függvénye
     * @param - ragadósság logikai értéke
     */
    public void setIsSticky(boolean sticky){ isSticky = sticky; }

    /**
     * A szoba befogadóképessét vissza adó getter függvény
     * @return - a szoba befogadó képesség
     */
    public int getCapacity(){ return capacity; }

    /**
     * A szoba befogadóképessét beállító setter függvény
     * @param c - a szoba befogadó képessége
     */
    public void setCapacity(int c){ capacity = c;}
    
    /**
     * hasBeenCleaned tagváltozó getter metódusa
     * @return hasBeenCleaned tagváltozó értéke
     */
    public boolean getHasBeenCleaned() {
    	return hasBeenCleaned;
    }
    
    /**
     * Beállítja a szoba (legalább egyszer megtörtént) kitisztítottságának
     * logikai értékét a kapott paraméter alapján
     * @param cleaned - a kitisztítottság új logikai értéke
     */
    public void setHasBeenCleaned(boolean cleaned) {
    	hasBeenCleaned = cleaned;
    }
    
    /**
     * entitiesSinceCleanup tagváltozó getter metódusa
     * @return roundsSinceCleanup tagváltozó értéke
     */
    public int getEntitiesSinceCleanup() {
    	return entitiesSinceCleanup;
    }
    
    /**
     * Beállítja az utolsó takarítás óta a szobába belépett Entity.Entity-k számát a paraméterben kapott értékre
     * @param rounds az utolsó takarítás óta a szobába belépett Entity.Entity-k számának új értéke
     */
    public void setEntitiesSinceCleanup(int rounds) {
    	entitiesSinceCleanup = rounds;
    }

    /**
     * A szoba ajtajainak listáját vissza adó getter függvény
     * @return - a szoba ajtajainak listája
     */
    public ArrayList<Door> getDoors(){ return doors; }

    /**
     * Vissza adja szobábnak lévő entitások számát
     * @return - szobában lévő entitások száma
     */
    public int getEntityCount(){ return entitiesInside.size(); }

    /**
     * A entitiesInside lista getter függvénye
     * @return - entitiesInside
     */
    public ArrayList<Entity> getEntitesInside(){ return entitiesInside; }

    /**
     * Tárolt tárgyak getter függvénye
     * @return - tárolt tárgyak
     */
    public ArrayList<Item> getContainedItems(){ return containedItems; }

    /**
     * Hozzá ad egy tárgyat a szobában lévő tárgyak listájához
     * @param i - hozzá adandó tárgy
     */
    public void addItem(Item i){
        containedItems.add(i);
        i.addObserver(this);
    }

    /**
     * Kiveszi az adott tárgyat a szobában lévő tárgyak listájából
     * @param i - eltávolítandó tárgy
     * @return - vissza adja a kivett tárgyat
     */
    public Item removeItem(Item i){
        if(isSticky)
            return null;
        containedItems.remove(i);
        i.removeObserver(this);
        return i;
    }
    
    /**
     * Ad egyet a szobában található tárgyak közül
     * @return Egy, a szobában lévő tárgy referenciája, ha a szobában
     * nincs tárgy akkor null.
     */
    public Item giveAnItem() {
    	int numOfItemsInRoom = containedItems.size();
    	if(numOfItemsInRoom == 0) return null;
    	return containedItems.get(GameController.getInstance().random.nextInt(numOfItemsInRoom));
    }
    
    /**
     * Egy helyváltoztatás végrehajtását kezdeményezi. Ellenőrzi, hogy a megadott ajtó
     * tényleg a szobában található-e. Valamint elvégzi az ajtóId és referencia közötti feloldást.
     * @param doorId Az ajtó id-ja amelyen az áthaladás történni fog
     * @param ent Az Entity.Entity referenciája aki szeretne az ajtón átmenni
     * @return A mozgás sikerességének logikai értéke
     */
    public boolean initiateMove(int doorId, Entity ent) {
    	
    	//-1 ha helyben akar maradni az entity
    	if(doorId == -1) {
    		return ent.moveTo(null);
    	}
    	
    	for(Door door : doors) {
    		if(door.getID() == doorId) {
    			return ent.moveTo(door);
    		}
    	}
    	GameController.getInstance().errorMsg(302, String.valueOf(doorId));
    	return false;
    }

    /**
     * Kezeli azt, hogy az entitás aki be szeretne menni a szobába befér - e.
     * Hogyha igen,hozzáadja a kapott entitást (e) az entitások listájához (entitiesInside),
     * meghívja az Entity.Entity roomChanged() metódusát, valamint ha a szoba gázzal telített, az Entity.Entity toxicate() metódusát is.
     * Ezek után visszatér “true”-val. Ha a szoba férőhelye megtelt, akkor pedig visszatér “false”-al.
     * @param ent - kezelendő entitás
     * @return  a mozgatás sikerességének logikai értéke
     */
    public boolean acceptEntity(Entity ent){
    	
    	//Abban az esetben ha az Entity.Entity a maradás mellett döntött.
    	if(entitiesInside.contains(ent)) {
    		
    		if(isToxic) ent.toxicate();
    		else ent.enteredNormal();

    		GameController.getInstance().statusMsg(311, "GameMap.Room:" + String.valueOf(getID()));
    		return true;
    		
    	}else if(capacity > (entitiesInside.size() + waitingToEnter.size())) {
    		
    		addToQueue(ent);
    		ent.roomChanged(this);
    		
    		if(isToxic) ent.toxicate();
    		else ent.enteredNormal();
			
    		entityEntered();
    		
    		GameController.getInstance().statusMsg(312, "GameMap.Room:" + String.valueOf(getID()));
    		return true;
    	}
    	
    	GameController.getInstance().errorMsg(301, "GameMap.Room:" + String.valueOf(ID));
    	return false; 
    	
    }
     
    /**
    * Entity.Janitor szobába lépésének következményeit kezeli. Akkor hívandó,
    * ha már a takarító biztosan be tudott lépni a szobába. A szobában lévő összes Entity.Entity-re
    * meghívódik a szoba „throwOut()” metódusa, mellyel kitessékeli őket.
    * Végül a szobában megtörténik a takarítás, azaz a „setHasBeenCleaned” igaz értékűre állítódik,
    * valamint a „roundsSinceCleanup„ értéke 0-ra változik. Meghívásra kerül a szoba „makeNotToxic” metódusa,
    *  amely a szobában lévő esetleges gáz kiszellőztetéséért felel.
    */
    public void janitorEntered() {
    	ArrayList<Entity> entitiesToThrowOut = new ArrayList<Entity>(entitiesInside);
		
		for(Entity ent : entitiesToThrowOut) throwOut(ent);
		
		setHasBeenCleaned(true);
		setEntitiesSinceCleanup(0);
		setIsSticky(false);
		makeNotToxic();
    }

    /**
     * Hozzá ad egy entitást a szobában lévő entitások listjához
     * @param ent - hozzá adandó entitás
     */
    public void addEntity(Entity ent){ entitiesInside.add(ent); }

    /**
     * Eltávoltít egy entitás a szobában lévő entitások listjából
     * @param ent -eltávolítandó entitás
     */
    public void removeEntity(Entity ent){ entitiesInside.remove(ent); }

    /**
     * Hozzáad egy Entityt a szolbába belépésre várakozók listábába
     * @param ent A hozzáadandó Entity.Entity
     */
    public void addToQueue(Entity ent) { waitingToEnter.add(ent); }

    /**
     * Mérgezővé teszi a szobát
     */
    public void makeToxic(){ isToxic = true; }

    /**
     * Megtisztítja a szobát a mérgezőségtől
     */
    public void makeNotToxic(){ isToxic = false; }

    /**
     * A szobák összeolvasztását kezelő függvény
     * @param r2 - ezzel a szobával kezdeményezi az összeolvadást a szoba amelyen hívták a függvényt
     * @return - az összeolvadás sikerességének logikai értéke
     */
    public boolean merge(Room r2){
        if(getEntityCount() == 0){
            ArrayList<Item> tmpItemList = r2.mergeIntoMe();
            if(tmpItemList != null){
                int state = GameController.getInstance().newMergeState(this,r2);
                this.setIsToxic(state);
                GameController.getInstance().mergeDoors(this,r2);
                this.setCapacity(r2.myCapacity(this.capacity));
                for (Item it : tmpItemList){
                    this.addItem(it);
                }
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * A merge() által hívott függvény, amely jelzi a cél szobának hogy olvadjon vele össze
     * @return - amennyiben lehetésges az összeolvadás vissza adja a szobában tárolt elemek listáját, ellenkező esetben null értékkel
     */
    public ArrayList<Item> mergeIntoMe(){
        if(this.getEntityCount() == 0) return containedItems;
        else return null;
    }

    /**
     * Egy szoba kettéválasztását kezelő függvény
     * @return - amennyiben lehetésges a szétválasztás az új szoba referenciájával tér vissza, ellenkező esetben null értékkel
     */
    public Room split(){
        if(this.getEntityCount() == 0){
            Room newR = new Room(getIsToxic(),getCapacity(), GameController.getInstance().getNextGlobalID());
            for(int i = 0; i < this.containedItems.size()/2; i++){
                Item tmpIt = this.containedItems.get(i);
                newR.addItem(tmpIt);
                this.removeItem(tmpIt);
            }

            for(int i = 0; i < this.doors.size()/2; i++){
                Door tmpDoor = this.doors.get(i);
                newR.addDoor(tmpDoor);
                this.removeDoor(tmpDoor);
            }

            Door conn = new Door(this, newR, false, Door.Direction.BOTH, GameController.getInstance().getNextGlobalID());
            this.addDoor(conn);
            newR.addDoor(conn);
            return newR;
        }
        else {
            return null;
        }
    }

    /**
     * Elátkozza egy szoba összes ajtaját
     */
    public void curseAllDoors(){ for (int i = 0; i < doors.size(); i++) { doors.get(i).setIsCursed(true); }}

    /**
     * Feloldja az átok a szoba összes ajtajáról
     */
    public void unCurseAllDoors(){ for (int i = 0; i < doors.size(); i++) { doors.get(i).setIsCursed(false); }}

    /**
     * Vissza adja egy szoba első szomszédos szobáját
     * @return - első szomszédos szoba
     */
    public Room getFirstNeighbour(){
        return doors.get(0).neighbourRoom(this);
    }

    /**
     * Átadja egy szoba a saját kapacitását, annak a szobának amelyen meghívta hogy összehasonlítsa saját kapacitásával
     * @param myCap - küldő kapacitása
     * @return - vissza adja a nagyobb kapacitás értékét
     */
    public int myCapacity(int myCap){
        int res = Math.max(myCap,this.getCapacity());
        return res;
    }

    /**
     * Hozzá ad egy ajtót a szobában tárolt ajtók listájához
     * @param d1 - hozzá adandó ajtó
     */
    public void addDoor(Door d1){ doors.add(d1); }

    /**
     * Eltávolít egy ajtót a szobában tárolt ajtók listájáról
     * @param d1 - eltávolítandó ajtó
     */
    public void removeDoor(Door d1){ doors.remove(d1); }
    
    /**
     * Adott számú ajtón megpróbálja az adott Entityt átmozgatni. Ha a megadott szám
     * nagyobb mint ahány ajtó van a szobában az összesen megpróbálja átmozgatni.
     * Az első sikeres mozgatásnál a próbálkozás leáll.
     * @param noOfDoors Száma az ajtóknak amennyin az áthaladást meg kell próbálni
     * @param entity Entity.Entity referenciája akit másik szobába kell mozdítani
     * @param movefrom A szoba referenciája ahonnan az Entityt mozgatjuk
     * @return Az átmozgatás sikerességének logikai értéke
     */
    public boolean tryToMove(int noOfDoors, Entity entity, Room movefrom) {
    	if(!entitiesInside.contains(entity) || noOfDoors == 0) return false;
    	
    	//Random ajtók kiválasztása a szobából
    	HashSet<Integer> doorIndexes = new HashSet<Integer>();
    	
    	while(doorIndexes.size() < noOfDoors && doorIndexes.size() < doors.size()) {
    		
    		doorIndexes.add(GameController.getInstance().random.nextInt(doors.size()));
    	}
    	
    	//Entity.Entity mozgatása az ajtókon keresztül
    	for(Integer dInd : doorIndexes) {
    		boolean isMoveSuccessful = doors.get(dInd).moveEntity(entity, movefrom);
    		if(isMoveSuccessful) return true;
    	}
    	
    	return false;
    }

    /**
     * Kidob egy Entity.Entity-t a szobából amennyiben lehetséges
     * @param entity - kidobandó Entity.Entity
     */
    public boolean throwOut(Entity entity){
		boolean canBeMoved = false;
		for(int i = 0; i < doors.size(); i++){
			canBeMoved = entity.moveTo(doors.get(i));
			if(canBeMoved) return true;
		}
        return false;
    }
	
	/**
	 * Végig megy a szoba összes tárgyán amennyiben van benne olyan amit eszméletlenné tud tenni, "true" értékkel tér vissza ellenkező esetben false
     * return - az eszméletlenné tévő tárgy a szobában levésének logikai értéke
	 */
	public boolean containsRag(){
		boolean isActRag = false;
		for(int i = 0; i < containedItems.size(); i++){
			isActRag = containedItems.get(i).ableToStun();
			if(isActRag) return true;
		}
		return false;
	}

    /**
     * Amennyiben containsRag "true" értékkel tér vissza minden szobában lévő entityn végig hívja az encounterRag-et
     */
    public void stunWithRag(){
        if(this.containsRag()){
            for (int i = 0; i < entitiesInside.size(); i++){
                entitiesInside.get(i).encounterRag();
            }
        }
    }
	
	/**
	 * Egy Entity.Entity szobába belépésének a szobára gyakorolt hatásait érvényesíti.
	 * Ellenőrzi, hogy a szoba ki lett-e már takarítva. Ha igen,
	 * megnöveli a takarítás óta a szobába belépett Entity.Entity-k számát eggyel (entitiesSinceCleanup).
	 * Ezt követően ha az így megnövelt érték legalább öt, a szoba padlóját ragacsossá állítja
	 * (az „isSticky” értékét „true”-ra állítja).
	 *
	 */
	public void entityEntered() {
		if(getHasBeenCleaned()) {
			setEntitiesSinceCleanup(getEntitiesSinceCleanup() + 1);
			if(getEntitiesSinceCleanup() >= 5) setIsSticky(true);
		}
	}
	
	/**
	 * Szoba egy új kör kezdetekor esedékes műveleteit végzi el.
	 * A szobába belépésre várakozó entity-ket a szobában tartozkódók listájába rakja,
	 * valamint végbemennek a találkozók is. Mindenki mindenki mással pontosan egyszer találkozik.
	 */
	public void tick() {
		entitiesInside.addAll(waitingToEnter);
		waitingToEnter.clear();
		int countOfEntitiesInRoom = entitiesInside.size();
		stunWithRag();
		//meet úgy, hogy mindenki mindenkivel csak egyszer találkozzon.
		for(int i = 0; i < countOfEntitiesInRoom; i++) {
			for(int j = i + 1; j < countOfEntitiesInRoom; j++) {
				Entity entToMeet = entitiesInside.get(j);
				
				entitiesInside.get(i).meetMe(entToMeet);
			}
		}
		
	}

    /**
     * Készít egy listát a szoba ajtajaival, és azzal, hogy azok melyik szobába tartanak.
     * @return - A kész lista
     */
    public String listDoors(){
        String message = "";
        String section;
        String roomSection;
        for(int i = 0; i < doors.size(); i++) {
            //Minden ajtó toString-je felülírva, és azt is megtudjuk hogy hova visz az ajtó
            section = doors.get(i).toString();
            roomSection = doors.get(i).getCurrentDirection(this);
            message += "\t-" + section + " leading to " + roomSection + "\n";
        }
        return message;
    }

    /**
     * Listába szedi a szobában lévő Entityket, és egy ebből álló stringgel tér vissza
     * @return - A lista
     */
    public String listEntities(){
        String message = "";
        String section;
        for(int i = 0; i < entitiesInside.size(); i++){
            section = entitiesInside.get(i).toString();
            message += "\t-" + section + "\n";
        }
        return  message;
    }

    /**
     * Listába szedi a szobában lévő Itemeket, és ebből álló stringel tér vissza
     * @return - a lista
     */
    public String listItems(){
        String message = "";
        String section;
        for(int i = 0; i < containedItems.size(); i++){
            section = containedItems.get(i).toString();
            message += "\t-" + section + "\n";
        }
        return message;
    }
    /**
     *  item pickup parancsának kezelése
     * @param pickupAll - összes item felvételre kerüljön-e
     * @param e - a felvevő entitás
     * @param input - a beütött parancs taglalva szóköz mentén
     * @param freeSpace - az entitásnál zsákjában levő szabad hely
     * @return - státusz
     */
    public boolean initiatePickup(boolean pickupAll, Entity e, String[] input, int freeSpace){
        //felvétel ha az -a flaget kapta a parancs (minden felvétel)
        //EZZEL TÖRŐDNI KELL, MERT EGYELŐRE CSAK ANNYIT VESZ FEL AMENNYIT TUD.
        //csak akkor történik meg, ha a feltételek teljesülnek
        if(isSticky){
            GameController.getInstance().errorMsg(203,input[1]);
            return false;
        }

        if(pickupAll && containedItems.size() <= freeSpace){
            for(Item i:this.containedItems){
                i.pickUp(e);
            }
            GameController.getInstance().statusMsg(214,String.valueOf(ID));
            return true;
        }
        else if(pickupAll && containedItems.size() > freeSpace){
            GameController.getInstance().errorMsg(209, String.valueOf(this.ID));
            return false;
        }

        Item grabbedItem = null;
        int targetItemID = Integer.parseInt(input[1]);
        //Tárgy keresése szobában
        for(Item i : containedItems){
            if(i.checkID(targetItemID)){
                grabbedItem = i;
                break;
            }
         }
        //Ha a tárgy nincs megtalálva, hiba keletkezik
        if(grabbedItem == null){
            GameController.getInstance().errorMsg(207, input[1]);
            return false;
        }
        
        boolean pickupResult = grabbedItem.pickUp(e);
        
        if(pickupResult) GameController.getInstance().statusMsg(211, input[1]);
        return pickupResult;
    }
    @Override
    /**
     * Debug szöveg generálása
     * @param cmdInput - ezzel tud ID-t ellenőrizni
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    public String debug() {
        String retVal =  "---- GameMap.Room " + this.getID() + " ----\nisToxic : " + isToxic + "\nisMerged : " + isMerged +"\nisSticky : " + isSticky + "\ncapacity : " + capacity + "\nhasBeenCleaned : " + hasBeenCleaned + "\nentitiesSinceCleanup : " + entitiesSinceCleanup + "\ncontainedItems :\n" ;
        
        for(Item i: containedItems){
            // deprecated retVal += "\n - "+ i.getClass().getName()+ " " + i.getID();
            retVal +=  i.debug();
        }
        retVal += "entitiesInside :\n";
        for(Entity e: entitiesInside){
            //deprecated retVal += "\n - "+ e.getClass().getName()+ " " + e.getID();
            retVal +=  e.debug();
        }
        retVal += "doors :\n";
        for(Door d: doors){
            //retVal += "\n - GameMap.Door "+ d.getID();
            retVal += d.debug();
        }
        retVal += "---- GameMap.Room " + this.getID() + " ----\n";
        
        return retVal;
    }

    @Override
    public void update() {
        notifyObservers();
    }
}