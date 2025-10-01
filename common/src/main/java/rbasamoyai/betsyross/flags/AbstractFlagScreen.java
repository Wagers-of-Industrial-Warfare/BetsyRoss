package rbasamoyai.betsyross.flags;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.conczin.immersive_paintings.ClientPaintingManager;
import net.conczin.immersive_paintings.Main;
import net.conczin.immersive_paintings.Painting;
import net.conczin.immersive_paintings.client.gui.ImmersivePaintingScreen;
import net.conczin.immersive_paintings.client.gui.ImmersivePaintingScreen.Page;
import net.conczin.immersive_paintings.client.gui.widget.IntegerSliderWidget;
import net.conczin.immersive_paintings.client.gui.widget.PaintingWidget;
import net.conczin.immersive_paintings.client.gui.widget.PercentageSliderWidget;
import net.conczin.immersive_paintings.network.LazyNetworkManager;
import net.conczin.immersive_paintings.network.NetworkHandler;
import net.conczin.immersive_paintings.network.payload.c2s.ImageUploadPayload;
import net.conczin.immersive_paintings.network.payload.c2s.PaintingDeletePayload;
import net.conczin.immersive_paintings.network.payload.c2s.PaintingRegisterPayload;
import net.conczin.immersive_paintings.network.payload.s2c.PaintingRegisterErrorPayload;
import net.conczin.immersive_paintings.registration.Configs;
import net.conczin.immersive_paintings.util.ImageManipulations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.BetsyRoss;
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

    protected final Map<ResourceLocation, PaintingWidget> paintingWidgets = new HashMap<>();
    protected BufferedImage currentImage;
    protected static int currentImagePixelZoomCache = -1;
    protected String currentImageName;
    protected ImmersivePaintingScreen.PixelatorSettings settings;
    protected BufferedImage pixelatedImage;

    protected List<File> screenshots = List.of();
    protected int screenshotPage;

    protected ResourceLocation deletePainting;
    protected Component error;
    protected boolean shouldReProcess;
    protected static volatile boolean shouldUpload;

    private final static ExecutorService service = Executors.newFixedThreadPool(1);

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
        super.render(context, mouseX, mouseY, delta);

        switch (this.page) {
            case NEW -> {
                context.fill(this.width / 2 - 115, this.height / 2 - 68, this.width / 2 + 115, this.height / 2 - 41, 0x50000000);
                List<Component> wrap = ImmersivePaintingScreen.wrap(Component.translatable("immersive_paintings.gui.drop"), 220);
                int y = this.height / 2 - 40 - wrap.size() * 12;
                for (Component text : wrap) {
                    context.drawCenteredString(this.font, text, this.width / 2, y, 0xFFFFFFFF);
                    y += 12;
                }
            }
            case CREATE -> {
                if (this.shouldReProcess && this.currentImage != null) {
                    service.submit(this::pixellateImage);
                    this.shouldReProcess = false;
                }

                if (shouldUpload && this.pixelatedImage != null) {
                    Minecraft.getInstance().getTextureManager().register(Main.locate("temp_pixelated"),
                        new DynamicTexture(ImageManipulations.bufferedToNative(this.pixelatedImage)));
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
                List<Component> wrap = ImmersivePaintingScreen.wrap(Component.translatable("immersive_paintings.gui.confirm_deletion"), 300);
                int y = this.height / 2 - 35;
                for (Component t : wrap) {
                    context.drawCenteredString(this.font, t, this.width / 2, y, 0XFFFFFF);
                    y += 15;
                }
            }
            case ADMIN_DELETE -> {
                context.fill(this.width / 2 - 160, this.height / 2 - 50, this.width / 2 + 160, this.height / 2 + 50, 0x88000000);
                List<Component> wrap = ImmersivePaintingScreen.wrap(Component.translatable("immersive_paintings.gui.confirm_admin_deletion"), 300);
                int y = this.height / 2 - 35;
                for (Component t : wrap) {
                    context.drawCenteredString(this.font, t, this.width / 2, y, 0XFFFFFF);
                    y += 15;
                }
            }
            case LOADING -> {
                Component text = Component.translatable("immersive_paintings.gui.upload", (int) Math.ceil(LazyNetworkManager.getRemainingTime()));
                context.drawCenteredString(this.font, text, this.width / 2, this.height / 2, 0xFFFFFFFF);
            }
        }
    }

    private void pixellateImage() {
        this.pixelatedImage = ImmersivePaintingScreen.pixelateImage(this.currentImage, this.settings);
        shouldUpload = true;
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
                Button btn = this.addRenderableWidget(Button.builder(
                    Component.translatable("immersive_paintings.gui.page." + page.name().toLowerCase(Locale.ROOT)), sender -> this.setPage(page))
                    .bounds(x, height / 2 - 90 - 22, w, 20)
                    .build());
                btn.active = page != this.page;
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

                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.load"), sender -> this.loadImage(editBox.getValue()))
                    .bounds(this.width / 2 - 50, this.height / 2 - 15, 100, 20)
                    .build());

                //screenshots
                this.rebuildScreenshots();

                //screenshot page
                this.addRenderableWidget(Button.builder(Component.literal("<<"), sender -> this.setScreenshotPage(this.screenshotPage - 1))
                    .bounds(this.width / 2 - 65, this.height / 2 + 70, 30, 20)
                    .build());
                this.pageWidget = this.addRenderableWidget(Button.builder(Component.literal(""), sender -> {})
                    .bounds(this.width / 2 - 65 + 30, this.height / 2 + 70, 70, 20)
                    .build());
                this.addRenderableWidget(Button.builder(Component.literal(">>"), sender -> this.setScreenshotPage(this.screenshotPage + 1))
                    .bounds(this.width / 2 - 65 + 100, this.height / 2 + 70, 30, 20)
                    .build());
                this.setScreenshotPage(this.screenshotPage);
            }
            case CREATE -> {
                // Name
                EditBox editBox = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 90, this.height / 2 - 100, 180, 20,
                    Component.translatable("immersive_paintings.gui.name")));
                editBox.setMaxLength(256);
                editBox.setValue(this.currentImageName);
                editBox.setResponder(s -> this.currentImageName = s);

                int y = this.height / 2 - 60;

                // Width
                this.addRenderableWidget(new IntegerSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.gui.width", this.settings.width, 1, 16, v -> {
                    this.settings.width = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Height
                this.addRenderableWidget(new IntegerSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.gui.height", this.settings.height, 1, 16, v -> {
                    this.settings.height = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Resolution
                int x = this.width / 2 - 200;

                Button resolutionWidget = this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(this.settings.resolution)), v -> {})
                    .bounds(x + 25, y, 50, 20)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.resolution")))
                    .build());

                this.addRenderableWidget(Button.builder(Component.literal("<"), v -> {
                        this.settings.resolution = Math.max(this.minResolution, this.settings.resolution / 2);
                        if (this.settings.pixelArt) {
                            this.adaptToPixelArt();
                            this.refreshPage();
                        }
                        this.shouldReProcess = true;
                        resolutionWidget.setMessage(Component.literal(String.valueOf(this.settings.resolution)));
                    })
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.resolution")))
                    .bounds(x, y, 25, 20)
                    .build());

                this.addRenderableWidget(Button.builder(Component.literal(">"), v -> {
                        this.settings.resolution = Math.min(this.maxResolution, this.settings.resolution * 2);
                        if (this.settings.pixelArt) {
                            this.adaptToPixelArt();
                            this.refreshPage();
                        }
                        this.shouldReProcess = true;
                        resolutionWidget.setMessage(Component.literal(String.valueOf(this.settings.resolution)));
                    })
                    .bounds(x + 75, y, 25, 20)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.resolution")))
                    .build());
                y += 22;
                y += 10;

                // Color reduction
                this.addRenderableWidget(new IntegerSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.gui.colors", this.settings.colors, 1, 25, v -> {
                    this.settings.colors = v;
                    this.shouldReProcess = true;
                })).active = !this.settings.pixelArt;
                y += 22;

                // Dither
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 - 200, y, 100, 20, "immersive_paintings.gui.dither", this.settings.dither, v -> {
                    this.settings.dither = v;
                    this.shouldReProcess = true;
                })).active = !this.settings.pixelArt;

                // PixelArt
                y = this.height / 2 - 50;
                this.addRenderableWidget(Checkbox.builder(Component.translatable("immersive_paintings.gui.pixelart"), this.font)
                    .pos(this.width / 2 + 100, y)
                    .selected(this.settings.pixelArt)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.pixelart.tooltip")))
                    .onValueChange((widget, value) -> {
                        this.settings.pixelArt = value;
                        this.adaptToPixelArt();
                        this.refreshPage();
                        this.shouldReProcess = true;
                    })
                    .build());
                y += 22;

                // Hide
                this.addRenderableWidget(Checkbox.builder(Component.translatable("immersive_paintings.gui.hide"), this.font)
                    .pos(this.width / 2 + 100, y)
                    .selected(this.settings.hidden)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.visibility")))
                    .onValueChange((widget, value) -> this.settings.hidden = !this.settings.hidden)
                    .build());
                y += 22;

                // NSFW
                this.addRenderableWidget(Checkbox.builder(Component.translatable("immersive_paintings.gui.nsfw"), this.font)
                    .pos(this.width / 2 + 100, y)
                    .selected(this.settings.nsfw)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.nsfw")))
                    .onValueChange((widget, value) -> this.settings.nsfw = !this.settings.nsfw)
                    .build());
                y += 22;

                // Offset X
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 + 100, y, 100, 20, "immersive_paintings.gui.x_offset", this.settings.offsetX, v -> {
                    this.settings.offsetX = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Offset Y
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 + 100, y, 100, 20, "immersive_paintings.gui.y_offset", this.settings.offsetY, v -> {
                    this.settings.offsetY = v;
                    this.shouldReProcess = true;
                }));
                y += 22;

                // Offset
                this.addRenderableWidget(new PercentageSliderWidget(this.width / 2 + 100, y, 100, 20, "immersive_paintings.gui.zoom", this.settings.zoom, 1.0, 3.0, v -> {
                    this.settings.zoom = v;
                    this.shouldReProcess = true;
                })).active = !this.settings.pixelArt;

                // Cancel
                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.cancel"), v -> this.setPage(Page.NEW))
                    .bounds(this.width / 2 - 85, this.height / 2 + 75, 80, 20)
                    .build());

                // Save
                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.save"), v -> {
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

                    byte[] encoded;

                    try {
                        encoded = ImageManipulations.encode(this.pixelatedImage);
                    } catch (IOException e) {
                        BetsyRoss.LOGGER.error("could not encode temp image", e);
                        return;
                    }

                    ImageManipulations.processByteArrayInChunks(encoded, (ints, split, splits) -> LazyNetworkManager.sendToServer(
                        new ImageUploadPayload(ints, split, splits)));

                    // Using LazyNetworkManager here guarantees the register request won't arrive before the image is uploaded
                    LazyNetworkManager.sendToServer(new PaintingRegisterPayload( this.settings.width, this.settings.height,
                        this.settings.resolution, this.currentImageName, this.settings.getFlags()));

                    this.setPage(Page.LOADING);
                }).bounds(this.width / 2 + 5, this.height / 2 + 75, 80, 20).build());
            }
            case YOURS, DATAPACKS, PLAYERS -> {
                this.rebuildPaintings();

                // page
                this.addRenderableWidget(Button.builder(Component.literal("<<"), sender -> setSelectionPage(this.selectionPage - 1))
                    .bounds(this.width / 2 - 35 - 30, this.height / 2 + 80, 30, 20)
                    .build());
                this.pageWidget = this.addRenderableWidget(Button.builder(Component.literal(""), sender -> {})
                    .bounds(this.width / 2 - 35, this.height / 2 + 80, 70, 20)
                    .build());
                this.addRenderableWidget(Button.builder(Component.literal(">>"), sender -> setSelectionPage(this.selectionPage + 1))
                    .bounds(this.width / 2 + 35, this.height / 2 + 80, 30, 20)
                    .build());
                setSelectionPage(this.selectionPage);

                //search
                EditBox searchBox = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 65, this.height / 2 - 88, 130, 16,
                    Component.translatable("immersive_paintings.gui.search")));
                searchBox.setMaxLength(64);
                searchBox.setSuggestion("search");
                searchBox.setResponder(s -> {
                    this.filteredString = s;
                    this.updateSearch();
                    searchBox.setSuggestion(null);
                });

                int x = this.width / 2 - 200 + 12;

                Button widget = this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(this.filteredResolution)), v -> {})
                    .bounds(x + 50 + 8, this.height / 2 - 90, 25, 20)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.filter_resolution")))
                    .build());

                Button allWidget = this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.filter_all"), v -> {
                        this.filteredResolution = 0;
                        this.updateSearch();
                        widget.setMessage(Component.literal(String.valueOf(this.filteredResolution)));
                        v.active = false;
                    })
                    .bounds(x, this.height / 2 - 90, 25, 20)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.filter_resolution")))
                    .build());

                this.addRenderableWidget(Button.builder(Component.literal("<"), v -> {
                        this.filteredResolution = this.filteredResolution == 0 ? 32 : Math.max(this.minResolution, this.filteredResolution / 2);
                        this.updateSearch();
                        widget.setMessage(Component.literal(String.valueOf(this.filteredResolution)));
                        allWidget.active = true;
                    })
                    .bounds(x + 25 + 8, this.height / 2 - 90, 25, 20)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.filter_resolution")))
                    .build());

                this.addRenderableWidget(Button.builder(Component.literal(">"), v -> {
                        this.filteredResolution = this.filteredResolution == 0 ? 32 : Math.min(this.maxResolution, this.filteredResolution * 2);
                        this.updateSearch();
                        widget.setMessage(Component.literal(String.valueOf(this.filteredResolution)));
                        allWidget.active = true;
                    })
                    .bounds(x + 75 + 8, this.height / 2 - 90, 25, 20)
                    .tooltip(Tooltip.create(Component.translatable("immersive_paintings.gui.tooltip.filter_resolution")))
                    .build());

                //width
                EditBox widthInput = this.addRenderableWidget(new EditBox(this.font, this.width / 2 + 80, this.height / 2 - 88, 40, 16,
                    Component.translatable("immersive_paintings.gui.filter_width")));
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
                    Component.translatable("immersive_paintings.gui.filter_height")));
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
                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.cancel"), v -> this.setPage(Page.YOURS))
                    .bounds(this.width / 2 - 100 - 5, this.height / 2 + 20, 100, 20)
                    .build());

                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.delete"), v -> {
                        NetworkHandler.Client.sendToServer(new PaintingDeletePayload(this.deletePainting, false));
                        this.setPage(Page.YOURS);
                    })
                    .bounds(this.width / 2 + 5, this.height / 2 + 20, 100, 20)
                    .build());
            }
            case ADMIN_DELETE -> {
                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.cancel"), v -> this.setPage(Page.PLAYERS))
                    .bounds(this.width / 2 - 115, this.height / 2 + 10, 70, 20)
                    .build());

                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.delete"), v -> {
                        NetworkHandler.Client.sendToServer(new PaintingDeletePayload(this.deletePainting, false));
                        this.setPage(Page.PLAYERS);
                    })
                    .bounds(this.width / 2 - 40, this.height / 2 + 10, 70, 20)
                    .build());

                this.addRenderableWidget(Button.builder(Component.translatable("immersive_paintings.gui.delete_all"), v -> {
                        NetworkHandler.Client.sendToServer(new PaintingDeletePayload(this.deletePainting, true));
                        this.setPage(Page.PLAYERS);
                    })
                    .bounds(this.width / 2 + 35, this.height / 2 + 10, 70, 20)
                    .build());
            }
        }
    }

    /**
     * Copied from {@link ImmersivePaintingScreen#updateWidget(ResourceLocation)}
     */
    public void updateWidget(ResourceLocation paintingLoc) {
        if (this.paintingWidgets.containsKey(paintingLoc)) {
            ClientPaintingManager.getPainting(paintingLoc)
                .ifPresent(p -> this.paintingWidgets.get(paintingLoc)
                    .update(ClientPaintingManager.getImageIdentifier(paintingLoc, Painting.Size.THUMBNAIL), p.width(), p.height()));
        }
    }

    /**
     * Copied from {@link ImmersivePaintingScreen#consolidate(List)}
     */
    private static Component consolidate(List<Component> textList) {
        if (textList == null)
            return null;

        Component base = Component.empty();
        MutableComponent lastTextNode = base.copy();

        if (textList.isEmpty())
            return base;

        for (int i = 0; i < textList.size() - 1; i++) {
            Component text = textList.get(i);
            lastTextNode = lastTextNode.append(text).append("\n");
        }

        Component finalElement = textList.getLast();
        return lastTextNode.append(finalElement);
    }

    protected void rebuildPaintings() {
        for (PaintingWidget w : this.paintingWidgets.values())
            this.removeWidget(w);
        this.paintingWidgets.clear();

        // paintings
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 8; x++) {
                int i = y * 8 + x + this.selectionPage * 24;
                if (i >= 0 && i < this.filteredPaintings.size()) {
                    ResourceLocation paintingLoc = this.filteredPaintings.get(i);
                    List<Component> tooltip = new LinkedList<>();

                    Optional<Painting> paintingOp = ClientPaintingManager.getPainting(paintingLoc);
                    if (paintingOp.isPresent()) {
                        Painting painting = paintingOp.get();
                        //tooltip
                        tooltip.add(Component.literal(painting.name()));
                        tooltip.add(Component.translatable("immersive_paintings.gui.by_author", painting.author()).withStyle(ChatFormatting.ITALIC));
                        tooltip.add(Component.translatable("immersive_paintings.gui.resolution", painting.width(), painting.height(), painting.resolution())
                            .withStyle(ChatFormatting.ITALIC));

                        if (this.page == Page.YOURS && painting.has(Painting.Flag.HIDDEN)) {
                            tooltip.add(Component.translatable("immersive_paintings.gui.hidden").withStyle(ChatFormatting.ITALIC)
                                .withStyle(ChatFormatting.GRAY));
                        }
                        if (this.page == Page.YOURS && painting.has(Painting.Flag.NSFW)) {
                            tooltip.add(Component.translatable("immersive_paintings.gui.nsfw").withStyle(ChatFormatting.ITALIC)
                                .withStyle(ChatFormatting.GRAY));
                        }

                        if (this.page == Page.YOURS || page == Page.PLAYERS && this.isOp()) {
                            tooltip.add(Component.translatable("immersive_paintings.right_click_to_delete")
                                .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                        }
                    }

                    PaintingWidget paintingWidget = this.addRenderableWidget(new PaintingWidget(
                        (int) (this.width / 2 + (x - 3.5) * 48) - 24, this.height / 2 - 66 + y * 48, 46, 46,
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
                        }
                    ));
                    paintingWidget.setTooltip(Tooltip.create(consolidate(tooltip)));
                    this.paintingWidgets.put(paintingLoc, paintingWidget);
                    this.updateWidget(paintingLoc);
                } else {
                    break;
                }
            }
        }
    }

    protected void rebuildScreenshots() {
        for (PaintingWidget w : this.paintingWidgets.values())
            this.removeWidget(w);
        this.paintingWidgets.clear();

        // screenshots
        for (int x = 0; x < SCREENSHOTS_PER_PAGE; x++) {
            int i = x + this.screenshotPage * SCREENSHOTS_PER_PAGE;
            if (i >= 0 && i < this.screenshots.size()) {
                File file = this.screenshots.get(i);

                PaintingWidget paintingWidget = this.addRenderableWidget(new PaintingWidget(
                    (this.width / 2 + (x - SCREENSHOTS_PER_PAGE / 2) * 68) - 32, this.height / 2 + 15, 64, 48,
                    b -> {
                        this.currentImage = ((PaintingWidget) b).getImage();
                        if (this.currentImage != null) {
                            currentImagePixelZoomCache = -1;
                            this.currentImageName = file.getName();
                            this.settings = PixelatorSettingsAccessor.callInit(this.currentImage, this.minResolution, this.maxResolution);
                            this.setPage(Page.CREATE);
                            this.pixelateImage();
                        }
                    },
                    b -> {}
                ));
                paintingWidget.setTooltip(Tooltip.create(Component.literal(file.getName())));
                ResourceLocation loc = Main.locate("screenshot_" + x);
                this.paintingWidgets.put(loc, paintingWidget);

                service.submit(() -> {
                    BufferedImage image = this.loadImage(file.getPath(), loc);
                    if (image != null)
                        paintingWidget.update(loc, image);
                });
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

        LocalPlayer player = Minecraft.getInstance().player;
        UUID uuid = player == null ? null : player.getUUID();
        boolean isOp = this.isOp();
        this.filteredPaintings.addAll(ClientPaintingManager.getPaintings().entrySet().stream()
            .filter(entry -> {
                Painting painting = entry.getValue();
                return (
                    (this.page == Page.YOURS && !painting.is(Painting.Type.DATAPACK) && painting.authorUUID().equals(uuid)) ||
                    (this.page == Page.PLAYERS && !painting.is(Painting.Type.DATAPACK) && (!painting.has(Painting.Flag.HIDDEN) || isOp) && (!painting.has(Painting.Flag.NSFW) || Configs.CLIENT.showNSFWPaintings || isOp)) ||
                    (this.page == Page.DATAPACKS && painting.is(Painting.Type.DATAPACK))
                ) &&
                    entry.getKey().toString().contains(this.filteredString) &&
                    (this.filteredResolution == 0 || painting.resolution() == this.filteredResolution) &&
                    (this.filteredWidth == 0 || painting.width() == this.filteredWidth) &&
                    (this.filteredHeight == 0 || painting.height() == this.filteredHeight) &&
                    (maxWidth == 0 || painting.width() <= maxWidth) &&
                    (maxHeight == 0 || painting.height() <= maxHeight);
            })
            .sorted(Comparator.comparing(p -> p.getValue().name()))
            .map(Map.Entry::getKey)
            .toList());

        this.setSelectionPage(this.selectionPage);
    }

    protected abstract int getConfigWidth();
    protected abstract int getConfigHeight();

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
            this.settings = PixelatorSettingsAccessor.callInit(this.currentImage, this.minResolution, this.maxResolution);
            this.setPage(Page.CREATE);
            this.pixelateImage();
        }
    }

    protected BufferedImage loadImage(String path, ResourceLocation loc) {
        InputStream stream = null;
        try {
            stream = new URL(path).openStream();
        } catch (Exception exception) {
            try {
                stream = new FileInputStream(path);
            } catch (Exception e) {
                BetsyRoss.LOGGER.error("failed loading image {} from path {}", loc, path, e);
            }
        }

        if (stream != null) {
            try {
                BufferedImage nativeImage = ImageIO.read(stream);
                Minecraft.getInstance().getTextureManager().register(loc, new DynamicTexture(ImageManipulations.bufferedToNative(nativeImage)));
                stream.close();
                return nativeImage;
            } catch (IOException e) {
                BetsyRoss.LOGGER.error("failed decoding image {} from path {}", loc, path, e);
            }
        }

        return null;
    }

    protected static int getCurrentImagePixelZoomCache(BufferedImage currentImage) {
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
    public void onReceivePaintingResponse(PaintingRegisterErrorPayload response) {
        if (response.error().isEmpty()) {
            if (this.canUpdateFlag() && response.identifier().isPresent())
                this.updateFlag(response.identifier().get());
            this.onClose();
        } else {
            this.setPage(Page.CREATE);
            this.setError(Component.translatable("immersive_paintings.error." + response.error()));
        }
    }

    protected abstract void updateFlag(ResourceLocation loc);
    protected abstract boolean canUpdateFlag();

    @Override public void refreshPage() { this.setPage(this.page); }

    public void setError(Component error) { this.error = error; }
    protected void clearError() { this.error = null; }

}
