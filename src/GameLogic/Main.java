package GameLogic;

import Entity.Student;
import Item.Beer;
import Item.Mask;
import Views.GameView;
import GameMap.*;

import javax.swing.*;
import java.io.Serializable;

public class Main implements Serializable {
    public static void main(String[] args){
		GameController.getInstance().mainMenu();

		//GameController.getInstance().initGui();

	}
}