Smart Mobile Meteorological Station

Let' go!

------------------------------------

Description of the code


Android app code ---->>>> android file

Server api code  ---->>>> api file

Webpage code     ---->>>> web file 


You can go into the folder to check it yourself.

---------------------------------------

Code Review Principle

1，代码的组织是否合理？每个类和函数的名字是否匹配，

2，是否乱用 public和private 变量，是否有直接引用其他累变量的行为

3，数据类型是否合理，比如该用byte的，是否用了int或者long

4,  如果有2段重复的代码，是否抽出一个函数或者类进行复用

5，类和类之间的是紧耦合还是低耦合？

6，每个单独的类是否可测？

7，线程安全性

8，是否在UI线程里面做了长操作？

9，内存占用率，运行时和后台

10，是否符合 设计模式？

11，coding style

       a. 一行的长度是否超过80， 或者100， 或者120

       b, 类名，函数名

       c, 空格，是否乱用 tab

       d, 变量赋值是否对齐

       