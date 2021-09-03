package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;
import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class Viewer extends JComponent implements SimulatorObserver {

	//constates para la dimensi�n del componente
	private static final int _WIDTH =  1000, _HEIGTH = 1000;
	
	//constantes para los colores
	private static final Color red = Color.RED;
	private static final Color blue = Color.BLUE;
	private static final Color green = Color.GREEN;
	private static final Color black = Color.BLACK;
	
	//creamos una constante para el campo help
	private static final String help = "h: toggle help, v: toggle vectors, +; zoom-in, -: zoom-out, =: fit";
	
	private int _centerX, _centerY;
	private double _scale;
	private List<Body> _bodies;
	private boolean _showHelp, _showVectors;
	
	
	
	public Viewer(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	
	public void initGUI() {
		
		//implementamos un borde con t�tulo viewer
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black, 2),
				"Viewer",
				TitledBorder.LEFT, TitledBorder.TOP));
		
		
		//inicializamos campos
		_bodies = new ArrayList<Body>();
		_scale = 1.0;
		_showHelp = true;
		_showVectors = true;
		
		
		
		this.setPreferredSize(new Dimension(_WIDTH, _HEIGTH));//fijamos el tama�o de la ventana
		this.setMinimumSize(new Dimension(_WIDTH, _HEIGTH));
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyChar()) {
					case '-':
						_scale  = _scale * 1.1;
						repaint();
						break;
					case '+':
						_scale = Math.max(1000.0, _scale/1.1);
						repaint();
						break;
					case '=':
						autoScale();
						repaint();
						break;
					case 'h':
						_showHelp = !_showHelp;
						repaint();
						break;
					case 'v':
						_showVectors = ! _showVectors;
						repaint();
						break;
					default:
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	@Override
	protected void  paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//calculamos el centro
		_centerX = getWidth()/2;
		_centerY = getHeight()/2;
		
		
		//dibujamos cruz en el centro
		gr.setColor(red);
		gr.drawLine(_centerX-5, _centerY,_centerX+5, _centerY);
		gr.drawLine(_centerX, _centerY-5,_centerX,_centerY+5);
		
		
		
		//si _showHelp esta a true mostramos help
		if(_showHelp) {
			gr.setColor(red);
			gr.drawString(help, 10, 30);
			gr.drawString("Scaling ratio: " + _scale, 10, 45);
		}
		
		//dibujamos los cuerpos
		for(Body b : _bodies) {
			gr.setColor(blue);
			int x = _centerX + (int) (b.getP().getX() / _scale);
			int y = _centerY - (int) (b.getP().getY() / _scale);
			gr.fillOval(x-5, y-5, 11, 11); //dibujamos el cuerpo con color azul
			gr.setColor(black);
			gr.drawString(b.getId(), x-5, y-5); //indicamos el id del cuerpo
			if(_showVectors) { //si _showVectors a true, mostramos los vectores
				Vector2D v = b.getV().direction().scale(20);
				int x2 = x + (int) v.getX();
				int y2 = y - (int) v.getY();
				drawLineWithArrow(gr, x, y, x2, y2, 3, 3, green, green);
				Vector2D f = b.getF().direction().scale(20);
				int x1 = x + (int) f.getX();
				int y1 = y - (int) f.getY();
				drawLineWithArrow(gr, x, y, x1, y1, 3, 3, red, red);

			}
		}
	}
	
	private void autoScale() {
		double max = 1.0;
		
		for(Body b: _bodies) {
			Vector2D p = b.getP();
			max = Math.max(max, Math.abs(p.getX()));
			max = Math.max(max, Math.abs(p.getY()));
		}
		
		double size = Math.max(1.0, Math.min(getWidth(), getHeight()));
		
		_scale = max > size ? 4.0 * max/size : 1.0;
		
	}
	
	private void drawLineWithArrow(Graphics g, int x1, int y1, int x2, int y2, int w, int h, Color lineColor, Color arrowColor){
		
		int dx = x2-x1, dy = y2-y1;
		double D = Math.sqrt(dx*dx + dy*dy);
		double xm = D - w, xn = xm, ym = h, yn = -h, x;
		double sin = dy/ D, cos = dx/D;
		
		x = xm * cos - ym * sin + x1;
		ym = xm * sin + ym * cos + y1;
		xm = x;

		x = xn * cos - yn * sin + x1;
		yn = xn * sin + yn * cos + y1;
		xn = x;

		int[] xpoints = { x2, (int) xm, (int) xn };
		int[] ypoints = { y2, (int) ym, (int) yn };

		
		g.setColor(lineColor);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(arrowColor);
		g.fillPolygon(xpoints, ypoints, 3);		
	}
	
	
	//Observers
	//-------------------------------------------------------------------
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String flawsDesc) {
		_bodies = bodies;
		autoScale();
		repaint();
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String flawsDesc) {
		_bodies = bodies;
		autoScale();
		repaint();
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		_bodies = bodies;
		autoScale();
		repaint();
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		_bodies = bodies;
		repaint();
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
