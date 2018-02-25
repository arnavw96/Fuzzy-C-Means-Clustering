import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;

import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Vector;


/* Main Class For This Module (Module-22; Arnav & Rachna's Contribution) */

class ARSatImage extends JFrame implements ActionListener
{
        JMenu mnEnhance;
		JMenuItem miOpen; //Under files
		JMenuItem miExit; //Under Files
        JMenuItem mifuzzy;  //Under Fuzzy Classifier
        JMenuItem fuzzy;

        public static String name;
        BufferedImage img;
        String ImageName; 
        DemoPanel pane;
        AboutDialog dialog = null;
		Container ContentPane = getContentPane();

	public static void main(String s[])
	{
		ARSatImage frame = new ARSatImage();
		frame.setTitle("Fuzzy C-Means Clustering for Satellite Imagery");
		frame.setVisible(true);
		frame.setLocation(100,100);
		frame.setSize(800, 550);
		frame.show();
	}
	
	ARSatImage()
	{
			JMenuBar mb = new JMenuBar();
			JMenu mnfile = new JMenu("File"); 
            JMenu mnEnhance = new JMenu("Classification");
			miOpen = new JMenuItem("Open");
			miExit = new JMenuItem("Exit");
            mifuzzy = new JMenuItem("Fuzzy Classifier");
            mnEnhance.add(mifuzzy);
			mnfile.add(miOpen);
			mnfile.add(miExit);
			mb.add(mnfile);
			mb.add(mnEnhance);
			setJMenuBar(mb);
			miOpen.addActionListener(this);
			miExit.addActionListener(this);
            mifuzzy.addActionListener(this);
            addWindowListener (new WindowAdapter()
							{
							public void windowClosing(WindowEvent we)
								{ System.exit(0); }
							});
	}

	public static File imgFile;
	public static int p,q,r;
	public static int tab = 0;

	public void actionPerformed(ActionEvent ae)
	{
		Object source = ae.getSource();

		if (source == miOpen)
		{
			JFileChooser chooser = new JFileChooser();
			int r5 = chooser.showOpenDialog(this);
			if (r5 == chooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				imgFile = file;
                name = file.getName();
				ReadImageData Rid1 = new ReadImageData(file);
                this.ImageName = name;
				picture Picture1 = new picture();
                picture.setimage(name);
				p=1;q=1;r=1;
				img = Picture1.getpicdata(p, q, r);
				DemoPanel pane = new DemoPanel(img);
				ContentPane.removeAll();
				ContentPane.add(new JScrollPane(pane));
                System.gc();
				validate();
			}
		}

		if (source == miExit)
		{
			System.exit(0);
		}
        
		if (source == mifuzzy)
		{
			System.out.println("FCM Implementation Started");
	    		Fcm fcm = new Fcm();
		}
	}
}
/* Main Class Ends Here */


/* Class Read Image Data To Read The Header File of Image. */

class ReadImageData
{
	public static File picfile;
	public static int Bands;
	private static int Rows;
	private static int Columns;

	public ReadImageData()
	{}

	public ReadImageData(File file)
	{
		String name = file.getName();
		this.picfile = file;
	}

	public void read(File Name)//read image file name & header file data
	{
		int index = 0;
		int b1 = 0;
		int row = 0;
		int col = 0;
		int x = 0;
		int c[];
		InputStream f1;
		char p[] = new char[42];
		String ban = new String();
		String row1 = new String();
		String col1 = new String();
		this.picfile = Name;
		String s[] = new String[30];
		try
		{
			f1 = new FileInputStream(Name + ".hdr");
			c = new int[11];
			for (int i = 0; i < 42; i = i + 1)
			{	p[i] = (char)f1.read();	}
			f1.close();
		}
		catch (Exception e)
		{}

		for (int i = 6; i < 13; i = i + 1)
		{
			if (p[i] == '\n') break;
			if (p[i] == ' ') p[i] = '0';
			ban = ban + p[i];
		}
		
		b1 = Integer.parseInt(ban);
		this.Bands = b1;
		//System.out.println(b1);
		for (int i = 20; i < 27; i = i + 1)
		{
			if (p[i] == '\n') break;
			if (p[i] == ' ') p[i] = '0';
			row1 = row1 + p[i];
		}
		row = Integer.parseInt(row1);
		//System.out.println(row);
		this.Rows = row;
		for (int i = 35; i < 41; i = i + 1)
		{
			if (p[i] == '\n') break;
			if (p[i] == ' ') p[i] = '0';
			col1 = col1 + p[i];
		}
		col = Integer.parseInt(col1);
        //System.out.println(col);
		this.Columns = col;
		System.gc();
	}

	public int bands()//return bands
	{
		return Bands;
	}

	public int rows()//return rows
	{
		return Rows;
	}

	public int columns()//return columns
	{
		return Columns;
	}

	public File imagefile()//return imagefilename
	{
		return picfile;
	}
}

/* Picture Class To Read & Display the Image */

class picture extends JPanel
{
	private BufferedImage img;
	private int Width;
	private int Height;
	private int Bands;
	private ReadImageData ImgData1;
	static private File ImageFile;
	static int[][][] a;
	int array_size;
	static String ImageName;
	int x_pass,y_pass,y_pass2,num_of_slice,origin_x=0,origin_y=0;
	public static int []maxBand;
	public static int []minBand;
	public static byte pix_rgb[][];
	public static int shiftFlag=0;

	public static void setimage(String nam)
	{
		ImageFile = new File(nam);
		ImageName = nam;
	}


