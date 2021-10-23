/* Copyright 2020 paul623
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Modifications copyright (C) 2021 Andrii Hubert

package l4stphoen1x.butterflyrecognizer.Utils;
import com.yanzhenjie.album.*;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import l4stphoen1x.butterflyrecognizer.R;
public class MediaLoader implements AlbumLoader{
    public void load(ImageView imageView, AlbumFile albumFile){
        load(imageView, albumFile.getPath());}
    public void load(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .load(l4stphoen1x.butterflyrecognizer.Utils.ImageUtils.getBitmapByPath(imageView.getContext(), url))
                .error(R.drawable.error_loading)
                .placeholder(R.drawable.img_loading)
                .into(imageView);}

}