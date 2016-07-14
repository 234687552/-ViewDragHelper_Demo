最终效果：

![github](https://github.com/234687552/ProjectSchedule/blob/master/screenshots/result.gif "github")

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