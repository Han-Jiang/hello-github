package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.ProdModel;

public class ProdSpearman extends ProdModel {

    public ProdSpearman(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ORCS_THROWER", priority, owner);
    }
}