	public BufferedImage getpicdata(int Color1, int Color2, int Color3)///return image
	{
		System.gc();
		ImgData1 = new ReadImageData();
		ImageFile = ImgData1.imagefile();

		ImgData1.read(ImageFile);
		Bands = ImgData1.bands();
		Width = ImgData1.columns();
		Height = ImgData1.rows();
		int p = Color1;
		int q = Color2;
		int r = Color3;
		System.out.println("Number of Bands in the image: " + Bands);
		System.out.println("Width of image (in pixels): " + Width);
		System.out.println("Height of image (in pixels): " + Height);
		System.gc();
		int i=0, x, j=0, l, k;
		int index = 0;

		int pix[];
		int pix1[];
		int pix2[];
		int pix3[];

		int pix_2[];
		int pix1_2[];
		int pix2_2[];
		int pix3_2[];

		int p1, p2;
		int z,g;
		char temp[] = new char[10];
		char temp2[] = new char[100];
		int myFlag = 0;

		int maxRGB = 0, tempMax = 0;
		byte temp_max[];

		try
		{
			InputStream MyHdr = new FileInputStream(ImageFile + ".hdr");
			for (k = 0; k < 71; k++)
				temp2[k] = (char)MyHdr.read();
			for (k = 71; k < 78; k++)
			{
				temp[k - 71] = (char)MyHdr.read();
			}
		}
		catch (Exception e) { myFlag = 1; }
		String myTemp = (new String(temp)).trim();
		System.out.println("Read Data in Format (for pixel values): " + myTemp);
		try
		{
			String s = myTemp.substring(1);
			shiftFlag = Integer.parseInt (s);
		}
		catch(Exception e){ myFlag = 1;}

		//System.out.println("My Shift : " + shiftFlag);
		if(myFlag == 1)
			shiftFlag = 8;

		long fsize, fsize2;
		MappedByteBuffer mBuf;
		int tempWidth;

		try
		{

			FileInputStream f1 = new FileInputStream(ImageFile);            //reading pixel values for each band
			FileChannel fchan = f1.getChannel();
			FileOutputStream fout[] = new FileOutputStream[Bands];
			FileChannel fchanOut[] = new FileChannel[Bands];

			myFlag = 0;
			try
			{
				FileInputStream chk = new FileInputStream("Data" + ImageName +  "0");
			}
			catch(FileNotFoundException e)
			{
				myFlag = 1;
			}
			if(myFlag == 1)
			{

				for(k=0; k<Bands; k++)
				{

					fout[k] = new FileOutputStream("Data" + ImageName +  k);

					fchanOut[k] = fout[k].getChannel();

				}

				int pos=0;
				index=1;
				System.out.println("My Database Creation Started");

				tempWidth = Width;

				if (shiftFlag != 8)
					tempWidth = Width * 2;

				index=1;
				for(i=0;i<Height*Bands;i++)
				{
					mBuf=fchan.map(FileChannel.MapMode.READ_ONLY,pos,tempWidth);
					pos=pos+tempWidth;
					fchanOut[index-1].write(mBuf);

					index=index+1;
					if(index==(Bands+1))
						index=1;
				}

				f1.close();
				fchan.close();
				for(i=0;i<Bands;i++)
				{
					fout[i].close();
					fchanOut[i].close();
				}

			}
			System.gc();
		}
		catch (Exception e) { System.out.println("Before Database creation : " + e); }

		System.out.println(" Database Creation started, for input image- "+ImageName);
;
		System.out.println(" Database Created in memory! ");

		img = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_ARGB);

		WritableRaster raster = img.getRaster();


		FileInputStream fin_band[]=new FileInputStream[Bands];
 		FileChannel fchan_in[]=new FileChannel[Bands];

 		index = 0;
 		double myTempCalc;

 		int slices, y_pass, y_origin;
 		int x_win=Width,y_win=600,y_win2,flag1=0;
 		int uneven_flag = 1;

 		if((Height % y_win) == 0)
 		{
 			slices = (int)(Height/y_win);
 			uneven_flag = 0;
 		}
 		else
 			slices = (int)(Height/y_win)+1;

 		fsize2=((Height-(slices-1)*y_win)*x_win);
 		y_win2 = (Height-(slices-1)*y_win);
		if(Height>y_win || Width>x_win)
		{
		 	fsize=(y_win*x_win);
		 	flag1=1;
		 	y_pass=y_win;

		}
	 	else
		{
		 	fsize=(Height*Width);
		 	System.out.println("Number of pixels in image : " + fsize);
		 	flag1=0;
		 	y_pass=Width;
		}

		int band_num[] = new int[Bands];
		String input;

		byte pix_byte[],pix_rgb2[][];

