package com.clang.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.xingzhao.gamesdk.ClGameSdk;
import com.xingzhao.gamesdk.bean.ClGameSdkUserInfoBean;
import com.xingzhao.gamesdk.bean.pay.ClGameSdkPayGoodsInfo;
import com.xingzhao.gamesdk.bean.pay.ClGameSdkPayInfo;
import com.xingzhao.gamesdk.callback.ClGameSdkInitCallback;
import com.xingzhao.gamesdk.callback.ClGameSdkPayCallback;
import com.xingzhao.gamesdk.callback.ClGameSdkUserCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 张鹏飞
 * @Date: 2025/8/8
 * @Desc:
 */
public class MainActivity extends AppCompatActivity {

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        initSdk(null);

        Button loginBtn = findViewById(R.id.btn_login);
        Button logoutBtn = findViewById(R.id.btn_logout);
        Button showFloatingBtn = findViewById(R.id.btn_show_float);
        Button payBtn = findViewById(R.id.btn_pay);
        EditText etOrder = findViewById(R.id.et_name);

        showFloatingBtn.setOnClickListener(v -> {
            ClGameSdk.getUid();
            ClGameSdk.showFloatingView(true);
        });

        loginBtn.setOnClickListener(v -> {
            initSdk(() -> ClGameSdk.login());
        });

        logoutBtn.setOnClickListener(v -> {
            ClGameSdk.logout();
        });

        ClGameSdk.setOnUserCallback(new ClGameSdkUserCallback() {
            @Override
            public void onLogout(String openid) {
                showToast("退出登录 openid: " + openid);
                super.onLogout(openid);
            }

            @Override
            public void onLoginSuccess(ClGameSdkUserInfoBean userInfo) {
                showToast("登陆成功 openid: " + (userInfo != null ? userInfo.getOpenId() : null));
            }

            @Override
            public void onError(int code, String msg) {
                showToast("登陆失败 code: " + code + " msg: " + msg);
            }
        });

        payBtn.setOnClickListener(v -> {
            ArrayList<ClGameSdkPayGoodsInfo> goodsInfo = new ArrayList<>();
            // 商品信息
            ClGameSdkPayGoodsInfo good1 = new ClGameSdkPayGoodsInfo("1", "钻石", 100L);
            goodsInfo.add(good1);
            // 扩展信息, 可选, 服务端接口会返回
            Map<String, byte[]> extInfo = new HashMap<>();
            extInfo.put("userId", "123456".getBytes());

            if (etOrder.getText().toString().isEmpty()) {
                showToast("请输入支付金额");
                return;
            }

            ClGameSdkPayInfo payInfo = new ClGameSdkPayInfo(
                    Long.parseLong(etOrder.getText().toString()),
                    String.valueOf(System.currentTimeMillis()),
                    goodsInfo,
                    null
            );
            ClGameSdk.pay(payInfo, new ClGameSdkPayCallback() {
                @Override
                public void onStart() {
                    // 开始支付
                    showToast("开始支付");
                }

                @Override
                public void onSuccess() {
                    // 支付成功, 不能作为真实到账的标准, 具体是否支付到账需要以服务端的通知为准, 或者查看服务端文档查看主动调用服务端接口查询
                    showToast("支付成功");
                }

                @Override
                public void onCancel() {
                    // 支付取消
                    showToast("支付取消");
                }

                @Override
                public void onError(int code, String msg) {
                    // 支付失败
                    showToast("支付失败: code: " + code + " msg: " + msg);
                }
            });
        });
    }

    private boolean isInit;

    private void initSdk(ClGameSdkCallback callback) {
        if (isInit) {
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }
        //请替换真实的AppId和AppKey
        ClGameSdk.init("123456", "123456", new ClGameSdkInitCallback() {
            @Override
            public void onSuccess() {
                isInit = true;
                if (callback != null) {
                    callback.onSuccess();
                }
                showToast("初始化成功");
            }

            @Override
            public void onError(int code, String msg) {
                showToast("初始化失败: code: " + code + " msg: " + msg);
            }
        });
    }

    public interface ClGameSdkCallback {
        void onSuccess();
    }

}
