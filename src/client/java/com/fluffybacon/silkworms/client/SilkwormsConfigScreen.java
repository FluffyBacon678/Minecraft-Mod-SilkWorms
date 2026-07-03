package com.fluffybacon.silkworms.client;

import com.fluffybacon.silkworms.SilkwormsConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.IntConsumer;

/**
 * Minimal settings screen built from vanilla widgets only — five sliders and
 * one toggle. Values are applied live and saved (clamped) when the screen
 * closes. Opened from Mod Menu's config button.
 */
public class SilkwormsConfigScreen extends Screen {
	private final Screen parent;

	public SilkwormsConfigScreen(Screen parent) {
		super(Text.translatable("silkworms.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		SilkwormsConfig config = SilkwormsConfig.get();
		int x = this.width / 2 - 100;
		int y = 36;
		addDrawableChild(new IntSliderWidget(x, y, "silkworms.config.grass_plants_required", 1, 10,
				config.grassPlantsRequired, value -> config.grassPlantsRequired = value));
		y += 24;
		addDrawableChild(new IntSliderWidget(x, y, "silkworms.config.eat_cooldown_min", 2, 60,
				config.eatCooldownMinSeconds, value -> config.eatCooldownMinSeconds = value));
		y += 24;
		addDrawableChild(new IntSliderWidget(x, y, "silkworms.config.eat_cooldown_max", 5, 120,
				config.eatCooldownMaxSeconds, value -> config.eatCooldownMaxSeconds = value));
		y += 24;
		addDrawableChild(new IntSliderWidget(x, y, "silkworms.config.cocoon_growth", 10, 600,
				config.cocoonGrowthSeconds, value -> config.cocoonGrowthSeconds = value));
		y += 24;
		addDrawableChild(new IntSliderWidget(x, y, "silkworms.config.moth_lifetime", 30, 1200,
				config.silkMothLifetimeSeconds, value -> config.silkMothLifetimeSeconds = value));
		y += 24;
		addDrawableChild(CyclingButtonWidget.onOffBuilder(config.naturalSilkwormSpawning)
				.build(x, y, 200, 20, Text.translatable("silkworms.config.natural_spawning"),
						(button, value) -> config.naturalSilkwormSpawning = value));
		addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
				.dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
	}

	@Override
	public void close() {
		SilkwormsConfig.get().sanitizeAndSave();
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 14, 0xFFFFFFFF);
	}

	/** A 200x20 slider over an integer range with a labelled value. */
	private static class IntSliderWidget extends SliderWidget {
		private final String translationKey;
		private final int min;
		private final int max;
		private final IntConsumer setter;

		IntSliderWidget(int x, int y, String translationKey, int min, int max, int current, IntConsumer setter) {
			super(x, y, 200, 20, Text.empty(), (current - min) / (double) (max - min));
			this.translationKey = translationKey;
			this.min = min;
			this.max = max;
			this.setter = setter;
			updateMessage();
		}

		private int intValue() {
			return (int) Math.round(this.min + this.value * (this.max - this.min));
		}

		@Override
		protected void updateMessage() {
			setMessage(Text.translatable(this.translationKey).append(": " + intValue()));
		}

		@Override
		protected void applyValue() {
			this.setter.accept(intValue());
		}
	}
}