 		try
 		{
 			for(k=0; k<Bands; k++)
 			{
 				fin_band[k] = new FileInputStream("Data" + ImageName +  k);
 				fchan_in[k] = fin_band[k].getChannel();
 			}

 			if(shiftFlag == 8)
 			{
	 			pix = new int [(int)fsize];
				pix1 = new int [(int)fsize];
				pix2 = new int [(int)fsize];
				pix3 = new int [(int)fsize];

				pix_2 = new int [(int)fsize2];
			 	pix1_2 = new int [(int)fsize2];
				pix2_2 = new int [(int)fsize2];
				pix3_2 = new int [(int)fsize2];

	 			pix_rgb=new byte[Bands][(int)fsize];
				pix_rgb2=new byte[Bands][(int)fsize2];
			}
			else
			{
				pix = new int [(int)fsize];
				pix1 = new int [(int)fsize];
				pix2 = new int [(int)fsize];
				pix3 = new int [(int)fsize];

				pix_2 = new int [(int)fsize2];
			 	pix1_2 = new int [(int)fsize2];
				pix2_2 = new int [(int)fsize2];
				pix3_2 = new int [(int)fsize2];

	 			pix_rgb=new byte[Bands][(int)fsize*2];
				pix_rgb2=new byte[Bands][(int)fsize2*2];

			}

			byte pix_line[];
			byte pix_line2[];
			int abc=0;
			if(shiftFlag == 8)
			{
				pix_line = new byte[x_win];
				pix_line2 = new byte[x_win];
				abc = x_win;
			}
			else
			{
				pix_line = new byte[x_win * 2];
				pix_line2 = new byte[x_win * 2];
				abc = x_win * 2;
			}
			int line_start,line_start2=0;
			int myMax = 0;
			int myMin=0;
			long count;

			int tempCount;
			if(shiftFlag == 8)
				count = fsize;
			else
				count = fsize * 2;


			long counter=0;
			long start_pos = 0;
			maxBand=new int[Bands];
			minBand=new int[Bands];
			int maxBand2[]=new int[Bands];
		   	int minBand2[]=new int[Bands];
		   	int myMin2=0;
		   	int myMax2=0;
		   	line_start = 0;
		   	int first_time=1;
		   		//System.out.println("Calculating Maximum and Minimum1");
		   	for(i=0; i<Bands; i++)
		   	{
		   		maxBand[i] = -1;
		   		minBand[i] = 65535;
		   	}
		   		//System.out.println("Calculating Maximum and Minimum2");

		   	//Calculation of Maximum and Minimum
		   	FileInputStream f1 = new FileInputStream(ImageFile);
			FileChannel fchan = f1.getChannel();

		   	if(shiftFlag == 8)
				tempWidth = Width;
			else
				tempWidth = Width * 2;
			//System.out.println("Calculating Maximum and Minimum");
		   	for(j=0; j<Height; j++)
		   	{

		   		for(i=0; i<Bands; i++)
		   		{
		   			mBuf = fchan.map(FileChannel.MapMode.READ_ONLY,line_start,tempWidth);
					mBuf.get(pix_line);
					for(k=0; k<tempWidth; k++)
						pix_rgb[i][k] = pix_line[k];
					line_start += tempWidth;

					for(k=0; k<Width; k++)
					{
						if(shiftFlag != 8)
						{
							p1 = pix_rgb[i][2*k] << 24;
							p2 = (pix_rgb[i][2*k+1] <<24 ) >>> 8;
							tempCount = p1 | p2;
							tempCount = tempCount >>> 16;
						}
						else
							tempCount = (pix_rgb[i][k] << 24) >>> 24;

						if(tempCount>maxBand[i])
							maxBand[i]=tempCount;

						if(tempCount<minBand[i])
						   minBand[i]=tempCount;
					}
		   		}
		   		if(j%500 == 0)
		   		{
		   	//		System.out.println("j : " + j);
		   			System.gc();
		   		}
		   	}

		myMax = maxBand[0];
		for(i=1; i<Bands; i++)
			if(myMax < maxBand[i])
				myMax = maxBand[i];

		for(i=0;i<Bands;i++)
			// System.out.println("the maximum value for band"+ (i+1 )+ " " + maxBand[i]);

		for(i=0;i<Bands;i++)
			//System.out.println("the minimum value for band"+ (i+1) + " " + minBand[i]);


			// when size of image is smaller than window size set by x_win & y_win
			if(flag1==0)
			{

				for(i=0; i<Bands; i++)
				{
					if(shiftFlag == 8)
						mBuf=fchan_in[i].map(FileChannel.MapMode.READ_ONLY,0,fsize);
					else
						mBuf=fchan_in[i].map(FileChannel.MapMode.READ_ONLY,0,fsize*2);
					mBuf.get(pix_rgb[i]);
                                        
				}
				System.gc();

 				//convert raw bytes into arrays.
 				if(shiftFlag == 8)
 				{
 					//System.out.println("(This is an 8 bit image)");
					for(i=0;i<fsize;i++)
					{
						pix[i]=255;
						if(Bands!=1)
						{
							if(pix_rgb[p-1][i]>=0)
								pix1[i]= pix_rgb[p-1][i];
							else
							  	pix1[i]= 256+pix_rgb[p-1][i];

							if(pix_rgb[q-1][i]>=0)
							  	pix2[i]= pix_rgb[q-1][i];
							else
							  	pix2[i]= 256+pix_rgb[q-1][i];

							if(pix_rgb[r-1][i]>=0)
							  	pix3[i]= pix_rgb[r-1][i];
							else
							  	pix3[i]= 256+pix_rgb[r-1][i];
						}

						else
						{
							if(pix_rgb[0][i]>=0)
							{
								pix1[i]= pix_rgb[0][i];
								pix2[i]=pix1[i];
								pix3[i]=pix1[i];
							}
							else
							{
								pix1[i]= 256+pix_rgb[0][i];
								pix2[i]=pix1[i];
								pix3[i]=pix1[i];
							}
						 }
	    	      		}//close for
				}
				else
				{

					for(i=0;i<fsize;i++)
					{
						pix[i]=255;
						if(Bands!=1)
						{
							//for pix1
							if(pix_rgb[p-1][2*i]<0)
								z=256+pix_rgb[p-1][2*i];
							else
								z=pix_rgb[p-1][2*i];

							if(pix_rgb[p-1][2*i+1]<0)
								g=256+pix_rgb[p-1][2*i+1];
							else
								g=pix_rgb[p-1][2*i+1];

							p1 = z << 8;
							p2 = g;
							pix1[i] = p1 | p2;

							myTempCalc = ((double)pix1[i] / (double)myMax) * 255.0;
							pix1[i] = (int)myTempCalc;

							// for pix2
							if(pix_rgb[q-1][2*i]<0)
								z=256+pix_rgb[q-1][2*i];
							else
								z=pix_rgb[q-1][2*i];

							if(pix_rgb[q-1][2*i+1]<0)
								g=256+pix_rgb[q-1][2*i+1];
							else
								g=pix_rgb[q-1][2*i+1];

							p1 = z << 8;
							p2 = g;
							pix2[i] = p1 | p2;

							myTempCalc = ((double)pix2[i] / (double)myMax) * 255.0;
							pix2[i] = (int)myTempCalc;

							// for pix3

							if(pix_rgb[r-1][2*i]<0)
								z=256+pix_rgb[r-1][2*i];
							else
								z=pix_rgb[r-1][2*i];

							if(pix_rgb[r-1][2*i+1]<0)
								g=256+pix_rgb[r-1][2*i+1];
							else
								g=pix_rgb[r-1][2*i+1];

							p1 = z << 8;
							p2 = g;
							pix3[i] = p1 | p2;

							myTempCalc = ((double)pix3[i] / (double)myMax) * 255.0;
							pix3[i] = (int)myTempCalc;

						}

						else
						{

						}

	    	      	}//close for
					System.out.println("Image Complete");
				}

 				// set rasters
				raster.setSamples(0,0,Width,Height,0,pix1);
				raster.setSamples(0,0,Width,Height,1,pix2);
				raster.setSamples(0,0,Width,Height,2,pix3);
				raster.setSamples(0,0,Width,Height,3,pix);


			}//close if
			else if(flag1==1) // when size of image is greater than window size set by x_win & y_win
			{

				System.out.println("the maximum value in bigger image is:"+ myMax);

				System.out.println("Maximum Value : " + myMax);

				if(shiftFlag==8)
				{
					line_start=0;
					for(int v=0;v<slices;v++)
					{
						System.out.println("enter v :"+v);

						if(v!=(slices-1))
						{

				  		  for( i=0;i<Bands;i++)
						  {
							if(Bands==1 && i!=0)
							{
								continue;
							}

							//line_start=line_start2;
							line_start=v*y_win*Width;
							System.out.println("line start i :"+line_start+" "+i);
							int k_index=0;
							// read the files line by line i.e x_win bytes in one go
							// and repeat unpo y_win i.e. size of window
							for(j=0;j<y_win;j++)
							{
								mBuf=fchan_in[i].map(FileChannel.MapMode.READ_ONLY,line_start,x_win);
								mBuf.get(pix_line);// store each line in separate buffer
								// i.e pix_line[]
								line_start=line_start+Width;

								// increment line_start by Width to point to next line
								// and leave rest of the pixels
								for(k=0;k<x_win;k++,k_index++)
								{
								// transfer one line data into pix_rgb[][]
								pix_rgb[i][k_index]=pix_line[k];

								}
							}//close for

							System.out.println("i :"+i);

				    	}//close for

						//line_start2=line_start;
							System.out.println("enter ");
							System.out.println("fsize :"+fsize);

						if(Bands!=1)
						{
							for( i=0;i<fsize;i++)
							{
				    		    pix[i]=255;
				    		    if(pix_rgb[p-1][i]>=0)
							    pix1[i]= pix_rgb[p-1][i];
								else
								pix1[i]= 256+pix_rgb[p-1][i];

								if(pix_rgb[q-1][i]>=0)
								pix2[i]= pix_rgb[q-1][i];
								else
								pix2[i]= 256+pix_rgb[q-1][i];

								if(pix_rgb[r-1][i]>=0)
								pix3[i]= pix_rgb[r-1][i];
								else
								pix3[i]= 256+pix_rgb[r-1][i];
							}

							raster.setSamples(0,origin_y,x_win,y_win,0,pix1);
							raster.setSamples(0,origin_y,x_win,y_win,1,pix2);
							raster.setSamples(0,origin_y,x_win,y_win,2,pix3);
				 			raster.setSamples(0,origin_y,x_win,y_win,3,pix);
				 			System.out.println("slice displayed");
							origin_x=0;
							origin_y=origin_y+y_win;
							System.gc();

					    }
						else
						{
							  for( i=0;i<fsize;i++)
							  {
							      pix[i]=255;
							      if(pix_rgb[0][i]>=0)
							      pix1[i]= pix_rgb[0][i];
							      else
							  	  pix1[i]= 256+pix_rgb[0][i];

							  }
							  pix2=pix3=pix1;

							raster.setSamples(0,origin_y,x_win,y_win,0,pix1);
							raster.setSamples(0,origin_y,x_win,y_win,1,pix2);
							raster.setSamples(0,origin_y,x_win,y_win,2,pix3);
						 	raster.setSamples(0,origin_y,x_win,y_win,3,pix);

							System.out.println("slice displayed");
						 	origin_x=0;
						 	origin_y=origin_y+y_win;

						}
						System.gc();

					}//close if(v!=num_of_slice-1)

					else if(v==(slices-1))
					{
						System.out.println("v origin_y line_start  :"+v+" "+origin_y+" "+line_start);
						for( i=0;i<Bands;i++)
						{
							if(Bands==1 && i!=0)
							{
								continue;
							}
							line_start=v*y_win*Width;
							System.out.println("line start2 i :"+line_start+" "+i);
							int k_index=0;
							// read the files line by line i.e x_win bytes in one go
							// and repeat unpo y_win i.e. size of window

						    for(j=0;j<(Height-(slices-1)*y_win);j++)
							{
								mBuf=fchan_in[i].map(FileChannel.MapMode.READ_ONLY,line_start,x_win);
								mBuf.get(pix_line);// store each line in separate buffer
								// i.e pix_line[]
								line_start=line_start+Width;
								// increment line_start by Width to point to next line
								// and leave rest of the pixels
								for(k=0;k<x_win;k++,k_index++)
								{
									// transfer one line data into pix_rgb[][]
									pix_rgb2[i][k_index]=pix_line[k];
								}

							}//close for
							System.out.println("i :"+i);
						}//close for

						System.out.println("enter ");
						System.out.println("fsize2 :"+fsize2);

						if(Bands!=1)
						{
							for( i=0;i<fsize2;i++)
							{
								pix_2[i]=255;

							    if(pix_rgb2[p-1][i]>=0)
							    pix1_2[i]= pix_rgb2[p-1][i];
							    else
							    pix1_2[i]= 256+pix_rgb2[p-1][i];

							    if(pix_rgb2[q-1][i]>=0)
							    pix2_2[i]= pix_rgb2[q-1][i];
								else
								pix2_2[i]= 256+pix_rgb2[q-1][i];

								if(pix_rgb2[r-1][i]>=0)
								pix3_2[i]= pix_rgb2[r-1][i];
								else
								pix3_2[i]= 256+pix_rgb2[r-1][i];
							}
						    y_pass2=(Height-origin_y);//(num_of_slice-1));
							raster.setSamples(0,origin_y,x_win,y_pass2,0,pix1_2);
							raster.setSamples(0,origin_y,x_win,y_pass2,1,pix2_2);
							raster.setSamples(0,origin_y,x_win,y_pass2,2,pix3_2);
				 			raster.setSamples(0,origin_y,x_win,y_pass2,3,pix_2);

						}
						else
						{
							for( i=0;i<fsize2;i++)
							{
								pix_2[i]=255;

								if(pix_rgb2[0][i]>=0)
							    pix1_2[i]= pix_rgb2[0][i];
							    else
							    pix1_2[i]= 256+pix_rgb2[0][i];
							}
							pix3_2=pix2_2=pix1_2;

						    y_pass2=(Height-origin_y);//(num_of_slice-1));
							raster.setSamples(0,origin_y,x_win,y_pass2,0,pix1_2);
							raster.setSamples(0,origin_y,x_win,y_pass2,1,pix2_2);
							raster.setSamples(0,origin_y,x_win,y_pass2,2,pix3_2);
				 			raster.setSamples(0,origin_y,x_win,y_pass2,3,pix_2);

						}

						System.gc();

					}//close if(v==num_of_slice-1)


				}//close for "v"

			}// close else if
			else
			{

			  /* the code for 16 bit bigger image */
				line_start=0;
			  	System.out.println("Total number of slices : " + slices);
				for(int v=0;v<slices;v++)
				{
					System.out.println("\nSlice Number v :"+v);

					if(v!=(slices-1))
					{

				  		for( i=0;i<Bands;i++)
						{
							if(Bands==1 && i!=0)
							{
								continue;
							}

							line_start=v*y_win*Width*2;
							System.out.println("line start i :"+line_start+" "+i);
							System.out.println("\n");
							int k_index=0;
							// read the files line by line i.e x_win bytes in one go
							// and repeat unpo y_win i.e. size of windowx
							for(j=0;j<y_win;j++)
							{
								mBuf=fchan_in[i].map(FileChannel.MapMode.READ_ONLY,line_start,x_win*2);
								mBuf.get(pix_line);// store each line in separate buffer
								for(k=0;k<x_win*2;k++,k_index++)
								{
									pix_rgb[i][k_index]=pix_line[k];

								}

								line_start=line_start+(Width*2);

							}//close for



				    	}//close for


							System.out.println("enter ");
							System.out.println("fsize :"+fsize);
							double TempCalc;
							int myChkVal;

						System.out.println("Check value : p : " + p + ", q : " + q + ", r : " + r);
						if(Bands!=1)
						{
							line_start = 0;
							for( i=0;i<fsize;i++)
							{
				    		    pix[i]=255;

				    		    //for pix1
								if(pix_rgb[p-1][2*i]<0)
									z=256+pix_rgb[p-1][2*i];
								else
									z=pix_rgb[p-1][2*i];

								if(pix_rgb[p-1][2*i+1]<0)
									g=256+pix_rgb[p-1][2*i+1];
								else
									g=pix_rgb[p-1][2*i+1];

								p1 = z << 8;
								p2 = g;
								pix1[i] = p1 | p2;

								myTempCalc = ((double)pix1[i] / (double)myMax) * 255.0;
								pix1[i] = (int)myTempCalc;

							// for pix2
								if(pix_rgb[q-1][2*i]<0)
									z=256+pix_rgb[q-1][2*i];
								else
									z=pix_rgb[q-1][2*i];

								if(pix_rgb[q-1][2*i+1]<0)
									g=256+pix_rgb[q-1][2*i+1];
								else
									g=pix_rgb[q-1][2*i+1];

								p1 = z << 8;
								p2 = g;
								pix2[i] = p1 | p2;

								myTempCalc = ((double)pix2[i] / (double)myMax) * 255.0;
								pix2[i] = (int)myTempCalc;

							// for pix3

								if(pix_rgb[r-1][2*i]<0)
									z=256+pix_rgb[r-1][2*i];
								else
									z=pix_rgb[r-1][2*i];

								if(pix_rgb[r-1][2*i+1]<0)
									g=256+pix_rgb[r-1][2*i+1];
								else
									g=pix_rgb[r-1][2*i+1];

								p1 = z << 8;
								p2 = g;
								pix3[i] = p1 | p2;

								myTempCalc = ((double)pix3[i] / (double)myMax) * 255.0;
								pix3[i] = (int)myTempCalc;

				    		    line_start=line_start+Width*2;
				    		}

							raster.setSamples(0,origin_y,x_win,y_win,0,pix1);
							raster.setSamples(0,origin_y,x_win,y_win,1,pix2);
							raster.setSamples(0,origin_y,x_win,y_win,2,pix3);
				 			raster.setSamples(0,origin_y,x_win,y_win,3,pix);
				 			System.out.println("Main Slice : " + v);
							origin_x=0;
							origin_y=origin_y+y_win;
							System.gc();

					    }
						else
						{
							System.out.println("slice displayed2");
						 	origin_x=0;
						 	origin_y=origin_y+y_win;

						}
						System.gc();

					}//close if(v!=num_of_slice-1)

					else if(v==(slices-1))
					{
						System.out.println("Last slice");
				  		for( i=0;i<Bands;i++)
						{
							if(Bands==1 && i!=0)
							{
								continue;
							}

							line_start=v*y_win*Width*2;
							System.out.println("line start i :"+line_start+" "+i);
							System.out.println("\n");
							int k_index=0;

							for(j=0;j<(Height-(slices-1)*y_win);j++)
							{
								mBuf=fchan_in[i].map(FileChannel.MapMode.READ_ONLY,line_start,x_win*2);
								mBuf.get(pix_line);// store each line in separate buffer
								for(k=0;k<x_win*2;k++,k_index++)
								{
									pix_rgb2[i][k_index]=pix_line[k];

								}

								line_start=line_start+(Width*2);

							}//close for



				    	}//close for


							System.out.println("enter ");
							System.out.println("fsize :"+fsize);
							double TempCalc;
							int myChkVal;

						System.out.println("Check value : p : " + p + ", q : " + q + ", r : " + r);
						if(Bands!=1)
						{
							line_start = 0;
							for( i=0;i<fsize2;i++)
							{

				    		    pix_2[i]=255;

				    		    //for pix1
								if(pix_rgb2[p-1][2*i]<0)
									z=256+pix_rgb2[p-1][2*i];
								else
									z=pix_rgb2[p-1][2*i];

								if(pix_rgb2[p-1][2*i+1]<0)
									g=256+pix_rgb2[p-1][2*i+1];
								else
									g=pix_rgb2[p-1][2*i+1];

								p1 = z << 8;
								p2 = g;
								pix1_2[i] = p1 | p2;

								myTempCalc = ((double)pix1_2[i] / (double)myMax) * 255.0;
								pix1_2[i] = (int)myTempCalc;

								// for pix2
								if(pix_rgb2[q-1][2*i]<0)
									z=256+pix_rgb2[q-1][2*i];
								else
									z=pix_rgb2[q-1][2*i];

								if(pix_rgb2[q-1][2*i+1]<0)
									g=256+pix_rgb2[q-1][2*i+1];
								else
									g=pix_rgb2[q-1][2*i+1];

								p1 = z << 8;
								p2 = g;
								pix2_2[i] = p1 | p2;

								myTempCalc = ((double)pix2_2[i] / (double)myMax) * 255.0;
								pix2_2[i] = (int)myTempCalc;

								// for pix3

								if(pix_rgb2[r-1][2*i]<0)
									z=256+pix_rgb2[r-1][2*i];
								else
									z=pix_rgb2[r-1][2*i];

								if(pix_rgb2[r-1][2*i+1]<0)
									g=256+pix_rgb2[r-1][2*i+1];
								else
									g=pix_rgb2[r-1][2*i+1];

								p1 = z << 8;
								p2 = g;
								pix3_2[i] = p1 | p2;
								myTempCalc = ((double)pix3_2[i] / (double)myMax) * 255.0;
								pix3_2[i] = (int)myTempCalc;
					    		    line_start=line_start+Width*2;
				    		}
                                                        System.out.println("Displayed Image");
							raster.setSamples(0,origin_y,x_win,y_win2,0,pix1_2);
							raster.setSamples(0,origin_y,x_win,y_win2,1,pix2_2);
							raster.setSamples(0,origin_y,x_win,y_win2,2,pix3_2);
				 			raster.setSamples(0,origin_y,x_win,y_win2,3,pix_2);
				 			System.out.println("slice displayed1");
							origin_x=0;
							origin_y=origin_y+y_win;
							System.gc();
					    }
						else
						{

							System.out.println("slice displayed2");
						 	origin_x=0;
						 	origin_y=origin_y+y_win;
						}
						System.gc();
					}//close if(v==num_of_slice-1)

			  }	//end of slice v==-1
			}
				
			for( i=0;i<Bands;i++)
			{
				fin_band[i].close();
				fchan_in[i].close();
			}
		}
 }
            catch(Exception e)
            {
            	System.out.println(e);
            }
 			//System.out.println("BVCOE");
 		return img;
 		}

	public int[] minArray()
	{
		return minBand;
	}

	public int[] maxArray()
	{
		return maxBand;
	}
}

