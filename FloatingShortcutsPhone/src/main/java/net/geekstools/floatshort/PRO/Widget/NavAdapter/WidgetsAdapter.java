package net.geekstools.floatshort.PRO.Widget.NavAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class WidgetsAdapter extends RecyclerView.Adapter<WidgetsAdapter.ViewHolder> {

    Activity activity;
    Context context;

    FunctionsClass functionsClass;

    ArrayList<NavDrawerItem> navDrawerItems;


    View view;
    ViewHolder viewHolder;

    public WidgetsAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.activity = activity;

        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context, activity);
    }

    @Override
    public WidgetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.widget_items, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {

        viewHolder.widgetPreview.setImageDrawable(navDrawerItems.get(position).getWidgetPreview());

        viewHolder.widgetitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.widgetitem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return navDrawerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout widgetitem;
        ImageView widgetPreview;

        public ViewHolder(View view) {
            super(view);
            widgetitem = (RelativeLayout) view.findViewById(R.id.widgetItem);
            widgetPreview = (ImageView) view.findViewById(R.id.widgetPreview);
        }
    }
}
