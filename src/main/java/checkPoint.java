public enum checkPoint {

    KEELUNG("基隆市", 25.126021, 121.735417),
    TAIPEI("臺北市", 25.046125,121.540530),
    NEW_TAIPEI("新北市", 25.008722,121.466810),
    TAOYUAN("桃園市", 25.001678, 121.301753),
    HSINCHU("新竹縣", 24.843992, 121.009728),
    HSINCHU_CITY("新竹市", 24.804224, 120.962444),
    MIAOLI("苗栗縣", 24.568426, 120.828075),
    TAICHUNG("臺中市", 24.152688, 120.696325),
    NANTOU("南投縣", 23.959686, 120.976676),
    CHANGHUA("彰化縣", 24.052812, 120.532546),
    YUNLIN("雲林縣", 23.705850, 120.441778),
    CHIAYI("嘉義縣", 23.542886, 120.393206),
    CHIAYI_CITY("嘉義市", 23.476912, 120.446696),
    TAINAN("臺南市", 23.012630, 120.263723),
    KAOHSIUNG("高雄市", 22.671384, 120.312357),
    PINGTUNG("屏東縣", 22.536420, 120.526632),
    YILAN("宜蘭縣", 24.693586, 121.779584),
    HUALIEN("花蓮縣", 23.997972, 121.532599),
    TAITUNG("臺東縣", 22.860640, 121.105802),
    PENGHU("澎湖縣", 23.565971, 119.584913),
    KINMEN("金門縣", 24.429969, 118.323905),
    LIENCHIANG("連江縣", 26.153847, 119.947038);

    private String cityName;
    private double lat;
    private double lng;

    checkPoint(String cityName, double lat, double lng) {
        this.cityName = cityName;
        this.lat = lat;
        this.lng = lng;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
