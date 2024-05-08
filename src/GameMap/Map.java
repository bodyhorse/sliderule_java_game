package GameMap;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Map implements Serializable, Debuggable {
    /**
     * Egyedi azonosító
     */
    public int ID;

    /**
     * A térképen található szobák listája
     */
    public ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Konstruktor
     * @param id - Egyedi azonosító
     */
    public Map(int id){
        ID = id;
        GameController.getInstance().debuggableObjects.put(id,this);
    }


    /**
     * Hozzá ad egy szobát a térképhez
     * @param newRoom - hozzá adandó szoba
     */
    public void addRoom(Room newRoom){
        rooms.add(newRoom);
    }

    /**
     * Eltávolít egy szobát a térképről
     * @param roomToRemove - eltávolítandó szoba
     */
    public void removeRoom(Room roomToRemove){
        rooms.remove(roomToRemove);
    }

    /**
     * Felépíti a játékteret (térképet) először egy bináris fát (szoba -> csúcs, ajtó -> él).
     * Második lépés képpen hozzá ad adott mennyiségű ajtót figyelve arra hogy nincs-e még ajtó
     * a két szoba között vagy nem 1 szobába tesz ajtót önmagához.
     * @param levels - a fa szintjeinek száma
     * @param extraEdges - az extra élek száma amit hozzá ad a fához
     */
    public void buildMap(int levels, int extraEdges){
        ArrayList<Room> waitingForDoors = new ArrayList<>();
        ArrayList<Room> waitingForWaitingForDoors = new ArrayList<>();

        //Creating basic binary tree with levels
        waitingForDoors.add( new Room(false,5, GameController.getInstance().getNextGlobalID()));
        for (int i = 0; i < levels - 1; i++){
            for(int k = 0; k < waitingForDoors.size(); k++){
                Room newR1 = new Room(false,5, GameController.getInstance().getNextGlobalID());
                Room newR2 = new Room(false,5, GameController.getInstance().getNextGlobalID());
                
                Door newD1 = new Door(waitingForDoors.get(k),newR1, false, Door.Direction.BOTH, GameController.getInstance().getNextGlobalID());
                Door newD2 = new Door(waitingForDoors.get(k),newR2, false, Door.Direction.BOTH, GameController.getInstance().getNextGlobalID());
                newR1.addDoor(newD1);
                newR2.addDoor(newD2);
                
                waitingForDoors.get(k).addDoor(newD1);
                waitingForDoors.get(k).addDoor(newD2);

                waitingForWaitingForDoors.add(newR1);
                waitingForWaitingForDoors.add(newR2);
            }
            rooms.addAll(waitingForDoors);
            waitingForDoors.clear();
            waitingForDoors.addAll(waitingForWaitingForDoors);
            waitingForWaitingForDoors.clear();
        }
        rooms.addAll(waitingForDoors);

        //Adding extra edges(doors) in the tree
        int room1Index = -1, room2Index = -1;
        boolean canAddDoor = false;
        for (int i = 0; i < extraEdges; i++) {
            while(!canAddDoor){
                while((room1Index == room2Index) || (room1Index < 3) || (room2Index < 3)){  //checking if its not the same ID or lower than lvl 2
                    room1Index = GameController.getInstance().random.nextInt(rooms.size() - 1);
                    room2Index = GameController.getInstance().random.nextInt(rooms.size() - 1);
                }
                if(!hasDoor(room1Index,room2Index)){                                        //adding new door if the rooms dont have a door already
                    Door newD = new Door(rooms.get(room1Index),rooms.get(room2Index),false, Door.Direction.BOTH, GameController.getInstance().getNextGlobalID());
                    rooms.get(room1Index).addDoor(newD);
                    rooms.get(room2Index).addDoor(newD);
                    canAddDoor = true;
                }
            }
        }
    }

    /**
     * Térképet tárgyakkal feltöltő metódus. Először egy listába rak meghatározott mennyiségű tárgyat,
     * majd ezeket a Random osztály segítségével szétszórja a térképen.
     */
    public void fillWithItems(int normalCount, int fakeCount){
        ArrayList<Item> itemsToAdd = new ArrayList<>();

        //adding normal items to list
        itemsToAdd.add(new SlideRule(GameController.getInstance().getNextGlobalID()));
        for(int i = 0; i < normalCount; i++){
            itemsToAdd.add(new AirFreshner(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new Beer(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new Camembert(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new Mask(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new Rag(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new TVSZ(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new Transistor(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new Transistor(GameController.getInstance().getNextGlobalID()));
        }

        //adding fake items to list
        itemsToAdd.add(new FakeSlideRule(GameController.getInstance().getNextGlobalID()));
        for (int i = 0; i < fakeCount; i++){
            itemsToAdd.add(new FakeMask(GameController.getInstance().getNextGlobalID()));
            itemsToAdd.add(new FakeTVSZ(GameController.getInstance().getNextGlobalID()));
        }

        //Putting items on the map
        int lastID = -1, randID = -1;
        for (int i = 0; i < itemsToAdd.size(); i++) {
            while ((randID == lastID) || (i == 0 && randID < 6))                        //checking if it isn't the same as the last one or if it is Item.SlideRule it has to be deeper than 3 level
                randID = GameController.getInstance().random.nextInt(rooms.size());
            rooms.get(randID).addItem(itemsToAdd.get(i));                               //adding item to room
            itemsToAdd.get(i).addObserver(rooms.get(randID));
            lastID = randID;                                                            //saving the last room's ID
        }
    }

    /**
     *  Hozzá ad a térképhez adott mennyiségű tanárt és takarítót figyelembe véve,
     *  hogy ne tegyen 2x ugyanoda entitást egymás után és az ID egyedi legyen az összes entitásra (ide értve a diákokat is)
     */
    public void entityPlacer(ArrayList<Entity> students, int teacherCount, int janitorCount){
        for (int i = 0; i < students.size(); i++) {
            students.get(i).setCurrentRoom(rooms.get(0));
            rooms.get(0).addEntity(students.get(i));
        }

        int lastID = -1, randID = -1;
        for (int i = 0; i < teacherCount; i++) {
            while ((randID == lastID) || randID == 0)
                randID = GameController.getInstance().random.nextInt(rooms.size());
            Teacher addTeach = new Teacher("", GameController.getInstance().getNextGlobalID(), rooms.get(randID));
            addTeach.setName(addTeach.randomName());
            GameController.getInstance().addEntity(addTeach);
            rooms.get(randID).addEntity(addTeach);
            lastID = randID;
        }
        lastID = -1; randID = -1;
        for (int i = 0; i < janitorCount; i++) {
            while ((randID == lastID) || randID == 0)
                randID = GameController.getInstance().random.nextInt(rooms.size());
            Janitor addJan = new Janitor("", GameController.getInstance().getNextGlobalID(),rooms.get(randID));
            addJan.setName(addJan.randomName());
            GameController.getInstance().addEntity(addJan);
            rooms.get(randID).addEntity(addJan);
            lastID = randID;
        }
    }

    /**
     * Vissza adja hogy a 2 szoba (index alapján) között van-e ajtó
     * @param r1Index - első szoba indexe
     * @param r2Index - második szoba indexe
     * @return - két szoba közti ajtó logikai értéke
     */
    private boolean hasDoor(int r1Index, int r2Index){
        ArrayList<Door> doors = rooms.get(r1Index).getDoors();
        for (int i = 0; i < doors.size(); i++){
            if(doors.get(i).containsRoom(rooms.get(r2Index))) return true;
        }
        return false;
    }

    /**
     * Beállítható mennyiségű random különböző ID-t hoz létre egy rangen
     * @param count - mennyiség
     * @param range - tartomány
     * @return - visszaad egy int[]-t tele random számokkal
     */
    private HashSet<Integer> getRandDiffIndexes(int count, int range){
        int randIndex = -1;
        HashSet<Integer> indexes = new HashSet<>();
        boolean sameIndex = true;
        while(indexes.size() != count){
            indexes.add(GameController.getInstance().random.nextInt());
        }
        return indexes;
    }

    /**
     * Összeolvaszt megadható mennyiségű szobát
     * @param count - mennyiség
     * @return - ha nem nagyobb a mennyiség mint a szobák száma "true" a visszatérési érték
     */
    public boolean mergeMany(int count){
        if(rooms.size() < (count)) return false;
        else{
            HashSet<Integer> roomIDs = getRandDiffIndexes(count, rooms.size() - 1);
            Room mergee = null;
            for (int id : roomIDs) {
                mergee = rooms.get(id);
                Room neighbour = mergee.getFirstNeighbour();
                if(mergee != null) {
                    mergee.merge(neighbour);
                    rooms.remove(neighbour);
                }
            }
            return true;
        }
    }

    /**
     * Megadható mennyiségű random szobát választ ketté, ha lehetséges
     * @param count - szobák mennyisége
     * @return - amennyiben nem nagyobb a mennyiség mint a szobák száma "true"-val térv vissza
     */
    public boolean splitMany(int count){
        if(rooms.size() < (count)) return false;
        else{
            HashSet<Integer> roomIDs = getRandDiffIndexes(count, rooms.size() - 1);
            for (int id : roomIDs) {
                Room newR = roomById(id).split();
                if(newR != null) rooms.add(newR);
            }
            return true;
        }
    }

    /**
     * Megadható mennyiségű, különböző szoba összes ajtaját elátkozottá teszi
     * @param count - mennyiség
     * @return - ha nem nagyobb a mennyiség mint a szobák össz. száma "true" értékkel tér vissza
     */
    public boolean curseMany(int count){
        if(rooms.size() < (count)) return false;
        else{
            HashSet<Integer> roomIDs = getRandDiffIndexes(count, rooms.size() - 1);
            for (int id : roomIDs) {
                roomById(id).curseAllDoors();
            }
            return true;
        }
    }

    /**
     * A térkép összes szobájának jelzi, hogy új kör kezdődött.
     */
    public void tickRooms() { for(Room room : rooms) room.tick(); }
    /**
     * Visszatér a paraméterként megadott ID-vel rendelkező szoba referenciájával.
     * Ha nem talál szobát a kapott ID-hez, akkor NullPointerException-t dob, annak megadva a kapott ID-t
     * @param id - az ID amihez keressük a szobát
     * @return - a szoba referenciája
     */
    public Room roomById(int id){
        for(Room room : rooms){
            if(room.getID() == id)
                return room;
        }
        //Ha nem találunk szobát ilyen ID-vel, akkor exceptiont dobunk, aminek az üzenetébe beírjuk a kapott ID-t
        return null;
    }


    /**
     * Debug szöveg generálása
     * @return - Az objektum állapotának szöveges reprezentációja
     */
    @Override
    public String debug() {
        String retVal = "---- GameMap.Map ----\nrooms : \n";
        for(Room r : this.rooms){
            retVal += r.debug();
        }
        retVal += "---- GameMap.Map ----\n";
        return retVal;
    }
}
