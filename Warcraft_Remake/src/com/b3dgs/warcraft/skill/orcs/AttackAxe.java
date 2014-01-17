package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.skill.AttackMelee;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class AttackAxe extends AttackMelee {

    public AttackAxe(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ATTACK_AXE", priority, owner);
    }
}
