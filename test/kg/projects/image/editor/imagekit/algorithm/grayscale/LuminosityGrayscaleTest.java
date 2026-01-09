package kg.projects.image.editor.imagekit.algorithm.grayscale;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LuminosityGrayscaleTest {
    private LuminosityGrayscale filter = new LuminosityGrayscale();

    @Test
    void testProcessNull() {
        assertThrows(IllegalArgumentException.class, () -> filter.process(null));
    }

    @Test
    void testProcessValidImage() {
        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < testImage.getWidth(); x++) {
            for (int y = 0; y < testImage.getHeight(); y++) {
                testImage.setRGB(x, y, x + y);
            }
        }

        BufferedImage result = filter.process(testImage);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                int sum = x + y;
                int expectedGrayVal = (sum >= 8) ? 1 : 0;
                int expectedPixel = (0xFF << 24) |
                        (expectedGrayVal << 16) |
                        (expectedGrayVal << 8) |
                        expectedGrayVal;
                assertEquals(expectedPixel, result.getRGB(x, y),
                        "Mismatch at coordinate " + x + "," + y);
            }
        }
    }
}
