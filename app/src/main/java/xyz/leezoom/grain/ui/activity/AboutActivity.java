/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/8/17 1:41 PM
 */

package xyz.leezoom.grain.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.leezoom.grain.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.about_version) TextView mVersion;
    @BindView(R.id.about_else) TextView mGithub;
    @BindView(R.id.about_license) TextView mLicense;
    private WebView webView;

    @OnClick(R.id.about_else)
    void toGithub(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_page)));
        startActivity(intent);
    }

    @OnClick(R.id.about_license)
    void showLicense() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        AlertDialog dialog= builder
                .setTitle("Attributions")
                .setIcon(R.mipmap.ic_reindeer)
                .setCancelable(true)
                .setView(webView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        webView.loadUrl("https://github.com/LeeReindeer/Grain/blob/master/ATTRIBUTIONS.md");
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        ButterKnife.bind(this);
        webView = new WebView(AboutActivity.this);
        webView.setWebViewClient(new WebViewClient());
        mVersion.setText("v"+getVersionName());
    }

    private String getVersionName(){
        PackageManager manager =this.getPackageManager();
        PackageInfo info = null;
        try {
            info =manager.getPackageInfo(this.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert info != null;
        return info.versionName;
    }
}
