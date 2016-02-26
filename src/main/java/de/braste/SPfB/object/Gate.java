package de.braste.SPfB.object;

import de.braste.SPfB.SPfB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.bukkit.block.BlockFace.*;

public class Gate {
    private String id;
    private String toId;
    private Gate to;
    private BlockFace facing;
    private Material portalMaterial;
    private Material frameMaterial;
    private Location startBlockLocation;
    private Location teleportLocation;
    private boolean isValid;
    private boolean loadFromConfig;
    private Map<BlockFace, Integer> faceCount;
    private Map<BlockFace, List<Block>> frameBlocks;
    private List<Block> portalBlocks;
    private World world;

    public Gate(String id, Material portalMaterial, BlockFace facing, Block startBlock, boolean loadFromConfig) {
        this.isValid = false;
        this.loadFromConfig = loadFromConfig;
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
        this.loadFromConfig = false;
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
        if (to != null)
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
        this.toId = null;
        this.facing = null;
        this.portalMaterial = null;
        this.frameMaterial = null;
        this.startBlockLocation = null;
        this.teleportLocation = null;
        this.faceCount = null;
        this.frameBlocks = null;
        this.portalBlocks = null;
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
            if (this.frameBlocks.size() == 0)
                return;
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
        Material checkMaterial = Material.AIR;
        if (loadFromConfig)
             checkMaterial = this.portalMaterial;
        Block b = block.getRelative(facing);
        BlockFace face = facing;
        BlockFace dir = direction;

        if (!b.getType().equals(checkMaterial)) {
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
        Material checkMaterial = Material.AIR;
        if (loadFromConfig)
            checkMaterial = this.portalMaterial;
        if (block.getType().equals(checkMaterial)) {
            this.portalBlocks.add(block);
            block.setType(this.portalMaterial, false);
            /*if (this.facing.equals(BlockFace.EAST) || this.facing.equals(BlockFace.WEST))
                block.setData((byte)2);*/
        }
        addPortalBlock(block.getRelative(facing), facing);
    }

    private void setTeleportLocation() {
        List<Block> blocks = this.frameBlocks.get(UP);
        int index = (this.faceCount.get(UP) - 1) / 2;
        if (index >= blocks.size())
            index = blocks.size() / 2 - 1;
        Block b = blocks.get(index);
        Location target = b.getRelative(this.facing.getOppositeFace()).getRelative(this.facing.getOppositeFace()).getLocation();
        target.add(0, 1, 0);
        switch (this.facing) {
            case NORTH:
                target.setYaw((float)0);
                break;
            case SOUTH:
                target.setYaw((float)-180);
                break;
            case WEST:
                target.setYaw((float)-90);
                break;
            case EAST:
                target.setYaw((float)90);
                break;
        }
        this.teleportLocation = target;
    }
}
