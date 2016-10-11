# LittleFinger
一款将电子文本转化为中文手写笔迹的图片的开源免费软件。
##使用须知
LittleFinger，一款将电子文本转化为中文手写笔迹的图片的开源免费软件。<br>
此软件基于命令行界面,通过引入随机性来模仿人类(特别是本人)手写。本软件需要使用者自行编辑配置文件，本软件的大部分参数均从配置文件中读取。使用本软件时建议在此程序的同文件夹下新建以下文件。<br>
<br>
****************************************<br>
* LittleFinger.jar<br>
* template
	* default<br>
		* template.properties<br>
		* background.png<br>
	* template1<br>
		* template.properties<br>
		* background.jpg<br>

****************************************<br>
<br>
文件夹default和template1均为存放各自模板信息的文件夹。模板文件夹存放于文件夹template内。<br>
<br>
注：配置文件与待处理文本文件均需用UTF-8格式编码。<br>
##核心算法
本程序主要通过java.awt.Graphics2D类的drawString方法来实现绘图。通过引入均匀概率分布和正态分布来使字体大小、字型、字间距和行间距具有一定的随机性，进而模仿手写。
##编程风格
请使新添加代码的风格与原来的代码风格相符。
