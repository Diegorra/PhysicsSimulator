package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravityBuilder extends Builder<ForceLaws> {
	
	protected double G = 6.67e-11;
	
	public NewtonUniversalGravityBuilder() {
		super("nlug", "Newton´s law of universal gravitation");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JSONObject createData() {
		JSONObject jo = new JSONObject();
		jo.put("G","the gravitational constant (a number)");
		return jo;
	}
	
	@Override
	public ForceLaws createTheInstance(JSONObject data) throws IllegalArgumentException {
		if(data.has("G")) {
			this.G = data.getDouble("G");
		}
		return new NewtonUniversalGravitation(this.G);
	}

}
