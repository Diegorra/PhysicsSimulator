package simulator.model;

import java.util.List;


import simulator.misc.Vector2D;

public class NewtonUniversalGravitation implements ForceLaws {

	protected double G = 6.67E-11;
	
	public NewtonUniversalGravitation(double G) {
		this.G = G;
	}
	
	@Override
	public void apply(List<Body> bs) {
		double fij = 0.0;
		for(Body bodyi : bs) {
			//si la masa del cuerpo es 0 la fuerza resultante es 0
			if(bodyi.mass == 0.0) {
				bodyi.p = new Vector2D();
				bodyi.v = new Vector2D();
			
			}else {
				for(Body bodyj : bs) {
					//un cuerpo no puede ejercer una fuerza sobre si mismo
					if(!bodyi.equals(bodyj)) {
						Vector2D d = new Vector2D(bodyj.p.minus(bodyi.p)); //Calculamos vector d
						
						if(d.magnitude() != 0) {
							fij = (G*bodyi.mass*bodyj.mass)/Math.abs(Math.pow(d.magnitude(), 2)); //calculamos módulo de fij si d es distinto de 0
						}
						bodyi.addForce(d.direction().scale(fij)); //sumamos a la fuerza del body la fuerza fij con direción y sentido d
					}
				}
			}
		}
	}

	@Override
	public String desc() {
		return "Newton´s law of universal gravitation with G= " + G; 
	}

}
