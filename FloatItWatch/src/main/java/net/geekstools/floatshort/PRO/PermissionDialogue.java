/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 12:13 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.widget.Toast;

public class PermissionDialogue extends Activity {

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);

        permissionsAlert();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 666) {
            if (Settings.canDrawOverlays(getApplicationContext())) {

                startActivity(new Intent(getApplicationContext(), Configurations.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());

                PermissionDialogue.this.finish();
            }
        }
    }

    public void permissionsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PermissionDialogue.this);
        alertDialog.setTitle(Html.fromHtml("<font color='" + getColor(R.color.default_color) + "'>" +
                getResources().getString(R.string.permTitle) + "</font>"));
        alertDialog.setMessage(
                Html.fromHtml("<font color='" + getColor(R.color.dark) + "'>" +
                        getResources().getString(R.string.permDesc) + "</font>"));
        alertDialog.setIcon(getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(getString(R.string.grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 666);

                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.overlayset).toUpperCase(), Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton(getString(R.string.later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionDialogue.this.finish();

                dialog.dismiss();
            }
        });

        alertDialog.setNeutralButton(getString(R.string.read), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionDialogue.this.finish();

                dialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        alertDialog.show();
    }
}
