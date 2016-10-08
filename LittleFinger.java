//�����㷨���������õĺ���.

import java.util.LinkedList;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import static java.lang.Math.random;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import static java.lang.Math.round;

//TODO:ʹ��java.util.Random����Math.random()��normsDists()��
public final class LittleFinger {

	//write��������ģ��������(�ر��Ǳ���)��дʱ��������֮��Ĺ�ϵ.
	//1.б�����б��ʹ�þ��ȸ��ʷֲ�.
	//2.�����С,���������м��ʹ����̬�ֲ�.
	//3.�����С���м���ƽ������Ϊ�������;�������ƽ�������������,�����ۻ�ЧӦ.
	//	����֮,ÿһ�е�������һ�����,����ÿһҳ���������һ�����.
	//������㷨�������²���:
	//1.���㷨���ܱ�֤���岻�ᳬ���±߾����߾�;���������������,�ʲ��ܱ�֤���岻�����±߾����ұ߾�.
	//��ʵҲ���Խ���������;������Ȼ���ߴ���ĸ��ӳ̶�,Ҳ�������µ�ϸ������.
	//2.Ӣ�ĵ����������˳����Զ�����ʱ���ܻᵼ�µ��ʷֳ��������Ρ�
	//ע:
	//1.����������,�м��͸�ҳ�߾��Ϊ����;��Ϊ�˳�����ֳ������,����ͼƬ����Ӧ�㹻��,������ֵҲ����Ӧ����
	//2.�ڶ��߳�������,Ӧ���Ǹ�д����,Ϊÿ���̴߳���������������������棻
	//3.�β�font�����㷨��ʵ�����õ����壬���������й�������Ϣ�����͡��Ƿ�ʹ�ô���������С���ķ�װ������font��styleֻ����BOLD��PLAIN��
	//4.���������Ϊ�������½Ƕ�������ꡣ
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
					}else if(x>=page.getWidth()-rightMargin-fontSize && word=='\n'){//������з�ǡ������ĩʱ���л�໻һ�е����⡣
						++i;
						break;
					}else if(x>=page.getWidth()-rightMargin-fontSize && !isEndChar(word)){//�����źͶ��ŵ�һ�㲻�ܷ������׵��ַ����Զ�����ʱ��д�����ס�
						break;
					}else if(word=='\n'){
						++i;
						break;
					}		
					
					realFontSize=fontSize+normsDist(fontSizeVariance);
					//б��ĸ���Ϊ0.5
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
	
	//��̬�ֲ���Ӧ����ֵΪ��������.����Ϊ������write����,�콫�������͸�Ϊint.
	private final static int normsDist(final double variance){
		//��ʽ�Ӹ�����̬�ֲ������ķ������Ƶ�����.
		return (int)round(sqrt(-2*variance*variance*log(random())));
	}

	//�жϴ����ַ��Ƿ�ռλ���٣��磺Ӣ����ĸ�����֣������ڽ��ռλ�����ַ�֮�������Ĳ��㡣
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
	
	//�Դ������ı�����Ҫ��Ԥ����
	//����������Ӣ�Ĳ�𲻴�ı�����ȫ������Ӣ�ĵı�㡣
	private final static void preprocess(char[] text){
		for(int i=0; i<text.length; ++i){
			switch(text[i]){
			case '��':
				text[i]='(';
				break;
			case '��':
				text[i]=')';
				break;
			case '��':
				text[i]='[';
				break;
			case '��':
				text[i]=']';
				break;
			case '��':
				text[i]=',';
				break;
			case '��':
				text[i]='!';
				break;
			case '��':
				text[i]='?';
				break;
			case '��':
				text[i]='"';
				break;
			case '��':
				text[i]='"';
				break;
			case '��':
				text[i]='\'';
				break;
			case '��':
				text[i]='\'';
				break;
			case '��':
				text[i]=':';
				break;
			case '��':
				text[i]=';';
				break;
			}
		}
	}
	
	//�ж�ĳ���ַ��Ƿ��ܷ������ס�
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