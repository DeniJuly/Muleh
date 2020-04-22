package com.rpl.muleh;

public class ApiConnect {

    private static final String BASE_URL            = "https://muleh.iamdeni.com/";
    //private static final String BASE_URL          = "http://192.168.43.92:8080/apimuleh/";

    public static final String URL_MASUK            = BASE_URL+"api/masuk";
    public static final String URL_DAFTAR           = BASE_URL+"api/daftar";
    public static final String URL_IMG_PROFILE      = BASE_URL+"asset/foto-penumpang/";
//    ubah
    public static final String URL_UBAH_DATA_USER   = BASE_URL+"api/ubah_data_user";
    public static final String URL_UBAH_FOTO_PENUMPANG     = BASE_URL+"api/upload_foto_penumpang";
//    get
    public static final String URL_GET_TRANSPORTASI = BASE_URL+"api/get_transportasi";
    public static final String URL_GET_DATA_USER    = BASE_URL+"api/get_data_user";
    public static final String URL_GET_GALERI_TRANSPORTASI = BASE_URL+"api/get_galeri_transportasi";
}
