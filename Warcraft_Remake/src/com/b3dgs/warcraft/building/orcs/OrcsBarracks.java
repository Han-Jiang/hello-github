package com.b3dgs.warcraft.building.orcs;

import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.building.ModelProductor;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.skill.orcs.ProdGrunt;
import com.b3dgs.warcraft.skill.orcs.ProdSpearman;

public class OrcsBarracks extends ModelProductor {

    public OrcsBarracks(Map map, RessourcesHandler rsch, EntryHandler handler, EntryFactory factory) {
        super(map, rsch, rsch.get("ORCS_BARRACKS"), rsch.get("CONSTRUCTION").ressource, handler, factory);
        this.addSkill(new ProdGrunt(0, this));
        this.addSkill(new ProdSpearman(1, this));
    }
}
