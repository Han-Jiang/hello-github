package com.b3dgs.warcraft.gameplay;

import com.b3dgs.lionengine.Drawable;
import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.Text;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractPlayer;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelAttacker;
import com.b3dgs.warcraft.unit.ModelUnit;
import com.b3dgs.warcraft.unit.ModelWorker;
import com.b3dgs.warcraft.unit.UnitType;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * All informations related to the player.<br/>
 * It allows fast access to a lot a data, such as entry list, and their activities.<br/>
 * This is a good start to establish an AI by inheritance.
 */
public class Player extends AbstractPlayer {

    public final Map map;
    public final Race race;
    public final Ressource gold, wood;
    protected final List<ModelBuilding> buildings;
    protected final List<ModelUnit> units;
    protected final List<ModelWorker> workers;
    protected final List<ModelAttacker> attackers;
    protected final EntryHandler entrys;
    protected final EntryFactory factory;
    private final Text text;
    protected int startX, startY;
    protected int attackX, attackY;
    private int numTownHall = 0, numFarm = 0, numBarrack = 0, numLumberMill = 0;
    private int numWorker = 0, numWarrior = 0, numThrower = 0;
    private int workersOnGold = 0, workersOnWood = 0, workersOnConstructing = 0, workersOnRepairing = 0;
    private int workersOnTownHall = 0, workersOnFarm = 0, workersOnBarracks = 0, workersOnLumberMill = 0;
    private int swordLvl, axeLvl, arrowLvl, spearLvl, shieldLvl;

    public Player(String name, Race race, Map map, EntryFactory factory, EntryHandler entrys) {
        super(name);
        this.race = race;
        this.map = map;
        this.entrys = entrys;
        this.factory = factory;
        this.gold = new Ressource(1000);
        this.wood = new Ressource(1000);
        this.buildings = new ArrayList<ModelBuilding>(1);
        this.units = new ArrayList<ModelUnit>(1);
        this.workers = new ArrayList<ModelWorker>(1);
        this.attackers = new ArrayList<ModelAttacker>(1);
        this.text = Drawable.DRAWABLE.createText(Font.SANS_SERIF, 8, Text.NORMAL);
        this.arrowLvl = 1;
        this.shieldLvl = 1;
        this.swordLvl = 1;
        this.axeLvl = 1;
        this.spearLvl = 1;
    }

    public void setStartingPoint(int tx, int ty) {
        this.startX = tx;
        this.startY = ty;
    }

    public int getStartX() {
        return this.startX;
    }

    public int getStartY() {
        return this.startY;
    }

    public void init() {
        this.addUnit(UnitType.WORKER, this.startX, this.startY);
    }

    @Override
    public void update(float extrp) {
        this.gold.update(extrp, 4.0f);
        this.wood.update(extrp, 4.0f);
    }

    @Override
    public void render(Graphics2D g) {
        this.text.draw(g, "" + (int) this.wood.getValue(), 170, 1, Alignment.RIGHT);
        this.text.draw(g, "" + (int) this.gold.getValue(), 270, 1, Alignment.RIGHT);
    }

    public void onProduced(ModelUnit unit) {
        this.units.add(unit);
        switch (unit.type) {
            case WORKER:
                this.numWorker++;
                if (this.numTownHall > 0) {
                    this.unlockCurrent(unit, BuildingType.FARM);
                    this.unlockCurrent(unit, BuildingType.BARRACKS);
                }
                if (this.numBarrack > 0) {
                    this.unlockCurrent(unit, BuildingType.LUMBERMILL);
                }
                this.workers.add((ModelWorker) unit);
                break;
            case WARRIOR:
                this.numWarrior++;
                this.attackers.add((ModelAttacker) unit);
                break;
            case THROWER:
                this.numThrower++;
                this.attackers.add((ModelAttacker) unit);
                break;
        }
    }

    public void onBuilt(ModelBuilding building) {
        this.buildings.add(building);
        switch (building.type) {
            case TOWNHALL:
                this.numTownHall++;
                this.unlockAll(UnitType.WORKER, BuildingType.FARM);
                this.unlockAll(UnitType.WORKER, BuildingType.BARRACKS);
                break;
            case FARM:
                this.numFarm++;
                break;
            case BARRACKS:
                this.numBarrack++;
                this.unlockAll(UnitType.WORKER, BuildingType.LUMBERMILL);
                if (this.numLumberMill > 0) {
                    this.unlockCurrent(building, UnitType.THROWER);
                }
                break;
            case LUMBERMILL:
                this.unlockAll(BuildingType.BARRACKS, UnitType.THROWER);
                if (building.faction == Race.humans) {
                    building.getSkill("UPGRADE_ARROW").setLvl(this.arrowLvl + 1);
                } else if (building.faction == Race.orcs) {
                    building.getSkill("UPGRADE_SPEAR").setLvl(this.spearLvl + 1);
                }
                this.numLumberMill++;
                break;
        }
    }

    public void setAttack(int tx, int ty) {
        this.attackX = tx;
        this.attackY = ty;
    }

    private void unlockAll(UnitType unit, BuildingType building) {
        if (unit == UnitType.WORKER) {
            for (ModelWorker w : this.workers) {
                w.getSkill(this.getTypeName(building)).setUnlocked(true);
            }
        }
    }

    private void unlockAll(BuildingType building, UnitType unit) {
        for (ModelBuilding b : this.buildings) {
            if (b.type == building) {
                b.getSkill(this.getTypeName(unit)).setUnlocked(true);
            }
        }
    }

    private void unlockCurrent(ModelBuilding building, UnitType type) {
        building.getSkill(this.getTypeName(type)).setUnlocked(true);
    }

