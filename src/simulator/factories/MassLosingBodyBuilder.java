package simulator.factories;

import org.json.JSONObject;

import simulator.model.Body;
import simulator.model.MassLossingBody;

public class MassLosingBodyBuilder extends Builder<Body> {
	
	protected MassLossingBody mlb = null;
	
	public MassLosingBodyBuilder() {
		super("mlb", "Body who loses mass");
	}
	
	@Override
	public JSONObject createData() {
		JSONObject jo = this.mlb.getState();
		jo.put("freq", this.mlb.getLossFrequency());
		jo.put("factor", this.mlb.getLossFactor());
		return jo;
	}
	
	@Override
	public MassLossingBody createTheInstance(JSONObject data) throws IllegalArgumentException {
		if(!data.has("id") || !data.has("m") || !data.has("v") || !data.has("p") || !data.has("factor") || !data.has("freq")) {
			throw new IllegalArgumentException();
		}
		
		this.mlb = new MassLossingBody(data.getString("id"), data.getDouble("m"), JSONArrayToVector2D(data.getJSONArray("v")), JSONArrayToVector2D(data.getJSONArray("p")), data.getDouble("factor"), data.getDouble("freq"));
		return this.mlb;
	}

}
