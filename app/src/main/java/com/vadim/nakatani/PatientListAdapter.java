package com.vadim.nakatani;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.vadim.nakatani.entity.PatientEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vadim on 11.11.2014.
 */
public class PatientListAdapter extends BaseAdapter implements Filterable{

    ArrayList<PatientEntity> originList = new ArrayList<PatientEntity>();
    List<PatientEntity> filteredList = new ArrayList<PatientEntity>();
    Context context;

    public PatientListAdapter(Context context, ArrayList<PatientEntity> arr) {
        if (arr != null) {
            originList = arr;
            filteredList = arr;
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return filteredList.size();
    }

    @Override
    public Object getItem(int num) {
        // TODO Auto-generated method stub
        Log.e(this.getClass().getName(), "call get");
        return filteredList.get(num);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int i, View someView, ViewGroup arg2) {
        //Получение объекта inflater из контекста
        LayoutInflater inflater = LayoutInflater.from(context);
        //Если someView (View из ListView) вдруг оказался равен
        //null тогда мы загружаем его с помошью inflater
        if (someView == null) {
            someView = inflater.inflate(R.layout.card_file_list_item, arg2, false);
        }
        //Обявляем наши текствьюшки и связываем их с разметкой
        TextView code = (TextView) someView.findViewById(R.id.text_card_list_patient_code);
        TextView lastNameAndFirst = (TextView) someView.findViewById(R.id.text_card_list_patient_nameLandF);
        TextView middleName = (TextView) someView.findViewById(R.id.text_card_list_patient_nameM);

        //Устанавливаем в каждую текствьюшку соответствующий текст
        // сначала заголовок
        code.setText(filteredList.get(i).getCode());
        // потом подзаголовок
        lastNameAndFirst.setText(filteredList.get(i).getLastName() + " " + filteredList.get(i).getFirstName());
        middleName.setText(filteredList.get(i).getMiddleName());
        return someView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<PatientEntity>) results.values;
                Log.e(this.getClass().getName(), "call publish");
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.e(this.getClass().getName(), "call performFiltering");

                FilterResults results = new FilterResults();
                List<PatientEntity> FilteredArrList = new ArrayList<PatientEntity>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = originList.size();
                    results.values = originList;
                } else {
                    String constr = constraint.toString();
                    constr = constr.toLowerCase();

                    for (int i = 0; i < originList.size(); i++) {
                        PatientEntity data = originList.get(i);
                        if ((((data.getFirstName()).toLowerCase()).startsWith(constr)) || (((data.getLastName()).toLowerCase()).startsWith(constr)) || (((data.getMiddleName()).toLowerCase()).startsWith(constr))) {
                            FilteredArrList.add(data);
                        }
                    }

                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }

                return results;
            }
        };
        return filter;
    }
}
