/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVTest {

    public OpenCVTest() {
        test();
    }

    public static void main(String[] args) {
        OpenCVTest ot = new OpenCVTest();
    }

    private void test() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            BufferedImage image = ImageIO.read(new File("kittens.jpg"));

            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.out.println("data:");
            for (int i = 0; i < 5000; i++) {
                if (i % 3 == 0) {
                    System.out.println();
                }
                System.out.print(" " + data[i]);
            }

        
    
    Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);

    mat.put (
    0, 0, data);

      Mat mat1 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);

    Imgproc.cvtColor (mat, mat1, Imgproc.COLOR_RGB2GRAY);

    byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int) (mat1.elemSize())];

    mat1.get (
    0, 0, data1);
      BufferedImage image1 = new BufferedImage(mat1.cols(), mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);

    image1.getRaster ()
    .setDataElements(0,0,mat1.cols(),mat1.rows(),data1);

      File ouptut = new File("grayscale.jpg");

    ImageIO.write (image1, 

"jpg", ouptut);
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }
   
}
