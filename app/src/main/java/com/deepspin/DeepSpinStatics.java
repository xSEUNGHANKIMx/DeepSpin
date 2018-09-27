package com.deepspin;

public class DeepSpinStatics {

    final public static String SERVER_IP = "http://75.80.129.141:7912/";

    final public static int RESULT_CAPTURE = 1001;
    final public static int RESULT_CAPTURE_RESULT = 1002;

    final public static String EXTRA_RESULT_URI = "extra_result_uri";

    private DeepSpinStatics() {
    }

    private static volatile com.deepspin.DeepSpinStatics sInstance;

    public static com.deepspin.DeepSpinStatics GetInstance() {

        if  (sInstance == null) {
            synchronized (com.deepspin.DeepSpinApp.class) {
                if (sInstance == null) {
                    sInstance = new com.deepspin.DeepSpinStatics();
                }
            }
        }

        return sInstance;
    }
}