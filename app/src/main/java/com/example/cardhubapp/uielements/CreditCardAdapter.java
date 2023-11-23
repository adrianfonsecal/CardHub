package com.example.cardhubapp.uielements;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cardhubapp.model.CreditCardProduct;

import java.util.List;

public class CreditCardAdapter extends ArrayAdapter<CreditCardProduct> {

    public CreditCardAdapter(Context context, List<CreditCardProduct> creditCards) {
        super(context, android.R.layout.simple_spinner_item, creditCards);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        CreditCardProduct creditCard = getItem(position);

        if (creditCard != null) {
            textView.setText(creditCard.getName());
        }

        return convertView;
    }
}
