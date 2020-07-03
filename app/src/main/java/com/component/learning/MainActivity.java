package com.component.learning;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.component.learning.databinding.ActivityMainBinding;
import com.component.learning.service.XueXiService;
import com.component.learning.utils.LogUtil;
import com.component.learning.utils.ScreenUtils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int width, height;
    private ActivityMainBinding binding;
    private String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        height = ScreenUtils.getScreenHeight();
        width = ScreenUtils.getScreenWidth();
        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.v(TAG, "start");
                if (!XueXiService.isStart()) {
                    try {
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    } catch (Exception e) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                        e.printStackTrace();
                    }
                } else {
                    LogUtil.v(TAG, "开始执行");
                    XueXiService.setZERO();
                    moveTaskToBack(true);
                }
            }
        });
        checkPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {//有权限
            XXPermissions.with(this)
                    // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                    .constantRequest()
                    // 支持请求6.0悬浮窗权限8.0请求安装权限
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .request(new OnPermission() {

                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {

                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {

                        }
                    });
        }


    }
}