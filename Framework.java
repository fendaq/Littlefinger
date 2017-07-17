//LittleFinger：一款将电子文本转化为中文手写笔迹图片的开源免费软件。
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
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import com.google.gson.*;

public final class Framework {
	private static final String VERSION = "1.4";
	
	 // @param args:template textPath outputPath
	public static void main(String[] args) throws Exception {
		final long beginTime=System.currentTimeMillis();
		
		if (args.length != 3) {
			giveHints();
			return;
		}
		
		final String programFolder = getProgramFolder();
		final String templatePath = programFolder + "template" + File.separator + args[0];
		final String textPath = args[1];
		final String outputPath = args[2];		
		final String templatePropPath = templatePath + File.separator + "template.json";
		
		BufferedReader inputConf = new BufferedReader(
				new InputStreamReader(new FileInputStream(templatePropPath), "UTF-8"));
		JsonObject conf=new JsonParser().parse(inputConf).getAsJsonObject();
		inputConf.close();
		
		final String backgroundFileName = conf.get("backgroundFileName").getAsString();
		final String outputFormatName = conf.get("outputFormatName").getAsString();
		final String fontFamily  = conf.get("fontFamily").getAsString(); 
		final JsonArray rgb = conf.get("rgb").getAsJsonArray();
		final boolean useBold = conf.get("useBold").getAsBoolean();
		final int topMargin = conf.get("topMargin").getAsInt();
		final int bottomMargin = conf.get("bottomMargin").getAsInt();
		final int leftMargin = conf.get("leftMargin").getAsInt();
		final int rightMargin = conf.get("rightMargin").getAsInt();
		final int fontSize = conf.get("fontSize").getAsInt();
		final int wordSpace = conf.get("wordSpace").getAsInt();
		final int lineSpace = conf.get("lineSpace").getAsInt();
		final double fontSizedeviation = conf.get("fontSizedeviation").getAsDouble();
		final double wordSpacedeviation = conf.get("wordSpacedeviation").getAsDouble();
		final double lineSpacedeviation = conf.get("lineSpacedeviation").getAsDouble();
		final String halfChars = conf.get("halfChars").getAsString();
		final String endChars = conf.get("endChars").getAsString();
		final JsonObject swapMap = conf.get("swapMap").getAsJsonObject();
		
		final Color color = new Color(
				rgb.get(0).getAsInt(),
				rgb.get(1).getAsInt(),
				rgb.get(2).getAsInt());

	    final BufferedImage background = ImageIO.read(
	    		new File(templatePath + File.separator + backgroundFileName));  
	    
		if ((topMargin + bottomMargin + fontSize) >= background.getHeight()) {
	    	System.out.print("topMargin,bottomMargin与fontSize之和须小于背景图高度!\n");
	    	return;
	    }
	    if ((leftMargin + rightMargin + fontSize) >= background.getWidth()) {
	    	System.out.print("leftMargin,rightMargin与fontSize之和须小于背景图宽度!\n");
	    	return;
	    }
	    
		BufferedReader inputText = new BufferedReader(
				new InputStreamReader(new FileInputStream(textPath), "UTF-8"));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = inputText.readLine()) != null) {
			text.append(line).append('\n');
		}
		inputText.close();
		
		//对字符序列进行预处理
		for (int i = 0; i != text.length(); ++i) {
			final String c = String.valueOf(text.charAt(i));
			if(swapMap.has(c))
				text.setCharAt(i, swapMap.get(c).getAsCharacter());
		}
		
		final Font font = new Font(fontFamily, useBold ? Font.BOLD : Font.PLAIN, fontSize);
		
		final List<BufferedImage> article=LittleFinger.handwrite(
				background,
				text,
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
				(c) -> halfChars.contains(c.toString()),
				(c) -> endChars.contains(c.toString()));
		
		final File outputFile = new File(outputPath);
		outputFile.mkdirs();
		int index = 0;
		for(BufferedImage page : article){
			ImageIO.write(page, outputFormatName, new File(outputPath, (index++) + "." + outputFormatName));
		}
	
		final long endTime = System.currentTimeMillis();
		System.out.print("文本文件（" + textPath + "）处理结果：\n"
				+ "耗时：" + (endTime - beginTime) + "ms\n"
				+ "生成图片（格式：" + outputFormatName + "）：" + index + "张\n"
				+ "输出文件夹:" + outputPath + "\n");
	}
	
	private static final String getProgramFolder() {
		final String classPath = System.getProperty("java.class.path");
		final int index = classPath.lastIndexOf(File.separatorChar);
		if (index == -1) {
			return "";
		}
		return classPath.substring(0, index + 1);
	}
	
	private static final void giveHints() {
		System.out.print(
				"LittleFinger " + VERSION + " Copyright (C) 2016-2017 Gsllchb\n"
				+ "一款将电子文本转化为中文手写笔迹的图片的开源免费软件。\n"
				+ "\n"
				+ "用法：java [-options] -jar LittleFinger.jar template textPath outputPath\n"
				+ "\ttemplate\t模板名称\n"
				+ "\ttextPath\t待处理文本文件路径\n"
				+ "\toutputPath\t生成图片输出路径\n"
				+ "\n"
				+ "更多信息：https://github.com/Gsllchb/LittleFinger");
	}
	
}
