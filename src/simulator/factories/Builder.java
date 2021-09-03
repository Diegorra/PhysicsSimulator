package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public abstract class Builder<T> {
	
	protected String typeTag;
	protected String desc;
	
	public Builder(String typeTag, String desc) {
		this.typeTag = typeTag;
		this.desc = desc;
	}
	/*public T createInstance(JSONObject info) {
	if (info == null) {
		throw new IllegalArgumentException("Invalid value for createInstance: null");
	}

	for (Builder<T> bb : _builders) {
		T o = bb.createInstance(info);
		if (o != null)
			return o;
	}

	throw new IllegalArgumentException("Invalid value for createInstance: " + info.toString());
}*/
	public T createInstance(JSONObject info)throws IllegalArgumentException{
		T instance = null;
		
		if(info.getString("type").equals(this.typeTag)) { //si coincide el tipo con tipo de info
			instance = createTheInstance(info.getJSONObject("data")); //tratamos de crear la instancia
		}else {
			throw new IllegalArgumentException("Invalid value for createInstance: " + info.toString());
		}
		
			
		return instance;
	}
	
	public JSONObject getBuilderInfo() {
		JSONObject jo = new JSONObject();
		
		jo.put("type", this.typeTag);
		jo.put("desc", this.desc);
		jo.put("data", createData());
		
		return jo;
		
	}
	
	public JSONObject createData() {
		return new JSONObject();
	}
	
	//método que dado un JSONArray lo transforma a vector2D
	public Vector2D JSONArrayToVector2D(JSONArray s1) throws IllegalArgumentException {
		Vector2D v = new Vector2D();
		try {
			v = new Vector2D(s1.getDouble(0), s1.getDouble(1));
		}
		catch(Exception e) {
			throw new IllegalArgumentException();
		}
		return v;
	}
	
	public abstract T createTheInstance(JSONObject data) throws IllegalArgumentException;
}