    private void unlockCurrent(ModelUnit unit, BuildingType type) {
        unit.getSkill(this.getTypeName(type)).setUnlocked(true);
    }

    protected String getTypeName(Enum<?> type) {
        return this.race.name().toUpperCase() + "_" + type;
    }

    public int getSwordLvl() {
        return this.swordLvl;
    }

    public int getAxeLvl() {
        return this.axeLvl;
    }

    public int getShieldLvl() {
        return this.shieldLvl;
    }

    public void incArrowLvl() {
        this.arrowLvl++;
        for (ModelBuilding b : this.buildings) {
            if (b.type == BuildingType.LUMBERMILL) {
                b.getSkill("UPGRADE_ARROW").setLvl(this.arrowLvl + 1);
            }
        }
        for (ModelAttacker a : this.attackers) {
            if (a.type == UnitType.THROWER) {
                a.getSkill("ATTACK_ARROW").setLvl(this.arrowLvl);
            }
        }
    }

    public int getArrowLvl() {
        return this.arrowLvl;
    }

    public int getSpearLvl() {
        return this.spearLvl;
    }

    public void incSpearLvl() {
        this.spearLvl++;
        for (ModelBuilding b : this.buildings) {
            if (b.type == BuildingType.LUMBERMILL) {
                b.getSkill("UPGRADE_SPEAR").setLvl(this.spearLvl + 1);
            }
        }
        for (ModelAttacker a : this.attackers) {
            if (a.type == UnitType.THROWER) {
                a.getSkill("ATTACK_SPEAR").setLvl(this.spearLvl);
            }
        }
    }

    public void addUnit(UnitType type, int x, int y) {
        this.addEntry(this.factory.createUnit(type, this.race), x, y);
    }

    public void addBuilding(BuildingType type, int x, int y) {
        this.addEntry(this.factory.createBuilding(type, this.race), x, y);
    }

    private void addEntry(AbstractEntry<Tile, ModelSkill, Attributes> entry, int x, int y) {
        entry.setOwnerID(this.id);
        this.entrys.add(entry);
        entry.place(x, y);
        if (entry instanceof ModelUnit) {
            this.onProduced((ModelUnit) entry);
        } else if (entry instanceof ModelBuilding) {
            this.onBuilt((ModelBuilding) entry);
        }
    }

    public void removeUnit(ModelUnit unit) {
        this.units.remove(unit);
        switch (unit.type) {
            case WORKER:
                this.numWorker--;
                this.workers.remove((ModelWorker) unit);
                break;
            case WARRIOR:
                this.numWarrior--;
                this.attackers.remove((ModelAttacker) unit);
                break;
            case THROWER:
                this.numThrower--;
                this.attackers.remove((ModelAttacker) unit);
                break;
        }
    }

    public void removeBuilding(ModelBuilding building) {
        this.buildings.remove(building);
        switch (building.type) {
            case TOWNHALL:
                this.numTownHall--;
                break;
            case FARM:
                this.numFarm--;
                break;
            case BARRACKS:
                this.numBarrack--;
                break;
            case LUMBERMILL:
                this.numLumberMill--;
                break;
        }
    }

    public ModelBuilding getClosestBuilding(ModelUnit unit, BuildingType type) {
        int min = Integer.MAX_VALUE;
        ModelBuilding closest = null;
        for (ModelBuilding b : this.buildings) {
            if (b.type != type) {
                continue;
            }
            int dist = unit.getDistance(b);
            if (dist < min) {
                min = dist;
                closest = b;
            }
        }
        return closest;
    }

    public void defendMe(AbstractEntry<Tile, ModelSkill, Attributes> building, AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
    }

    public int getTownhalls() {
        return this.numTownHall;
    }

    public int getFarms() {
        return this.numFarm;
    }

    public int getBarracks() {
        return this.numBarrack;
    }

    public int getLumbermills() {
        return this.numLumberMill;
    }

    public int getWorkers() {
        return this.numWorker;
    }

    public int getWarriors() {
        return this.numWarrior;
    }

    public int getThrowers() {
        return this.numThrower;
    }

    public int getFarmGrowth() {
        return this.numFarm * 4;
    }

    public int getFarmUsed() {
        return this.numWorker + this.numWarrior + this.numThrower;
    }

    public void incWorkersOnGold(int step) {
        this.workersOnGold += step;
    }

    public void incWorkersOnWood(int step) {
        this.workersOnWood += step;
    }

    public void incWorkersOnConstructing(int step) {
        this.workersOnConstructing += step;
    }

    public void incWorkersOnRepairing(int step) {
        this.workersOnRepairing += step;
    }

    public void incWorkersOnTownHall(int step) {
        this.workersOnTownHall += step;
    }

    public void incWorkersOnFarm(int step) {
        this.workersOnFarm += step;
    }

    public void incWorkersOnBarracks(int step) {
        this.workersOnBarracks += step;
    }

    public void incWorkersOnLumberMill(int step) {
        this.workersOnLumberMill += step;
    }

    public int getWorkersOnGold() {
        return this.workersOnGold;
    }

    public int getWorkersOnWood() {
        return this.workersOnWood;
    }

    public int getWorkersOnConstructing() {
        return this.workersOnConstructing;
    }

    public int getWorkersOnRepairing() {
        return this.workersOnRepairing;
    }

    public int getWorkersOnTownHall() {
        return this.workersOnTownHall;
    }

    public int getWorkersOnFarm() {
        return this.workersOnFarm;
    }

    public int getWorkersOnBarracks() {
        return this.workersOnBarracks;
    }

    public int getWorkersOnLumberMill() {
        return this.workersOnLumberMill;
    }
}
