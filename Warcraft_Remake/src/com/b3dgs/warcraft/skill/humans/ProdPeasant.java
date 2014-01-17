package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.ProdModel;

public class ProdPeasant extends ProdModel {

    public ProdPeasant(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("HUMANS_WORKER", priority, owner);
        this.setUnlocked(true);
    }
}
