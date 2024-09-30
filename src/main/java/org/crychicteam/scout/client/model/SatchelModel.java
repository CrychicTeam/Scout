package org.crychicteam.scout.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SatchelModel<T extends LivingEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "satchel"), "main");
	private final ModelPart root;
	private final ModelPart satchel;
	private final ModelPart strap;

	public SatchelModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
		this.satchel = root.getChild("satchel");
		this.strap = this.satchel.getChild("strap");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition satchel = partdefinition.addOrReplaceChild("satchel", CubeListBuilder.create().texOffs(10, 0).addBox(-6.0F, -12.0F, -2.5F, 2.0F, 3.0F, 5.0F, new CubeDeformation(0.275F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition strap = satchel.addOrReplaceChild("strap", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -13.0F, -2.0F, 1.0F, 14.0F, 4.0F, new CubeDeformation(0.275F)), PartPose.offsetAndRotation(-3.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		satchel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}