class DemoPanel extends JPanel
{
	static BufferedImage Imag;
	private Dimension ViewSize;
	private picture Picture1;
	int x;
	int y;
	int width;
	int height;
	int bands;
	int maxindex, minindex;
	public static int flag1=1;
	int click_x[]=new int[10000];
	int click_y[]=new int[10000];
	ReadImageData imgdata;
        public static int Bands1;
   	int Width1;
   	int Height1;
	MappedByteBuffer mBuf;
	static double val1[][];
	public static double xratios[]= new double[ReadImageData.Bands];
	public static double rowdata[]= new double[new ReadImageData().bands()];

	public DemoPanel()
	{
	}

	public BufferedImage getpixdata()
	{
		return Imag;
	}

	int return_x()
	{
		if (x >= 0)
			return x;
		else
			return -1;

	}

	int return_y()
	{
		if (y >= 0)
			return y;
		else
			return -1;
	}

	public DemoPanel (BufferedImage Img)
	{
	  System.gc();
	  this.Imag=Img;
	  int Width=Math.min(256,Imag.getWidth());
	  final int Height=Math.min(256,Imag.getHeight());
	  ViewSize=new Dimension(Width,Height);
	  setPreferredSize(new Dimension(Imag.getWidth(),Imag.getHeight()));
	  imgdata=new ReadImageData();
          Bands1=imgdata.bands();
  	  Width1=imgdata.columns();
  	  Height1=imgdata.rows();
  	  FileInputStream fin_band[]=new FileInputStream[Bands1];
          final FileChannel fchan_in[]=new FileChannel[Bands1];
 	  val1=new double[Bands1][100];
        }
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(Imag, 0, 0, this);
		g2.dispose();
	}
}

