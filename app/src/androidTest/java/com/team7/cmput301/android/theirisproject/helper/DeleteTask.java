package com.team7.cmput301.android.theirisproject.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.searchly.jestdroid.JestDroidClient;
import com.team7.cmput301.android.theirisproject.IrisProjectApplication;
import com.team7.cmput301.android.theirisproject.model.Patient;
import com.team7.cmput301.android.theirisproject.model.Record;
import com.team7.cmput301.android.theirisproject.task.Callback;

import java.io.IOException;
import java.util.HashMap;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * DeleteTask is used for testing purposes to delete an entry from the DB, given their identifier
 * Note that this is not involved in any use cases for the application!
 *
 * @author Jmmxp
 */
public class DeleteTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = DeleteTask.class.getSimpleName();
    private Callback<Boolean> callback;

    public DeleteTask(Callback<Boolean> callback) {
        this.callback = callback;
    }

    /**
     * Deletes the entry with the given identifier for a field
     */
    @Override
    protected Boolean doInBackground(String... strings) {

        String type = strings[0];  // e.g. "user"
        String field = strings[1];  // e.g. "email"
        String identifier = strings[2];  // e.g. "gg@gmail.com"

        if (identifier == null) {
            return false;
        }

        String query = String.format("{\"query\": {\"term\": {\"%s\": \"%s\"}}}", field, identifier);
        Search search = new Search.Builder(query)
                .addIndex(IrisProjectApplication.INDEX)
                .addType(type)
                .build();

        JestDroidClient client = IrisProjectApplication.getDB();

        try {
            SearchResult searchResult = client.execute(search);

            if (!searchResult.isSucceeded()) {
                Log.i(TAG, "search failed");
                return false;
            }

            JsonArray arrayHits = searchResult.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
            // Make sure there is a hit in the array before trying to delete
            if (arrayHits.size() == 0) {
                return false;
            }

            String id;
            switch(type) {
                case "user": id = searchResult.getSourceAsObject(Patient.class, true).getId(); break;
                case "record" : id = searchResult.getSourceAsObject(Record.class, true).getId(); break;
                default: return false;
            }
//            id = arrayHits.getAsJsonObject().get("_index").getAsString();

            Delete delete = new Delete.Builder(id)
                    .index(IrisProjectApplication.INDEX).type(type).build();

            JestResult deleteResult = client.execute(delete);

            if (!deleteResult.isSucceeded()) {
                Log.i(TAG, "delete failed");
                return false;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean deleteSuccess) {
        super.onPostExecute(deleteSuccess);
        callback.onComplete(deleteSuccess);
    }
}
