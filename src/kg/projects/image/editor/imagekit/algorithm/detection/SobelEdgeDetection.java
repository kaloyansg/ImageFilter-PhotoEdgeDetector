package kg.projects.image.editor.imagekit.algorithm.detection;

import kg.projects.image.editor.imagekit.algorithm.ImageAlgorithm;
import kg.projects.image.editor.imagekit.algorithm.grayscale.GrayscaleAlgorithm;

import java.awt.image.BufferedImage;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {
    private final int maxValue = 255;
    private final int redBit = 16;
    private final int greenBit = 8;
    private final int bitMask = 0xff;

    ImageAlgorithm grayscaleAlgorithm;

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        if (grayscaleAlgorithm == null) {
            throw new IllegalArgumentException("grayscale algorithm cannot be null");
        }
        if (!(grayscaleAlgorithm instanceof GrayscaleAlgorithm)) {
            throw new IllegalArgumentException("grayscale algorithm must be an instance of GrayscaleAlgorithm");
        }
        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image must not be null");
        }

        BufferedImage grayImage = grayscaleAlgorithm.process(image);
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int newPixel = computeNewPixel(grayImage, x, y);
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    private int computeNewPixel(BufferedImage image, int x, int y) {
        int gx = calculateGx(image, x, y);
        int gy = calculateGy(image, x, y);

        double gTotal = Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2));
        int pixelValue = Math.min(maxValue, (int) Math.round(gTotal));

        return (pixelValue << redBit) | (pixelValue << greenBit) | pixelValue;
    }

    private int calculateGx(BufferedImage image, int x, int y) {
        int sumGx = 0;

        sumGx -= 2 * getSafePixel(image, x - 1, y);
        sumGx += -1 * getSafePixel(image, x - 1, y + 1);
        sumGx += -1 * getSafePixel(image, x - 1, y - 1);

        sumGx += 2 * getSafePixel(image, x + 1, y);
        sumGx += getSafePixel(image, x + 1, y + 1);  // <--- Check this line
        sumGx += getSafePixel(image, x + 1, y - 1);  // <--- Check this line

        return sumGx;
    }

    private int calculateGy(BufferedImage image, int x, int y) {
        int sumGy = 0;

        sumGy += 2 * getSafePixel(image, x, y + 1);
        sumGy += getSafePixel(image, x + 1, y + 1);
        sumGy += getSafePixel(image, x - 1, y + 1);

        sumGy -= 2 * getSafePixel(image, x, y - 1);
        sumGy += -1 * getSafePixel(image, x + 1, y - 1);
        sumGy += -1 * getSafePixel(image, x - 1, y - 1);

        return sumGy;
    }

    private int getSafePixel(BufferedImage image, int x, int y) {
        if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
            return image.getRGB(x, y) & bitMask;
        }
        return 0;
    }

    /*@Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image must not be null");
        }

        int sumGx = 0;
        int sumGy = 0;
        image = grayscaleAlgorithm.process(image);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                final int coeff = -2;
                if (x - 1 >= 0) {
                    sumGx += coeff * (image.getRGB(x - 1, y) & bitMask);
                    if (y + 1 < image.getHeight()) {
                        sumGx += -1 * (image.getRGB(x - 1, y + 1) & bitMask);
                    }
                    if (y - 1 >= 0) {
                        sumGx += -1 * (image.getRGB(x - 1, y - 1) & bitMask);
                    }
                }
                if (x + 1 < image.getWidth()) {
                    sumGx += 2 * (image.getRGB(x + 1, y) & bitMask);
                    if (y + 1 < image.getHeight()) {
                        sumGx += (image.getRGB(x + 1, y + 1) & bitMask);
                    }
                    if (y - 1 >= 0) {
                        sumGx += (image.getRGB(x + 1, y - 1) & bitMask);
                    }
                }

                if (y + 1 < image.getHeight()) {
                    sumGy += 2 * (image.getRGB(x, y + 1) & bitMask);
                    if (x + 1 < image.getWidth()) {
                        sumGy += (image.getRGB(x + 1, y + 1) & bitMask);
                    }
                    if (x - 1 >= 0) {
                        sumGy += (image.getRGB(x - 1, y + 1) & bitMask);
                    }
                }
                if (y - 1 >= 0) {
                    sumGy += coeff * (image.getRGB(x, y - 1) & bitMask);
                    if (x + 1 < image.getWidth()) {
                        sumGy += -1 * (image.getRGB(x + 1, y - 1) & bitMask);
                    }
                    if (x - 1 >= 0) {
                        sumGy += -1 * (image.getRGB(x - 1, y - 1) & bitMask);
                    }
                }

                double sumG = Math.sqrt(Math.pow(sumGx, 2) + Math.pow(sumGy, 2));
                int pixelValue = Math.min(maxValue, (int) Math.round(sumG));
                result.setRGB(x, y, ((pixelValue << redBit) | (pixelValue << greenBit) | pixelValue));
                sumGx = 0;
                sumGy = 0;
            }
        }
        return result;
    }*/
}
