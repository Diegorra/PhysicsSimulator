package simulator.view;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

	
public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver  {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Body> _bodies;
	private String[] _colNames = { "Id", "Mass", "Position", "Velocity", "Force" };

	public BodiesTableModel(Controller ctrl) {
		_bodies = null;
		ctrl.addObserver(this);
	}
	
	public void update() {
		// observar que si no refresco la tabla no se carga
		// La tabla es la represantación visual de una estructura de datos,
		// en este caso de un ArrayList, hay que notificar los cambios.
		
		// We need to notify changes, otherwise the table does not refresh.
		fireTableDataChanged();;		
	}
	
	public void setBodiesList(List<Body> bodies_l) {
		_bodies = bodies_l;
		update();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	@Override
	public int getRowCount() {
		return _bodies == null ? 0 : _bodies.size();
	}

	@Override
	public String getColumnName(int col) {
		return _colNames[col];
	}

	@Override
	public int getColumnCount() {
		return _colNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String s = null;
		switch (columnIndex) {
		case 0:
			s = _bodies.get(rowIndex).getId();
			break;
		case 1:
			s = "" + _bodies.get(rowIndex).getMass();
			break;
		case 2:
			s = _bodies.get(rowIndex).getP().toString();
			break;
		case 3:
			s = _bodies.get(rowIndex).getV().toString();
			break;
		case 4:
			s = _bodies.get(rowIndex).getF().toString();
			break;
		}
		return s;
	}

		
	//Observers
	//-------------------------------------------------------------------
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String flawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setBodiesList(bodies);
			}
			});
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String flawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setBodiesList(bodies);
			}
			});	
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setBodiesList(bodies);
			}
			});	
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setBodiesList(bodies);
			}
			});	
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onForceLawsChanged(String flwasDesc) {
		// TODO Auto-generated method stub

	}

}
