package net.geekstools.floatshort.PRO.Util.NavAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;

import java.util.ArrayList;

public class ShareListAdapter extends BaseAdapter {

    FunctionsClass functionsClass;
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public ShareListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.share_card_list, null);

            viewHolder = new ViewHolder();
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.purchaseItemName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (PublicVariable.themeLightDark == true) {
            viewHolder.txtTitle.setTextColor(context.getResources().getColor(R.color.dark));
        } else if (PublicVariable.themeLightDark == false) {
            viewHolder.txtTitle.setTextColor(context.getResources().getColor(R.color.light));
        }
        viewHolder.imgIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        viewHolder.txtTitle.setText(navDrawerItems.get(position).getCharTitle());
        Typeface face = Typeface.createFromAsset(context.getAssets(), "upcil.ttf");
        viewHolder.txtTitle.setTypeface(face);
        if (position != 5) {
            viewHolder.txtTitle.setTextSize(27);
        }
        viewHolder.txtTitle.setShadowLayer(25, 0, 0, PublicVariable.primaryColor);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navDrawerItems.get(position).getCharTitle().equals(context.getString(R.string.facebook))) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent f = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(context.getString(R.string.link_facebook)));
                            f.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(f);
                        }
                    }, 150);
                } else if (navDrawerItems.get(position).getCharTitle().equals(context.getString(R.string.twitter))) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent f = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(context.getString(R.string.link_twitter)));
                            f.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(f);
                        }
                    }, 150);
                } else if (navDrawerItems.get(position).getCharTitle().equals(context.getString(R.string.share))) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String shareText =
                                    context.getString(R.string.invitation_title) +
                                            "\n" + context.getString(R.string.appDesc) +
                                            "\n" + context.getString(R.string.play_store_link) + context.getPackageName();

                            Intent s = new Intent(Intent.ACTION_SEND);
                            s.putExtra(Intent.EXTRA_TEXT, shareText);
                            s.setType("text/plain");
                            s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(s);
                        }
                    }, 150);
                } else if (navDrawerItems.get(position).getCharTitle().equals(context.getString(R.string.email))) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String textMsg = "\n\n\n\n\n"
                                    + "[Essential Information]" + "\n"
                                    + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase();
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.supportEmail)});
                            email.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_tag));
                            email.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_tag) + " [" + functionsClass.appVersionName(context.getPackageName()) + "] ");
                            email.putExtra(Intent.EXTRA_TEXT, textMsg);
                            email.setType("text/*");
                            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(Intent.createChooser(email, context.getString(R.string.feedback_tag)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    }, 150);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent f = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(context.getString(R.string.play_store_link) + context.getPackageName()));
                            f.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(f);
                        }
                    }, 150);
                }
                functionsClass.Toast(context.getString(R.string.thanks), Gravity.BOTTOM);
            }
        });
        RippleDrawable drawItem = (RippleDrawable) context.getResources().getDrawable(R.drawable.ripple_effect);
        GradientDrawable gradientDrawable = (GradientDrawable) drawItem.findDrawableByLayerId(android.R.id.mask);
        gradientDrawable.setColor(PublicVariable.primaryColorOpposite);
        drawItem.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
        convertView.setBackground(drawItem);
        return convertView;
    }

    static class ViewHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }
}