package org.crychicteam.scout.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class PouchFeatureRenderer<T extends AbstractClientPlayer, M extends PlayerModel<T>> extends RenderLayer<T, M> {
	private final ItemInHandRenderer itemInHandRenderer;

	public PouchFeatureRenderer(RenderLayerParent<T, M> renderer, ItemInHandRenderer itemInHandRenderer) {
		super(renderer);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		var leftPouch = ScoutUtil.findBagItem(player, BaseBagItem.BagType.POUCH, false);
		var rightPouch = ScoutUtil.findBagItem(player, BaseBagItem.BagType.POUCH, true);

		if (!leftPouch.isEmpty()) {
			poseStack.pushPose();
			this.getParentModel().leftLeg.translateAndRotate(poseStack);
			poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, -(float) Math.PI / 2));
			poseStack.scale(0.325F, 0.325F, 0.325F);
			poseStack.translate(0F, -0.325F, -0.475F);
			this.itemInHandRenderer.renderItem(player, leftPouch.get(), ItemDisplayContext.FIXED, false, poseStack, buffer, packedLight);
			poseStack.popPose();
		}

		if (!rightPouch.isEmpty()) {
			poseStack.pushPose();
			this.getParentModel().rightLeg.translateAndRotate(poseStack);
			poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, -(float) Math.PI / 2));
			poseStack.scale(0.325F, 0.325F, 0.325F);
			poseStack.translate(0F, -0.325F, 0.475F);
			this.itemInHandRenderer.renderItem(player, rightPouch.get(), ItemDisplayContext.FIXED, false, poseStack, buffer, packedLight);
			poseStack.popPose();
		}
	}
}