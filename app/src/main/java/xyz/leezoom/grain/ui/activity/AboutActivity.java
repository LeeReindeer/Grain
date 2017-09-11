package xyz.leezoom.grain.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.leezoom.grain.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.about_version) TextView mVersion;
    @BindView(R.id.about_else) TextView mGithub;

    @OnClick(R.id.about_else) void toGithub(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_page)));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        ButterKnife.bind(this);
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
