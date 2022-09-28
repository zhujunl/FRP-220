package com.miaxis.sm93m.fragment;

import android.os.Bundle;
import android.view.View;

import com.miaxis.sm93m.MainViewModel;
import com.miaxis.sm93m.R;
import com.miaxis.sm93m.databinding.FragmentExportBinding;

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
public class FragmentExport extends BaseBindingFragment<FragmentExportBinding> implements View.OnClickListener {
    private MainViewModel mMainViewModel;

    public FragmentExport(String title) {
        super(title);
    }

    public FragmentExport() {
        super("Export");
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_export;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewModel(mMainViewModel);
        binding.setOnClickListener(this);

    }

    @Override
    protected void initView(@NonNull FragmentExportBinding binding, @Nullable Bundle savedInstanceState) {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnGetTemplateData.getId()){
            int type = binding.spTemplate.getSelectedItemPosition();
            String typeName = getResources().getStringArray(R.array.template)[type];
            mMainViewModel.exportAsTemplate(type, typeName);
        } else if (id == binding.btnGetImageData.getId()){
            int type = binding.spImage.getSelectedItemPosition();
            String typeName = getResources().getStringArray(R.array.image)[type];
            mMainViewModel.exportAsImage(type, typeName);
        } else if (id == binding.btnGetFir2005.getId()){
            int type = binding.spFir.getSelectedItemPosition();
            String typeName = getResources().getStringArray(R.array.fir)[type];
            mMainViewModel.exportAsFIR2005(type, typeName);
        } else if (id == binding.btnGetFir2011.getId()){
            int type = binding.spFir.getSelectedItemPosition();
            String typeName = getResources().getStringArray(R.array.fir)[type];
            mMainViewModel.exportAsFIR2011(type, typeName);
        }
    }
}
