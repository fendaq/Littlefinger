//LittleFinger
//一款将电子文本转化为中文手写笔迹图片的开源免费软件。。
//基于命令行界面,通过引入随机性来模仿人类(特别是本人)手写.

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

import java.util.Properties;
import java.util.List;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;

public final class Framework {
	final static String VERSION="1.0";
	
	 // @param args:templatePath textPath outputPath
	public static void main(String[] args) throws Exception {
		final long beginTime=System.currentTimeMillis();
		
		if(args.length!=3){
			giveHints();
			return;
		}
		
		final String templatePath=args[0];
		final String textPath=args[1];
		final String outputPath=args[2];
		
		final String templatePropPath=templatePath+"/template.properties";
		Properties prop=new Properties();
		
		BufferedReader inputProp=new BufferedReader(
				new InputStreamReader(new FileInputStream(templatePropPath),"UTF-8"));
		prop.load(inputProp);
		inputProp.close();
		
		final String  fontFamily         = prop.getProperty("fontFamily");
		final int     rgbR               = Integer.parseInt(prop.getProperty("rgbR"));
		final int     rgbG               = Integer.parseInt(prop.getProperty("rgbG"));
		final int     rgbB               = Integer.parseInt(prop.getProperty("rgbB"));
		final boolean useBold            = Boolean.parseBoolean(prop.getProperty("useBold"));
		final int     topMargin          = Integer.parseInt(prop.getProperty("topMargin"));
		final int     bottomMargin       = Integer.parseInt(prop.getProperty("bottomMargin"));
		final int     leftMargin         = Integer.parseInt(prop.getProperty("leftMargin"));
		final int     rightMargin        = Integer.parseInt(prop.getProperty("rightMargin"));
		final int     fontSize           = Integer.parseInt(prop.getProperty("fontSize"));
		final int     wordSpace          = Integer.parseInt(prop.getProperty("wordSpace"));
		final int     lineSpace          = Integer.parseInt(prop.getProperty("lineSpace"));
		final double  fontSizeVariance   = Double.parseDouble(prop.getProperty("fontSizeVariance"));
		final double  wordSpaceVariance  = Double.parseDouble(prop.getProperty("wordSpaceVariance"));
		final double  lineSpaceVariance  = Double.parseDouble(prop.getProperty("lineSpaceVariance"));
		final String  backgroundFileName = prop.getProperty("backgroundFileName");
		final String  outputFormatName   = prop.getProperty("outputFormatName");       
		
		if(rgbR<0 || rgbR>255 || rgbG<0 || rgbG>255 || rgbB<0 || rgbB>255){
			System.out.print("RGB值均需在区间[0,255]内!");
			return;
		}
		final Color color=new Color(rgbR,rgbG,rgbB);

	    final BufferedImage background = ImageIO.read(
	    		new File(templatePath+"/"+backgroundFileName));  
	    
		if((topMargin+bottomMargin+fontSize) >= background.getHeight()){
	    	System.out.print("topMargin,bottomMargin与fontSize之和须小于背景图高度!\n");
	    	return;
	    }
	    if((leftMargin+rightMargin+fontSize) >= background.getWidth()){
	    	System.out.print("leftMargin,rightMargin与fontSize之和须小于背景图宽度!\n");
	    	return;
	    }
	    
		BufferedReader inputText=new BufferedReader(
				new InputStreamReader(new FileInputStream(textPath),"UTF-8"));
		String text="";
		String line;
		while((line=inputText.readLine())!=null){
			text=text+line+"\n";
		}		
		inputText.close();
		
		final Font font=new Font(fontFamily,useBold ? Font.BOLD : Font.PLAIN, fontSize);
		
		final List<BufferedImage> article=LittleFinger.write(
				background,
				text.toCharArray(),
				font,
				color,
				wordSpace,
				lineSpace,
				topMargin,
				bottomMargin,
				leftMargin,
				rightMargin,
				fontSizeVariance,
				wordSpaceVariance,
				lineSpaceVariance);
		
		final File outputFile=new File(outputPath);
		outputFile.mkdirs();
		int index=0;
		for(BufferedImage page:article){
			ImageIO.write(page, outputFormatName, new File(outputPath,(++index)+"."+outputFormatName));
		}
		
		final long endTime=System.currentTimeMillis();
		System.out.print("文本文件（"+textPath+"）处理结果：\n"
				+ "耗时："+(endTime-beginTime)+"ms\n"
				+ "生成图片（格式："+outputFormatName+"）："+index+"张\n"
				+ "输出文件夹:"+outputPath+"\n");


	}
	
	final static void giveHints(){
		System.out.print("\n"
				+ "LittleFinger[版本："+VERSION+"]\n"
				+ "一款将电子文本转化为中文手写笔迹的图片的开源免费软件。\n"
				+ "\n"
				+ "用法：LittleFinger.jar templatePath textPath outputPath\n"
				+ "\ttemplatePath\t模板路径\n"
				+ "\ttextPath\t待处理文本文件路径\n"
				+ "\toutputPath\t生成图片输出路径\n"
				+ "\n"
				+ "更多信息：https://github.com/Gsllchb/LittleFinger\n"
				+ "\n");
	}
}
