package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsuruta.meet.R;

public class UserRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    UserRecyclerAdapter parent;
    TextView tvName;
    ImageView ivUserSelect;
    LinearLayout llUserRow;
    boolean selected;

    public UserRecyclerViewHolder(UserRecyclerAdapter parent, View itemView)
    {
        super(itemView);
        this.parent = parent;
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        llUserRow = (LinearLayout) itemView.findViewById(R.id.llUserRow);
        ivUserSelect = (ImageView) itemView.findViewById(R.id.ivUserSelect);
        llUserRow.setOnClickListener(this);
    }

    public void setTvName(String name)
    {
        tvName.setText(name);
    }

    @Override
    public void onClick(View v)
    {
        if (v == llUserRow)
        {
            if (selected)
            {
                selected = false;
                ivUserSelect.setVisibility(View.GONE);
                int position = getLayoutPosition();
                parent.userClicked(position, selected);
            }
            else
            {
                selected = true;
                ivUserSelect.setVisibility(View.VISIBLE);
                int position = getLayoutPosition();
                parent.userClicked(position, selected);
            }
        }
    }
}
