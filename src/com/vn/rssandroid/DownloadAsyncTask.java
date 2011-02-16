package com.vn.rssandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadAsyncTask extends AsyncTask<DownloadAsyncTask.Payload, Article, DownloadAsyncTask.Payload> {
	
	public static final int FEED_DOWNLOADER_TASK = 1001;
	
	/**
	 * Payload pattern for efficient and simple use of AsyncTasks
	 * @author vamsi
	 *
	 */
	static class Payload {
		public int taskType;
		public Object[] data;
		public Object result;
		
		public Payload(int taskType, Object[] data) {
			this.taskType = taskType;
			this.data = data;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Post execution stuff for the AsyncTasks
	 */
	protected void onPostExecute(DownloadAsyncTask.Payload payload) {
		ArrayList<Article> articles = (ArrayList<Article>) payload.data[2];
		Reader reader = (Reader) payload.data[0];
		
		if (payload.result != null) {
			
			try {
				switch (payload.taskType) {
					case FEED_DOWNLOADER_TASK:
						JSONObject response = new JSONObject((String) payload.result);
						JSONArray items = response.getJSONArray("items");
						Article article;
						
						for (int index = 0; index < items.length(); ++index) {
							JSONObject item = new JSONObject(items.getString(index)); 
							article = new Article();
							
							article.setTitle(item.getString("title"));
							article.setDescription(item.getString("description"));
							article.setLink(item.getString("link"));
							
							articles.add(article);
						}
						
						reader.setArticles(articles);
						break;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
	@Override
	/**
	 * Main AsynTask work horse. Feed downloading are done in this
	 */
	protected DownloadAsyncTask.Payload doInBackground(DownloadAsyncTask.Payload... params) {
		DownloadAsyncTask.Payload payload = params[0];
		
		try {
			switch (payload.taskType) {
				case FEED_DOWNLOADER_TASK:
					String link = (String) payload.data[1];
					
					String parserServiceLink = "http://evecal.appspot.com/feedParser";
					
					parserServiceLink += "?feedLink=" + URLEncoder.encode(link, "UTF-8") + "&";
					parserServiceLink += "response=" + URLEncoder.encode("json", "UTF-8");
					
					URL url = new URL(parserServiceLink);
					InputStream responseStream = url.openConnection().getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
					StringBuilder builder = new StringBuilder();
					String oneLine;
					
					while ((oneLine = reader.readLine()) != null) {
						builder.append(oneLine);
					}
					reader.close();
					
					payload.result = new String(builder.toString());
			}
			
		} catch (Exception exception) {	
			Log.e("PlaudibleAsyncTask::doInBackground", exception.getMessage());
			exception.printStackTrace();
			payload.result = null;
		}
		return payload;
	}
	
	
}
