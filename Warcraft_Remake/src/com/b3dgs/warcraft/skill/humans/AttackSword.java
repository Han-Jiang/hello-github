package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.AttackMelee;
import com.b3dgs.warcraft.skill.ModelSkill;

public class AttackSword extends AttackMelee {

    public AttackSword(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("ATTACK_SWORD", priority, owner);
    }
}
