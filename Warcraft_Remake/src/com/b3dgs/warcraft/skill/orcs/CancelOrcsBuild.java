package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class CancelOrcsBuild extends ModelSkill {

    public CancelOrcsBuild(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ORCS_CANCEL", priority, owner);
	this.setUnlocked(true);
	this.setIgnore(true);
	this.setOrder(false);
    }

    @Override
    public void action() {
	this.owner.getSkill("MOVE").setIgnore(false);
	this.owner.getSkill("STOP").setIgnore(false);
	this.owner.getSkill("EXTRACT").setIgnore(false);
	this.owner.getSkill("REPAIR").setIgnore(false);
	this.owner.getSkill("ORCS_STDBUILD").setIgnore(false);
	this.owner.getSkill("ORCS_TOWNHALL").setIgnore(true);
	this.owner.getSkill("ORCS_FARM").setIgnore(true);
	this.owner.getSkill("ORCS_BARRACKS").setIgnore(true);
	this.owner.getSkill("ORCS_LUMBERMILL").setIgnore(true);
	this.owner.getSkill("ORCS_CANCEL").setIgnore(true);
    }
}