class AboutDialog extends JDialog implements ActionListener
{
	static int Band1 = 4;
	static int Band2 = 4;
	static int Band3 = 4;
	private ReadImageData ImgData2;
	Container contentPane = getContentPane();
	JLabel pict, pict1, pict2;
	//JTextField f1, f2, f3;
	JButton b1, b2;
	JComboBox list, list1, list2;
	int x, y, z;
	private boolean ok;

	public int show_band1()
	{
		return Band1;
	}

	public int show_band2()
	{
		return Band2;
	}

	public int show_band3()
	{
		return Band3;
	}

	public AboutDialog(JFrame parent)
	{
		super(parent, "Bands Chooser", true);
		ImgData2 = new ReadImageData();
		int i = ImgData2.bands();
		String str[] = new String[i];
		GridBagLayout g1;
		GridBagConstraints gbc;
		int k = i;

		for (int j = 0; j < i; j++)
		{
			str[j] = Integer.toString(k);
			k--;
		}

		b1 = new JButton("Ok");
		b2 = new JButton("Cancel");
		list = new JComboBox(str);
		list1 = new JComboBox(str);
		list2 = new JComboBox(str);
		pict = new JLabel("        Red");
		pict1 = new JLabel("        Green");
		pict2 = new JLabel("        Blue");

		g1 = new GridBagLayout();
		contentPane.setLayout(g1);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 0;
		gbc.gridy = 0;
		g1.setConstraints(pict, gbc);
		contentPane.add(pict);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 2;
		gbc.gridy = 0;


		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 4;
		gbc.gridy = 0;

		g1.setConstraints(list, gbc);
		contentPane.add(list);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 6;//4
		gbc.gridy = 0;
		g1.setConstraints(pict1, gbc);
		contentPane.add(pict1);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 8;
		gbc.gridy = 0;


		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 10;
		gbc.gridy = 0;

		g1.setConstraints(list1, gbc);
		contentPane.add(list1);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 12;//8
		gbc.gridy = 0;
		g1.setConstraints(pict2, gbc);
		contentPane.add(pict2);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 14;//10
		gbc.gridy = 0;



		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = 1;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 16;//12
		gbc.gridy = 0;

		g1.setConstraints(list2, gbc);
		contentPane.add(list2);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = 2;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 3;
		gbc.gridy = 2;
		g1.setConstraints(b1, gbc);
		contentPane.add(b1);
		b1.addActionListener(this);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = 2;
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.gridx = 6;
		gbc.gridy = 2;
		g1.setConstraints(b2, gbc);
		contentPane.add(b2);
		b2.addActionListener(this);
		dispose();
	}

	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		if (source == b1)//
		{
			String s = (String)list.getSelectedItem();
			String s1 = (String)list1.getSelectedItem();
			String s2 = (String)list2.getSelectedItem();
			int x = Integer.parseInt(s);
			this.Band1 = x;
			int y = Integer.parseInt(s1);
			this.Band2 = y;
			int z = Integer.parseInt(s2);
			this.Band3 = z;
			dispose();
		}
		else
		{
			dispose();
		}
	}
}

