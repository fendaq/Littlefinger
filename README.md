# 本软件已经停止开发和维护，请移步至[PyLf](https://github.com/Gsllchb/PyLf)。

# LittleFinger
一款将电子文本转化为中文手写笔迹的图片的开源免费软件。

## 安装
下载解压[Releases](https://github.com/Gsllchb/LittleFinger/releases)。

## 模板
模板位于文件夹template内，文件夹名称即模板名。各模板文件夹内存放该模板的配置文件(文件名：template.json)和背景图片。配置文件template.json的编写参考模板default。

## 核心算法
通过引入均匀概率分布和正态分布来使字体大小、字型、字间距和行间距具有一定的随机性，进而模仿手写。
