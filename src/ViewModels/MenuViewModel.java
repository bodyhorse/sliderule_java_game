package ViewModels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Entity.Entity;
import Entity.Student;
import GameLogic.GameController;
import GameMap.Room;
import Interfaces.Observer;
import Views.MenuView;
import Views.GameView;

public class MenuViewModel extends MenuView implements Observer {
    /**
     * Az új játék gomb ActionListenere.
     */
    public ActionListener newGameActionListener;

    /**
     * A játék betöltése gomb ActionListenere.
     */
    public ActionListener loadGameActionListener;

    /**
     * A játék indítását végző gomb ActionListenere.
     */
    public ActionListener startActionListener;

    /**
     * A játékos hozzáadását végző gomb ActionListenere.
     */
    public ActionListener addPlayListener;

    /**
     * A játékos eltávolítását végző gomb ActionListenere.
     */
    public ActionListener removePlayListener;

    /**
     * Az osztály konstruktora.
     * @param g - A játék GameView-ja.
     */
    public MenuViewModel(GameView g){
    	//super(g);
        super(GameController.getInstance().getGameView());

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(studentArray);
        removePlayerBox.setModel(model);

        newGameActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//gv.remove(menuPanel);
            	//gv.add(playerManagerPanel);
            	//gv.revalidate();
                GameController.getInstance().getGameView().remove(menuPanel);
                GameController.getInstance().getGameView().add(playerManagerPanel);
                if (GameController.getInstance().gameStarted) GameController.getInstance().clearMap();
                GameController.getInstance().getGameView().revalidate();
                GameController.getInstance().getGameView().repaint();
            }           
        };

        loadGameActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameController.getInstance().load();
                //GameController.getInstance().terminal("start ");

                if(GameController.getInstance().gameStarted)
                    GameController.getInstance().getGameView().remove(menuPanel);
            } 
        };

        startActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                GameController.getInstance().terminal("start ");
                
                if(GameController.getInstance().gameStarted)
            	    GameController.getInstance().getGameView().remove(playerManagerPanel);
            } 
        };

        
        //A GameController entities listához kell adni az itt létrehozott játékosokat
        addPlayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();

                String[] temp = new String[studentList.size()];

                for (int i = 0; i < studentList.size(); i++){
                    temp[i] = studentList.get(i);
                }

                studentArray = temp;

                model.removeAllElements();
                for (String student : studentList) {
                    model.addElement(student);
                }
            } 
        };

        //Itt meg a GameController entities listájából kell kitöröli a törlendő jatekosokat
        removePlayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!studentList.isEmpty()) {
                    studentList.remove(removePlayerBox.getSelectedIndex());

                    Entity entToRemove = GameController.getInstance().getEntityAt(removePlayerBox.getSelectedIndex());
                    GameController.getInstance().removeEntity(entToRemove);

                    String[] temp = new String[studentList.size()];

                    for (int i = 0; i < studentList.size(); i++) {
                        temp[i] = studentList.get(i);
                    }
                    studentArray = temp;
                    model.removeAllElements();
                    for (String student : studentList) {
                        model.addElement(student);
                    }
                }
            } 
        };

        newGameButton.addActionListener(newGameActionListener);
        loadGameButton.addActionListener(loadGameActionListener);
        startButton.addActionListener(startActionListener);
        addPlayButton.addActionListener(addPlayListener);
        removePlayerButton.addActionListener(removePlayListener);
    }

    /**
     * A képernyő frissítését végző absztrakt metódus felülírása.
     */
    @Override
    public void update() {

    }
}
