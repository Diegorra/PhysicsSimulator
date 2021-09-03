package simulator.model;

import org.json.JSONObject;

import simulator.misc.Vector2D;

public class Body {

	protected String id;
	protected double mass;
	protected Vector2D v, f, p;
	
	public Body(String id, double mass, Vector2D v, Vector2D p) {
		this.id = id;
		this.mass = mass;
		this.p = new Vector2D(p);
		this.v = new Vector2D(v);
		this.f = new Vector2D();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public Vector2D getV() {
		return v;
	}

	public void setV(Vector2D v) {
		this.v = v;
	}

	public Vector2D getF() {
		return f;
	}

	public void setF(Vector2D f) {
		this.f = f;
	}

	public Vector2D getP() {
		return p;
	}

	public void setP(Vector2D p) {
		this.p = p;
	}
	
	//add force f to this.f
	void addForce(Vector2D force) {
		this.f = new Vector2D(f.plus(force));
	}
	
	//reset force with values 0.0
	void resetForce() {
		this.f = new Vector2D();
	}
	
	void move(double t) {
		Vector2D a = new Vector2D();
		
		if(this.mass != 0.0) {
			a = new Vector2D(a.plus(this.f.scale(1/this.mass)));
		}
		
		this.p = new Vector2D(this.p.plus(this.v.scale(t)).plus(a.scale((1/2)*Math.pow(t,2))));
		this.v = new Vector2D(this.v.plus(a.scale(t)));
			
		
	}
	
	//método que pasamos al contains para ver que no son iguales
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Body other = (Body) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}
	
	public JSONObject getState() {
		JSONObject ob = new JSONObject();
		
		ob.put("id", this.id);
		ob.put("m", this.mass);
		ob.put("v", this.v.asJSONArray());
		ob.put("f", this.f.asJSONArray());
		ob.put("p", this.p.asJSONArray());
		
		return ob;
	}
	
	public String toString() {
		return getState().toString();
	}

}
