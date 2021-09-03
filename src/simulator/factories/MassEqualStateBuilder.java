package simulator.factories;

import org.json.JSONObject;

import simulator.control.MassEqualState;
import simulator.control.StateComparator;

public class MassEqualStateBuilder extends Builder<StateComparator> {

	public MassEqualStateBuilder() {
		super("masseq", "Mass state comparator");
		// TODO Auto-generated constructor stub
	}

	@Override
	public StateComparator createTheInstance(JSONObject data) throws IllegalArgumentException {
		return new MassEqualState();
	}

}
