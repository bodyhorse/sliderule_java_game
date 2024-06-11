package Views;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import Entity.Student;
import GameLogic.GameController;
import Interfaces.*;

public class MenuView extends JPanel implements Observer {
    /**
     * A játék főmenüjének panele.
     */
    public JPanel menuPanel;

    /**
     * A játék játékoskezelő menüjének panele.
     */
    public JPanel playerManagerPanel;

    /**
     * Az új játékot indító gomb.
     */
    public JButton newGameButton;

    /**
     * Mentett játék betöltése gomb.
     */
    public JButton loadGameButton;

    /**
     * Játékost hozzáadó gomb.
     */
    public JButton addPlayButton;

    /**
     * Játékost eltávolító gomb.
     */
    public JButton removePlayerButton;

    /**
     * Új játék esetén játékot indító gomb.
     */
    public JButton startButton;

    /**
     * Főmenübe visszalépő gomb.
     */
    public JButton mainMenuButton;

    /**
     * Játékos nevének megadására szolgáló szövegmező.
     */
    public JTextField playerTextField;

    /**
     * Combobox, amellyel ki lehet választani az eltávolítandó játékost.
     */
    public JComboBox removePlayerBox;

    /**
     * Credithez szöveg.
     */
    public JLabel credit;

    /**
     * A játék GameView-ja.
     */
    public GameView gv;

    /**
     * Játékos hozzáadásánál használt lista.
     */
    public ArrayList<String> studentList;

    /**
     * Játékos hozzáadásánál használt tömb.
     */
    public String[] studentArray;

    /**
     * A háttérképet tartalmazó változó.
     */
    private Image backGroundImage;

    /**
     * Az osztály konstruktora.
     * @param g - A játék GameView-ja.
     */
    public MenuView(GameView g){

        JLabel bgLabel = imgToLabel("./rsc/bg_img_menu.jpg");
        JPanel bgPanel = new JPanel();
        bgPanel.add(bgLabel);
        studentList = new ArrayList<String>();
        studentArray = new String[0];
        gv = GameController.getInstance().getGameView(); //g;
        menuPanel = new JPanel(new BorderLayout());
        playerManagerPanel = new JPanel();

        newGameButton = new JButton("New Game");
        loadGameButton = new JButton("Load Game");
        addPlayButton = new JButton("Add Player");
        removePlayerButton = new JButton("Remove Player");
        startButton = new JButton("Start");

        playerTextField = new JTextField();



        //menuPanel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));
        //menuPanel.setLayout(new GridLayout(3, 1, 20, 20));

        playerManagerPanel.setBorder(BorderFactory.createEmptyBorder(200, 100, 200, 100));
        playerManagerPanel.setLayout(new GridLayout(3, 2, 50, 20));

        removePlayerBox = new JComboBox<>();

        menuPanel.add(bgLabel,BorderLayout.CENTER);
        menuPanel.setSize(512,512);
        JPanel menuButtonsPanel = new JPanel();
        menuButtonsPanel.add(newGameButton);
        menuButtonsPanel.add(loadGameButton);
        menuButtonsPanel.setBackground(Color.BLACK);
        menuPanel.add(menuButtonsPanel, BorderLayout.SOUTH);

        /*
        credit = new JLabel("00FF00");
        credit.setForeground(Color.WHITE);
        credit.setFont(new Font("Arial", Font.PLAIN, 30));
        credit.setHorizontalAlignment(JLabel.CENTER);

        menuButtonsPanel.add(credit);
        menuPanel.add(bgLabel, BorderLayout.CENTER);

        //menuPanel.add(newGameButton);
        //menuPanel.add(loadGameButton);
        //menuPanel.add(credit);

        */
        playerManagerPanel.add(addPlayButton);
        playerManagerPanel.add(playerTextField);
        playerManagerPanel.add(removePlayerButton);
        playerManagerPanel.add(removePlayerBox);
        playerManagerPanel.add(startButton);

        this.add(menuPanel);
    }

    /**
     * Játékos hozzáadását végző függvény.
     */
    public void addStudent(){
        String s = playerTextField.getText();
        if (s == null ||s == "" || s.isEmpty()){
            return;
        }


        String command = "addplayer " + s;
        
        GameController.getInstance().terminal(command);

        studentList.add(s);
        playerTextField.setText("");
    }


    /**
     * A képernyő frissítését végző absztrakt metódus felülírása.
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Képből JLabel-t konvertáló függvény.
     * @param path - A kép file helye.
     * @return - A kép JLabel-re konvertálva.
     */
    private JLabel imgToLabel(String path){
        try {
            ImageIcon pic = new ImageIcon(path);
            return new JLabel(pic);
        } catch (Exception e) {
            return new JLabel("Image not found");
        }
    }
}
