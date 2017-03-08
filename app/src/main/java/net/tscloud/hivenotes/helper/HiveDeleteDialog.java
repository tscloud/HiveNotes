package net.tscloud.hivenotes.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by tscloud on 9/16/16.
 */
abstract public class HiveDeleteDialog {

    public static final String TAG = "HiveDeleteDialog";

    private Context ctx;
    private String msg;

    public HiveDeleteDialog(Context aCtx, String aMsg) {
        ctx = aCtx;
        msg = aMsg;
    }

    // implemented by class to do the actual delete
    protected abstract void doDelete();

    public void doDeleteDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Delete...");

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_menu_delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog,int which){

                // Write your code here to invoke YES event
                Toast.makeText(ctx, "You clicked on YES", Toast.LENGTH_SHORT).show();
                // Call abstract to do the actual delete
                doDelete();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
                public void onClick (DialogInterface dialog,int which){
                // Write your code here to invoke NO event
                Toast.makeText(ctx, "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
