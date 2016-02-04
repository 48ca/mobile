package me.jhoughton.multidrop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by james on 2/4/2016.
 */
public class DirectionsAdapter extends BaseAdapter {

    public ArrayList<String> destinations;
    private Activity context;

    public DirectionsAdapter(Activity context, ArrayList<String> destinations) {
        this.context = context;
        this.destinations = destinations;
    }

    @Override
    public int getCount() {
        if (destinations != null) {
            return destinations.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getItem(int position) {
        if (destinations != null) {
            return destinations.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // ViewHolder holder;
        final String destination = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final DirectionsAdapter adapter = this;
        if (convertView == null) {
            // if(destination.getIsme())
            //     convertView = vi.inflate(R.layout.view_right, null);
            // else
            convertView = vi.inflate(R.layout.directions_item, null);
            // holder = createViewHolder(convertView);
            // convertView.setTag(holder);
            ImageButton b = (ImageButton) convertView.findViewById(R.id.imageButton);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destinations.remove(destination);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
        textView.setText(destination);
        return convertView;
    }

    public void add(String message) {
        destinations.add(message);
        // notifyDataSetChanged();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
