package vn.com.z11.z11app.Utilities;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by kienlv58 on 12/6/16.
 */
public class CommonMethod {

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

// onClick of button perform this simplest code.
    public boolean validateEmail(String email, Context context) {
        if (email.matches(emailPattern)) {
            return true;
            //Toast.makeText(context, "valid email address", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show();
            return  false;
        }
    }
}
