package com.miaxis.sm93m.fragment;

import android.os.Bundle;
import android.view.View;

import com.miaxis.sm93m.MainViewModel;
import com.miaxis.sm93m.R;
import com.miaxis.sm93m.databinding.FragmentInfoBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author Tank
 * @date 2022/5/17 1:15 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FragmentInfo extends BaseBindingFragment<FragmentInfoBinding> implements View.OnClickListener {
    private MainViewModel mMainViewModel;

    public FragmentInfo(String title) {
        super(title);
    }

    public FragmentInfo() {
        super("Info");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setOnClickListener(this);
        mMainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(mMainViewModel);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_info;
    }

    @Override
    protected void initView(@NonNull FragmentInfoBinding binding, @Nullable Bundle savedInstanceState) {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnDeviceVersion.getId()){
            mMainViewModel.getDeviceInfo();
        } else if (id == binding.btnAlgorithmVersion.getId()){
            mMainViewModel.getAlgorithmVersion();
        } else if (id == binding.btnSdkVersion.getId()){
            mMainViewModel.getSDKVersion();
        } else if (id == binding.btnLiveAlgVersion.getId()){
            mMainViewModel.getLiveAlgVersion();
        } else if (id == binding.btnNfiq.getId()){
            mMainViewModel.nfiq();
        } else if (id == binding.btnMinutae.getId()){
            mMainViewModel.minutae();
        }
    }
}
