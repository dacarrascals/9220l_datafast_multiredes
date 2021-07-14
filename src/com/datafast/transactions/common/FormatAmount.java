package com.datafast.transactions.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 * Created by http://stackoverflow.com/questions/12338445/how-to-automatically-add-thousand-separators-as-number-is-input-in-edittext on 01/11/2016.
 */

public class FormatAmount implements TextWatcher {

    EditText editText;
    private String current = "";

    public FormatAmount(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try
        {
            if(!s.toString().equals(current)){
                editText.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[$,.]", "");

                double parsed = Double.parseDouble(cleanString);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                current = formatted;
                editText.setText(formatted);
                editText.setSelection(formatted.length());

                editText.addTextChangedListener(this);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public static String trimCommaOfString(String string) {

        String ret = "";

        if(string.contains(","))
        {
            ret = string.replace(",","");
        }
        else {
            ret = string;
        }

        return ret;
    }

    /**
     * Includes thousands indicator
     * @return
     */
    public static String format(EditText amounteditText){
        return FormatAmount.trimCommaOfString(amounteditText.getText().toString());
    }

    /**
     * Remove character entered by parameter
     * @param cadena
     * @param busqueda
     * @param reemplazo
     * @return
     */
    public static String removeCharacter(String cadena, String busqueda, String reemplazo) {
        return cadena.replaceAll(busqueda, reemplazo);
    }
}
