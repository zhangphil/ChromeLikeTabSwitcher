/*
 * Copyright 2016 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.tabswitcher.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import de.mrapp.android.tabswitcher.Tab;
import de.mrapp.android.tabswitcher.TabSwitcher;
import de.mrapp.android.tabswitcher.model.TabItem;
import de.mrapp.android.tabswitcher.util.AbstractDataBinder;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A data binder, which allows to asynchronously render preview images of tabs and display them
 * afterwards.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class PreviewDataBinder extends AbstractDataBinder<Bitmap, Tab, ImageView, TabItem> {

    /**
     * The tab switcher, the tabs belong to.
     */
    private final TabSwitcher tabSwitcher;

    /**
     * The view recycler, which is used to inflate child views.
     */
    private final ChildViewRecycler childViewRecycler;

    /**
     * The view, which is currently rendered as a preview image.
     */
    private View child;

    /**
     * Creates a new data binder, which allows to asynchronously render preview images of tabs and
     * display them afterwards.
     *
     * @param tabSwitcher
     *         The tab switcher, the tabs belong to, as an instance of the class {@link
     *         TabSwitcher}. The tab switcher may not be null
     * @param childViewRecycler
     *         The view recycler, which should be used to inflate child views, as an instance of the
     *         class {@link ChildViewRecycler}. The view recycler may not be null
     */
    public PreviewDataBinder(@NonNull final TabSwitcher tabSwitcher,
                             @NonNull final ChildViewRecycler childViewRecycler) {
        super(tabSwitcher.getContext());
        ensureNotNull(tabSwitcher, "The tab switcher may not be null");
        ensureNotNull(childViewRecycler, "The child view recycler may not be null");
        this.tabSwitcher = tabSwitcher;
        this.childViewRecycler = childViewRecycler;
    }

    @Override
    protected void onPreExecute(@NonNull final ImageView view, @NonNull final TabItem... params) {
        TabItem tabItem = params[0];
        TabViewHolder viewHolder = tabItem.getViewHolder();
        child = viewHolder.child;
        Tab tab = tabItem.getTab();

        if (child == null) {
            child = childViewRecycler.inflate(tab, viewHolder.childContainer);
            // TODO: Must the view also be added to the parent? This is relevant when calling the showSwitcher-method, while the TabSwitcher is not yet inflated
        } else {
            viewHolder.child = null;
        }

        tabSwitcher.getDecorator().applyDecorator(getContext(), tabSwitcher, child, tab);
    }

    @Nullable
    @Override
    protected Bitmap doInBackground(@NonNull final Tab key, @NonNull final TabItem... params) {
        Bitmap bitmap =
                Bitmap.createBitmap(child.getWidth(), child.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        child.draw(canvas);

        // TODO: This is only for debugging purposes
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(100, 100, 40, paint);

        return bitmap;
    }

    @Override
    protected void onPostExecute(@NonNull final ImageView view, @Nullable final Bitmap data,
                                 @NonNull final TabItem... params) {
        view.setImageBitmap(data);
        view.setVisibility(data != null ? View.VISIBLE : View.GONE);
    }

}