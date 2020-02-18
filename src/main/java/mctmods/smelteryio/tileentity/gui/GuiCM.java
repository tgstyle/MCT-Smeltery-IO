package mctmods.smelteryio.tileentity.gui;

import com.google.common.collect.Lists;

import java.io.IOException;

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

public class GuiCM extends GuiContainer {
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(SmelteryIO.MODID, "textures/gui/container/casting_machine.png");

	private TileEntityCM tileEntity;

	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;

	private static int PROGRESSWIDTH = 22;
	private static int FLUIDHEIGHT = 52;

	private GuiButton buttonEmptyTank;
	private GuiButton buttonLockSlots;

	public static final int BUTTON_EMPTY_TANK = 0, BUTTON_LOCK_SLOTS = 1;

	public GuiCM(ContainerBase serverGuiElement, TileEntityCM tileEntity) {
		super(serverGuiElement);
		this.tileEntity = tileEntity;
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(BG_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if(!this.tileEntity.isControlledByRedstone()) this.drawTexturedModalRect(guiLeft + 123, guiTop + 52, 179, 63, 10, 10);
		if(!this.tileEntity.canWork()) {
			this.drawTexturedModalRect(guiLeft + 126, guiTop + 51, 192, 60, 4, 4);
			this.drawTexturedModalRect(guiLeft + 119, guiTop + 34, 176, 60, 16, 16);
		}
		if(!this.tileEntity.isReady()) this.drawTexturedModalRect(guiLeft + 142, guiTop + 33, 176, 60, 16, 16);
		if(this.tileEntity.getCurrentMode() == TileEntityCM.BASIN) {
			this.drawTexturedModalRect(guiLeft + 47, guiTop + 52, 176, 60, 16, 16);
		} else {
			this.drawTexturedModalRect(guiLeft + 65, guiTop + 52, 176, 60, 16, 16);
		}
		if(this.tileEntity.isFueled() && this.tileEntity.isProgressing() != 0) {
			int progress = this.tileEntity.getGUIProgress(PROGRESSWIDTH);
			this.drawTexturedModalRect(guiLeft + 117, guiTop + 34, 176, 0, progress, 16);
		}
		if(this.tileEntity.getCurrentFluid() != null) {
			int fluidAmount = this.tileEntity.getGUIFluidBarHeight(FLUIDHEIGHT);
			GuiUtil.renderTiledFluid(guiLeft + 19, guiTop + 15 + FLUIDHEIGHT - fluidAmount, 12, fluidAmount, this.zLevel, this.tileEntity.getCurrentFluid());
		}
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = I18n.format("container.casting_machine");
		fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 5, 0xffffff);
		int outputSize = this.tileEntity.getOutputStackSize();
		String outputname = String.valueOf(outputSize);
		this.fontRenderer.drawString(outputname, 151 - fontRenderer.getStringWidth(outputname) / 2, 18, 0x0000aa);
		if(this.tileEntity.getCurrentFluid() != null) {
			List<String> tooltip = getTankTooltip(this.tileEntity.getTank(), this.tileEntity.getCurrentFluid(), mouseX, mouseY, guiLeft + 19, guiTop + 15, guiLeft + 38, guiTop + 67);
			if(tooltip != null) {
				this.drawHoveringText(tooltip, mouseX-guiLeft, mouseY-guiTop);
			}
		}
		buttonEmptyTank.enabled = Util.isShiftKeyDown();
		buttonLockSlots.enabled = Util.isShiftKeyDown();
		if(buttonEmptyTank.isMouseOver()) {
			String[] desc = {TextFormatting.RED + I18n.format("container.casting_machine.buttontank.header"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttontank.info")};
			List<String> temp = Arrays.asList(desc);
			drawHoveringText(temp, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
		}
		if(buttonLockSlots.isMouseOver()) {
			if(this.tileEntity.isSlotsLocked()) {
				String[] desc = {TextFormatting.RED + I18n.format("container.casting_machine.buttonslot.header"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info1"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info2"), TextFormatting.DARK_GREEN + I18n.format("container.casting_machine.buttonslot.enabled")};
				List<String> temp = Arrays.asList(desc);
				drawHoveringText(temp, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
			} else {
				String[] desc = {TextFormatting.RED + I18n.format("container.casting_machine.buttonslot.header"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info1"), TextFormatting.GRAY + I18n.format("container.casting_machine.buttonslot.info2"), TextFormatting.DARK_RED + I18n.format("container.casting_machine.buttonslot.disabled")};
				List<String> temp = Arrays.asList(desc);
				drawHoveringText(temp, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonEmptyTank = new GuiButton(BUTTON_EMPTY_TANK, guiLeft - 20,  guiTop + ySize - 166, 20, 20, "") {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				super.drawButton(mc, mouseX, mouseY, partialTicks);
				GuiCM.this.mc.getTextureManager().bindTexture(GuiCM.BG_TEXTURE);
				this.drawTexturedModalRect(x, y, 177, 17, 20, 20);
			}
		};
		buttonLockSlots = new GuiButton(BUTTON_LOCK_SLOTS, guiLeft - 20,  guiTop + ySize - 146, 20, 20, "") {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				super.drawButton(mc, mouseX, mouseY, partialTicks);
				GuiCM.this.mc.getTextureManager().bindTexture(GuiCM.BG_TEXTURE);
				this.drawTexturedModalRect(x, y, 177, 39, 20, 20);
			}
		};
		buttonList.add(buttonEmptyTank);
		buttonList.add(buttonLockSlots);
		buttonEmptyTank.enabled = false;
		buttonLockSlots.enabled = false;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id) {
			case BUTTON_EMPTY_TANK:
				this.tileEntity.emptyTank();
				NetworkHandler.sendToServer(new MessageEmptyTank(this.tileEntity.getPos()));
				break;
			case BUTTON_LOCK_SLOTS:
				this.tileEntity.slotsLocked();
				NetworkHandler.sendToServer(new MessageLockSlots(this.tileEntity.getPos()));
			default:
				break;
		}
	}

	private static List<String> getTankTooltip(IFluidTank tank, FluidStack fluid, int mouseX, int mouseY, int xmin, int ymin, int xmax, int ymax) {
		if(xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
			FluidStack hovered = fluid;
			List<String> text = Lists.newArrayList();
			if(hovered == null) {
				text.add(Util.translateFormatted("gui.smeltery.capacity_used"));
			} else {
				text.add(TextFormatting.WHITE + hovered.getLocalizedName());
				GuiUtil.liquidToString(hovered, text);
			}
			return text;
		}
		return null;
	}

}