package server;
	
import java.awt.Dimension;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import application.PersoIHM.Sens;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class ServerWindow extends JFrame {
	
	private Button btn;
	
	private Rectangle table;
	
	private Rectangle zone_carte;
	
	public ServerWindow() {
		JPanel panel = new JPanel();
		
		this.setTitle("Server configuration");
		this.setSize(350, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
		
		JLabel label_nb_max = new JLabel("Nombre maximum de joueurs : ");
		Integer[] nb_player = new Integer[]{2,3,4,5,6,7,8,9,10};
		JComboBox<Integer> list_nb_player = new JComboBox<Integer>(nb_player);
		list_nb_player.setPreferredSize(new Dimension(300,25));
		
		JLabel label_time_increase = new JLabel("Nombre de tours avant augmentation : ");
		JFormattedTextField time_increase = new JFormattedTextField(NumberFormat.getIntegerInstance());
		time_increase.setPreferredSize(new Dimension(300, 25));
		
		ButtonGroup radio_group = new ButtonGroup();
		
		JRadioButton radio_distrib_1 = new JRadioButton("Distribution 1 ");
		radio_distrib_1.setToolTipText("Explication");
		radio_group.add(radio_distrib_1);
		
		JRadioButton radio_distrib_2 = new JRadioButton("Distribution 2 ");
		radio_distrib_2.setToolTipText("Explication");
		radio_group.add(radio_distrib_2);
		
		JRadioButton radio_distrib_3 = new JRadioButton("Distribution 3 ");
		radio_distrib_3.setToolTipText("Explication");
		radio_group.add(radio_distrib_3);
		
		JLabel label_players = new JLabel("Joueurs : ");
		String subject[] = {"Aucun joueur pour l'instant"};
		JList list_player = new JList<String>(subject);
		list_player.setPreferredSize(new Dimension(300, 150));
		list_player.setEnabled(false);
		
		JButton button_launch = new JButton("Lancer le server");
		button_launch.setPreferredSize(new Dimension(250, 40));
		JButton button_begin = new JButton("Commencer la partie");
		button_begin.setPreferredSize(new Dimension(250, 40));
		button_begin.setEnabled(false);
		
		panel.add(label_nb_max);
		panel.add(list_nb_player);
		
		panel.add(label_time_increase);
		panel.add(time_increase);
		
		panel.add(radio_distrib_1);
		panel.add(radio_distrib_2);
		panel.add(radio_distrib_3);
		
		panel.add(label_players);
		panel.add(list_player);
		
		panel.add(button_launch);
		panel.add(button_begin);
		
		this.setContentPane(panel);
		this.setVisible(true);
	}
	
	public void initializeAction()
	{
		 btn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                System.out.println("Hello World");
            }
        });
	}
}
