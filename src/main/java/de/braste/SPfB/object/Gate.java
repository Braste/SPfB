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

    public Gate(String id, Material portalMaterial, BlockFace facing, Block startBlock) {
        this.isValid = false;
        this.id = id;
        this.portalMaterial = portalMaterial;
        this.facing = facing;
        this.frameBlocks = new HashMap<>();
        this.portalBlocks = new ArrayList<>();
        this.faceCount = new HashMap<>();
        this.world = startBlock.getWorld();
        this.frameMaterial = startBlock.getType();
        this.startBlockLocation = startBlock.getLocation();
        createPortal(startBlock);
    }

    public Gate(String id, Material portalMaterial, BlockFace facing, Block startBlock, Gate to) {
        this.isValid = false;
        this.id = id;
        this.portalMaterial = portalMaterial;
        this.to = to;
        this.toId = to.getId();
        this.facing = facing;
        this.frameBlocks = new HashMap<>();
        this.portalBlocks = new ArrayList<>();
        this.faceCount = new HashMap<>();
        this.world = startBlock.getWorld();
        this.frameMaterial = startBlock.getType();
        this.startBlockLocation = startBlock.getLocation();
        createPortal(startBlock);
    }

    /*public Gate(String id, Material portalMaterial, BlockFace facing, Location startBlockLocation) {
        this.isValid = false;
        this.id = id;
        this.portalMaterial = portalMaterial;
        this.facing = facing;
        this.frameBlocks = new HashMap<>();
        this.portalBlocks = new ArrayList<>();
        this.faceCount = new HashMap<>();
        this.startBlockLocation = startBlockLocation;
        this.world = startBlockLocation.getWorld();
        this.frameMaterial = startBlockLocation.getBlock().getType();
        createPortal(startBlockLocation.getBlock());
    }*/

    public World getWorld() {
        return this.world;
    }

    public boolean getIsValid() {
        return this.isValid;
    }

    public Gate getTo() {
        return this.to;
    }

    public String getToId() { return toId; }

    public void setTo(Gate to) {
        this.to = to;
        this.toId = to.getId();
    }

    public String getId() {
        return this.id;
    }

    public BlockFace getFacing() {
        return this.facing;
    }

    public Material getPortalMaterial() {
        return this.portalMaterial;
    }

    public Material getFrameMaterial() {
        return this.frameMaterial;
    }

    public Location getStartBlockLocation() {
        return this.startBlockLocation;
    }

    public void rename(String newId) {
        this.id = newId;
    }

    public Location getTeleportLocation() {
        return this.teleportLocation;
    }

    public List<Block> getPortalBlocks() {
        return this.portalBlocks;
    }

    public boolean containsFrameBlock(Block block) {
        for (List<Block> l : this.frameBlocks.values()) {
            if (l.contains(block))
                return true;
        }
        return false;
    }

    public boolean containsPortalBlock(Block block) {
        return this.portalBlocks.contains(block);
    }

    public boolean containsBlock(Block block) {
        return containsFrameBlock(block) || containsPortalBlock(block);
    }

    public void removeGate() {
        this.isValid = false;
        for (Block b : this.portalBlocks) {
            b.setType(Material.AIR);
        }
        this.id = null;
        this.to = null;
        this.facing = null;
        this.frameBlocks = null;
        this.portalBlocks = null;
        this.portalMaterial = null;
        this.teleportLocation = null;
        this.faceCount = null;
        this.world = null;
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
            for (Block b : this.frameBlocks.get(face)) {
                addPortalBlock(b.getRelative(face), face);
            }
            setTeleportLocation();
            this.isValid = true;
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
            if (!b.getType().equals(this.frameMaterial)) {
                this.isValid = false;
                return;
            }
            facing = dir.getOppositeFace();
            direction = face;
        }
        if (!this.frameBlocks.containsKey(face))
            this.frameBlocks.put(face, new ArrayList<>());
        this.frameBlocks.get(face).add(block);
        int i = 0;
        if (this.faceCount.containsKey(dir))
            i = this.faceCount.remove(dir);
        this.faceCount.put(dir, ++i);
        Block nextBlock = block.getRelative(direction);
        addFrameBlock(nextBlock, facing, direction);
    }

    private void addPortalBlock(Block block, BlockFace facing) {
        if (containsBlock(block))
            return;
        if (block.getType().equals(Material.AIR)) {
            this.portalBlocks.add(block);
            block.setType(this.portalMaterial, false);
        }
        addPortalBlock(block.getRelative(facing), facing);
    }

    private void setTeleportLocation() {
        List<Block> blocks = this.frameBlocks.get(UP);
        int index = this.faceCount.get(UP) / 2;
        Block b = blocks.get(index);

        this.teleportLocation = b.getRelative(this.facing.getOppositeFace()).getRelative(this.facing.getOppositeFace()).getLocation().add(0, 1, 0);
    }
}
