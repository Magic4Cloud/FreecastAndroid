package com.cloud4magic.freecast.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.bean.MediaBean;
import com.cloud4magic.freecast.widget.decoration.GridItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/7/10
 * Author  xiaomao
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ItemViewHolder> {

    private Context mContext;
    private List<MediaBean> mList;
    private boolean mSelect = false;
    private OnItemClickListener mListener;

    private GridItemDecoration mDecoration;

    public MediaAdapter(Context context) {
        mContext = context;
        mDecoration = new GridItemDecoration(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_library, parent, false);
        if (itemView != null) {
            return new ItemViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (mList == null || mList.size() == 0) {
            return;
        }
        holder.setBean(mList.get(position % mList.size()));
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name)
        TextView textView;
        @BindView(R.id.item_recycler_view)
        RecyclerView recyclerView;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setBean(MediaBean bean) {
            if (bean != null) {
                textView.setText(bean.getName());
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.removeItemDecoration(mDecoration);
                recyclerView.addItemDecoration(mDecoration);
                ChildAdapter adapter = new ChildAdapter(bean.getList());
                recyclerView.setAdapter(adapter);
            }
        }

    }

    class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

        private List<String> mData;

        ChildAdapter(List<String> data) {
            mData = data;
        }

        @Override
        public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_child, parent, false);
            if (itemView != null) {
                return new ChildViewHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ChildViewHolder holder, int position) {
            if (mData == null || mData.size() == 0) {
                return;
            }
            holder.setBean(mData.get(position % mData.size()), position);
        }

        @Override
        public int getItemCount() {
            if (mData == null) {
                return 0;
            }
            return mData.size();
        }

        class ChildViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.item_layout)
            RelativeLayout itemLayout;
            @BindView(R.id.item_image)
            ImageView itemImage;
            @BindView(R.id.item_select)
            ImageView itemSelect;

            ChildViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void setBean(final String path, final int position) {
                // set LayoutParams
                ViewGroup.LayoutParams params = itemImage.getLayoutParams();
                params.width = getItemWidth();
                params.height = getItemWidth();
                itemImage.setLayoutParams(params);
                // show image
                Glide.with(mContext)
                        .load(path)
                        .placeholder(R.drawable.shape_cover)
                        .error(R.drawable.shape_cover)
                        .into(itemImage);
                itemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            if (mSelect) {
                                itemSelect.setSelected(!itemSelect.isSelected());
                                mListener.onItemSelected(itemSelect.isSelected(), position, path);
                            } else {
                                mListener.onItemClick(position, path);
                            }
                        }
                    }
                });
                // show select image
                if (mSelect) {
                    itemSelect.setVisibility(View.VISIBLE);
                } else {
                    itemSelect.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setData(List<MediaBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setSelect(boolean select) {
        mSelect = select;
        if (mList == null) {
            return;
        }
        notifyItemRangeChanged(0, mList.size());
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String path);

        void onItemSelected(boolean selected, int position, String path);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private int getItemWidth() {
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        return (int) ((float) screenWidth / 3.0);
    }

}
