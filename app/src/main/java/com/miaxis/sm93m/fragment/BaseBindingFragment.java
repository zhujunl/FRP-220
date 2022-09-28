package com.miaxis.sm93m.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.sm93m.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @author Tank
 * @date 2021/4/25 3:59 PM
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class BaseBindingFragment<V extends ViewDataBinding> extends Fragment {

    protected V binding;
    private final String title;

    public BaseBindingFragment(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initLayout(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        initView(binding, savedInstanceState);
        initData(binding, savedInstanceState);
    }

    public String getTitle() {
        return this.title;
    }

    protected abstract int initLayout();

    protected abstract void initView(@NonNull V binding, @Nullable Bundle savedInstanceState);

    protected void initData(@NonNull V binding, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.unbind();
        }
    }

    protected void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}