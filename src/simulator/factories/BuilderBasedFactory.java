package simulator.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;



public class BuilderBasedFactory<T> implements Factory<T> {
	
	protected List<Builder<T>> build ;
	List<JSONObject> ljo;
	List<JSONObject> _factoryElements;
	
	public BuilderBasedFactory(List<Builder<T>> builders) {
		this.build = new ArrayList<Builder<T>>(builders);
		this.ljo = new ArrayList<JSONObject>();
		
		
		this._factoryElements = Collections.unmodifiableList(ljo);
	}

	@Override
	public T createInstance(JSONObject info) {
		T object = null;
		for(Builder<T> b : this.build) {
			if(info.getString("type").equals(b.typeTag)){//si el método no devuelve null creamos la instancia
				object = b.createInstance(info);
			}
			
		}
		return object;
	}

	@Override
	public List<JSONObject> getInfo() {
		for(Builder<T> b : this.build) {
			ljo.add(b.getBuilderInfo());
		}
		return ljo;
	}
	

/*	protected List<Builder<T>> build = new ArrayList<Builder<T>>(); 
	
	public BuilderBasedFactory(List<Builder<T>> builders) {
		this.build = builders;
	}

	@Override
	public T createInstance(JSONObject info) {
		T object = null;
		for(Builder<T> b : this.build) {
			if(info.getString("type").equals(b.typeTag)){//si el método no devuelve null creamos la instancia
				object = b.createInstance(info);
			}
			
		}
		return object;
	}

	@Override
	public List<JSONObject> getInfo() {
		ArrayList<JSONObject> ljo = new ArrayList<JSONObject>();
		
		for(Builder<T> b : this.build) {
			ljo.add(b.getBuilderInfo());
		}
		return ljo;
	}
*/
	

}
