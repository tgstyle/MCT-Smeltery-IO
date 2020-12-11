package mctmods.smelteryio.tileentity.gui;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;

public class GuiFC extends GuiContainer {
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(SmelteryIO.MODID, "textures/gui/container/fuel_controller.png");
	private TileEntityFC tileEntity;
	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;
	private static int PROGRESSHEIGHT = 13;

	public GuiFC(ContainerBase serverGuiElement, TileEntityFC tileEntity) {
		super(serverGuiElement);
		this.tileEntity = tileEntity;
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(BG_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft - 110, guiTop, 146, 170, 110, 60);
		if(tileEntity.isActive()) {
			int progress = tileEntity.getGUIProgress(PROGRESSHEIGHT);
			if(!tileEntity.isActive()) progress = PROGRESSHEIGHT;
			drawTexturedModalRect(guiLeft + 81, guiTop + 37 + progress, 176, 33 + progress, 13, 13 - progress);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = I18n.format("container.fuel_controller");
		fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 5, 0xffffff);
		String temperatureInfo = TextFormatting.RED + I18n.format("container.fuel_controller.temperature", new Object[0]);
		fontRenderer.drawString(temperatureInfo, (-55 - (fontRenderer.getStringWidth(temperatureInfo))/2), 9, 4210752);
		String currentTemperature = TextFormatting.RED + Util.temperatureString(tileEntity.getFuelTemp() + 300);
		fontRenderer.drawString(currentTemperature, (-55 - (fontRenderer.getStringWidth(currentTemperature))/2), 21, 4210752);
		double ratio = tileEntity.getRatio();
		String msgRatio = TextFormatting.AQUA + I18n.format("container.fuel_controller.ratio", new Object[0]) + " " + ratio;
		fontRenderer.drawString(msgRatio, (-55 - (fontRenderer.getStringWidth(msgRatio))/2), 32, 4210752);
		String warn = null;
		if(!tileEntity.isHeatingSmeltery()) warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.erroritems", new Object[0]);
		if(tileEntity.atCapacity()) warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.errorcapacity", new Object[0]);
		if(!tileEntity.isReady()) warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.errorfuel", new Object[0]);
		if(!tileEntity.isFueled()) warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.errorsmelteryfuel", new Object[0]);
		if(!tileEntity.getOwner()) warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.errorowner", new Object[0]);
		if(!tileEntity.hasController()) warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.errorsmeltery", new Object[0]);		
		if(warn != null) fontRenderer.drawString(warn, (-55 - (fontRenderer.getStringWidth(warn))/2), 44, 4210752);
	}

}