/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 *
 * @author gbl
 */
public class DownloadGui extends Screen {

    TextFieldWidget urlInput;
    Text message1, message2;
    int innerX, innerY;

    public DownloadGui() {
        super(new LiteralText(FabricMod.MODNAME));
        message1 = message2 = null;
    }
    
    @Override
    protected void init() {
        innerX = width/2 - 100;
        innerY = height/2 - 80;

        this.addDrawableChild(urlInput = new TextFieldWidget(this.textRenderer, innerX+10, innerY+38, 180, 20, new LiteralText("")));
        urlInput.setMaxLength(200);
        String clipboard = client.keyboard.getClipboard();
        if (clipboard.startsWith(Downloader.urlStart)) {
            urlInput.setText(clipboard);
        }
        urlInput.setTextFieldFocused(true);
        this.setFocused(urlInput);
        this.addDrawableChild(new ClickableWidget(this.width/2-90, innerY+60, 180, 20, new LiteralText("Download Litematic")) {
            @Override
            public void onClick(double x, double y) {
                doDownload(false);
            }

            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
            }
        });
        
        this.addDrawableChild(new ClickableWidget(this.width/2-90, innerY+82, 80, 20, Downloader.getFlipXText()) {
            @Override
            public void onClick(double x, double y) {
                Downloader.toggleFlipX();
                setMessage(Downloader.getFlipXText());
            }
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
            }
        });

        this.addDrawableChild(new ClickableWidget(this.width/2+10, innerY+82, 80, 20, Downloader.getFlipZText()) {
            @Override
            public void onClick(double x, double y) {
                Downloader.toggleFlipZ();
                setMessage(Downloader.getFlipZText());
            }
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
            }
        });
        
        if (ConfigurationHandler.isExpertMode()) {
            this.addDrawableChild(new ClickableWidget(this.width/2-90, innerY+110, 180, 20, new LiteralText("Download Blockmap only")) {
                @Override
                public void onClick(double x, double y) {
                    doDownload(true);
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                }
            });
        }
    }
    
    private void doDownload(boolean mapOnly) {
        String downloadResult = Downloader.download(urlInput.getText(), mapOnly);
        if (downloadResult.contains("\n")) {
            String[] parts = downloadResult.split("\n");
            message1 = new LiteralText(parts[0]);
            message2 = new LiteralText(parts[1]);
        } else {
            message1 = new LiteralText(downloadResult);
            message2 = null;
        }
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float ticks) {
        super.render(stack, mouseX, mouseY, ticks);
        drawTextWithShadow(stack, textRenderer, new LiteralText("Copy/paste your URL here"), innerX+10, innerY+20, 0xffffff);
        if (message1 != null) {
            drawTextWithShadow(stack, textRenderer, message1, innerX+10, innerY+100, 0xff0000);
        }
        if (message2 != null) {
            drawTextWithShadow(stack, textRenderer, message2, innerX+10, innerY+100+textRenderer.fontHeight, 0xffffff);
        }
    }
}
