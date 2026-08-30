package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Arrays;

public class WndChooseEnchant extends Window {

	private static final int WIDTH      = 120;
	private static final int MAX_HEIGHT = 160;
	private static final int BTN_HEIGHT = 18;
	private static final int GAP        = 4;

	public interface Listener {
		void onEnchantChosen(Object enchant);
	}

	public WndChooseEnchant(Item item, Listener listener) {
		super();

		ArrayList<Class<?>> options = new ArrayList<>();
		if (item instanceof Weapon) {
			options.addAll(Arrays.asList(Weapon.Enchantment.common));
			options.addAll(Arrays.asList(Weapon.Enchantment.uncommon));
			options.addAll(Arrays.asList(Weapon.Enchantment.rare));
		} else if (item instanceof Armor) {
			options.addAll(Arrays.asList(Armor.Glyph.common));
			options.addAll(Arrays.asList(Armor.Glyph.uncommon));
			options.addAll(Arrays.asList(Armor.Glyph.rare));
		}

		int winHeight = Math.min(MAX_HEIGHT, options.size() * (BTN_HEIGHT + GAP));
		resize(WIDTH, winHeight);

		ScrollPane pane = new ScrollPane(new Component());
		add(pane);
		pane.setRect(0, 0, WIDTH, winHeight);

		Component content = pane.content();

		int y = 0;
		for (Class<?> cls : options) {
			try {
				final Object instance = cls.getDeclaredConstructor().newInstance();
				RedButton btn = new RedButton(Messages.titleCase(cls.getSimpleName())) {
					@Override
					protected void onClick() {
						hide();
						listener.onEnchantChosen(instance);
					}
				};
				btn.setRect(0, y, WIDTH, BTN_HEIGHT);
				content.add(btn);
				y += BTN_HEIGHT + GAP;
			} catch (Exception e) {
				//skip classes that can't be no-arg constructed
			}
		}

		content.setRect(0, 0, WIDTH, y);
		pane.setRect(0, 0, WIDTH, winHeight); // re-layout now that content's real height is known
	}
}
