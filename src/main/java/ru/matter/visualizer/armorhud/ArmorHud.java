package ru.matter.visualizer.armorhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ArmorHud.MODID, name = ArmorHud.NAME, version = ArmorHud.VERSION, clientSideOnly = true)
public class ArmorHud {
    public static final String MODID = "mattersarmorhud";
    public static final String NAME = "matter's armor hud";
    public static final String VERSION = "1.0";

    // vanilla widgets atlas (contains hotbar + offhand slot frames)
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    // The offhand slot frame is a 29x24 sprite in widgets.png:
    //   right-hand variant: u=53, v=22   (notch on the LEFT)
    //   left-hand  variant: u=24, v=22   (notch on the RIGHT)
    // Inside that frame vanilla insets the 16x16 item by:
    //   right variant -> (+10, +4)
    //   left  variant -> (+3,  +4)
    private static final int FRAME_W = 29;
    private static final int FRAME_H = 24;

    // ---- layout (tweak to taste) ----
    private static final boolean HORIZONTAL = true;   // true = row, false = column
    private static final boolean RIGHT_SIDE = true;   // true = right of hotbar, false = left
    private static final int SPACING = 22;            // distance between slots along the strip
    private static final int GAP = 1;                 // gap from the hotbar
    private static final int OFFSET_X = 0;            // fine-tune
    private static final int OFFSET_Y = 0;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || mc.gameSettings.hideGUI) {
            return;
        }

        // gather equipped pieces, helmet(3) first so it leads the strip
        ItemStack[] pieces = new ItemStack[4];
        int count = 0;
        for (int i = 3; i >= 0; i--) {
            ItemStack s = player.inventory.armorInventory.get(i);
            if (!s.isEmpty()) {
                pieces[count++] = s;
            }
        }
        if (count == 0) {
            return;
        }

        ScaledResolution res = new ScaledResolution(mc);
        int center = res.getScaledWidth() / 2;
        int screenH = res.getScaledHeight();

        int hotbarRight = center + 91;
        int hotbarLeft = center - 91;

        // frame UV + item inset depend on which side the notch should face
        int frameU = RIGHT_SIDE ? 53 : 24;
        int insetX = RIGHT_SIDE ? 10 : 3;
        int insetY = 4;

        // origin (top-left of the FIRST frame)
        int baseX;
        int baseY;
        if (HORIZONTAL) {
            baseY = screenH - 23 + OFFSET_Y;
            if (RIGHT_SIDE) {
                baseX = hotbarRight + GAP + OFFSET_X;
            } else {
                baseX = hotbarLeft - GAP - (FRAME_W + (count - 1) * SPACING) + OFFSET_X;
            }
        } else {
            // vertical column sitting on the side of the hotbar, growing upward
            int stripHeight = FRAME_H + (count - 1) * SPACING;
            baseY = screenH - stripHeight - 2 + OFFSET_Y;
            baseX = (RIGHT_SIDE ? hotbarRight + GAP : hotbarLeft - GAP - FRAME_W) + OFFSET_X;
        }

        // 1) draw all slot frames (plain 2D textured quads)
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(WIDGETS);
        for (int k = 0; k < count; k++) {
            int fx = HORIZONTAL ? baseX + k * SPACING : baseX;
            int fy = HORIZONTAL ? baseY : baseY + k * SPACING;
            Gui.drawModalRectWithCustomSizedTexture(fx, fy, (float) frameU, 22.0F, FRAME_W, FRAME_H, 256.0F, 256.0F);
        }

        // 2) draw all items + their durability/count overlays
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        for (int k = 0; k < count; k++) {
            int fx = HORIZONTAL ? baseX + k * SPACING : baseX;
            int fy = HORIZONTAL ? baseY : baseY + k * SPACING;
            int ix = fx + insetX;
            int iy = fy + insetY;
            ItemStack stack = pieces[k];
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, ix, iy);
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, ix, iy);
        }
        RenderHelper.disableStandardItemLighting();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
