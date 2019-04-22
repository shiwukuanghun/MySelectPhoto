package com.wuchen.myselectphoto;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    List<Uri> mSelected;
    private static final int REQUEST_CODE_CHOOSE = 100;
    private ImageView mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIv = findViewById(R.id.iv);
        requestPermission();
    }

    public void selectPhoto(View view) {
        requestPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            GlideApp.with(this).load(mSelected.get(0)).centerCrop().into(mIv);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        select();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {

            // 已经申请过权限，做想做的事
            select();
        } else {
            // 没有申请过权限，现在去申请
            EasyPermissions.requestPermissions(this, "没有权限",
                    100, perms);
        }
    }

    private void select() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .maxSelectable(9)
                .originalEnable(true)
                .maxOriginalSize(10)
                .imageEngine(new Glide4Engine())
                .capture(true)          //这个和下面这个加上就有了拍照功能
                .captureStrategy(
                        new CaptureStrategy(true, "com.wuchen.myselectphoto.fileprovider"))
                .forResult(REQUEST_CODE_CHOOSE);
    }
}
