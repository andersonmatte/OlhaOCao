package br.com.andersonmatte.olhaocao.activities;

import android.os.Bundle;

import br.com.andersonmatte.olhaocao.base.ActivityMenuBase;
import br.com.andersonmatte.olhaocao.R;

public class HomeActivity extends ActivityMenuBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home);
    }

}
