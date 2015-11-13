package com.lastwarmth.mylistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends BaseAdapter {
    private List<MyModel> data;
    private Context mContext;

    public MyAdapter(List<MyModel> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (data != null) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder holder;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            holder.imageView = (CircleImageView) contentView.findViewById(R.id.profile_image);
            holder.groupName = (TextView) contentView.findViewById(R.id.group_name);
            holder.content = (TextView) contentView.findViewById(R.id.qq_content);
            holder.toTop = (TextView) contentView.findViewById(R.id.to_top);
            holder.hadRead = (TextView) contentView.findViewById(R.id.had_read);
            holder.delete = (TextView) contentView.findViewById(R.id.delete);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        MyModel myModel = (MyModel) getItem(position);
        holder.groupName.setText(myModel.getGroupName());
        holder.content.setText(myModel.getContent());
        Picasso.with(mContext)
                .load(myModel.getImageUrl())
                .placeholder(R.mipmap.lb_zjtx)
                .into(holder.imageView);
        final MyItemLayout finalContentView = (MyItemLayout) contentView;
        holder.toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "已置顶", Toast.LENGTH_SHORT).show();
                finalContentView.smoothCloseMenu();
            }
        });
        holder.hadRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "已阅读", Toast.LENGTH_SHORT).show();
                finalContentView.smoothCloseMenu();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                finalContentView.smoothCloseMenu();
                notifyDataSetChanged();
                Toast.makeText(mContext, "已删除", Toast.LENGTH_SHORT).show();
            }
        });
        return contentView;
    }

    private static class ViewHolder {
        CircleImageView imageView;
        TextView groupName;
        TextView content;
        TextView toTop;
        TextView hadRead;
        TextView delete;
    }
}
