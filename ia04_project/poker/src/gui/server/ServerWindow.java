package gui.server;
	
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import sma.agent.SimAgent;


public class ServerWindow extends JFrame implements PropertyChangeListener {
	
	private Button btn;
	
	private Rectangle table;
	
	private Rectangle zone_carte;
	
	private JComboBox<Integer> list_nb_player;
	private JFormattedTextField nb_tour_increase;
	private JRadioButton radio_distrib_1;
	private JRadioButton radio_distrib_2;
	private JRadioButton radio_distrib_3;
	private JList list_player;
	private JButton button_launch;
	private JButton button_begin;
	
	private SimAgent sim_agent;
	
	public ServerWindow(SimAgent simAgent) {
		JPanel panel = new JPanel();
		
		this.setTitle("Server configuration");
		this.setSize(350, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    
	    this.sim_agent = simAgent;
		
		JLabel label_nb_max = new JLabel("Nombre maximum de joueurs : ");
		Integer[] nb_player = new Integer[]{2,3,4,5,6,7,8,9,10};
		list_nb_player = new JComboBox<Integer>(nb_player);
		list_nb_player.setPreferredSize(new Dimension(300,25));
		
		JLabel label_time_increase = new JLabel("Nombre de tours avant augmentation : ");
		nb_tour_increase = new JFormattedTextField(NumberFormat.getIntegerInstance());
		nb_tour_increase.setPreferredSize(new Dimension(300, 25));
		nb_tour_increase.setText("1");
		
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
		list_player = new JList<String>(subject);
		list_player.setPreferredSize(new Dimension(300, 150));
		list_player.setEnabled(false);
		
		button_launch = new JButton("Lancer le server");
		button_launch.setPreferredSize(new Dimension(250, 40));
		button_begin = new JButton("Commencer la partie");
		button_begin.setPreferredSize(new Dimension(250, 40));
		button_begin.setEnabled(false);
		
		panel.add(label_nb_max);
		panel.add(list_nb_player);
		
		panel.add(label_time_increase);
		panel.add(nb_tour_increase);
		
		panel.add(radio_distrib_1);
		panel.add(radio_distrib_2);
		panel.add(radio_distrib_3);
		
		panel.add(label_players);
		panel.add(list_player);
		
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
	}
	
	private void launchServer() {
		GuiEvent ev = new GuiEvent(this,SimAgent.LAUNCH_SERVER);
		
		Integer nb_player = (Integer)list_nb_player.getSelectedItem();
		ev.addParameter(nb_player);
		
		ev.addParameter(Integer.parseInt(nb_tour_increase.getText()));
		
		int selected_distrib = 0;
		if(radio_distrib_1.isSelected())
			selected_distrib = 1;
		else if(radio_distrib_2.isSelected())
			selected_distrib = 2;
		else if(radio_distrib_3.isSelected())
			selected_distrib = 3;
		
		ev.addParameter(selected_distrib);
		
		sim_agent.postGuiEvent(ev);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		
		
		
	}
}
