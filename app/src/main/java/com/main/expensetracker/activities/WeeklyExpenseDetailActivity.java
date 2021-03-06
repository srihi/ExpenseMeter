/**
 * 
 */
package com.main.expensetracker.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.main.expensetracker.adapter.WeeklyExpenseDetailAdapter;
import com.main.expensetracker.constants.AppConstants;
import com.main.expensetracker.data.ExpenseData;
import com.main.expensetracker.processor.ExpenseDataProcessor;
import com.main.expensetracker.utility.SwipeDismissList;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

/**
 * @author MaRoy
 *
 */
public class WeeklyExpenseDetailActivity extends ListActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = this.getIntent().getExtras();
		String weekNbr = b.getString(AppConstants.WEEK_NBR);
		ArrayList<ExpenseData> smsDataList = b.getParcelableArrayList(AppConstants.WEEKLY_LIST);

		ListView lv = getListView();
		
		getActionBar().setTitle(weekNbr);

		final WeeklyExpenseDetailAdapter mAdapter = new WeeklyExpenseDetailAdapter(this, smsDataList);
		SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(mAdapter);
		animationAdapter.setAbsListView(lv);
		lv.setAdapter(animationAdapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		SwipeDismissList swipeList = new SwipeDismissList(lv,
				new SwipeDismissList.OnDismissCallback() {
			// Gets called whenever the user deletes an item.
			public SwipeDismissList.Undoable onDismiss(AbsListView listView, final int position) {
				// Get your item from the adapter (mAdapter being an adapter for MyItem objects)
				final ExpenseData deletedItem = mAdapter.getItem(position);
				// Delete item from adapter
				mAdapter.remove(deletedItem);
				// Return an Undoable implementing every method
				return new SwipeDismissList.Undoable() {

					// Method is called when user undoes this deletion
					public void undo() {
						// Reinsert item to list
						mAdapter.insert(deletedItem, position);
					}

					// Return an undo message for that item
					public String getTitle() {
						return "Item deleted";
					}

					// Called when user cannot undo the action anymore
					public void discard() {
						// Use this place to e.g. delete the item from database
						mAdapter.remove(deletedItem);
						mAdapter.notifyDataSetChanged();
						ExpenseDataProcessor.getInstance().updateWeeklyDataInFile(mAdapter.getSmsList(), getApplicationContext());
					}
				};
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, WeeklyExpenseActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		WeeklyExpenseDetailActivity.this.finish();
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	{  
		super.onActivityResult(requestCode, resultCode, data); 

		SwingBottomInAnimationAdapter animationAdapter = (SwingBottomInAnimationAdapter)getListView().getAdapter();
		WeeklyExpenseDetailAdapter baseAdapter = ((WeeklyExpenseDetailAdapter)animationAdapter.getDecoratedBaseAdapter());
		baseAdapter.onActivityResult(requestCode, data);

	}
}
