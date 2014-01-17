package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.AttackDistance;
import com.b3dgs.warcraft.skill.ModelSkill;

public class AttackArrow extends AttackDistance {

    public AttackArrow(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("ATTACK_ARROW", priority, owner);
    }
}
