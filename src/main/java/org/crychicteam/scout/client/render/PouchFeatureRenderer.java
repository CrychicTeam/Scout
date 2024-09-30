package org.crychicteam.scout.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class PouchFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private final ItemInHandRenderer itemInHandRenderer;

	public PouchFeatureRenderer(LivingEntityRenderer<? extends Player, ? extends PlayerModel<? extends Player>> renderer, ItemInHandRenderer itemInHandRenderer) {
		super((RenderLayerParent<T, M>) renderer);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}

		var leftPouch = ScoutUtil.findBagItem(player, BaseBagItem.BagType.POUCH, false);
		var rightPouch = ScoutUtil.findBagItem(player, BaseBagItem.BagType.POUCH, true);

		if (!leftPouch.isEmpty()) {
			poseStack.pushPose();
			((PlayerModel<?>) this.getParentModel()).leftLeg.translateAndRotate(poseStack);
			poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, -(float) Math.PI / 2));
			poseStack.scale(0.325F, 0.325F, 0.325F);
			poseStack.translate(0F, -0.325F, -0.475F);
			this.itemInHandRenderer.renderItem(player, leftPouch.get(), ItemDisplayContext.FIXED, false, poseStack, buffer, packedLight);
			poseStack.popPose();
		}

		if (!rightPouch.isEmpty()) {
			poseStack.pushPose();
			((PlayerModel<?>) this.getParentModel()).rightLeg.translateAndRotate(poseStack);
			poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, -(float) Math.PI / 2));
			poseStack.scale(0.325F, 0.325F, 0.325F);
			poseStack.translate(0F, -0.325F, 0.475F);
			this.itemInHandRenderer.renderItem(player, rightPouch.get(), ItemDisplayContext.FIXED, false, poseStack, buffer, packedLight);
			poseStack.popPose();
		}
	}
}