package com.b3dgs.warcraft.building.humans;

import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.building.ModelProductor;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.skill.humans.ProdFootman;
import com.b3dgs.warcraft.skill.humans.ProdArcher;

public class HumansBarracks extends ModelProductor {

    public HumansBarracks(Map map, RessourcesHandler rsch, EntryHandler handler, EntryFactory factory) {
        super(map, rsch, rsch.get("HUMANS_BARRACKS"), rsch.get("CONSTRUCTION").ressource, handler, factory);
        this.addSkill(new ProdFootman(0, this));
        this.addSkill(new ProdArcher(1, this));
    }
}
