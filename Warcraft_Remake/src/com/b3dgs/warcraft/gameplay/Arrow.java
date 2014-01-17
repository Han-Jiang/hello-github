package com.b3dgs.warcraft.gameplay;

import com.b3dgs.lionengine.game.Projectile;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.unit.ModelUnit;

public class Arrow extends Projectile<Arrow> {

    public Arrow(RessourcesHandler rsc) {
        super(rsc.get("ARROW").ressource, 4, 2);
        this.setSpeed(3.0f);
        this.setSize(4, 4);
        this.setLifeTime(1000);
        this.setRelativeHit(true);
    }

    protected Arrow(Arrow arrow) {
        super(arrow);
    }

    @Override
    public Arrow createInstance() {
        return new Arrow(this);
    }

    @Override
    protected void onMove() {
    }

    @Override
    protected void onHitTarget(AbstractEntry<?, ?, ?> target) {
        target.life.decrease(this.damages.getRandomDmg());
        if ((target instanceof ModelBuilding && ((ModelBuilding) target).isOnScreen()) || target instanceof ModelUnit && ((ModelUnit) target).isOnScreen()) {
            ControlPanel.playSfx(SFX.arrow_hit);
        }
    }

    @Override
    protected void onDestoyed() {
    }

    @Override
    public void setSkipLastFrameOnReverse(boolean skip) {
        this.sprite.setSkipLastFrameOnReverse(skip);
    }

    @Override
    public float getRealFrame() {
        return this.sprite.getRealFrame();
    }
}
