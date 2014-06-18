package gui.server;
	
import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.shape.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import sma.agent.SimulationAgent;


public class ServerWindow extends JFrame implements PropertyChangeListener {
	
	public enum ServerGuiEvent  {
		LAUNCH_SERVER,
		LAUNCH_GAME,
	}
	
	
	private Button btn;
	
	private Rectangle table;
	
	private Rectangle zone_carte;
	
	private JComboBox<Integer> list_nb_player;
	private JFormattedTextField nb_tour_increase;
	private JCheckBox radio_complete_by_ia;
	
	private JButton button_launch;
	private JButton button_begin;
	
	private SimulationAgent sim_agent;
	
	public ServerWindow(SimulationAgent simAgent) {
		JPanel panel = new JPanel();
		
		this.setTitle("Server configuration");
		this.setSize(350, 250);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    
	    this.sim_agent = simAgent;
		
		JLabel label_nb_max = new JLabel("Nombre maximum de joueurs humain : ");
		Integer[] nb_player = new Integer[]{2,3,4,5,6,7,8,9,10};
		list_nb_player = new JComboBox<Integer>(nb_player);
		list_nb_player.setPreferredSize(new Dimension(300,25));
		list_nb_player.setSelectedIndex(2);
		
		radio_complete_by_ia = new JCheckBox("Compléter les joueurs manquants par des IA");
		radio_complete_by_ia.setSelected(true);
		
		
		JLabel label_time_increase = new JLabel("Nombre de minutes avant augmentation : ");
		nb_tour_increase = new JFormattedTextField(NumberFormat.getIntegerInstance());
		nb_tour_increase.setPreferredSize(new Dimension(300, 25));
		nb_tour_increase.setText("5");
		
		button_launch = new JButton("Lancer le server");
		button_launch.setPreferredSize(new Dimension(250, 40));
		button_begin = new JButton("Commencer la partie");
		button_begin.setPreferredSize(new Dimension(250, 40));
		button_begin.setEnabled(false);
		
		panel.add(label_nb_max);
		panel.add(list_nb_player);
		
		panel.add(radio_complete_by_ia);
		
		panel.add(label_time_increase);
		panel.add(nb_tour_increase);
		
		panel.add(button_launch);
		panel.add(button_begin);
		
		this.setContentPane(panel);
		this.setVisible(true);
		
		initializeAction();
	}
	
	public void initializeAction()
	{
		button_launch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				launchServer();
			}
		});
		
		button_begin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				launchGame();
			}
		});
	}
	
	private void launchServer() {
		GuiEvent ev = new GuiEvent(this, ServerGuiEvent.LAUNCH_SERVER.ordinal());
		
		Integer nb_player = (Integer)list_nb_player.getSelectedItem();
		

		ev.addParameter(nb_player);
		ev.addParameter(Integer.parseInt(nb_tour_increase.getText()));
		ev.addParameter(radio_complete_by_ia.isSelected());
		
		sim_agent.postGuiEvent(ev);
		
		nb_tour_increase.setEnabled(false);
		list_nb_player.setEnabled(false);
		button_launch.setEnabled(false);
		button_launch.setText("Server started ...");
		button_begin.setEnabled(true);
	}
	
	private void launchGame() {
		GuiEvent ev = new GuiEvent(this, ServerGuiEvent.LAUNCH_GAME.ordinal());
		sim_agent.postGuiEvent(ev);
		button_begin.setEnabled(false);
		button_begin.setText("Game started ...");
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		
		
		
	}
}
