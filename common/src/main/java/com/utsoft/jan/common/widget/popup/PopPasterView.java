package com.utsoft.jan.common.widget.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.utsoft.jan.common.R;
import com.utsoft.jan.common.R2;
import com.utsoft.jan.common.widget.recycle.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2019/9/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.widget.popup
 */
public class PopPasterView {

    private static final String TAG = "PopPasterView";
    private final Context mContext;

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;

    private View popupWindowView;

    private List<Integer> integerList = new ArrayList<>();

    private int[] drawables = new int[]{R.mipmap.aini, R.mipmap.baituole, R.mipmap.bufuhanzhe, R.mipmap.burangwo, R.mipmap.buyue, R.mipmap.dengliao, R.mipmap.mudengkoudai, R.mipmap.nizabushagntian, R.mipmap.nizaidouwo, R.mipmap.xiase, R.mipmap.aini, R.mipmap.zan};
    private PopupWindow popupWindow;
    private RecyclerAdapter<String> mAdapter;

    public PopPasterView(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        if (popupWindowView == null) {
            //加载
            popupWindowView = View.inflate(mContext, R.layout.pop_paster_view, null);
            ButterKnife.bind(this, popupWindowView);
            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());

            //popupWindow.setOnDismissListener();

            popupWindowView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

            initView();
        }
    }

    private void initView() {
        final MyAdapter myadapter = new MyAdapter();

        for (int drawable : drawables) {
            integerList.add(drawable);
        }
        myadapter.replace(integerList);
        recyclerView.setAdapter(myadapter);
        myadapter.setAdapterListener(new RecyclerAdapter.AdapterListener<Integer>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Integer integer) {
                if (mListener!=null)
                {
                    mListener.onItemClick(integer);
                }
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Integer integer) {

            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
    }

    public void show() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(LayoutInflater.from(mContext).inflate(R.layout.pop_paster_view, null), Gravity.BOTTOM, 0, 0);
        }
    }


    class MyAdapter extends RecyclerAdapter<Integer> {

        @Override
        protected int getItemViewType(int position, Integer s) {

            return R.layout.paster_item;
        }

        @Override
        protected ViewHolder<Integer> onCreateViewHolder(@NonNull View root, int viewtype) {

            return new MyViewHolder(root);
        }

    }

    public class MyViewHolder extends RecyclerAdapter.ViewHolder<Integer> {

        @BindView(R2.id.pasterview)
        ImageView pasterview;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Integer mData) {
            pasterview.setImageResource(mData);
        }
    }

    private onSelectListener mListener;

    public void setSelectListener(onSelectListener mListener) {
        this.mListener = mListener;
    }

    public interface onSelectListener{
        void onItemClick(int resId);
    }
}
