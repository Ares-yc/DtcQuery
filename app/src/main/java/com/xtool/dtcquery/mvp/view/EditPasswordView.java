package com.xtool.dtcquery.mvp.view;

import android.support.v4.app.Fragment;

import com.xtool.dtcquery.base.BaseFragmentView;

/**
 * Created by xtool on 2017/10/9.
 */

public interface EditPasswordView extends BaseFragmentView{

    String getNewPassword();

    String getNewPassword2();

    String getUserName();

    void setNewPassword(String s);

    void setNewPassword2(String s);
}
