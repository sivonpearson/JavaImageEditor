import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.io.*;

public class ImageFilter {
  private Random rand = new Random();
  private File f;
  private BufferedImage image;
  private int width;
  private int height;
  private String tag;

  private final int overallColorThreshold = 45; //60

  private final int ALPHA_VALUE = 255; //fully opaque

  public ImageFilter(File f, String tag) {
    this.f = f;
    System.out.println("Reading file: "+f);
    image = ImageUtilities.readImage(f);
    width = image.getWidth();
    height = image.getHeight();
    this.tag = tag;
  }

  

  //red colors: Red (#D30000; 211,0,0)
  public void applyCustomFilter() {
    System.out.println("Applying custom filter...");
    Color blackC = new Color(0, 0, 0);
    int blackRGB = blackC.getRGB();
    Color whiteC = new Color(255, 255, 255);
    int whiteRGB = whiteC.getRGB();
    for(int r = 0; r < height; r++) {
      for(int c = 0; c < width; c++) {
        Color color = new Color(image.getRGB(c, r));
        int colorRGB = color.getRGB();
        Color redColor = new Color(255, 0, 0);
        if(colorRGB != whiteRGB && colorRGB != blackRGB) {
          image.setRGB(c, r, redColor.getRGB());
        }

      }
    }
    
  }

  public void applyBlackAndWhite() {
    System.out.println("Applying black and white filter...");
    for(int r = 0; r < height; r++) { // 'r' corresponds to 'y'
      for(int c = 0; c < width; c++) { // 'c' corresponds to 'x'
        Color color = new Color(image.getRGB(c, r));
        int luminance = getLuminance(color);
        color = new Color(luminance, luminance, luminance, ALPHA_VALUE);
        image.setRGB(c, r, color.getRGB());
      }
    }
  }

  private int getLuminance(Color c) {
    final int RED_LUMINANCE_COEFFICIENT = 30;
    final int GREEN_LUMINANCE_COEFFICIENT = 59; 
    final int BLUE_LUMINANCE_COEFFICIENT = 11;
    int luminance = (RED_LUMINANCE_COEFFICIENT * c.getRed() + GREEN_LUMINANCE_COEFFICIENT * c.getGreen() + BLUE_LUMINANCE_COEFFICIENT * c.getBlue()) / (RED_LUMINANCE_COEFFICIENT + GREEN_LUMINANCE_COEFFICIENT + BLUE_LUMINANCE_COEFFICIENT);
    return luminance;
  }

  public void applySepia() {
    System.out.println("Applying sepia filter...");
    final int adjustRed = 16;
    final int adjustGreen = -3;
    final int adjustBlue = -30;
    for(int r = 0; r < height; r++) { // 'r' corresponds to 'y'
      for(int c = 0; c < width; c++) { // 'c' corresponds to 'x'
        Color color = new Color(image.getRGB(c, r));
        int luminance = getLuminance(color);
        int red = truncateValues(luminance + adjustRed);
        int green = truncateValues(luminance + adjustGreen);
        int blue = truncateValues(luminance + adjustBlue);
        color = new Color(red, green, blue, ALPHA_VALUE);
        image.setRGB(c, r, color.getRGB());
      }
    }
    updateTag("SEPIA"); //applies tag of "SEPIA"
  }

