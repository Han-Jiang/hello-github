package com.b3dgs.warcraft.ability;

import com.b3dgs.lionengine.game.strategy.AbstractBuilding;
import com.b3dgs.lionengine.game.strategy.AbstractEntryHandler;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.ability.AbstractBuilderAbility;
import com.b3dgs.lionengine.game.strategy.ability.BuilderAbility;
import com.b3dgs.lionengine.geometry.Point2D;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.AI;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.Cost;
import com.b3dgs.warcraft.gameplay.Costs;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelWorker;

public class BuilderAbilityImpl extends AbstractBuilderAbility<Tile, ModelSkill, Attributes>
        implements BuilderAbility<Tile, ModelSkill, Attributes> {

    private final AbstractEntryHandler<Tile, ModelSkill, Attributes> handler;
    private final EntryFactory factory;
    private final ModelWorker worker;

    public BuilderAbilityImpl(AbstractUnit<Tile, ModelSkill, Attributes> builder,
            AbstractEntryHandler<Tile, ModelSkill, Attributes> handler,
            EntryFactory factory) {
        super(builder, handler);
        this.handler = handler;
        this.factory = factory;
        this.worker = (ModelWorker) builder;
    }

    @Override
    public AbstractBuilding<Tile, ModelSkill, Attributes> getBuildingToBuild(String name) {
        int i = name.indexOf('_');
        Race race = Race.valueOf(name.substring(0, i).toLowerCase());
        BuildingType type = BuildingType.valueOf(name.substring(i + 1, name.length()));
        return this.factory.createBuilding(type, race);
    }

    @Override
    public void startConstruction(String name) {
        Player p = this.worker.player();
        Cost cost = Costs.get(name);
        boolean free = this.worker.map.checkFreePlace(this.worker.getXInTile(), this.worker.getYInTile(), cost.tw, cost.th, this.worker.id);
        if (free && p.gold.canSpend(cost.gold.get()) && p.wood.canSpend(cost.wood.get())) {
            p.gold.spend(cost.gold.get());
            p.wood.spend(cost.wood.get());
            super.startConstruction(name);
            this.worker.setVisibility(false);
            this.worker.setActive(false);
            this.worker.unselect();
            this.worker.setOver(false);
            this.handler.selection.remove(this.worker);
            ControlPanel.playSfx(this.worker.getOwnerID(), null, SFX.construction);
        } else {
            this.constructionRefused(this.cur);
        }
    }

    @Override
    public void constructionRefused(Constructible c) {
        if (this.worker.player() instanceof AI) {
            Point2D free = this.worker.map.getFreePlaceArround(c.tx - 1, c.ty - 1, c.tw + 2, c.th + 2, this.worker.id, 32);
            if (free != null) {
                c.tx = free.getX() + 1;
                c.ty = free.getY() + 1;
                this.worker.assignDestination(c.tx, c.ty);
            }
        } else {
            this.worker.player().incWorkersOnConstructing(-1);
            this.worker.stop();
        }
    }

    @Override
    public boolean canBuild(Constructible c) {
        if (this.worker.player() instanceof AI) {
            if (!this.worker.map.checkFreePlace(c.tx - 1, c.ty - 1, c.tw + 2, c.th + 2, this.worker.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void stopBuild() {
        if (this.cur != null) {
            this.adjustWorkersCount(this.cur.name, -1);
        }
        super.stopBuild();
    }

    @Override
    public void willBuild(String name) {
        this.adjustWorkersCount(name, 1);
    }

    /** This function will inform the player owner about his workers activities.
     * Used by the AI to know who is doing what.
     * @param name construction name.
     * @param side incrementation value.
     */
    private void adjustWorkersCount(String name, int side) {
        BuildingType type = BuildingType.valueOf(name.substring(this.worker.getDataString("FACTION").length() + 1));
        switch (type) {
            case TOWNHALL:
                this.worker.player().incWorkersOnTownHall(side);
                break;
            case FARM:
                this.worker.player().incWorkersOnFarm(side);
                break;
            case BARRACKS:
                this.worker.player().incWorkersOnBarracks(side);
                break;
            case LUMBERMILL:
                this.worker.player().incWorkersOnLumberMill(side);
                break;
        }
    }

    @Override
    public void onConstructed(AbstractBuilding<Tile, ModelSkill, Attributes> building) {
        ModelBuilding build = (ModelBuilding) building;
        Map map = this.worker.map;
        Tile tile = map.getTileArround(this.worker, build.getYInTile(), build.getXInTile(), build.getWidthInTile(), build.getHeightInTile());
        if (tile != null) {
            this.worker.place(tile.getX() / map.getTileWidth(), -tile.getY() / map.getTileHeight());
        }
        this.worker.setVisibility(true);
        this.worker.setActive(true);
        this.worker.setAvailable(true);
        this.worker.player().incWorkersOnConstructing(-1);
        this.adjustWorkersCount(this.cur.name, -1);
        build.player().onBuilt(build);
        super.onConstructed(building);
    }
}
