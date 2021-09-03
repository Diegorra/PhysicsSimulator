package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class PhysicsSimulator {
	
	protected double tReal, tActual;
	protected ForceLaws fl = null;
	protected List<Body> lb = null;
	protected List<Body> bodiesUnmodifiable = null;
	protected ArrayList<SimulatorObserver> observer = null;
	
	public PhysicsSimulator(ForceLaws fl, double t) {
		try {
			if(fl.equals(null) || t <= 0) {
				throw new IllegalArgumentException();
			}
			else {
				this.tReal = t;
				this.tActual = 0.0;
				this.fl = fl;
				this.lb = new ArrayList<Body>();
				this.observer = new ArrayList<SimulatorObserver>();
				this.bodiesUnmodifiable = Collections.unmodifiableList(lb);
			}
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public void advance() {
		
		for(Body body : lb) {
			body.resetForce(); //Reseteamos las fuerzas
		}
		
		fl.apply(lb);//Aplicamos la fuerza a todos los cuerpos
		
		
		for(Body body : lb) {
			body.move(this.tReal); //Movemos los cuerpos
		}
		
		this.tActual += this.tReal; //Actualizamos el tiempo
		
		for(SimulatorObserver o : this.observer) {
			o.onAdvance(bodiesUnmodifiable, tActual);
		}
	}
	
	//----------------------------------------------
	public void addBody(Body b) {
			try {
				if(!lb.contains(b)) {
					lb.add(b); //añadimos elemento b en la última posición
					
					for(SimulatorObserver o : this.observer) {
						o.onBodyAdded(bodiesUnmodifiable, b);
					}
					
				}else {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
	}
	
	public void addObserver(SimulatorObserver o) {
		try {
			if(!observer.contains(o)) {//faltaria metodo equals???
				observer.add(o); //añadimos elemento ob en la última posición
				o.onRegister(bodiesUnmodifiable, tActual, tReal, fl.desc()); //registramos el estado del simulador
			}else {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	//--------------------------------------------------------
	public JSONObject getState() {
		JSONObject job = new JSONObject();
		JSONArray jar = new JSONArray();
		
		job.put("time", this.tActual);
		for(Body body : lb) {
			jar.put(body.getState());
		}
		job.put("bodies", jar);
		
		return job;
	}
	
	public String toString() {
		return getState().toString();
	}
	
	public void reset() {
		this.lb.clear(); //vaciamos lista de bodies
		this.tReal = 0;//ponemos el tiempo a 0
		
		for(SimulatorObserver o : this.observer) { //registramos el reset para todos los observadores
			o.onReset(bodiesUnmodifiable, tActual, tReal,fl.desc());
		}
	}
	
	//Getters & Setters
	//---------------------------------------------------
	public void settReal(double tReal) {
		this.tReal = tReal;
		
		for(SimulatorObserver o : this.observer) {
			o.onDeltaTimeChanged(tReal);
		}
	}

	public void setFl(ForceLaws fl) {
		this.fl = fl;
		
		for(SimulatorObserver o : this.observer) {
			o.onForceLawsChanged(fl.desc());
		}
	}

	public List<Body> getLb() {
		return lb;
	}

	public void setLb(List<Body> lb) {
		this.lb = lb;
	}

	
	
}






