## 什么是沉浸式模式 ##
Android 4.4 (API Level 19)引入一个新的概念——“沉浸式模式”，即真正的全屏模式：SystemUI（StatusBar和NavigationBar）也都被隐藏，具体可以参考Android开发者官网的介绍：[Using Immersive Full-Screen Mode](https://developer.android.com/training/system-ui/immersive.html)
如果大家已经下载Android SDK的文档，那么也可以查看SDK目录下的文档：docs\training\system-ui\index.html

## 沉浸式模式的实现 ##
沉浸式模式的实现，在Android不同版本是有差异的。
### Android 4.4 (API Level 19)及以上 ###
针对Android 4.4 (API Level 19)及以上的版本，上面的版本已经介绍了如何实现沉浸式模式：

``` java
 int systemUiVisibility = decor.getSystemUiVisibility();
 int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
systemUiVisibility |= flags;
activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);             
```
### Android 4.4 以下 ###
直接给Window添加全屏标记位

``` java
activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
WindowManager.LayoutParams.FLAG_FULLSCREEN);
```
同时，为了不让布局因SystemUI的可见或隐藏而重新layout，可以给Window添加FLAG_LAYOUT_IN_SCREEN和FLAG_LAYOUT_NO_LIMITS这2个标记位，这样SystemUI出现时候是overlay在布局内容的上面。如：

``` java
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);    
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
```
## 取消沉浸式模式##
###  Android 4.4 (API Level 19)及以上 ###

```java
int systemUiVisibility = decor.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
systemUiVisibility &= ~flags;
activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);             
```
考虑Activity所使用的主题和背景，如有必要，可以设置SystemUI为半透明，否则取消沉浸式模式后SystemUI可能看不清：

```java
// Translucent status bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Translucent navigation bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
```

### Android 4.4 以下 ###

```
activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
```
## 判断是否处于沉浸式模式 ##
###  Android 4.4 (API Level 19)及以上 ###

```java
 View decor = activity.getWindow().getDecorView();
        int systemUiVisibility = decor.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        return (systemUiVisibility & flags) == flags;
```
###  Android 4.4下 ###

``` java
return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) > 0;
```
## 监听 沉浸式模式变化##

有多种方案：

 - OnSystemUiVisibilityChangeListener

```java
View decorView = getWindow().getDecorView();       decorView.setOnSystemUiVisibilityChangeListener(this::onSystemUiVisibilityChange);

void onSystemUiVisibilityChange(int visibility) {
	Log.d(TAG, "visibility=" + visibility);
}
```
改方案仅适用于Android 4.4 (API Level 19)及以上。

 - ViewTreeObserver.OnGlobalLayoutListener
 

```java
decorView.getViewTreeObserver().addOnGlobalLayoutListener(this::onGlobalLayout);

void onGlobalLayout() {
		Log.d(TAG, "isFullScreenActivity=" + isFullScreenActivity(ImmersiveModeActivity.this));
}
```
这是通用方案。
