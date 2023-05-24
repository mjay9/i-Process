import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

public class iProcess {
    public static void main(String[] args) {
        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File("input.jpg"));

            // Create the output image with the same dimensions
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Define the number of threads for parallel processing
            int numThreads = Runtime.getRuntime().availableProcessors();

            // Create a fixed-size thread pool
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            // Calculate the height of each chunk
            int chunkHeight = inputImage.getHeight() / numThreads;

            // Process each chunk of the image using a separate thread
            for (int i = 0; i < numThreads; i++) {
                final int threadIndex = i;

                executor.execute(() -> {
                    // Define the starting and ending rows for this thread
                    int startRow = threadIndex * chunkHeight;
                    int endRow = (threadIndex == numThreads - 1) ? inputImage.getHeight() : (startRow + chunkHeight);

                    // Apply the grayscale filter to the image chunk
                    for (int y = startRow; y < endRow; y++) {
                        for (int x = 0; x < inputImage.getWidth(); x++) {
                            // Get the color of the pixel at (x, y)
                            Color color = new Color(inputImage.getRGB(x, y));

                            // Calculate the grayscale value
                            int gray = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());

                            // Create a new Color object with the grayscale value
                            Color grayColor = new Color(gray, gray, gray);

                            // Set the grayscale color to the output image
                            outputImage.setRGB(x, y, grayColor.getRGB());
                        }
                    }
                });
            }

            // Shutdown the executor and wait for all threads to finish
            executor.shutdown();
            while (!executor.isTerminated()) {
                // Waiting for all threads to finish
            }

            // Save the output image
            ImageIO.write(outputImage, "jpg", new File("output.jpg"));

            System.out.println("Image processing completed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}