package com.b3dgs.warcraft.gameplay;

import com.b3dgs.lionengine.File;
import com.b3dgs.lionengine.file.XMLNode;
import com.b3dgs.lionengine.file.XMLParser;
import java.util.List;
import java.util.TreeMap;

/**
 * Costs data handker, by loading a file describing all costs for each entry.
 */
public class Costs {

    private static final TreeMap<String, Cost> costs = new TreeMap<String, Cost>();

    private Costs() {
    }

    public static void initializeCosts(String file) {
        XMLParser parser = File.FILE.createXMLParser();
        XMLNode root = parser.load(file);
        List<XMLNode> children = root.getChildren();
        for (XMLNode node : children) {
            String name = node.readString("name");
            int gold = node.readInteger("gold");
            int wood = node.readInteger("wood");
            int time = node.readInteger("time");
            int tw = node.readInteger("tw");
            int th = node.readInteger("th");
            costs.put(name, new Cost(gold, wood, time, tw, th));
        }
        children.clear();
    }

    public static Cost get(String name) {
        return costs.get(name);
    }
}
