package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.skill.AttackDistance;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class AttackSpear extends AttackDistance {

    public AttackSpear(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("ATTACK_SPEAR", priority, owner);
    }
}
