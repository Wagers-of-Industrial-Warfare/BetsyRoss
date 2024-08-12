package rbasamoyai.betsyross.flags;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import immersive_paintings.Main;
import immersive_paintings.client.ClientUtils;
import immersive_paintings.client.gui.ImmersivePaintingScreen;
import immersive_paintings.client.gui.ImmersivePaintingScreen.Page;
import immersive_paintings.client.gui.widget.CallbackCheckboxWidget;
import immersive_paintings.client.gui.widget.DefaultButtonWidget;
import immersive_paintings.client.gui.widget.IntegerSliderWidget;
import immersive_paintings.client.gui.widget.PaintingWidget;
import immersive_paintings.client.gui.widget.PercentageSliderWidget;
import immersive_paintings.client.gui.widget.TooltipButtonWidget;
import immersive_paintings.cobalt.network.NetworkHandler;
import immersive_paintings.network.LazyNetworkManager;
import immersive_paintings.network.c2s.PaintingDeleteRequest;
import immersive_paintings.network.c2s.RegisterPaintingRequest;
import immersive_paintings.network.c2s.UploadPaintingRequest;
import immersive_paintings.network.s2c.RegisterPaintingResponse;
import immersive_paintings.resources.ByteImage;
import immersive_paintings.resources.ClientPaintingManager;
import immersive_paintings.resources.Painting;
import immersive_paintings.util.FlowingText;
import immersive_paintings.util.ImageManipulations;
import immersive_paintings.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.foundation.BetsyRossUtils;
import rbasamoyai.betsyross.mixin.client.PixelatorSettingsAccessor;

/**
 * Heavily adapted from {@link ImmersivePaintingScreen}
 */
public abstract class AbstractFlagScreen extends Screen implements BetsyRossFlagScreen {

    protected static final int SCREENSHOTS_PER_PAGE = 5;

    protected final int minResolution;
    protected final int maxResolution;
    protected final boolean showOtherPlayersPaintings;
    protected final int uploadPermissionLevel;

    protected String filteredString = "";
    protected int filteredResolution = 0;
    protected int filteredWidth = 0;
    protected int filteredHeight = 0;
    protected final List<ResourceLocation> filteredPaintings = new ArrayList<>();

    protected int selectionPage;
    protected Page page;

    protected Button pageWidget;

    protected final List<PaintingWidget> paintingWidgetList = new LinkedList<>();
    protected ByteImage currentImage;
    protected static int currentImagePixelZoomCache = -1;
    protected String currentImageName;
    protected ImmersivePaintingScreen.PixelatorSettings settings;
    protected ByteImage pixelatedImage;

    protected List<File> screenshots = List.of();
    protected int screenshotPage;

    protected ResourceLocation deletePainting;
    protected Component error;
    protected boolean shouldReProcess;
    protected static volatile boolean shouldUpload;

    final ExecutorService service = Executors.newFixedThreadPool(1);

    protected AbstractFlagScreen(int minResolution, int maxResolution, boolean showOtherPlayersPaintings, int uploadPermissionLevel) {
        super(Component.translatable("block.betsyross.flag_block"));
        this.minResolution = minResolution;
        this.maxResolution = maxResolution;
        this.showOtherPlayersPaintings = showOtherPlayersPaintings;
        this.uploadPermissionLevel = uploadPermissionLevel;
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        super.init();

        if (this.page == null) {
            this.setPage(Page.DATAPACKS);
        } else {
            this.refreshPage();
        }

        //reload screenshots
        File file = new File(Minecraft.getInstance().gameDirectory, "screenshots");
        File[] files = file.listFiles(v -> v.getName().endsWith(".png"));
        if (files != null)
            this.screenshots = Arrays.stream(files).toList();
    }

