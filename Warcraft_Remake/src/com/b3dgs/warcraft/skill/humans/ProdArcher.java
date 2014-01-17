package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.ProdModel;

public class ProdArcher extends ProdModel {

    public ProdArcher(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("HUMANS_THROWER", priority, owner);
    }
}
