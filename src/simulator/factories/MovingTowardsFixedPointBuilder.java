package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.ForceLaws;
import simulator.model.MovingTowardsFixedPoint;

public class MovingTowardsFixedPointBuilder extends Builder<ForceLaws> {
	
	protected Vector2D c = new Vector2D();
	protected double g = 9.81;
	
	public MovingTowardsFixedPointBuilder() {
		super("mtfp", "Moving towards fixed point force");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JSONObject createData() {
		JSONObject jo = new JSONObject();
		jo.put("c", "the point towards which bodies move(a json list of 2 numbers, e.g., [100.0, 50.0])");
		jo.put("g", "the length of the acceleration vector (a number)");
		return jo;
	}
	
	@Override
	public ForceLaws createTheInstance(JSONObject data) throws IllegalArgumentException {
		if(data.has("c")) {
			this.c = this.JSONArrayToVector2D(data.getJSONArray("c"));
		}
		if(data.has("g")) {
			this.g = data.getDouble("g");
		}
		return new MovingTowardsFixedPoint(this.c, this.g);
	}

}
