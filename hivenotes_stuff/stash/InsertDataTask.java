private class InsertDataTask extends AsyncTask<Void, Void, Void> {

  private final ProgressDialog dialog = new ProgressDialog(Main.this);

  // can use UI thread here
  protected void onPreExecute() {
     this.dialog.setMessage("Inserting data...");
     this.dialog.show();
  }
  // automatically done on worker thread (separate from UI thread)
  protected Void doInBackground(final String... args) {
     //do something in background, i.e. loading data
     return null;
  }

  // can use UI thread here
  protected void onPostExecute(final Void unused) {
     if (this.dialog.isShowing()) {
        this.dialog.dismiss();
     }
     // populate list here
  }
}