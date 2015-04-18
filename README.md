# patent
This is a project for patent.
Mainly analyze xml and do some segmentations

---
#程序大体结构说明
##src文件夹
  源文件
####Config.Java
  定义了读取配置文件的load方法
  主要用到了Scanner类与JsonObject
####DBConn.Java
  一切与数据库操作有关的方法都在这个类中定义
####ExportXML.Java
  辅助类，目的是为了输出xml文件，以便于分析
####Launcher.Java
  项目入口从程序，会调用Config.Java中的load方法来读取配置文件
####OriginData.Java
  定义的一个辅助类，目的是为了比较好的传递参数
####ParseTask.Java
  核心类，run方法用来统计每一份专利中的词频
####StatisticAbstract.Java
  测试类，用来测试abstract，可以去掉
####WordCount.Java
  也是一个辅助类，与OriginData类相似
####WordCountServuce.Java
  核心类，利用单词树进行词频统计，目的是优化性能，将时间复杂度降到O（m）
  
  <img
  src="https://github.com/lihang1870719/patent/blob/master/charTree.jpg" alt="charTree.jpg" title="charTree" width="200"/>

  主要和核心思想：
1.
getWordCount由外界调用，传进来一个字符串 

2.
根据字符串调用GeneCharTree得到单词树，其中CharTreeNode定义的一个数据结构

3.
调用getWordCountFromCharTree，是利用**深度优先搜索**遍历单词树并将对应的单词放入结果集中

4.
对得到的结果集排序

说明：单词树中每个结点保存属性值cnt与指向其26个子结点的指针（每一条路径代表一个英文字母），其中cnt为到达该结点经过路径所对应的英文单词在文章中出现的次数。也就是说，我们开始读文章时让一个指针指向单词数的根结点，之后每度一个字母就让该指针指向当前结点对应路径上的子结点（若子结点为空则新建一个），一个单词读完后让当前结点的cnt值加一，并让指针重新指向根结点。

####WordPare.Java
  测试类，用于测试
####WordStatistics.Java
  统计完每一份专利的词频后，这个类将所有数据整合统计得到最终的数据
####WordStatistics2.Java
  另一种统计方法

---
##Jre system Library
  依赖库

----
##Maven Dependencis
  项目是基于Maven的，Maven可以很方便的管理一些Jar包。pom.xml中就可以设置项目依赖的jar包，从而不需要去做网上找。

----
##bin
  生成的Class文件

----
##cofig.json
  配置文件。一些可能需要修改的变量，建议大家都可以这样使用，可以降低项目的耦合，同时修改数据时不需要在源文件中大量修改，例如与数据库相关的变量。

----
##pom.xml
  设置项目依赖的jar包，从而不需要去做网上找