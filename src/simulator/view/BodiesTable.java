package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import simulator.control.Controller;
import simulator.model.Body;

public class BodiesTable extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BodiesTableModel _bodiesModel;
	private JTable _bodiesTable;
	private Controller _ctrl;
	
	private List<Body> _bodiesList;
	
	public BodiesTable(Controller ctrl) {
		_ctrl = ctrl;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black, 2),
				"Bodies",
				TitledBorder.LEFT, TitledBorder.TOP));
		initGUI();
	}
	
	public void initGUI() {
		
		_bodiesModel = new BodiesTableModel(_ctrl);
		_bodiesTable = new JTable(_bodiesModel) {
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
	
		JScrollPane tableScroll = new JScrollPane(_bodiesTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
		
		
		_bodiesList = new ArrayList<Body>();
		_bodiesList = _ctrl.getPs().getLb();
		_bodiesModel.setBodiesList(_bodiesList);
		
		
		this.add(tableScroll);
		
		
		
		
		setVisible(true);
	}
	
}
