package com.b3dgs.warcraft.ability;

import com.b3dgs.lionengine.game.strategy.AbstractBuilding;
import com.b3dgs.lionengine.game.strategy.AbstractEntryHandler;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.ability.AbstractProducerAbility;
import com.b3dgs.lionengine.game.strategy.ability.ProducerAbility;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.building.ModelProductor;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelUnit;
import com.b3dgs.warcraft.unit.UnitType;

public class ProducerAbilityImpl extends AbstractProducerAbility<Tile, ModelSkill, Attributes>
        implements ProducerAbility<Tile, ModelSkill, Attributes> {

    private final EntryFactory factory;
    private final ModelProductor building;

    public ProducerAbilityImpl(AbstractBuilding<Tile, ModelSkill, Attributes> building,
            AbstractEntryHandler<Tile, ModelSkill, Attributes> handler, EntryFactory factory) {

        super(building, handler);
        this.factory = factory;
        this.building = (ModelProductor) building;
    }

    @Override
    protected AbstractUnit<Tile, ModelSkill, Attributes> getUnitToProduce(String name) {
        // Perform a string deconstruction, in order to get the race and the abstract name.
        int i = name.indexOf('_');
        Race race = Race.valueOf(name.substring(0, i).toLowerCase());
        UnitType type = UnitType.valueOf(name.substring(i + 1, name.length()));
        return this.factory.createUnit(type, race);
    }

    @Override
    public boolean canProduce() {
        if (this.building.player().getFarmUsed() < this.building.player().getFarmGrowth()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canBeProduced() {
        return this.canProduce();
    }

    @Override
    public void onCanNotProduce() {
    }

    @Override
    public void onProducing() {
    }

    @Override
    public void onProduced(AbstractUnit<Tile, ModelSkill, Attributes> unit) {
        ModelUnit u = (ModelUnit) unit;
        Player player = u.player();
        this.checkUpgrade(unit, "ATTACK_SWORD", player.getSwordLvl());
        this.checkUpgrade(unit, "ATTACK_AXE", player.getAxeLvl());
        this.checkUpgrade(unit, "ATTACK_ARROW", player.getArrowLvl());
        this.checkUpgrade(unit, "ATTACK_SPEAR", player.getSpearLvl());
        player.onProduced(u);
    }

    private void checkUpgrade(AbstractUnit<Tile, ModelSkill, Attributes> unit, String skillname, int value) {
        ModelSkill skill = unit.getSkill(skillname);
        if (skill != null) {
            skill.setLvl(value);
        }
    }
}
