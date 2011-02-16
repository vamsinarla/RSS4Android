package com.vn.rssandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Reader extends Activity {
	/**
	 *  ViewHolder pattern for efficient ListAdapter usage
	 * @author vamsi
	 *
	 */
	static class ViewHolder {
        TextView title;
        TextView description;
        
        int position;
    }
	
	private ArrayList<Article> articles;
	private ItemAdapter articlesAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        articles = new ArrayList<Article>();
        articlesAdapter = new ItemAdapter(this, R.layout.layout_item, articles);
        articlesAdapter.setNotifyOnChange(true);
        
        // the listview in this page
		ListView list = (ListView) findViewById(R.id.itemsList);
		list.setAdapter(articlesAdapter);
		
		Button fetchItems = (Button) findViewById(R.id.fetchItems);
		fetchItems.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// get the input URL
				EditText inputBox = (EditText) findViewById(R.id.inputBox);
				
				String link = inputBox.getEditableText().toString();
				new DownloadAsyncTask().execute(
		        		new DownloadAsyncTask.Payload(
		        				DownloadAsyncTask.FEED_DOWNLOADER_TASK,
		        				new Object[] { Reader.this,
		        								link,
		        								articles }));
			}
		});
		
    }
    
    /**
     *  Used by the AsyncTask to update the set of articles
     * @param articles
     */
    public void setArticles(ArrayList<Article> articles) {
    	this.articles = articles;
    	this.articlesAdapter.notifyDataSetChanged();
    }
    
    /**
     * Adapter to hold our items
     * @author vnarla
     *
     */
    private class ItemAdapter extends ArrayAdapter<Article> implements View.OnClickListener {

    	private int textViewLayoutId;
    	private ArrayList<Article> articles;
    	
		public ItemAdapter(Context context, int textViewResourceId, ArrayList<Article> objects) {
			super(context, textViewResourceId, objects);
			articles = objects;
			textViewLayoutId = textViewResourceId;
		}

		/**
	    * Item's View
	    */
	   @Override
	   public View getView(int position, View convertView, ViewGroup parent) {
		   ViewHolder holder;
	
		   // First time creating the holder. Inflate the views only once with this pattern.
		   if (convertView == null) {
			   LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   convertView = inflater.inflate(textViewLayoutId, null);
	
			   holder = new ViewHolder();
			   holder.title = (TextView) convertView.findViewById(R.id.ArticleTitle);
			   holder.description = (TextView) convertView.findViewById(R.id.ArticleDescription);
			   convertView.setTag(holder);
		   } else {
			   holder = (ViewHolder) convertView.getTag();
		   }
		   convertView.setOnClickListener(this);
		   
		   holder.title.setText(articles.get(position).getTitle());
		   holder.description.setText(articles.get(position).getDescription());
		   holder.position = position;
		   
		   return convertView;
	   }
	   
	   public int getCount() {
		   return articles.size();
	   }
	   
	   public Article getItem(int position) {
		   return articles.get(position);
	   }
	   
	   public long getItemId(int position) {
		   return position;
	   }
	   
		@Override
		public void onClick(View view) {
			Integer position = (Integer) view.getTag();
			
			if (position != null) {
				Uri uri = Uri.parse(articlesAdapter.getItem(position).getLink());
				Intent webViewIntent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(Intent.createChooser(webViewIntent, "Open this article in"));
			}
		}
			
    }
}