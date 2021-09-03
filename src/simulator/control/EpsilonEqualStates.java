package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class EpsilonEqualStates implements StateComparator {

	protected double eps;
	protected MassEqualState mes;
	public EpsilonEqualStates(double eps) {
		this.eps = eps;
		this.mes = new MassEqualState();
	}

	@Override
	public boolean equal(JSONObject s1, JSONObject s2) {
		boolean equal = false;
		
		JSONArray ja1 = s1.getJSONArray("bodies");
		JSONArray ja2 = s2.getJSONArray("bodies");
		
		for(int i = 0; i < ja1.length(); i++) {
			equal = (JSONArraytoVector2D(ja1.getJSONObject(i).getJSONArray("p"),ja2.getJSONObject(i).getJSONArray("p")) && JSONArraytoVector2D(ja1.getJSONObject(i).getJSONArray("v"),ja2.getJSONObject(i).getJSONArray("v")) && JSONArraytoVector2D(ja1.getJSONObject(i).getJSONArray("f"),ja2.getJSONObject(i).getJSONArray("f")));
		}
		return (mes.equal(s1, s2) && equal);
	}
	
	public boolean JSONArraytoVector2D(JSONArray jo1, JSONArray jo2) {
		double x_jo1 = jo1.getDouble(0), x_jo2 = jo2.getDouble(0);
		double y_jo1 = jo1.getDouble(1), y_jo2 = jo2.getDouble(1);
		
		return equalAbsEps(new Vector2D(x_jo1, y_jo1), new Vector2D(x_jo2, y_jo2));
	}
	public boolean equalAbsEps(Vector2D v1, Vector2D v2) {
		return (v1.distanceTo(v2) <= this.eps);
	}

}
