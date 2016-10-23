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

//�����㷨���������õĺ���.

import java.util.LinkedList;

import java.util.Random;
import java.util.function.Predicate;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public final class LittleFinger {
	//TODO:debug:������ɫ��color��һ�¡�
	
	//write��������ģ��������(�ر��Ǳ���)��дʱ��������֮��Ĺ�ϵ.
	//1.б�����б��ʹ�þ��ȸ��ʷֲ�.
	//2.�����С,���������м��ʹ����̬�ֲ�.
	//3.�����С���м���ƽ������Ϊ�������;�������ƽ�������������,�����ۻ�ЧӦ.
	//	����֮,ÿһ�е�������һ�����,����ÿһҳ���������һ�����.
	//������㷨�������²���:
	//1.���㷨���ܱ�֤���岻�ᳬ���±߾����߾�;���������������,�ʲ��ܱ�֤���岻�����ϱ߾����ұ߾�.
	//��ʵҲ���Խ���������;������Ȼ���ߴ���ĸ��ӳ̶�,Ҳ�������µ�ϸ������.
	//2.Ӣ�ĵ����������˳����Զ�����ʱ���ܻᵼ�µ��ʷֳ��������Ρ�
	//ע:
	//1.����������,�м��͸�ҳ�߾��Ϊ����;��Ϊ�˳�����ֳ������,����ͼƬ����Ӧ�㹻��,������ֵҲ����Ӧ����
	//2.�β�font�����㷨��ʵ�����õ����壬���������й�������Ϣ�����͡��Ƿ�ʹ�ô���������С���ķ�װ������font��styleֻ����BOLD��PLAIN��
	//3.���������Ϊ�������½Ƕ�������ꣻ
	//4.ʹ�ô��㷨ʱ��Ҫ���������ɵ��ö���isHalfChar��isEndChar���ַ������жϡ�
	public final static LinkedList<BufferedImage> write(
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
			final Predicate<Character> isEndChar){
		
		Random random=new Random();
		final int fontSize=font.getSize();	
		final int fontStyle=font.getStyle();
		final int length=text.length();
		LinkedList<BufferedImage> article=new LinkedList<BufferedImage>();
		
		for(int i=0; i!=length; ){
			BufferedImage page=deepCopy(background);
			Graphics2D context=page.createGraphics();
			context.setColor(color);
			
			for(int y=topMargin+fontSize;
				y<page.getHeight()-bottomMargin && i!=length;
				y+=lineSpace){
				
				for(int x=leftMargin,realFontSize; ;){			
					final char word=text.charAt(i);
					
					if(i==length){
						break;
					//������з�ǡ������ĩʱ���л�໻һ�е����⡣
					}else if(x>=page.getWidth()-rightMargin-fontSize && word=='\n'){
						++i;
						break;
					//�����źͶ��ŵ�һ�㲻�ܷ������׵��ַ����Զ�����ʱ��д�����ס�
					}else if(x>=page.getWidth()-rightMargin-fontSize && !isEndChar.test(word)){
						break;
					}else if(word=='\n'){
						++i;
						break;
					}		
					
					realFontSize=fontSize+(int)(fontSizedeviation*random.nextGaussian());
					//б��ĸ���Ϊ0.5
					context.setFont(font.deriveFont( 
									random.nextDouble()<0.5 ? fontStyle : fontStyle|Font.ITALIC, 
									realFontSize));

					context.drawString(String.valueOf(word), 
							x,
							y+(int)(lineSpacedeviation*random.nextGaussian()));
					++i;
					
					x+=realFontSize
						+wordSpace+(int)(wordSpacedeviation*random.nextGaussian())
						-(isHalfChar.test(text.charAt(i-1)) ? fontSize/2 : 0);		
				}
			}
			context.dispose();
			article.add(page);
		}
		
		return article;
	}
	
	private final static BufferedImage deepCopy(final BufferedImage origin) {
	    BufferedImage clone = new BufferedImage(origin.getWidth(),
	            origin.getHeight(), 
	            origin.getType());
	    Graphics2D g2d = clone.createGraphics();
	    g2d.drawImage(origin, 0, 0, null);
	    g2d.dispose();
	    return clone;
	}
	
}