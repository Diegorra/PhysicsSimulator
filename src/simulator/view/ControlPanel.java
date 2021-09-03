package simulator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import simulator.control.Controller;
import simulator.control.StateComparator;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

@SuppressWarnings("serial")

public class ControlPanel extends JPanel implements SimulatorObserver {
	
	private Controller _ctrl;
	protected boolean _stopped;
	
	//componentes visuales
	private JToolBar toolB;
	private JButton button_loadBodies, button_selectForce, button_start, button_stop, button_exit;
	private JLabel label_steps, label_deltaTime;
	private JSpinner spinner_steps;
	private JTextField text_deltaTime;
	private ForceLawsDialog sflaws = null;
	
	public ControlPanel(Controller ctrl) {
		this._ctrl = ctrl;
		this._stopped = true;
		initGUI();
		_ctrl.addObserver(this);
	}

	public void initGUI() {
		
		toolB = new JToolBar();
		
		
		
		//--Botón de load bodies--
		this.button_loadBodies = new JButton();
		this.button_loadBodies.setActionCommand("loadBodies");
		this.button_loadBodies.setIcon(loadImage("resources/icons/open.png")); // botón con imagen
		this.button_loadBodies.setToolTipText("Load bodies file into the editor");//describimos efecto al pulsarlo 
		this.button_loadBodies.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadBodies_selected();
			}
		});
		toolB.add(button_loadBodies); //agregamos el botón al ControlPanel
		toolB.addSeparator();
		
		//--Botón de select forces--
		this.button_selectForce = new JButton();
		this.button_loadBodies.setActionCommand("selectForce");
		this.button_selectForce.setIcon(loadImage("resources/icons/physics.png"));
		this.button_selectForce.setToolTipText("Select force");
		this.button_selectForce.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectForce_selected();
			}
		});		

		toolB.add(this.button_selectForce);
		toolB.addSeparator();
		

		//--Botón de start--
		this.button_start = new JButton();		
		this.button_loadBodies.setActionCommand("start");
		this.button_start.setIcon(loadImage("resources/icons/run.png"));
		this.button_start.setToolTipText("run simulation");
		this.button_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				start_selected();
			}
		});
		toolB.add(this.button_start);
		
		
		//--Botón de stop--
		this.button_stop = new JButton();
		this.button_loadBodies.setActionCommand("stop");
		this.button_stop.setIcon(loadImage("resources/icons/stop.png"));
		this.button_stop.setToolTipText("stop simulation");
		this.button_stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_stopped = true;
			}
		});
		toolB.add(button_stop);
		
		//--Label de steps--
		this.label_steps = new JLabel("Steps: ");
		toolB.add(this.label_steps);
		
		//--Spinner de steps--
		this.spinner_steps = new JSpinner(new SpinnerNumberModel(150, 0, 200000, 150));
		toolB.add(this.spinner_steps);
		
		//--Label de delta time--
		this.label_deltaTime = new JLabel("Delta-Time: ");
		toolB.add(this.label_deltaTime);
		
		//--Text de delta time--
		this.text_deltaTime = new JTextField();
		text_deltaTime.setText("10000");
		toolB.add(this.text_deltaTime);
		
		//--Botón de exit--
		this.button_exit = new JButton();
		this.button_loadBodies.setActionCommand("exit");
		this.button_exit.setIcon(loadImage("resources/icons/exit.png"));
		this.button_exit.setToolTipText("exit");
		this.button_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 exit_selected();
			}
		});
		
		for(int i = 0; i < 30; ++i) {
			  toolB.addSeparator();
		}
		
		toolB.add(button_exit);
		
		
		this.add(toolB);//añadimos la tool bar al ControlPanel
	}
	
	private ImageIcon loadImage(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(path));
	}
	

	public void loadBodies_selected() {
		//1.se selecciona un fichero
		JFileChooser fileC = new JFileChooser();
		fileC.setCurrentDirectory(new File("/resources/examples/")); //seleccionamos directorio de origen
		fileC.setMultiSelectionEnabled(false); //no se pueden seleccionar varios ficheros
		fileC.setFileFilter(new FileNameExtensionFilter("Archivos JSON", "json")); //solo se acepta .json
		int ret = fileC.showOpenDialog(this); //salta la ventana
		
		//2.se limpia el simulador
		this._ctrl.reset();
		
		//3. se carga el fichero en el simulador
		if (ret == JFileChooser.APPROVE_OPTION) { //si selecciona un fichero apropiado
			try {
				FileInputStream in = new FileInputStream(fileC.getSelectedFile()); //creamos input stream con el fichero
				this._ctrl.loadBodies(in); //añadimos los bodies
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Ha surgido un error", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	public void selectForce_selected() {
		// se abre una caja de diálogo con las opciones
		if(sflaws == null) {
			 sflaws = new ForceLawsDialog((Frame) SwingUtilities.getWindowAncestor(this), this._ctrl.getForceLawsInfo());
		}
		
		try {
			int status = sflaws.open();
			
			if(status == 1) { //si pulsa el botón ok entonces se establece la ley de fuerza
				_ctrl.setForceLaws(sflaws.getForceLaw());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Los valores introducidos no son correctos", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void changeStateOfButtons(Boolean state) {
		button_loadBodies.setEnabled(state);
		button_selectForce.setEnabled(state);
		button_start.setEnabled(state);
		button_exit.setEnabled(state);
	}
	
	public void start_selected() {
		//1. desactiva todos los botones menos el stop
		changeStateOfButtons(false);
		_stopped = false;
		
		//2. setea el delta time al del textField
		_ctrl.setDeltaTime(Double.parseDouble(this.text_deltaTime.getText()));
		
		//3. llama al run_sim con el valor del spinner
		this.run_sim(Integer.parseInt(spinner_steps.getValue().toString()));
		
	}
	
	public void exit_selected() {
		Object options[] = {"ok", "cancel"};
		int res = JOptionPane.showOptionDialog(this, "Salir", "salir del programa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(res == 0) {
			System.exit(0);
		}
	}
	
	
	//--------------------------------------------
	private void run_sim(int n) {
		if(n > 0 && !this._stopped) {
			try {
				
				OutputStream out = new OutputStream() {
					public void write(int b) throws IOException {
						
					}
				};
				
				InputStream expOut = null;
				StateComparator cmp = null;
				
				this._ctrl.run(1, out, expOut, cmp);
			}
			catch(Exception e) {
				//Mostramos error en una JOptionPane
				JOptionPane.showMessageDialog(this, "Los valores introducidos no son correctos", "ERROR", JOptionPane.ERROR_MESSAGE);
				//Todos los botones a enable
				changeStateOfButtons(true);
				this.button_stop.setEnabled(true);

				
				this._stopped = true;
				return;
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					run_sim(n-1);
				}
				});
		}else {
			this._stopped = true;
			changeStateOfButtons(true);
			this.button_stop.setEnabled(true);
		}
	}
	
	
	//Observers
	//-------------------------------------------------------------------
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String flawsDesc) {
		this.text_deltaTime.setText(Double.toString(dt));
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String flawsDesc) {
		this.text_deltaTime.setText(Double.toString(dt));
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		this.text_deltaTime.setText(Double.toString(dt));
	}

	@Override
	public void onForceLawsChanged(String flwasDesc) {
	}

	
}
