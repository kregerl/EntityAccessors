package com.loucaskreger.entityaccessors.client.gui.screen;

import javax.annotation.Nullable;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.container.AbstractAccessorContainer;
import com.loucaskreger.entityaccessors.tileentity.AbstractAccessorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractAccessorScreen extends ContainerScreen<AbstractAccessorContainer> {

	@Nullable
	public AbstractAccessorTileEntity tileEntity;
//	@Nullable
//	public LivingEntity entity;
	private final ResourceLocation texture = new ResourceLocation(EntityAccessors.MOD_ID,
			"textures/gui/entityaccessor.png");
	// the previous mouseX and mouseY
	private float prevMouseX;
	private float prevMouseY;

	public AbstractAccessorScreen(AbstractAccessorContainer screenContainer, PlayerInventory inv,
			ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
//		this.texture = new ResourceLocation(EntityAccessors.MOD_ID, getTexturePath(screenContainer));
		this.tileEntity = screenContainer.getTileEntity();
//		this.entity = null;

		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 165;
	}
//
//	public String getTexturePath(AbstractAccessorContainer container) {
//		return "textures/gui/entityaccessor.png";
//	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(texture);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		LivingEntity entity = AbstractAccessorTileEntity.getEntityAbove(this.tileEntity.getPos(),
				this.tileEntity.getWorld());
		if (entity != null) {
			// posX posY
			drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - this.prevMouseX,
					(float) (j + 75 - 50) - this.prevMouseY, entity);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		this.prevMouseX = mouseX;
		this.prevMouseY = mouseY;
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

//		System.out.println("Screen: " + this.tileEntity.getSelectedEntity().toString());
	}

	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,
			LivingEntity entity) {
		if (entity != null) {
			float f = (float) Math.atan((double) (mouseX / 40.0F));
			float f1 = (float) Math.atan((double) (mouseY / 40.0F));
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float) posX, (float) posY, 1050.0F);
			RenderSystem.scalef(1.0F, 1.0F, -1.0F);
			MatrixStack matrixstack = new MatrixStack();
			matrixstack.translate(0.0D, 0.0D, 1000.0D);
			matrixstack.scale((float) scale, (float) scale, (float) scale);
			Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
			Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
			quaternion.multiply(quaternion1);
			matrixstack.rotate(quaternion);
			float f2 = entity.renderYawOffset;
			float f3 = entity.rotationYaw;
			float f4 = entity.rotationPitch;
			float f5 = entity.prevRotationYawHead;
			float f6 = entity.rotationYawHead;
			entity.renderYawOffset = 180.0F + f * 20.0F;
			entity.rotationYaw = 180.0F + f * 40.0F;
			entity.rotationPitch = -f1 * 20.0F;
			entity.rotationYawHead = entity.rotationYaw;
			entity.prevRotationYawHead = entity.rotationYaw;
			EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
			quaternion1.conjugate();
			entityrenderermanager.setCameraOrientation(quaternion1);
			entityrenderermanager.setRenderShadow(false);
			IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers()
					.getBufferSource();
			entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack,
					irendertypebuffer$impl, 15728880);
			irendertypebuffer$impl.finish();
			entityrenderermanager.setRenderShadow(true);
			entity.renderYawOffset = f2;
			entity.rotationYaw = f3;
			entity.rotationPitch = f4;
			entity.prevRotationYawHead = f5;
			entity.rotationYawHead = f6;
			RenderSystem.popMatrix();
		}
	}

}
