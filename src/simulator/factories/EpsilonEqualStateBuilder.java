package simulator.factories;

import org.json.JSONObject;

import simulator.control.EpsilonEqualStates;
import simulator.control.StateComparator;

public class EpsilonEqualStateBuilder extends Builder<StateComparator> {

	protected double eps = 0.0;
	
	public EpsilonEqualStateBuilder() {
		super("epseq", "Epsilon comparator");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JSONObject createData() {
		JSONObject jo = new JSONObject();
		jo.put("eps", this.eps);
		return jo;
	}
	
	@Override
	public StateComparator createTheInstance(JSONObject data) throws IllegalArgumentException {
		if(data.has("eps")) {
			this.eps = data.getDouble("eps");
		}
		return new EpsilonEqualStates(this.eps);
	}

}
