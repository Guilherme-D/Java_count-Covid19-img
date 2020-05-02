/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pid;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import org.opencv.imgproc.Imgproc;


/**
 *
 * @author desenvolvedor
 */
public class Img_process {
    
    static void getSelectedImage(int x_pressed, int x_released,
            int y_pressed, int y_released, ImageIcon label_img) {
        
        int width = Math.abs(x_released - x_pressed);
        int height = Math.abs(y_released - y_pressed);
        
        int minx = Math.min(x_pressed, x_released);
        int miny = Math.min(y_pressed, y_released);
        //int maxx = Math.max(x_pressed, x_released);
        //int maxy = Math.max(y_pressed, y_released);
        
        Image i = label_img.getImage();
        BufferedImage b_img = new BufferedImage(label_img.getIconWidth(),
                label_img.getIconHeight(), BufferedImage.TYPE_BYTE_GRAY);
        b_img.getGraphics().drawImage(label_img.getImage(), 0, 0, null);
        
        System.out.println("x+w = "+x_pressed+width);
        BufferedImage subimage = b_img.getSubimage(minx, miny, width-1, height-1);
       
        openNewFrame(subimage);
               
    }
    
    static void openNewFrame(BufferedImage subimage){
     
        JFrame s = new JFrame();
        JLabel label = new JLabel(new ImageIcon(subimage));
        s.add(label);
        s.pack();
        s.setVisible(true);
    }
    
    public static BufferedImage getHistogram(String img_path){

        System.load("/home/desenvolvedor/NetBeansProjects/Pid/src/pid/libopencv_java430.so");
        
        
        Mat src = imread(img_path);
  
        if (src.empty()) {
            System.err.println("Cannot read image: " + img_path);
            System.exit(0);
        }
       
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);
        int histSize = 256;
        
        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);
        
        boolean accumulate = false;
            
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);

        
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar(0) );
        
        
        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);

        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);

        for( int i = 1; i < histSize; i++ ) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255), 2);
        }
        
        return MatToBufferedImage(histImage);
       
    }

    public static ImageIcon zoomImg(ImageIcon image, boolean zoom, int factor){
    
        int width = image.getIconWidth();
        int height = image.getIconHeight();
        
        if(zoom){
            width *= 1+(factor*0.1);//(int) (image.getIconWidth() * 2);
            height *= 1+(factor*0.1);//(int) (image.getIconHeight() * 2);
        }else{
            width *= 1+(factor*0.1);//(int) (image.getIconWidth() * 2);
            height *= 1+(factor*0.1);//(int) (image.getIconHeight() * 2);
        }
        
        Image img = image.getImage(); // transform it 
        Image newimg = img.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  

        return new ImageIcon(newimg); 
        
    }
    
    public static ImageIcon getIcon(String path){
    
        ImageIcon icon = new ImageIcon(path);
        
        return icon;
    }
    
    public static BufferedImage MatToBufferedImage(Mat mat_img){
    
         int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( mat_img.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(mat_img.cols(),mat_img.rows(), type);
        mat_img.get(0,0,((DataBufferByte)image.getRaster().getDataBuffer()).getData()); // get all the pixels
        
        return image;
       
    }

    public static BufferedImage draw_rectangle_dragging(int x_pressed, int x_released,
            int y_pressed, int y_released, ImageIcon label_img){
        
        int width = Math.abs(x_released - x_pressed);
        int height = Math.abs(y_released - y_pressed);
        
        int minx = Math.min(x_pressed, x_released);
        int miny = Math.min(y_pressed, y_released);
        //int maxx = Math.max(x_pressed, x_released);
        //int maxy = Math.max(y_pressed, y_released);
        
        Mat SrcMat = imread(label_img.toString());
        
        
        //TODO: ver posição dos pontos para desenhar retangulo, nao vai certo
       Imgproc.rectangle(SrcMat, new Point(x_pressed, y_pressed), 
                new Point(x_pressed + width, y_pressed + height), 
                new Scalar(255, 255, 255), 5);
    
       
       return MatToBufferedImage(SrcMat);
    }

}
