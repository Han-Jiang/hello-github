package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.ProdModel;

public class ProdGrunt extends ProdModel {

    public ProdGrunt(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ORCS_WARRIOR", priority, owner);
	this.setUnlocked(true);
    }
}
