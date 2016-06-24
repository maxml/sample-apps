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

package org.kaaproject.kaa.demo.photoframe.adapter;

import org.kaaproject.kaa.demo.photoframe.AlbumInfo;
import org.kaaproject.kaa.demo.photoframe.PhotoFrameController;
import org.kaaproject.kaa.demo.photoframe.PlayInfo;
import org.kaaproject.kaa.demo.photoframe.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * The implementation of the {@link BaseAdapter} class. Used as an adapter class for the albums list view.
 * Provides list item views with the information about remote device albums.
 */
public class AlbumsAdapter extends BaseAdapter {

    private final Context mContext;
    private final PhotoFrameController mController;
    private final String mEndpointKey;
    
    public AlbumsAdapter(Context context, PhotoFrameController controller, String endpointKey) {
        mContext = context;
        mController = controller;
        mEndpointKey = endpointKey;
    }
    
    @Override
    public int getCount() {
        return mController.getRemoteDeviceAlbums(mEndpointKey).size();
    }

    @Override
    public Object getItem(int position) {
        return mController.getRemoteDeviceAlbums(mEndpointKey).get(position);
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
            view = inflater.inflate(R.layout.album_list_item, null);
        }
        TextView albumTitleView = (TextView) view.findViewById(R.id.albumTitle);
        AlbumInfo albumInfo = (AlbumInfo) getItem(position);
        albumTitleView.setText(albumInfo.getTitle());
        
        TextView imageCountView = (TextView) view.findViewById(R.id.imageCount);
        String imageCountText = mContext.getString(R.string.image_count_pattern, albumInfo.getImageCount());
        imageCountView.setText(imageCountText);
        
        TextView nowPlayingView = (TextView) view.findViewById(R.id.nowPlaying);
        PlayInfo playInfo = mController.getRemoteDeviceStatus(mEndpointKey);
        if (playInfo != null && playInfo.getCurrentAlbumInfo() != null &&
                playInfo.getCurrentAlbumInfo().getBucketId().equals(albumInfo.getBucketId())) {
            nowPlayingView.setVisibility(View.VISIBLE);
            view.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_text_material_light));
        } else {
            nowPlayingView.setVisibility(View.GONE);
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        
        return view;
    }

}
