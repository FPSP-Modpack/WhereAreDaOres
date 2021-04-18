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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class WATORecipeHandler extends TemplateRecipeHandler {

	public static final int WIDTH = 176;
	public static final int HEIGHT = 160;
	public static int textOffset = 20;
	public static int lineHeight = 9;
	public static int tooltipOffsetX = 8;
	public static int tooltipOffsetY = -12;
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
		String key = ConfigHandler.possibleItems.get(WATO.getStackName(result));
		if (key != null) {

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
			GuiDraw.drawString(dim, (WIDTH - font.getStringWidth(dim)) / 2, textOffset + i * lineHeight, 0x404040,
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
		if (WATO.largeNEI) {
			transferRects.add(new RecipeTransferRect(new Rectangle(13, -29, 139, 26), OUTPUT_ID));
		} else {
			int stringLenght = GuiDraw.getStringWidth(I18n.format("gui.nei.wato"));
			transferRects.add(new RecipeTransferRect(
					new Rectangle((WIDTH - stringLenght + 1) / 2, -12, stringLenght + 2, 10), OUTPUT_ID));
		}
	}

	/**
	 * Draws a tooltip with details about the dimension the mouse is hovering over.
	 */
	private void drawTooltip(CachedOreRecipe recipe) {

		// Get mouse position relative to
		Point mouseScreen = GuiDraw.getMousePosition();
		Point mouseGUI = new Point(mouseScreen.x - getXOffset(), mouseScreen.y - getYOffset());

		// Exit if the mouse is not inside the text area (horizontally)
		if (mouseGUI.x >= WIDTH || mouseGUI.x < 0)
			return;

		// Get number of lines in the text area
		int toolTipSize = recipe.dimensionInfo.size();

		// Exit if there is nothing to be displayed
		if (toolTipSize == 0)
			return;

		// Get the line, the mouse is hovering over
		int dY = mouseGUI.y - textOffset;
		int line = dY / lineHeight;

		// Exit if the mouse is not inside the text area (vertically)
		if (dY < 0 || line >= toolTipSize)
			return;

		// Get the dimension's info text
		List<String> infoText = (List<String>) recipe.dimensionInfo.values().toArray()[line];

		// Exit if the info text is empty
		if (infoText.size() == 0)
			return;

		// Display the info text
		GuiDraw.drawMultilineTip(mouseGUI.x + tooltipOffsetX, mouseGUI.y + tooltipOffsetY, infoText);
	}

	private int getXOffset() {
		return (GuiDraw.displaySize().width - WIDTH) / 2;
	}

	private int getYOffset() {
		if (WATO.largeNEI) {
			int guiHeight = Minecraft.getMinecraft().currentScreen.height;
			int ySize = Math.min(Math.max(guiHeight - 68, 166), 370);
			return (guiHeight - ySize) / 2 + 42;
		} else {
			return (GuiDraw.displaySize().height - HEIGHT) / 2 + 13;
		}
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
