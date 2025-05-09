package com.kushcola.bloodmagic.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.container.item.ContainerFilter;
import com.kushcola.bloodmagic.common.item.routing.IItemFilterProvider;
import com.kushcola.bloodmagic.network.RouterFilterPacket;
import com.kushcola.bloodmagic.util.GhostItemHelper;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenFilter extends ScreenBase<ContainerFilter> {
	private static final ResourceLocation BACKGROUND = BloodMagic.rl("textures/gui/routingfilter.png");
	private final ContainerFilter container;
	private final Player player;
	private int left, top;

	private EditBox textBox;
	private int numberOfAddedButtons;
	private final List<String> buttonKeyList = new ArrayList<>();
	private final List<Button> buttonList = new ArrayList<>();

	public ScreenFilter(ContainerFilter container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		this.container = container;
		this.player = playerInventory.player;
		imageWidth = 176;
		imageHeight = 187;
	}

	@Override
	public void init() {
		super.init();
		left = (width - imageWidth) / 2;
		top = (height - imageHeight) / 2;

		// Use Component.translatable for the grey “search” hint
		textBox = new EditBox(
				Minecraft.getInstance().font,
				left + 23, top + 19,
				70, 12,
				Component.translatable("itemGroup.search")
		);
		textBox.setBordered(false);
		textBox.setMaxLength(50);
		textBox.setVisible(true);
		textBox.setTextColor(0xFFFFFF);
		textBox.setValue("");
		setInitialFocus(textBox);

		numberOfAddedButtons = 0;
		buttonKeyList.clear();
		buttonList.clear();

		ItemStack filterStack = container.filterStack;
		if (filterStack.getItem() instanceof IItemFilterProvider provider) {
			List<Pair<String, Button.OnPress>> actions = provider.getButtonAction(container);
			for (Pair<String, Button.OnPress> pair : actions) {
				String key = pair.getKey();
				if (buttonKeyList.contains(key)) continue;
				buttonKeyList.add(key);

				Pair<Integer, Integer> loc = getButtonLocation(numberOfAddedButtons);
				// Empty label, so use Component.literal("")
				Button btn = new Button(
						left + loc.getLeft(),
						top + loc.getRight(),
						20, 20,
						Component.literal(""),
						pair.getRight()
				);
				if (!provider.isButtonGlobal(filterStack, key)) {
					btn.active = false;
				}
				addRenderableWidget(btn);
				buttonList.add(btn);
				numberOfAddedButtons++;
			}
		}
	}

	private Pair<Integer, Integer> getButtonLocation(int index) {
		int x = 7 + 20 * index;
		int y = 32;
		return Pair.of(x, y);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		textBox.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// handle backspace/delete in the EditBox and update ghost slot
		if (textBox.isFocused() && (keyCode == 259 || keyCode == 261) && container.lastGhostSlotClicked != -1) {
			String s = textBox.getValue();
			if (!s.isEmpty()) {
				s = s.substring(0, s.length() - 1);
				textBox.setValue(s);
				updateGhostAmount(s);
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		// only digits, update ghost
		if (Character.isDigit(typedChar) && textBox.charTyped(typedChar, keyCode)) {
			updateGhostAmount(textBox.getValue());
			return true;
		}
		return super.charTyped(typedChar, keyCode);
	}

	private void updateGhostAmount(String str) {
		int amount = 0;
		if (!str.isEmpty()) {
			try { amount = Integer.parseInt(str); } catch (NumberFormatException ignored) {}
		}
		int slot = container.lastGhostSlotClicked;
		if (slot >= 0) {
			Slot s = container.getSlot(slot);
			ItemStack ghost = s.getItem();
			if (!ghost.isEmpty()) {
				GhostItemHelper.setItemGhostAmount(ghost, amount);
				GhostItemHelper.setItemGhostAmount(container.inventoryFilter.getItem(slot), amount);
				if (container.filterStack.getItem() instanceof IItemFilterProvider prov) {
					prov.setGhostItemAmount(container.filterStack, slot, amount);
				}
				BloodMagic.packetHandler.sendToServer(new RouterFilterPacket(
						player.getInventory().selected, slot, amount
				));
			}
		}
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		super.mouseClicked(mx, my, button);
		if (textBox.mouseClicked(mx, my, button)) return true;

		int slot = container.lastGhostSlotClicked;
		if (slot >= 0) {
			enableAllButtons();
			Slot s = container.getSlot(slot);
			ItemStack st = s.getItem();
			textBox.setValue(st.isEmpty() ? "" : String.valueOf(GhostItemHelper.getItemGhostAmount(st)));
		}
		return true;
	}

	private void enableAllButtons() {
		for (AbstractWidget b : buttonList) b.active = true;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

	@Override
	protected void renderLabels(PoseStack pose, int mx, int my) {
		// "container.inventory" becomes translatable
		font.draw(pose, Component.translatable("container.inventory"), 8, 93, 0x404040);
		font.draw(pose, container.filterStack.getHoverName(), 8, 4, 0x404040);

		// Draw the little state icons
		if (container.filterStack.getItem() instanceof IItemFilterProvider prov) {
			for (int i = 0; i < numberOfAddedButtons; i++) {
				Pair<Integer, Integer> bl = getButtonLocation(i);
				Pair<Integer, Integer> tx = prov.getTexturePositionForState(
						container.filterStack, buttonKeyList.get(i), container.lastGhostSlotClicked
				);
				RenderSystem.setShaderColor(1,1,1,1);
				RenderSystem.setShaderTexture(0, BACKGROUND);
				blit(pose,
						leftPos + bl.getLeft(), topPos + bl.getRight(),
						tx.getLeft(), tx.getRight(),
						20, 20
				);
			}
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float partial, int mx, int my) {
		RenderSystem.setShaderColor(1,1,1,1);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		blit(pose, x, y, 0, 0, imageWidth, imageHeight);

		// highlight the last ghost slot
		int slot = container.lastGhostSlotClicked;
		if (slot >= 0) {
			int col = x + 106 + 21 * (slot % 3);
			int row = y + 11 + 21 * (slot / 3);
			blit(pose, col, row, 0, 187, 24, 24);
		}
	}

	@Override
	public void render(PoseStack pose, int mx, int my, float partial) {
		super.render(pose, mx, my, partial);
		textBox.render(pose, mx, my, partial);

		List<Component> tip = new ArrayList<>();
		if (container.filterStack.getItem() instanceof IItemFilterProvider prov) {
			for (int i = 0; i < numberOfAddedButtons; i++) {
				Pair<Integer, Integer> bl = getButtonLocation(i);
				int bx = leftPos + bl.getLeft(), by = topPos + bl.getRight();
				if (mx >= bx && mx < bx + 20 && my >= by && my < by + 20) {
					List<Component> c = prov.getTextForHoverItem(
							container.filterStack, buttonKeyList.get(i), container.lastGhostSlotClicked
					);
					if (c != null && !c.isEmpty()) tip.addAll(c);
				}
			}
		}
		if (!tip.isEmpty()) renderTooltip(pose, tip, Optional.empty(), mx, my, font);
	}
}