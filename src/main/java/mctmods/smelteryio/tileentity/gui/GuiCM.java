package mctmods.smelteryio.tileentity.gui;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.library.util.network.NetworkHandler;
import mctmods.smelteryio.library.util.network.messages.MessageEmptyTank;
import mctmods.smelteryio.library.util.network.messages.MessageLockSlots;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;

import javax.annotation.Nonnull;

public class GuiCM extends GuiContainer {
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(SmelteryIO.MODID, "textures/gui/container/casting_machine.png");
	private final TileEntityCM tileEntity;
	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;
	private static final int PROGRESSWIDTH = 22;
	private static final int FLUIDHEIGHT = 52;
	private GuiButton buttonEmptyTank;
	private GuiButton buttonLockSlots;
	public static final int BUTTON_EMPTY_TANK = 0, BUTTON_LOCK_SLOTS = 1;

	public GuiCM(ContainerBase container, TileEntityCM tileEntity) {
		super(container);
		this.tileEntity = tileEntity;
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
	}

	@Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(BG_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (!tileEntity.isControlledByRedstone()) {
			drawTexturedModalRect(guiLeft + 123, guiTop + 52, 179, 63, 10, 10);
		}

		if (tileEntity.isActive()) {
			int progress = tileEntity.getGUIProgress(PROGRESSWIDTH);
			drawTexturedModalRect(guiLeft + 117, guiTop + 34, 176, 0, progress, 16);
		}

		if (!tileEntity.canWork()) {
			drawTexturedModalRect(guiLeft + 126, guiTop + 51, 192, 60, 4, 4);
			drawTexturedModalRect(guiLeft + 119, guiTop + 34, 176, 60, 16, 16);
		}

		if (tileEntity.isReady()) {
			drawTexturedModalRect(guiLeft + 142, guiTop + 33, 176, 60, 16, 16);
		}

		if (tileEntity.getCurrentMode() == TileEntityCM.BASIN) {
			drawTexturedModalRect(guiLeft + 47, guiTop + 52, 176, 60, 16, 16);
		} else {
			drawTexturedModalRect(guiLeft + 65, guiTop + 52, 176, 60, 16, 16);
		}

		if (tileEntity.getCurrentFluid() != null) {
			int fluidAmount = tileEntity.getGUIFluidBarHeight(FLUIDHEIGHT);
			GuiUtil.renderTiledFluid(guiLeft + 19, guiTop + 15 + FLUIDHEIGHT - fluidAmount, 12, fluidAmount, zLevel, tileEntity.getCurrentFluid());
		}

		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	@Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = I18n.format("container.casting_machine");
		fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 5, 0xffffff);

		int outputSize = tileEntity.getOutputStackSize();
		String outputName = String.valueOf(outputSize);
		fontRenderer.drawString(outputName, 151 - fontRenderer.getStringWidth(outputName) / 2, 18, 0x0000aa);

		List<String> tooltip = getTankTooltip(tileEntity.getTank(), tileEntity.getCurrentFluid(), mouseX, mouseY, guiLeft + 19, guiTop + 15, guiLeft + 38, guiTop + 67);
		if (tooltip != null) {
			drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop);
		}

		buttonEmptyTank.enabled = Util.isShiftKeyDown();
		buttonLockSlots.enabled = Util.isShiftKeyDown();

		if (buttonEmptyTank.isMouseOver()) {
			String[] desc = { TextFormatting.RED + I18n.format("container.casting_machine.buttontank.header"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttontank.info") };
			List<String> temp = Arrays.asList(desc);
			drawHoveringText(temp, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
		}

		if (buttonLockSlots.isMouseOver()) {
			if (tileEntity.isSlotsLocked()) {
				String[] desc = { TextFormatting.RED + I18n.format("container.casting_machine.buttonslot.header"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info1"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info2"), TextFormatting.DARK_GREEN + I18n.format("container.casting_machine.buttonslot.enabled") };
				List<String> temp = Arrays.asList(desc);
				drawHoveringText(temp, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
			} else {
				String[] desc = { TextFormatting.RED + I18n.format("container.casting_machine.buttonslot.header"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info1"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info2"), TextFormatting.DARK_RED + I18n.format("container.casting_machine.buttonslot.disabled") };
				List<String> temp = Arrays.asList(desc);
				drawHoveringText(temp, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
			}
		}
	}

	@Override public void initGui() {
		super.initGui();

		buttonEmptyTank = new GuiButton(BUTTON_EMPTY_TANK, guiLeft - 20, guiTop, 20, 20, "") {
			@Override public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				super.drawButton(mc, mouseX, mouseY, partialTicks);
				GuiCM.this.mc.getTextureManager().bindTexture(GuiCM.BG_TEXTURE);
				drawTexturedModalRect(x, y, 177, 17, 20, 20);
			}
		};

		buttonLockSlots = new GuiButton(BUTTON_LOCK_SLOTS, guiLeft - 20, guiTop + 20, 20, 20, "") {
			@Override public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				super.drawButton(mc, mouseX, mouseY, partialTicks);
				GuiCM.this.mc.getTextureManager().bindTexture(GuiCM.BG_TEXTURE);
				drawTexturedModalRect(x, y, 177, 39, 20, 20);
			}
		};

		buttonList.add(buttonEmptyTank);
		buttonList.add(buttonLockSlots);
		buttonEmptyTank.enabled = false;
		buttonLockSlots.enabled = false;
	}

	@Override protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case BUTTON_EMPTY_TANK:
				tileEntity.emptyTank();
				NetworkHandler.sendToServer(new MessageEmptyTank(tileEntity.getPos()));
				break;
			case BUTTON_LOCK_SLOTS:
				tileEntity.slotsLocked();
				NetworkHandler.sendToServer(new MessageLockSlots(tileEntity.getPos()));
				break;
		}
	}

	private static List<String> getTankTooltip(IFluidTank tank, FluidStack hovered, int mouseX, int mouseY, int xMin, int yMin, int xMax, int yMax) {
		if (xMin <= mouseX && mouseX < xMax && yMin <= mouseY && mouseY < yMax) {
			List<String> text = Lists.newArrayList();
			int amount = hovered != null ? hovered.amount : 0;
			if (amount == 0) {
				text.add(TextFormatting.GRAY + "Empty");
			} else {
				text.add(TextFormatting.WHITE + hovered.getLocalizedName());
			}
			text.add(String.format("%d / %d mB", amount, tank.getCapacity()));
			return text;
		}
		return null;
	}
}
