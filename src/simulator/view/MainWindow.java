package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import simulator.control.Controller;

@SuppressWarnings("serial")

public class MainWindow extends JFrame {
	
	protected Controller _ctrl;
	
	public MainWindow(Controller ctrl) throws HeadlessException {
		super("Physics Simulator");
		this._ctrl = ctrl;
		initGUI();
	}
	
	public void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		//--ControlPanel--
		ControlPanel ctrl_Panel = new ControlPanel(this._ctrl);
		mainPanel.add(ctrl_Panel, BorderLayout.PAGE_START);
		
		//--BodiesTable--
		BodiesTable bTable = new BodiesTable(this._ctrl);
		bTable.setPreferredSize(new Dimension(800, 200));
		bTable.setMinimumSize(new Dimension(800, 200));
		centerPanel.add(bTable);
		
		//--Viewer--
		Viewer viewer = new Viewer(this._ctrl);
		viewer.setPreferredSize(new Dimension(800, 600));
		viewer.setMinimumSize(new Dimension(800, 600));
		centerPanel.add(viewer);
				
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		//--StatusBar--
		StatusBar status = new StatusBar(this._ctrl);
		this.add(status, BorderLayout.PAGE_END);
		
		centerPanel.add(new JScrollPane(viewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		
	}

	//Private & Protected methods
	//------------------------------------
	
}
