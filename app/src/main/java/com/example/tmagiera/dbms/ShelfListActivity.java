package com.example.tmagiera.dbms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.tmagiera.dbms.tasks.GetShelfContentTask;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import de.greenrobot.event.EventBus;


/**
 * An activity representing a list of Shelf. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ShelfDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ShelfListFragment} and the item details
 * (if present) is a {@link ShelfDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ShelfListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ShelfListActivity extends FragmentActivity
        implements ShelfListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_list);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
        EventBus.getDefault().register(this);

        if (findViewById(R.id.shelf_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ShelfListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.shelf_list))
                    .setActivateOnItemClick(true);
        }


        GetShelfContentTask getShelfContentTask = new GetShelfContentTask(ApiHandler.getSessionId());
        getShelfContentTask.execute((Void) null);
    }


    public void onEvent(GetShelfContentTask.ShelfContentMessageEvent event) {
        Log.d(this.getClass().getSimpleName(), "get shelf content event recieved");
        if(event.results != null) {
            Toast.makeText(this, "Content list updated", Toast.LENGTH_SHORT).show();
        } else {
        }
    }

    /**
     * Callback method from {@link ShelfListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ShelfDetailFragment.ARG_ITEM_ID, id);
            ShelfDetailFragment fragment = new ShelfDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.shelf_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ShelfDetailActivity.class);
            detailIntent.putExtra(ShelfDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
