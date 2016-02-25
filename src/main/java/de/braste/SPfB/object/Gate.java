package de.braste.SPfB.object;

import de.braste.SPfB.SPfB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

import static org.bukkit.block.BlockFace.*;
import static java.lang.String.format;

public class Gate {
    private String id;
    private Gate to;
    private String toId;
    private BlockFace facing;
    private transient Map<BlockFace, List<Block>> frameBlocks;
    private transient  List<Block> portalBlocks;
    private Material portalMaterial;
    private Material frameMaterial;
    private Location startBlockLocation;
    private Location teleportLocation;
    private transient boolean isValid;
    private Map<BlockFace, Integer> faceCount;
    private World world;

    public Gate(String id, Material mat, BlockFace facing, Block startBlock) {
        isValid = false;
        this.id = id;
        this.portalMaterial = mat;
        this.facing = facing;
        frameBlocks = new HashMap<>();
        portalBlocks = new ArrayList<>();
        faceCount = new HashMap<>();
        world = startBlock.getWorld();
        frameMaterial = startBlock.getType();
        startBlockLocation = startBlock.getLocation();
        createPortal(startBlock);
    }

    public Gate(String id, Material mat, BlockFace facing, Block startBlock, Gate to) {
        isValid = false;
        this.id = id;
        this.portalMaterial = mat;
        this.to = to;
        this.toId = to.getId();
        this.facing = facing;
        frameBlocks = new HashMap<>();
        portalBlocks = new ArrayList<>();
        faceCount = new HashMap<>();
        world = startBlock.getWorld();
        frameMaterial = startBlock.getType();
        startBlockLocation = startBlock.getLocation();
        createPortal(startBlock);
    }

    public World getWorld() {
        return world;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public Gate getTo() {
        return to;
    }

    public void setTo(Gate to) {
        this.to = to;
        this.toId = to.getId();
    }

    public String getId() {
        return id;
    }

    public void rename(String newId) {
        id = newId;
    }

    public Location getTeleportLocation() {
        return teleportLocation;
    }

    public List<Block> getPortalBlocks() {
        return portalBlocks;
    }

    public boolean containsFrameBlock(Block block) {
        for (List<Block> l : frameBlocks.values()) {
            if (l.contains(block))
                return true;
        }
        return false;
    }

    public boolean containsPortalBlock(Block block) {
        return portalBlocks.contains(block);
    }

    public boolean containsBlock(Block block) {
        return containsFrameBlock(block) || containsPortalBlock(block);
    }

    public void removeGate() {
        isValid = false;
        synchronized (SPfB.Portals) {
            SPfB.Portals.stream().filter(g -> g.getTo().equals(this)).forEach(g -> g.setTo(null));
            SPfB.Portals.remove(this);
        }
        for (Block b : portalBlocks) {
            b.setType(Material.AIR);
        }
        id = null;
        to = null;
        facing = null;
        frameBlocks = null;
        portalBlocks = null;
        portalMaterial = null;
        teleportLocation = null;
        faceCount = null;
        world = null;
    }

    private void loadGate () {
        isValid = false;
        Block startBlock = world.getBlockAt(startBlockLocation);
        frameMaterial = startBlock.getType();
        createPortal(startBlock);
    }

    private void createPortal(Block startBlock) {
        try {
            BlockFace face = null;
            switch (this.facing) {
                case NORTH:
                    face = WEST;
                    break;
                case SOUTH:
                    face = EAST;
                    break;
                case WEST:
                    face = SOUTH;
                    break;
                case EAST:
                    face = NORTH;
                    break;
            }
            addFrameBlock(startBlock, face, UP);
            for (Block b : frameBlocks.get(face)) {
                addPortalBlock(b.getRelative(face), face);
            }
            setTeleportLocation();
            isValid = true;
        }
        catch (Exception e) {
            SPfB.logger.warning(format("Gate %s kann nicht erzeugt werden: %s", id, e));
        }
    }

    private void addFrameBlock(Block block, BlockFace facing, BlockFace direction) {
        if (containsFrameBlock(block))
            return;
        Block b = block.getRelative(facing);
        BlockFace face = facing;
        BlockFace dir = direction;

        if (!b.getType().equals(Material.AIR)) {
            if (!b.getType().equals(frameMaterial)) {
                isValid = false;
                return;
            }
            facing = dir.getOppositeFace();
            direction = face;
        }
        if (!frameBlocks.containsKey(face))
            frameBlocks.put(face, new ArrayList<>());
        frameBlocks.get(face).add(block);
        int i = 0;
        if (faceCount.containsKey(dir))
            i = faceCount.remove(dir);
        faceCount.put(dir, ++i);
        Block nextBlock = block.getRelative(direction);
        addFrameBlock(nextBlock, facing, direction);
    }

    private void addPortalBlock(Block block, BlockFace facing) {
        if (containsBlock(block))
            return;
        if (block.getType().equals(Material.AIR)) {
            portalBlocks.add(block);
            block.setType(Material.PORTAL, false);
        }
        addPortalBlock(block.getRelative(facing), facing);
    }

    private void setTeleportLocation() {
        List<Block> blocks = frameBlocks.get(facing.getOppositeFace());
        int index = faceCount.get(facing.getOppositeFace()) / 2;
        Block b = blocks.get(index);
        BlockFace face = null;

        switch (facing)
        {
            case NORTH:
                face = WEST;
                break;
            case SOUTH:
                face = EAST;
                break;
            case WEST:
                face = SOUTH;
                break;
            case EAST:
                face = NORTH;
                break;
        }
        teleportLocation = b.getRelative(face).getLocation();
    }
}
