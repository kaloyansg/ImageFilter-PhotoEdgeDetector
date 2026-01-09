package kg.projects.image.editor.imagekit.algorithm.detection;

import kg.projects.image.editor.imagekit.algorithm.grayscale.GrayscaleAlgorithm;
import kg.projects.image.editor.imagekit.algorithm.grayscale.LuminosityGrayscale;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SobelEdgeDetectionTest {
    private GrayscaleAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
    private SobelEdgeDetection filter = new SobelEdgeDetection(grayscaleAlgorithm);

    @Test
    void testProcessNull() {
        assertThrows(IllegalArgumentException.class, () -> filter.process(null));
    }

    private int getBlue(BufferedImage img, int x, int y) {
        return img.getRGB(x, y) & 0xFF;
    }

    @Test
    void testProcessValidImage() {
        int width = 10;
        int height = 10;
        BufferedImage testImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int val = x + y;
                int pixel = (0xFF << 24) | (val << 16) | (val << 8) | val;
                testImage.setRGB(x, y, pixel);
            }
        }
        BufferedImage result = filter.process(testImage);
        int bitMask = 0xFF;
        int maxValue = 255;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int sumGx = 0;
                int sumGy = 0;

                if (x - 1 >= 0) {
                    sumGx += -2 * getBlue(testImage, x - 1, y);
                    if (y + 1 < height)
                        sumGx += -1 * getBlue(testImage, x - 1, y + 1);
                    if (y - 1 >= 0)
                        sumGx += -1 * getBlue(testImage, x - 1, y - 1);
                }
                if (x + 1 < width) {
                    sumGx += 2 * getBlue(testImage, x + 1, y);
                    if (y + 1 < height)
                        sumGx += getBlue(testImage, x + 1, y + 1);
                    if (y - 1 >= 0)
                        sumGx += getBlue(testImage, x + 1, y - 1);
                }
                if (y + 1 < height) {
                    sumGy += 2 * getBlue(testImage, x, y + 1);
                    if (x + 1 < width)
                        sumGy += getBlue(testImage, x + 1, y + 1);
                    if (x - 1 >= 0)
                        sumGy += getBlue(testImage, x - 1, y + 1);
                }
                // Top Row (y-1)
                if (y - 1 >= 0) {
                    sumGy += -2 * getBlue(testImage, x, y - 1);
                    if (x + 1 < width)
                        sumGy += -1 * getBlue(testImage, x + 1, y - 1);
                    if (x - 1 >= 0)
                        sumGy += -1 * getBlue(testImage, x - 1, y - 1);
                }

                double sumG = Math.sqrt(Math.pow(sumGx, 2) + Math.pow(sumGy, 2));
                int expectedVal = Math.min(maxValue, (int) Math.round(sumG));

                int expectedPixel = (0xFF << 24) | (expectedVal << 16) | (expectedVal << 8) | expectedVal;

                assertEquals(expectedPixel, result.getRGB(x, y),
                        "Mismatch at " + x + "," + y + " | Gx:" + sumGx + " Gy:" + sumGy);
            }
        }
    }
}
