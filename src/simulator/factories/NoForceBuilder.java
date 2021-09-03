package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NoForce;

public class NoForceBuilder extends Builder<ForceLaws> {

	public NoForceBuilder() {
		super("nf", "No force");
	}

	@Override
	public ForceLaws createTheInstance(JSONObject data) throws IllegalArgumentException {
		return new NoForce();
	}

}