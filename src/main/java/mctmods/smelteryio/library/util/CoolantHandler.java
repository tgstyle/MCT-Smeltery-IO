package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class CoolantHandler {

    public static class Entry {
        public final Item item;
        public final int meta; // -1 = wildcard
        public final int consumeTable;
        public final int consumeBasin;
        public final int castsTable;
        public final int castsBasin;
        public final char shape; // 'C' or 'S'

        public Entry(Item item, int meta, int consumeTable, int consumeBasin, int castsTable, int castsBasin, char shape) {
            this.item = item;
            this.meta = meta;
            this.consumeTable = Math.max(1, consumeTable);
            this.consumeBasin = Math.max(1, consumeBasin);
            this.castsTable = Math.max(1, castsTable);
            this.castsBasin = Math.max(1, castsBasin);
            this.shape = shape;
        }

        public boolean matches(ItemStack stack) {
            if (stack.isEmpty() || stack.getItem() != item) return false;
            return meta == -1 || stack.getMetadata() == meta;
        }

        public int getConsume(boolean tableMode) { return tableMode ? consumeTable : consumeBasin; }
        public int getCasts(boolean tableMode) { return tableMode ? castsTable : castsBasin; }
        public char getShape() { return shape; }
    }

    public static final List<Entry> COOLANTS = new ArrayList<>();

    public static void init() {
        COOLANTS.clear();

        for (String line : ConfigSIO.customCoolants) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            try {
                String[] parts = line.split(";");
                if (parts.length < 2) continue;

                String itemPart = parts[0].trim();
                String[] values = parts[1].trim().split(",");
                if (values.length != 4) continue;

                int cTable = Integer.parseInt(values[0].trim());
                int cBasin = Integer.parseInt(values[1].trim());
                int mTable = Integer.parseInt(values[2].trim());
                int mBasin = Integer.parseInt(values[3].trim());

                char shape = 'C';
                if (parts.length > 2) {
                    String shapeStr = parts[2].trim().toUpperCase();
                    if (!shapeStr.isEmpty() && (shapeStr.charAt(0) == 'C' || shapeStr.charAt(0) == 'S')) {
                        shape = shapeStr.charAt(0);
                    }
                }

                String[] itemParts = itemPart.split(":");
                if (itemParts.length < 2 || itemParts.length > 3) continue;

                ResourceLocation loc = new ResourceLocation(itemParts[0], itemParts[1]);
                Item item = ForgeRegistries.ITEMS.getValue(loc);
                if (item == null) {
                    SmelteryIO.logger.warn("Custom coolant item not found: {}", line);
                    continue;
                }

                int meta = 0;
                if (itemParts.length == 3) {
                    String metaStr = itemParts[2].trim();
                    meta = metaStr.equals("*") ? -1 : Integer.parseInt(metaStr);
                }

                COOLANTS.add(new Entry(item, meta, cTable, cBasin, mTable, mBasin, shape));
            } catch (Exception ex) {
                SmelteryIO.logger.warn("Failed to parse custom coolant line: {}", line);
            }
        }
    }
}
