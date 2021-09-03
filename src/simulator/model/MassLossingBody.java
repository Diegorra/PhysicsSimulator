package simulator.model;

import simulator.misc.Vector2D;

public class MassLossingBody extends Body {
	
	protected double lossFactor, lossFrequency, c;
	
	public MassLossingBody(String id, double mass, Vector2D v, Vector2D p, double lossFactor, double lossFrequency) {
		super(id, mass, v, p);
		this.lossFactor = lossFactor;
		this.lossFrequency = lossFrequency;
		this.c = 0.0;
	}
	
	public double getLossFactor() {
		return lossFactor;
	}

	public void setLossFactor(double lossFactor) {
		this.lossFactor = lossFactor;
	}

	public double getLossFrequency() {
		return lossFrequency;
	}

	public void setLossFrequency(double lossFrequency) {
		this.lossFrequency = lossFrequency;
	}
	

	void move(double t) {
		super.move(t);
		c += t;
		if(c >= lossFrequency) {
			super.mass = super.mass*(1-this.lossFactor);
			c = 0.0;
		}
	}

}
