package Views;

import java.awt.LayoutManager;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Entity.Student;
import Interfaces.*;

public class MenuView extends JPanel implements Observer {
    public JPanel menuPanel;
    public JPanel playerManagerPanel;

    public JButton newGameButton;
    public JButton loadGameButton;
    public JButton addPlayButton;
    public JButton removePlayerButton;
    public JButton startButton;
    public JButton mainMenuButton;

    public JTextField playerTextField;

    public JComboBox removePlayerBox;

    public JLabel credit;

    public GameView gv;

    public ArrayList<String> studentList;
    public String[] studentArray;

    public MenuView(GameView g){
        studentList = new ArrayList<String>();
        studentArray = new String[0];
        gv = g;
        menuPanel = new JPanel();
        playerManagerPanel = new JPanel();

        newGameButton = new JButton("New Game");
        loadGameButton = new JButton("Load Game");
        addPlayButton = new JButton("Add Player");
        removePlayerButton = new JButton("Remove Player");
        startButton = new JButton("Start");

        playerTextField = new JTextField();

        credit = new JLabel("00FF00");

        menuPanel.setBackground(Color.GRAY);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));
        menuPanel.setLayout(new GridLayout(3, 1, 20, 20));

        playerManagerPanel.setBackground(Color.GRAY);
        playerManagerPanel.setBorder(BorderFactory.createEmptyBorder(200, 100, 200, 100));
        playerManagerPanel.setLayout(new GridLayout(3, 2, 50, 20));

        removePlayerBox = new JComboBox<>();

        credit.setForeground(Color.WHITE);
        credit.setFont(new Font("Arial", Font.PLAIN, 30));
        credit.setHorizontalAlignment(JLabel.CENTER);

        menuPanel.add(newGameButton);
        menuPanel.add(loadGameButton);
        menuPanel.add(credit);

        playerManagerPanel.add(addPlayButton);
        playerManagerPanel.add(playerTextField);
        playerManagerPanel.add(removePlayerButton);
        playerManagerPanel.add(removePlayerBox);
        playerManagerPanel.add(startButton);

        this.add(menuPanel);
    }

    public void addStudent(){
        String s = playerTextField.getText();
        if (s == null ||s == "" || s.isEmpty()){
            return;
        }

        if (s.contains(" ")){
            return;
        }

        studentList.add(s);
        playerTextField.setText("");
    }


    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
}
