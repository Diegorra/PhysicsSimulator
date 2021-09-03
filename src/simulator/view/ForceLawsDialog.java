package simulator.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONArray;
import org.json.JSONObject;



public class ForceLawsDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private List<JSONObject> _listFL;
	private int _status;
	private JComboBox<String> _flaws;
	private ForceLawsTableModel _dataTableModel;
	
	private class ForceLawsTableModel extends AbstractTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] _header = { "Key", "Value", "Description" };
		String[][] _data;
		
		
		public ForceLawsTableModel() {
			_data = new String[2][3];
			clear();
			setValueAt("G", 0, 0);
			setValueAt("the gravitational constant(a number)", 0, 2);
			update();
		}
		
		public void update() {
			fireTableDataChanged();
		}
		
		public void clear() {
			for (int i = 0; i < _data.length; i++)
				for (int j = 0; j < 3; j++)
					_data[i][j] = "";
			
			fireTableStructureChanged();
		}
		
		@Override
		public String getColumnName(int column) {
			return _header[column];
		}

		@Override
		public int getRowCount() {
			return _data.length;
		}

		@Override
		public int getColumnCount() {
			return _header.length;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 1) { //solo se puede editar el campo value
				return true;
			}
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _data[rowIndex][columnIndex];
		}

		@Override
		public void setValueAt(Object o, int rowIndex, int columnIndex) {
			_data[rowIndex][columnIndex] = o.toString();
		}
		
		public JSONObject getData() {
			JSONObject jo = new JSONObject();
			for (int i = 0; i < _data.length; i++) {
				if (!_data[i][0].isEmpty() && !_data[i][1].isEmpty()) {
			
					if(_data[i][0].equals("c")) { //si tenemos c hay que parsearlo a JSONArray
						JSONArray ja = new JSONArray(_data[i][1]);
						jo.put(_data[i][0], ja);
					}else {
						jo.put(_data[i][0], Double.parseDouble(_data[i][1]));
					}
			
				}
			}
			return jo;
		}
		
		public void refresh(JSONObject jo) {
			clear();
			//meter nuevos datos deserializando el JSON
			
			int i=0;
			for (String key : jo.keySet()) {
					setValueAt(key, i, 0);
					setValueAt(jo.getString(key), i, 2); //cogemos el valor
					i++;
			}			
			update();
		}
		
		
	}

	public ForceLawsDialog(Frame owner,List<JSONObject> _listFL ) {
		super(owner, true);
		this._listFL = _listFL;
		initGUI();
	}

	public void initGUI() {
		
		_status = 0;
		
		setTitle("Force Laws Selection");
		
		//AJUSTES PANEL PRINCIPAL
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		//TEXTO DE CABECERA
		JLabel text = new JLabel("Select a force law and provide values for the parameters in the Value Column(dafoult values are used for parameters with no value)");
		text.setAlignmentX(RIGHT_ALIGNMENT);
		mainPanel.add(text);
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));//espacion separación
		
		//TABLA
		_dataTableModel = new ForceLawsTableModel();//de momento no hay nada en la tabla
		JTable dataTable = new JTable(_dataTableModel) {
			
			private static final long serialVersionUID = 1L;

			// we override prepareRenderer to resized rows to fit to content
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			}
		};
		
		JScrollPane tableScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(tableScroll);
		
								
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));//espacion separación

		//COMBOBOX
		JPanel viewsPanel = new JPanel();//creamos una panel para el ComboBox
		viewsPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(viewsPanel);
				
		JLabel helptxt = new JLabel("Force Law: ");
		viewsPanel.add(helptxt);
				
		_flaws = new JComboBox<>();
		
		 //inicializamos el combo box
		_flaws.addItem(_listFL.get(0).getString("desc"));
		_flaws.addItem(_listFL.get(1).getString("desc"));
		_flaws.addItem(_listFL.get(2).getString("desc"));
		
		_flaws.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Según la opción escogida pasamos un JSONObject de la lista de leyes de fuerza
				_dataTableModel.refresh(_listFL.get(_flaws.getSelectedIndex()).getJSONObject("data"));
			}
		});
		viewsPanel.add(_flaws);
		
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));//espacion separación
		
		//BOTONES OK Y CANCEL
		JPanel buttonsPanel = new JPanel(); //creamos un panel para los botones
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(buttonsPanel);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				ForceLawsDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 1;
				ForceLawsDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(okButton);
		
		//OTRAS CONFIGURACIONES DE LA VENTANA
		setPreferredSize(new Dimension(600, 400));
		pack();
		setResizable(true);
		setVisible(false);
		
	}
	
	public int open() {
		
		if (getParent() != null)
			setLocation(//
					getParent().getLocation().x + getParent().getWidth() / 2 - getWidth() / 2, //
					getParent().getLocation().y + getParent().getHeight() / 2 - getHeight() / 2);
		
		
		pack();
		setVisible(true);
		return _status;
	}
	
	public JSONObject getForceLaw() {
		JSONObject jo = new JSONObject();
				
		jo.put("type",_listFL.get(_flaws.getSelectedIndex()).getString("type"));
		jo.put("data", _dataTableModel.getData());
		
		return jo;
	}

}
