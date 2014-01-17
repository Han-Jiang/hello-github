package com.b3dgs.warcraft.ability;

import com.b3dgs.lionengine.game.map.Border20;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.ability.AbstractExtractAbility;
import com.b3dgs.lionengine.game.strategy.ability.ExtractAbility;
import com.b3dgs.lionengine.geometry.Point2D;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.warcraft.gameplay.RessourceType;
import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.GoldMine;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.CollisionType;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelWorker;
import java.util.List;

public class ExtractAbilityImpl extends AbstractExtractAbility<Tile, ModelSkill, Attributes, RessourceType>
        implements ExtractAbility<Tile, ModelSkill, Attributes, RessourceType> {

    public static final int EXTRACT_GOLD_AMMOUNT = 100;
    public static final int EXTRACT_WOOD_AMMOUNT = 100;
    public static final long EXTRACT_GOLD_TIME = 1500;
    public static final long EXTRACT_WOOD_TIME = 20000;
    public static final long DROPOFF_TIME = 1500;
    private final ModelWorker worker;
    private boolean extract, carry;
    private Tile tile;

    public ExtractAbilityImpl(AbstractUnit<Tile, ModelSkill, Attributes> worker) {
        super(worker);
        this.worker = (ModelWorker) worker;
    }

    @Override
    public boolean canExtract() {
        this.tile = null;
        this.extract = false;
        this.carry = false;
        if (this.rscX == -1 || this.rscY == -1) {
            return false;
        }
        // If ressource is wood
        if (this.worker.map.getTile(this.rscY, this.rscX).getCollType() == CollisionType.TREE) {
            if (Maths.getDistance(this.worker.getXInTile(), this.worker.getYInTile(), this.rscX, this.rscY) <= 1) {
                this.worker.stopMoves();
                if (this.worker.hasReachedDestination()) {
                    this.setRessourceType(RessourceType.WOOD);
                    this.setExtractionTime(EXTRACT_WOOD_TIME);
                    this.tile = this.worker.map.getTile(this.rscY, this.rscX);
                    return true;
                }
            }
        } else if (this.worker.map.getTile(this.rscY, this.rscX).getNumber() == this.worker.map.getTCut()) {
            this.searchNextTree(1);
            if (this.worker.map.getTile(this.rscY, this.rscX).getCollType() != CollisionType.TREE) {
                this.worker.stop();
            }
        } // If ressource is gold
        else {
            int id = this.worker.map.getRef(this.rscY, this.rscX);
            if (id > 0) {
                ModelBuilding e = ModelBuilding.get(id);
                if (e instanceof GoldMine) {
                    if (this.worker.getDistance((GoldMine) e) <= 1) {
                        this.worker.stopMoves();
                        if (this.worker.hasReachedDestination()) {
                            this.setRessourceType(RessourceType.GOLD);
                            this.setExtractionTime(EXTRACT_GOLD_TIME);
                            this.worker.setVisibility(false);
                            this.worker.setActive(false);
                            this.worker.setAvailable(false);
                            this.worker.unselect();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canCarry() {
        int id = this.worker.map.getRef(this.whsY, this.whsX);
        if (id > 0) {
            ModelBuilding e = ModelBuilding.get(id);
            if (e != null) {
                if (this.worker.getDistance(e) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /** This function will search another tree, arround the last cut.
     * @param step starting search radius.
     */
    private void searchNextTree(int step) {
        for (int h = this.rscX - step; h <= this.rscX + step; h++) {
            try {
                if (this.worker.map.getTile(this.rscY, h).getCollType() == CollisionType.TREE) {
                    this.setRessourceLocation(h, this.rscY);
                    return;
                }
            } catch (java.lang.NullPointerException e) {
                continue;
            }
        }
        for (int v = this.rscY - step; v <= this.rscY + step; v++) {
            try {
                if (this.worker.map.getTile(v, this.rscX).getCollType() == CollisionType.TREE) {
                    this.setRessourceLocation(this.rscX, v);
                    return;
                }
            } catch (java.lang.NullPointerException e) {
                continue;
            }
        }
        // Avoid infinite search
        if (step < 64) {
            this.searchNextTree(step + 1);
        }
    }

    @Override
    public void onGotoRessource() {
    }

    @Override
    public void onExtract() {
        if (!this.extract) {
            if (this.getRessourceType() == RessourceType.WOOD) {
                this.worker.setAnimation("EXTRACT");
            }
            int id = this.worker.map.getRef(this.rscY, this.rscX);
            if (id > 0) {
                ModelBuilding e = ModelBuilding.get(id);
                if (e instanceof GoldMine) {
                    GoldMine gm = (GoldMine) e;
                    if (gm.isEmpty()) {
                        this.worker.stopExtraction();
                        this.getOutFromTo(this.rscX, this.rscY, this.whsX, this.whsY);
                    } else if (gm.gold.canSpend(EXTRACT_GOLD_AMMOUNT)) {
                        gm.gold.spend(EXTRACT_GOLD_AMMOUNT);
                    }
                }
            }
            this.extract = true;
        }
        if (this.getRessourceType() == RessourceType.GOLD) {
            if (this.worker.map.getRef(this.worker.getYInTile(), this.worker.getXInTile()) == this.worker.id) {
                this.worker.map.setRef(this.worker.getYInTile(), this.worker.getXInTile(), 0);
            }
        }
        // Ensute that processing tree is not cut by another worker
        if (this.getRessourceType() == RessourceType.WOOD) {
            if (this.worker.map.getTile(this.rscY, this.rscX).getCollType() != CollisionType.TREE) {
                this.searchNextTree(1);
                this.startExtraction();
            }
        }
    }

    @Override
    public void onExtracted() {
        if (!this.hasWarehouse()) {
            ModelBuilding b = worker.player().getClosestBuilding(this.worker, BuildingType.TOWNHALL);
            this.worker.setWarehouse(b.getXInTile() + b.getXInTile() / 2, b.getYInTile() + b.getYInTile() / 2);
        }
        this.extract = false;
        this.worker.setAvailable(true);
        if (this.tile != null) {
            Map map = this.worker.map;
            int tw = map.getTileWidth();
            int th = map.getTileHeight();
            this.cutTree(map, tw, th, this.tile);
            this.tile = null;
        } else {
            this.getOutFromTo(this.rscX, this.rscY, this.whsX, this.whsY);
            this.worker.setVisibility(true);
            this.worker.setActive(true);
        }
        if (this.getRessourceType() == RessourceType.GOLD) {
            this.worker.ignoreID(this.worker.map.getRef(this.rscY, this.rscX), false);
        }
        this.worker.ignoreID(this.worker.map.getRef(this.whsY, this.whsX), true);
    }

    @Override
    public void onCarry() {
        if (!this.carry) {
            switch (this.worker.getRessourceType()) {
                case GOLD:
                    this.worker.setAnimation("CARRY_GOLD");
                    break;
                case WOOD:
                    this.worker.setAnimation("CARRY_WOOD");
                    break;
            }
            this.carry = true;
            this.worker.setDropOffTime(DROPOFF_TIME);
        }
    }

    @Override
    public void onCarried() {
        this.carry = false;
        switch (this.worker.getRessourceType()) {
            case GOLD:
                this.worker.player().gold.add(EXTRACT_GOLD_AMMOUNT);
                break;
            case WOOD:
                this.worker.player().wood.add(EXTRACT_WOOD_AMMOUNT);
                break;
        }
    }

    @Override
    public void onDropOff() {
        this.worker.unselect();
        this.worker.setAvailable(false);
        this.worker.setActive(false);
        this.worker.setVisibility(false);
        if (this.worker.map.getRef(this.worker.getYInTile(), this.worker.getXInTile()) == this.worker.id) {
            this.worker.map.setRef(this.worker.getYInTile(), this.worker.getXInTile(), 0);
        }
    }

    @Override
    public void onDroppedOff() {
        this.getOutFromTo(this.whsX, this.whsY, this.rscX, this.rscY);
        this.worker.setVisibility(true);
        this.worker.setActive(true);
        this.worker.ignoreID(this.worker.map.getRef(this.whsY, this.whsX), false);
        if (this.getRessourceType() == RessourceType.GOLD) {
            this.worker.ignoreID(this.worker.map.getRef(this.rscY, this.rscX), true);
        }
        if (this.getRessourceType() == RessourceType.WOOD) {
            this.searchNextTree(1);
            if (this.worker.map.getTile(this.rscY, this.rscX).getCollType() != CollisionType.TREE) {
                this.stopExtraction();
            }
        }
        this.worker.setAvailable(true);
    }

    /** Handle building entrance and exit by searching the best exit, closest to the destination.
     * @param ftx from tile x.
     * @param fty from tile y.
     * @param ttx to tile x.
     * @param tty to tile y.
     */
    private void getOutFromTo(int ftx, int fty, int ttx, int tty) {
        List<Point2D> places = this.worker.map.getAllFreePlaceArround(ftx, fty, 1, 1, 0, 3);
        Point2D best = null;
        int min = Integer.MAX_VALUE;
        for (Point2D p : places) {
            int dist = Maths.getDistance(ttx, tty, p.getX(), p.getY());
            if (min > dist) {
                min = dist;
                best = p;
            }
        }
        places.clear();
        if (best != null) {
            this.worker.place(best.getX(), best.getY());
        }
    }

    /** Cut specific tree, and update all trees arround by adjusting their tile number.
     * @param map map reference.
     * @param tw tile x.
     * @param th tile y.
     * @param tile tile cut (tree).
     */
    private void cutTree(Map map, int tw, int th, Tile tile) {
        this.cut(tile);
        Border20 n;

        Tile top = this.getTop(map, tw, th, tile);
        if (top != null) {
            n = top.getId();
            if (n == Border20.DOWN || n == Border20.DOWN_MIDDLE || n == Border20.CORNER_DOWN_LEFT || n == Border20.CORNER_DOWN_RIGHT) {
                this.cut(top);
            }
        }

        Tile down = this.getDown(map, tw, th, tile);
        if (down != null) {
            n = down.getId();
            if (n == Border20.TOP || n == Border20.TOP_MIDDLE || n == Border20.CORNER_TOP_LEFT || n == Border20.CORNER_TOP_RIGHT) {
                this.cut(down);
            }
        }
        try {
            Tile left = this.getLeft(map, tw, th, tile);
            if (this.getLeft(map, tw, th, left).getId() == Border20.NONE && this.getRight(map, tw, th, left).getId() == Border20.NONE) {
                if (left.getId() != Border20.NONE) {
                    left.setNumber(Border20.TOP_MIDDLE);
                    this.worker.map.setAxis(left, Border20.TOP_MIDDLE);
                    Tile t = this.getTop(map, tw, th, left);
                    if (t.getId() == Border20.RIGHT) {
                        t.setNumber(Border20.RIGHT_MIDDLE);
                        this.worker.map.setAxis(t, Border20.RIGHT_MIDDLE);
                    }
                }
            }
        } catch (NullPointerException ex) {
        }
        try {
            Tile right = this.getRight(map, tw, th, tile);
            if (this.getLeft(map, tw, th, right).getId() == Border20.NONE && this.getRight(map, tw, th, right).getId() == Border20.NONE) {
                if (right.getId() != Border20.NONE) {
                    right.setNumber(Border20.TOP_MIDDLE);
                    this.worker.map.setAxis(right, Border20.TOP_MIDDLE);
                    Tile t = this.getTop(map, tw, th, right);
                    if (t.getId() == Border20.LEFT) {
                        t.setNumber(Border20.LEFT_MIDDLE);
                        this.worker.map.setAxis(t, Border20.LEFT_MIDDLE);
                    }
                }
            }
        } catch (NullPointerException ex) {
        }

        this.worker.map.updateTree(tile, false);
        if (top != null) {
            this.worker.map.updateTree(top, true);
        }
        if (down != null) {
            this.worker.map.updateTree(down, true);
        }
    }

    private void cut(Tile tile) {
        tile.setNumber(this.worker.map.getTCut());
        tile.setBlocking(false);
        tile.setCollType(CollisionType.GROUND);
    }

    private Tile getTop(Map map, int tw, int th, Tile tile) {
        return map.getTile(-tile.getY() / th - 1, tile.getX() / tw);
    }

    private Tile getLeft(Map map, int tw, int th, Tile tile) {
        return map.getTile(-tile.getY() / th, tile.getX() / tw - 1);
    }

    private Tile getRight(Map map, int tw, int th, Tile tile) {
        return map.getTile(-tile.getY() / th, tile.getX() / tw + 1);
    }

    private Tile getDown(Map map, int tw, int th, Tile tile) {
        return map.getTile(-tile.getY() / th + 1, tile.getX() / tw);
    }
}
