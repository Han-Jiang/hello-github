package com.b3dgs.warcraft;

import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.GoldMine;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.building.humans.HumansBarracks;
import com.b3dgs.warcraft.building.humans.HumansFarm;
import com.b3dgs.warcraft.building.humans.HumansLumberMill;
import com.b3dgs.warcraft.building.humans.HumansTownHall;
import com.b3dgs.warcraft.building.orcs.OrcsBarracks;
import com.b3dgs.warcraft.building.orcs.OrcsFarm;
import com.b3dgs.warcraft.building.orcs.OrcsLumberMill;
import com.b3dgs.warcraft.building.orcs.OrcsTownHall;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.unit.ModelUnit;
import com.b3dgs.warcraft.unit.UnitType;
import com.b3dgs.warcraft.unit.humans.Archer;
import com.b3dgs.warcraft.unit.humans.Footman;
import com.b3dgs.warcraft.unit.humans.Peasant;
import com.b3dgs.warcraft.unit.orcs.Grunt;
import com.b3dgs.warcraft.unit.orcs.Peon;
import com.b3dgs.warcraft.unit.orcs.Spearman;

/**
 * This class will allow to create instance of each entry with ease.
 * The function createEntry will permit new entry instance from their name and faction.
 * This way, it will be possible to create new intance by generating string name from the race name.
 * (It will be useful to make generic instance, for both Orcs & Humans.)
 */
public class EntryFactory {

    private final Map map;
    private final EntryHandler entrys;
    private final RessourcesHandler rsc;

    public EntryFactory(Map map, EntryHandler entrys, RessourcesHandler rsc) {
        super();
        this.map = map;
        this.entrys = entrys;
        this.rsc = rsc;
    }

    //*********************************** Abstract ********************************//
    public ModelBuilding createBuilding(BuildingType type, Race race) {
        switch (type) {
            case GOLDMINE:
                return this.createGoldMine();
            case TOWNHALL:
                switch (race) {
                    case humans:
                        return this.createHumansTownHall();
                    case orcs:
                        return this.createOrcsTownHall();
                }
                break;
            case FARM:
                switch (race) {
                    case humans:
                        return this.createHumansFarm();
                    case orcs:
                        return this.createOrcsFarm();
                }
                break;
            case BARRACKS:
                switch (race) {
                    case humans:
                        return this.createHumansBarracks();
                    case orcs:
                        return this.createOrcsBarracks();
                }
                break;
            case LUMBERMILL:
                switch (race) {
                    case humans:
                        return this.createHumansLumberMill();
                    case orcs:
                        return this.createOrcsLumberMill();
                }
                break;
        }
        return null;
    }

    public ModelUnit createUnit(UnitType type, Race race) {
        switch (type) {
            case WORKER:
                switch (race) {
                    case humans:
                        return this.createPeasant();
                    case orcs:
                        return this.createPeon();
                }
                break;
            case WARRIOR:
                switch (race) {
                    case humans:
                        return this.createFootman();
                    case orcs:
                        return this.createGrunt();
                }
                break;
            case THROWER:
                switch (race) {
                    case humans:
                        return this.createArcher();
                    case orcs:
                        return this.createSpearman();
                }
                break;
        }
        return null;
    }

    //*********************************** Neutral *********************************//
    public GoldMine createGoldMine() {
        return new GoldMine(this.map, this.rsc, this.entrys);
    }

    //*********************************** Humans **********************************//
    public HumansTownHall createHumansTownHall() {
        return new HumansTownHall(this.map, this.rsc, this.entrys, this);
    }

    public HumansFarm createHumansFarm() {
        return new HumansFarm(this.map, this.rsc, this.entrys);
    }

    public HumansBarracks createHumansBarracks() {
        return new HumansBarracks(this.map, this.rsc, this.entrys, this);
    }

    public HumansLumberMill createHumansLumberMill() {
        return new HumansLumberMill(this.map, this.rsc, this.entrys);
    }

    public Peasant createPeasant() {
        return new Peasant(this.map, this.rsc, this.entrys, this);
    }

    public Footman createFootman() {
        return new Footman(this.map, this.rsc);
    }

    public Archer createArcher() {
        return new Archer(this.map, this.rsc);
    }

    //************************************ Orcs ***********************************//
    public OrcsTownHall createOrcsTownHall() {
        return new OrcsTownHall(this.map, this.rsc, this.entrys, this);
    }

    public OrcsFarm createOrcsFarm() {
        return new OrcsFarm(this.map, this.rsc, this.entrys);
    }

    public OrcsBarracks createOrcsBarracks() {
        return new OrcsBarracks(this.map, this.rsc, this.entrys, this);
    }

    public OrcsLumberMill createOrcsLumberMill() {
        return new OrcsLumberMill(this.map, this.rsc, this.entrys);
    }

    public Peon createPeon() {
        return new Peon(this.map, this.rsc, this.entrys, this);
    }

    public Grunt createGrunt() {
        return new Grunt(this.map, this.rsc);
    }

    public Spearman createSpearman() {
        return new Spearman(this.map, this.rsc);
    }
}
