/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GrabcraftLitematic;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
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
    Text message;
    int innerX, innerY;

    public DownloadGui() {
        super(new LiteralText(FabricMod.MODNAME));
        message = null;
    }
    
    @Override
    protected void init() {
        innerX = width/2 - 100;
        innerY = height/2 - 40;

        this.addButton(urlInput = new TextFieldWidget(this.textRenderer, innerX+10, innerY+38, 180, 20, new LiteralText("")));
        urlInput.setMaxLength(200);
        this.addButton(new AbstractButtonWidget(this.width/2-25, innerY+60, 50, 20, new LiteralText("Download")) {
            @Override
            public void onClick(double x, double y) {
                message = new LiteralText(Downloader.download(urlInput.getText()));
            }
        });
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float ticks) {
        super.render(stack, mouseX, mouseY, ticks);
        drawTextWithShadow(stack, textRenderer, new LiteralText("Copy/paste your URL here"), innerX+10, innerY+20, 0xffffff);
        if (message != null) {
            drawTextWithShadow(stack, textRenderer, message, innerX+10, innerY+80, 0xff0000);
        }
    }
}
