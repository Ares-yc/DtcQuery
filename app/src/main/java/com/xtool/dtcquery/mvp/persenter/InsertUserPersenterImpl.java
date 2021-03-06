package com.xtool.dtcquery.mvp.persenter;

import android.content.Context;

import com.xtool.dtcquery.R;
import com.xtool.dtcquery.entity.UserDTO;
import com.xtool.dtcquery.mvp.model.InsertUserModel;
import com.xtool.dtcquery.mvp.model.InsertUserModelImpl;
import com.xtool.dtcquery.mvp.view.InsertUserView;
import com.xtool.dtcquery.mvp.view.LoginFragment;

import java.util.List;

import cn.smssdk.SMSSDK;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xtool on 2017/10/31.
 */

public class InsertUserPersenterImpl implements InsertUserPersenter {

    private Context context;
    private InsertUserView view;
    private InsertUserModel model;

    public InsertUserPersenterImpl(Context context,InsertUserView view) {
        this.context = context;
        this.view = view;
        model = new InsertUserModelImpl();
    }

    @Override
    public void switchLoginFragment() {
        view.switchFragment(new LoginFragment());
    }

    @Override
    public void checkSMSCode() {
        if (checkEdit() != null) {
            view.showProgressDialog();
            SMSSDK.submitVerificationCode("86", view.getUserName(), view.getSmsCode());//提交短信验证码，在监听中返回
        }else {
            view.showToast(context.getString(R.string.edittext_not_empty));
        }
    }
    /**
     * 发送验证码
     */
    @Override
    public void sendSMSCode() {
        if(view.getUserName() != null && !view.getUserName().equals("")) {
            view.startCountDownTimer();
            SMSSDK.getVerificationCode("86",view.getUserName());
        }else {
            view.showToast(context.getString(R.string.phone_not_empty));
        }
    }

    /**
     * 访问服务器注册
     */
    @Override
    public void regist() {
            UserDTO userDTO = new UserDTO();
            userDTO.setUname(view.getUserName());
            userDTO.setUpassword(view.getNewPassword());
            userDTO.setIslogin("logout");
            model.postInsertUser(userDTO)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<List<UserDTO>>() {

                        @Override
                        public void onNext(@NonNull List<UserDTO> userDTOs) {
                            view.showToast(context.getString(R.string.regist_success));
                            view.finishCountDownTimer();
                            switchLoginFragment();
                            view.dismissProgressDialog();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            view.showToast(context.getString(R.string.regist_fail));
                            view.dismissProgressDialog();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
    }

    /**
     *
     * @return null输入有误
     */
    private String checkEdit() {
        if(view.getNewPassword() != null && view.getNewPassword2() != null
                && view.getUserName() != null
                && view.getSmsCode() != null) {
            if(view.getNewPassword().equals(view.getNewPassword2())) {
                return view.getNewPassword();
            }
        }
        return null;
    }
}
