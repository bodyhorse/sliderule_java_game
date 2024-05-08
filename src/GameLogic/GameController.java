package GameLogic;

import GameLogic.*;
import GameMap.*;
import Entity.*;
import Item.*;
import Interfaces.*;
import Views.GameView;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GameController extends Observable implements Serializable, Observer {
	
	/********************
	 ********Misc******** 
	 *********************/

    /**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Az egyetlen létező instance-e a GameLogic.GameController-nek
     */
    private static GameController instance;
    
    
    /************************
	 ********Gameplay******** 
	 ************************/
    
    /**
     * A játékból fennmaradó körök száma
     */
    private int remainingRounds;
    
    //----------------Entity.Entity---------------------
    /**
     * A játékba beregisztrált diákok száma.
     */
    private int countOfPlayers;
    
    /**
     * Azon diákok száma akik elvesztették a lelküket, azaz nem végezhetnek több cselekvést a játékban.
     */
    private int playersWithoutSoul;
    
    /**
     * A játék futása közben épp soron lévő Entity.Entity referenciája
     */
    public Entity currentEntity;
    
    /**
	 * A játékban szereplő összes Enitiy listája
	 */
    private ArrayList<Entity> entities;
    
    //----------------Item.Item---------------------
    
    /**
     * A térképen található romladnó tárgyak
     */
    private ArrayList<Decaying> decayingItems;
    
    //----------------GameMap.Map---------------------
    
    /**
     * A térkép amelyen a jelenlegi játék játszódik
     */
    private Map map;
    
    /******************************
	 ********GameLogic.GameController********
	 ******************************/
    
    //----------------Collections---------------------

    /**
     * Hashmap az összes debugolható objektum tárolására
     */
    public HashMap<Integer, Debuggable> debuggableObjects;

    /**
     * Csak menüben hívható command-ok
     */
    public HashMap<String,Runnable> menuCommands;
    
    /**
     * Csak a játékmenet során hívható command-ok
     */
    public HashMap<String,Runnable> gameCommands;
    
    /**
     * Csak a teszt inicializálása során hívható command-ok
     */
    public HashMap<String,Runnable> testCommands;
    
    /**
     * Bemenetek
     */
    public ArrayList<String> testInputs;
    
    //----------------Gamestate---------------------
    
    /**
     * A játék elindítását figyeli, parancsok kezeléséhez, alapból false, lefutó start állítja true-ra
     */
    public boolean gameStarted;
    
    /**
     * Logikai értéke annak, hogy a játék tesztelési módban fut-e
     */
    public boolean testmode;
    
    /**
     * Logikai érték annak, hogy a játékban az Entityket kézzel irányítjuk-e
     */
    public boolean manualInputMode;
    
    /**
     * Logikai értéke annak, hogy a jelenlegi játék véget ért-e-
     * Szükséges a főmenübe visszatéréshez, miután egy játékmenet végetért.
     */
    public boolean gameIsOver;
    
    //----------------Util---------------------
    
    /**
     * A játék véletlen elemeinek forrásaként szolgáló Random pédány
     */
    public Random random;
    
    /**
     * A felhasználó általi input bekéréséért felelős Scanner objektum
     */
    public Scanner scanner;
    
    /**
     * A kovetkezo ID, amit student kaphat.
     */
    private int globalID;

    /**
     * Kiírás helyét jelző flag
     */
    private boolean printToConsole;

    /**
     * Kiírást végző FileWriter
     */
    private FileWriter fileWriter;

    /**
     * Kiíró nyitottságát ellenőrző flag
     */
    private boolean writerClosed;

    /**
     * Az utolsó konzol üzenet eltárolása
     */
    public String lastOutMessage;

    /**
     * Alap konstruktor az instance létrehozásához.
     */
    protected GameController(){
        remainingRounds = 0;
        countOfPlayers = 0;
        playersWithoutSoul = 0;

        entities = new ArrayList<>();
        decayingItems = new ArrayList<>();
        debuggableObjects = new HashMap<>();
        menuCommands = new HashMap<>();
        gameCommands = new HashMap<>();
        testCommands = new HashMap<>();
        testInputs = new ArrayList<>();

        gameStarted = false;
        testmode = false;
        manualInputMode = false;
        gameIsOver = false;

        random = new Random();
        globalID = 1;
        printToConsole = true;
        writerClosed = true;

        menuCommands.put("debug", this::debug);
        menuCommands.put("start", this::start);
        menuCommands.put("load", this::load);
        menuCommands.put("execute", this::execute);
        menuCommands.put("addplayer", this::addplayer);
        menuCommands.put("removeplayer", this::removeplayer);
        menuCommands.put("showplayers", this::showplayers);
        menuCommands.put("exit", this::exit);
        menuCommands.put("help", this::help);

        gameCommands.put("debug", this::debug);
        gameCommands.put("save", this::save);
        gameCommands.put("show", this::show);
        gameCommands.put("pickup", this::pickup);
        gameCommands.put("drop", this::drop);
        gameCommands.put("use", this::use);
        gameCommands.put("move", this::move);
        gameCommands.put("connect", this::connect);
        gameCommands.put("disconnect", this::disconnect);
        gameCommands.put("teleport", this::teleport);
        gameCommands.put("exit", this::exit);
        gameCommands.put("help", this::help);

        testCommands.put("debug", this::debug);
        testCommands.put("start", this::start);
        testCommands.put("execute", this::execute);
        testCommands.put("taddroom", this::taddroom);
        testCommands.put("tadddoor", this::tadddoor);
        testCommands.put("tadditem", this::tadditem);
        testCommands.put("taddentity", this::taddentity);
        testCommands.put("tmerge", this::tmerge);
        testCommands.put("tsplit", this::tsplit);
        testCommands.put("tcurse", this::tcurse);
        testCommands.put("tuncurse", this::tuncurse);
        testCommands.put("executetest", this::executetest);
        testCommands.put("exit", this::exit);
        testCommands.put("help", this::help);

        //Experimental rész
        testCommands.put("addplayer", this::addplayer);
        testCommands.put("removeplayer", this::removeplayer);
        testCommands.put("showplayers", this::showplayers);
        testCommands.put("save", this::save);
        testCommands.put("load", this::load);
    }

    
    //----------------Input---------------------
    
    /**
     * A legutolsó beolvasott command
     */
    public String lastInput;

    /**
     * A következő test parancs helye
     */
    public int nextTestCommand;


    /*******************************
	 ********Getters&Setters******** 
	 *******************************/

    /**
     * Visszaadja a következő egyedi azonosítót amit objektum kaphat.
     * @return Szigorúan egyedi, ID-nak adható egész szám
     */
    public int getNextGlobalID(){
        return globalID++;
    }

    public void setLastInput(String newInput){
        lastInput = newInput;
    }
    
    /**
     * Visszatér a soron következő teszt paranccsal
     * @return - a következő teszt parancs
     */
    public String nextTestLine(){
        if(testInputs.isEmpty()) {
            gameIsOver = true;
            return "debug 0";
        }
        else{
            return testInputs.get(nextTestCommand++);
        }
    }
    
    /**
     * A GameLogic.GameController osztály instance elérése
     * @return - Az instance
     */
    public static GameController getInstance(){
        if(instance == null)
            instance = new GameController();
        return instance;
    }
    
    /**
     * Vissza adja a játék hátralevő köreinek a számát
     * @return - hátrlévő körök száma
     */
    public int getRemainingRounds(){
        return remainingRounds;
    }

    /**
     * Beállítja a hátralévő körök számát
     * @param newRemainingVal - hátralévő körök száma
     */
    public void setRemainingRounds(int newRemainingVal){
        if(newRemainingVal >= 0) remainingRounds = newRemainingVal;
        else remainingRounds = 0;
    }

    /**
     * Entity.Entity hozzáaadására használt függgvény
     * @param nEntity - a hozzáadandó entity
     */
    public void addEntity(Entity nEntity){ entities.add(nEntity); }
    
    /**
     * Megnöveli a lélekkel már nem rendelkező diákok számát.
     */
    public void addALostSoul() {playersWithoutSoul++;}

    /**
     * Hozzá ad egy tárgyat a romladnó tárgyak listájához
     * @param newItem - hozzá adandó tárgy
     */
    public void addDecayingItem(Decaying newItem){
    	if(!decayingItems.contains(newItem)) decayingItems.add(newItem);
    }

    /**
     * Eltávolít egy tárgyat a romladnó tárgyak listájából
     * @param itemToRemove - romlandó tárgy
     */
    public void removeDecayingItem(Decaying itemToRemove){
        decayingItems.remove(itemToRemove);
    }
    
    
    /*******************************
	 ********User interface********* 
	 *******************************/

    /**
     * Az executeTest-ben beállított helyere írja ki a különböző, felhasználónak szánt üzeneteket. Egy egész sort ír ki, sorlezárással
     * Alapértelmezett esetben Console-ra ír ki, de képes fileba is írni. Ilyenkor a "consoleOutput.txt" nevű fileba írja ki az üzeneteket
     * @param outPut - a kiírandó String
     */
    public void println(String outPut){
        lastOutMessage = outPut;
        if(printToConsole){
            System.out.println(outPut);
        }
        else{
            if(!writerClosed){
                try{
                    fileWriter.write(outPut + "\n");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println(outPut);
            }
        }
    }

    /**
     * Az executeTest-ben beállított helyere írja ki a különböző, felhasználónak szánt üzeneteket. Egy egész sort ír ki, sorlezárás nélkül.
     * Alapértelmezett esetben Console-ra ír ki, de képes fileba is írni. Ilyenkor a "consoleOutput.txt" nevű fileba írja ki az üzeneteket.
     * @param outPut - a kiírandó String
     */
    public void print(String outPut){
        lastOutMessage = outPut;
        if(printToConsole){
            System.out.println(outPut);
        }
        else{
            if(!writerClosed){
                try{
                    fileWriter.write(outPut);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else{
                System.out.print(outPut);
            }
        }
    }

    /**
     * Grafikus indításra szolgáló függvény
     */
    public void initGui(){
        GameView frame = new GameView();
        frame.setVisible(true);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }

    /**
     * A fömenütől egészen a játék indításáig kezeli a felhasználói inputot
     */
    public void mainMenu(){
    	String mainMenuInput = null;
    	scanner = new Scanner(System.in);
    	map = new Map(0);
    	
    	
    	//Külső do loop egészen addig, amíg a játékos ki nem akar lépni
    	do {
    		clearMap();
            printToConsole = true;
    		println("""
     			   (       (                         (            
     			   )\\      )\\      (  (      )  (    )\\   (       
     			((((_)(   ((_) (   )\\))(  ( /(  )(  ((_) ))\\  (   
     			 )\\ _ )\\   _   )\\ ((_))\\  )(_))(()\\  _  /((_) )\\  
     			 (_)_\\(_) | | ((_) (()(_)((_)_  ((_)| |(_))  ((_) 
     			  / _ \\   | |/ _ \\/ _` | / _` || '_|| |/ -_)/ _|  
     			 /_/ \\_\\  |_|\\___/\\__, | \\__,_||_|  |_|\\___|\\__|  
     			                  |___/                         
     			                    
     			                 [Main Menu]""");
    		gameStarted = false;
    		gameIsOver = false;
    		
    		//A teszt módba való lépés eldöntésére van, amíg nem 'y' vagy 'n' az input, addig kérdezgeti
            println("Do you wish to run the game in test mode [y/n]?");
            boolean flag = false;
            do {
                println("[y/n]>");
                mainMenuInput = scanner.nextLine().strip();
                if(mainMenuInput.equals("y")){
                	testmode = true;
                    map = new Map(0);
                    flag = true;
                }
                if(mainMenuInput.equals("n")){
                    flag = true;
                }
            }while(!flag);
            
            //Belső do egészen addig ameddig egy játék vagy teszt véget nem ér
        	do {
                if(testmode) println("\n[testmode]\nMain menu>");
        		else println("\nMain menu>");
        		mainMenuInput =  scanner.nextLine().strip();
        		terminal(mainMenuInput);
        	}while (!gameIsOver);
    		
    	}while(!mainMenuInput.equals("exit"));
    }
    
    /**
     * A játék összes inputjának végrehajtását kezeli
     * @param input A végrehajtandó parancs teljes sora
     */
    public void terminal(String input){
    	
    	String[] tokenizedInput = input.strip().split(" ");
    	
    	HashMap<String, Runnable> commandsToUse;
    	
    	//Az állapotok alapján meg kell határozni melyik map-ből kell a commandot végrehajtani
    	if(gameStarted) commandsToUse = gameCommands;
    	else if(testmode)commandsToUse = testCommands;
    	else commandsToUse = menuCommands;
    	
    	
    	if(tokenizedInput.length > 0) {
    		//Vizsgálat arra, hogy a command létezik-e egyáltalán
    		boolean isExistentCommand = gameCommands.containsKey(tokenizedInput[0]) || menuCommands.containsKey(tokenizedInput[0]) || testCommands.containsKey(tokenizedInput[0]);
    		
    		//Vizsgálat arra, hogy a játék jelenlegi állapotában használható-e a command
    		boolean isUsableCommand = commandsToUse.containsKey(tokenizedInput[0]);
    		
    		if(isExistentCommand && isUsableCommand) {
    			lastInput = input;
    			commandsToUse.get(tokenizedInput[0]).run();
    		}else if(isExistentCommand && !isUsableCommand) errorMsg(1, null);
    		else errorMsg(0, null);
    	}
    }
    
    /**
     * A játék megnyerését kezelő metódus
     */
    public void gameWon(){
        println("""
                
                                
                ____    ____  ______    __    __     ____    __    ____  __  .__   __.  __ \s
                \\   \\  /   / /  __  \\  |  |  |  |    \\   \\  /  \\  /   / |  | |  \\ |  | |  |\s
                 \\   \\/   / |  |  |  | |  |  |  |     \\   \\/    \\/   /  |  | |   \\|  | |  |\s
                  \\_    _/  |  |  |  | |  |  |  |      \\            /   |  | |  . `  | |  |\s
                    |  |    |  `--'  | |  `--'  |       \\    /\\    /    |  | |  |\\   | |__|\s
                    |__|     \\______/   \\______/         \\__/  \\__/     |__| |__| \\__| (__)\s
                                                                                           \s
                                
                """);
        if(testmode)
            terminal("debug 0");
        println("[The students have found the sliderule! GG WP!]\nPress enter to continue>");
        gameIsOver = true;
        if(manualInputMode || !testmode) scanner.nextLine();

    }
    
    /**
     * A játék elvesztését kezelő metódus
     * @param type A játék végének az oka:
     * 0 - Az összes diák elvesztette lelkét;
     * 1 - lejártak a körök.
     */
    public void gameLost(int type){
    	println(""" 
    			   _____                         ____                 
    			  / ____|                       / __ \\                
    			 | |  __  __ _ _ __ ___   ___  | |  | |_   _____ _ __ 
    			 | | |_ |/ _` | '_ ` _ \\ / _ \\ | |  | \\ \\ / / _ \\ '__|
    			 | |__| | (_| | | | | | |  __/ | |__| |\\ V /  __/ |   
    			  \\_____|\\__,_|_| |_| |_|\\___|  \\____/  \\_/ \\___|_|                                      
    			""");
    	if(testmode)
            terminal("debug 0");
    	String message = (type == 0) ? "[The teachers achieved their goal, all students have lost their soul!]" : "[The students couldn’t find the slide rule in time.]";
    	println(message);
        println("Press enter to continue>");
    	gameIsOver = true;
    	if(manualInputMode || !testmode) scanner.nextLine();
    }
    
    /**
     * Minden hibaüzeneteket tartalmazó és szabványos kimenetre ki író függvény.
     * @param errorType - hiba típusa
     * @param param - állítható paraméter hogy személyre lehessen szabni az egyes hibaüzeneteket
     */
    public void errorMsg(int errorType, String param){
        print("\nERROR -> (" + errorType + ") ");
    	switch (errorType) {
            //000 Basic system errors
    	    case 0: {
			    println("Wrong syntax!");
			    break;
		    }
		    case 1: {
			    println("This command can not be called right now!");
			    break;
		    }
            case 3:{
                println("Only use one switch per command!");
                break;
            }
            case 4:{
                println("Nonexistent switch for this command!");
                break;
            }
            case 5:{
                println("This command can only be used in test mode!");
                break;
            }
            case 6:{
                println("This command can not be used is test mode!");
            }

            //100 Player
            case 101:{
                //param -> playerName
                println("Player named '" + param +"' already exists!");
                break;
            }
            case 102:{
                //param -> playerName
                println("There is no player named ‘" + param + "’!");
                break;
            }
            case 103:{
                println("Not enough players(2) to start!");
                break;
            }

            //200 Item.Item and inventory
            case 201:{
                println("Cannot use this item!");
                break;
            }
            case 202:{
                //param -> item#101
                println("While picking up " + param + ". Inventory is full!");
                break;
            }
            case 203:{
                //param -> item#101
                println("While picking up " + param + ". GameMap.Room is sticky!");
                break;
            }
            case 204:{
                //param -> item#101
                println("While dropping " + param + ". No such item in inventory!");
                break;
            }
            case 205:{
                //param -> #101
                println("While dropping Item.Mask " + param + ". GameMap.Room is toxic!");
                break;
            }
            case 206:{
                //param -> item#101
                println("While using " + param + ". It is already being used.");
                break;
            }
            case 207:{
                //param -> item#101
                println("While picking up " + param + ". No such item in room!");
                break;
            }
            case 208:{
                //param -> item#101
                println("While using " + param + ". No such item in inventory.");
                break;
            }
            case 209:{
                println("While picking up all items from room " + param + ". There is not enough free space in inventory.");
                break;
            }
            case 210:{
            	println("While picking up " + param + ". You're not the owner of this item!");
                break;
            }

            //300 Moving
            case 301:{
                //param -> room#101
                println("While moving to " + param + ". GameMap.Room is full!");
                break;
            }
            case 302:{
                //param -> room#101. GameMap.Door#11
                println("While moving to " + param + " doesn’t exist!");
                break;
            }
            case 303:{
                //param -> room#101. GameMap.Door#11
                println("While moving to room " + param + " cannot be used in this direction!");
                break;
            }
            case 304:{
                //param -> room#101. door#11
                println("While moving to " + param + " is cursed, and can not be used at this time!");
                break;
            }

            //400 File and debug
            case 401:{
                //param -> objectName
                println("Object " + param + " doesn’t exist!");
                break;
            }
            case 402:{
                println("File name not specified!");
                break;
            }
            case 403:{
                println("While saving file!");
                break;
            }
            case 404:{
                //param -> fileName
                println("While loading file " + param + ", file not found.");
                break;
            }
            case 405:{
                //param -> object id
                println("While writing debug: Object with ID " + param + " doesn't exist.");
                break;
            }
            case 406:{
                println("While writing debug file!");
                break;
            }

            //500 Item.Transistor
            case 501:{
                //param -> Item.Transistor #53 and Item.Transistor #42. Item.Transistor #44
                println("While connecting " + param + " is already connected.");
                break;
            }
            case 502:{
                //param -> Item.Transistor #53 and Item.TVSZ #76
                println("While connecting " + param + ". These items cannot be connected!");
                break;
            }
            case 503:{
                //param -> Item.Transistor #6 and Item.Beer #9
                println("While disconnecting " + param + ". They are not connected!");
                break;
            }
            case 504:{
                //param -> Item.Transistor #2
                println("While teleporting using " + param + ". Remote location is full!");
                break;
            }
            case 505:{
                //param -> Item.Transistor #32
                println("While teleporting with " + param + ". This transistor was not placed by you!");
                break;
            }
            case 506:{
                //param -> Item.Rag #32
                println("While teleporting with " + param + ". Cannot teleport with this item!");
                break;
            }

            //600 init language specific errors
            case 601:{
                //param -> a problémás szoba ID-je
                println("There is no room with ID " + param + ".");
                break;
            }
            case 602:{
                println("Cannot merge rooms!");
                break;
            }
            case 603:{
                println("Cannot split room!");
                break;
            }
            case 604:{
                println("Errors in test in the following line(s):");
                break;
            }
            case 605:{
                println("Error, file lengths do not match!");
                break;
            }
            case 606:{
                println("The conditions were not met to start the game in terminal mode!");
                break;
            }
		    default:
			    throw new IllegalArgumentException("Unexpected value: " + errorType);
        }
    }

    /**
     * Státusz üzeneteket tartalmazó és szabványos kimenetre ki író függvény.
     * @param statMsg - státusz üzenet típusa
     * @param param - állítható paraméter hogy személyre lehessen szabni az egyes státusz üzeneteket
     */
    public void statusMsg(int statMsg, String param){
        print("\n");
        switch (statMsg){

            //110 Player
            case 111:{
                //param -> playerName
                println("Player '" + param + "' added to the game!");
                break;
            }
            case 112:{
                //param -> playerName
                println("Player named ‘" + param + "’ removed from the game!");
                break;
            }
            case 113:{
                //param -> List of players (formatted)
                println("Listing all players who are added to the game:\n" + param );
                break;
            }
            case 114:{
                //param -> List of players (formatted)
                println("The game started with the following players:\n" + param);
                break;
            }
            case 115:{
                println("Game started in test mode!");
                break;
            }
            case 116:{
                println("<--- Usable commands at the moment --->\n" + param);
                break;
            }

            //210 -> Item.Item and inventory
            //220 -> show command
            case 211:{
                //param -> beer#211
                println("Picked up " + param + "!");
                break;
            }
            case 212:{
                //param -> beer#186
                println("Dropped " + param + "!");
                break;
            }
            case 213:{
                //param -> beer#111
                println("Used " + param + "!");
                break;
            }
            case 214:{
                //param -> beer#111
                println("Picked up all items from room " + param);
                break;
            }
            case 220:{
                //param -> List of player inventory (formatted)
                println("Listing player's items!\n" + "Inventory:\n" + param);
                break;
            }
            case 221:{
                //param -> List of doors (formatted)
                println("Listing all doors in current room!\n" + "Doors:\n" + param);
                break;
            }
            case 222:{
                //param -> List of entities (formatted)
                println("Listing entities in current room!\n" + "Entities:\n" + param);
                break;
            }
            case 223:{
                //param -> List of room inventory (formatted)
                println("Listing items in current room!\n" + "Items:\n" + param);
                break;
            }
            case 224:{
                //param -> List of teleport possibilities
                println("Listing all teleportation possibilities!\n" + "Rooms:\n"  + param);
                break;
            }

            //310 Moving
            case 311:{
                //param -> room#111
                println("Successfully stayed in " + param + "!");
                break;
            }
            case 312:{
                //param -> room#101
                println("Successfully moved to " + param + "!");
                break;
            }

            //410 File and debug
            case 411:{
                //param -> Content of debug.txt
                println("Successfully wrote debug to “debug.txt”\n" + "Objects dumped:\n" + param);
                break;
            }
            case 412:{
                //param -> fileName
                println("File ‘" + param + "’ successfully loaded!");
                break;
            }
            case 413:{
                //param -> fileName
                println("Progress saved, file named ‘" + param + "’.");
                break;
            }
            case 414:{
                println("Progress saved, file named ‘" + param + "’.");
                break;
            }

            //510 Item.Transistor
            case 511:{
                //param -> transistor #64 and transistor #9
                println("Connected " + param + "!");
                break;
            }
            case 512:{
                //param -> transistor #32 and transistor #27
                println("Disconnected " + param + "!");
                break;
            }
            case 513:{
                //param -> transistor #219 to room #888
                println("Teleported using " + param + "!");
                break;
            }

            //600 init commands
            case 601:{
                println("GameMap.Room succesfully added!");
                break;
            }
            case 602:{
                println("Item.Item succesfully added!");
                break;
            }
            case 603:{
                println("Entity.Student successfully added!");
                break;
            }
            case 604:{
                println("Entity.Teacher successfully added!");
                break;
            }
            case 605:{
                println("Entity.Janitor successfully added!");
                break;
            }
            case 606:{
                println("GameMap.Door succesfully added!");
                break;
            }
            case 607:{
                println("The map has been cleared, ready for new command!");
                break;
            }
            case 608:{
                println("Merge successful");
                break;
            }
            case 609:{
                println("Split successful, ID of new room: #" + param);
                break;
            }
            case 610:{
                println("Test successful!");
                break;
            }
            case 611:{
                println("Curse succesful, all doors are cursed in GameMap.Room#" + param + "!");
                break;
            }
            case 612:{
                println("Uncurse succesful, all doors are uncursed in GameMap.Room#" + param + "!");
                break;
            }

            default:
                throw new IllegalArgumentException("Unexpected value: " + statMsg);
        }
    }

    /**************************
	 ********GameLogic********* 
	 **************************/
    
    /**
     * A játék elindításáért felel, a start parancs végén hívandó, ha az indítási feltételek adottak.
     * Addig híja újra körönként a newRound-ot ameddig a játékosoknak maradt még hátralévő köre.
     */
    public void runGame(){
    	if(gameIsOver) return;
    	
        random.setSeed(1);
       
        for(int i = 0; i < remainingRounds; i++){
            if(gameIsOver) return;
            newRound();
        }
        gameLost(1);
    }
    
    /**
     * A játék egy fordulóját bonyolítja le. Véletlenszerűen hajt végre térkép modifikációkat,
     * öregíti azokata a tárgyakat amelyeket szükséges, jelez a szobáknak, hogy új forduló kezdődött,
     * valamint átadja az irányítást sorban a játék Eniityjeinek.
     */
     public void newRound(){
    	 
    	 if(gameIsOver) return;
        	    	
    	int countOfRooms = map.rooms.size();
        	
        if(countOfRooms >= 4 && !testmode) {
        	//Random mennyiségű szoba megátkozása
            map.curseMany(random.nextInt(countOfRooms / 4));
            	
            	
            //Random mennyiségű szoba splitelese
            map.splitMany(random.nextInt(countOfRooms / 4));
            	
            	
            	//Random mennyiségű szoba mergeelese
            map.mergeMany(random.nextInt(countOfRooms / 4));
        }
        	
        //Romlandó tárgyak öregítése egy körrel
        ageAll();
        	
        //Új kör kezdetének jelzése a szobáknak
        map.tickRooms();
     
      	//Új fordulón belül minden Entity.Entity lejátsza a körét
        for (int i = 0; i < entities.size(); i++){
            currentEntity = entities.get(i);
            entities.get(i).newEntRound();
        }
        	
       //Ha elfogytak a cselekvőképes játékosok, a játéknak vége
       if(countOfPlayers == playersWithoutSoul) gameLost(0);
    }
     
     /**
      * Minden romladnó tárgyat egy körrel öregít
      */
     public void ageAll(){
         for (Decaying item : decayingItems) item.age();
     }
    
    
    
    
     /********************************
 	 ********GameMap.Map manipulation*********
 	 *********************************/

    /**
     * Kitisztítja a jelenlegi állásban lévő map-et, és annak minden lényeges változóját / objektumát.
     * Miután ezt megtörtént, készít egy új map-et
     */
    public void clearMap(){
        entities.clear();
        map.rooms.clear();
        countOfPlayers = 0;
        decayingItems.clear();
        debuggableObjects.clear();
        testInputs.clear();
        nextTestCommand = 0;
        remainingRounds = 0;
        playersWithoutSoul = 0;
        currentEntity = null;
        map = new Map(0);
        globalID = 1;
    }

    /**
     * Vissza adja két összeolvasztáskor a keletkező szoba toxicitását
     * @param r1 - 1.szoba
     * @param r2 - 2. szoba
     * @return - toxicitás szám alapú értéke
     */
    public int newMergeState(Room r1,Room r2){
        int state = r1.getIsToxic() || r2.getIsToxic() ? 1 : 0;
        return state;
    }

    /**
     * Össze olvasztja 2 szoba ajtajainak tömbjeit, az első szobába
     * @param r1 - 1. szoba
     * @param r2 - 2. szoba
     */
    public void mergeDoors(Room r1,Room r2){
        for (int i = 0; i < r2.getDoors().size(); i++){
            Door tmpDoor = r2.getDoors().get(i);
            if((tmpDoor.getRoomOne() == r1  && tmpDoor.getRoomTwo() == r2) || (tmpDoor.getRoomOne() == r2  && tmpDoor.getRoomTwo() == r1)){
                r1.removeDoor(tmpDoor);
                r2.removeDoor(tmpDoor);
            }
            else{
                r1.addDoor(tmpDoor);
                r2.removeDoor(tmpDoor);
            }
        }
    }



    
    /*************************************
 	 ********User action handlers********* 
 	 *************************************/

    /**
     * Ki írja a kozolra az elérhető utasításokat az adott rétegben
     */
    public void help(){
        String listOfcomms = "";
        String[] split = lastInput.split(" ");

        if (split.length == 1){
            if(!gameStarted){
                for(String comm : menuCommands.keySet()){
                    listOfcomms += (comm + "\n");
                }
            } else if (manualInputMode) {
                for(String comm : testCommands.keySet()){
                    listOfcomms += (comm + "\n");
                }
            }
            else{
                for(String comm : gameCommands.keySet()){
                    listOfcomms += (comm + "\n");
                }
            }
        }
        else{
            errorMsg(0,"");
        }
        statusMsg(116,listOfcomms);
    }

    /**
     * Addplayer commandot kezelő függvény.
     */
    public void addplayer(){
        String[] split = lastInput.split(" ");

        if (split.length == 1){
            errorMsg(0,"");
            return;
        }
//
//        //Ha teszt módban vagyunk akkor nem lehet használni
//        if(testmode){
//            errorMsg(6, "");
//        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        int id = getNextGlobalID();
        String n = split[1];

        //már van ilyen nevű játékos
        for (int i = 0; i < entities.size(); i++){
            if (entities.get(i).getName().equals(n)){
                errorMsg(101, n);
                return;
            }
        }

        //sikeres hozzáadás
        entities.add(new Student(n, id, null));
        countOfPlayers++;
        statusMsg(111, split[1]);
    }

    /**
     * Függvény a "removepalyer" parancs működésére. Ha rosszul lett hívva, akkor visszatér hibaüzenettel. Ha jól lett hívva, akkor ha megtalálja név szerint a playert, akkor
     * kitörli, egyéb esetben hibával visszatér.
     *
     *
     *
     */
    public void removeplayer(){
        String[] split = lastInput.split(" ");

        //syntax error hiba
        if (split.length == 1){
            errorMsg(0,"");
            return;
        }

//        //Ha teszt módban vagyunk akkor nem lehet használni
//        if(testmode){
//            errorMsg(6, "");
//        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        for (int i = 0; i < entities.size(); i++){
            if (entities.get(i).getName().equals(split[1])){
                entities.remove(i);
                countOfPlayers--;
                statusMsg(112, split[1]);
                return;
            }
        }

        //nem talalt ilyen nevu player-t
        errorMsg(102, split[1]);        
    }
    
    /**
     * Függvény a 'showplayers' parancs működéséhez. Ellenőrzi, hogy jól, és helyes időpontban lett-e hívva.
     * Ha helytelenül hívták, akkor meghívja helyes paraméterekkel az errorMsg() függvényt, és visszatér.
     * Ha helyesen lett hívva, akkor készít egy Stringet a hozzáadott diákokkal, majd meghívja helyes paraméterekkel a
     * statusMsg() függvényt, mely kiírja a parancs eredményét.
     */
    public void showplayers(){
        //Részekre daraboljuk az inputot, ha több mint egy rész van, akkor error
        String[] splitInput = lastInput.split(" ");
        if(splitInput.length > 1) {
            errorMsg(0, "");
            return;
        }

//        //Ha teszt módban vagyunk akkor nem lehet használni
//        if(testmode){
//            errorMsg(6, "");
//        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        // Ha idáig eljutottunk, akkor helyesen és jókor lett hívva
        // Átadott string készítése
        String message = "";
        String section;
        for (Entity entity : entities) {
            section = entity.getName();
            message += "-" + section + "\n";
        }
        //Kiírás hívása
        statusMsg(113, message);
    }

    /**
     * Függvény a 'start' parancs működésére. Ellenőrzi, hogy jól, és helyes időpontban lett-e hívva.
     * Ha helytelenül hívták, akkor meghívja helyes paraméterekkel az errorMsg() függvényt, és visszatér.
     * Ha kettőnél kevesebb játékossal próbálják meg elindítani a játékot, akkor is egy helyesen paraméterezett erroMsg() függvényhívás következik.
     * Ha helyesen lett hívva, akkor készít egy Stringet a hozzáadott diákokkal, meghívja a játéktér építésére létrehozott függvénykeet,
     * majd meghívja a helyes paraméterekkel ellátott statusMsg() függvényt, mely kiírja a parancs eredményét.
     * Elvárt syntax normál módu futtatás esetén: 'start'
     * Elvárt syntax teszt módban való futtatás esetén: 'start [int: remainingRounds] [char: 't'/'m' -> 't' ha fileból szeretnénk mozgatni az entityket, 'm', ha kézzel szeretnénk]
     */
    public void start(){
        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted){
            errorMsg(1, "");
            return;
        }

        //Ha nem teszt módban vagyunk
        if(!testmode){
            //Részekre daraboljuk az inputot, ha több mint egy rész van, akkor error
            String[] splitInput = lastInput.split(" ");
            if (splitInput.length > 1){
                errorMsg(0, "");
                return;
            }

            //Ha nincs legalább kettő játékos, akkor error
            if(entities.size() < 2){
                errorMsg(103, "");
                return;
            }

            //Ha ide eljutottunk, akkor mehet az indítás
            //Átadott string készítése
            String message = "";
            String section;
            for (Entity entity : entities) {
                section = entity.getName();
                message += "\t-" + section + "\n";
            }

            map = new Map(0);
            map.buildMap(3, 4);
            map.fillWithItems(4,2);
            map.entityPlacer(entities, 2, 2);

            gameStarted = true;
            gameIsOver = false;
            playersWithoutSoul = 0;
            remainingRounds = 20;

            //Üzenet küldése
            statusMsg(114, message);

            //Játék futtatása
            runGame();
        }
        //Ha teszt módban vagyunk
        else{
            //Részekre daraboljuk az inputot, ha nem prontosan három rész van, akkor error
            String[] splitInput = lastInput.split(" ");
            if (splitInput.length != 3){
                errorMsg(0, "");
                return;
            }

            //Ha terminálról kéri be, akkor
            if(splitInput[2].equals("t")){
            	if(testInputs.isEmpty()) {
            		errorMsg(606, null);
            		return;
            	}
                manualInputMode = false;
            }
            else if (splitInput[2].equals("m")) {
                manualInputMode = true;
            }
            else{
                errorMsg(0, "");
                return;
            }

            //Megpróbáljuk elindítani a játékot
            try{
                gameStarted = true;
                playersWithoutSoul = 0;

                statusMsg(115, "");
                testRun(map, entities, countOfPlayers, Integer.parseInt(splitInput[1]));
            }
            catch (NumberFormatException ex){
                errorMsg(0, "");
                return;
            }
        }
    }

    /**
     *  Függvény a 'show' command megvalósítására. Ellenőrzi, hogy helyesen lett e meghívva, hogyha nem akkor hibaüzenetet küld az
     *  errorMsg() függvény segítségével. Ha helyesen lett hívva akkor a következő 5 lehetőség folyik le
     *      -i -> a currentEntity inventoryát listázza ki
     *      -d -> a currentEntity currentRoom-jának szobáit listázza ki
     *      -e -> a currentEntity currentRoom-jában lévő Entityket listázza ki
     *      -ri -> a currentEntity currentRoom-jában lévő Itemeket listázza ki
     *      -t -> a currentEntity teleportálási lehetőségeit listázza ki
     */
    public void show(){
        //Részekre daraboljuk az inputot, ha kevesebb mint 2, akkor error
        String[] splitInput = lastInput.split(" ");
        if (splitInput.length < 2){
            errorMsg(0, "");
            return;
        }

        //Ha több mint 2, akkor is error
        if (splitInput.length > 2){
            errorMsg(3, "");
            return;
        }

        //Ha a játék még nem indult el mikor hívják, akkor error
        if(!gameStarted){
            errorMsg(1, "");
            return;
        }

        //Ha itt vagyunk, akkor csak egy kapcsolónk van, ha az nem jó, akkor azt a default-ban kezeljük
        String message;
        switch (splitInput[1])
        {
            //Az inventory listázása meghívja a currentEntity listItems() függvényét, és meghívja a statusMsg-t
            case "-i" : {
                message = currentEntity.listItems();
                statusMsg(220, message);
                break;
            }
            //Meghívja a jelenlegi entity current roomján a listDoors() fgv-t, és meghívja a statusMsg()-t
            case "-d" : {
                message = currentEntity.getCurrentRoom().listDoors();
                statusMsg(221, message);
                break;
            }
            //Meghívja a jelenlegi entity current roomján a listEntities() fgv-nyt és a statusMsg-t
            case "-e" : {
                message = currentEntity.getCurrentRoom().listEntities();
                statusMsg(222, message);
                break;
            }
            //Meghívja a currentRoom listItems() fgv-ét
            case "-ri" : {
                message = currentEntity.getCurrentRoom().listItems();
                statusMsg(223, message);
                break;
            }
            //Meghívja a currentEntity listTeleport() függvényét
            case "-t" : {
                message = currentEntity.listTeleport();
                statusMsg(224, message);
                break;
            }
            //nem létezik ilyen switch -> error
            default : {
                errorMsg(4, "");
                return;
            }
        }

    }

    /**
     * Ez a függvény teszi lehetővé, hogy felvegyünk egy tárgyat
     */
    public void pickup(){
        boolean pickupAll = false;
        //Ha a játék még nem indult el mikor hívják, akkor error
        if(!gameStarted){
            errorMsg(1, "");
            return;
        }
        String[] splitInput = lastInput.split(" ");
        //ha nem megfelelő a bemeneti formátum:
        if(splitInput.length > 3 || splitInput.length < 2)
        {
            errorMsg(0, null);
            return;
        }
        if(splitInput.length == 3 && !splitInput[2].equals("-a")){
            errorMsg(4, null);
            return;
        }
        else if(splitInput.length == 3 && splitInput[2].equals("-a")){
            pickupAll = true;
        }
        
        //logic fn
        currentEntity.getCurrentRoom().initiatePickup(pickupAll, currentEntity, splitInput, currentEntity.getInventory().size());
    }

    /**
     * Ez a függvény teszi lehetővé, hogy eldobjunk egy tárgyat
     */
    public void drop(){
        //Ha a játék még nem indult el mikor hívják, akkor error
        if(!gameStarted){
            errorMsg(1, "");
            return;
        }
        String[] splitInput = lastInput.split(" ");
        if(splitInput.length > 3 || splitInput.length < 2)
        {
            errorMsg(0, null);
            return;
        }
        //ha rossz flaget kap a parancs
        else if(splitInput.length == 3 && !splitInput[2].equals("-a")){
            errorMsg(4, null);
            return;
        }
        //logic
        currentEntity.initiateDrop(splitInput);
    }

     /**
      * Ez a függvény teszi lehetővé, hogy használjunk egy tárgyat
    */
    public void use(){
        //Ha a játék még nem indult el mikor hívják, akkor error
        if(!gameStarted){
            errorMsg(1, "");
            return;
        }
        String[] splitInput = lastInput.split(" ");
        //ha túl sok paramétert kap a parancs
        if (splitInput.length != 2) {
            errorMsg(0, null);
        }
        else{
            currentEntity.initiateUse(splitInput);
        }
    }

    
    /**
     * A játékos által kiadott move parancs végrehajtására szolgáló metódus
     */
    public void move(){
    	String[] tokenizedInput = lastInput.strip().split(" ");
    	
    	if(tokenizedInput.length == 1) {
    		
    		currentEntity.getCurrentRoom().initiateMove(-1, currentEntity);
    		
    	}else if(tokenizedInput.length > 2) {
    		
    		errorMsg(0, null);
    		
    	}else {
    		
    		try {
    			int doorId = Integer.parseInt(tokenizedInput[1]);
        		currentEntity.getCurrentRoom().initiateMove(doorId, currentEntity);
        		
     		}catch (NumberFormatException e) {
     			
				errorMsg(302, "DoorId: " + tokenizedInput[1]);
			}
    	}
    }
    
    /**
     * A játékos által kiadott connect parancs végrehajtására szolgáló metódus
     */
    public void connect(){
        String[] split = lastInput.split(" ");

        //wrong syntax
        if (split.length < 3){
            errorMsg(0, "");
            return;
        }

        currentEntity.initiateConnect(split);
    }

    /**
     * A játékos által kiadott disconnect parancs végrehajtására szolgáló metódus
     */
    public void disconnect(){
        String[] split = lastInput.split(" ");

        //wrong syntax
        if (split.length < 3){
            errorMsg(0, "");
            return;
        }

        currentEntity.initiateDisconnect(split);
    }

    /**
     * A játékos által kiadott teleport parancs végrehajtására szolgáló metódus
     */
    public void teleport(){
        String[] split = lastInput.split(" ");

        //wrong syntax
        if (split.length < 2){
            errorMsg(0, "");
            return;
        }

        currentEntity.initiateTeleport(split);
    }
    
    /**
     * Függvény a 'save' parancs megvaalósítására. Szerializáció használatával elmentjük a játék jelenlegi állását. Rossz paraméteretés sesetén hibát dob
     * Ha nem lett specifikálva mentési fájlnév, akkor az alap 'mentes' fájlba menti el a jelenlegi állást.
     * Elvárt syntax alap mentési helynél: 'save'
     * Elvárt syntax specifikált mentési helynél: 'save [fileName]'
     */
    public void save(){
        //Alap mentesi hely
        String fileName = "mentes.ser";

        //Részekre daraboljuk az inputot, ha több mint kettő rész van, akkor error
        String[] splitInput = lastInput.split(" ");
        if(splitInput.length > 2) {
            errorMsg(0, "");
            return;
        }

        //Ha a játék még nem indult el mikor hívják, akkor error
        if(!gameStarted){
            errorMsg(1, "");
            return;
        }

        //Ha több mint egy rész van -> megadott filenév
        if(splitInput.length == 2)
            fileName = splitInput[1] + ".ser";

        //Megpróbáljuk a szerializációt
        try{
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(remainingRounds);
            out.writeObject(countOfPlayers);
            out.writeObject(playersWithoutSoul);
            out.writeObject(currentEntity);
            out.writeObject(entities);
            out.writeObject(decayingItems);
            out.writeObject(map);
            out.writeObject(debuggableObjects);
            out.writeObject(gameStarted);
            out.writeObject(testmode);
            out.writeObject(manualInputMode);
            out.writeObject(gameIsOver);
            out.writeObject(random);
            out.writeObject(globalID);

            out.close();
            file.close();

            statusMsg(414, fileName);
        }
        catch (IOException ex){
            errorMsg(403, "");
        }
    }
    
    /**
     * Egy játékmenet betöltésére használjuk. Ellenőrzi, hogy helyesen lett-e hívva, ha nem akkor error-t dob.
     * Ha helyesen lett hívva, akkor a megadott filenévből deszerializálja az elmentett adatokat.
     * Betöltési hiba esetén error üzenettel jelzi.
     */
    public void load(){
        //Részekre daraboljuk az inputot, ha kevesebb mint kettő rész van, akkor error
        String[] splitInput = lastInput.split(" ");
        if(splitInput.length < 2) {
            errorMsg(402, "");
            return;
        }

        //Ha több mint kettő rész van, akkor is error
        if(splitInput.length > 2) {
            errorMsg(0, "");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted){
            errorMsg(1, "");
            return;
        }

        //Ha itt vagyunk, akkor legalább a syntax jó
        String fileName = splitInput[1] + ".ser";
        try{
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);

            remainingRounds = (int) in.readObject();
            countOfPlayers = (int) in.readObject();
            playersWithoutSoul = (int) in.readObject();
            currentEntity = (Entity) in.readObject();
            entities = (ArrayList<Entity>) in.readObject();
            decayingItems = (ArrayList<Decaying>) in.readObject();
            map = (Map) in.readObject();
            debuggableObjects = (HashMap<Integer, Debuggable>) in.readObject();
            gameStarted = (boolean) in.readObject();
            testmode = (boolean) in.readObject();
            manualInputMode = (boolean) in.readObject();
            gameIsOver = (boolean) in.readObject();
            random = (Random) in.readObject();
            globalID = (int) in.readObject();


            statusMsg(412, splitInput[1]);

            runGame();
        }
        catch (IOException | ClassNotFoundException ex){
            errorMsg(404, splitInput[1]);
        }

    }
    
    /**
     * A játékból (pontosabban a teljes programból) kilépést végreható metódus. A kilépés előtt
     * felajánlja a játék jelenlegi állánának mentési lehetőségét.
     */
    public void exit() {
    	String exitInput = null;
        println("Do you want to save your progress before exiting?");
        boolean flag = false;
        do {
            println("[y/n]>");
            exitInput = scanner.nextLine().strip();
            if(exitInput.equals("y")){
            	save();
                flag = true;
            }
            if(exitInput.equals("n")){
                flag = true;
            }
        }while(!flag);
        
    	scanner.close();
    	
    	System.exit(0);
    }

    
    
    
    
    /*************************************
 	 ********User test handlers********* 
 	 *************************************/

    /**
     * Lehetővé teszi a játék futtatását előre meghatározott körülmények mellett.
     * Nem végez input helyesség ellenőrzést!
     * @param map Egy előre összeállított térkép
     * @param entities A játékban szereplő összes Entity.Entity
     * @param countOfPlayers A játékot játszó diákok száma (npc-k nélkül)
     * @param remainingRounds A játékból hátralévő körök száma
     */
    public void testRun(Map map, ArrayList<Entity> entities, int countOfPlayers ,int remainingRounds) {
    	GameController.getInstance().map = map;
    	GameController.getInstance().entities = entities;
    	GameController.getInstance().remainingRounds = remainingRounds;
    	scanner = new Scanner(System.in);
    	
    	testmode = true;
    	
    	gameStarted = true;
    	
    	GameController.getInstance().countOfPlayers = countOfPlayers;
    	playersWithoutSoul = 0;
    	
    	runGame();
    }

    /**
     * Lehetővé teszi a játék állásának ellenőrzését szöveges kimeneten keresztül.
     * Megnyit egy debug.txt file-t, ebbe beleírja a debugolandó objektumot sztringes állapotát. Az ID alapján a debuggableObjects<ID,OBJ>-ból kiveszi a keresett objektumot, majd a kimenethez hozzáfűzi a sztringes reprezentációt.
     * a functió rekurzív, azaz pl. egy szoba az ajtajai debugjait meghívja, a jobb struktúráltság érdekében. 
     * Ez a debug() nem összetévesztentdő a Interfaces.Debuggable interface-n keresztül megvalósított debug()-al, mert ez a logika, és az interface-s debug() végzi el a rekurzív hívást és a sting reprezentáció létrehozásást. Itt tulajdonképpen csak az összefűzés történik a kiírással.
     */
    public void debug(){
        if(testmode)
            gameIsOver = true;
        String[] splitInput = lastInput.split(" ");
        String outMessage = "";

        //Az értelmezett parancs 1. indexű tagjától az utolsóig megpróbál debug szöveget gyűjteni, ha érvénytelen valamelyik ID, akkor hibaüzenet. (Ezek elméletben objektum ID-k)
        for(int i = 1; i < splitInput.length ;i++){
            if(!debuggableObjects.containsKey(Integer.parseInt(splitInput[i]))){
                errorMsg(405, splitInput[i]);
                return;
            }
            else{
                outMessage += debuggableObjects.get(Integer.parseInt(splitInput[i])).debug();
            }
        }
        //Fileba írás
        try{
            FileWriter w = new FileWriter("debug.txt");
            w.write(outMessage);
            w.close();
        }
        catch(IOException ex){
            errorMsg(406, "");
        }
    }
    
    /**
     * Tesztesetek futtatására használható parancs. Ellenőrzi, hogy helyesen lett-e paraméterezve, ha nem errort dob.
     * Ha helyesen lett paraméterezve, akkor csinál egy új map-et, amit a teszt nulláról kell, hogy felépítsen.
     * Miután lefutott a tesztben az összes parancs, meghívjuk a 'debug' parncsot, amely segítségével a 'debug.txt' fileba
     * kiírjuk minden fontos objektum állapotát.
     * Ha ez is megtörtént, akkor ezt a 'debug.txt' file-t összehasonlítjuk a megkapott 'expected.txt' file-al, amiben
     * az elvárt objektum állpaotokat előre defniáltunk. Ha nem egyeznek a fileok, akkor a felhasználónak jelezzük, hogy melyik sorban van
     * az első eltérés.
     * Miután ezt megtettük a jelenlegi állapotot töröljük, és visszaállítjuk a térképet az indítás előtti állapotba.
     * Ezután ezt jelezzük a felhasználónak, és várjuk a következő parancsot.
     * Elvárt syntax: 'executetest ['c'/'f' 'c'->Ha konzolra szeretnénk kiírni az üzeneteket 'f'-> Ha egy fileba szeretnénk kiírni az üzeneteket] [String: input .txt filename] [String: expected .txt filename]'
     */
    public void executetest() {
        String[] splitInput = lastInput.split(" ");
        //Ha nem teszt módban hívják -> error
        if(!testmode){
            errorMsg(5, "");
            return;
        }

        //Ha nem pontosan 4 hosszú -> error
        if(splitInput.length != 4){
            errorMsg(0, "");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        if(splitInput[1].equals("c") || splitInput[1].equals("f")){
            try{
                //Kitisztítjuk a map-et, és minden hozzá tartozó objektumot
                clearMap();

                if(splitInput[1].equals("f")){
                    printToConsole = false;
                    fileWriter = new FileWriter("consoleOutput.txt");
                    writerClosed = false;
                }

                nextTestCommand = 0;
                boolean isStarted = false;

                //Beolvassuk az inputokat, és sorban el is végezzük azokat.
                //Elkészítjük az olvasót, és az első sort beolvassuk
                String fileName = splitInput[2] + ".txt";
                BufferedReader reader;
                reader = new BufferedReader(new FileReader(fileName));
                String line = reader.readLine();
                ArrayList<String> lines = new ArrayList<>();

                //Üzenet a felhasználónak, hogy a fájlbetöltés sikeres
                statusMsg(412, splitInput[2]);

                //Amíg van mit olvasni, azt szedjük be. A beolvasott sort pedig adjuk át a terminal() függvénynek, ami véghezviszi a megfelelő parancsot
                while(line != null){
                    if(!isStarted)
                        lines.add(line);
                    else
                        testInputs.add(line);

                    if(line.contains("start"))
                        isStarted = true;
                    line = reader.readLine();
                }
                reader.close();

                //Végigmegyünk az arraylisten
                for(String lin :lines){
                    terminal(lin);
                }

                String fileToCompare = (printToConsole) ? "debug.txt" : "consoleOutput.txt";

                //Fileíró bezárása
                if(!printToConsole){
                    fileWriter.close();
                    writerClosed = true;
                }


                if(fileCompare(fileToCompare, splitInput[3] + ".txt"))
                    statusMsg(610, "");
                else{
                    reader = new BufferedReader(new FileReader("compareOutput.txt"));
                    line = reader.readLine();

                    if(line.equals("Input and expected file have a different length. Operation aborted.")){
                        errorMsg(605, "");
                    }
                    else{
                        errorMsg(604, "");
                        int count = 0;
                        while(line != null){
                            if(!line.contains("line length not equal"))
                            {
                                switch(count)
                                {
                                    case 1:
                                        println("Actual:");
                                        count++;
                                        break;
                                    case 2:
                                        println("Expected:");
                                        count = 0;
                                        break;
                                    default:
                                        count++;
                                }
                            }

                            println(line);
                            line = reader.readLine();
                        }
                    }
                }

                //Mitán összehasonlítottunk, és adtunk választ kitisztítjuk a map-et
                clearMap();
                statusMsg(607, "");


            }
            catch (IOException ex){
                errorMsg(404, splitInput[2]);
                return;
            }
        }
        else{
            errorMsg(0, "");
            return;
        }
    }
    
    /**
     *  Teszt módban egy szoba hozzáadására szolgáló parancs megvalósítása. Ellenőrzi, hogy helyes módon lett-e hívva, ha nem akkor a megfelelő
     *  hibaüzenetet dobja. Csak a teszt módban használható.
     *  Helyes használatnál hozzáad egy a megadott paramétereknek megfelelő szobát a térképhez.
     *  Elvárt synatx: 'addroom [char: 't'/'f' -> a "gázosság" true vagy false] [int: capacity -> a szoba befogadóképessége] [int: ID -> egyedi azonosító]'
     */
    public void taddroom(){
        String[] splitInput = lastInput.split(" ");
        //Ha nem teszt módban hívják -> error
        if(!testmode){
            errorMsg(5, "");
            return;
        }

        //Ha nem pontosan 4 darab -> error
        if (splitInput.length != 4){
            errorMsg(0,"");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        //Ha a második érték, azaz a gázosság nem 't' vagy 'f' -> error
        if(splitInput[1].equals("t") || splitInput[1].equals("f")){
            //Exception miatt, ha dob egyet akkor rossz a syntax (nem integert adott meg a 3 vagy 4 értéknek) -> error
            try{
                Room newRoom = new Room(splitInput[1].equals("t"), Integer.parseInt(splitInput[2]), Integer.parseInt(splitInput[3]));
                map.addRoom(newRoom);
                statusMsg(601, "");
            }
            catch (NumberFormatException ex){
                errorMsg(0, "");
                return;
            }
        }
        else{
            errorMsg(0, "");
            return;
        }
    }
    
    /**
     * Teszt módban egy ajtó hozzáadására szolgáló parancs.
     * Ellenőrzi, hogy helyes paraméterezéssel lett - e hívva, hogyha nem akkor a megfelelő hibaüzenetet dobja.
     * Ha helyesen lett paraméterezve, akkor készít egy új ajtót amellyel összeköti a kettő ID alapján megadott szobát.
     * Elvárt syntax: 'adddoor [int: Room1 ID] [int: Room2 ID] [char: 't'/'f' -> az elátkozottság] [char: 'r'/'l'/'b' -> az átjárhatósági irány r->jobbra, l->balra, b->mindkettő irányba] [int: ID -> egyedi azonosító]'
     */
    public void tadddoor(){
        String[] splitInput = lastInput.split(" ");
        //Ha nem teszt módban hívják -> error
        if(!testmode){
            errorMsg(5, "");
            return;
        }

        //Ha nem pontosan 6 darab -> error
        if (splitInput.length != 6){
            errorMsg(0,"");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        //Ha a harmadik és negyedik adat helyes
        if(splitInput[3].equals("t") || splitInput[3].equals("f")){
            if(splitInput[4].equals("r") ||splitInput[4].equals("l") || splitInput[4].equals("b")){
                try{
                    //A két szoba ID alapján
                    Room r1 = map.roomById(Integer.parseInt(splitInput[1]));
                    Room r2 = map.roomById(Integer.parseInt(splitInput[2]));

                    //Nem létezik valamelyik szoba ID
                    if(r1 == null){
                        errorMsg(601, splitInput[1]);
                        return;
                    }
                    else if(r2 == null){
                        errorMsg(601, splitInput[2]);
                        return;
                    }

                    //Az ajtó átjárhatósága
                    Door.Direction dir;
                    switch(splitInput[4]){
                        case "r" : {
                            dir = Door.Direction.RIGHT;
                            break;
                        }
                        case "l" : {
                            dir = Door.Direction.LEFT;
                            break;
                        }
                        default : {
                            dir = Door.Direction.BOTH;
                            break;
                        }
                    }

                    //Az ajtó készítése, majd a két szobához az új ajtó hozzáadása
                    Door newDoor = new Door(r1, r2, splitInput[3].equals("t"), dir, Integer.parseInt(splitInput[5]));
                    r1.addDoor(newDoor);
                    r2.addDoor(newDoor);
                    statusMsg(606, "");
                }
                //Ha nem Integert adott meg valahol ahol integert kértünk
                catch (NumberFormatException ex){
                    errorMsg(0, "");
                    return;
                }
            }
            //Ha a negyedik nem helyes
            else{
                errorMsg(0, "");
                return;
            }
        }
        //Ha a harmadik nem helyes
        else{
            errorMsg(0, "");
            return;
        }
    }
    
    /**
     * Teszt módban egy Entity.Entity hozzáadására szolgáló parancs.
     * Ellenőrzi, hogy helyesen lett e paraméterezve, ha nem akkor hibát dob.
     * Ha helyesen lett paraméterezve akkor a kapott ID-jű szobába betesz egy Entityt, melyet a kapott adatoknak megfelelően készít el.
     * Elvárt syntax:
     * 'addentity [char: 's'/'t'/'j' -> s:Entity.Student, t:Entity.Teacher, j:Entity.Janitor] [String: name] [int: ID -> egyedi azonosító] [int: RoomID -> currentRoom]'
     */
    public void taddentity(){
        String[] splitInput = lastInput.split(" ");
        //Ha nem teszt módban hívják -> error
        if(!testmode){
            errorMsg(5, "");
            return;
        }

        //Ha nem pontosan 5 hosszú -> error
        if(splitInput.length != 5){
            errorMsg(0, "");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        //Megpróbáljuk megcsinálni a cuccot
        try{
            //A szoba ahova majd egyszer kerül a cucc
            Room room = map.roomById(Integer.parseInt(splitInput[4]));

            //Ha nem létezik ilyen ID-s szoba
            if(room == null){
                errorMsg(601, splitInput[3]);
                return;
            }

            switch(splitInput[1]){
                //Tanár hozzáadása
                case "t" : {
                    Teacher teacher = new Teacher(splitInput[2], Integer.parseInt(splitInput[3]), room);
                    entities.add(teacher);
                    room.addEntity(teacher);
                    statusMsg(604, "");
                    break;
                }
                //Entity.Janitor hozzáadása
                case "j" : {
                    Janitor janitor = new Janitor(splitInput[2], Integer.parseInt(splitInput[3]), room);
                    entities.add(janitor);
                    room.addEntity(janitor);
                    statusMsg(605, "");
                    break;
                }
                //Entity.Student hozzáadása -> count növelése
                default : {
                    Student student = new Student(splitInput[2], Integer.parseInt(splitInput[3]), room);
                    entities.add(student);
                    room.addEntity(student);
                    countOfPlayers++;
                    statusMsg(603, "");
                }
            }
        }
        //Ha nem Integert adott meg ahol Integert vártunk
        catch (NumberFormatException ex){
            errorMsg(0, "");
            return;
        }
    }
    
    /**
     * Teszt módban egy tárgy hozzáadására szolgáló parancs.
     * Helytelen paraméterezés esetén a hibát jelzi.
     * Ha helyesen lett paraméterezve, akkor a megadott ID-jű szobába letesz egy olyan itemet amit megadtak neki a második paraméterben.
     *
     * Elvárt syntax:
     * 'tadditem [String: airfreshner/beer/camembert/fakemask/fakesliderule/faketvsz/mask/rag/sliderule/transistor/tvsz] [int: itemID] [int: roomID -> ahova kerulni fog]'
     */
    public void tadditem(){
        String[] splitInput = lastInput.split(" ");
        //Ha nem teszt módban hívják -> error
        if(!testmode){
            errorMsg(5, "");
            return;
        }

        //Ha nem pontosan 4 hosszú -> error
        if(splitInput.length != 4){
            errorMsg(0, "");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted) {
            errorMsg(1, "");
            return;
        }

        try {
            //A megadott szoba, ahova a tárgy kerülni fog
            Room room = map.roomById(Integer.parseInt(splitInput[3]));

            //Ha nem létezik ilyen szoba ID
            if(room == null){
                errorMsg(601, splitInput[3]);
                return;
            }

            //Item.Item generálása a megadott input alapján
            switch (splitInput[1]) {
                //Légfrissítő hozzáadása
                case "airfreshner": {
                    AirFreshner item = new AirFreshner(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Sör hozzáadása
                case "beer": {
                    Beer item = new Beer(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Item.Camembert hozzáadása
                case "camembert": {
                    Camembert item = new Camembert(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Hamis maszk hozzáadása
                case "fakemask": {
                    FakeMask item = new FakeMask(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Hamis logarléc hozzáadása
                case "fakesliderule": {
                    FakeSlideRule item = new FakeSlideRule(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Hamis Item.TVSZ hozzáadása
                case "faketvsz": {
                    FakeTVSZ item = new FakeTVSZ(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Maszk hozzáadása
                case "mask": {
                    Mask item = new Mask(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Rongy hozzáadása
                case "rag": {
                    Rag item = new Rag(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Logarléc hozzáadása
                case "sliderule": {
                    SlideRule item = new SlideRule(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Tranzisztor hozzáadása
                case "transistor": {
                    Transistor item = new Transistor(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Item.TVSZ hozzáadása
                case "tvsz": {
                    TVSZ item = new TVSZ(Integer.parseInt(splitInput[2]));
                    room.addItem(item);
                    statusMsg(602, "");
                    break;
                }
                //Helytelen tárgynév
                default: {
                    errorMsg(0, "");
                    return;
                }
            }
        }
        //Nem Integert adott meg ahol Integert vártunk
        catch (NumberFormatException ex){
            errorMsg(0, "");
            return;
        }
    }

    /**
     * A tmerge command implenetációja, a console-on megadott 2 szobát olvasztja össze, csak teszt módban használható
     */
    public void tmerge(){
        String[] splitInput = lastInput.split(" ");
        if(!testmode){
            errorMsg(5,"");
            return;
        }
        else if (splitInput.length != 3){
            errorMsg(0,"");
            return;
        }

        Room r1 = map.roomById(Integer.parseInt(splitInput[1]));
        Room r2 = map.roomById(Integer.parseInt(splitInput[2]));

        if(r1 == null || r2 == null){
            errorMsg(606,splitInput[1] + " or " + splitInput[2]);
            return;
        }
        else{
            boolean canMerge = r1.merge(r2);
            if(!canMerge) {
                errorMsg(602, "");
                return;
            }
            map.removeRoom(r2);
            debuggableObjects.remove(r2.getID());
            statusMsg(608,"");
        }
    }

    /**
     * A tsplit commandot valósítja meg, a console-ról beolvasott szobát választja ketté ha lehet, csak test módban lehet használni
     */
    public void tsplit(){
        String[] splitInput = lastInput.split(" ");
        if(!testmode){
            errorMsg(5,"");
            return;
        }
        else if (splitInput.length != 2){
            errorMsg(0,"");
            return;
        }

        Room r1 = map.roomById(Integer.parseInt(splitInput[1]));

        if(r1 == null){
            errorMsg(603,splitInput[1]);
        }
        else{
            Room newR = r1.split();
            if(newR == null) errorMsg(603,"");
            else{
                map.rooms.add(newR);
                debuggableObjects.put(newR.getID(),newR);
                statusMsg(609,Integer.toString(newR.getID()));
            }
        }
    }

    /**
     * Elátkozza az adott szoba összes ajtaját
     */
    public void tcurse(){
        String[] splitInput = lastInput.split(" ");
        if(!testmode){
            errorMsg(5,"");
            return;
        }
        else if (splitInput.length != 2){
            errorMsg(0,"");
            return;
        }

        Room r1 = map.roomById(Integer.parseInt(splitInput[1]));

        if(r1 == null){
            errorMsg(603,splitInput[1]);
        }
        else{
            r1.curseAllDoors();
            statusMsg(611,Integer.toString(r1.getID()));
        }
    }

    /**
     * Feloldja az elázkoottságot az adott szoba össze ajtajáról
     */
    public void tuncurse(){
        String[] splitInput = lastInput.split(" ");
        if(!testmode){
            errorMsg(5,"");
            return;
        }
        else if (splitInput.length != 2){
            errorMsg(0,"");
            return;
        }

        Room r1 = map.roomById(Integer.parseInt(splitInput[1]));

        if(r1 == null){
            errorMsg(603,splitInput[1]);
        }
        else{
            r1.unCurseAllDoors();
            statusMsg(612,Integer.toString(r1.getID()));
        }
    }
    

    /**
     * Összehasonlít 2 file-t, kimenete a "compareOutput.txt" amennyiben nem ugyanolyan hosszú a 2 file azt írja a kimenetre,
     * ha ugyanolyan hosszúak de talál különböző sorokat ki írja, hogy hanyadik sor valamint egymás alá teszi a 2 sort
     * @param filePath1 - első filenév/elérési út
     * @param filePath2 - második filenév/elérési út
     */
    public boolean fileCompare(String filePath1, String filePath2){
        boolean correct = true;
        try{
            File file1 = new File(filePath1);
            File file2 = new File(filePath2);
            File output = new File("compareOutput.txt");
            FileWriter writer = new FileWriter("compareOutput.txt");

            int file1Len = fileLineCount(file1);
            int file2len = fileLineCount(file2);

            if(file1Len != file2len){
                writer.write("Input files does not match in length aborted.");
                writer.close();
                return false;
            }

            Scanner reader1 = new Scanner(file1);
            Scanner reader2 = new Scanner(file2);
            int lineCount = 0;

            while(reader1.hasNextLine()){
                lineCount++;
                String fileLine1 = reader1.nextLine();
                String fileLine2;
                if(reader2.hasNextLine()) {
                    fileLine2 = reader2.nextLine();
                    if(fileLine1.length() == fileLine2.length()) {
                        for (int i = 0; i < fileLine1.length(); i++) {
                            if (fileLine1.charAt(i) != fileLine2.charAt(i)) {
                                writer.write("--+> line " + lineCount + ":\n" + fileLine1 + "\n" + fileLine2 + "\n");
                                correct = false;
                                break;
                            }
                        }
                    }
                    else{
                        writer.write("--+> line " + lineCount + ": line length not equal.\n");
                        correct = false;
                    }
                }
            }
            reader1.close();
            reader2.close();
            writer.close();

        }catch (FileNotFoundException e){
            errorMsg(404,filePath1 + " or " + filePath2);
        }catch (IOException e){
            errorMsg(404,filePath1 + " or " + filePath2);
        }
        return correct;
    }

    /**
     * Megszámolja egy fájlban hány sor van
     * @param file - fájl neve
     * @return - fájlban lévő sorok száma
     */
    private int fileLineCount(File file){
        int lineCount = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            errorMsg(404, file.getName());
        }
        return lineCount;
    }
    
    /**
     * Függvény az 'execute' parancs megvalósítására. Ellenőrzi, hogy helyesen lett e paraméterezve, ha nem akkor azt errorral jelzi.
     * Ha helyesen hívódott, akkor beolvas egy a felhasználó által specifikált '.txt' fájlt, melyben minden sorban egy a bemeneti
     * nyelvenek megfelelő parancs van. Ezt a beolvasott sort átadja a terminal() függvénynek, mely ezt értelmezni tudja, és végbe is viszi.
     * Ha a file beolvasás során hiba lép fel, akkor azt az errorMsg() függvény segítségével közli.
     * Ha valami probléma van a fileban lévő parancsokkal, akkor azokat az egyes parancsok kezelik.
     */
    public void execute(){
        //Részekre daraboljuk az inputot, ha kevesebb mint kettő rész van, akkor error
        String[] splitInput = lastInput.split(" ");
        if(splitInput.length < 2) {
            errorMsg(402, "");
            return;
        }

        //Ha több mint kettő rész van, akkor is error
        if(splitInput.length > 2) {
            errorMsg(0, "");
            return;
        }

        //Ha a játék már elindult mikor hívják, akkor error
        if(gameStarted){
            errorMsg(1, "");
            return;
        }

        //Ha itt vagyunk kaptunk egy filenevet, ami lehet jó. lehet nem
        String fileName = splitInput[1] + ".txt";
        BufferedReader reader;
        try{
            //Elkészítjük az olvasót, és az első sort beolvassuk
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            //Üzenet a felhasználónak, hogy a fájlbetöltés sikeres
            statusMsg(412, splitInput[1]);

            //Amíg van mit olvasni, azt szedjük be. A beolvasott sort pedig adjuk át a terminal() függvénynek, ami véghezviszi a megfelelő parancsot
            while(line != null){
                terminal(line);
                line = reader.readLine();
            }

            reader.close();
        }
        catch (IOException ex){
            errorMsg(404, splitInput[1]);
        }

    }

    @Override
    public void update() {
        notifyObservers();
    }
}
