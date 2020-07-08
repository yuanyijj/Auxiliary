package com.component.learning.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.component.learning.MainActivity;
import com.component.learning.R;
import com.component.learning.base.AppApplication;
import com.component.learning.databinding.ActivityMainBinding;
import com.component.learning.databinding.ToastBinding;
import com.component.learning.toast.XToast;
import com.component.learning.utils.LogUtil;
import com.component.learning.utils.ServiceUtils;

import java.util.LinkedList;
import java.util.List;

import static com.component.learning.utils.AbstractTF.isEmptyArray;


/**
 * 电视台观看视频的时候，是不会有点阅号的。
 * 阅读文章的时候要特别关注，是不是会有。
 */
public class XueXiService extends AccessibilityService {
    LinkedList linkedList = new LinkedList();//用来存放当天视频主题，文章主题。防止看重复

    private final String TAG = getClass().getName();
    public static int videoNum = 0;
    public static int television_videoNum = 0;
    public static int bookNum = 0;
    public static int collectNum = 0;


    //首页——视频
    private String video = "cn.xuexi.android:id/home_bottom_tab_button_ding";
    //首页——文章
    private String article = "cn.xuexi.android:id/home_bottom_tab_button_mine";
    //首页——电视台——长视频
    private String television = "cn.xuexi.android:id/home_bottom_tab_button_contact";

    //分数
    private String score = "cn.xuexi.android:id/comm_head_xuexi_score";


    private long videoTime = 10 * 1000;
    private int videoNumMax = 6;

    private long videoTime_television = 10 * 1000;
    private int videoNumMax_television = 6;

    private long bookTime = 10 * 1000;
    private int bookNumMax = 6;

//    private long videoTime = 20 * 1000;
//    private int videoNumMax = 1;
//    private long bookTime = 20 * 1000;
//    private int bookNumMax = 1;

    private String BOOK_KEY = "bookkey";

    private boolean isSearWebView = false;//是否去搜寻webview的Des字段（）

