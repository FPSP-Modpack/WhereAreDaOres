package glowredman.wherearetheores.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import glowredman.wherearetheores.WATO;
import glowredman.wherearetheores.config.ConfigHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class WATORecipeHandler extends TemplateRecipeHandler {

	public static final int WIDTH = 170;
	public static final int HEIGHT = 160;
	public static final int TEXT_OFFSET_X = 4;
	public static final int TEXT_OFFSET_Y = 20;
	public static final int TEXT_SPACING = 9;
	public static final String OUTPUT_ID = "wato.ore";

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals(OUTPUT_ID)) {

			for (Entry<String, Map<String, List<String>>> e : ConfigHandler.config.entrySet()) {
				String ore = e.getKey();
				Map<String, List<String>> dimensionInfo = e.getValue();

				// decode ore:
				List<ItemStack> decodedOres = new ArrayList<ItemStack>();
				if (ore.startsWith("[")) {
					for (String item : ore.substring(1).split(";")) {
						decodedOres.add(WATO.findItem(item));
					}
				} else {
					decodedOres = OreDictionary.getOres(ore);
				}
				if (decodedOres.size() > 0) {
					this.arecipes.add(new CachedOreRecipe(dimensionInfo, decodedOres));
				}
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (ConfigHandler.possibleItems.containsKey(WATO.getUSIID(result))) {
			String key = ConfigHandler.possibleItems.get(WATO.getUSIID(result));

			// decode ore:
			List<ItemStack> decodedOres = new ArrayList<ItemStack>();
			if (key.startsWith("[")) {
				for (String item : key.substring(1).split(";")) {
					decodedOres.add(WATO.findItem(item));
				}
			} else {
				decodedOres = OreDictionary.getOres(key);
			}
			if (decodedOres.size() > 0) {
				this.arecipes.add(new CachedOreRecipe(ConfigHandler.config.get(key), decodedOres));
			}
		} else {
			super.loadCraftingRecipes(result);
		}
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
	}

	@Override
	public void drawForeground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		drawExtras(recipe);
	}

	@Override
	public void drawExtras(int recipe) {
		CachedOreRecipe cRecipe = (CachedOreRecipe) arecipes.get(recipe);
		List<String> dimensions = new ArrayList<String>();
		dimensions.addAll(cRecipe.dimensionInfo.keySet());
		FontRenderer font = GuiDraw.fontRenderer;
		for (int i = 0; i < dimensions.size(); i++) {
			String dim = dimensions.get(i);
			GuiDraw.drawString(dim, (WIDTH - font.getStringWidth(dim)) / 2, TEXT_OFFSET_Y + i * TEXT_SPACING, 0x404040,
					false);
		}
		drawTooltip(cRecipe);
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public String getRecipeName() {
		return I18n.format("gui.nei.wato");
	}

	@Override
	public String getGuiTexture() {
		return null;
	}

	@Override
	public void loadTransferRects() {
		int stringLenght = GuiDraw.getStringWidth(I18n.format("gui.nei.wato"));
		transferRects.add(new RecipeTransferRect(
				new Rectangle((WIDTH - stringLenght + 1) / 2, -12, stringLenght + 2, 10), OUTPUT_ID));
	}

	/**
	 * Draws a tooltip with details about the dimension the mouse is hovering over.
	 */
	public void drawTooltip(CachedOreRecipe recipe) {
		Point mouse = GuiDraw.getMousePosition();
		Point mouseRel = new Point(mouse.x - getXOffset(), mouse.y - getYOffset());

		// Check, if the mouse is inside the GUI (horizontally)
		if (mouseRel.x <= WIDTH && mouseRel.x >= 0) {

			// Get the line, the mouse is hovering over
			int line = (int) Math.floor((mouseRel.y - TEXT_OFFSET_Y) / TEXT_SPACING);

			// Check, if the mouse position is inside the text area (vertically)
			if (line < recipe.dimensionInfo.size() && line >= 0) {
				String dim = (String) recipe.dimensionInfo.keySet().toArray()[line];

				// Don't draw the tooltip, if it wouldn't contain text
				if (recipe.dimensionInfo.get(dim).size() > 0) {
					GuiDraw.drawMultilineTip(mouseRel.x + 8, mouseRel.y - TEXT_OFFSET_Y + 32,
							recipe.dimensionInfo.get(dim));
				}
			}
		}
	}

	private int getXOffset() {
		return (GuiDraw.displaySize().width - WIDTH) / 2;
	}

	private int getYOffset() {
		return (GuiDraw.displaySize().height - HEIGHT) / 2 + 13;
	}

	public class CachedOreRecipe extends CachedRecipe {

		public Map<String, List<String>> dimensionInfo;
		public PositionedStack ore;
		public int numVariants;

		public CachedOreRecipe(Map<String, List<String>> dimensionInfo, List<ItemStack> ores) {
			this.dimensionInfo = dimensionInfo;
			this.ore = new PositionedStack(ores, (WIDTH - 18) / 2, 1);
			this.numVariants = ores.size();
		}

		@Override
		public PositionedStack getIngredient() {
			ore.setPermutationToRender((cycleticks / 20) % numVariants);
			return ore;
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

	}

}
