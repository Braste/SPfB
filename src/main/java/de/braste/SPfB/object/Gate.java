package de.braste.SPfB.object;

import de.braste.SPfB.SPfB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

import static org.bukkit.block.BlockFace.*;

public class Gate {
    private String id;
    private Gate to;
    private String toId;
    private BlockFace facing;
    private transient Map<BlockFace, List<Block>> frameBlocks;
    private transient  List<Block> portalBlocks;
    private Material material;
    private Location startBlockLocation;
    private Location teleportLocation;
    private transient boolean isValid;
    private Map<BlockFace, Integer> faceCount;
    private World world;

    public Gate(String id, Material mat, BlockFace facing, Block startBlock) {
        isValid = false;
        this.id = id;
        this.material = mat;
        this.facing = facing;
        frameBlocks = new HashMap<>();
        portalBlocks = new ArrayList<>();
        faceCount = new HashMap<>();
        world = startBlock.getWorld();
        startBlockLocation = startBlock.getLocation();
        createPortal(startBlock);
    }

    public Gate(String id, Material mat, BlockFace facing, Block startBlock, Gate to) {
        isValid = false;
        this.id = id;
        this.material = mat;
        this.to = to;
        this.toId = to.getId();
        this.facing = facing;
        frameBlocks = new HashMap<>();
        portalBlocks = new ArrayList<>();
        faceCount = new HashMap<>();
        world = startBlock.getWorld();
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
        return frameBlocks.containsValue(block);
    }

    public boolean containsPortalBlock(Block block) {
        return portalBlocks.contains(block);
    }

    public boolean containsBlock(Block block) {
        return frameBlocks.containsValue(block) || portalBlocks.contains(block);
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
        material = null;
        teleportLocation = null;
        faceCount = null;
        world = null;
    }

    private void loadGate () {
        isValid = false;
        Block startBlock = world.getBlockAt(startBlockLocation);
        createPortal(startBlock);
    }

    private void createPortal(Block startBlock) {
        BlockFace facing = null;
        switch (this.facing)
        {
            case NORTH:
                facing = WEST;
                break;
            case SOUTH:
                facing = EAST;
                break;
            case WEST:
                facing = SOUTH;
                break;
            case EAST:
                facing = NORTH;
                break;
        }
        addFrameBlock(startBlock, facing, UP);
        for (Block b : frameBlocks.get(UP)) {
            addPortalBlock(b.getRelative(facing));
        }
        setTeleportLocation();
        isValid = true;
    }

    private void addFrameBlock(Block block, BlockFace facing, BlockFace direction) {
        if (frameBlocks.containsValue(block))
            return;
        Block b = block.getRelative(facing);
        BlockFace face = facing;
        BlockFace dir = direction;

        if (!b.getType().equals(Material.AIR)) {
            if (!b.getType().equals(material)) {
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

    private void addPortalBlock(Block block) {
        if (frameBlocks.containsValue(block))
            return;
        if (block.getType().equals(Material.AIR)) {
            portalBlocks.add(block);
            block.setType(Material.PORTAL, false);
        }
        addPortalBlock(block.getRelative(facing));
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
