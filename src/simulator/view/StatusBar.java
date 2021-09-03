package simulator.view;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel implements SimulatorObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel _currTime, _currLaws, _numBodies;
	
	

	public StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	
	public void initGUI() {
		this.setLayout(new  FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		JSeparator separator = new JSeparator();
		
		_currTime = new JLabel("Time: ");
		this.add(_currTime);
		this.add(separator);
				
		_numBodies = new JLabel("Bodies: ");
		this.add(_numBodies);
		this.add(separator);
		
		_currLaws = new JLabel("Laws: ");
		this.add(_currLaws);
		
		setVisible(true);
	}
	
	
	
	
	//Observers
	//-------------------------------------------------------------------
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String flawsDesc) {
		_currTime.setText("Time: " + time);
		_numBodies.setText("Bodies: " + bodies.size());
		_currLaws.setText("Laws: " + flawsDesc);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String flawsDesc) {
		_currTime.setText("Time: " + time);
		_numBodies.setText("Bodies: " + bodies.size());
		_currLaws.setText("Laws: " + flawsDesc);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		_numBodies.setText("Bodies: " + bodies.size());
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		_currTime.setText("Time: " + time);
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onForceLawsChanged(String flwasDesc) {
		_currLaws.setText("Laws: " + flwasDesc);
	}

}
