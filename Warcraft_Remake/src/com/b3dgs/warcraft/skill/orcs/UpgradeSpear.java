package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class UpgradeSpear extends ModelSkill {

    public UpgradeSpear(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("UPGRADE_SPEAR", priority, owner);
        this.setOrder(false);
        this.setUnlocked(true);
        this.setLvl(2);
    }

    @Override
    public final void setLvl(int lvl) {
        super.setLvl(lvl);
        if (lvl >= 3) {
            this.setIgnore(true);
        }
    }

    @Override
    public void action() {
        Player player = (Player) Player.get(this.owner.getOwnerID());
        player.incSpearLvl();
    }
}
