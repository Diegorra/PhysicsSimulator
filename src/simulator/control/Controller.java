package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;
import simulator.model.Body;
import simulator.model.ForceLaws;

public class Controller {
	
	protected PhysicsSimulator ps;
	protected Factory<Body> fb;
	protected Factory<ForceLaws> fflaws;
	
	public Controller(PhysicsSimulator ps, Factory<Body> fb, Factory<ForceLaws> fflaws) {
		this.ps = ps;
		this.fb = fb;
		this.fflaws = fflaws;
	}
	
	public void reset() {
		this.ps.reset();
	}
	
	public void setDeltaTime(double dt) {
		this.ps.settReal(dt);
	}
	
	public void addObserver(SimulatorObserver o) {
		this.ps.addObserver(o);
	}
	
	public List<JSONObject> getForceLawsInfo(){
		return this.fflaws.getInfo();
	}
	
	public void setForceLaws(JSONObject info) {
		ForceLaws fLaws  = this.fflaws.createInstance(info);
		this.ps.setFl(fLaws);
	}
	
	public void loadBodies(InputStream in)throws IllegalArgumentException {
		JSONObject jsonInupt = new JSONObject(new JSONTokener(in));
		JSONArray jsonArInupt = new JSONArray();
		
		jsonArInupt = jsonInupt.getJSONArray("bodies"); //volcamos en el JSONArray el array de bodies
		
		for(int i=0; i < jsonArInupt.length(); i++) { //recorremos el array
			Body b = fb.createInstance(jsonArInupt.getJSONObject(i)); //tratamos de crear las instancias de bodies a través de la factoría
			ps.addBody(b); //añadimos los bodies al simulador
		}
	}
	
	public void run(int n, OutputStream out, InputStream expOut, StateComparator cmp) {
		
		JSONArray jsonArOut = new JSONArray();
		PrintStream p = new PrintStream(System.out);
		
		JSONObject jsonExpOut = null;
		JSONArray jsonArExpOut = null ;
		
		if(expOut != null) {
			jsonExpOut = new JSONObject(new JSONTokener(expOut));
			jsonArExpOut = new JSONArray();
			jsonArExpOut = jsonExpOut.getJSONArray("states"); //volcamos en un JSONArray el array de estados
		}
		
		
		
		//escribir en un OutputStream
		if(out != null) {
			p = new PrintStream(out);
		}
		p.println("{");
		p.println("\"states\":[");
		
		//estado inicial del simulador
		jsonArOut.put(ps.getState());
		p.println(jsonArOut.get(0));//añadimos el estado al OutputStream
		
		
		//ejecutamos n pasos de simulación y volcamos cada uno de los estados en un JSONArray
		try {
			for(int i=1; i <= n; i++) { 
				p.print(",");
				ps.advance();
				jsonArOut.put(ps.getState());
				if(expOut != null) { //procedemos a comparar
					if(!cmp.equal(jsonArOut.getJSONObject(i), jsonArExpOut.getJSONObject(i))) {
							throw new NonEqualException("Difference between: " + jsonArOut.getJSONObject(i).toString() + " and " + jsonArExpOut.getJSONObject(i).toString() + " in step: " + i);
					}
				}
				p.println(jsonArOut.get(i)); //añadimos cada estado al OutpuStream
			}
		}catch(NonEqualException notE) {
			p.println(notE.getMessage());
		}
		
		p.println("]");
		p.println("}");
	}
	//-----------------------------------------------------
	
	public PhysicsSimulator getPs() {
		return ps;
	}

	public void setPs(PhysicsSimulator ps) {
		this.ps = ps;
	}
}

