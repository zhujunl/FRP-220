package com.miaxis.sm93m.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.miaxis.sm93m.MainViewModel;
import com.miaxis.sm93m.R;
import com.miaxis.sm93m.databinding.FragmentSettingBinding;

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
public class FragmentSetting extends BaseBindingFragment<FragmentSettingBinding> implements View.OnClickListener {
    private static final String TAG = FragmentSetting.class.getName();
    private MainViewModel mMainViewModel;

    public FragmentSetting(String title) {super(title);}

    public FragmentSetting() {super("Setting");}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(mMainViewModel);
        binding.setOnClickListener(this);

        mMainViewModel.communicationTypeRadio.observe(getViewLifecycleOwner(), checkedId -> {
            if (checkedId == binding.rbUsb.getId()) {
                Log.d("MainViewModel", "onCheckedChanged rbUsb");
                mMainViewModel.communicationType.setValue(0);
                binding.llCom.setVisibility(View.GONE);
                binding.btnSetBaudRate.setVisibility(View.GONE);
            } else if (checkedId == binding.rbUart.getId()) {
                Log.d("MainViewModel", "onCheckedChanged rbUart");
                mMainViewModel.communicationType.setValue(1);
                binding.llCom.setVisibility(View.VISIBLE);
                binding.btnSetBaudRate.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(@NonNull FragmentSettingBinding binding, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnSetBaudRate.getId()) {
            mMainViewModel.setBaudRate();
        }
    }
}
