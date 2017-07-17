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

//核心算法及其所调用的函数.

import java.util.LinkedList;
import java.util.Random;
import java.util.function.Predicate;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public final class LittleFinger {
	
	//write方法尽量模仿在人类(特别是本人)手写时的字与字之间的关系.
	//1.斜体与非斜体使用均匀概率分布.
	//2.字体大小,字体间距与行间距使用正态分布.
	//3.字体大小与行间距的平均数均为非随机数;字体间距的平均数含有随机数,具有累积效应.
	//	换言之,每一行的字数不一定相等,但是每一页的最大行数一定相等.
	//另外此算法存在以下不足:
	//1.此算法仅能保证字体不会超过下边距和左边距;但由于引入随机性,故不能保证字体不超过上边距与右边距.
	//其实也可以解决这个不足;但是这既会提高代码的复杂程度,也会引入新的细节问题.
	//2.英文单词在遇到此程序自动换行时可能会导致单词分成上下两段。
	//注:
	//1.由于字体间距,行间距和各页边距均为整型;故为了充分体现出随机性,背景图片像素应足够大,上述各值也须相应增大；
	//2.形参font并非算法中实际运用的字体，而仅仅是有关字体信息（字型、是否使用粗体和字体大小）的封装，所以font的style只能是BOLD或PLAIN；
	//3.字体的坐标为字体左下角顶点的坐标；
	//4.使用此算法时需要传入两个可调用对象isHalfChar和isEndChar对字符进行判断。
	public static final LinkedList<BufferedImage> handwrite(
			final BufferedImage background,
			final CharSequence text,
			final Font font,
			final Color color,
			final int wordSpace,
			final int lineSpace,
			final int topMargin,
			final int bottomMargin,
			final int leftMargin,
			final int rightMargin,
			final double fontSizedeviation,
			final double wordSpacedeviation,
			final double lineSpacedeviation,
			final Predicate<Character> isHalfChar,
			final Predicate<Character> isEndChar) {
		
		Random random = new Random();
		final int fontSize = font.getSize();	
		final int fontStyle = font.getStyle();
		final int length = text.length();
		LinkedList<BufferedImage> article = new LinkedList<BufferedImage>();
		
		for (int i = 0; i != length; ) {
			BufferedImage page = deepCopy(background);
			Graphics2D context = page.createGraphics();
			context.setColor(color);
			
			for (int y = topMargin + fontSize; 
					y < page.getHeight() - bottomMargin && i != length; 
					y += lineSpace) {
				
				for (int x = leftMargin, realFontSize; ; ) {			
					final char word = text.charAt(i);
					
					if (i == length) {
						break;
					//解决换行符恰好在行末时换行会多换一行的问题。
					} else if (x >= page.getWidth() - rightMargin - fontSize && word == '\n') {
						++i;
						break;
					//解决句号和逗号等一般不能放于行首的字符在自动换行时，写于行首。
					} else if (x >= page.getWidth() - rightMargin - fontSize && !isEndChar.test(word)) {
						break;
					} else if (word == '\n') {
						++i;
						break;
					}		
					
					realFontSize = fontSize + (int)(fontSizedeviation * random.nextGaussian());
					//斜体的概率为0.5
					context.setFont(font.deriveFont(
							random.nextDouble() < 0.5 ? fontStyle : fontStyle | Font.ITALIC,
							realFontSize));

					context.drawString(String.valueOf(word), 
							x,
							y + (int)(lineSpacedeviation * random.nextGaussian()));
					++i;
					
					x += realFontSize
						 + wordSpace+(int)(wordSpacedeviation * random.nextGaussian())
						 - (isHalfChar.test(text.charAt(i - 1)) ? fontSize / 2 : 0);		
				}
			}
			context.dispose();
			article.add(page);
		}
		
		return article;
	}
	
	private static final BufferedImage deepCopy(final BufferedImage origin) {
	    BufferedImage clone = new BufferedImage(origin.getWidth(),
	            origin.getHeight(), 
	            origin.getType());
	    Graphics2D g2d = clone.createGraphics();
	    g2d.drawImage(origin, 0, 0, null);
	    g2d.dispose();
	    return clone;
	}
	
}