  // 'p' determines how far from the center to start fading the pixels; 'p' has a range of 0.0 - 1.0
  public void applyVignette(double p) {
    System.out.println("Applying vignette...");
    // center coordinates:
    int cx = width / 2;
    int cy = height / 2;
    // constants:
    double xp2 = Math.pow(cx, 2);
    double yp2 = Math.pow(cy, 2);
    double threshold = Math.pow(p, 2);
    // processing:
    for(int r = 0; r < height; r++) { // 'r' corresponds to 'y'
      for(int c = 0; c < width; c++) { // 'c' corresponds to 'x'
        Color color = new Color(image.getRGB(c, r));
        double d = (Math.pow((cx - c), 2) / xp2) + (Math.pow((cy - r), 2) / yp2);
        if(d > 1.0) { // if pixel is located outside of the ellipse
          color = new Color(0, 0, 0, ALPHA_VALUE);
        }
        else if(d > threshold) { //if the pixel is located in the faded area
          double fade = 1 - Math.pow((d - threshold) / (1 - threshold), 2);
          int red = (int)(color.getRed() * fade);
          int green = (int)(color.getGreen() * fade);
          int blue = (int)(color.getBlue() * fade);
          color = new Color(red, green, blue, ALPHA_VALUE);
        }
        else{
          color = new Color(color.getRed(), color.getGreen(), color.getBlue(), ALPHA_VALUE);
        }
        image.setRGB(c, r, color.getRGB());
      }
    }

    updateTag("VIGNETTE"); //applies tag of "VIGNETTE" or "VIGNETTE_BW" when combined w/ BW filter or "VIGNETTE_SEPIA" when combined w/ vignette filter
  }

  /*
   * Splits image into 3x3
   * 
   * | 1 2 3 |
   * | 4 5 6 |
   * | 7 8 9 |
   */
  public void split3x3(int x, int y, int pxSize) {
    if(x + pxSize >= width || y + pxSize >= height) {
      System.out.println("FAILED SPLIT!");
      return;
    }
    BufferedImage[] splitImages = new BufferedImage[9];
    int divSize = pxSize / 3;
    for(int i = 0; i < 9; i++) {
      splitImages[i] = image.getSubimage(x + (i % 3) * divSize, y + (i / 3) * divSize, divSize, divSize);
      System.out.println("Writing file: "+splitImages[i]);
      ImageUtilities.writeImage(splitImages[i], f, "split"+(i+1));
    }
    System.out.println("Split complete.");

  }

  /*
   * For scrolling images 
   */
  public void split1xN(int x, int y, int pxSize, int N) {
    if(x + pxSize >= width || y + pxSize >= height) {
      System.out.println("FAILED SPLIT!");
      return;
    }
    BufferedImage[] splitImages = new BufferedImage[9];
    int divSize = pxSize / 3;
    for(int i = 0; i < 9; i++) {
      splitImages[i] = image.getSubimage(x + (i % 3) * divSize, y + (i / 3) * divSize, divSize, divSize);
      System.out.println("Writing file: "+splitImages[i]);
      ImageUtilities.writeImage(splitImages[i], f, "split"+(i+1));
    }
    System.out.println("Split complete.");

  }

  public void addBorders() {
    int newSize;
    if(width > height) newSize = width;
    else newSize = height;
    BufferedImage newImage = new BufferedImage(newSize, newSize, 5);
    for(int r = 0; r < newSize; r++) {
      for(int c = 0; c < newSize; c++) { // c < width
        newImage.setRGB(c, r, 0);
      }
    }
    for(int r = 0; r < height; r++) {
      for(int c = 0; c < width; c++) { // c < width
        newImage.setRGB(c + (newSize - width) / 2, r + (newSize - height) / 2, image.getRGB(c, r));
      }
    }

    System.out.println("Writing file: "+newImage);
    ImageUtilities.writeImage(newImage, f, "bordered");
  }

  // truncates negative values to zero
  // truncates positive values that exceed 255 to 255
  // doesn't affect positive values in the inclusive range of 0-255
  private int truncateValues(int num) {
    if(num < 0)
      return 0;
    else if(num > 255)
      return 255;
    else
      return num;
  }

  private void updateTag(String attribute) {
    //attribute will be: 'BW', 'SEPIA', or 'VIGNETTE'
    if(tag == "") { //when tag is empty
      tag = attribute;
    }
    else{ //when tag is NOT empty
      // an image will never have both SEPIA and BW
      switch(attribute) {
        case "BW":
          tag += "_BW";
          break;
        case "SEPIA":
          tag += "_SEPIA";
          break;
        case "VIGNETTE":
          tag = "VIGNETTE_"+tag;
          break;
        default: //unlikely to happen
          tag = "ORIGINAL";
          break;
      }
    }
  }

  public void write() {
    System.out.println("Writing file: "+f);
    ImageUtilities.writeImage(image, f, tag);
  }


}