package com.b3dgs.warcraft.gameplay;

import com.b3dgs.lionengine.utility.Maths;

/**
 * Ressource handler (gold or wood).
 */
public class Ressource extends com.b3dgs.lionengine.game.strategy.Ressource {

    private float value;

    public Ressource(int amount) {
	super(amount);
	this.value = amount;
    }

    /** Update progressive value effect.
     * @param extrp extrapolation reference.
     * @param speed progressive speed.
     */
    public void update(float extrp, float speed) {
	this.value = Maths.curveValue(this.value, this.get(), speed / extrp);
	if (this.value >= this.get() - 0.1f && this.value <= this.get() + 0.1f) {
	    this.value = this.get();
	}
    }

    /** Get current progressive value.
     * @return progressive value.
     */
    public float getValue() {
	return this.value;
    }
}
