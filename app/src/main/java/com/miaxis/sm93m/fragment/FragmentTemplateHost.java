package com.miaxis.sm93m.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.miaxis.sm93m.MainViewModel;
import com.miaxis.sm93m.R;
import com.miaxis.sm93m.databinding.FragmentTemplateBinding;
import com.miaxis.sm93m.databinding.FragmentTemplateHostBinding;

import java.util.Objects;

/**
 * @author Tank
 * @date 2022/5/17 1:15 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FragmentTemplateHost extends BaseBindingFragment<FragmentTemplateHostBinding> implements View.OnClickListener {
    private MainViewModel mMainViewModel;
    private boolean isFirst = true;

    public FragmentTemplateHost(String title) {
        super(title);
    }

    public FragmentTemplateHost() {
        super("Template");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(mMainViewModel);
        binding.setOnClickListener(this);

        binding.hostSpTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMainViewModel.hostFeatureType.getValue() == null) return;
                if (mMainViewModel.hostFeatureType.getValue() != position) {
                    mMainViewModel.hostFeatureType.setValue(position);
                    mMainViewModel.changeAlgorithm(getResources().getStringArray(R.array.template)[position]);
                }
                if (isFirst) {
                    isFirst = false;
                    mMainViewModel.changeAlgorithm(getResources().getStringArray(R.array.template)[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_template_host;
    }

    @Override
    protected void initView(@NonNull FragmentTemplateHostBinding binding, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.hostBtnEnroll.getId()) {
            mMainViewModel.hostEnroll();
        } else if (id == binding.hostBtnVerify.getId()) {
            mMainViewModel.hostVerify();
        } else if (id == binding.hostBtnSearch.getId()) {
            mMainViewModel.hostSearch();
        } else if (id == binding.hostBtnRemove.getId()) {
            mMainViewModel.hostRemove();
        } else if (id == binding.hostBtnClear.getId()) {
            if (mMainViewModel.hostFeatureType.getValue() == null) return;

            String typeName = getResources().getStringArray(R.array.template)[mMainViewModel.hostFeatureType.getValue()];
            new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setTitle("Notice")
                    .setMessage(String.format("Do you want to clear all %s template ?", typeName))
                    .setPositiveButton("YES", (dialog, which) -> mMainViewModel.hostClear(typeName))
                    .setNegativeButton("Cancel", null)
                    .show();

        } else if (id == binding.hostBtnDbShow.getId()) {
            mMainViewModel.hostShowDB();
        }
    }
}