// Class FCM- contains computation of FCM Algorithm to generate clusters on images

class Fcm extends JFrame implements ActionListener
{ 
  double epsilon = 0.0;
  double x[];
  public static byte pix_rgb1[][];
  public byte pix_rgbu[][];
  double m;
  int b,h,w;
  private ReadImageData imgdata8=new ReadImageData();
  int bands=imgdata8.bands();
  double pixel[][];
  int cluster;
  static int Bands;
  int iteration;
  JButton btClasi=new JButton("Classify");
  JTextField iter=new JTextField(2);
  JTextField numofcluster =new JTextField(2);
  JTextField objectivefunction =new JTextField(2);
  JLabel rachna = new JLabel("maximum number of iterations");
  JLabel arnav = new JLabel("Enter number of clusters");
  JLabel pathak = new JLabel("Enter the value of epsilon(0)");
  double dsq[][];
  double m1,m2;
  double mix_m;
  JFrame f= new JFrame("Classification");
  int pixrgb[][];
  double meuij[][];
  FileOutputStream fout;
  int fsize;   
  public Fcm()
  {
        iter.setEnabled(true);
        numofcluster.setEnabled(true);
        objectivefunction.setEnabled(true);
        iter.setText("");
        numofcluster.setText("");
        objectivefunction.setText("");
           f.add(iter);
           f.add(btClasi);
           f.add(rachna);
           f.add(arnav);
           f.add(numofcluster);
           f.add(objectivefunction);
           f.add(pathak);
           f.setSize(400,400);  
           f.setLayout(null);  
           f.setVisible(true);
           iter.setBounds(50, 70, 200, 30);
           numofcluster.setBounds(50, 150, 200, 30);
           objectivefunction.setBounds(50,220,200,30);
            
                    btClasi.setBounds(50,290,200,30);
                    arnav.setBounds(50,120,200,30);
                    rachna.setBounds(50,30,200,30);
                    pathak.setBounds(50,180,200,30);
                    bands=new ReadImageData().bands();
                    btClasi.addActionListener(this);
                    iter.addActionListener(this);
                    objectivefunction.addActionListener(this);

                 numofcluster.addActionListener(this);
                 int bands;
                 this.Bands=imgdata8.bands();
	         bands=imgdata8.bands();
        
        
          }

