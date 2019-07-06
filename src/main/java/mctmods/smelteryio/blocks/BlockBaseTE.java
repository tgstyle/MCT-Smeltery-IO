package mctmods.smelteryio.blocks;

import mctmods.smelteryio.blocks.base.BlockBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;


public abstract class BlockBaseTE extends BlockBase implements ITileEntityProvider {

    public BlockBaseTE(Material material, MapColor mapColor, String registry) {
        super(material, mapColor, registry);
    }

}
