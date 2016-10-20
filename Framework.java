//LittleFinger��һ������ı�ת��Ϊ������д�ʼ�ͼƬ�Ŀ�Դ��������
//Copyright (C) 2016  Gsllchb <Gsllchb@icloud.com>
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
	private final static String VERSION="1.1";
	
	 // @param args:template textPath outputPath
	public static void main(String[] args) throws Exception {
		final long beginTime=System.currentTimeMillis();
		
		if(args.length!=3){
			giveHints();
			return;
		}
		final String programFolder=getProgramFolder();
		final String templatePath=programFolder+"template"+File.separator+args[0];
		final String textPath=args[1];
		final String outputPath=args[2];
		
		final String templatePropPath=templatePath+File.separator+"template.properties";
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
		final double  fontSizedeviation  = Double.parseDouble(prop.getProperty("fontSizedeviation"));
		final double  wordSpacedeviation = Double.parseDouble(prop.getProperty("wordSpacedeviation"));
		final double  lineSpacedeviation = Double.parseDouble(prop.getProperty("lineSpacedeviation"));
		final String  backgroundFileName = prop.getProperty("backgroundFileName");
		final String  outputFormatName   = prop.getProperty("outputFormatName");       
		
		if(rgbR<0 || rgbR>255 || rgbG<0 || rgbG>255 || rgbB<0 || rgbB>255){
			System.out.print("RGBֵ����������[0,255]��!");
			return;
		}
		final Color color=new Color(rgbR,rgbG,rgbB);

	    final BufferedImage background = ImageIO.read(
	    		new File(templatePath+File.separator+backgroundFileName));  
	    
		if((topMargin+bottomMargin+fontSize) >= background.getHeight()){
	    	System.out.print("topMargin,bottomMargin��fontSize֮����С�ڱ���ͼ�߶�!\n");
	    	return;
	    }
	    if((leftMargin+rightMargin+fontSize) >= background.getWidth()){
	    	System.out.print("leftMargin,rightMargin��fontSize֮����С�ڱ���ͼ���!\n");
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
				preprocess(text),
				font,
				color,
				wordSpace,
				lineSpace,
				topMargin,
				bottomMargin,
				leftMargin,
				rightMargin,
				fontSizedeviation,
				wordSpacedeviation,
				lineSpacedeviation,
				Framework::isHalfChar,
				Framework::isEndChar);
		
		final File outputFile=new File(outputPath);
		outputFile.mkdirs();
		int index=0;
		for(BufferedImage page:article){
			ImageIO.write(page, outputFormatName, new File(outputPath,(++index)+"."+outputFormatName));
		}
		
		final long endTime=System.currentTimeMillis();
		System.out.print("�ı��ļ���"+textPath+"����������\n"
				+ "��ʱ��"+(endTime-beginTime)+"ms\n"
				+ "����ͼƬ����ʽ��"+outputFormatName+"����"+index+"��\n"
				+ "����ļ���:"+outputPath+"\n");


	}
	
	private final static String getProgramFolder(){
		final String classPath=System.getProperty("java.class.path");
		final int index=classPath.lastIndexOf(File.separatorChar);
		if(index==-1)
			return "";
		return classPath.substring(0, index+1);
	}
	
	private final static void giveHints(){
		System.out.print("\n"
				+ "LittleFinger[�汾��"+VERSION+"]\n"
				+ "һ������ı�ת��Ϊ������д�ʼ���ͼƬ�Ŀ�Դ��������\n"
				+ "\n"
				+ "�÷���LittleFinger.jar template textPath outputPath\n"
				+ "\ttemplate\tģ������\n"
				+ "\ttextPath\t�������ı��ļ�·��\n"
				+ "\toutputPath\t����ͼƬ���·��\n"
				+ "\n"
				+ "������Ϣ��https://github.com/Gsllchb/LittleFinger\n"
				+ "\n");
	}
	
	private final static String preprocess(String text){
		char[] chars=text.toCharArray();
		for(int i=0; i<chars.length; ++i){
			switch(chars[i]){
			case '��':
				chars[i]='(';
				break;
			case '��':
				chars[i]=')';
				break;
			case '��':
				chars[i]='[';
				break;
			case '��':
				chars[i]=']';
				break;
			case '��':
				chars[i]=',';
				break;
			case '��':
				chars[i]='!';
				break;
			case '��':
				chars[i]='?';
				break;
			case '��':
				chars[i]='"';
				break;
			case '��':
				chars[i]='"';
				break;
			case '��':
				chars[i]='\'';
				break;
			case '��':
				chars[i]='\'';
				break;
			case '��':
				chars[i]=':';
				break;
			case '��':
				chars[i]=';';
				break;
			}
		}
		return new String(chars);
	}
	
	private final static boolean isHalfChar(final char c){
		//����Ӣ����ĸ�Ͱ���������
		if(c>='0' && c<='9') return true;
		if(c>='a' && c<='z') return true;
		if(c>='A' && c<='Z') return true;
		if(c>='��' && c<='��') return true;
		if(c>='��' && c<='��') return true;
		//Ӣ�ı��
		switch(c){
		case ' ':
		case '~':
		case '`':
		case '!':
		case '@':
		case '#':
		case '$': case '��':
		case '%':
		case '^':
		case '&':
		case '(': case ')':
		case '+': case '-': case '*': case '/':
		case '_':
		case '=':
		case '[': case ']':
		case '{': case '}':
		case '|':
		case '\\':
		case ':':
		case ';':
		case '"':
		case '\'':
		case '<': case '>':
		case ',':
		case '.':
		case '?':
		case '��': case '��': case '��':
		case '��': case '��': case '��':
			return true;
		}
		return false;
	}
	
	private final static boolean isEndChar(final char c){
		switch(c){
		case ',':
		case ';':
		case '.':
		case '��':
		case ')':
		case ']':
		case '}':
		case '>':
		case '��':
		case '!':
		case '?':
		case '��':
		case '��': case '��': case '��':
		case '��': case '�H':
			return true;		
		}
		return false;
	}
}
