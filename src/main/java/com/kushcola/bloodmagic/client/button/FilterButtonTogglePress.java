package com.kushcola.bloodmagic.client.button;

import com.kushcola.bloodmagic.common.container.item.ContainerFilter;
import com.kushcola.bloodmagic.common.item.routing.IItemFilterProvider;
import com.kushcola.bloodmagic.network.BloodMagicPacketHandler;
import com.kushcola.bloodmagic.network.FilterButtonPacket;
import net.minecraft.client.gui.components.Button;

public class FilterButtonTogglePress implements Button.OnPress
{
	private final String buttonKey;
	private final ContainerFilter container;

	public FilterButtonTogglePress(String key, ContainerFilter container)
	{
		this.buttonKey = key;
		this.container = container;
	}

	@Override
	public void onPress(Button button)
	{
		if (button.active)
		{
			int currentGhostSlot = container.lastGhostSlotClicked;
			if (container.filterStack.getItem() instanceof IItemFilterProvider)
			{
				int currentButtonState = ((IItemFilterProvider) container.filterStack.getItem()).getCurrentButtonState(container.filterStack, buttonKey, currentGhostSlot);

				BloodMagicPacketHandler.INSTANCE.sendToServer(new FilterButtonPacket(container.player.getInventory().selected, currentGhostSlot, buttonKey, currentButtonState));

				((IItemFilterProvider) container.filterStack.getItem()).receiveButtonPress(container.filterStack, buttonKey, currentGhostSlot, currentButtonState);
			}
		}
	}
}