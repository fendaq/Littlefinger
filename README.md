# LittleFinger
一款将电子文本转化为中文手写笔迹的图片的开源免费软件。
##使用须知
LittleFinger，一款将电子文本转化为中文手写笔迹的图片的开源免费软件。<br>
此软件基于命令行界面,通过引入随机性来模仿人类(特别是本人)手写。本软件需要使用者自行编辑配置文件，本软件的大部分参数均从配置文件中读取。使用本软件时建议在此程序的同文件夹下新建以下文件。<br>
<br>
****************************************<br>
* LittleFinger.jar<br>
* default<br>
	* setting.properties<br>
	* background.png<br>
* template1<br>
	* setting.properties<br>
	* background.jpg<br>

******************************************<br>
<br>
文件夹default和template1均为存放各自模板信息的文件夹。配置文件setting.properties中须包含程序运行时所必须参数。以下为一个配置文件例子。<br>
<br>
*****************************************************************************************************<br>
\#--LittleFinger--<br>
\#default 模板配置文件<br>
\#注:配置文件与待处理文本文件均需用UTF-8格式编码。<br>
backgroundFileName = background.png<br>
\#背景图片文件名（该图片须与此配置文件同文件）<br>
outputFormatName = png<br>
\#输出图片格式<br>
fontFamily = 宋体<br>
\#字体(须为系统字库中存在的字体)<br>
rgbR = 0<br>
rgbG = 0<br>
rgbB = 0<br>
\#字体颜色(区间[0,255]内的整数)<br>
useBold = false<br>
\#使用粗体(true\false)<br>
topMargin = 350<br>
\#纸张上侧留白（像素）<br>
bottomMargin = 100<br>
\#纸张下侧留白（像素）<br>
leftMargin = 60<br>
\#纸张左侧留白（像素）<br>
rightMargin = 60<br>
\#纸张右侧留白（像素）<br>
fontSize = 40<br>
\#字体大小（正整数）<br>
wordSpace = 5<br>
\#字体间距（像素）<br>
lineSpace = 10<br>
\#行间距（像素）<br>
fontSizeVariance = 2<br>
\#字体大小正态分布方差（非负实数）<br>
wordSpaceVariance = 2<br>
\#字体间距正态分布方差（非负实数）<br>
lineSpaceVariance = 2<br>
\#行间距正态分布方差（非负实数）<br>
**************************************************************************************************************<br>
<br>
注：配置文件与待处理文本文件均需用UTF-8格式编码。<br>
##核心算法
本程序主要通过java.awt.Graphics2D类的drawString方法来实现绘图。通过引入均匀概率分布和正态分布来使字体大小、字型、字间距和行间距具有一定的随机性，进而模仿手写。
##编程风格
请使新添加代码的风格与原来的代码风格相符。
