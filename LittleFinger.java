//核心算法及其所调用的函数.

import java.util.LinkedList;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import static java.lang.Math.random;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import static java.lang.Math.round;

//TODO:使用java.util.Random代替Math.random()和normsDists()。
public final class LittleFinger {

	//write方法尽量模仿在人类(特别是本人)手写时的字与字之间的关系.
	//1.斜体与非斜体使用均匀概率分布.
	//2.字体大小,字体间距与行间距使用正态分布.
	//3.字体大小与行间距的平均数均为非随机数;字体间距的平均数含有随机数,具有累积效应.
	//	换言之,每一行的字数不一定相等,但是每一页的最大行数一定相等.
	//另外此算法存在以下不足:
	//1.此算法仅能保证字体不会超过下边距和左边距;但由于引入随机性,故不能保证字体不超过下边距与右边距.
	//其实也可以解决这个不足;但是这既会提高代码的复杂程度,也会引入新的细节问题.
	//2.英文单词在遇到此程序自动换行时可能会导致单词分成上下两段。
	//注:
	//1.由于字体间距,行间距和各页边距均为整型;故为了充分体现出随机性,背景图片像素应足够大,上述各值也须相应增大；
	//2.在多线程情形下,应考虑改写代码,为每个线程创建独立的随机数生成引擎；
	//3.形参font并非算法中实际运用的字体，而仅仅是有关字体信息（字型、是否使用粗体和字体大小）的封装，所以font的style只能是BOLD或PLAIN；
	//4.字体的坐标为字体左下角顶点的坐标。
	public final static LinkedList<BufferedImage> write(
			final BufferedImage background,
			char[] text,
			final Font font,
			final Color color,
			final int wordSpace,
			final int lineSpace,
			final int topMargin,
			final int bottomMargin,
			final int leftMargin,
			final int rightMargin,
			final double fontSizeVariance,
			final double wordSpaceVariance,
			final double lineSpaceVariance){
		
		preprocess(text);
		final int fontSize=font.getSize();	
		final int fontStyle=font.getStyle();
		LinkedList<BufferedImage> article=new LinkedList<BufferedImage>();
		
		for(int i=0; i!=text.length; ){
			BufferedImage page=deepCopy(background);
			Graphics2D context=page.createGraphics();
			context.setColor(color);
			
			for(int y=topMargin+fontSize;
				y<page.getHeight()-bottomMargin && i!=text.length;
				y+=lineSpace){
				
				for(int x=leftMargin,realFontSize; ;){			
					final char word=text[i];
					
					if(i==text.length){
						break;
					}else if(x>=page.getWidth()-rightMargin-fontSize && word=='\n'){//解决换行符恰好在行末时换行会多换一行的问题。
						++i;
						break;
					}else if(x>=page.getWidth()-rightMargin-fontSize && !isEndChar(word)){//解决句号和逗号等一般不能放于行首的字符在自动换行时，写于行首。
						break;
					}else if(word=='\n'){
						++i;
						break;
					}		
					
					realFontSize=fontSize+normsDist(fontSizeVariance);
					//斜体的概率为0.5
					context.setFont(font.deriveFont( 
									random()<0.5 ? fontStyle : fontStyle|Font.ITALIC, 
									realFontSize));

					context.drawString(String.valueOf(word), x, y+normsDist(lineSpaceVariance));
					++i;
					
					x+=realFontSize+wordSpace+normsDist(wordSpaceVariance)-(isHalfChar(text[i-1]) ? fontSize/2 : 0);
					
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
	
	//正态分布本应返回值为浮点类型.但是为适用于write方法,遂将部分类型改为int.
	private final static int normsDist(final double variance){
		//此式子根据正态分布函数的反函数推导而得.
		return (int)round(sqrt(-2*variance*variance*log(random())));
	}

	//判断传入字符是否占位过少（如：英文字母和数字），用于解决占位较少字符之间间距过大的不足。
	private final static boolean isHalfChar(final char c){
		//所有英文字母和阿拉伯数字
		if(c>='0' && c<='9') return true;
		if(c>='a' && c<='z') return true;
		if(c>='A' && c<='Z') return true;
		if(c>='α' && c<='ω') return true;
		if(c>='Α' && c<='Ω') return true;
		//英文标点
		switch(c){
		case ' ':
		case '~':
		case '`':
		case '!':
		case '@':
		case '#':
		case '$': case '￥':
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
		case '°': case '′': case '″':
		case '·': case '、': case '。':
			return true;
		}
		return false;
	}
	
	//对待处理文本做必要的预处理。
	//将中文中与英文差别不大的标点符号全部换成英文的标点。
	private final static void preprocess(char[] text){
		for(int i=0; i<text.length; ++i){
			switch(text[i]){
			case '（':
				text[i]='(';
				break;
			case '）':
				text[i]=')';
				break;
			case '【':
				text[i]='[';
				break;
			case '】':
				text[i]=']';
				break;
			case '，':
				text[i]=',';
				break;
			case '！':
				text[i]='!';
				break;
			case '？':
				text[i]='?';
				break;
			case '“':
				text[i]='"';
				break;
			case '”':
				text[i]='"';
				break;
			case '‘':
				text[i]='\'';
				break;
			case '’':
				text[i]='\'';
				break;
			case '：':
				text[i]=':';
				break;
			case '；':
				text[i]=';';
				break;
			}
		}
	}
	
	//判断某个字符是否不能放于行首。
	private final static boolean isEndChar(final char c){
		switch(c){
		case ',':
		case ';':
		case '.':
		case '。':
		case ')':
		case ']':
		case '}':
		case '>':
		case '》':
		case '!':
		case '?':
		case '、':
		case '°': case '′': case '″':
		case '℃': case '℉':
			return true;		
		}
		return false;
	}
}