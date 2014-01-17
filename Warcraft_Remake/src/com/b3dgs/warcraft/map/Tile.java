package com.b3dgs.warcraft.map;

import com.b3dgs.lionengine.file.FileReader;
import com.b3dgs.lionengine.file.FileWriter;
import com.b3dgs.lionengine.file.XMLNode;
import com.b3dgs.lionengine.game.map.AbstractPathTile;
import com.b3dgs.lionengine.game.map.Border20;
import java.io.IOException;

public class Tile extends AbstractPathTile {

    private int coll;
    private Border20 id;
    private int add;
    private Collision c;

    public Tile(int pattern, int number, int x, int y, String collision) {
        super(pattern, number, x, y, collision);
        this.id = Border20.NONE;
        this.add = 0;
        if (Map.THEME.equals("swamp")) {
            this.add = 19;
        }
    }

    @Override
    public void setCollision(String collision) {
        super.setCollision(collision);
        this.c = Collision.valueOf(collision);
        switch (this.c) {
            case BORDER:
                this.coll = CollisionType.BORDER;
                break;
            case WATER:
                this.coll = CollisionType.WATER;
                break;
            case TREE_BORDER:
                this.coll = CollisionType.TREE;
                if (this.getNumber() >= 125 + this.add && this.getNumber() <= 144 + this.add) {
                    this.id = Border20.values()[this.getNumber() - (125 + this.add)];
                }
                break;
            case TREE:
                this.coll = CollisionType.TREE;
                if (this.getNumber() >= 125 + this.add && this.getNumber() <= 144 + this.add) {
                    this.id = Border20.values()[this.getNumber() - (125 + this.add)];
                }
                break;
            default:
                this.coll = CollisionType.GROUND;
                break;
        }

        if (this.coll != CollisionType.GROUND) {
            this.setBlocking(true);
        }
    }

    public void setNumber(Border20 id) {
        this.id = id;
        super.setNumber(id.ordinal() + 125 + this.add);
    }

    @Override
    public void setNumber(int number) {
        super.setNumber(number);
        if (number >= 125 + this.add && number <= 144 + this.add) {
            this.id = Border20.values()[number - (125 + this.add)];
        } else {
            this.id = Border20.NONE;
        }
    }

    public Border20 getId() {
        return this.id;
    }

    public void setCollType(int type) {
        this.coll = type;
    }

    public int getCollType() {
        return this.coll;
    }

    public Collision getCollisionEnum() {
        return this.c;
    }

    @Override
    public void save(FileWriter file) throws IOException {
        file.writeByte((byte) (this.getPattern() + Byte.MIN_VALUE));
        file.writeByte((byte) (this.getNumber() + Byte.MIN_VALUE));
        file.writeShort((short) (this.getX() / Map.TILE_WIDTH));
        file.writeShort((short) (this.getY() / Map.TILE_HEIGHT));
    }

    @Override
    public void load(FileReader file) throws IOException {
        this.setPattern(file.readByte() - Byte.MIN_VALUE);
        this.setNumber(file.readByte() - Byte.MIN_VALUE);
        this.setX(file.readShort() * Map.TILE_WIDTH);
        this.setY(file.readShort() * Map.TILE_HEIGHT);
    }

    @Override
    public void save(XMLNode node) {
        node.writeInteger("p", this.getPattern());
        node.writeInteger("n", this.getNumber());
        node.writeInteger("x", this.getX() / Map.TILE_WIDTH);
        node.writeInteger("y", this.getY() / Map.TILE_HEIGHT);
    }

    @Override
    public void load(XMLNode node) {
        this.setPattern(node.readInteger("p"));
        this.setNumber(node.readInteger("n"));
        this.setX(node.readInteger("x") * Map.TILE_WIDTH);
        this.setY(node.readInteger("y") * Map.TILE_HEIGHT);
    }
}
