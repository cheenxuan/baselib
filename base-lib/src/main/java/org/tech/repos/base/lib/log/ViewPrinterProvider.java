package org.tech.repos.base.lib.log;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.tech.repos.base.lib.utils.DisplayUtil;


/**
 * Author: xuan
 * Created on 2021/3/19 11:09.
 * <p>
 * Describe:
 */
public class ViewPrinterProvider {
    private FrameLayout rootView;
    private View floatingView;
    private boolean isOpen;
    private FrameLayout logView;
    private RecyclerView recyclerView;
    private static final String TAG_FLOATING_VIEW = "TAG_FLOATING_VIEW";
    private static final String TAG_LOG_VIEW = "TAG_LOG_VIEW";

    public ViewPrinterProvider(FrameLayout rootView, RecyclerView recyclerView) {
        this.rootView = rootView;
        this.recyclerView = recyclerView;
    }

    /**
     * 展示floatView 悬浮按钮
     */
    public void showFloatingView() {
        if (rootView.findViewWithTag(TAG_FLOATING_VIEW) != null)
            return;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        View floatingView = geneFloatingView();
        floatingView.setTag(TAG_FLOATING_VIEW);
        floatingView.setBackgroundColor(Color.BLACK);
        floatingView.setAlpha(0.8f);
        params.bottomMargin = DisplayUtil.INSTANCE.dp2px(100f, rootView.getResources());
        params.rightMargin = DisplayUtil.INSTANCE.dp2px(16, rootView.getResources());
        rootView.addView(geneFloatingView(), params);
    }

    /**
     * 关闭floatView 悬浮按钮
     */
    public void closeFloatingView() {
        rootView.removeView(geneFloatingView());
    }

    private View geneFloatingView() {
        if(floatingView != null)
            return floatingView;
        TextView textView = new TextView(rootView.getContext());
        textView.setOnClickListener(v -> {
            if(!isOpen)
                showLogView();
        });
        textView.setText("VLog");
        return floatingView = textView;
    }

    private void showLogView() {
        if(rootView.findViewWithTag(TAG_LOG_VIEW) != null)
            return;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.INSTANCE.dp2px(160,rootView.getResources()));
        params.gravity = Gravity.BOTTOM;
        View logView = geneLogView();
        logView.setTag(TAG_LOG_VIEW);
        rootView.addView(geneLogView(),params);
    }

    private View geneLogView() {
        if(logView != null)
            return logView;
        FrameLayout logView = new FrameLayout(rootView.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logView.setBackgroundColor(Color.BLACK);
        logView.addView(recyclerView,params);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        TextView closeView = new TextView(rootView.getContext());
        closeView.setOnClickListener(v -> closeLogView());
        closeView.setText("Close");
        logView.addView(closeView,params);
        return this.logView = logView;
    }

    private void closeLogView() {
        isOpen = false;
        rootView.removeView(geneLogView());
    }
    
}
