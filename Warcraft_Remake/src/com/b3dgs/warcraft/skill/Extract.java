package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.RessourceType;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.GoldMine;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.CollisionType;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.unit.ModelWorker;

public class Extract extends ModelSkill {

    public Extract(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("EXTRACT", priority, owner);
        this.setUnlocked(true);
    }

    @Override
    public void action() {
        if (this.owner instanceof ModelWorker) {
            ModelWorker worker = (ModelWorker) this.owner;
            ModelBuilding b = worker.player().getClosestBuilding(worker, BuildingType.TOWNHALL);
            if (b != null) {
                worker.stop();
                int dx = this.destX;
                int dy = this.destY;
                int id = worker.map.getRef(dy, dx);
                if (id > 0) {
                    ModelBuilding e = ModelBuilding.get(id);
                    if (e instanceof GoldMine) {
                        worker.ignoreID(id, true);
                        worker.setRessourceType(RessourceType.GOLD);
                        worker.player().incWorkersOnGold(1);
                        dx = e.getXInTile() + e.getWidthInTile() / 2;
                        dy = e.getYInTile() + e.getHeightInTile() / 2;
                    }
                } else {
                    if (worker.map.getTile(dy, dx).getCollType() == CollisionType.TREE) {
                        worker.setRessourceType(RessourceType.WOOD);
                        worker.player().incWorkersOnWood(1);
                    }
                }
                worker.setRessourceLocation(dx, dy);
                worker.setWarehouse(b.getXInTile() + b.getWidthInTile() / 2, b.getYInTile() + b.getHeightInTile() / 2);
                worker.startExtraction();
                ControlPanel.playSfx(worker.getOwnerID(), worker.faction, SFX.confirm);
            }
        }
    }
}
