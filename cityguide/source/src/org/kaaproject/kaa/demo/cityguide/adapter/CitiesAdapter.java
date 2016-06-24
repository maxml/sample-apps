/**
 *  Copyright 2014-2016 CyberVision, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kaaproject.kaa.demo.cityguide.adapter;

import java.util.List;

import org.kaaproject.kaa.demo.cityguide.City;
import org.kaaproject.kaa.demo.cityguide.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * The implementation of the {@link BaseAdapter} class. Used as an adapter class for the cities list view.
 * Provides list item views containing a name of each city.
 */
public class CitiesAdapter extends BaseAdapter {

    private Context mContext;
    private List<City> mCities;

    public CitiesAdapter(Context context, List<City> cities) {
        mContext = context;
        mCities = cities;
    }

    @Override
    public int getCount() {
        return mCities.size();
    }

    @Override
    public City getItem(int position) {
        return position < getCount() ? mCities.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.city_list_item, null);
        }
        TextView cityNameView = (TextView) view.findViewById(R.id.cityName);
        City city = mCities.get(position);
        cityNameView.setText(city.getName());
        return view;
    }

}
