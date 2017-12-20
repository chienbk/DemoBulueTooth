package com.example.congvc1.webviewexample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ChienNV9 on 12/20/2017.
 */

public class ListStatusAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater = null;
    private List<SensorInfomation> listSensorInfomation;
    private ViewHolder holder;

    public ListStatusAdapter(Activity a, List<SensorInfomation> list) {
        activity = a;
        listSensorInfomation = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshListThumb(List<SensorInfomation> listThumb) {
        this.listSensorInfomation.clear();
        this.listSensorInfomation.addAll(listThumb);
        notifyDataSetChanged();
    }

    public int getCount() {
        return listSensorInfomation.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_layout_status, null);
            holder = new ViewHolder();
            holder.imgAvatar = (ImageView) convertView
                    .findViewById(R.id.img_Avatar);
            holder.nameOfStatus = (TextView) convertView
                    .findViewById(R.id.txt_nameOfStatus);
            holder.value = (TextView)
                    convertView.findViewById(R.id.txt_values);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set
        try {
            final SensorInfomation thumbItem = listSensorInfomation.get(position);
            //holder.imgAvatar.setBackground((Drawable) getItem(R.drawable.vehicle_speed));
            holder.nameOfStatus.setText(thumbItem.getName());
            holder.value.setText(thumbItem.getValue());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imgAvatar;
        TextView nameOfStatus;
        TextView value;
    }
}
