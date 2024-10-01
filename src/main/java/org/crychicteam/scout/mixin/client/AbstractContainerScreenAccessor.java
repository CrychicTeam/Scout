package org.crychicteam.scout.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor<T extends AbstractContainerMenu> {
	@Accessor("leftPos")
	int getLeftPos();

	@Accessor("topPos")
	int getTopPos();

	@Accessor("imageWidth")
	int getImageWidth();

	@Accessor("imageHeight")
	int getImageHeight();

	@Accessor("menu")
	T getMenu();
}