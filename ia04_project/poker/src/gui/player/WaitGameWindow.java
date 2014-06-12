package gui.player;
	
import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import sma.agent.HumanPlayerAgent;
import sma.agent.SimulationAgent;


public class WaitGameWindow extends JFrame implements PropertyChangeListener {
	
	public enum WaitGameGuiEvent  {
		TRY_CONNECT,
		GAME_START,
		FAILURE_CONNECT
	}
	
	private JFormattedTextField field_pseudo;
	private JButton button_launch;
	
	private HumanPlayerAgent human_player_agent;
	
	public WaitGameWindow(HumanPlayerAgent human_player_agent) {
		JPanel panel = new JPanel();
		
		this.setTitle("Join poker game");
		this.setSize(350, 250);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    
	    this.human_player_agent = human_player_agent;
		
		JLabel label_pseudo = new JLabel("Pseudo :");
		field_pseudo = new JFormattedTextField();
		field_pseudo.setPreferredSize(new Dimension(325,40));
		
		button_launch = new JButton("Rejoindre une partie");
		button_launch.setPreferredSize(new Dimension(250, 40));
		
		panel.add(field_pseudo);
		panel.add(button_launch);
		
		this.setContentPane(panel);
		
		initializeAction();
	}
	
	public void initializeAction()
	{
		button_launch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				joinGame();
			}
		});
	}
	
	private void joinGame() {
		if(!field_pseudo.getText().isEmpty())
		{
			GuiEvent ev = new GuiEvent(this, WaitGameGuiEvent.TRY_CONNECT.ordinal());
			ev.addParameter(field_pseudo.getText());
			human_player_agent.postGuiEvent(ev);
			button_launch.setEnabled(false);
			button_launch.setText("Try connecting ...");
		}
		else
			JOptionPane.showMessageDialog(this, "Veuillez saisir un pseudo.");
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		
		if(arg0.getPropertyName().equals(WaitGameGuiEvent.FAILURE_CONNECT.toString()))
		{
			button_launch.setText("Rejoindre une partie");
			button_launch.setEnabled(true);
			
			JOptionPane.showMessageDialog(this, "The server not ready.");
		}
		
	}
}
