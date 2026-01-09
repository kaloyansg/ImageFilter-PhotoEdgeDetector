package kg.projects.image.editor.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements  GrayscaleAlgorithm {

    private final int emptyBit = 24;
    private final int redBit = 16;
    private final int greenBit = 8;
    private final int bitMask = 0xff;
    private final double redCoeff = 0.21;
    private final double greenCoeff = 0.72;
    private final double blueCoeff = 0.07;
    private final int maxValue = 255;

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);

                int r =  (pixel >> redBit) & bitMask;
                int g =  (pixel >> greenBit) & bitMask;
                int b =  (pixel) & bitMask;

                double luminosityVal = (redCoeff * r) + (greenCoeff * g) + (blueCoeff * b);
                int grayVal = (int) Math.round(luminosityVal);
                grayVal = Math.min(grayVal, maxValue);

                int newPixel = (bitMask << emptyBit) | (grayVal << redBit) | (grayVal << greenBit) | grayVal;
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }
}
