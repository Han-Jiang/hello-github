package com.b3dgs.warcraft.unit;

import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.game.strategy.AbstractBuilding;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractEntryHandler;
import com.b3dgs.lionengine.game.strategy.ability.BuilderAbility;
import com.b3dgs.lionengine.game.strategy.ability.ExtractAbility;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.ability.BuilderAbilityImpl;
import com.b3dgs.warcraft.ability.ExtractAbilityImpl;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.RessourceType;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.Extract;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.Repair;
import java.awt.image.BufferedImage;

public abstract class ModelWorker extends ModelUnit
        implements BuilderAbility<Tile, ModelSkill, Attributes>, ExtractAbility<Tile, ModelSkill, Attributes, RessourceType> {

    private static final long REPAIR_TIME = 500;
    private static final int REPAIR_GOLD_COST = 10;
    private static final int REPAIR_WOOD_COST = 5;
    private static final int REPAIR_LIFE_INC = 5;
    private final BuilderAbility<Tile, ModelSkill, Attributes> builder;
    private final ExtractAbility<Tile, ModelSkill, Attributes, RessourceType> extract;
    private boolean available, repair;
    private ModelBuilding toRepair;
    private long repairTimer;

    public ModelWorker(Map map, RessourcesHandler rsch, MediaRessource<BufferedImage> rsc,
            AbstractEntryHandler<Tile, ModelSkill, Attributes> handler,
            EntryFactory factory) {

        super(map, rsch, rsc.file, rsc.ressource);
        this.builder = new BuilderAbilityImpl(this, handler, factory);
        this.extract = new ExtractAbilityImpl(this);
        this.addSkill(new Repair(2, this));
        this.addSkill(new Extract(3, this));
        this.available = true;
        this.toRepair = null;
    }

    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        super.update(keyboard, mouse, extrp);
        this.updateConstruction(extrp);
        this.updateExtraction(extrp);
        if (this.isAlive()) {
            this.checkRepair();
        }
    }

    private void checkRepair() {
        if (!this.repair) {
            if (this.hasReachedDestination() && this.toRepair != null && this.toRepair.isAlive()) {
                this.pointTo(this.toRepair);
                this.setAnimation("EXTRACT");
                this.repairTimer = Maths.time();
                this.repair = true;
            }
        } else {
            if (Maths.time() - this.repairTimer > REPAIR_TIME) {
                if (this.player.gold.canSpend(REPAIR_GOLD_COST) && this.player.wood.canSpend(REPAIR_WOOD_COST) && this.toRepair.isAlive()) {
                    this.toRepair.life.increase(REPAIR_LIFE_INC);
                    this.player.gold.spend(REPAIR_GOLD_COST);
                    this.player.wood.spend(REPAIR_WOOD_COST);
                    this.repairTimer = Maths.time();
                    if (this.toRepair.life.getCurrent() >= this.toRepair.life.getMax()) {
                        this.stop();
                    }
                }
            }
            if (this.toRepair != null && !this.toRepair.isAlive()) {
                this.stopRepair();
            }
        }
    }

    private void stopRepair() {
        this.repair = false;
        this.toRepair = null;
        this.player.incWorkersOnRepairing(-1);
        this.setAnimation("IDLE");
    }

    @Override
    public void stop() {
        this.stopBuild();
        this.stopExtraction();
        super.stop();
        this.setAvailable(true);
        this.stopRepair();
    }

    @Override
    public boolean isPassive() {
        return (super.isPassive() && !this.isConstructing() && !this.isExtracting() && !this.repair);
    }

    @Override
    public void onMove() {
        if (!this.isExtracting()) {
            super.onMove();
        }
    }

    @Override
    public void setVisibility(boolean state) {
        super.setVisibility(state);
        if (!state) {
            this.getSkill(this.getDataString("FACTION").toUpperCase() + "_" + "CANCEL").action();
        }
    }

    @Override
    public void onOrderedFail(ModelSkill skill) {
    }

    @Override
    public void onHit(AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
        super.onHit(attacker);
        this.player().defendMe(this, attacker);
    }

    public void setAvailable(boolean state) {
        this.available = state;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public void repair(ModelBuilding building) {
        this.toRepair = building;
    }

    /*
     ***************************** Builder handle *****************************
     * Uses of builder ability.
     **************************************************************************
     */
    @Override
    public void buildAt(int dx, int dy, String building, int tw, int th, int time) {
        this.builder.buildAt(dx, dy, building, tw, th, time);
    }

    @Override
    public void updateConstruction(float extrp) {
        this.builder.updateConstruction(extrp);
    }

    @Override
    public boolean isConstructing() {
        return this.builder.isConstructing();
    }

    @Override
    public void stopBuild() {
        this.builder.stopBuild();
    }

    @Override
    public AbstractBuilding<Tile, ModelSkill, Attributes> getBuilding() {
        return this.builder.getBuilding();
    }

    @Override
    public void onConstructed(AbstractBuilding<Tile, ModelSkill, Attributes> building) {
        this.builder.onConstructed(building);
    }

    /*
     ***************************** Extract handle *****************************
     * Uses of extract ability.
     **************************************************************************
     */
    @Override
    public void updateExtraction(float extrp) {
        this.extract.updateExtraction(extrp);
    }

    @Override
    public void setRessourceLocation(int dx, int dy) {
        this.extract.setRessourceLocation(dx, dy);
    }

    @Override
    public void setWarehouse(int tx, int ty) {
        this.extract.setWarehouse(tx, ty);
    }

    @Override
    public boolean hasRessources() {
        return this.extract.hasRessources();
    }

    @Override
    public boolean hasWarehouse() {
        return this.extract.hasWarehouse();
    }

    @Override
    public final void setExtractionTime(long time) {
        this.extract.setExtractionTime(time);
    }

    @Override
    public final void setDropOffTime(long time) {
        this.extract.setDropOffTime(time);
    }

    @Override
    public void startExtraction() {
        this.extract.startExtraction();
    }

    @Override
    public boolean isExtracting() {
        return this.extract.isExtracting();
    }

    @Override
    public void stopExtraction() {
        RessourceType r = this.getRessourceType();
        if (r != null) {
            switch (r) {
                case GOLD:
                    this.player.incWorkersOnGold(-1);
                    break;
                case WOOD:
                    this.player.incWorkersOnWood(-1);
            }
        }
        this.extract.stopExtraction();
        this.clearIgnoredID();
    }

    @Override
    public void setRessourceType(RessourceType type) {
        this.extract.setRessourceType(type);
    }

    @Override
    public RessourceType getRessourceType() {
        return this.extract.getRessourceType();
    }
}
