package mctmods.smelteryio.tileentity.gui;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

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
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexturedModalRect(guiLeft - 110, guiTop + 10, 146, 170, 110, 60);

        if (this.tileEntity.isReady()) {
        	int progress = this.tileEntity.getGUIProgress(PROGRESSHEIGHT);
        	if (this.tileEntity.getCurrentTemp() == 0) {
            	progress = PROGRESSHEIGHT;
        	}
        	drawTexturedModalRect(guiLeft + 103, guiTop + 36 + progress, 176, 33 + progress, 13, 13 - progress);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        String name = I18n.format("container.fuel_controller");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 5, 0xffffff);

        String temperatureInfo = TextFormatting.RED + I18n.format("container.fuel_controller.temperature", new Object[0]);
        this.fontRenderer.drawString(temperatureInfo, (-55 - (this.fontRenderer.getStringWidth(temperatureInfo))/2), 14, 4210752);

        String currentTempture = TextFormatting.RED + String.valueOf(this.tileEntity.getFuelTemp());
        this.fontRenderer.drawString(currentTempture, (-55 - (this.fontRenderer.getStringWidth(currentTempture))/2), 26, 4210752);

        double ratio = this.tileEntity.getRatio();
        String msgRatio = TextFormatting.AQUA + I18n.format("container.fuel_controller.ratio", new Object[0]) + " " + ratio;
        this.fontRenderer.drawString(msgRatio, (-55 - (this.fontRenderer.getStringWidth(msgRatio))/2), 37, 4210752);

        if (this.tileEntity.getCurrentTemp() == 0 && this.tileEntity.atCapacity()) {
            String warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.error", new Object[0]);
            this.fontRenderer.drawString(warn, (-55 - (this.fontRenderer.getStringWidth(warn))/2), 49, 4210752);
        } else {
        	if (!this.tileEntity.atCapacity()) {
        		String warn = TextFormatting.DARK_RED + I18n.format("container.fuel_controller.errorcapacity", new Object[0]);
        		this.fontRenderer.drawString(warn, (-55 - (this.fontRenderer.getStringWidth(warn))/2), 49, 4210752);
        	}
        }
    }

}
