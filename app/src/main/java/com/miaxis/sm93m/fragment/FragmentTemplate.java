package com.miaxis.sm93m.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.miaxis.sm93m.MainViewModel;
import com.miaxis.sm93m.R;
import com.miaxis.sm93m.databinding.FragmentTemplateBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

/**
 * @author Tank
 * @date 2022/5/17 1:15 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FragmentTemplate extends BaseBindingFragment<FragmentTemplateBinding> implements View.OnClickListener {
    private MainViewModel mMainViewModel;

    public FragmentTemplate(String title) {
        super(title);
    }

    public FragmentTemplate() {
        super("Template");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(mMainViewModel);
        binding.setOnClickListener(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_template;
    }

    @Override
    protected void initView(@NonNull FragmentTemplateBinding binding, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnEnroll.getId()){
            mMainViewModel.enroll();
        } else if (id == binding.btnVerify.getId()){
            mMainViewModel.verify();
        } else if (id == binding.btnSearch.getId()){
            mMainViewModel.search();
        } else if (id == binding.btnRemove.getId()){
            mMainViewModel.delete();
        } else if (id == binding.btnClear.getId()){
            new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setTitle("Notice")
                    .setMessage("Do you want to clear all fingerprints of the module ?")
                    .setPositiveButton("YES", (dialog, which) -> mMainViewModel.erase())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (id == binding.btnUpload.getId()){
            mMainViewModel.upload();
        } else if (id == binding.btnCapacity.getId()){
            mMainViewModel.getCapacity();
        }
    }
}
