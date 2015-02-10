package co.mobilemakers.githubrepos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by diana.perez on 10/02/2015.
 */
public class TagPopularityAdapter extends ArrayAdapter<TagPopularity> {

    List<TagPopularity> mTags;

    public class ViewHolder{
        public final TextView textViewTagName;
        public final TextView textViewPopularity;

        public ViewHolder(View view){
            textViewTagName = (TextView)view.findViewById(R.id.text_view_tag_name);
            textViewPopularity = (TextView)view.findViewById(R.id.text_view_popularity);
        }
    }

    public TagPopularityAdapter(Context context, List<TagPopularity> tags){
        super(context, R.layout.list_item_tag_popularity, tags);
        mTags = tags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = reuseOrGenerateRowView(convertView, parent);
        displayTagInRow(position, rowView);
        return rowView;
    }

    private void displayTagInRow(int position, View rowView) {
        ViewHolder viewHolder = (ViewHolder)rowView.getTag();
        viewHolder.textViewTagName.setText(mTags.get(position).getTagName());
        viewHolder.textViewPopularity.setText(mTags.get(position).getPopularity());
    }

    private View reuseOrGenerateRowView(View convertView, ViewGroup parent) {
        View rowView;
        if(convertView != null){
            rowView = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_tag_popularity, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }
        return rowView;
    }
}
