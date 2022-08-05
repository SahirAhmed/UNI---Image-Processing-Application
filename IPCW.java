import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
//import java.util.Stack;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
 
public class IPCW extends Component implements ActionListener {
    
    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************
  
	String descs[] = {
        "Original", //bi
		"Undo Operation", //biPrevious
        "Negative", //biFiltered
		"Rescaling", //biFiltered
		"Shifting", //biFiltered
		"Shifting and Rescaling via Random Integer", //biFiltered
		"Two Image Arithmetic Operations", //biFiltered, biArithmetic
		"Bitwise Boolean - NOT", //biFiltered
		"Bitwise Boolean - AND, OR, XOR", //biFiltered, biArithmetic
		"ROI-based Operation", //biFiltered, biROI
		"LUT Operations", //biFiltered
		"Random LUT Table", //biFiltered
		"Bit Plane Slicing", //biFiltered
		"Histograms", //biFiltered
		"Image Convolution", //biFiltered
		"Salt And Pepper Noise", //biFiltered
		"Filtering", //biFiltered
		"Mean and Standard Deviation", //biFiltered
		"Simple Thresholding", //biFiltered
		"Automated Thresholding", //biFiltered
		"<< Use to perform additional operation within SAME category >>", //biFiltered
    };
 
    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage bi, biPrevious, biFiltered, biArithmetic, biROI;   // the input image saved as bi; previous image before change is saved as biPrevious; biFiltered is image after filter has been applied; biArithmetic is the image used for arithmetic operations; biROI is the image used for ROI operations//
    int w, h;
     
    public IPCW() {
        try {
			
			JButton openbi = new JButton(); //button needs to be used as an component to open the box, odd
			JFileChooser fcbi = new JFileChooser();
			fcbi.setCurrentDirectory(new java.io.File(".")); //path to specified directory
			fcbi.setDialogTitle("Choose Original Image"); //box title
			fcbi.setFileSelectionMode(JFileChooser.FILES_ONLY); //list both files and directories
			if (fcbi.showOpenDialog(openbi) == JFileChooser.APPROVE_OPTION){
			} //although its not being used, it is needed to open up the dialog box
			
			JButton openbiArith = new JButton(); //File chooser for arithmetic operation image
			JFileChooser fcbiArith = new JFileChooser();
			fcbiArith.setCurrentDirectory(new java.io.File("."));
			fcbiArith.setDialogTitle("Choose Image to use with Arithmetic Operations");
			fcbiArith.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fcbiArith.showOpenDialog(openbiArith) == JFileChooser.APPROVE_OPTION){
			}
			
			JButton openbiROI = new JButton(); //File chooser for roi operation image
			JFileChooser fcbiROI = new JFileChooser();
			fcbiROI.setCurrentDirectory(new java.io.File("."));
			fcbiROI.setDialogTitle("Choose Image to use with ROI Operations");
			fcbiROI.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fcbiROI.showOpenDialog(openbiROI) == JFileChooser.APPROVE_OPTION){
			}
			
			bi = ImageIO.read(new File(fcbi.getSelectedFile().getAbsolutePath())); //original image, uses path chosen by user to retrieve file
			biArithmetic = ImageIO.read(new File(fcbiArith.getSelectedFile().getAbsolutePath())); //additional image chosen by user for arithmetic/logical operations
			biROI = ImageIO.read(new File(fcbiROI.getSelectedFile().getAbsolutePath())); //additional image chosen by user for ROI operations
			
			JFrame fbi = new JFrame("Original Image"); //Second Window for original image
			ImageIcon icon1 = new ImageIcon(bi);
			fbi.add(new JLabel(icon1));
			fbi.pack();
			fbi.setVisible(true);
			
			Point leftFrameLocation1 = fbi.getLocation(); //specifying relative jframe location
			Point rightFrameLocation1 = new Point(
						leftFrameLocation1.x + fbi.getWidth(),
						leftFrameLocation1.y);
			fbi.setLocation(rightFrameLocation1);
			
			JFrame fbiArithmetic = new JFrame("Arithmetic Image"); //Second Window for Arithemetic image
			ImageIcon icon2 = new ImageIcon(biArithmetic);
			fbiArithmetic.add(new JLabel(icon2));
			fbiArithmetic.pack();
			fbiArithmetic.setVisible(true);
			
			Point leftFrameLocation2 = fbiArithmetic.getLocation(); //specifying relative jframe location
			Point rightFrameLocation2 = new Point(
						leftFrameLocation2.x,
						leftFrameLocation2.y + fbiArithmetic.getHeight());
			fbiArithmetic.setLocation(rightFrameLocation2);
			
			JFrame fbiROI = new JFrame("ROI Image"); //Second Window for ROI image
			ImageIcon icon3 = new ImageIcon(biROI);
			fbiROI.add(new JLabel(icon3));
			fbiROI.pack();
			fbiROI.setVisible(true);
			
			Point leftFrameLocation3 = fbiROI.getLocation(); //specifying relative jframe location
			Point rightFrameLocation3 = new Point(
						leftFrameLocation3.x + fbiROI.getWidth(),
						leftFrameLocation3.y + fbiROI.getHeight());
			fbiROI.setLocation(rightFrameLocation3);
			
