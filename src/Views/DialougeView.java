package Views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import Entity.Entity;
import GameLogic.GameController;
import Interfaces.Observer;


public class DialougeView extends JPanel{

    public JTextArea txt;
    public JScrollPane logPane;
    public Entity curEntity;
    public String newMString;
    public String lastPrinted;

    public DialougeView(){
        curEntity = GameController.getInstance().getCurrentEntity();
        this.setLayout(new BorderLayout());
        //the text area for the commands
        txt = new JTextArea();
        //disabled, only the system writes to it
        txt.setEditable(false);
        txt.setBackground(Color.LIGHT_GRAY);
        logPane = new JScrollPane(txt);
        //tweaking the scrollpane
        logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setPreferredSize(new Dimension(200,512));
        this.add(logPane,BorderLayout.CENTER);
        this.update();
    }

    public void update() {
    //checking whether we moved or the update was just a text call
        if(GameController.getInstance().getCurrentEntity() != curEntity){
            //if the entity is changed, that means that we changed to another entity, so the console has to be cleaned
            //selecting all text then replacing it with an empty string (𝓪𝓶𝓪𝔃𝓲𝓷𝓰, i know)
            txt.selectAll();
            txt.replaceSelection("");
            curEntity = GameController.getInstance().getCurrentEntity();
            return;
        }
        //if the entity stayed the same, the update was just a console message
        newMString = GameController.getInstance().lastOutMessage;
        if(!newMString.equals(lastPrinted)){
            txt.append(newMString);
            lastPrinted = newMString;
        }
        
    }
}
