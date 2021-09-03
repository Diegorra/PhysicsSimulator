package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class MovingTowardsFixedPoint implements ForceLaws {
	
	protected Vector2D o;
	protected double g = 9.81;
	
	public MovingTowardsFixedPoint(Vector2D v, double g) {
		this.o = v;
		this.g = g;
	}

	@Override
	public void apply(List<Body> bs) {
		for(Body body : bs) {
			//Segunda ley de Newton : F = m*a
			Vector2D a = new Vector2D((body.p.minus(this.o)).direction().scale(-g));
			body.addForce(a.scale(body.mass));
		}

	}

	@Override
	public String desc() {
		return "Moving towards fixed point force with o=" + o.toString() + "and g=" + g;
	}

}
