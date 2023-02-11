package com.manipur.locationtracker.Utils;

public class TimeStamp {
    private static final String BASE_URL = "https://play.googleapis.com/play/log/";
    private static final String TAG = "TimeStampTag";

    public static void getTimeStamp(TimeListener timeListener) {
        timeListener.getTime(System.currentTimeMillis());
/*
        // Retrofit Interceptor
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Converting to Gson response
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        jsonPlaceHolderApi.getTime().enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Log.d(TAG, "onResponse: ");

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Unsucc: " + response.code());
                    timeListener.getTime(System.currentTimeMillis());
                    return;
                }

                Long res = response.body();
                if(res==null){
                    res = System.currentTimeMillis();
                }

                timeListener.getTime(res);
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.d(TAG, "onFailure: Err: " + t.getMessage());
                timeListener.getTime(System.currentTimeMillis());
            }
        });

*/
    }

    public interface TimeListener {
        public void getTime(long time);
    }
}