    public static XueXiService mService;
    Path path;
    /*Back(-1, "返回"),
    Home_Refresh(0, "刷新"),
    Home_Video(1, "百灵——视频"),
    Home_Television(2, "电视台——长视频"),
    Home_Article(3, "电台——文章"),
    Video_play(4, "视频——播放"),
    Article_collection(5, "文章——收藏"),
    Article_share(6, "文章——分享"),
    Article_share_secondary(7, "文章——分享——点击分享");*/
    private final String TAG1 = "handleMessage";
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void handleMessage(Message msg) {
            LogUtil.v(TAG1, "what:" + msg.what + ";");
            switch (msg.what) {
                case -1://点击返回

                    break;
                case 10://短视频返回
                    if (!isHome()) {
                        isSearWebView = false;
                        updateShow("全局检测，非主页视频界面返回" + isPingLun());
                        XueXiService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        LogUtil.v(TAG1, "videoNum=" + videoNum + ";videoNumMax=" + videoNumMax);
                        if (videoNum >= videoNumMax) {//播放短视频完成
                            updateShow("设定的短视频数量，已经播放完毕" + videoNum + "个短视频");
                            ServiceUtils.performClickWithID(XueXiService.this, television);
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Television.getStatus(), 3000);//跳转播放长视频
                        } else if (videoNum < videoNumMax) {//播放的短视频小于规定的短视频个数s
                            LogUtil.v(TAG1, "播放的短视频小于规定的短视频个数:videoNum=" + videoNum + ";videoNumMax=" + videoNumMax);
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Refresh.getStatus(), 1000);//刷新
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Video.getStatus(), 3000);//跳转播放短视频
                        }
                    }
                    break;
                case 11://长视频返回
                    if (!isHome()) {
                        isSearWebView = false;
                        updateShow("全局检测，非主页视频界面返回" + isPingLun());
                        XueXiService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        LogUtil.v(TAG1, "television_videoNum=" + television_videoNum + ";videoNumMax_television=" + videoNumMax_television);
                        if (television_videoNum >= videoNumMax_television) {//播放长视频完成
                            updateShow("设定的长视频数量，已经播放完毕" + videoNum + "个短视频");
                            ServiceUtils.performClickWithID(XueXiService.this, article);
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Article.getStatus(), 3000);//跳转文章
                        } else if (television_videoNum < videoNumMax_television) {//播放的长视频小于规定的长视频个数
                            LogUtil.v(TAG1, "播放的长视频小于规定的长视频个数:television_videoNum=" + television_videoNum + ";videoNumMax_television=" + videoNumMax_television);
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Refresh.getStatus(), 1000);//刷新
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Television.getStatus(), 3000);//跳转播放长视频
                        }
                    }
                    break;
                case 12://文章返回
                    if (!isHome()) {
                        isSearWebView = false;
                        updateShow("全局检测，非主页视频界面返回" + isPingLun());
                        XueXiService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        LogUtil.v(TAG1, "bookNum=" + bookNum + ";bookNumMax=" + bookNumMax);
                        if (bookNum >= bookNumMax) {
                            updateShow("设定的文章数量，已经播放完毕" + bookNum + "个文章");
                            updateShow("可以退出");
                            mHandler.sendEmptyMessageDelayed(ActionType.Query_score.getStatus(), 3000);//查询分数
                            /*ServiceUtils.performClickWithID(XueXiService.this, article);
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Article.getStatus(), 1000);//跳转文章*/
                        } else if (bookNum < bookNumMax) {//播放的文章小于规定的文章个数
                            LogUtil.v(TAG1, "播放的文章小于规定的文章个数:bookNum=" + bookNum + ";bookNumMax=" + bookNumMax);
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Refresh.getStatus(), 1000);//刷新
                            mHandler.sendEmptyMessageDelayed(ActionType.Home_Article.getStatus(), 3000);//跳转播放长视频
                        }
                    }
                    break;
                case 0://刷新
                    updateShow("刷新页面开始滚动");
                    path = new Path();
                    path.moveTo(400, MainActivity.height / 2);
                    path.lineTo(400, 0);
                    dispatchGestureMove(path, 20);
                    break;
                case 1://播放短视频
                    clickVideo();
                    break;
                case 2://播放长视频
                    clickVideo_television();
                    break;
                case 3://首页——文章
                    if (isHome()) {
                        ServiceUtils.performClickWithID(XueXiService.this, article);
                        bookNum++;
                        updateShow("点击文章");
                        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条文章
                        isSearWebView = true;
                        if (collectNum < 4) {//去收藏
                            updateShow("收藏数量小于4，需要收藏");
                            collectNum++;
                            mHandler.sendEmptyMessageDelayed(ActionType.Article_collection.getStatus(), 2000);//去收藏
                        }
                        //去执行阅读
                        updateShow("第" + bookNum + "篇," + "阅读文章" + bookTime / 1000 + "秒，请勿关闭");
                        mHandler.sendEmptyMessageDelayed(ActionType.Article_Back.getStatus(), bookTime);
                    } else {
                        updateShow("未检测到首页，请自动恢复《学习强国》至首页");
                        mHandler.sendEmptyMessageDelayed(ActionType.Home_Article.getStatus(), 1000);
                    }
                    break;
                case 5://点击收藏
                    clickCollect();
                    mHandler.sendEmptyMessageDelayed(ActionType.Article_share.getStatus(), 1000);
                    break;
                case 6://弹出分享框
                    clickShare();
                    break;
                case 7://点击分享按钮
                    clickWXshare();
                    break;
                case 8://从强国的分享返回
                    if (!isHome()) {
                        updateShow("分享成功，从分享页面返回");
                        XueXiService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    break;
                case 9://点击分数
                    updateShow("点击分数，进入任务界面");
                    ServiceUtils.performClickWithID(XueXiService.this, score);
                    mHandler.sendEmptyMessageDelayed(999, 2000);
                    break;
                case 999://进入到分数界面之后，滑动到最下面
                    updateShow("进入到页面，准备滑动到最底部");
                    path = new Path();
                    path.moveTo(400, MainActivity.height / 2 + 200);
                    path.lineTo(400, 0);
                    dispatchGestureMove(path, 20);
                    mHandler.sendEmptyMessageDelayed(1000, 2000);
                    break;
                case 1000://点击本地频道,去看看按钮
                    updateShow("点击本地频道去看看按钮");
                    dispatchGestureClick(1050, 2130);
                    mHandler.sendEmptyMessageDelayed(1001, 2000);
                    break;
                case 1001:
                    if (isHome()) {
                        updateShow("当前处在首页，点击本地频道文章，阅读文章");
                        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条本地文章
                        mHandler.sendEmptyMessageDelayed(1002, bookTime);//查看时间，时间到达
                    } else {
                        updateShow("本地频道任务结束,返回首页");
                        mHandler.sendEmptyMessageDelayed(1002, 1000);
                    }
                    break;
                case 1002:
                    if (!isHome()) {
                        updateShow("返回，从本地任务页面返回");
                        XueXiService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    updateShow("========关闭学习辅助========");
                    updateShow("全部任务执行完毕，3秒准备关闭...");
                    mHandler.sendEmptyMessageDelayed(1011, 3000);
                case 1011:
                    if (t != null) {
                        t.cancel();
                    }
                    break;
            }
        }
    };


    public static boolean canHome = true;//是否是从首页进入


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.v(TAG, "服务已经连接");
        mService = this;
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = TYPE_WINDOW_STATE_CHANGED;
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        serviceInfo.packageNames = new String[]{"cn.xuexi.android", "com.android.systemui"};// 监控的app
        serviceInfo.notificationTimeout = 100;
        //设置可以监控webview
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        final AccessibilityServiceInfo info = getServiceInfo();
        //获取到webview的内容
        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);
    }

    AccessibilityEvent accessibilityEvent;
    String packageName = "";

    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        this.accessibilityEvent = accessibilityEvent;
        LogUtil.v("YuChangXueService", accessibilityEvent.getPackageName() + "\n" + accessibilityEvent.getClassName());
        packageName = accessibilityEvent.getPackageName().toString();
        if (!canHome) {
            return;
        }
        if (!isShow) {
            show();//开启面板
            //回到桌面
            XueXiService.this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        }
        if (isHome()) {
            canHome = false;
            updateShow("发现处于首页，请等待2秒自动操作！！！！！！！！！");
            updateShow("发现处于首页，请等待2秒自动操作！！！！！！！！！");
            updateShow("发现处于首页，请等待2秒自动操作！！！！！！！！！");
            ServiceUtils.performClickWithID(XueXiService.this, video);
            mHandler.sendEmptyMessageDelayed(ActionType.Home_Video.getStatus(), 2000);
        }

    }


    /**
     * 遍历出webview
     *
     * @param source
     */
    public void getBookWebView(AccessibilityNodeInfo source) {
        LogUtil.v("YuChangXueService", "getBookWebView");
        if (source == null) {
            return;
        }
        if (source.getChildCount() > 0) {
            for (int i = 0; i < source.getChildCount(); i++) {
                if (source.getChild(i) != null) {
                    if ("android.webkit.WebView".equals(source.getChild(i).getClassName())) {
                        if ("com.uc.webview.export.WebView".equals(source.getChild(i).getClassName())) {
                            if (source.getChild(i) != null && source.getChild(i).getContentDescription() != null) {
                                linkedList.add(source.getChild(i).getContentDescription());
                                updateShow(source.getChild(i).getContentDescription().toString());
                                LogUtil.v("YuChangXueService", "webNode.getContentDescription():" + source.getChild(i).getContentDescription());
                            } else {
                                getBookWebView(source.getChild(i));
                            }
                        }
                    }
                } else {
                    getBookWebView(source.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        updateShow("学习功能被迫中断");
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        updateShow("学习功能已关闭");
        mService = null;
    }


    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }

    //点击收藏
    public void clickCollect() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByViewId(this.getRootInActiveWindow(), edtext);
        if (accessibilityNodeInfo != null) {
            updateShow("查询到EditText控件");
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
            if (parent != null && parent.getChildCount() > 0) {
                updateShow("点击控件................" + parent.getChildCount());
                parent.getChild(5).performAction(AccessibilityNodeInfo.ACTION_CLICK);//
            }
        } else {
            updateShow("未查询到EditText控件");
        }
    }

    private String edtext = "cn.xuexi.android:id/BOTTOM_LAYER_VIEW_ID";

    private String img_gv_item = "cn.xuexi.android:id/img_gv_item";

    //点击分享按钮
    public void clickShare() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByViewId(this.getRootInActiveWindow(), edtext);
        if (accessibilityNodeInfo != null) {
            updateShow("查询到EditText控件");
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
            if (parent != null && parent.getChildCount() > 0) {
                updateShow("点击控件................" + parent.getChildCount());
                parent.getChild(6).performAction(AccessibilityNodeInfo.ACTION_CLICK);//

                mHandler.sendEmptyMessageDelayed(ActionType.Article_share_secondary.getStatus(), 1000);
            }
        } else {
            updateShow("未查询到EditText控件");
        }
    }

    //点击分享强国
    public void clickWXshare() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByViewId(this.getRootInActiveWindow(), img_gv_item);
        if (accessibilityNodeInfo != null) {
            updateShow("检测到分享按钮，不支持点击，其父布局支持");
            accessibilityNodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            mHandler.sendEmptyMessageDelayed(ActionType.Article_share_secondary_Back.getStatus(), 500);//500毫秒后，自动返回
        } else {
            updateShow("未检测到分享按钮，本次分享终止");
        }
    }


    //点击视听
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void clickSound() {
        LogUtil.v("YuChangXueService", "播放语音");
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByText(this.getRootInActiveWindow(), "欢迎发表你的观点");
        if (accessibilityNodeInfo != null && accessibilityNodeInfo.getClassName().equals("android.widget.TextView")) {
            LogUtil.v("YuChangXueService", "找到了这个控件");
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
            if (parent != null && parent.getChildCount() > 0 && parent.getChildCount() == 9) {
//                parent.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);//
//                mHandler.sendEmptyMessageDelayed(9, 1000);
                Rect rect = new Rect();
                parent.getChild(4).getBoundsInScreen(rect);
                updateShow("开启播放视听");
                dispatchGestureClick(rect.left - 10, rect.top + 10);//模拟点击最右边的更多
                LogUtil.v("YuChangXueService", "rect.left:" + rect.left);
                LogUtil.v("YuChangXueService", "rect.top:" + rect.top);
            }
        }
    }


    //点击视频
    public void clickVideo() {
        //ServiceUtils.performClickWithID(XueXiService.this, video);

        videoNum++;
        updateShow("点击短视频,视频数增加");
        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条视频新闻
        isSearWebView = true;
        updateShow("第" + videoNum + "条短视频,正在观看" + videoTime / 1000 + "秒,请勿关闭");
        mHandler.sendEmptyMessageDelayed(ActionType.Video_Back.getStatus(), videoTime);

    }

    //点击长视频
    public void clickVideo_television() {
        //ServiceUtils.performClickWithID(XueXiService.this, television);
        television_videoNum++;
        updateShow("点击长视频,视频数增加");
        dispatchGestureClick(400, MainActivity.height / 3);//模拟点击一条视频新闻
        isSearWebView = true;
        updateShow("第" + television_videoNum + "条长视频,正在观看" + videoTime_television / 1000 + "秒,请勿关闭");
        mHandler.sendEmptyMessageDelayed(ActionType.Television_Video_Back.getStatus(), videoTime_television);
    }


    /**
     * 立即发送移动的手势
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     *
     * @param path  移动路径
     * @param mills 持续总时间
     */
    @RequiresApi(24)
    public void dispatchGestureMove(Path path, long mills) {
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, mills)).build(), new AccessibilityService.GestureResultCallback() {
            public void onCompleted(GestureDescription gestureDescription) {
                LogUtil.v(TAG1, "手势更新成功");

            }

            public void onCancelled(GestureDescription gestureDescription) {
                LogUtil.v(TAG1, "手势更新失败");
            }

        }, null);
    }

    /**
     * 点击指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    @RequiresApi(24)
    public void dispatchGestureClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), new AccessibilityService.GestureResultCallback() {
            public void onCompleted(GestureDescription gestureDescription) {
                LogUtil.v(TAG1, "手势更新成功");

            }

            public void onCancelled(GestureDescription gestureDescription) {
                LogUtil.v(TAG1, "手势更新失败");
            }

        }, null);
    }

    /**
     * 由于太多,最好回收这些AccessibilityNodeInfo
     */
    public static void recycleAccessibilityNodeInfo(List<AccessibilityNodeInfo> listInfo) {
        if (isEmptyArray(listInfo)) return;
        for (AccessibilityNodeInfo info : listInfo) {
            info.recycle();
        }
    }


    /**
     * 判断当前页面view在不在
     *
     * @param packageAndId
     * @return
     */
    public boolean isViewExist(String packageAndId) {
        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        if (nodeInfo == null) {
            return false;
        }
        AccessibilityNodeInfo targetNode = ServiceUtils.findNodeInfoByViewId(nodeInfo, packageAndId);
        nodeInfo.recycle();
        if (targetNode != null) {
            return true;
        }
        return false;
    }

    /**
     * 是否处于视频的评论以及文章的评论页面
     *
     * @return
     */
    public boolean isPingLun() {
        AccessibilityNodeInfo accessibilityNodeInfo = ServiceUtils.findNodeInfoByText(this.getRootInActiveWindow(), "欢迎发表你的观点");
        if (accessibilityNodeInfo != null && accessibilityNodeInfo.getClassName().equals("android.widget.TextView")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否在首页
     *
     * @return
     */
    public boolean isHome() {
        return isViewExist("cn.xuexi.android:id/home_bottom_tab_button_ding") && isViewExist("cn.xuexi.android:id/home_bottom_tab_button_message");
    }

    public boolean isCanClickCollect() {
        return isViewExist("cn.xuexi.android:id/common_webview");
    }

    /**
     * 是否在应用内
     *
     * @param str
     * @return
     */
    public boolean isInApp(String str) {
        return str.equals("cn.xuexi.android");
    }

    XToast t;
    public static boolean isShow = false;

    public void show() {
        if (t != null) {
            t.cancel();
        }
        isShow = true;
        t = new XToast(AppApplication.getApp())
                .setView(R.layout.toast)
                // 设置成可拖拽的
                //.setDraggable()
                // 设置显示时长
                .setGravity(Gravity.CENTER)
                .setDuration(5000 * 1000)
                // 设置动画样式
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setText(R.id.tv_toast, "========开启学习辅助========\n目标学习" + videoNumMax + "个视频，每个视频花费时长"
                        + videoTime / 1000 + "秒" + ";\n目的学习" + bookNumMax + "个文章，每个文章花费" + bookTime / 1000 + "秒")
//                .setOnClickListener(R.id.tv_toast, new OnClickListener<TextView>() {
//                    @Override
//                    public void onClick(XToast toast, TextView view) {
//                        // 点击这个 View 后消失
//                        toast.cancel();
////                        mHandler.removeCallbacksAndMessages(null);
//                        // 跳转到某个Activity
//                        // toast.startActivity(intent);
//                    }
//                })
                .show();

        updateShow("请打开学习强国，使学习强国处在首页");
    }

    public void updateShow(String mes) {

        TextView textView = (TextView) t.findViewById(R.id.tv_toast);
        ScrollView scrollView = (ScrollView) t.findViewById(R.id.scrollView);
        textView.setText(textView.getText().toString() + "\n" + mes);
        scrollView.smoothScrollBy(0, 500);
    }


    public static void setZERO() {
        videoNum = 0;
        bookNum = 0;
        collectNum = 0;
        isShow = false;
        XueXiService.canHome = true;
    }


}