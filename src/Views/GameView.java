package Views;

import Entity.Entity;
import GameLogic.GameController;
import Interfaces.*;
import ViewModels.MenuViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameView extends JFrame implements Observer {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Játék elején megjelenő menühöz tartozó panel.
	 */
    private MenuView menu = new MenuViewModel(this);
    
    /**
     * A játék grafikus felületén a szoba vizualizációjáért felelős panel.
     */
    private RoomView roomView;
    
    /**
     * A játék grafikus felületén az éppen soron lévő játékos és környezetének
     * összes információját kilistázó táblázatokért felelős, valamint a tárgyakkal való
     * interakciót lehetővé tevő panel.
     */
    private InventoryView inventoryView;
    
    /**
     * A játék grafikus felületén az éppen soron lévő játékos interakcióit szövegesen
     * nyugtáző, vagy annak hibáit jelző szöveget megjelenító panel.
     */
    private DialougeView dialougeView;
    
    /**
     * A játék grafikus felületén az éppen soron lévő játékos szobák közötti
     * mozgását lehetővé tevő panel.
     */
    private DoorsView doorsView;
    
    /**
     * A játék vezérléséért felelős singleton osztály.
     */
    private GameController gameController;
    
    private boolean updateIsEnabled;
    
    private JPanel mainPanel;

    /**
     * A GameView konstruktora. Beállítja a megjelenő keret tulajdonságait,
     * majd megjeleníti a játék főmenüjét.
     */
    public GameView() {
    	
    	gameController = GameController.getInstance();
    	
    	updateIsEnabled = false;
    	
        setTitle("Logarléc");
        setSize(712, 712);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        //A keret megjelenítése
        this.add(menu.menuPanel);
        this.setVisible(true);
        setLocationRelativeTo(null);
    }


    /**
     * A játék grafikus felületét inicializáló, valamint megjelenítő metódus.
     */
    public void showGame(){
    	
    	this.remove(menu.menuPanel);
    	
    	//A négy megjelenítő panel inicializálása
    	roomView = new RoomView(gameController.getCurrentEntity().getCurrentRoom());
    	inventoryView = new InventoryView();
    	dialougeView = new DialougeView();
    	doorsView = new DoorsView(gameController.getCurrentEntity().getCurrentRoom());
    	updateIsEnabled = true;
    	
    	
    	//Csomagoló panel létrehozása, ami lehetővé teszi a megfeleő grid elrendezést
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        //Roomview bal felül
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(roomView, gbc);

        //InventoryView jobb felül
        inventoryView.setPreferredSize(new Dimension(200, 512));
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(inventoryView, gbc);

        // DialogueView bal alulra
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialougeView.setPreferredSize(new Dimension(512,200));
        mainPanel.add(dialougeView, gbc);
        

        // DoorsPanel jobb alul
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(doorsView, gbc);

        //Elkészült panel becsomagolása a keretbe
        this.add(mainPanel);
        this.setSize(712, 712);
        this.pack();
        this.setVisible(true);
        this.revalidate();
        this.repaint();

        //exit gomb működése
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleEscAction();
            }
        });
    }

    /**
     * Az exit gomb működését leíró függvény
     */
    private void handleEscAction() {
        int choice = JOptionPane.showConfirmDialog(null,
                "Do you want to save before quitting?", "Save and exit confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            GameController.getInstance().save();
        }
        
        if (choice != JOptionPane.CANCEL_OPTION){
            this.dispose();
        }
    }

    /**
     * Frissíti a játék grafikus megjelenítéséhez használt ablak összes komponensének a tartalmát
     * oly módon, hogy minden komponensnek meghívja a megfeleó metódusát.
     */
    @Override
    public void update() {
    	if(GameController.getInstance().gameStarted && updateIsEnabled) {
        	roomView.update();
        	inventoryView.update(); 
        	dialougeView.update();
        	doorsView.refreshDoors();
        	this.revalidate();
            this.repaint();
            
            if(!gameController.gameIsOver) {
                if(gameController.getCurrentEntity().getRemainingStun() > 0) guiStunnedEntityMessage();
                else if(!gameController.getCurrentEntity().getIsAlive()) guiSoulLessEntityMessage();
            }
    	}
    }
    
    /**
     * Visszaállítja a játék kezdeti menüjét, törölve az eddigi játékállást,
     * meghívása után új játék kezdhető a megszokott módon.
     */
    public void newGame() {
    	updateIsEnabled = false;
		this.remove(mainPanel);
		add(menu.menuPanel);
		//TODO Ezt a kettőt valójában nem itt kellene 
		menu.studentList.clear();
        menu.removePlayerBox.removeAllItems();
		this.pack();
		this.revalidate();
		this.repaint();
    }
    
    /**
     * A logarléc sikeres felvétele/Játék megnyerése után megjelenő üzenetért
     * felelős metódus. Lehetőség van benne új játékot indítani, vagy a játékból
     * kilépni.
     */
    public void guiGameWon() {
    	
    	String[] responses = {"New Game!", "Exit game."};
    	
    	int answer = JOptionPane.showOptionDialog(this, "The students have found the sliderule! GG WP!", "You Win", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, responses, 0);
    	
    	if(answer == 0) newGame();
    	else this.dispose();
    }
    
    /**
     * A játék végét jelzi grafikus módon a játékos számára.
     * Lehetősége van az üzenet megjelenése után új játékot kezdeni,
     * vagy kilépni.
     * @param type A játék végének típusát meghhatározó azonosító.
     */
    public void guiGameLost(int type) {
    	
    	String message = (type == 0) ? "[The teachers achieved their goal, all students have lost their soul!]" : "[The students couldn’t find the slide rule in time.]";
    	
    	String[] responses = {"New Game!", "Exit game."};
    	
    	int answer = JOptionPane.showOptionDialog(this, message, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, responses, 0);
    	
    	if(answer == 0) newGame();
    	else this.dispose();
    }
    
    /**
     * A játékos karakterének bénított állapotát a játékosnak grafikus
     * módon tudtára adó üzenet megjelenítését végző metódus.
     */
    public void guiStunnedEntityMessage() {
    	Entity currEntity = gameController.getCurrentEntity();
    	String message = "Your character " + currEntity.getName()  + " is currently stunned. Remaining stunned rounds: " + currEntity.getRemainingStun();
    	
    	JOptionPane.showMessageDialog(this, message, "Character stunned!", JOptionPane.WARNING_MESSAGE);
    	
    	gameController.nextPlayerPassive(); 
    }
    
    /**
     * A lélekvesztett karakterek állapotát a játékos számára 
     * grafikus módon megjelenítő metódus. Meghívása után valamint
     * a megjelenő ablakkal bármilyen felhasználói interakció hatására
     * a következő karakter számára adódik át az irányítás.
     */
    public void guiSoulLessEntityMessage() {
    	
    	String message = "Your character " +gameController.getCurrentEntity().getName() + " does not have a soul anymore.";
    	
    	JOptionPane.showMessageDialog(this, message, "Oh no!", JOptionPane.ERROR_MESSAGE);
    	
    	gameController.nextPlayerPassive();
    }
}
