package me.jhoughton.login;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by james on 10/23/2015.
 */
public class ChatAdapter extends BaseAdapter {

    private final List<ChatMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
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
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            // if(chatMessage.getIsme())
            //     convertView = vi.inflate(R.layout.view_right, null);
            // else
            convertView = vi.inflate(R.layout.view_left, null);
            // holder = createViewHolder(convertView);
            // convertView.setTag(holder);
        } /* else {
            holder = (ViewHolder) convertView.getTag();
        } */

        // View view;
        // view = vi.inflate(R.layout.view_left,parent);

        // boolean myMsg = chatMessage.getIsme() ;//Just a dummy check
        //to simulate whether it me or other sender
        // holder.txtMessage.setText(chatMessage.getMessage());
        // holder.txtInfo.setText(chatMessage.getDate());

        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
        textView.setText(chatMessage.getName() + ":  " + chatMessage.getMessage());
        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
        // notifyDataSetChanged();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /*
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
    */
}