package com.loucaskreger.entityaccessors.client.gui.screen;

import com.loucaskreger.entityaccessors.container.AbstractAccessorContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class AccessorScreen extends AbstractAccessorScreen {

	public AccessorScreen(AbstractAccessorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

}