			w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biPrevious = biFiltered = bi = bi2;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }                         
 
    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }
 	
	
	String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
 

    void setOpIndex(int i) {
        opIndex = i;
    }
 
    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();      

        g.drawImage(biFiltered, 0, 0, null);
    }
 

    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
      int width = image.getWidth();
      int height = image.getHeight();

      int[][][] result = new int[width][height][4];

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int p = image.getRGB(x,y);
            int a = (p>>24)&0xff;
            int r = (p>>16)&0xff;
            int g = (p>>8)&0xff;
            int b = p&0xff;

            result[x][y][0]=a;
            result[x][y][1]=r;
            result[x][y][2]=g;
            result[x][y][3]=b;
         }
      }
      return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];
                
                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
		
        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }


    //************************************
    //  Your turn now:  Add more function below
    //************************************
	
	public BufferedImage PreviousImage(BufferedImage timg){ //Undo Operation 1.1
		timg = biPrevious; //to enfore multiple undos, could store in an arraylist
        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

	public BufferedImage ImageRescaling(BufferedImage timg){ //2.1
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered; // used to store previous image so we could carry out the undo operation if needed
	
		// Image Rescaling Operation:
		Double[] options = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0, 
			1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0}; //options for the rescale intensities
        double d = (Double)JOptionPane.showInputDialog(null, "Pick Rescale Value", 
                "Floating-Point Numbers", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
   	
		float s = (float)d;
        for(int y=0; y<height; y++){ //scan through image
			for(int x=0; x<width; x++){ //rescaling done via multiplying
				ImageArray[x][y][1] = Math.round(s*(ImageArray[x][y][1])); //r
				ImageArray[x][y][2] = Math.round(s*(ImageArray[x][y][2])); //g
				ImageArray[x][y][3] = Math.round(s*(ImageArray[x][y][3])); //b
				if (ImageArray[x][y][1]<0) { ImageArray[x][y][1] = 0; } //setting limits to prevent errors as we are only working with 8-bit images
				if (ImageArray[x][y][2]<0) { ImageArray[x][y][2] = 0; }
				if (ImageArray[x][y][3]<0) { ImageArray[x][y][3] = 0; }
				if (ImageArray[x][y][1]>255) { ImageArray[x][y][1] = 255; }
				if (ImageArray[x][y][2]>255) { ImageArray[x][y][2] = 255; }
				if (ImageArray[x][y][3]>255) { ImageArray[x][y][3] = 255; }
			}
		}
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage ImageShifting(BufferedImage timg){ //2.2
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
	
		// Image Shifting Operation:
		String inputShift = JOptionPane.showInputDialog("Enter an Integer, negative or positive (-256 to 256)");
		int t = Integer.parseInt(inputShift); //no hard limits set but to see results, it has to be between these two values
   
        for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){ //shifting done via addition/subtraction of the RGB values
				ImageArray[x][y][1] = (ImageArray[x][y][1]+t); //r
				ImageArray[x][y][2] = (ImageArray[x][y][2]+t); //g
				ImageArray[x][y][3] = (ImageArray[x][y][3]+t); //b
				if (ImageArray[x][y][1]<0) { ImageArray[x][y][1] = 0; } //these checks are definitely needed since it is likely to go below/above the possible limits
				if (ImageArray[x][y][2]<0) { ImageArray[x][y][2] = 0; }
				if (ImageArray[x][y][3]<0) { ImageArray[x][y][3] = 0; }
				if (ImageArray[x][y][1]>255) { ImageArray[x][y][1] = 255; }
				if (ImageArray[x][y][2]>255) { ImageArray[x][y][2] = 255; }
				if (ImageArray[x][y][3]>255) { ImageArray[x][y][3] = 255; }
			}
		}
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage ImageShiftAndRescale(BufferedImage timg){ //2.3 
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
		
		Random random = new Random();
		
		int s = random.nextInt(255); //random shifting and scaling values will be generated
		int t = random.nextInt(255); //both operations can be seen via the multiplication and addition to the RGB values
	
		// Image ShiftAndRescale Operation:
		int rmin = 0; int rmax = 0;
		int gmin = 0; int gmax = 0;
		int bmin= 0; int bmax = 0;
		
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				ImageArray[x][y][1] = s*(ImageArray[x][y][1]+t); //r
				ImageArray[x][y][2] = s*(ImageArray[x][y][2]+t); //g
				ImageArray[x][y][3] = s*(ImageArray[x][y][3]+t); //b
				if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; } //this can affect the image in way we may not understand due to it being so random
				if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; } //to safeguard ourselves, we store the absolute minimum and absolute max RGB values in a variable
				if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
				if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
				if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
				if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
			}
		}
		
		for(int y=0; y<height; y++){
			for(int x =0; x<width; x++){ //we use the min/max variables from the previous for loop to normalise the image and scale it back to the constraints we have which is 8-bit images
				ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
				ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
				ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
			}
		}
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage TwoImages(BufferedImage timg, BufferedImage timg2){ //3.1
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);          //  Convert the image to array
		
		biPrevious = biFiltered;
		
		String[] options = {"Addition","Subtraction","Reverse Subtraction","Multiplication",
							"Division","Reverse Division"}; //options for the ARITHMETIC OPERATIONS
        String d = (String)JOptionPane.showInputDialog(null, "Pick Arithmetic Operation", 
                "Arithmetic Operations", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		//using JOptionPane to create a dropdown menu which the user can select a certain operation		
				
		
		if (d == "Addition"){
			// Arithmetic Addition Operation:
			int rmin = 0; int rmax = 0;
			int gmin = 0; int gmax = 0;
			int bmin= 0; int bmax = 0;
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //addition can be seen via the +
					ImageArray[x][y][1]=ImageArray[x][y][1]+ImageArray2[x][y][1]; //r
					ImageArray[x][y][2]=ImageArray[x][y][2]+ImageArray2[x][y][2]; //g
					ImageArray[x][y][3]=ImageArray[x][y][3]+ImageArray2[x][y][3]; //b
					
					if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
					if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
				}	
			}
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //normalisation
					ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
					ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
					ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
				}
			}
			
		} else if (d == "Subtraction") {
			// Arithmetic Subtraction Operation:
			int rmin = 0; int rmax = 0;
			int gmin = 0; int gmax = 0;
			int bmin= 0; int bmax = 0;
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //subtraction can be seen via the -
					ImageArray[x][y][1]=ImageArray[x][y][1]-ImageArray2[x][y][1]; //r
					ImageArray[x][y][2]=ImageArray[x][y][2]-ImageArray2[x][y][2]; //g
					ImageArray[x][y][3]=ImageArray[x][y][3]-ImageArray2[x][y][3]; //b
					
					if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
					if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
				}	
			}
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //normalisation
					ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
					ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
					ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
				}
			}
			
		} else if (d == "Reverse Subtraction") {
			// Arithmetic Reverse Subtraction Operation:
			int rmin = 0; int rmax = 0;
			int gmin = 0; int gmax = 0;
			int bmin= 0; int bmax = 0;
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //reverse subtraction flips the variables around so you have more variety
					ImageArray[x][y][1]=ImageArray2[x][y][1]-ImageArray[x][y][1]; //r
					ImageArray[x][y][2]=ImageArray2[x][y][2]-ImageArray[x][y][2]; //g
					ImageArray[x][y][3]=ImageArray2[x][y][3]-ImageArray[x][y][3]; //b
					
					if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
					if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
				}	
			}
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //normalisation
					ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
					ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
					ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
				}
			}
			
		} else if (d == "Multiplication") {
			// Arithmetic Multiplication Operation:
			int rmin = 0; int rmax = 0;
			int gmin = 0; int gmax = 0;
			int bmin= 0; int bmax = 0;
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //multiplication can be seen via the *
					ImageArray[x][y][1]=ImageArray[x][y][1]*ImageArray2[x][y][1]; //r
					ImageArray[x][y][2]=ImageArray[x][y][2]*ImageArray2[x][y][2]; //g
					ImageArray[x][y][3]=ImageArray[x][y][3]*ImageArray2[x][y][3]; //b
					
					if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
					if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
					
				}	
			}
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //normalisation
					ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
					ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
					ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
				}
			}
			
		} else if (d == "Division") {
			// Arithmetic Division Operation:
			int rmin = 0; int rmax = 0;
			int gmin = 0; int gmax = 0;
			int bmin= 0; int bmax = 0;
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ 
					if (ImageArray2[x][y][1]==0) { ImageArray2[x][y][1] = 1; }
					if (ImageArray2[x][y][2]==0) { ImageArray2[x][y][2] = 1; }
					if (ImageArray2[x][y][3]==0) { ImageArray2[x][y][3] = 1; } //extra chunk of code to prevent division by 0 as it will cause errors
					//division can be seen via the /
					ImageArray[x][y][1]=ImageArray[x][y][1]/ImageArray2[x][y][1]; //r
					ImageArray[x][y][2]=ImageArray[x][y][2]/ImageArray2[x][y][2]; //g
					ImageArray[x][y][3]=ImageArray[x][y][3]/ImageArray2[x][y][3]; //b
					
					if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
					if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
					
				}	
			}
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //normalising
					ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
					ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
					ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
				}
			}
			
		} else if (d == "Reverse Division") {
			// Arithmetic Reverse Division Operation:
			int rmin = 0; int rmax = 0;
			int gmin = 0; int gmax = 0;
			int bmin= 0; int bmax = 0;
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){ //same as previous operation but images are flipped to give a greater variety of results
					if (ImageArray[x][y][1]==0) { ImageArray[x][y][1] = 1; }
					if (ImageArray[x][y][2]==0) { ImageArray[x][y][2] = 1; }
					if (ImageArray[x][y][3]==0) { ImageArray[x][y][3] = 1; } //extra chunk of code to prevent division by 0
					
					ImageArray[x][y][1]=ImageArray2[x][y][1]/ImageArray[x][y][1]; //r
					ImageArray[x][y][2]=ImageArray2[x][y][2]/ImageArray[x][y][2]; //g
					ImageArray[x][y][3]=ImageArray2[x][y][3]/ImageArray[x][y][3]; //b
					
					if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
					if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
					if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
					if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
					
				}	
			}
			
			for(int y=0; y<height; y++){
				for(int x =0; x<width; x++){
					ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
					ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
					ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
				}
			}
			
		}
			
		return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}

	public BufferedImage BitwiseNOT(BufferedImage timg){ //3.2
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(timg);		  //  Identical image array to map perfectly
		
		biPrevious = biFiltered; 
	
		// Image bitwise logical NOT Operation:
		
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r = ImageArray[x][y][1]; //r
				int g = ImageArray[x][y][2]; //g
				int b = ImageArray[x][y][3]; //b
				ImageArray2[x][y][1] = (~r)&0xFF; //r //basically NOT's the r,g,b values based on the 256 value capacity
				ImageArray2[x][y][2] = (~g)&0xFF; //g
				ImageArray2[x][y][3] = (~b)&0xFF; //b
			}
		}
        
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
	}
    
	public BufferedImage BooleanOperations(BufferedImage timg, BufferedImage timg2){ //3.3
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(timg2);		  //  Convert Second image to array
		//two different images required for this chunk of code
		biPrevious = biFiltered;

		String[] options = {"AND","OR","XOR"}; //options for the BITWISE
        String d = (String)JOptionPane.showInputDialog(null, "Pick Bitwise Operation", 
                "Bitwise Operations", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	
		if (d == "AND"){ //all similar to Bitwise NOT but slightly had to change the symbols
			// Image bitwise logical AND Operation:
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					int r1 = ImageArray[x][y][1]; //r
					int g1 = ImageArray[x][y][2]; //g
					int b1 = ImageArray[x][y][3]; //b
					int r2 = ImageArray2[x][y][1]; //r
					int g2 = ImageArray2[x][y][2]; //g
					int b2 = ImageArray2[x][y][3]; //b
					ImageArray2[x][y][1] = (r1&r2)&0xFF; //r
					ImageArray2[x][y][2] = (g1&g2)&0xFF; //g
					ImageArray2[x][y][3] = (b1&b2)&0xFF; //b
				}
			}
		} else if (d == "OR") {
			// Image bitwise logical OR Operation:
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					int r1 = ImageArray[x][y][1]; //r
					int g1 = ImageArray[x][y][2]; //g
					int b1 = ImageArray[x][y][3]; //b
					int r2 = ImageArray2[x][y][1]; //r
					int g2 = ImageArray2[x][y][2]; //g
					int b2 = ImageArray2[x][y][3]; //b
					ImageArray2[x][y][1] = (r1|r2)&0xFF; //r
					ImageArray2[x][y][2] = (g1|g2)&0xFF; //g
					ImageArray2[x][y][3] = (b1|b2)&0xFF; //b
				}
			}
		} else if (d == "XOR") {
			// Image bitwise logical XOR Operation:
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					int r1 = ImageArray[x][y][1]; //r
					int g1 = ImageArray[x][y][2]; //g
					int b1 = ImageArray[x][y][3]; //b
					int r2 = ImageArray2[x][y][1]; //r
					int g2 = ImageArray2[x][y][2]; //g
					int b2 = ImageArray2[x][y][3]; //b
					ImageArray2[x][y][1] = (r1^r2)&0xFF; //r
					ImageArray2[x][y][2] = (g1^g2)&0xFF; //g
					ImageArray2[x][y][3] = (b1^b2)&0xFF; //b
				}
			}
		}
        
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
	}
	
	public BufferedImage BiRoiOperations(BufferedImage timg, BufferedImage timg2){ //3.4 - NEEDS WORK
		int width = timg.getWidth();
        int height = timg.getHeight();
		
		int width2 = timg2.getWidth();
        int height2 = timg2.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(timg2);		  //  ROI mask image converted to an array
		int[][][] ImageArray3 = convertToArray(timg);          //  Third Image where edits will occur
		
		biPrevious = biFiltered; 
	
		// Image bitwise logical AND Operation:

		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r1 = ImageArray[x][y][1]; //r
				int g1 = ImageArray[x][y][2]; //g
				int b1 = ImageArray[x][y][3]; //b
				int r2 = ImageArray2[x][y][1]; //r
				int g2 = ImageArray2[x][y][2]; //g
				int b2 = ImageArray2[x][y][3]; //b
				ImageArray2[x][y][1] = (r1&r2)&0xFF; //r
				ImageArray2[x][y][2] = (g1&g2)&0xFF; //g
				ImageArray2[x][y][3] = (b1&b2)&0xFF; //b
			}
		}

        
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
	}
	
	public BufferedImage LutOperations(BufferedImage timg){ //4.1, 4.2, 4.3
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered; 
	
		String[] options = {"Negative Linear Transformation","Logarithmic Function","Power-Law Function"}; //options for LUT operations
        String d = (String)JOptionPane.showInputDialog(null, "Pick LUT Operation", 
                "LUT Operations", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);		
		
		
		if (d == "Negative Linear Transformation") {
			// Image negative linear transform Operation: 4.1
			//LUT negative linear (256 levels)
			int[] LUTneg = new int[256]; //creating the negative LUT and filling it up
			for(int k=0; k<=255; k++){
				LUTneg[k] = 256-1-k;
			}
				
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					int r = ImageArray[x][y][1]; //r
					int g = ImageArray[x][y][2]; //g
					int b = ImageArray[x][y][3]; //b
					ImageArray[x][y][1] = LUTneg[r]; //r
					ImageArray[x][y][2] = LUTneg[g]; //g
					ImageArray[x][y][3] = LUTneg[b]; //b
				}
			}//basically looks at the LUT table at the specific index and retrieves a certain value
			
		} else if (d == "Logarithmic Function") {
			// Image logarithmic function Operation: 4.2
			//LUT logarithmic (256 levels)
			int[] LUTlog = new int[256]; //creating the log LUT and filling it up
			for(int k=0; k<=255; k++){
				LUTlog[k] = (int)(Math.log(1+k)*255/Math.log(256));
			}
			
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					int r = ImageArray[x][y][1]; //r
					int g = ImageArray[x][y][2]; //g
					int b = ImageArray[x][y][3]; //b
					ImageArray[x][y][1] = LUTlog[r]; //r
					ImageArray[x][y][2] = LUTlog[g]; //g
					ImageArray[x][y][3] = LUTlog[b]; //b
				}
			}
			
		} else if (d == "Power-Law Function") {
			// Image power-law Operation: 4.3
			//LUT power-law (256 levels)
			
			Double[] fullrange = new Double[2500]; //getting all the possible values from 0.01 to 25.00
			double value = 0.01;
			for(int i=0; i<2499; i++){ 
				value = Math.round(value*100.0)/100.0; //math.round function is needed because it adds endless 9's for no reason
				fullrange[i] = value;
				value = value + 0.01;
			}
			
			//https://www.tutorialspoint.com/how-to-use-joptionpane-with-array-elements-in-java
			
			Double inputPL = (Double) JOptionPane.showInputDialog(null, "Choose a Power", "Power", 
						JOptionPane.PLAIN_MESSAGE, null, fullrange, fullrange[0]);
			
			double p = inputPL.doubleValue(); //Convert Double Object to Double Primitive value
			
			//System.out.println(VARIABLENAME.getClass().getSimpleName()); CAN BE USED TO CHECK VARIABLE DATA TYPE
						
			int[] LUTpl = new int[256];
			for(int k=0; k<=255; k++){ //creating the power-law LUT and filling it up
				LUTpl[k] = (int)(Math.pow(255,1-p)*Math.pow(k,p));
			}
			
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					int r = ImageArray[x][y][1]; //r
					int g = ImageArray[x][y][2]; //g
					int b = ImageArray[x][y][3]; //b
					ImageArray[x][y][1] = LUTpl[r]; //r
					ImageArray[x][y][2] = LUTpl[g]; //g
					ImageArray[x][y][3] = LUTpl[b]; //b
				}
			}
		}
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage LutRandom(BufferedImage timg){ //4.4
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered; 
	
		// Random Look-up table Operation: 4.4
		//LUT Random (256 levels)
		int[] LUTrand = new int[256];
		for(int k=0; k<=255; k++){ //random LUT table with all 256 items filled with random values
			Random random = new Random();
			LUTrand[k] = 256-random.nextInt(255);
		}
			
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r = ImageArray[x][y][1]; //r
				int g = ImageArray[x][y][2]; //g
				int b = ImageArray[x][y][3]; //b
				ImageArray[x][y][1] = LUTrand[r]; //r
				ImageArray[x][y][2] = LUTrand[g]; //g
				ImageArray[x][y][3] = LUTrand[b]; //b
			}
		}
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage BitPlaneSlicing(BufferedImage timg){ //4.5
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered; 
	
		Integer[] options = {0,1,2,3,4,5,6,7}; //options for bit plane k
        int d = (Integer)JOptionPane.showInputDialog(null, "Pick bit plane K Value", 
                "K-Value", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
   	
		int k = (int)d;
		
		int rmin = 0; int rmax = 0;
		int gmin = 0; int gmax = 0;
		int bmin= 0; int bmax = 0;
			
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int r = ImageArray[x][y][1]; //r
				int g = ImageArray[x][y][2]; //g
				int b = ImageArray[x][y][3]; //b
				ImageArray[x][y][1] = (r>>k)&1; //r
				ImageArray[x][y][2] = (g>>k)&1; //g
				ImageArray[x][y][3] = (b>>k)&1; //b

				if (ImageArray[x][y][1]<rmin) { rmin = ImageArray[x][y][1]; }
				if (ImageArray[x][y][2]<gmin) { gmin = ImageArray[x][y][2]; }
				if (ImageArray[x][y][3]<bmin) { bmin = ImageArray[x][y][3]; }
				if (ImageArray[x][y][1]>rmax) { rmax = ImageArray[x][y][1]; }
				if (ImageArray[x][y][2]>gmax) { gmax = ImageArray[x][y][2]; }
				if (ImageArray[x][y][3]>bmax) { bmax = ImageArray[x][y][3]; }
				//we needed to normalise, because we can barely see it if we do not
			}
		}
		
		for(int y=0; y<height; y++){
			for(int x =0; x<width; x++){ //normalisation
				ImageArray[x][y][1]=255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
				ImageArray[x][y][2]=255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
				ImageArray[x][y][3]=255*(ImageArray[x][y][3]-bmin)/(bmax-bmin);
			}
		}
		
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage Histograms(BufferedImage timg){ //5.1, 5.2, 5.3
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered; 
			
		//Finding Histogram: //5.1
		// To construct the histograms for RGB components of an image
		int[] HistgramR = new int[256];
		int[] HistgramG = new int[256];
		int[] HistgramB = new int[256];
		
		for(int k=0; k<256; k++){ // Initialisation
			HistgramR[k] = 0;
			HistgramG[k] = 0;
			HistgramB[k] = 0;
		}
		
		for(int y=0; y<height; y++){ // bin histograms
			for(int x=0; x<width; x++){
				int r = ImageArray[x][y][1]; //r
				int g = ImageArray[x][y][2]; //g
				int b = ImageArray[x][y][3]; //b
				HistgramR[r]++;
				HistgramG[g]++;
				HistgramB[b]++;
			}
		}
		
		//Histogram Normalisation: //5.2
		float[] nHistgramR = new float[256]; //has to be float (also float maps with int quite well - long is used with double) because we are not always dividing by factors
		float[] nHistgramG = new float[256];
		float[] nHistgramB = new float[256];
		
		for(int k=0; k<256; k++){ // Normalisation
			nHistgramR[k] = (float)HistgramR[k]/(height*width); // r //float cast to divide float value by integer
			nHistgramG[k] = (float)HistgramG[k]/(height*width); // g
			nHistgramB[k] = (float)HistgramB[k]/(height*width); // b
		}
		
		//Histogram Equalisation: //5.3 - Watch lecture video
		float[] cdHistgramR = new float[256]; 
		float[] cdHistgramG = new float[256];
		float[] cdHistgramB = new float[256];
		
		for(int k=0; k<256; k++){ //works out the cumulative distribution for whole array
			if(k==0){
				cdHistgramR[k] = nHistgramR[k];
				cdHistgramG[k] = nHistgramG[k];
				cdHistgramB[k] = nHistgramB[k];
			}
			else{
				cdHistgramR[k] = cdHistgramR[k-1]+nHistgramR[k];
				cdHistgramG[k] = cdHistgramG[k-1]+nHistgramG[k];
				cdHistgramB[k] = cdHistgramB[k-1]+nHistgramB[k];
			}
		}
	
		int[] cdroundedHistgramR = new int[256]; 
		int[] cdroundedHistgramG = new int[256];
		int[] cdroundedHistgramB = new int[256];
		
		for(int k=0; k<256; k++){ //works out the cumulative distribution multipled by the max value and rounding to an integer
			cdroundedHistgramR[k] = Math.round(cdHistgramR[k]*255);
			cdroundedHistgramG[k] = Math.round(cdHistgramG[k]*255);
			cdroundedHistgramB[k] = Math.round(cdHistgramB[k]*255);
		}
		
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){	
				ImageArray[x][y][1] = cdroundedHistgramR[ImageArray[x][y][1]];
				ImageArray[x][y][2] = cdroundedHistgramG[ImageArray[x][y][2]];
				ImageArray[x][y][3] = cdroundedHistgramB[ImageArray[x][y][3]];
			}
		}
		
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage ImageConvolution(BufferedImage timg){ //6
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(timg);		  //  Second array needed to host the filtered image
		
		biPrevious = biFiltered; 
		
		String[] options = {"Averaging","Weighted Averaging","4-Neighbour Laplacian", "8-Neighbour Laplacian",
							"4-Neighbour Laplacian Enhancement", "8-Neighbour Laplacian Enhancement",
							"Roberts First Mask", "Roberts Second Mask", "Sobel X", "Sobel Y", "Gaussian", 
							"Laplacian of Gaussian (LoG)"}; //options for the different masks
        String d = (String)JOptionPane.showInputDialog(null, "Pick Mask", 
                "Convulation Masks", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		if (d == "Averaging"){			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= 1;
			Mask[0][1]= 1;
			Mask[0][2]= 1;
			
			Mask[1][0]= 1;
			Mask[1][1]= 1;
			Mask[1][2]= 1;
			
			Mask[2][0]= 1;
			Mask[2][1]= 1;
			Mask[2][2]= 1;
			
			float numerator = 1;
			float denominator = 0;
			
			for (int row=0; row < Mask.length; row++){ //for loop which works out the denomitator by adding up all the values from the mask
				for (int col=0; col < Mask[row].length; col++){
					denominator = denominator + Mask[row][col];
				}
			}
			
			float multiplier = numerator/denominator;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){ //reason for why it starts at 1 is to prevent errors as the mask should not be able to go out of bounds
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					r = Math.round(r*multiplier); g = Math.round(g*multiplier); b = Math.round(b*multiplier);
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
			}
			
			
		} else if (d == "Weighted Averaging"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= 1;
			Mask[0][1]= 2;
			Mask[0][2]= 1;
			
			Mask[1][0]= 2;
			Mask[1][1]= 4;
			Mask[1][2]= 2;
			
			Mask[2][0]= 1;
			Mask[2][1]= 2;
			Mask[2][2]= 1;
			
			float numerator = 1;
			float denominator = 0;
			
			for (int row=0; row < Mask.length; row++){
				for (int col=0; col < Mask[row].length; col++){
					denominator = denominator + Mask[row][col];
				}
			}
			
			float multiplier = numerator/denominator;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					r = Math.round(r*multiplier); g = Math.round(g*multiplier); b = Math.round(b*multiplier);
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
			}
		
			
		} else if (d == "4-Neighbour Laplacian"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= 0;
			Mask[0][1]= -1;
			Mask[0][2]= 0;
			
			Mask[1][0]= -1;
			Mask[1][1]= 4;
			Mask[1][2]= -1;
			
			Mask[2][0]= 0;
			Mask[2][1]= -1;
			Mask[2][2]= 0;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
			
		} else if (d == "8-Neighbour Laplacian"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= -1;
			Mask[0][1]= -1;
			Mask[0][2]= -1;
			
			Mask[1][0]= -1;
			Mask[1][1]= 8;
			Mask[1][2]= -1;
			
			Mask[2][0]= -1;
			Mask[2][1]= -1;
			Mask[2][2]= -1;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
			
		} else if (d == "4-Neighbour Laplacian Enhancement"){
						
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= 0;
			Mask[0][1]= -1;
			Mask[0][2]= 0;
			
			Mask[1][0]= -1;
			Mask[1][1]= 5;
			Mask[1][2]= -1;
			
			Mask[2][0]= 0;
			Mask[2][1]= -1;
			Mask[2][2]= 0;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
			
			
		} else if (d == "8-Neighbour Laplacian Enhancement"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= -1;
			Mask[0][1]= -1;
			Mask[0][2]= -1;
			
			Mask[1][0]= -1;
			Mask[1][1]= 9;
			Mask[1][2]= -1;
			
			Mask[2][0]= -1;
			Mask[2][1]= -1;
			Mask[2][2]= -1;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
			
		} else if (d == "Roberts First Mask"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= 0;
			Mask[0][1]= 0;
			Mask[0][2]= 0;
			
			Mask[1][0]= 0;
			Mask[1][1]= 0;
			Mask[1][2]= -1;
			
			Mask[2][0]= 0;
			Mask[2][1]= 1;
			Mask[2][2]= 0;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
		} else if (d == "Roberts Second Mask"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= 0;
			Mask[0][1]= 0;
			Mask[0][2]= 0;
			
			Mask[1][0]= 0;
			Mask[1][1]= -1;
			Mask[1][2]= 0;
			
			Mask[2][0]= 0;
			Mask[2][1]= 0;
			Mask[2][2]= 1;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
			
		} else if (d == "Sobel X"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= -1;
			Mask[0][1]= 0;
			Mask[0][2]= 1;
			
			Mask[1][0]= -2;
			Mask[1][1]= 0;
			Mask[1][2]= 2;
			
			Mask[2][0]= -1;
			Mask[2][1]= 0;
			Mask[2][2]= 1;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
		} else if (d == "Sobel Y"){
			
			//2D array for mask
			int[][] Mask = new int[3][3];
			Mask[0][0]= -1;
			Mask[0][1]= -2;
			Mask[0][2]= -1;
			
			Mask[1][0]= 0;
			Mask[1][1]= 0;
			Mask[1][2]= 0;
			
			Mask[2][0]= 1;
			Mask[2][1]= 2;
			Mask[2][2]= 1;
			
			// for Mask of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
			
		} else if (d == "Gaussian"){
			
			//2D array for mask
			int[][] Mask = new int[5][5];
			Mask[0][0]= 1;
			Mask[0][1]= 4;
			Mask[0][2]= 7;
			Mask[0][3]= 4;
			Mask[0][4]= 1;
			
			Mask[1][0]= 4;
			Mask[1][1]= 16;
			Mask[1][2]= 26;
			Mask[1][3]= 16;
			Mask[1][4]= 4;
			
			Mask[2][0]= 7;
			Mask[2][1]= 26;
			Mask[2][2]= 41;
			Mask[2][3]= 26;
			Mask[2][4]= 7;
			
			Mask[3][0]= 4;
			Mask[3][1]= 16;
			Mask[3][2]= 26;
			Mask[3][3]= 16;
			Mask[3][4]= 4;
			
			Mask[4][0]= 1;
			Mask[4][1]= 4;
			Mask[4][2]= 7;
			Mask[4][3]= 4;
			Mask[4][4]= 1;
			
			float numerator = 1;
			float denominator = 0;
			
			for (int row=0; row < Mask.length; row++){
				for (int col=0; col < Mask[row].length; col++){
					denominator = denominator + Mask[row][col];
				}
			}
			
			float multiplier = numerator/denominator;
			
			// for Mask of size 5x5, no border extension
			for(int y=2; y<height-2; y++){
				for(int x=2; x<width-2; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					r = Math.round(r*multiplier); g = Math.round(g*multiplier); b = Math.round(b*multiplier);
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
			}
			
			
		} else if (d == "Laplacian of Gaussian (LoG)"){
		
			//2D array for mask
			int[][] Mask = new int[5][5];
			Mask[0][0]= 0;
			Mask[0][1]= 0;
			Mask[0][2]= -1;
			Mask[0][3]= 0;
			Mask[0][4]= 0;
			
			Mask[1][0]= 0;
			Mask[1][1]= -1;
			Mask[1][2]= -2;
			Mask[1][3]= -1;
			Mask[1][4]= 0;
			
			Mask[2][0]= -1;
			Mask[2][1]= -2;
			Mask[2][2]= 16;
			Mask[2][3]= -2;
			Mask[2][4]= -1;
			
			Mask[3][0]= 0;
			Mask[3][1]= -1;
			Mask[3][2]= -2;
			Mask[3][3]= -1;
			Mask[3][4]= 0;
			
			Mask[4][0]= 0;
			Mask[4][1]= 0;
			Mask[4][2]= -1;
			Mask[4][3]= 0;
			Mask[4][4]= 0;
			
			// for Mask of size 5x5, no border extension
			for(int y=2; y<height-2; y++){
				for(int x=2; x<width-2; x++){
					int r = 0; int g = 0; int b = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							r = r + Mask[1-s][1-t]*ImageArray[x+s][y+t][1]; //r
							g = g + Mask[1-s][1-t]*ImageArray[x+s][y+t][2]; //g
							b = b + Mask[1-s][1-t]*ImageArray[x+s][y+t][3]; //b
						}
					}
					
					ImageArray2[x][y][1] = r; //r
					ImageArray2[x][y][2] = g; //g
					ImageArray2[x][y][3] = b; //b
					
					if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
					if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
					if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
					if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
					if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
					if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
				}
				
			}
			
		}
		
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
	}
	
	public BufferedImage SaltNPepper(BufferedImage timg){ //7.1
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
	
		// Image Noise Operation:
		Random random = new Random();
		
		int arrSize = random.nextInt(width*height); // using total pixels to generate a random value up until that range which will then determine the array size
		int[] widthArray = new int[arrSize];
		int[] heightArray = new int[arrSize]; // arrays which will map the different locations where this will be applied
		
		for(int x=0; x<arrSize; x++){ // this will fill up the array with the different values
			widthArray[x] = random.nextInt(width);
			heightArray[x] = random.nextInt(height);
		}
		
		for(int x=0; x<arrSize; x++){ //this will be used to either give each position a salt, pepper or default value
			int randomNum = random.nextInt(300);
			
			if(randomNum <= 100){
				ImageArray[widthArray[x]][heightArray[x]][1] = 255;
				ImageArray[widthArray[x]][heightArray[x]][2] = 255;
				ImageArray[widthArray[x]][heightArray[x]][3] = 255;
			} else if(randomNum >= 200){
				ImageArray[widthArray[x]][heightArray[x]][1] = 0;
				ImageArray[widthArray[x]][heightArray[x]][2] = 0;
				ImageArray[widthArray[x]][heightArray[x]][3] = 0;
			}
		}
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage Filtering(BufferedImage timg){ //7.2, 7.3, 7.4, 7.5
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		int[][][] ImageArray2 = convertToArray(timg);          //  Convert the image to second array
		
		biPrevious = biFiltered;
	
		String[] options = {"Min Filtering", "Max Filtering", "Midpoint Filtering", "Median Filtering"
		}; //options for the different masks
        String d = (String)JOptionPane.showInputDialog(null, "Pick Mask", 
                "Convulation Masks", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		
		int[] rWindow = new int[9];
		int[] gWindow = new int[9];
		int[] bWindow = new int[9];
		
		if(d == "Min Filtering"){ //7.2
			// Minimum Filtering Operation:
			// for Window of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int k = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							rWindow[k] = ImageArray[x+s][y+t][1]; //r
							gWindow[k] = ImageArray[x+s][y+t][2]; //g
							bWindow[k] = ImageArray[x+s][y+t][3]; //b
							k++;
						}
					}
					Arrays.sort(rWindow);
					Arrays.sort(gWindow);
					Arrays.sort(bWindow);
					
					ImageArray2[x][y][1] = rWindow[0]; //r
					ImageArray2[x][y][2] = gWindow[0]; //g
					ImageArray2[x][y][3] = bWindow[0]; //b
				} //minimum value retrieved by smallest index of sorted window array
			}
			
		} else if(d == "Max Filtering"){ //7.3
			// Maximum Filtering Operation:
			// for Window of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int k = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							rWindow[k] = ImageArray[x+s][y+t][1]; //r
							gWindow[k] = ImageArray[x+s][y+t][2]; //g
							bWindow[k] = ImageArray[x+s][y+t][3]; //b
							k++;
						}
					}
					Arrays.sort(rWindow);
					Arrays.sort(gWindow);
					Arrays.sort(bWindow);
					
					ImageArray2[x][y][1] = rWindow[rWindow.length-1]; //r
					ImageArray2[x][y][2] = gWindow[gWindow.length-1]; //g
					ImageArray2[x][y][3] = bWindow[bWindow.length-1]; //b
				} //maximum value retrieved by largest index of sorted window array
			}
			
		} else if(d == "Midpoint Filtering"){ //7.4
			// Midpoint Filtering Operation:
			// for Window of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int k = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							rWindow[k] = ImageArray[x+s][y+t][1]; //r
							gWindow[k] = ImageArray[x+s][y+t][2]; //g
							bWindow[k] = ImageArray[x+s][y+t][3]; //b
							k++;
						}
					}
					Arrays.sort(rWindow);
					Arrays.sort(gWindow);
					Arrays.sort(bWindow);

					ImageArray2[x][y][1] = Math.round((rWindow[rWindow.length-1]+rWindow[0])/2); //r
					ImageArray2[x][y][2] = Math.round((gWindow[gWindow.length-1]+gWindow[0])/2); //g
					ImageArray2[x][y][3] = Math.round((bWindow[bWindow.length-1]+bWindow[0])/2); //b
				} //midpoint value retrieved by middle index of sorted window array
			}
			
		} else if(d == "Median Filtering"){ //7.5
			// Median Filtering Operation:
			// for Window of size 3x3, no border extension
			for(int y=1; y<height-1; y++){
				for(int x=1; x<width-1; x++){
					int k = 0;
					for(int s=-1; s<=1; s++){
						for(int t=-1; t<=1; t++){
							rWindow[k] = ImageArray[x+s][y+t][1]; //r
							gWindow[k] = ImageArray[x+s][y+t][2]; //g
							bWindow[k] = ImageArray[x+s][y+t][3]; //b
							k++;
						}
					}
					Arrays.sort(rWindow);
					Arrays.sort(gWindow);
					Arrays.sort(bWindow);

					ImageArray2[x][y][1] = rWindow[4]; //r
					ImageArray2[x][y][2] = gWindow[4]; //g
					ImageArray2[x][y][3] = bWindow[4]; //b
				}
			}
			
		}
        
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
	}
	
	public BufferedImage MSD(BufferedImage timg){ //8.1
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
	
		//Finding Histogram: //5.1
		// To construct the histograms for RGB components of an image
		int[] HistgramR = new int[256];
		int[] HistgramG = new int[256];
		int[] HistgramB = new int[256];
		
		for(int k=0; k<256; k++){ // Initialisation
			HistgramR[k] = 0;
			HistgramG[k] = 0;
			HistgramB[k] = 0;
		}
		
		for(int y=0; y<height; y++){ // bin histograms
			for(int x=0; x<width; x++){
				int r = ImageArray[x][y][1]; //r
				int g = ImageArray[x][y][2]; //g
				int b = ImageArray[x][y][3]; //b
				HistgramR[r]++;
				HistgramG[g]++;
				HistgramB[b]++;
			}
		}
		
		//Histogram Normalisation: //5.2
		float[] nHistgramR = new float[256]; //has to be float (also float maps with int quite well - long is used with double) because we are not always dividing by factors
		float[] nHistgramG = new float[256];
		float[] nHistgramB = new float[256];
		
		//https://www.statisticshowto.com/probability-and-statistics/binomial-theorem/find-the-mean-of-the-probability-distribution-binomial/ - How to find mean of a probability distribution
		
		float accMeanR = 0;
		float accMeanG = 0;
		float accMeanB = 0;
		
		for(int k=0; k<256; k++){ // Normalisation
			nHistgramR[k] = (float)HistgramR[k]/(height*width); // r //float cast to divide float value by integer
			nHistgramG[k] = (float)HistgramG[k]/(height*width); // g
			nHistgramB[k] = (float)HistgramB[k]/(height*width); // b
			
			accMeanR = accMeanR + (nHistgramR[k] * k);
			accMeanG = accMeanG + (nHistgramG[k] * k);
			accMeanB = accMeanB + (nHistgramB[k] * k);
		}
		
		String str1 = String.format("Mean R Value: %.3f \r\n", accMeanR); 
		String str2 = String.format("Mean G Value: %.3f \r\n", accMeanG);
		String str3 = String.format("Mean B Value: %.3f \r\n", accMeanB);
		
		//https://www.statology.org/standard-deviation-of-probability-distribution/ - How to find standard deviation of a probability distribution
		
		float accSdR = 0;
		float accSdG = 0;
		float accSdB = 0;
		
		for(int k=0; k<256; k++){
			accSdR = accSdR + (float)(Math.pow((k-accMeanR),2)*nHistgramR[k]);
			accSdG = accSdG + (float)(Math.pow((k-accMeanG),2)*nHistgramG[k]);
			accSdB = accSdB + (float)(Math.pow((k-accMeanB),2)*nHistgramB[k]);
		}
		
		accSdR = (float)Math.sqrt(accSdR);
		accSdG = (float)Math.sqrt(accSdG);
		accSdB = (float)Math.sqrt(accSdB);
		
		String str4 = String.format("Standard Deviation R Value: %.3f \r\n", accSdR);
		String str5 = String.format("Standard Deviation G Value: %.3f \r\n", accSdG);
		String str6 = String.format("Standard Deviation B Value: %.3f \r\n", accSdB);
		
		JOptionPane.showMessageDialog(null, str1 + str2 + str3 + str4 + str5 + str6);
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage SimpleThresholding(BufferedImage timg){ //8.2
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
	
		// Simple Thresholding: //8.2
		
		Integer[] fullrange = new Integer[256]; //getting all the possible values from 0 to 255
		int value = 0;
		for(int i=0; i<256; i++){
			fullrange[i] = value;
			value++;
		}
		
		Integer inputPL = (Integer) JOptionPane.showInputDialog(null, "Choose a Threshold", "Threshold", 
					JOptionPane.PLAIN_MESSAGE, null, fullrange, fullrange[0]);
		
		int t = inputPL.intValue(); //Convert Integer Object to int Primitive value
		
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
			//becomes binary image in the sense that the outcomes of all if statements are to both extremes
				if(ImageArray[x][y][1] <= t) { ImageArray[x][y][1] = 0; }
				if(ImageArray[x][y][2] <= t) { ImageArray[x][y][2] = 0; }
				if(ImageArray[x][y][3] <= t) { ImageArray[x][y][3] = 0; }
				if(ImageArray[x][y][1] > t) { ImageArray[x][y][1] = 255; }
				if(ImageArray[x][y][2] > t) { ImageArray[x][y][2] = 255; }
				if(ImageArray[x][y][3] > t) { ImageArray[x][y][3] = 255; }
			//or did they want something like this?
				/*if(ImageArray[x][y][1] <= t) { ImageArray[x][y][1] = 0;ImageArray[x][y][2] = 0;ImageArray[x][y][3] = 0; }
				if(ImageArray[x][y][1] > t) { ImageArray[x][y][1] = 255;ImageArray[x][y][2] = 255;ImageArray[x][y][3] = 255; }*/
			}
		}
		
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	public BufferedImage AutomatedThresholding(BufferedImage timg){ //8.3
		int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		
		biPrevious = biFiltered;
		
		List<Integer> allThresholdRecords = new ArrayList<>(); //Empty array which will hold all of the threshold values
		
		// Initial Threshold: //8.3
		int initialRthresholdCorner = 0; int initialGthresholdCorner = 0; int initialBthresholdCorner = 0;
		int initialRthresholdRest = 0; int initialGthresholdRest = 0; int initialBthresholdRest = 0;
		
		for(int y=0; y<height; y++){ //going through all the pixels and accumulating the R,G,B values
			for(int x=0; x<width; x++){
				if((y == 0)&(x == 0)) { initialRthresholdCorner = initialRthresholdCorner + ImageArray[x][y][1]; initialGthresholdCorner = initialGthresholdCorner + ImageArray[x][y][2]; initialBthresholdCorner = initialBthresholdCorner + ImageArray[x][y][3]; } //these if statements are for the corners to help find the background average
				if((y == 0)&(x == width-1)) { initialRthresholdCorner = initialRthresholdCorner + ImageArray[x][y][1]; initialGthresholdCorner = initialGthresholdCorner + ImageArray[x][y][2]; initialBthresholdCorner = initialBthresholdCorner + ImageArray[x][y][3]; }
				if((y == height-1)&(x == 0)) { initialRthresholdCorner = initialRthresholdCorner + ImageArray[x][y][1]; initialGthresholdCorner = initialGthresholdCorner + ImageArray[x][y][2]; initialBthresholdCorner = initialBthresholdCorner + ImageArray[x][y][3]; }
				if((y == height-1)&(x == width-1)) { initialRthresholdCorner = initialRthresholdCorner + ImageArray[x][y][1]; initialGthresholdCorner = initialGthresholdCorner + ImageArray[x][y][2]; initialBthresholdCorner = initialBthresholdCorner + ImageArray[x][y][3]; }
					
				else {
					initialRthresholdRest = initialRthresholdRest + ImageArray[x][y][1];
					initialGthresholdRest = initialGthresholdRest + ImageArray[x][y][2];
					initialBthresholdRest = initialBthresholdRest + ImageArray[x][y][3];
				}
			}
		}
		int initialAvgRthresholdC = Math.round(initialRthresholdCorner/4);
		int initialAvgGthresholdC = Math.round(initialGthresholdCorner/4);
		int initialAvgBthresholdC = Math.round(initialBthresholdCorner/4);
		int initialAvgRthresholdR = Math.round(initialRthresholdRest/((height*width)-4));
		int initialAvgGthresholdR = Math.round(initialGthresholdRest/((height*width)-4));
		int initialAvgBthresholdR = Math.round(initialBthresholdRest/((height*width)-4));
		
		int midpointITr = Math.round((initialAvgRthresholdC + initialAvgRthresholdR)/2); 
		int midpointITg = Math.round((initialAvgGthresholdC + initialAvgGthresholdR)/2);
		int midpointITb = Math.round((initialAvgBthresholdC + initialAvgBthresholdR)/2);
		
		int initialThreshold = Math.round(midpointITr + midpointITg + midpointITb);
		
		//finding an initial threshold value by adding all R,G,B values and finding an average...not entirely sure if correct
		
		allThresholdRecords.add(0);
		allThresholdRecords.add(initialThreshold);
		//putting this initial threshold value in the array along with a default number at the beginning (needed for while loop later on to prevent any errors)
		
		
		// Automated Thresholding in an Iterative manner: (will start to loop here)
		int firstcornerR = 0; int secondcornerR = 0;
		int firstcornerG = 0; int secondcornerG = 0;
		int firstcornerB = 0; int secondcornerB = 0;
		
		int thirdcornerR = 0; int fourthcornerR = 0;
		int thirdcornerG = 0; int fourthcornerG = 0;
		int thirdcornerB = 0; int fourthcornerB = 0;
		
		int rTotal = 0; int gTotal = 0; int bTotal = 0;
		
		while(Math.abs(allThresholdRecords.get(allThresholdRecords.size()-1) - allThresholdRecords.get(allThresholdRecords.size()-2)) >= allThresholdRecords.get(1)){ //the final checks which are needed for iterative optimal threshold selection are set as the conditions for the while loop
		
			for(int y=0; y<height; y++){ // STEP 1 for loop scans through image and notes the RGB values for both the background and the rest of the image
				for(int x=0; x<width; x++){ //as mentioned earlier, background derived from corners and focus of image is derived from all other pixels
					if((y == 0)&(x == 0)) { firstcornerR = ImageArray[x][y][1]; firstcornerG = ImageArray[x][y][2]; firstcornerB = ImageArray[x][y][3]; } //these if statements are for the corners to help find the background average
					if((y == 0)&(x == width-1)) { secondcornerR = ImageArray[x][y][1]; secondcornerG = ImageArray[x][y][2]; secondcornerB = ImageArray[x][y][3]; }
					if((y == height-1)&(x == 0)) { thirdcornerR = ImageArray[x][y][1]; thirdcornerG = ImageArray[x][y][2]; thirdcornerB = ImageArray[x][y][3]; }
					if((y == height-1)&(x == width-1)) { fourthcornerR = ImageArray[x][y][1]; fourthcornerG = ImageArray[x][y][2]; fourthcornerB = ImageArray[x][y][3]; }
					else {
						rTotal = rTotal + ImageArray[x][y][1]; //this inner else function is for the main section of the image and to find the average here
						gTotal = gTotal + ImageArray[x][y][2];
						bTotal = bTotal + ImageArray[x][y][3];
					}
				}
			}
			int allcornerRavg = Math.round((firstcornerR + secondcornerR + thirdcornerR + fourthcornerR)/4); //all averages for corners as well as main focus of image are calculated here
			int allcornerGavg = Math.round((firstcornerG + secondcornerG + thirdcornerG + fourthcornerG)/4);
			int allcornerBavg = Math.round((firstcornerB + secondcornerB + thirdcornerB + fourthcornerB)/4);
			int rTotalavg = rTotal/((height*width)-4);
			int gTotalavg = gTotal/((height*width)-4);
			int bTotalavg = bTotal/((height*width)-4);
			
			int midpointTr = Math.round((allcornerRavg + rTotalavg)/2); // STEP 2
			int midpointTg = Math.round((allcornerGavg + gTotalavg)/2);
			int midpointTb = Math.round((allcornerBavg + bTotalavg)/2);
			int Thresholdval = Math.round((midpointTr + midpointTg + midpointTb)/3); //this is a bit weird because we are operating on a coloured image so we have to use 3 values instead of one...NOT SURE IF CORRECT
			//threshold value derived from all the averages of the R,G,B values we have gathered and used to find one threshold value
			allThresholdRecords.add(Thresholdval); //added to array
			
			for(int y=0; y<height; y++){ // STEP 3 thresholding
				for(int x=0; x<width; x++){
				//becomes binary image in the sense that the outcomes of all if statements are to both extremes
					if(ImageArray[x][y][1] <= Thresholdval) { ImageArray[x][y][1] = 0; }
					if(ImageArray[x][y][2] <= Thresholdval) { ImageArray[x][y][2] = 0; }
					if(ImageArray[x][y][3] <= Thresholdval) { ImageArray[x][y][3] = 0; }
					if(ImageArray[x][y][1] > Thresholdval) { ImageArray[x][y][1] = 255; }
					if(ImageArray[x][y][2] > Thresholdval) { ImageArray[x][y][2] = 255; }
					if(ImageArray[x][y][3] > Thresholdval) { ImageArray[x][y][3] = 255; }
				}
			}
		
		}
		
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	
	public BufferedImage AdditionalOperation(BufferedImage timg){ //Additional Function to help check things out since I put multiple in If-Else statements within the same function
        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
		biPrevious = biFiltered;
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
	}
	
	//************************************
    //  You need to register your function here
    //************************************
    public void filterImage() {
 
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
        case 0: biPrevious = biFiltered; biFiltered = bi; /* original */
                return;
        case 1: biFiltered = PreviousImage(biPrevious); //undo function
				return;
		case 2: biFiltered = ImageNegative(biFiltered); /* Image Negative */
                return;
		case 3: biFiltered = ImageRescaling(biFiltered); /* Image Rescaling */
                return;
		case 4: biFiltered = ImageShifting(biFiltered); /* Image Shifting */
                return;
		case 5: biFiltered = ImageShiftAndRescale(biFiltered); /* Image Shifting and Rescaling */
                return;
		case 6: biFiltered = TwoImages(biFiltered, biArithmetic); /* Two Image Arithmetic Operations */
                return;	
		case 7: biFiltered = BitwiseNOT(biFiltered); /* Bitwise NOT of an image */
                return;
		case 8: biFiltered = BooleanOperations(biFiltered, biArithmetic); /* Bitwise Other Operations */
                return;
		case 9: biFiltered = BiRoiOperations(biFiltered, biROI); /* ROI based operations */
                return;	
		case 10: biFiltered = LutOperations(biFiltered); /* All three LUT functions */
                return;	
		case 11: biFiltered = LutRandom(biFiltered); /* Random LUT transformation */
                return;
		case 12: biFiltered = BitPlaneSlicing(biFiltered); /* Random LUT transformation */
                return;
		case 13: biFiltered = Histograms(biFiltered); /* Histogram Operations */
                return;
		case 14: biFiltered = ImageConvolution(biFiltered); /* Image Correlation Function */
                return;
		case 15: biFiltered = SaltNPepper(biFiltered); /* Salt and Pepper Noise Operation */
                return;
		case 16: biFiltered = Filtering(biFiltered); /* Filtering Operations */
                return;
		case 17: biFiltered = MSD(biFiltered); /* Mean and Standard Deviation */
                return;
		case 18: biFiltered = SimpleThresholding(biFiltered); /* Simple Thresholding Operation */
                return;
		case 19: biFiltered = AutomatedThresholding(biFiltered); /* Automated Thresholding Operation */
                return;
		case 20: biFiltered = AdditionalOperation(biFiltered); /* Additional Operation Function */
                return;
        //************************************
        // case 2:
        //      return;
        //************************************

        }
 
    }
 
	 
    public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		if (cb.getActionCommand().equals("SetFilter")) {
			setOpIndex(cb.getSelectedIndex());
            repaint();
        } else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        } 
    };
 
    public static void main(String s[]) {
		
		//Original Window
		JFrame f = new JFrame("Image Processing Demo");
		f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
		
        IPCW de = new IPCW();
        f.add("Center", de);

        /*JMenuBar tabbar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		tabbar.add(menu);
		JMenuItem undo = new JMenuItem("Undo");
		menu.add(undo);
		undo.setActionCommand("UndoAction");
		undo.addActionListener(de);*/
			
		JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
		
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
		
        JPanel panel = new JPanel();
		//panel.add(tabbar);
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
		
        f.add("North", panel);
        f.pack();
    }
}