    protected void clearSearch() {
        this.filteredString = "";
        this.filteredResolution = 0;
        this.filteredWidth = 0;
        this.filteredHeight = 0;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        switch (this.page) {
            case NEW -> {
                context.fill(this.width / 2 - 115, this.height / 2 - 68, this.width / 2 + 115, this.height / 2 - 41, 0x50000000);
                List<Component> wrap = FlowingText.wrap(Component.translatable("immersive_paintings.drop"), 220);
                int y = this.height / 2 - 40 - wrap.size() * 12;
                for (Component text : wrap) {
                    context.drawCenteredString(this.font, text, this.width / 2, y, 0xFFFFFFFF);
                    y += 12;
                }
            }
            case CREATE -> {
                if (this.shouldReProcess && this.currentImage != null) {
                    Runnable task = () -> {
                        this.pixelatedImage = ImmersivePaintingScreen.pixelateImage(this.currentImage, this.settings);
                        shouldUpload = true;
                    };
                    this.service.submit(task);
                    this.shouldReProcess = false;
                }

                if (shouldUpload && this.pixelatedImage != null) {
                    Minecraft.getInstance().getTextureManager().register(Main.locate("temp_pixelated"),
                        new DynamicTexture(ClientUtils.byteImageToNativeImage(this.pixelatedImage)));
                }

                int maxWidth = 190;
                int maxHeight = 135;
                int tw = this.settings.resolution * this.settings.width;
                int th = this.settings.resolution * this.settings.height;
                float size = Math.min((float) maxWidth / tw, (float) maxHeight / th);
                PoseStack matrices = context.pose();
                matrices.pushPose();
                matrices.translate(this.width / 2.0f - tw * size / 2.0f, this.height / 2.0f - th * size / 2.0f, 0.0f);
                matrices.scale(size, size, 1.0f);
                context.blit(Main.locate("temp_pixelated"), 0, 0, 0, 0, tw, th, tw, th);
                matrices.popPose();

                if (this.error != null)
                    context.drawCenteredString(this.font, this.error, this.width / 2, this.height / 2, 0xFFFF0000);
            }
            case DELETE -> {
                context.fill(this.width / 2 - 160, this.height / 2 - 50, this.width / 2 + 160, this.height / 2 + 50, 0x88000000);
                List<Component> wrap = FlowingText.wrap(Component.translatable("immersive_paintings.confirm_deletion"), 300);
                int y = this.height / 2 - 35;
                for (Component t : wrap) {
                    context.drawCenteredString(this.font, t, this.width / 2, y, 0XFFFFFF);
                    y += 15;
                }
            }
            case ADMIN_DELETE -> {
                context.fill(this.width / 2 - 160, this.height / 2 - 50, this.width / 2 + 160, this.height / 2 + 50, 0x88000000);
                List<Component> wrap = FlowingText.wrap(Component.translatable("immersive_paintings.confirm_admin_deletion"), 300);
                int y = this.height / 2 - 35;
                for (Component t : wrap) {
                    context.drawCenteredString(this.font, t, this.width / 2, y, 0XFFFFFF);
                    y += 15;
                }
            }
            case LOADING -> {
                Component text = Component.translatable("immersive_paintings.upload", (int) Math.ceil(LazyNetworkManager.getRemainingTime()));
                context.drawCenteredString(this.font, text, this.width / 2, this.height / 2, 0xFFFFFFFF);
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    protected void rebuild() {
        this.clearWidgets();

        // filters
        if (this.page != Page.CREATE) {
            List<Page> b = new LinkedList<>();
            b.add(Page.YOURS);
            b.add(Page.DATAPACKS);
            if (this.showOtherPlayersPaintings || this.isOp())
                b.add(Page.PLAYERS);
            if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.hasPermissions(this.uploadPermissionLevel))
                b.add(Page.NEW);

            int x = this.width / 2 - 200;
            int w = 400 / b.size();
            for (Page page : b) {
                this.addRenderableWidget(new DefaultButtonWidget(x, height / 2 - 90 - 22, w, 20,
                    Component.translatable("immersive_paintings.page." + page.name().toLowerCase(Locale.ROOT)),
                    sender -> this.setPage(page))).active = page != this.page;
                x += w;
            }
        }
        if (this.page == Page.FRAME)
            this.page = Page.DATAPACKS;

        switch (this.page) {
            case NEW -> {
                //URL
                EditBox editBox = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 90,
                    this.height / 2 - 38, 180, 16, Component.literal("URL")));
                editBox.setMaxLength(1024);

                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 50, this.height / 2 - 15, 100, 20,
                    Component.translatable("immersive_paintings.load"), sender -> this.loadImage(editBox.getValue())));

                //screenshots
                rebuildScreenshots();

                //screenshot page
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 65, this.height / 2 + 70, 30, 20,
                    Component.literal("<<"), sender -> this.setScreenshotPage(this.screenshotPage - 1)));
                this.pageWidget = this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 65 + 30, this.height / 2 + 70, 70, 20,
                    Component.literal(""), sender -> {}));
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 65 + 100, this.height / 2 + 70, 30, 20,
                    Component.literal(">>"), sender -> this.setScreenshotPage(this.screenshotPage + 1)));
                setScreenshotPage(this.screenshotPage);
            }
            case CREATE -> {
                // Name
                EditBox editBox = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 90, this.height / 2 - 100, 180, 20,
                    Component.translatable("immersive_paintings.name")));
                editBox.setMaxLength(256);
                editBox.setValue(this.currentImageName);
                editBox.setResponder(s -> this.currentImageName = s);

                int y = this.height / 2 - 60;

                // Width
                this.addRenderableWidget(new IntegerSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.width", this.settings.width, 1, 16, v -> {
                    this.settings.width = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Height
                this.addRenderableWidget(new IntegerSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.height", this.settings.height, 1, 16, v -> {
                    this.settings.height = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Resolution
                int x = this.width / 2 - 200;

                TooltipButtonWidget widget = this.addRenderableWidget(new TooltipButtonWidget(x + 25, y, 50, 20,
                    Component.literal(String.valueOf(this.settings.resolution)),
                    Component.translatable("immersive_paintings.tooltip.resolution"), v -> {}));

                this.addRenderableWidget(new TooltipButtonWidget(x, y, 25, 20,
                    Component.literal("<"),
                    Component.translatable("immersive_paintings.tooltip.resolution"),
                    v -> {
                        this.settings.resolution = Math.max(this.minResolution, this.settings.resolution / 2);
                        if (this.settings.pixelArt) {
                            this.adaptToPixelArt();
                            this.refreshPage();
                        }
                        this.shouldReProcess = true;
                        widget.setMessage(Component.literal(String.valueOf(this.settings.resolution)));
                    }));

                this.addRenderableWidget(new TooltipButtonWidget(x + 75, y, 25, 20,
                    Component.literal(">"),
                    Component.translatable("immersive_paintings.tooltip.resolution"),
                    v -> {
                        this.settings.resolution = Math.min(this.maxResolution, this.settings.resolution * 2);
                        if (this.settings.pixelArt) {
                            this.adaptToPixelArt();
                            this.refreshPage();
                        }
                        this.shouldReProcess = true;
                        widget.setMessage(Component.literal(String.valueOf(this.settings.resolution)));
                    }));
                y += 22;
                y += 10;

                // Color reduction
                this.addRenderableWidget(new IntegerSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.colors", this.settings.colors, 1, 25, v -> {
                    this.settings.colors = v;
                    this.shouldReProcess = true;
                })).active = !this.settings.pixelArt;
                y += 22;

                // Dither
                this.addRenderableWidget(new PercentageSliderWidget(width / 2 - 200, y, 100, 20, "immersive_paintings.dither", this.settings.dither, v -> {
                    this.settings.dither = v;
                    this.shouldReProcess = true;
                })).active = !this.settings.pixelArt;

                // PixelArt
                y = this.height / 2 - 50;
                this.addRenderableWidget(new CallbackCheckboxWidget(width / 2 + 100, y, 20, 20,
                    Component.translatable("immersive_paintings.pixelart"),
                    Component.translatable("immersive_paintings.pixelart.tooltip"),
                    this.settings.pixelArt, true, b -> {
                    this.settings.pixelArt = b;
                    this.adaptToPixelArt();
                    this.refreshPage();
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Hide
                this.addRenderableWidget(new CallbackCheckboxWidget(this.width / 2 + 100, y, 100, 20,
                    Component.translatable("immersive_paintings.hide"),
                    Component.translatable("immersive_paintings.visibility"),
                    this.settings.hidden, true,
                    v -> this.settings.hidden = !this.settings.hidden));
                y += 22;

                // Offset X
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 + 100, y, 100, 20, "immersive_paintings.x_offset", this.settings.offsetX, v -> {
                    this.settings.offsetX = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Offset Y
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 + 100, y, 100, 20, "immersive_paintings.y_offset", this.settings.offsetY, v -> {
                    this.settings.offsetY = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Offset
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 + 100, y, 100, 20, "immersive_paintings.zoom", this.settings.zoom, 1.0, 3.0, v -> {
                    this.settings.zoom = v;
                    this.shouldReProcess = true;
                })).active = !this.settings.pixelArt;

                // Cancel
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 85, this.height / 2 + 75, 80, 20,
                    Component.translatable("immersive_paintings.cancel"), v -> this.setPage(Page.NEW)));

                // Save
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 + 5, this.height / 2 + 75, 80, 20, Component.translatable("immersive_paintings.save"),
                    v -> {
                        int maxWidth = this.getConfigWidth();
                        int maxHeight = this.getConfigHeight();
                        if (maxWidth != 0 && this.settings.width > maxWidth) {
                            this.setError(Component.translatable("gui.betsyross.flag_maker.too_wide", Math.max(0, maxWidth)));
                            return;
                        }
                        if (maxHeight != 0 && this.settings.height > maxHeight) {
                            this.setError(Component.translatable("gui.betsyross.flag_maker.too_tall", Math.max(0, maxHeight)));
                            return;
                        }
                        Utils.processByteArrayInChunks(this.pixelatedImage.encode(),
                            (ints, split, splits) -> LazyNetworkManager.sendToServer(new UploadPaintingRequest(ints, split, splits)));

                        LazyNetworkManager.sendToServer(new RegisterPaintingRequest(this.currentImageName, new Painting(
                            this.pixelatedImage,
                            this.settings.width,
                            this.settings.height,
                            this.settings.resolution,
                            this.settings.hidden,
                            false
                        )));

                        this.setPage(Page.LOADING);
                    }));
            }
            case YOURS, DATAPACKS, PLAYERS -> {
                this.rebuildPaintings();

                // page
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 35 - 30, this.height / 2 + 80, 30, 20,
                    Component.literal("<<"), sender -> setSelectionPage(this.selectionPage - 1)));
                this.pageWidget = this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 35, this.height / 2 + 80, 70, 20,
                    Component.literal(""), sender -> {}));
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 + 35, this.height / 2 + 80, 30, 20,
                    Component.literal(">>"), sender -> setSelectionPage(this.selectionPage + 1)));
                setSelectionPage(this.selectionPage);

                //search
                EditBox searchBox = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 65, this.height / 2 - 88, 130, 16,
                    Component.translatable("immersive_paintings.search")));
                searchBox.setMaxLength(64);
                searchBox.setSuggestion("search");
                searchBox.setResponder(s -> {
                    this.filteredString = s;
                    this.updateSearch();
                    searchBox.setSuggestion(null);
                });

                int x = this.width / 2 - 200 + 12;

                Button widget = this.addRenderableWidget(new TooltipButtonWidget(x + 50 + 8, this.height / 2 - 90, 25, 20,
                    Component.literal(String.valueOf(this.filteredResolution)),
                    Component.translatable("immersive_paintings.tooltip.filter_resolution"),
                    v -> {}));

                TooltipButtonWidget allWidget = this.addRenderableWidget(new TooltipButtonWidget(x, this.height / 2 - 90, 25, 20,
                    Component.translatable("immersive_paintings.filter.all"),
                    Component.translatable("immersive_paintings.tooltip.filter_resolution"),
                    v -> {
                        this.filteredResolution = 0;
                        this.updateSearch();
                        widget.setMessage(Component.literal(String.valueOf(this.filteredResolution)));
                        v.active = false;
                    }));

                this.addRenderableWidget(new TooltipButtonWidget(x + 25 + 8, this.height / 2 - 90, 25, 20,
                    Component.literal("<"),
                    Component.translatable("immersive_paintings.tooltip.filter_resolution"),
                    v -> {
                        this.filteredResolution = this.filteredResolution == 0 ? 32 : Math.max(this.minResolution, this.filteredResolution / 2);
                        this.updateSearch();
                        widget.setMessage(Component.literal(String.valueOf(this.filteredResolution)));
                        allWidget.active = true;
                    }));

                this.addRenderableWidget(new TooltipButtonWidget(x + 75 + 8, this.height / 2 - 90, 25, 20,
                    Component.literal(">"),
                    Component.translatable("immersive_paintings.tooltip.filter_resolution"),
                    v -> {
                        this.filteredResolution = this.filteredResolution == 0 ? 32 : Math.min(this.maxResolution, this.filteredResolution * 2);
                        this.updateSearch();
                        widget.setMessage(Component.literal(String.valueOf(this.filteredResolution)));
                        allWidget.active = true;
                    }));

                //width
                EditBox widthInput = this.addRenderableWidget(new EditBox(this.font, this.width / 2 + 80, this.height / 2 - 88, 40, 16,
                    Component.translatable("immersive_paintings.filter_width")));
                widthInput.setMaxLength(2);
                widthInput.setSuggestion("width");
                widthInput.setResponder(s -> {
                    try {
                        this.filteredWidth = Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        this.filteredWidth = 0;
                    }
                    this.updateSearch();
                    widthInput.setSuggestion(null);
                });

                //height
                EditBox heightInput = this.addRenderableWidget(new EditBox(this.font, this.width / 2 + 80 + 40, this.height / 2 - 88, 40, 16,
                    Component.translatable("immersive_paintings.filter_height")));
                heightInput.setMaxLength(2);
                heightInput.setSuggestion("height");
                heightInput.setResponder(s -> {
                    try {
                        this.filteredHeight = Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        this.filteredHeight = 0;
                    }
                    this.updateSearch();
                    heightInput.setSuggestion(null);
                });
            }
            case DELETE -> {
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 100 - 5, this.height / 2 + 20, 100, 20,
                    Component.translatable("immersive_paintings.cancel"), v -> this.setPage(Page.YOURS)));

                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 + 5, this.height / 2 + 20, 100, 20, Component.translatable("immersive_paintings.delete"), v -> {
                    NetworkHandler.sendToServer(new PaintingDeleteRequest(this.deletePainting));
                    this.setPage(Page.YOURS);
                }));
            }
            case ADMIN_DELETE -> {
                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 115, this.height / 2 + 10, 70, 20,
                    Component.translatable("immersive_paintings.cancel"), v -> this.setPage(Page.PLAYERS)));

                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 - 40, this.height / 2 + 10, 70, 20, Component.translatable("immersive_paintings.delete"), v -> {
                    NetworkHandler.sendToServer(new PaintingDeleteRequest(deletePainting));
                    this.setPage(Page.PLAYERS);
                }));

                this.addRenderableWidget(new DefaultButtonWidget(this.width / 2 + 35, this.height / 2 + 10, 70, 20, Component.translatable("immersive_paintings.delete_all"), v -> {
                    String author = ClientPaintingManager.getPainting(this.deletePainting).author;
                    ClientPaintingManager.getPaintings().entrySet().stream()
                        .filter(p -> Objects.equals(p.getValue().author, author) && !p.getValue().datapack)
                        .map(Map.Entry::getKey)
                        .forEach(p -> NetworkHandler.sendToServer(new PaintingDeleteRequest(p)));
                    this.setPage(Page.PLAYERS);
                }));
            }
        }
    }

    protected void rebuildPaintings() {
        for (PaintingWidget w : this.paintingWidgetList)
            this.removeWidget(w);
        this.paintingWidgetList.clear();

        // paintings
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 8; x++) {
                int i = y * 8 + x + this.selectionPage * 24;
                if (i >= 0 && i < this.filteredPaintings.size()) {
                    ResourceLocation paintingLoc = this.filteredPaintings.get(i);
                    Painting painting = ClientPaintingManager.getPainting(paintingLoc);

                    //tooltip
                    List<Component> tooltip = new LinkedList<>();
                    tooltip.add(Component.literal(painting.name));
                    tooltip.add(Component.translatable("immersive_paintings.by_author", painting.author).withStyle(ChatFormatting.ITALIC));
                    tooltip.add(Component.translatable("immersive_paintings.resolution", painting.width, painting.height, painting.resolution)
                        .withStyle(ChatFormatting.ITALIC));

                    if (this.page == Page.YOURS && painting.hidden) {
                        tooltip.add(Component.translatable("immersive_paintings.hidden").withStyle(ChatFormatting.ITALIC)
                            .withStyle(ChatFormatting.GRAY));
                    }

                    if (this.page == Page.YOURS || page == Page.PLAYERS && this.isOp()) {
                        tooltip.add(Component.translatable("immersive_paintings.right_click_to_delete")
                            .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                    }

                    this.paintingWidgetList.add(this.addRenderableWidget(new PaintingWidget(ClientPaintingManager.getPaintingTexture(paintingLoc, Painting.Type.THUMBNAIL),
                        (int) (width / 2 + (x - 3.5) * 48) - 24, height / 2 - 66 + y * 48, 46, 46,
                        sender -> {
                            if (this.canUpdateFlag())
                                this.updateFlag(paintingLoc);
                            this.onClose();
                        },
                        b -> {
                            if (this.page == Page.YOURS) {
                                this.deletePainting = paintingLoc;
                                this.setPage(Page.DELETE);
                            } else if (this.page == Page.PLAYERS && this.isOp()) {
                                this.deletePainting = paintingLoc;
                                this.setPage(Page.ADMIN_DELETE);
                            }
                        },
                        () -> tooltip.stream().map(Component::getVisualOrderText).toList())));
                } else {
                    break;
                }
            }
        }
    }

    protected void rebuildScreenshots() {
        for (PaintingWidget w : this.paintingWidgetList)
            this.removeWidget(w);
        this.paintingWidgetList.clear();

        // screenshots
        for (int x = 0; x < SCREENSHOTS_PER_PAGE; x++) {
            int i = x + this.screenshotPage * SCREENSHOTS_PER_PAGE;
            if (i >= 0 && i < this.screenshots.size()) {
                File file = this.screenshots.get(i);
                Painting painting = new Painting(null, 16, 16, 16, false, true);
                this.paintingWidgetList.add(this.addRenderableWidget(new PaintingWidget(painting.thumbnail,
                    (this.width / 2 + (x - SCREENSHOTS_PER_PAGE / 2) * 68) - 32, this.height / 2 + 15, 64, 48,
                    b -> {
                        this.currentImage = ((PaintingWidget) b).thumbnail.image;
                        if (this.currentImage != null) {
                            currentImagePixelZoomCache = -1;
                            this.currentImageName = file.getName();
                            this.settings = PixelatorSettingsAccessor.callInit(this.currentImage);
                            this.setPage(Page.CREATE);
                            this.pixelateImage();
                        }
                    },
                    b -> {},
                    () -> Tooltip.splitTooltip(Minecraft.getInstance(), Component.literal(file.getName())))));

                ResourceLocation loc = Main.locate("screenshot_" + x);
                Runnable task = () -> {
                    ByteImage image = this.loadImage(file.getPath(), loc);
                    if (image != null) {
                        painting.width = image.getWidth();
                        painting.height = image.getHeight();
                        painting.thumbnail.image = image;
                        painting.thumbnail.textureIdentifier = loc;
                    }
                };
                this.service.submit(task);
            } else {
                break;
            }
        }
    }

    public void setPage(Page page) {
        this.clearError();
        if (page != this.page)
            this.clearSearch();

        this.page = page;
        this.filteredResolution = page == Page.DATAPACKS ? 32 : 0;

        this.rebuild();

        if (page == Page.DATAPACKS || page == Page.PLAYERS || page == Page.YOURS)
            this.updateSearch();
    }

    protected void updateSearch() {
        this.filteredPaintings.clear();

        int maxWidth = this.getConfigWidth();
        int maxHeight = this.getConfigHeight();

        String playerName = this.getPlayerName();
        this.filteredPaintings.addAll(ClientPaintingManager.getPaintings().entrySet().stream()
            .filter(v -> this.page != Page.YOURS || Objects.equals(v.getValue().author, playerName) && !v.getValue().datapack)
            .filter(v -> this.page != Page.PLAYERS || !v.getValue().datapack && !v.getValue().hidden)
            .filter(v -> this.page != Page.DATAPACKS || v.getValue().datapack)
            .filter(v -> v.getKey().toString().contains(this.filteredString))
            .filter(v -> this.filteredResolution == 0 || v.getValue().resolution == this.filteredResolution)
            .filter(v -> this.filteredWidth == 0 || v.getValue().width == this.filteredWidth)
            .filter(v -> this.filteredHeight == 0 || v.getValue().height == this.filteredHeight)
            .filter(v -> maxWidth == 0 || v.getValue().width <= maxWidth)
            .filter(v -> maxHeight == 0 || v.getValue().height <= maxHeight)
            .map(Map.Entry::getKey)
            .toList());

        this.setSelectionPage(this.selectionPage);
    }

    protected abstract int getConfigWidth();
    protected abstract int getConfigHeight();

    protected String getPlayerName() {
        return Minecraft.getInstance().player == null ? "" : Minecraft.getInstance().player.getGameProfile().getName();
    }

    protected boolean isOp() {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasPermissions(4);
    }

    protected void setSelectionPage(int p) {
        this.selectionPage = Math.min(this.getMaxPages() - 1, Math.max(0, p));
        this.rebuildPaintings();
        this.pageWidget.setMessage(Component.literal((this.selectionPage + 1) + " / " + this.getMaxPages()));
    }

    protected int getMaxPages() { return (int) Math.ceil(this.filteredPaintings.size() / 24.0); }

    protected void setScreenshotPage(int p) {
        int oldPage = this.screenshotPage;
        this.screenshotPage = Math.min(this.getScreenshotMaxPages() - 1, Math.max(0, p));
        if (oldPage != this.screenshotPage)
            this.rebuildScreenshots();
        this.pageWidget.setMessage(Component.literal((screenshotPage + 1) + " / " + this.getScreenshotMaxPages()));
    }

    protected int getScreenshotMaxPages() { return (int) Math.ceil(this.screenshots.size() / 8.0); }

    @Override
    public void onFilesDrop(List<Path> paths) {
        Path path = paths.get(0);
        this.loadImage(path.toString());
    }

    protected void loadImage(String path) {
        this.currentImage = this.loadImage(path, Main.locate("temp"));
        currentImagePixelZoomCache = -1;
        if (this.currentImage != null) {
            this.currentImageName = FilenameUtils.getBaseName(path).replaceFirst("[.][^.]+$", "");
            this.settings = PixelatorSettingsAccessor.callInit(this.currentImage);
            this.setPage(Page.CREATE);
            this.pixelateImage();
        }
    }

    protected ByteImage loadImage(String path, ResourceLocation loc) {
        InputStream stream = null;
        try {
            stream = new URL(path).openStream();
        } catch (Exception exception) {
            try {
                stream = new FileInputStream(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (stream != null) {
            try {
                ByteImage nativeImage = ByteImage.read(stream);
                Minecraft.getInstance().getTextureManager().register(loc, new DynamicTexture(ClientUtils.byteImageToNativeImage(nativeImage)));
                stream.close();
                return nativeImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected static int getCurrentImagePixelZoomCache(ByteImage currentImage) {
        if (currentImagePixelZoomCache < 0)
            currentImagePixelZoomCache = ImageManipulations.scanForPixelArtMultiple(currentImage);
        return currentImagePixelZoomCache;
    }

    protected void pixelateImage() {
        this.pixelatedImage = ImmersivePaintingScreen.pixelateImage(this.currentImage, this.settings);
        shouldUpload = true;
    }

    protected void adaptToPixelArt() {
        double zoom = getCurrentImagePixelZoomCache(this.currentImage);
        this.settings.width = Math.max(1, Math.min(16, (int) (this.currentImage.getWidth() / zoom / this.settings.resolution)));
        this.settings.height = Math.max(1, Math.min(16, (int) (this.currentImage.getHeight() / zoom / this.settings.resolution)));
    }

    @Override
    public void onReceivePaintingResponse(RegisterPaintingResponse response) {
        if (response.error.isEmpty()) {
            if (this.canUpdateFlag())
                this.updateFlag(BetsyRossUtils.location(response.identifier));
            this.onClose();
        } else {
            this.setPage(ImmersivePaintingScreen.Page.CREATE);
            this.setError(Component.translatable("immersive_paintings.error." + response.error));
        }
    }

    protected abstract void updateFlag(ResourceLocation loc);
    protected abstract boolean canUpdateFlag();

    @Override public void refreshPage() { this.setPage(this.page); }

    public void setError(Component error) { this.error = error; }
    protected void clearError() { this.error = null; }

}
