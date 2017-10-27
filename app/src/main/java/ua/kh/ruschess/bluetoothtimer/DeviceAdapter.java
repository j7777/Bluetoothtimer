package ua.kh.ruschess.bluetoothtimer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    Context context;
    OnItemClickListener clickListener;
    private ArrayList deviceList;

    public DeviceAdapter(Context context) {
        this.context = context;
    }

    public DeviceAdapter(ArrayList<HashMap<String, String>> CatList) {
        this.deviceList = CatList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder ViewHolder, int i) {
        Map<String, String> hashmap;
        hashmap = (Map<String, String>) deviceList.get(i);

        ViewHolder.nameDevice.setText(hashmap.get("name"));
        ViewHolder.iconText.setText(hashmap.get("name").substring(0, 1));

        Context ViewHolderContext = ViewHolder.itemView.getContext();

        if(hashmap.get("sel") == "sel") {
            ViewHolder.layout_bg.setBackgroundColor(Color.argb(100, 240, 240, 240));
            FlipAnimator.flipView(ViewHolderContext, ViewHolder.iconBack, ViewHolder.iconFront, true);
        }
        else if(hashmap.get("back_anim") == "anim") {
            FlipAnimator.flipView(ViewHolderContext, ViewHolder.iconBack, ViewHolder.iconFront, false);
        }

        ViewHolder.imgProfile.setImageResource(R.drawable.bg_circle);
        ViewHolder.imgProfile.setColorFilter(Integer.parseInt(hashmap.get("color")));
        ViewHolder.iconText.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return deviceList == null ? 0 : deviceList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView nameDevice;
        ImageView imgProfile;
        TextView iconText;
        RelativeLayout iconBack;
        RelativeLayout iconFront;
        RelativeLayout layout_bg;

        public ViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardView);
            nameDevice = (TextView) itemView.findViewById(R.id.name_device);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            iconText = (TextView) itemView.findViewById(R.id.icon_text);
            iconBack = (RelativeLayout) itemView.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) itemView.findViewById(R.id.icon_front);
            layout_bg = (RelativeLayout) itemView.findViewById(R.id.layout_bg);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
