package com.rubenpla.develop.safetynetsample.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;

import java.util.Random;

/**
 * Created by alten on 22/3/17.
 */

public class DialogTile {

    public static AlertDialog getDialog(Context context) {

        Random random = new Random();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Fact")
                .setMessage(Constants.facts[random.nextInt(6)]);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    static class Constants {

        public static final String  facts[] = {
                "The domain name www.youtube.com was registered on February 14, 2005.",
                "The average 21 year old has spent 5,000 hours playing video games, has exchanged 250,000 e-mails, instant and text messages and has spent 10,000 hours on the mobile phone",
                "The first computer mouse was invented by Doug Engelbart in around 1964 and was made of wood.",
                "On an average work day, a typist’s fingers travel 12.6 miles.",
                "Bill Gates, the founder of Microsoft was a college drop out.",
                "While it took the radio 38 years, and the television a short 13 years, it took the World Wide Web only 4 years to reach 50 million users."
        };
    }

}