 public void actionPerformed(ActionEvent e) {
     
System.out.println("Rachna & Arnav's Module");
     
    
      String g = iter.getText();
       iteration = Integer.parseInt(g);

      String k = numofcluster.getText();
      cluster = Integer.parseInt(k);
      
      String f = objectivefunction.getText();
      epsilon = Float.parseFloat(f);
   
      b= new ReadImageData().bands(); 
      h= new ReadImageData().rows();
      w= new ReadImageData().columns();
			
                pix_rgb1 = new byte[Bands][(int)(h*w*2)];
 		        x=new double[b];
 		    
 		        pix_rgb1 = picture.pix_rgb;
                fsize = h*w;
                pixrgb = new int[bands][h*w*2];
                meuij = new double[cluster][h*w];  
              
                double dsq[][] = new double[cluster][fsize];
        
                double a1, a2, a3, a4 ,b1, b2, b3, b4, insqrt;
        
             
                m = 2.1;
                
                
                for (int i = 0 ; i < h*w ; i++)
               {
                   for (int j = 0 ; j < bands ; j++)
                   {
                	   	if (pix_rgb1[j][i]<0)
                       pixrgb[j][i] = 256 + pix_rgb1[j][i];
                	   	else
                	   		pixrgb[j][i] = pix_rgb1[j][i];
                   }
               }
     
        int rows = imgdata8.rows();
        int columns = imgdata8.columns();        
        int fsize = rows*columns;
        double clustermean[][] = new double[bands][fsize];
                
        int epsum = 0;
        
        // randomly initializing cluster means
        
        for (int setcol = 0; setcol < cluster; setcol++)
        {
            for (int setrow = 0; setrow < bands; setrow++)
            {
                clustermean[setrow][setcol] = (int)(Math.random() * 255);
                //System.out.println(clustermean[setrow][setcol]);
            }
        }    
        int numiter = 1;
        double finalepsilon = 0;
        double J = 0;
        
        
        //Main loop will start here
       
     do {      
        

// computing dsq[][] : (d-square for values of meu)

        for(int j = 0 ; j < fsize ; j ++)
        {        
            for (int i = 0 ; i < cluster ; i ++)
                {
                    a1 = pixrgb[0][j] - clustermean[0][i];
                            a2 = pixrgb[1][j] - clustermean[1][i];
                            a3 = pixrgb[2][j] - clustermean[2][i];
                            a4 = pixrgb[3][j] - clustermean[3][i];
                            b1 = java.lang.Math.pow(a1,2);
                            b2 = java.lang.Math.pow(a2,2); 
                            b3 = java.lang.Math.pow(a3,2); 
                            b4 = java.lang.Math.pow(a4,2); 
                            insqrt = b1+b2+b3+b4;
                            dsq[i][j] = java.lang.Math.pow(insqrt,0.5); 
                }
        }       

        // computing membership values
        
        double num, den;
        int i,j, alpha;
        
        for (j=0; j<fsize; j++)
        {   
            for ( alpha = 0; alpha < cluster; alpha ++)
                {
                    den = 0; num = 0;
                    for (i = 0; i < cluster ; i++)
                        {
                            den = den + 1/ java.lang.Math.pow(dsq[i][j],(1/1.1));
                        }
                 
                    num = 1/ java.lang.Math.pow(dsq[alpha][j],(1/1.1));
                    meuij[alpha][j] = num/den;
                }
        }
        
        
        // computing the updated means in a new array
        
         double updateclustermean[][] = new double[bands][cluster];
         double multvector[][] = new double[bands][fsize];
         double pixelvector[][] = new double[bands][1];
         double minus[]=new double[bands];
         double minusquare[] = new double[bands];
         double midepsilon[]= new double[cluster];
         double parker= 0;
         
         // computing Vj
         
         for (int h =0; h<cluster; h++)
         {
            double pen =0;
            double den1 = 0;         
            for(int p=0; p<fsize; p++)
                { 
                    double a = meuij[h][p]; 
                    J = J +( a*dsq[h][p]);
                    den1 = den1 + java.lang.Math.pow(a,m);
                    for(int y=0; y < bands; y++)
                        { 
                            double u =   pixrgb[y][p];
                            multvector[y][p] = u *java.lang.Math.pow(a,m);
                        }
                }
            
            for(int n=0; n<bands; n++)
                {   pixelvector[n][0]=0;
                    for(int jaguar=0; jaguar<fsize; jaguar++)
                        {
                            pixelvector[n][0] = pixelvector[n][0] + multvector[n][jaguar];
                        }
                    //division
                    updateclustermean[n][h] = pixelvector[n][0]/den1;
                    minus[n] = updateclustermean[n][h]-clustermean[n][h];
                    minusquare[n] = java.lang.Math.pow(minus[n],2);
                    pen = pen+minusquare[n];                  
                }
            
            midepsilon[h] = java.lang.Math.pow(pen,0.5);
            parker = parker+midepsilon[h];
            
         }
         
         // updating mean array
         for (j = 0 ; j < cluster ; j++)
         {
              for (i = 0 ; i < bands ; i++)
                    {
                        clustermean[i][j] = updateclustermean[i][j];
                    }    
         }

         finalepsilon = parker/cluster;
     
         System.out.println(numiter);

        numiter++;
       System.out.println("Epsilon = "+finalepsilon);
         
     }  while( finalepsilon > epsilon && numiter < iteration );
 
 System.out.println("Hallelujah");
 

 
 //Printing the means of clusters
  for(int i=0; i<cluster ; i++)
  {
     for(int j = 0;j<bands;j++)
        {
            System.out.println("Mean value of cluster " + i + " - band "+ j + " :- " + clustermean[j][i]);
        }
  }
  
  //generating .hdr files of clusters. Put in comment, to NOT-save generated files to your system.
  
  
                    try{
                                for( int j=0; j<cluster; j++)
                                {
                                        fout = new FileOutputStream("cluster" + j);
					for (int ice = 0 ; ice < h*w ; ice++)
                                        {
                                            {fout.write( (int)( meuij[j][ice] * 255) );}
                                        }
                                        System.out.println("creating cluster, file number - "+j);
					FileWriter out=new FileWriter("cluster"+j+".hdr");
					out.write("BANDS:      "+1+"\n");
					out.write("8[+_{:    "+h+"\n");
					out.write("COLS:   "+w+"\n");
					out.close();
                   }
					fout.close();

                            }
                            catch(IOException he)
                             {
                                 System.out.println("Exception is :"+ he);
                             }
					
 
 
 } // end of action performed
}// end of class fcm

// end of project's module-22