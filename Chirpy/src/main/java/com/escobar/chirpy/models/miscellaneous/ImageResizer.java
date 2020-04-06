/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.models.miscellaneous;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author escob
 */
public class ImageResizer {
    
    public static byte[] imageResizeFromUser(byte[] imagebyte, int width, int height) throws IOException{
        
        ByteArrayInputStream bais = new ByteArrayInputStream(imagebyte);
        BufferedImage bImage2 = ImageIO.read(bais);
        
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(bImage2, 0, 0, width, (int) height, null);
        graphics2D.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();            
        ImageIO.write(bufferedImage, "jpg", baos);        
        return baos.toByteArray();
        
    }
    
    public static byte[] imageResizeFromImages(byte[] imagebyte, int width) throws IOException{
        
        ByteArrayInputStream bais = new ByteArrayInputStream(imagebyte);
        BufferedImage bImage2 = ImageIO.read(bais);
        
        double aspectRatio = (double) bImage2.getWidth(null)/(double) bImage2.getHeight(null);
        System.out.println(aspectRatio);
        
        
        final BufferedImage bufferedImage = new BufferedImage(width, (int) (width/aspectRatio), BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(bImage2, 0, 0, width, (int) (width/aspectRatio), null);
        graphics2D.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();            
        ImageIO.write(bufferedImage, "jpg", baos);        
        return baos.toByteArray();
        
    }
    
}
