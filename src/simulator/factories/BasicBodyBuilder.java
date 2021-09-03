package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;

public class BasicBodyBuilder extends Builder<Body> {

	protected Body body = null;
	
	public BasicBodyBuilder() {
		super("basic", "Basic body");
		
	}

	@Override
	public JSONObject createData() {
		return this.body.getState();
	}

	@Override
	public Body createTheInstance(JSONObject data) throws IllegalArgumentException {
		if(!data.has("id") || !data.has("m") || !data.has("v") || !data.has("p")) {
			throw new IllegalArgumentException();
		}
		
		this.body = new Body(data.getString("id"), data.getDouble("m"), JSONArrayToVector2D(data.getJSONArray("v")), JSONArrayToVector2D(data.getJSONArray("p"))); 
		return body;
	}
	
}
