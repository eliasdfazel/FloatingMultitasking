package net.geekstools.floatshort.PRO.Util.IAP.skulist.row;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import net.geekstools.floatshort.PRO.R;

public final class RowViewHolder extends RecyclerView.ViewHolder {
    public TextView purchaseItemName, purchaseItemDescription, purchaseItemPrice, purchaseItemInfo;
    public MaterialButton purchaseItemButton;
    public ImageView purchaseItemIcon;

    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }

    public RowViewHolder(final View itemView, final OnButtonClickListener clickListener) {
        super(itemView);
        purchaseItemName = (TextView) itemView.findViewById(R.id.purchaseItemName);
        purchaseItemPrice = (TextView) itemView.findViewById(R.id.purchaseItemPrice);
        purchaseItemDescription = (TextView) itemView.findViewById(R.id.purchaseItemDescription);
        purchaseItemIcon = (ImageView) itemView.findViewById(R.id.purchaseItemIcon);
        purchaseItemButton = (MaterialButton) itemView.findViewById(R.id.purchaseItemButton);
        purchaseItemInfo = (TextView) itemView.findViewById(R.id.purchaseItemInfo);
        if (purchaseItemButton != null) {
            purchaseItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onButtonClicked(getAdapterPosition());
                }
            });
        }
    }
}
