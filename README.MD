最终效果：

![github](https://github.com/234687552/ProjectSchedule/blob/master/screenshots/0731.gif "github")

历史效果图在screenshots文件夹中

7/31

新问题：

	listview里adapter的notification要在ui线程，而adapter的数据更新要在新线程new Thread里，如果刷新数据过快会导致崩溃报错：
	The content of the adapter has changed but ListView did not receive a notification.
	Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. 
	
问题分析：
	
	刷新过快，上一次线程的数据还没有notification，下一次线程刷新来了就会导致上述问题
	
解决办法：
	
	从线程新获得的新数据先传到主线程，不要直接在子线程中为其赋值。保证数据在ui线程刷新；
	传输：msg.obj=tempDataList；
	接受：dataList=msg.obj;

参考网址：

	http://www.eoeandroid.com/thread-79950-1-1.html

7/29 

新需求：
	在项目清单上添加聊天界面
	
分析：利用 网上资源“环信”进行IM功能实现，大概框架如效果图中聊天界面 

7/20

新加需求：
	划分已完成和完成部分
	
新问题：
	当项目条目过多的时候，采用上下两个Listview来划分完成是否的 效果不好，主要是listview自身有滚动
	导致两个listview都各自上下滚动。
分析:
	采用scrollView包含两个Listview，并把两个Listview长度固定；
	
老问题：
	新添加的Item不可以马上进行滑动处理，要重新进入别的界面回来才可以
	
7.19的新问题解决：
	dialogFragment里面的内容填充逻辑有误；
	

7/19.PM

未完成部分：
	1.时间修改，2.完成条数未弄 

新问题：
	清单备注是利用dialogFragment来实现的，新添加两条后，马上删除在添加发现问题；
	dialogFragment里面的内容还是原来的内容。既是dialogFragment保留旧数据；

网上解决办法以及原因：
	fragment会保存数据，也会走相应的生命周期方法，这也是dialogfragment能在屏幕翻转时能保持状态的原因。
	fragmentmanager有自己的内存堆栈管理，show虽然重走了生命周期方法，但还是原来的对象，并由fragment transaction进行恢复。因此，题主想要解决问题，要么重新初始化dialog对象
	因此，要么重新初始化dialog对象(new)，要么就是在onViewStateRestored()中进行再次初始化一次
	最后选中了在onViewStateRestored()中进行新数据绑定；
	
还没解决问题：
	新添加的Item不可以马上进行滑动处理，要重新进入别的界面回来才可以

7/19

sql部分大部分已经完成，未完成部分：
	1.时间修改，2.完成条数未弄 3.清单备注未添加

遇到问题：
	新添加的Item不可以进行滑动处理，要重新进入别的界面回来才可以
	
分析：
	新添加的Item还是null，adapter.notifyDataSetChanged()方法并无做非null处理？


7/16

problem1:

	为了实现滑动删除的效果，需要实现第一个view满屏，滑动出来的view在屏幕外，如何实现？

1.利用linearlayout 第一个match后，第二个view的width用wrap后的宽度为0；只能指定某一个值

2.利用framelayout 能出第二个view，但?一直都在屏幕左上角出现，利用重写onlayout 并无效果，结果view还是在左上角？

现在剩余的方法是：

尝试：在linearlayout方法里面重写onlayout，结果：得出的第二个view宽度还是0；

最后手动指定第二个view的宽度

problem2:

	展开某一个Item后再手动关闭后，其他Item需要点第二次才可以滑动，逻辑问题，已解决。

7/15

上一次不良之处：

无法保证只展开一项；原因：滑动展开是Item里面OnTouch处理的，所以难以控制相互独立的其他Item展开关闭；

解决办法：每一个Item设置一个public的OpenItem，判断OptenItem是否与本人相同，不同的话关闭OptenItem；

接着每一个Item设置回调 ，监听 Item是否展开，展开就返回给MainActivity，然后借MainActivity的刀把展开的Item传入给listVIew 所有的Item；
		

学习心得：
=====
1.如果在适配器Adpter里面的getView里面直接使用return newItemView（context）的话。

	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return new ItemView(mContext);
        }
        
会导致以下问题：
		Item的滑动在ListView上下滚动后会失效
原因分析：通过在getVIew里面log出

	Log.w("getView", position+","+view );
	
可以知道滚动后ItemVIew前后的地址是不一致的，从null变成***，估计这是导致原因

解决方法：
	在return之前先判断是否为null，然后为null的话先实例出一个view即可
	
	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView view= (ItemView) convertView;
            if (view==null){
                view=new ItemView(mContext);
            }
            Log.w("getView", position+","+view );
            return view;
        }
        

2.之前点击事件都是在ItemView处理的，并且在ItemView里面的OnTouch方法里面设置返回值true，也就导致ListView 里面的OnTouch方法无效，所以导致无法实现保证只有一项展开，因为每一个Item相对都是独立的，无法控制别的Item自动关闭展开
所以现在考虑直接在ListVIew里面的OnTouch方法实现Item滑动效果

3.OnTouchEvent个人理解：
![github](https://github.com/234687552/ProjectSchedule/blob/master/screenshots/onTouchEvent.png "github")
