final View dialogView = View.inflate(activity, R.layout.date_time_picker, null);
final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

         DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
         TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

         Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());

         time = calendar.getTimeInMillis();
         alertDialog.dismiss();
    }});
alertDialog.setView(dialogView);
alertDialog.show();